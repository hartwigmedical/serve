package com.hartwig.serve.datamodel;

import java.util.Comparator;
import java.util.Set;
import java.util.StringJoiner;

import com.google.common.collect.Sets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public enum Knowledgebase {
    CKB(RefGenome.V38, "CKB", EvidenceLevel.B, EvidenceLevel.B),
    DOCM(RefGenome.V37, "DoCM", EvidenceLevel.A, EvidenceLevel.A),
    HARTWIG_HOTSPOT_COHORT(RefGenome.V37, "HMF Hotspot Cohort", EvidenceLevel.A, EvidenceLevel.A),
    HARTWIG_HOTSPOT_CURATED(RefGenome.V37, "HMF Hotspot Curated", EvidenceLevel.A, EvidenceLevel.A),
    HARTWIG_GENE_DRIVER_PANEL(RefGenome.V37, "HMF Gene Driver Panel", EvidenceLevel.A, EvidenceLevel.A),
    HARTWIG_GENE_CURATED(RefGenome.V37, "HMF Gene Curated", EvidenceLevel.A, EvidenceLevel.A),
    ICLUSION(RefGenome.V37, "iClusion", EvidenceLevel.B, EvidenceLevel.B),
    VICC_CGI(RefGenome.V37, "CGI", EvidenceLevel.B, EvidenceLevel.B),
    VICC_CIVIC(RefGenome.V37, "CIViC", EvidenceLevel.B, EvidenceLevel.B),
    VICC_JAX(RefGenome.V37, "CKB Core", EvidenceLevel.B, EvidenceLevel.B),
    VICC_ONCOKB(RefGenome.V37, "OncoKB", EvidenceLevel.B, EvidenceLevel.B),
    UNKNOWN(RefGenome.V37, "Unknown", EvidenceLevel.D, EvidenceLevel.D);

    private static final Logger LOGGER = LogManager.getLogger(Knowledgebase.class);

    @NotNull
    private final RefGenome refGenome;
    @NotNull
    private final String display;
    @NotNull
    private final EvidenceLevel maxCertainEvidenceReportingLevel;
    @NotNull
    private final EvidenceLevel maxPredictedEvidenceReportingLevel;

    Knowledgebase(@NotNull final RefGenome refGenome, @NotNull final String display,
            @NotNull final EvidenceLevel maxCertainEvidenceReportingLevel,
            @NotNull final EvidenceLevel maxPredictedEvidenceReportingLevel) {
        this.refGenome = refGenome;
        this.display = display;
        this.maxCertainEvidenceReportingLevel = maxCertainEvidenceReportingLevel;
        this.maxPredictedEvidenceReportingLevel = maxPredictedEvidenceReportingLevel;
    }

    @NotNull
    public RefGenome refGenomeVersion() {
        return refGenome;
    }

    @SuppressWarnings("unused")
    @NotNull
    public String display() {
        return display;
    }

    @SuppressWarnings("unused")
    @NotNull
    public EvidenceLevel maxCertainEvidenceReportingLevel() {
        return maxCertainEvidenceReportingLevel;
    }

    @SuppressWarnings("unused")
    @NotNull
    public EvidenceLevel maxPredictedEvidenceReportingLevel() {
        return maxPredictedEvidenceReportingLevel;
    }

    @NotNull
    public static Set<Knowledgebase> fromCommaSeparatedSourceString(@NotNull String sources) {
        Set<Knowledgebase> consolidated = Sets.newHashSet();

        for (String source : sources.split(",")) {
            Knowledgebase knowledgebase = lookupKnowledgebase(source);
            if (knowledgebase != Knowledgebase.UNKNOWN) {
                consolidated.add(knowledgebase);
            } else {
                LOGGER.warn("Could not resolve knowledgebase from source '{}'", source);
            }
        }

        return consolidated;
    }

    @NotNull
    public static Knowledgebase lookupKnowledgebase(@NotNull String knowledgebaseToFind) {
        for (Knowledgebase knowledgebase : Knowledgebase.values()) {
            if (knowledgebase.toString().equals(knowledgebaseToFind)) {
                return knowledgebase;
            }
        }
        return Knowledgebase.UNKNOWN;
    }

    @NotNull
    public static String toCommaSeparatedSourceString(@NotNull Set<Knowledgebase> sources) {
        Set<Knowledgebase> sorted = Sets.newTreeSet(Comparator.naturalOrder());
        sorted.addAll(sources);

        StringJoiner joiner = new StringJoiner(",");
        for (Knowledgebase source : sorted) {
            joiner.add(source.toString());
        }
        return joiner.toString();
    }
}