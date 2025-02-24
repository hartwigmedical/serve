package com.hartwig.serve.dao;

import static com.hartwig.serve.database.Tables.ACTIONABLECHARACTERISTIC;
import static com.hartwig.serve.database.Tables.ACTIONABLECODON;
import static com.hartwig.serve.database.Tables.ACTIONABLEEXON;
import static com.hartwig.serve.database.Tables.ACTIONABLEFUSION;
import static com.hartwig.serve.database.Tables.ACTIONABLEGENE;
import static com.hartwig.serve.database.Tables.ACTIONABLEHLA;
import static com.hartwig.serve.database.Tables.ACTIONABLEHOTSPOT;
import static com.hartwig.serve.database.Tables.MOLECULARCRITERIUM;

import java.util.Set;

import com.hartwig.serve.datamodel.molecular.MolecularCriterium;
import com.hartwig.serve.datamodel.molecular.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.molecular.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.molecular.gene.ActionableGene;
import com.hartwig.serve.datamodel.molecular.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.molecular.immuno.ActionableHLA;
import com.hartwig.serve.datamodel.molecular.range.ActionableRange;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

class MolecularCriteriumDAO {

    @NotNull
    private final DSLContext context;

    public MolecularCriteriumDAO(@NotNull final DSLContext context) {
        this.context = context;
    }

    public void deleteAll() {
        context.deleteFrom(MOLECULARCRITERIUM).execute();
        context.deleteFrom(ACTIONABLEHOTSPOT).execute();
        context.deleteFrom(ACTIONABLECODON).execute();
        context.deleteFrom(ACTIONABLEEXON).execute();
        context.deleteFrom(ACTIONABLEGENE).execute();
        context.deleteFrom(ACTIONABLEFUSION).execute();
        context.deleteFrom(ACTIONABLECHARACTERISTIC).execute();
        context.deleteFrom(ACTIONABLEHLA).execute();
    }

    public int write(@NotNull MolecularCriterium molecularCriterium) {
        int molecularCriteriumId =
                context.insertInto(MOLECULARCRITERIUM).values().returning(MOLECULARCRITERIUM.ID).fetchOne().getValue(MOLECULARCRITERIUM.ID);

        // TODO do we need a subset id to model the hotspot grouping?
        molecularCriterium.hotspots().forEach(hotspots -> writeActionableHotspots(molecularCriteriumId, hotspots.hotspots()));
        writeActionableCodons(molecularCriteriumId, molecularCriterium.codons());
        writeActionableExons(molecularCriteriumId, molecularCriterium.exons());
        writeActionableGenes(molecularCriteriumId, molecularCriterium.genes());
        writeActionableFusions(molecularCriteriumId, molecularCriterium.fusions());
        writeActionableCharacteristics(molecularCriteriumId, molecularCriterium.characteristics());
        writeActionableHla(molecularCriteriumId, molecularCriterium.hla());

        return molecularCriteriumId;
    }

    private void writeActionableHotspots(int molecularCriteriumId, @NotNull Set<ActionableHotspot> hotspots) {
        for (ActionableHotspot hotspot : hotspots) {
            context.insertInto(ACTIONABLEHOTSPOT,
                            ACTIONABLEHOTSPOT.MOLECULARCRITERIUMID,
                            ACTIONABLEHOTSPOT.SOURCEDATE,
                            ACTIONABLEHOTSPOT.SOURCEEVENT,
                            ACTIONABLEHOTSPOT.SOURCEURLS,
                            ACTIONABLEHOTSPOT.GENE,
                            ACTIONABLEHOTSPOT.CHROMOSOME,
                            ACTIONABLEHOTSPOT.POSITION,
                            ACTIONABLEHOTSPOT.REF,
                            ACTIONABLEHOTSPOT.ALT)
                    .values(molecularCriteriumId,
                            hotspot.sourceDate(),
                            hotspot.sourceEvent(),
                            DatabaseUtil.concat(hotspot.sourceUrls()),
                            hotspot.gene(),
                            hotspot.chromosome(),
                            hotspot.position(),
                            hotspot.ref(),
                            hotspot.alt())
                    .execute();
        }
    }

    private void writeActionableCodons(int molecularCriteriumId, @NotNull Set<ActionableRange> codons) {
        for (ActionableRange codon : codons) {
            context.insertInto(ACTIONABLECODON,
                            ACTIONABLECODON.MOLECULARCRITERIUMID,
                            ACTIONABLECODON.SOURCEDATE,
                            ACTIONABLECODON.SOURCEEVENT,
                            ACTIONABLECODON.SOURCEURLS,
                            ACTIONABLECODON.GENE,
                            ACTIONABLECODON.CHROMOSOME,
                            ACTIONABLECODON.START,
                            ACTIONABLECODON.END,
                            ACTIONABLECODON.APPLICABLEMUTATIONTYPE)
                    .values(molecularCriteriumId,
                            codon.sourceDate(),
                            codon.sourceEvent(),
                            DatabaseUtil.concat(codon.sourceUrls()),
                            codon.gene(),
                            codon.chromosome(),
                            codon.start(),
                            codon.end(),
                            codon.applicableMutationType().name())
                    .execute();
        }
    }

    private void writeActionableExons(int molecularCriteriumId, @NotNull Set<ActionableRange> exons) {
        for (ActionableRange exon : exons) {
            context.insertInto(ACTIONABLEEXON,
                            ACTIONABLEEXON.MOLECULARCRITERIUMID,
                            ACTIONABLEEXON.SOURCEDATE,
                            ACTIONABLEEXON.SOURCEEVENT,
                            ACTIONABLEEXON.SOURCEURLS,
                            ACTIONABLEEXON.GENE,
                            ACTIONABLEEXON.CHROMOSOME,
                            ACTIONABLEEXON.START,
                            ACTIONABLEEXON.END,
                            ACTIONABLEEXON.APPLICABLEMUTATIONTYPE)
                    .values(molecularCriteriumId,
                            exon.sourceDate(),
                            exon.sourceEvent(),
                            DatabaseUtil.concat(exon.sourceUrls()),
                            exon.gene(),
                            exon.chromosome(),
                            exon.start(),
                            exon.end(),
                            exon.applicableMutationType().name())
                    .execute();
        }
    }

    private void writeActionableGenes(int molecularCriteriumId, @NotNull Set<ActionableGene> genes) {
        for (ActionableGene gene : genes) {
            context.insertInto(ACTIONABLEGENE,
                            ACTIONABLEGENE.MOLECULARCRITERIUMID,
                            ACTIONABLEGENE.SOURCEDATE,
                            ACTIONABLEGENE.SOURCEEVENT,
                            ACTIONABLEGENE.SOURCEURLS,
                            ACTIONABLEGENE.GENE,
                            ACTIONABLEGENE.EVENT)
                    .values(molecularCriteriumId,
                            gene.sourceDate(),
                            gene.sourceEvent(),
                            DatabaseUtil.concat(gene.sourceUrls()),
                            gene.gene(),
                            gene.event().name())
                    .execute();
        }
    }

    private void writeActionableFusions(int molecularCriteriumId, @NotNull Set<ActionableFusion> fusions) {
        for (ActionableFusion fusion : fusions) {
            context.insertInto(ACTIONABLEFUSION,
                            ACTIONABLEFUSION.MOLECULARCRITERIUMID,
                            ACTIONABLEFUSION.SOURCEDATE,
                            ACTIONABLEFUSION.SOURCEEVENT,
                            ACTIONABLEFUSION.SOURCEURLS,
                            ACTIONABLEFUSION.GENEUP,
                            ACTIONABLEFUSION.MINEXONUP,
                            ACTIONABLEFUSION.MAXEXONUP,
                            ACTIONABLEFUSION.GENEDOWN,
                            ACTIONABLEFUSION.MINEXONDOWN,
                            ACTIONABLEFUSION.MAXEXONDOWN)
                    .values(molecularCriteriumId,
                            fusion.sourceDate(),
                            fusion.sourceEvent(),
                            DatabaseUtil.concat(fusion.sourceUrls()),
                            fusion.geneUp(),
                            fusion.minExonUp(),
                            fusion.maxExonUp(),
                            fusion.geneDown(),
                            fusion.minExonDown(),
                            fusion.maxExonDown())
                    .execute();
        }
    }

    private void writeActionableCharacteristics(int molecularCriteriumId, @NotNull Set<ActionableCharacteristic> characteristics) {
        for (ActionableCharacteristic characteristic : characteristics) {
            context.insertInto(ACTIONABLECHARACTERISTIC,
                            ACTIONABLECHARACTERISTIC.MOLECULARCRITERIUMID,
                            ACTIONABLECHARACTERISTIC.SOURCEDATE,
                            ACTIONABLECHARACTERISTIC.SOURCEEVENT,
                            ACTIONABLECHARACTERISTIC.SOURCEURLS,
                            ACTIONABLECHARACTERISTIC.TYPE,
                            ACTIONABLECHARACTERISTIC.CUTOFFTYPE,
                            ACTIONABLECHARACTERISTIC.CUTOFF)
                    .values(molecularCriteriumId,
                            characteristic.sourceDate(),
                            characteristic.sourceEvent(),
                            DatabaseUtil.concat(characteristic.sourceUrls()),
                            characteristic.type().name(),
                            characteristic.cutoffType() != null ? characteristic.cutoffType().name() : null,
                            characteristic.cutoff())
                    .execute();
        }
    }

    private void writeActionableHla(int molecularCriteriumId, @NotNull Set<ActionableHLA> hlas) {
        for (ActionableHLA hla : hlas) {
            context.insertInto(ACTIONABLEHLA,
                            ACTIONABLEHLA.MOLECULARCRITERIUMID,
                            ACTIONABLEHLA.SOURCEDATE,
                            ACTIONABLEHLA.SOURCEEVENT,
                            ACTIONABLEHLA.SOURCEURLS,
                            ACTIONABLEHLA.HLAALLELE)
                    .values(molecularCriteriumId,
                            hla.sourceDate(),
                            hla.sourceEvent(),
                            DatabaseUtil.concat(hla.sourceUrls()),
                            hla.hlaAllele())
                    .execute();
        }
    }
}
