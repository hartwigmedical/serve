package com.hartwig.serve.extraction;

import java.util.List;

import com.hartwig.serve.datamodel.molecular.characteristic.TumorCharacteristic;
import com.hartwig.serve.datamodel.molecular.fusion.FusionPair;
import com.hartwig.serve.datamodel.molecular.gene.GeneAnnotation;
import com.hartwig.serve.datamodel.molecular.hotspot.VariantAnnotation;
import com.hartwig.serve.extraction.codon.CodonAnnotation;
import com.hartwig.serve.extraction.exon.ExonAnnotation;
import com.hartwig.serve.extraction.immuno.ImmunoHLA;

import org.jetbrains.annotations.NotNull;

public final class EventExtractorOutputFunctions {

    private EventExtractorOutputFunctions() {
    }

    public static int eventCount(@NotNull EventExtractorOutput output) {
        List<VariantAnnotation> variants = output.variants();
        List<CodonAnnotation> codons = output.codons();
        List<ExonAnnotation> exons = output.exons();
        GeneAnnotation geneLevel = output.geneLevel();
        GeneAnnotation copyNumber = output.copyNumber();
        FusionPair fusionPair = output.fusionPair();
        TumorCharacteristic characteristic = output.characteristic();
        ImmunoHLA hla = output.hla();

        int variantCount = variants != null ? variants.size() : 0;
        int codonCount = codons != null ? codons.size() : 0;
        int exonCount = exons != null ? exons.size() : 0;
        int geneLevelCount = geneLevel != null ? 1 : 0;
        int copyNumberCount = copyNumber != null ? 1 : 0;
        int fusionPairCount = fusionPair != null ? 1 : 0;
        int characteristicCount = characteristic != null ? 1 : 0;
        int hlaCount = hla != null ? 1 : 0;

        return variantCount + codonCount + exonCount + geneLevelCount + copyNumberCount + fusionPairCount + characteristicCount + hlaCount;
    }
}
