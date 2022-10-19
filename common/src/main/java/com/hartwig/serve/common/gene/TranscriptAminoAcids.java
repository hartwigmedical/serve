package com.hartwig.serve.common.gene;

import java.util.StringJoiner;

import com.hartwig.serve.common.ensemblcache.EnsemblDataLoader;

public class TranscriptAminoAcids
{
    public final String GeneId;
    public final String GeneName;
    public final String TransName;
    public final boolean Canonical;
    public final String AminoAcids;

    public TranscriptAminoAcids(
            final String geneId, final String geneName, final String transName, boolean canonical, final String aminoAcids)
    {
        GeneId = geneId;
        GeneName = geneName;
        TransName = transName;
        Canonical = canonical;
        AminoAcids = aminoAcids;
    }

    public static String csvHeader() { return "GeneId,GeneName,TransName,Canonical,AminoAcids"; }

    public String toCsv()
    {
        StringJoiner sj = new StringJoiner(EnsemblDataLoader.ENSEMBL_DELIM);
        sj.add(GeneId);
        sj.add(GeneName);
        sj.add(TransName);
        sj.add(String.valueOf(Canonical));
        sj.add(AminoAcids);
        return sj.toString();
    }

    public int baseLength() { return AminoAcids.length() + 1; }

}
