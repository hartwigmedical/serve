package com.hartwig.serve.common.variant.impact;

import org.jetbrains.annotations.NotNull;

public final class VariantTranscriptImpactFactory {

    // In the VCF, transcript impacts are separated by ',', the components by ',' and the effects by '&"
    private static final String VAR_TRANS_IMPACT_ITEM_DELIM = "\\|";

    private VariantTranscriptImpactFactory() {
    }

    @NotNull
    public static VariantTranscriptImpact fromVcfData(@NotNull String data) {
        String[] items = data.split(VAR_TRANS_IMPACT_ITEM_DELIM);

        return ImmutableVariantTranscriptImpact.builder()
                .geneId(items[0])
                .geneName(items[1])
                .transcript(items[2])
                .effects(items[3])
                .spliceRegion(Boolean.parseBoolean(items[4]))
                .hgvsCoding(items[5])
                .hgvsProtein(items.length == 7 ? items[6] : null)
                .build();
    }
}
