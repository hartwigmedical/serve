package com.hartwig.serve.dao;

import static com.hartwig.serve.database.Tables.ACTIONABLECHARACTERISTIC;
import static com.hartwig.serve.database.Tables.ACTIONABLECODON;
import static com.hartwig.serve.database.Tables.ACTIONABLEEXON;
import static com.hartwig.serve.database.Tables.ACTIONABLEFUSION;
import static com.hartwig.serve.database.Tables.ACTIONABLEGENE;
import static com.hartwig.serve.database.Tables.ACTIONABLEHLA;
import static com.hartwig.serve.database.Tables.ACTIONABLEHOTSPOT;
import static com.hartwig.serve.database.Tables.EVENTINTERPRETATION;
import static com.hartwig.serve.database.Tables.KNOWNCODON;
import static com.hartwig.serve.database.Tables.KNOWNCOPYNUMBER;
import static com.hartwig.serve.database.Tables.KNOWNEXON;
import static com.hartwig.serve.database.Tables.KNOWNFUSION;
import static com.hartwig.serve.database.Tables.KNOWNGENE;
import static com.hartwig.serve.database.Tables.KNOWNHOTSPOT;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.*;
import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.fusion.KnownFusion;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.gene.KnownCopyNumber;
import com.hartwig.serve.datamodel.gene.KnownGene;
import com.hartwig.serve.datamodel.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.hotspot.KnownHotspot;
import com.hartwig.serve.datamodel.immuno.ActionableHLA;
import com.hartwig.serve.datamodel.range.ActionableRange;
import com.hartwig.serve.datamodel.range.KnownCodon;
import com.hartwig.serve.datamodel.range.KnownExon;
import com.hartwig.serve.extraction.events.EventInterpretation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.*;

@SuppressWarnings({ "unchecked", "ResultOfMethodCallIgnored" })
public class ServeDAO {

    private static final Logger LOGGER = LogManager.getLogger(ServeDAO.class);

    private static final String MAIN_JOINER = ",";
    private static final String NAME_DOID_JOINER = ";";

    @NotNull
    private final DSLContext context;

    ServeDAO(@NotNull final DSLContext context) {
        this.context = context;
    }

    public void deleteAll() {
        LOGGER.info("Deleting all data from SERVE database");
        context.deleteFrom(ACTIONABLEHOTSPOT).execute();
        context.deleteFrom(ACTIONABLECODON).execute();
        context.deleteFrom(ACTIONABLEEXON).execute();
        context.deleteFrom(ACTIONABLEGENE).execute();
        context.deleteFrom(ACTIONABLEFUSION).execute();
        context.deleteFrom(ACTIONABLECHARACTERISTIC).execute();
        context.deleteFrom(ACTIONABLEHLA).execute();
        context.deleteFrom(KNOWNHOTSPOT).execute();
        context.deleteFrom(KNOWNCODON).execute();
        context.deleteFrom(KNOWNEXON).execute();
        context.deleteFrom(KNOWNGENE).execute();
        context.deleteFrom(KNOWNCOPYNUMBER).execute();
        context.deleteFrom(KNOWNFUSION).execute();
        context.deleteFrom(EVENTINTERPRETATION).execute();
    }

    void write(@NotNull ActionableEvents actionableEvents, @NotNull KnownEvents knownEvents,
            @NotNull List<EventInterpretation> eventInterpretations) {
        deleteAll();

        Timestamp timestamp = new Timestamp(new Date().getTime());

        writeActionableHotspots(timestamp, actionableEvents.hotspots());
        writeActionableCodons(timestamp, actionableEvents.codons());
        writeActionableExons(timestamp, actionableEvents.exons());
        writeActionableGenes(timestamp, actionableEvents.genes());
        writeActionableFusions(timestamp, actionableEvents.fusions());
        writeActionableCharacteristics(timestamp, actionableEvents.characteristics());
        writeActionableHLA(timestamp, actionableEvents.hla());

        writeKnownHotspots(timestamp, knownEvents.hotspots());
        writeKnownCodons(timestamp, knownEvents.codons());
        writeKnownExons(timestamp, knownEvents.exons());
        writeKnownGenes(timestamp, knownEvents.genes());
        writeKnownCopyNumbers(timestamp, knownEvents.copyNumbers());
        writeKnownFusions(timestamp, knownEvents.fusions());

        writeEventInterpretations(timestamp, eventInterpretations);
    }

    private void writeActionableHotspots(@NotNull Timestamp timestamp, @NotNull List<ActionableHotspot> hotspots) {
        for (List<ActionableHotspot> batch : Iterables.partition(hotspots, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep21 inserter = context.insertInto(ACTIONABLEHOTSPOT,
                    ACTIONABLEHOTSPOT.MODIFIED,
                    ACTIONABLEHOTSPOT.GENE,
                    ACTIONABLEHOTSPOT.CHROMOSOME,
                    ACTIONABLEHOTSPOT.POSITION,
                    ACTIONABLEHOTSPOT.REF,
                    ACTIONABLEHOTSPOT.ALT,
                    ACTIONABLEHOTSPOT.SOURCE,
                    ACTIONABLEHOTSPOT.SOURCEEVENT,
                    ACTIONABLEHOTSPOT.SOURCEURLS,
                    ACTIONABLEHOTSPOT.STUDYNCTID,
                    ACTIONABLEHOTSPOT.STUDYTITLE,
                    ACTIONABLEHOTSPOT.COUNTRIESOFSTUDY,
                    ACTIONABLEHOTSPOT.TREATMENT,
                    ACTIONABLEHOTSPOT.SOURCETREATMENTAPPROACH,
                    ACTIONABLEHOTSPOT.TREATMENTAPPROACH,
                    ACTIONABLEHOTSPOT.APPLICABLECANCERTYPE,
                    ACTIONABLEHOTSPOT.APPLICABLEDOID,
                    ACTIONABLEHOTSPOT.BLACKLISTCANCERTYPES,
                    ACTIONABLEHOTSPOT.LEVEL,
                    ACTIONABLEHOTSPOT.DIRECTION,
                    ACTIONABLEHOTSPOT.EVIDENCEURLS);
            batch.forEach(entry -> writeActionableHotspotBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    @Nullable
    private static Treatment extractOptionalTreatment(@NotNull ActionableEvent event) {
        Treatment treatment = null;
         if (event.intervention() instanceof Treatment) {
            treatment = (Treatment) event.intervention();
        }
        return treatment;
    }

    @Nullable
    private static ClinicalTrial extractOptionalClinicalTrial(@NotNull ActionableEvent event) {
        ClinicalTrial clinicalTrial = null;
        if (event.intervention() instanceof ClinicalTrial) {
            clinicalTrial = (ClinicalTrial) event.intervention();
        }

        return clinicalTrial;
    }

    @NotNull
    private static String therapyName(@Nullable ClinicalTrial clinicalTrial, @Nullable Treatment treatment) {
        boolean isClinicalTrial = clinicalTrial != null;
        boolean isTreatment = treatment != null;

        if (isClinicalTrial && isTreatment) {
            throw new IllegalStateException("An actionable event cannot be both a treatment and clinical trial");
        }

        if (isTreatment) {
            return treatment.name();
        } else {
            assert clinicalTrial != null;
            return setToField(clinicalTrial.therapyNames());
        }
    }

    @NotNull
    private static String setToField(@NotNull Set<String> strings) {
        StringJoiner joiner = new StringJoiner(",");
        for (String string : strings) {
            joiner.add(string);
        }
        return joiner.toString();
    }

    private static void writeActionableHotspotBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStep21 inserter,
            @NotNull ActionableHotspot actionableHotspot) {

        ClinicalTrial clinicalTrial = extractOptionalClinicalTrial(actionableHotspot);
        Treatment treatment = extractOptionalTreatment(actionableHotspot);

        inserter.values(timestamp,
                actionableHotspot.gene(),
                actionableHotspot.chromosome(),
                actionableHotspot.position(),
                actionableHotspot.ref(),
                actionableHotspot.alt(),
                actionableHotspot.source(),
                actionableHotspot.sourceEvent(),
                concat(actionableHotspot.sourceUrls()),
                clinicalTrial != null ? clinicalTrial.studyNctId() : null,
                clinicalTrial != null ?  clinicalTrial.studyTitle() : null,
                clinicalTrial != null ?  concat(clinicalTrial.countriesOfStudy()) : null,
                therapyName(clinicalTrial, treatment),
                treatment != null ? concat(treatment.sourceRelevantTreatmentApproaches()) : null,
                treatment != null ?concat(treatment.relevantTreatmentApproaches()) : null,
                actionableHotspot.applicableCancerType().name(),
                actionableHotspot.applicableCancerType().doid(),
                concat(toStrings(actionableHotspot.blacklistCancerTypes())),
                actionableHotspot.level(),
                actionableHotspot.direction(),
                concat(actionableHotspot.evidenceUrls()).isEmpty());
    }

    private void writeActionableCodons(@NotNull Timestamp timestamp, @NotNull List<ActionableRange> codons) {
        for (List<ActionableRange> batch : Iterables.partition(codons, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep21 inserter = context.insertInto(ACTIONABLECODON,
                    ACTIONABLECODON.MODIFIED,
                    ACTIONABLECODON.GENE,
                    ACTIONABLECODON.CHROMOSOME,
                    ACTIONABLECODON.START,
                    ACTIONABLECODON.END,
                    ACTIONABLECODON.APPLICABLEMUTATIONTYPE,
                    ACTIONABLECODON.SOURCE,
                    ACTIONABLECODON.SOURCEEVENT,
                    ACTIONABLECODON.SOURCEURLS,
                    ACTIONABLECODON.STUDYNCTID,
                    ACTIONABLECODON.STUDYTITLE,
                    ACTIONABLECODON.COUNTRIESOFSTUDY,
                    ACTIONABLECODON.TREATMENT,
                    ACTIONABLECODON.SOURCETREATMENTAPPROACH,
                    ACTIONABLECODON.TREATMENTAPPROACH,
                    ACTIONABLECODON.APPLICABLECANCERTYPE,
                    ACTIONABLECODON.APPLICABLEDOID,
                    ACTIONABLECODON.BLACKLISTCANCERTYPES,
                    ACTIONABLECODON.LEVEL,
                    ACTIONABLECODON.DIRECTION,
                    ACTIONABLECODON.EVIDENCEURLS);
            batch.forEach(entry -> writeActionableRangeBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private void writeActionableExons(@NotNull Timestamp timestamp, @NotNull List<ActionableRange> exons) {
        for (List<ActionableRange> batch : Iterables.partition(exons, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep21 inserter = context.insertInto(ACTIONABLEEXON,
                    ACTIONABLEEXON.MODIFIED,
                    ACTIONABLEEXON.GENE,
                    ACTIONABLEEXON.CHROMOSOME,
                    ACTIONABLEEXON.START,
                    ACTIONABLEEXON.END,
                    ACTIONABLEEXON.APPLICABLEMUTATIONTYPE,
                    ACTIONABLEEXON.SOURCE,
                    ACTIONABLEEXON.SOURCEEVENT,
                    ACTIONABLEEXON.SOURCEURLS,
                    ACTIONABLEEXON.STUDYNCTID,
                    ACTIONABLEEXON.STUDYTITLE,
                    ACTIONABLEEXON.COUNTRIESOFSTUDY,
                    ACTIONABLEEXON.TREATMENT,
                    ACTIONABLEEXON.SOURCETREATMENTAPPROACH,
                    ACTIONABLEEXON.TREATMENTAPPROACH,
                    ACTIONABLEEXON.APPLICABLECANCERTYPE,
                    ACTIONABLEEXON.APPLICABLEDOID,
                    ACTIONABLEEXON.BLACKLISTCANCERTYPES,
                    ACTIONABLEEXON.LEVEL,
                    ACTIONABLEEXON.DIRECTION,
                    ACTIONABLEEXON.EVIDENCEURLS);
            batch.forEach(entry -> writeActionableRangeBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private static void writeActionableRangeBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStep21 inserter,
                                                   @NotNull ActionableRange actionableRange) {

        ClinicalTrial clinicalTrial = extractOptionalClinicalTrial(actionableRange);
        Treatment treatment = extractOptionalTreatment(actionableRange);

        inserter.values(timestamp,
                actionableRange.gene(),
                actionableRange.chromosome(),
                actionableRange.start(),
                actionableRange.end(),
                actionableRange.applicableMutationType(),
                actionableRange.source(),
                actionableRange.sourceEvent(),
                concat(actionableRange.sourceUrls()),
                clinicalTrial != null ?  clinicalTrial.studyNctId() : null,
                clinicalTrial != null ?  clinicalTrial.studyTitle() : null,
                clinicalTrial != null ?  concat(clinicalTrial.countriesOfStudy()) : null,
                therapyName(clinicalTrial, treatment),
                treatment != null ? concat(treatment.sourceRelevantTreatmentApproaches()) : null,
                treatment != null ? concat(treatment.relevantTreatmentApproaches()) : null,
                actionableRange.applicableCancerType().name(),
                actionableRange.applicableCancerType().doid(),
                concat(toStrings(actionableRange.blacklistCancerTypes())),
                actionableRange.level(),
                actionableRange.direction(),
                concat(actionableRange.evidenceUrls()));
    }

    private void writeActionableGenes(@NotNull Timestamp timestamp, @NotNull List<ActionableGene> genes) {
        for (List<ActionableGene> batch : Iterables.partition(genes, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep18 inserter = context.insertInto(ACTIONABLEGENE,
                    ACTIONABLEGENE.MODIFIED,
                    ACTIONABLEGENE.GENE,
                    ACTIONABLEGENE.EVENT,
                    ACTIONABLEGENE.SOURCE,
                    ACTIONABLEGENE.SOURCEEVENT,
                    ACTIONABLEGENE.SOURCEURLS,
                    ACTIONABLEGENE.STUDYNCTID,
                    ACTIONABLEGENE.STUDYTITLE,
                    ACTIONABLEGENE.COUNTRIESOFSTUDY,
                    ACTIONABLEGENE.TREATMENT,
                    ACTIONABLEGENE.SOURCETREATMENTAPPROACH,
                    ACTIONABLEGENE.TREATMENTAPPROACH,
                    ACTIONABLEGENE.APPLICABLECANCERTYPE,
                    ACTIONABLEGENE.APPLICABLEDOID,
                    ACTIONABLEGENE.BLACKLISTCANCERTYPES,
                    ACTIONABLEGENE.LEVEL,
                    ACTIONABLEGENE.DIRECTION,
                    ACTIONABLEGENE.EVIDENCEURLS);
            batch.forEach(entry -> writeActionableGeneBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private static void writeActionableGeneBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStep18 inserter,
            @NotNull ActionableGene actionableGene) {

        ClinicalTrial clinicalTrial = extractOptionalClinicalTrial(actionableGene);
        Treatment treatment = extractOptionalTreatment(actionableGene);

        inserter.values(timestamp,
                actionableGene.gene(),
                actionableGene.event(),
                actionableGene.source(),
                actionableGene.sourceEvent(),
                concat(actionableGene.sourceUrls()),
                clinicalTrial != null ? clinicalTrial.studyNctId() : null,
                clinicalTrial != null ? clinicalTrial.studyTitle() : null,
                clinicalTrial != null ? concat(clinicalTrial.countriesOfStudy()) : null,
                therapyName(clinicalTrial, treatment),
                treatment != null ? concat(treatment.sourceRelevantTreatmentApproaches()) : null,
                treatment != null ? concat(treatment.relevantTreatmentApproaches()) : null,
                actionableGene.applicableCancerType().name(),
                actionableGene.applicableCancerType().doid(),
                concat(toStrings(actionableGene.blacklistCancerTypes())),
                actionableGene.level(),
                actionableGene.direction(),
                concat(actionableGene.evidenceUrls()));
    }

    private void writeActionableFusions(@NotNull Timestamp timestamp, @NotNull List<ActionableFusion> fusions) {
        for (List<ActionableFusion> batch : Iterables.partition(fusions, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep22 inserter = context.insertInto(ACTIONABLEFUSION,
                    ACTIONABLEFUSION.MODIFIED,
                    ACTIONABLEFUSION.GENEUP,
                    ACTIONABLEFUSION.MINEXONUP,
                    ACTIONABLEFUSION.MAXEXONUP,
                    ACTIONABLEFUSION.GENEDOWN,
                    ACTIONABLEFUSION.MINEXONDOWN,
                    ACTIONABLEFUSION.MAXEXONDOWN,
                    ACTIONABLEFUSION.SOURCE,
                    ACTIONABLEFUSION.SOURCEEVENT,
                    ACTIONABLEFUSION.SOURCEURLS,
                    ACTIONABLEFUSION.STUDYNCTID,
                    ACTIONABLEFUSION.STUDYTITLE,
                    ACTIONABLEFUSION.COUNTRIESOFSTUDY,
                    ACTIONABLEFUSION.TREATMENT,
                    ACTIONABLEFUSION.SOURCETREATMENTAPPROACH,
                    ACTIONABLEFUSION.TREATMENTAPPROACH,
                    ACTIONABLEFUSION.APPLICABLECANCERTYPE,
                    ACTIONABLEFUSION.APPLICABLEDOID,
                    ACTIONABLEFUSION.BLACKLISTCANCERTYPES,
                    ACTIONABLEFUSION.LEVEL,
                    ACTIONABLEFUSION.DIRECTION,
                    ACTIONABLEFUSION.EVIDENCEURLS);
            batch.forEach(entry -> writeActionableFusionBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private static void writeActionableFusionBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStep22 inserter,
            @NotNull ActionableFusion actionableFusion) {

        ClinicalTrial clinicalTrial = extractOptionalClinicalTrial(actionableFusion);
        Treatment treatment = extractOptionalTreatment(actionableFusion);

        inserter.values(timestamp,
                actionableFusion.geneUp(),
                actionableFusion.minExonUp(),
                actionableFusion.maxExonUp(),
                actionableFusion.geneDown(),
                actionableFusion.minExonDown(),
                actionableFusion.maxExonDown(),
                actionableFusion.source(),
                actionableFusion.sourceEvent(),
                concat(actionableFusion.sourceUrls()),
                clinicalTrial != null ? clinicalTrial.studyNctId() : null,
                clinicalTrial != null ? clinicalTrial.studyTitle() : null,
                clinicalTrial != null ? concat(clinicalTrial.countriesOfStudy()) : null,
                therapyName(clinicalTrial, treatment),
                treatment != null ? concat(treatment.sourceRelevantTreatmentApproaches()) : null,
                treatment != null ? concat(treatment.relevantTreatmentApproaches()) : null,
                actionableFusion.applicableCancerType().name(),
                actionableFusion.applicableCancerType().doid(),
                concat(toStrings(actionableFusion.blacklistCancerTypes())),
                actionableFusion.level(),
                actionableFusion.direction(),
                concat(actionableFusion.evidenceUrls()));
    }

    private void writeActionableCharacteristics(@NotNull Timestamp timestamp, @NotNull List<ActionableCharacteristic> characteristics) {
        for (List<ActionableCharacteristic> batch : Iterables.partition(characteristics, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep19 inserter = context.insertInto(ACTIONABLECHARACTERISTIC,
                    ACTIONABLECHARACTERISTIC.MODIFIED,
                    ACTIONABLECHARACTERISTIC.TYPE,
                    ACTIONABLECHARACTERISTIC.CUTOFFTYPE,
                    ACTIONABLECHARACTERISTIC.CUTOFF,
                    ACTIONABLECHARACTERISTIC.SOURCE,
                    ACTIONABLECHARACTERISTIC.SOURCEEVENT,
                    ACTIONABLECHARACTERISTIC.SOURCEURLS,
                    ACTIONABLECHARACTERISTIC.STUDYNCTID,
                    ACTIONABLECHARACTERISTIC.STUDYTITLE,
                    ACTIONABLECHARACTERISTIC.COUNTRIESOFSTUDY,
                    ACTIONABLECHARACTERISTIC.TREATMENT,
                    ACTIONABLECHARACTERISTIC.SOURCETREATMENTAPPROACH,
                    ACTIONABLECHARACTERISTIC.TREATMENTAPPROACH,
                    ACTIONABLECHARACTERISTIC.APPLICABLECANCERTYPE,
                    ACTIONABLECHARACTERISTIC.APPLICABLEDOID,
                    ACTIONABLECHARACTERISTIC.BLACKLISTCANCERTYPES,
                    ACTIONABLECHARACTERISTIC.LEVEL,
                    ACTIONABLECHARACTERISTIC.DIRECTION,
                    ACTIONABLECHARACTERISTIC.EVIDENCEURLS);
            batch.forEach(entry -> writeActionableCharacteristicBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private static void writeActionableCharacteristicBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStep19 inserter,
            @NotNull ActionableCharacteristic actionableCharacteristic) {

        ClinicalTrial clinicalTrial = extractOptionalClinicalTrial(actionableCharacteristic);
        Treatment treatment = extractOptionalTreatment(actionableCharacteristic);

        inserter.values(timestamp,
                actionableCharacteristic.type(),
                actionableCharacteristic.cutoffType(),
                actionableCharacteristic.cutoff(),
                actionableCharacteristic.source(),
                actionableCharacteristic.sourceEvent(),
                concat(actionableCharacteristic.sourceUrls()),
                clinicalTrial != null ? clinicalTrial.studyNctId() : null,
                clinicalTrial != null ? clinicalTrial.studyTitle() : null,
                clinicalTrial != null ? concat(clinicalTrial.countriesOfStudy()) : null,
                therapyName(clinicalTrial, treatment),
                treatment != null ? concat(treatment.sourceRelevantTreatmentApproaches()) : null,
                treatment != null ? concat(treatment.relevantTreatmentApproaches()) : null,
                actionableCharacteristic.applicableCancerType().name(),
                actionableCharacteristic.applicableCancerType().doid(),
                concat(toStrings(actionableCharacteristic.blacklistCancerTypes())),
                actionableCharacteristic.level(),
                actionableCharacteristic.direction(),
                concat(actionableCharacteristic.evidenceUrls()));
    }

    private void writeActionableHLA(@NotNull Timestamp timestamp, @NotNull List<ActionableHLA> hla) {
        for (List<ActionableHLA> batch : Iterables.partition(hla, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep17 inserter = context.insertInto(ACTIONABLEHLA,
                    ACTIONABLEHLA.MODIFIED,
                    ACTIONABLEHLA.HLAALLELE,
                    ACTIONABLEHLA.SOURCE,
                    ACTIONABLEHLA.SOURCEEVENT,
                    ACTIONABLEHLA.SOURCEURLS,
                    ACTIONABLEHLA.STUDYNCTID,
                    ACTIONABLEHLA.STUDYTITLE,
                    ACTIONABLEHLA.COUNTRIESOFSTUDY,
                    ACTIONABLEHLA.TREATMENT,
                    ACTIONABLEHLA.SOURCETREATMENTAPPROACH,
                    ACTIONABLEHLA.TREATMENTAPPROACH,
                    ACTIONABLEHLA.APPLICABLECANCERTYPE,
                    ACTIONABLEHLA.APPLICABLEDOID,
                    ACTIONABLEHLA.BLACKLISTCANCERTYPES,
                    ACTIONABLEHLA.LEVEL,
                    ACTIONABLEHLA.DIRECTION,
                    ACTIONABLEHLA.EVIDENCEURLS);
            batch.forEach(entry -> writeActionableHLABatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private static void writeActionableHLABatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStep17 inserter,
            @NotNull ActionableHLA actionableHLA) {
        ClinicalTrial clinicalTrial = extractOptionalClinicalTrial(actionableHLA);
        Treatment treatment = extractOptionalTreatment(actionableHLA);

        inserter.values(timestamp,
                actionableHLA.hlaAllele(),
                actionableHLA.source(),
                actionableHLA.sourceEvent(),
                concat(actionableHLA.sourceUrls()),
                clinicalTrial != null ? clinicalTrial.studyNctId() : null,
                clinicalTrial != null ? clinicalTrial.studyTitle() : null,
                clinicalTrial != null ? concat(clinicalTrial.countriesOfStudy()) : null,
                therapyName(clinicalTrial, treatment),
                treatment != null ? concat(treatment.sourceRelevantTreatmentApproaches()) : null,
                treatment != null ? concat(treatment.relevantTreatmentApproaches()) : null,
                actionableHLA.applicableCancerType().name(),
                actionableHLA.applicableCancerType().doid(),
                concat(toStrings(actionableHLA.blacklistCancerTypes())),
                actionableHLA.level(),
                actionableHLA.direction(),
                concat(actionableHLA.evidenceUrls()));
    }

    private void writeKnownHotspots(@NotNull Timestamp timestamp, @NotNull Set<KnownHotspot> hotspots) {
        for (List<KnownHotspot> batch : Iterables.partition(hotspots, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep12 inserter = context.insertInto(KNOWNHOTSPOT,
                    KNOWNHOTSPOT.MODIFIED,
                    KNOWNHOTSPOT.GENE,
                    KNOWNHOTSPOT.GENEROLE,
                    KNOWNHOTSPOT.PROTEINEFFECT,
                    KNOWNHOTSPOT.ASSOCIATEDWITHDRUGRESISTANCE,
                    KNOWNHOTSPOT.CHROMOSOME,
                    KNOWNHOTSPOT.POSITION,
                    KNOWNHOTSPOT.REF,
                    KNOWNHOTSPOT.ALT,
                    KNOWNHOTSPOT.INPUTTRANSCRIPT,
                    KNOWNHOTSPOT.INPUTPROTEINANNOTATION,
                    KNOWNHOTSPOT.SOURCES);
            batch.forEach(entry -> writeKnownHotspotBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private static void writeKnownHotspotBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStep12 inserter,
            @NotNull KnownHotspot knownHotspot) {
        inserter.values(timestamp,
                knownHotspot.gene(),
                knownHotspot.geneRole().toString(),
                knownHotspot.proteinEffect().toString(),
                DatabaseUtil.toByte(knownHotspot.associatedWithDrugResistance()),
                knownHotspot.chromosome(),
                knownHotspot.position(),
                knownHotspot.ref(),
                knownHotspot.alt(),
                knownHotspot.inputTranscript(),
                knownHotspot.inputProteinAnnotation(),
                Knowledgebase.toCommaSeparatedSourceString(knownHotspot.sources()));
    }

    private void writeKnownCodons(@NotNull Timestamp timestamp, @NotNull Set<KnownCodon> codons) {
        for (List<KnownCodon> batch : Iterables.partition(codons, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep12 inserter = context.insertInto(KNOWNCODON,
                    KNOWNCODON.MODIFIED,
                    KNOWNCODON.GENE,
                    KNOWNCODON.GENEROLE,
                    KNOWNCODON.PROTEINEFFECT,
                    KNOWNCODON.ASSOCIATEDWITHDRUGRESISTANCE,
                    KNOWNCODON.CHROMOSOME,
                    KNOWNCODON.START,
                    KNOWNCODON.END,
                    KNOWNCODON.APPLICABLEMUTATIONTYPE,
                    KNOWNCODON.INPUTTRANSCRIPT,
                    KNOWNCODON.INPUTCODONRANK,
                    KNOWNCODON.SOURCES);
            batch.forEach(entry -> writeKnownCodonBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private static void writeKnownCodonBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStep12 inserter,
            @NotNull KnownCodon knownCodon) {
        inserter.values(timestamp,
                knownCodon.gene(),
                knownCodon.geneRole().toString(),
                knownCodon.proteinEffect().toString(),
                DatabaseUtil.toByte(knownCodon.associatedWithDrugResistance()),
                knownCodon.chromosome(),
                knownCodon.start(),
                knownCodon.end(),
                knownCodon.applicableMutationType(),
                knownCodon.inputTranscript(),
                knownCodon.inputCodonRank(),
                Knowledgebase.toCommaSeparatedSourceString(knownCodon.sources()));
    }

    private void writeKnownExons(@NotNull Timestamp timestamp, @NotNull Set<KnownExon> exons) {
        for (List<KnownExon> batch : Iterables.partition(exons, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep12 inserter = context.insertInto(KNOWNEXON,
                    KNOWNEXON.MODIFIED,
                    KNOWNEXON.GENE,
                    KNOWNEXON.GENEROLE,
                    KNOWNEXON.PROTEINEFFECT,
                    KNOWNEXON.ASSOCIATEDWITHDRUGRESISTANCE,
                    KNOWNEXON.CHROMOSOME,
                    KNOWNEXON.START,
                    KNOWNEXON.END,
                    KNOWNEXON.APPLICABLEMUTATIONTYPE,
                    KNOWNEXON.INPUTTRANSCRIPT,
                    KNOWNEXON.INPUTEXONRANK,
                    KNOWNEXON.SOURCES);
            batch.forEach(entry -> writeKnownExonBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private static void writeKnownExonBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStep12 inserter,
            @NotNull KnownExon knownExon) {
        inserter.values(timestamp,
                knownExon.gene(),
                knownExon.geneRole().toString(),
                knownExon.proteinEffect().toString(),
                DatabaseUtil.toByte(knownExon.associatedWithDrugResistance()),
                knownExon.chromosome(),
                knownExon.start(),
                knownExon.end(),
                knownExon.applicableMutationType(),
                knownExon.inputTranscript(),
                knownExon.inputExonRank(),
                Knowledgebase.toCommaSeparatedSourceString(knownExon.sources()));
    }

    private void writeKnownGenes(@NotNull Timestamp timestamp, @NotNull Set<KnownGene> genes) {
        for (List<KnownGene> batch : Iterables.partition(genes, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep4 inserter = context.insertInto(KNOWNGENE,
                    KNOWNGENE.MODIFIED,
                    KNOWNGENE.GENE,
                    KNOWNGENE.GENEROLE,
                    KNOWNGENE.SOURCES);
            batch.forEach(entry -> writeKnownGeneBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private static void writeKnownGeneBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStep4 inserter,
            @NotNull KnownGene knownGene) {
        inserter.values(timestamp,
                knownGene.gene(),
                knownGene.geneRole().toString(),
                Knowledgebase.toCommaSeparatedSourceString(knownGene.sources()));
    }

    private void writeKnownCopyNumbers(@NotNull Timestamp timestamp, @NotNull Set<KnownCopyNumber> copyNumbers) {
        for (List<KnownCopyNumber> batch : Iterables.partition(copyNumbers, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep7 inserter = context.insertInto(KNOWNCOPYNUMBER,
                    KNOWNCOPYNUMBER.MODIFIED,
                    KNOWNCOPYNUMBER.GENE,
                    KNOWNCOPYNUMBER.GENEROLE,
                    KNOWNCOPYNUMBER.PROTEINEFFECT,
                    KNOWNCOPYNUMBER.ASSOCIATEDWITHDRUGRESISTANCE,
                    KNOWNCOPYNUMBER.EVENT,
                    KNOWNCOPYNUMBER.SOURCES);
            batch.forEach(entry -> writeKnownCopyNumberBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private static void writeKnownCopyNumberBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStep7 inserter,
            @NotNull KnownCopyNumber knownCopyNumber) {
        inserter.values(timestamp,
                knownCopyNumber.gene(),
                knownCopyNumber.geneRole().toString(),
                knownCopyNumber.proteinEffect().toString(),
                DatabaseUtil.toByte(knownCopyNumber.associatedWithDrugResistance()),
                knownCopyNumber.event().toString(),
                Knowledgebase.toCommaSeparatedSourceString(knownCopyNumber.sources()));
    }

    private void writeKnownFusions(@NotNull Timestamp timestamp, @NotNull Set<KnownFusion> fusions) {
        for (List<KnownFusion> batch : Iterables.partition(fusions, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep10 inserter = context.insertInto(KNOWNFUSION,
                    KNOWNFUSION.MODIFIED,
                    KNOWNFUSION.GENEUP,
                    KNOWNFUSION.MINEXONUP,
                    KNOWNFUSION.MAXEXONUP,
                    KNOWNFUSION.GENEDOWN,
                    KNOWNFUSION.MINEXONDOWN,
                    KNOWNFUSION.MAXEXONDOWN,
                    KNOWNFUSION.PROTEINEFFECT,
                    KNOWNFUSION.ASSOCIATEDWITHDRUGRESISTANCE,
                    KNOWNFUSION.SOURCES);
            batch.forEach(entry -> writeKnownFusionBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private static void writeKnownFusionBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStep10 inserter,
            @NotNull KnownFusion fusion) {
        inserter.values(timestamp,
                fusion.geneUp(),
                fusion.minExonUp(),
                fusion.maxExonUp(),
                fusion.geneDown(),
                fusion.minExonDown(),
                fusion.maxExonDown(),
                fusion.proteinEffect().toString(),
                DatabaseUtil.toByte(fusion.associatedWithDrugResistance()),
                Knowledgebase.toCommaSeparatedSourceString(fusion.sources()));
    }

    private void writeEventInterpretations(@NotNull Timestamp timestamp, @NotNull List<EventInterpretation> eventInterpretations) {
        for (List<EventInterpretation> batch : Iterables.partition(eventInterpretations, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep6 inserter = context.insertInto(EVENTINTERPRETATION,
                    EVENTINTERPRETATION.MODIFIED,
                    EVENTINTERPRETATION.SOURCE,
                    EVENTINTERPRETATION.SOURCEEVENT,
                    EVENTINTERPRETATION.INTERPRETEDGENE,
                    EVENTINTERPRETATION.INTERPRETEDEVENT,
                    EVENTINTERPRETATION.INTERPRETEDEVENTTYPE);
            batch.forEach(entry -> writeEventInterpretationBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private static void writeEventInterpretationBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStep6 inserter,
            @NotNull EventInterpretation eventInterpretation) {
        inserter.values(timestamp,
                eventInterpretation.source(),
                eventInterpretation.sourceEvent(),
                eventInterpretation.interpretedGene(),
                eventInterpretation.interpretedEvent(),
                eventInterpretation.interpretedEventType());
    }

    @NotNull
    private static Set<String> toStrings(@NotNull Set<CancerType> cancerTypes) {
        Set<String> strings = Sets.newHashSet();
        for (CancerType cancerType : cancerTypes) {
            strings.add(cancerType.name() + NAME_DOID_JOINER + cancerType.doid());
        }
        return strings;
    }

    @NotNull
    private static String concat(@NotNull Set<String> strings) {
        StringJoiner joiner = new StringJoiner(MAIN_JOINER);
        for (String string : strings) {
            joiner.add(string);
        }
        return joiner.toString();
    }
}