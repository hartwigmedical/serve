package com.hartwig.serve.dao;

import static com.hartwig.serve.database.Tables.KNOWNCODON;
import static com.hartwig.serve.database.Tables.KNOWNCOPYNUMBER;
import static com.hartwig.serve.database.Tables.KNOWNEXON;
import static com.hartwig.serve.database.Tables.KNOWNFUSION;
import static com.hartwig.serve.database.Tables.KNOWNGENE;
import static com.hartwig.serve.database.Tables.KNOWNHOTSPOT;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Iterables;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.molecular.KnownEvents;
import com.hartwig.serve.datamodel.molecular.fusion.KnownFusion;
import com.hartwig.serve.datamodel.molecular.gene.KnownCopyNumber;
import com.hartwig.serve.datamodel.molecular.gene.KnownGene;
import com.hartwig.serve.datamodel.molecular.hotspot.KnownHotspot;
import com.hartwig.serve.datamodel.molecular.range.KnownCodon;
import com.hartwig.serve.datamodel.molecular.range.KnownExon;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.InsertValuesStep10;
import org.jooq.InsertValuesStep12;
import org.jooq.InsertValuesStep4;
import org.jooq.InsertValuesStep7;

class KnownEventsDAO {

    @NotNull
    private final DSLContext context;

    public KnownEventsDAO(@NotNull final DSLContext context) {
        this.context = context;
    }

    public void deleteAll() {
        context.deleteFrom(KNOWNHOTSPOT).execute();
        context.deleteFrom(KNOWNCODON).execute();
        context.deleteFrom(KNOWNEXON).execute();
        context.deleteFrom(KNOWNGENE).execute();
        context.deleteFrom(KNOWNCOPYNUMBER).execute();
        context.deleteFrom(KNOWNFUSION).execute();
    }

    public void write(@NotNull Timestamp timestamp, @NotNull KnownEvents knownEvents) {
        writeKnownHotspots(timestamp, knownEvents.hotspots());
        writeKnownCodons(timestamp, knownEvents.codons());
        writeKnownExons(timestamp, knownEvents.exons());
        writeKnownGenes(timestamp, knownEvents.genes());
        writeKnownCopyNumbers(timestamp, knownEvents.copyNumbers());
        writeKnownFusions(timestamp, knownEvents.fusions());
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
            InsertValuesStep4 inserter =
                    context.insertInto(KNOWNGENE, KNOWNGENE.MODIFIED, KNOWNGENE.GENE, KNOWNGENE.GENEROLE, KNOWNGENE.SOURCES);
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

}
