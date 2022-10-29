package com.hartwig.serve.common.variant;

import java.util.List;
import java.util.StringJoiner;

import com.google.common.collect.Lists;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public enum VariantConsequence {

    // See also http://sequenceontology.org
    // starting with the ones used by HMF
    SPLICE_ACCEPTOR_VARIANT("splice_acceptor_variant"),
    SPLICE_DONOR_VARIANT("splice_donor_variant"),
    SPLICE_REGION_VARIANT("splice_region_variant", "exonic_splice_region_variant", "non_coding_transcript_splice_region_variant"),
    STOP_GAINED("stop_gained"),
    STOP_LOST("stop_lost"),
    START_LOST("start_lost"),
    FRAMESHIFT_VARIANT("frameshift_variant",
            "frame_restoring_variant",
            "frameshift_elongation",
            "frameshift_truncation",
            "minus_1_frameshift_variant",
            "minus_2_frameshift_variant",
            "plus_1_frameshift_variant",
            "plus_2_frameshift_variant"),

    INFRAME_INSERTION("inframe_insertion", "conservative_inframe_insertion", "disruptive_inframe_insertion"),
    INFRAME_DELETION("inframe_deletion", "conservative_inframe_deletion", "disruptive_inframe_deletion"),
    MISSENSE_VARIANT("missense_variant",
            "conservative_missense_variant",
            "non_conservative_missense_variant",
            "rare_amino_acid_variant",
            "pyrrolysine_loss",
            "selenocysteine_loss"),
    SYNONYMOUS_VARIANT("synonymous_variant", "stop_retained_variant"),
    INTRON_VARIANT("intron_variant"),
    UTR_VARIANT("UTR_variant",
            "3_prime_UTR_variant",
            "5_prime_UTR_variant",
            "5_prime_UTR_premature_start_codon_gain_variant",
            "5_prime_UTR_truncation",
            "3_prime_UTR_truncation"),
    UPSTREAM_GENE_VARIANT("upstream_gene_variant"),
    NON_CODING_TRANSCRIPT_VARIANT("non_coding_transcript_variant", "non_coding_transcript_exon_variant"),

    INTRAGENIC_VARIANT("intragenic_variant"),
    TRANSCRIPT("transcript"),
    SEQUENCE_FEATURE("sequence_feature"),
    REGULATORY_REGION_VARIANT("regulatory_region_variant", "TF_binding_site_variant"),
    INITIATOR_CODON_VARIANT("initiator_codon_variant"),
    EXON_LOSS_VARIANT("exon_loss_variant", "exon_loss"),
    NON_CANONICAL_START_CODON("non_canonical_start_codon"),
    TRANSCRIPT_ABLATION("transcript_ablation"),
    TFBS_ABLATION("TFBS_ablation"),
    TRANSCRIPT_AMPLIFICATION("transcript_amplification"),
    STRUCTURAL_INTERACTION_VARIANT("structural_interaction_variant"),
    FUSION("bidirectional_gene_fusion", "gene_fusion"),
    PROTEIN_PROTEIN_CONTACT("protein_protein_contact"),
    OTHER(Strings.EMPTY);

    public static final String VARIANT_CONSEQ_DELIM = "&";

    @NotNull
    private final String parentSequenceOntologyTerm;
    @NotNull
    private final List<String> sequenceOntologySubTerms;

    VariantConsequence(@NotNull final String parentSequenceOntologyTerm, @NotNull final String... sequenceOntologySubTerms) {
        this.parentSequenceOntologyTerm = parentSequenceOntologyTerm;
        this.sequenceOntologySubTerms = Lists.newArrayList(sequenceOntologySubTerms);
    }

    @NotNull
    public static List<VariantConsequence> convertFromEffects(@NotNull List<String> effects) {
        List<VariantConsequence> consequences = Lists.newArrayList();
        for (String effect : effects) {
            consequences.add(fromEffect(effect));
        }
        return consequences;
    }

    @NotNull
    public static VariantConsequence fromEffect(@NotNull String effect) {
        for (VariantConsequence consequence : VariantConsequence.values()) {
            if (consequence.isParentTypeOf(effect)) {
                return consequence;
            }
        }

        return VariantConsequence.OTHER;
    }

    @NotNull
    public static String consequenceString(@NotNull List<VariantConsequence> consequences) {
        StringJoiner consequenceString = new StringJoiner(VARIANT_CONSEQ_DELIM);
        for (VariantConsequence consequence : consequences) {
            if (!consequence.parentTerm().isEmpty()) {
                consequenceString.add(consequence.parentTerm());
            }
        }
        return consequenceString.toString();
    }

    public boolean isParentTypeOf(@NotNull String annotation) {
        return annotation.equals(parentSequenceOntologyTerm) || sequenceOntologySubTerms.contains(annotation);
    }

    @NotNull
    public String parentTerm() {
        return parentSequenceOntologyTerm;
    }

    @NotNull
    public String description() {
        if (!sequenceOntologySubTerms.isEmpty()) {
            return sequenceOntologySubTerms.get(0);
        }

        return parentSequenceOntologyTerm;
    }

    public int rank() {
        switch (this) {
            case STOP_LOST:
            case STOP_GAINED:
            case START_LOST:
                return 60;

            case SPLICE_DONOR_VARIANT:
            case SPLICE_ACCEPTOR_VARIANT:
                return 50;

            case SPLICE_REGION_VARIANT:
                return 49;

            case FRAMESHIFT_VARIANT:
            case MISSENSE_VARIANT:
                return 40;

            case INFRAME_INSERTION:
            case INFRAME_DELETION:
                return 30;

            case SYNONYMOUS_VARIANT:
                return 25;

            case UTR_VARIANT:
            case INTRON_VARIANT:
                return 20;

            case NON_CODING_TRANSCRIPT_VARIANT:
                return 15;

            case UPSTREAM_GENE_VARIANT:
                return 10;

            default:
                return 0;
        }
    }
}
