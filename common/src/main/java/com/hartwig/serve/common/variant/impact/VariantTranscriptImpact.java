package com.hartwig.serve.common.variant.impact;

public class VariantTranscriptImpact {

    public final String GeneId;
    public final String GeneName;
    public final String Transcript;
    public final String Effects;
    public final boolean SpliceRegion;
    public final String HgvsCoding;
    public final String HgvsProtein;

    public VariantTranscriptImpact(final String geneId, final String geneName, final String transcript, final String effects,
            final boolean spliceRegion, final String hgvsCoding, final String hgvsProtein) {
        GeneId = geneId;
        GeneName = geneName;
        Transcript = transcript;
        Effects = effects;
        SpliceRegion = spliceRegion;
        HgvsCoding = hgvsCoding;
        HgvsProtein = hgvsProtein;
    }

    // the in the VCF, transcript impacts are separated by ',', the components by ',' and the effects by '&"
    public static final String VAR_TRANS_IMPACT_ITEM_DELIM = "\\|";

    public static VariantTranscriptImpact fromVcfData(final String data) {
        String[] items = data.split(VAR_TRANS_IMPACT_ITEM_DELIM);
        return new VariantTranscriptImpact(items[0],
                items[1],
                items[2],
                items[3],
                Boolean.parseBoolean(items[4]),
                items[5],
                items.length == 7 ? items[6] : null);
    }
}
