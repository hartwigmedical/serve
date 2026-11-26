package com.hartwig.serve.common.classification;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a fusion event in which an explicit exon is given for both fusion partners, eg. "RUFY2 exon9::RET exon12".
 * Whitespace around the gene names, the "exon" keyword and the "::" separator is optional.
 */
public final class ExonicFusionPair {

    private static final Pattern EXONIC_FUSION_PAIR_PATTERN =
            Pattern.compile("^([A-Za-z0-9.\\-]+?)\\s*exon\\s*(\\d+)\\s*::\\s*([A-Za-z0-9.\\-]+?)\\s*exon\\s*(\\d+)$",
                    Pattern.CASE_INSENSITIVE);

    @NotNull
    private final String geneUp;
    private final int exonUp;
    @NotNull
    private final String geneDown;
    private final int exonDown;

    private ExonicFusionPair(@NotNull final String geneUp, final int exonUp, @NotNull final String geneDown, final int exonDown) {
        this.geneUp = geneUp;
        this.exonUp = exonUp;
        this.geneDown = geneDown;
        this.exonDown = exonDown;
    }

    public static boolean isExonicFusionPair(@NotNull String event) {
        return EXONIC_FUSION_PAIR_PATTERN.matcher(event.trim()).matches();
    }

    @Nullable
    public static ExonicFusionPair fromEvent(@NotNull String event) {
        Matcher matcher = EXONIC_FUSION_PAIR_PATTERN.matcher(event.trim());
        if (!matcher.matches()) {
            return null;
        }

        return new ExonicFusionPair(matcher.group(1),
                Integer.parseInt(matcher.group(2)),
                matcher.group(3),
                Integer.parseInt(matcher.group(4)));
    }

    @NotNull
    public String geneUp() {
        return geneUp;
    }

    public int exonUp() {
        return exonUp;
    }

    @NotNull
    public String geneDown() {
        return geneDown;
    }

    public int exonDown() {
        return exonDown;
    }

    @Override
    public String toString() {
        return geneUp + " exon" + exonUp + "::" + geneDown + " exon" + exonDown;
    }
}
