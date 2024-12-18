package com.hartwig.serve.dao;

import static com.hartwig.serve.database.Tables.KNOWNCODON;
import static com.hartwig.serve.database.Tables.KNOWNCOPYNUMBER;
import static com.hartwig.serve.database.Tables.KNOWNEXON;
import static com.hartwig.serve.database.Tables.KNOWNFUSION;
import static com.hartwig.serve.database.Tables.KNOWNGENE;
import static com.hartwig.serve.database.Tables.KNOWNHOTSPOT;

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
import org.jooq.InsertValuesStep11;
import org.jooq.InsertValuesStep3;
import org.jooq.InsertValuesStep6;
import org.jooq.InsertValuesStep9;

@SuppressWarnings({ "ResultOfMethodCallIgnored", "unchecked" })
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

    public void write(@NotNull KnownEvents knownEvents) {
        writeKnownHotspots(knownEvents.hotspots());
        writeKnownCodons(knownEvents.codons());
        writeKnownExons(knownEvents.exons());
        writeKnownGenes(knownEvents.genes());
        writeKnownCopyNumbers(knownEvents.copyNumbers());
        writeKnownFusions(knownEvents.fusions());
    }

    private void writeKnownHotspots(@NotNull Set<KnownHotspot> hotspots) {
        for (List<KnownHotspot> batch : Iterables.partition(hotspots, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep11 inserter = context.insertInto(KNOWNHOTSPOT,
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
            batch.forEach(entry -> writeKnownHotspotBatch(inserter, entry));
            inserter.execute();
        }
    }

    private static void writeKnownHotspotBatch(@NotNull InsertValuesStep11 inserter, @NotNull KnownHotspot knownHotspot) {
        inserter.values(knownHotspot.gene(),
                knownHotspot.geneRole().name(),
                knownHotspot.proteinEffect().name(),
                DatabaseUtil.toByte(knownHotspot.associatedWithDrugResistance()),
                knownHotspot.chromosome(),
                knownHotspot.position(),
                knownHotspot.ref(),
                knownHotspot.alt(),
                knownHotspot.inputTranscript(),
                knownHotspot.inputProteinAnnotation(),
                Knowledgebase.toCommaSeparatedSourceString(knownHotspot.sources()));
    }

    private void writeKnownCodons(@NotNull Set<KnownCodon> codons) {
        for (List<KnownCodon> batch : Iterables.partition(codons, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep11 inserter = context.insertInto(KNOWNCODON,
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
            batch.forEach(entry -> writeKnownCodonBatch(inserter, entry));
            inserter.execute();
        }
    }

    private static void writeKnownCodonBatch(@NotNull InsertValuesStep11 inserter, @NotNull KnownCodon knownCodon) {
        inserter.values(knownCodon.gene(),
                knownCodon.geneRole().name(),
                knownCodon.proteinEffect().name(),
                DatabaseUtil.toByte(knownCodon.associatedWithDrugResistance()),
                knownCodon.chromosome(),
                knownCodon.start(),
                knownCodon.end(),
                knownCodon.applicableMutationType(),
                knownCodon.inputTranscript(),
                knownCodon.inputCodonRank(),
                Knowledgebase.toCommaSeparatedSourceString(knownCodon.sources()));
    }

    private void writeKnownExons(@NotNull Set<KnownExon> exons) {
        for (List<KnownExon> batch : Iterables.partition(exons, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep11 inserter = context.insertInto(KNOWNEXON,
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
            batch.forEach(entry -> writeKnownExonBatch(inserter, entry));
            inserter.execute();
        }
    }

    private static void writeKnownExonBatch(@NotNull InsertValuesStep11 inserter, @NotNull KnownExon knownExon) {
        inserter.values(knownExon.gene(),
                knownExon.geneRole().name(),
                knownExon.proteinEffect().name(),
                DatabaseUtil.toByte(knownExon.associatedWithDrugResistance()),
                knownExon.chromosome(),
                knownExon.start(),
                knownExon.end(),
                knownExon.applicableMutationType(),
                knownExon.inputTranscript(),
                knownExon.inputExonRank(),
                Knowledgebase.toCommaSeparatedSourceString(knownExon.sources()));
    }

    private void writeKnownGenes(@NotNull Set<KnownGene> genes) {
        for (List<KnownGene> batch : Iterables.partition(genes, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep3 inserter = context.insertInto(KNOWNGENE, KNOWNGENE.GENE, KNOWNGENE.GENEROLE, KNOWNGENE.SOURCES);
            batch.forEach(entry -> writeKnownGeneBatch(inserter, entry));
            inserter.execute();
        }
    }

    private static void writeKnownGeneBatch(@NotNull InsertValuesStep3 inserter, @NotNull KnownGene knownGene) {
        inserter.values(knownGene.gene(), knownGene.geneRole().name(), Knowledgebase.toCommaSeparatedSourceString(knownGene.sources()));
    }

    private void writeKnownCopyNumbers(@NotNull Set<KnownCopyNumber> copyNumbers) {
        for (List<KnownCopyNumber> batch : Iterables.partition(copyNumbers, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep6 inserter = context.insertInto(KNOWNCOPYNUMBER,
                    KNOWNCOPYNUMBER.GENE,
                    KNOWNCOPYNUMBER.GENEROLE,
                    KNOWNCOPYNUMBER.PROTEINEFFECT,
                    KNOWNCOPYNUMBER.ASSOCIATEDWITHDRUGRESISTANCE,
                    KNOWNCOPYNUMBER.EVENT,
                    KNOWNCOPYNUMBER.SOURCES);
            batch.forEach(entry -> writeKnownCopyNumberBatch(inserter, entry));
            inserter.execute();
        }
    }

    private static void writeKnownCopyNumberBatch(@NotNull InsertValuesStep6 inserter, @NotNull KnownCopyNumber knownCopyNumber) {
        inserter.values(knownCopyNumber.gene(),
                knownCopyNumber.geneRole().name(),
                knownCopyNumber.proteinEffect().name(),
                DatabaseUtil.toByte(knownCopyNumber.associatedWithDrugResistance()),
                knownCopyNumber.event().name(),
                Knowledgebase.toCommaSeparatedSourceString(knownCopyNumber.sources()));
    }

    private void writeKnownFusions(@NotNull Set<KnownFusion> fusions) {
        for (List<KnownFusion> batch : Iterables.partition(fusions, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep9 inserter = context.insertInto(KNOWNFUSION,
                    KNOWNFUSION.GENEUP,
                    KNOWNFUSION.MINEXONUP,
                    KNOWNFUSION.MAXEXONUP,
                    KNOWNFUSION.GENEDOWN,
                    KNOWNFUSION.MINEXONDOWN,
                    KNOWNFUSION.MAXEXONDOWN,
                    KNOWNFUSION.PROTEINEFFECT,
                    KNOWNFUSION.ASSOCIATEDWITHDRUGRESISTANCE,
                    KNOWNFUSION.SOURCES);
            batch.forEach(entry -> writeKnownFusionBatch(inserter, entry));
            inserter.execute();
        }
    }

    private static void writeKnownFusionBatch(@NotNull InsertValuesStep9 inserter, @NotNull KnownFusion fusion) {
        inserter.values(fusion.geneUp(),
                fusion.minExonUp(),
                fusion.maxExonUp(),
                fusion.geneDown(),
                fusion.minExonDown(),
                fusion.maxExonDown(),
                fusion.proteinEffect().name(),
                DatabaseUtil.toByte(fusion.associatedWithDrugResistance()),
                Knowledgebase.toCommaSeparatedSourceString(fusion.sources()));
    }
}
