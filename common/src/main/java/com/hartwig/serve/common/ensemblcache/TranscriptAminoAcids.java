package com.hartwig.serve.common.ensemblcache;

public class TranscriptAminoAcids {

    public final String GeneId;
    public final String GeneName;
    public final String TransName;
    public final boolean Canonical;
    public final String AminoAcids;

    public TranscriptAminoAcids(final String geneId, final String geneName, final String transName, boolean canonical,
            final String aminoAcids) {
        GeneId = geneId;
        GeneName = geneName;
        TransName = transName;
        Canonical = canonical;
        AminoAcids = aminoAcids;
    }
}
