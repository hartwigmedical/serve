package com.hartwig.serve.extraction;

import com.hartwig.serve.common.classification.EventType;
import com.hartwig.serve.extraction.characteristic.TumorCharacteristicExtractor;
import com.hartwig.serve.extraction.codon.CodonExtractor;
import com.hartwig.serve.extraction.copynumber.CopyNumberExtractor;
import com.hartwig.serve.extraction.exon.ExonExtractor;
import com.hartwig.serve.extraction.fusion.FusionExtractor;
import com.hartwig.serve.extraction.gene.GeneLevelExtractor;
import com.hartwig.serve.extraction.hotspot.VariantExtractor;
import com.hartwig.serve.extraction.immuno.ImmunoHLAExtractor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EventExtractor {

    @NotNull
    private final VariantExtractor variantExtractor;
    @NotNull
    private final CodonExtractor codonExtractor;
    @NotNull
    private final ExonExtractor exonExtractor;
    @NotNull
    private final GeneLevelExtractor geneLevelExtractor;
    @NotNull
    private final CopyNumberExtractor copyNumberExtractor;
    @NotNull
    private final FusionExtractor fusionExtractor;
    @NotNull
    private final TumorCharacteristicExtractor tumorCharacteristicExtractor;
    @NotNull
    private final ImmunoHLAExtractor immunoHLAExtractor;

    public EventExtractor(@NotNull final VariantExtractor variantExtractor, @NotNull final CodonExtractor codonExtractor,
            @NotNull final ExonExtractor exonExtractor, @NotNull final GeneLevelExtractor geneLevelExtractor,
            @NotNull final CopyNumberExtractor copyNumberExtractor, @NotNull final FusionExtractor fusionExtractor,
            @NotNull final TumorCharacteristicExtractor tumorCharacteristicExtractor, @NotNull ImmunoHLAExtractor immunoHLAExtractor) {
        this.variantExtractor = variantExtractor;
        this.codonExtractor = codonExtractor;
        this.exonExtractor = exonExtractor;
        this.geneLevelExtractor = geneLevelExtractor;
        this.copyNumberExtractor = copyNumberExtractor;
        this.fusionExtractor = fusionExtractor;
        this.tumorCharacteristicExtractor = tumorCharacteristicExtractor;
        this.immunoHLAExtractor = immunoHLAExtractor;
    }

    @NotNull
    public EventExtractorOutput extract(@NotNull String gene, @Nullable String transcriptId, @NotNull EventType type,
            @NotNull String event) {
        return ImmutableEventExtractorOutput.builder()
                .variants(variantExtractor.extract(gene, transcriptId, type, event))
                .codons(codonExtractor.extract(gene, transcriptId, type, event))
                .exons(exonExtractor.extract(gene, transcriptId, type, event))
                .geneLevel(geneLevelExtractor.extract(gene, type, event))
                .copyNumber(copyNumberExtractor.extract(gene, type))
                .fusionPair(fusionExtractor.extract(gene, type, event))
                .characteristic(tumorCharacteristicExtractor.extract(type, event))
                .hla(immunoHLAExtractor.extract(type, event))
                .build();
    }
}
