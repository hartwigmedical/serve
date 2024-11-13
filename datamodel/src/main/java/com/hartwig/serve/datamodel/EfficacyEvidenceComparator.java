package com.hartwig.serve.datamodel;

import java.util.Comparator;

import com.hartwig.serve.datamodel.util.CompareFunctions;

import org.jetbrains.annotations.NotNull;

public class EfficacyEvidenceComparator implements Comparator<EfficacyEvidence> {

    @Override
    public int compare(@NotNull EfficacyEvidence evidence1, @NotNull EfficacyEvidence evidence2) {
        int sourceCompare = evidence1.source().compareTo(evidence2.source());
        if (sourceCompare != 0) {
            return sourceCompare;
        }

        int treatmentCompare = evidence1.treatment().name().compareTo(evidence2.treatment().name());
        if (treatmentCompare != 0) {
            return treatmentCompare;
        }

        int indicationCompare = compareIndications(evidence1.indication().applicableType(), evidence2.indication().applicableType());
        if (indicationCompare != 0) {
            return indicationCompare;
        }

        int efficacyDescriptionCompare = evidence1.efficacyDescription().compareTo(evidence2.efficacyDescription());
        if (efficacyDescriptionCompare != 0) {
            return efficacyDescriptionCompare;
        }

        int evidenceLevelCompare = evidence1.evidenceLevel().toString().compareTo(evidence2.evidenceLevel().toString());
        if (evidenceLevelCompare != 0) {
            return evidenceLevelCompare;
        }

        int evidenceLevelDetailsCompare = evidence1.evidenceLevelDetails().toString().compareTo(evidence2.evidenceLevelDetails().toString());
        if (evidenceLevelDetailsCompare != 0) {
            return evidenceLevelDetailsCompare;
        }

        int evidenceDirectionCompare = evidence1.evidenceDirection().toString().compareTo(evidence2.evidenceDirection().toString());
        if (evidenceDirectionCompare != 0) {
            return evidenceDirectionCompare;
        }

        int evidenceYearCompare = Integer.compare(evidence1.evidenceYear(), evidence2.evidenceYear());
        if (evidenceYearCompare != 0) {
            return evidenceYearCompare;
        }

        return CompareFunctions.compareSetOfComparable(evidence1.urls(), evidence2.urls());
    }

    private static int compareIndications(@NotNull CancerType cancerType1, @NotNull CancerType cancerType2) {
        int nameCompare = cancerType1.name().compareTo(cancerType2.name());
        if (nameCompare != 0) {
            return nameCompare;
        }

        return cancerType1.doid().compareTo(cancerType2.doid());
    }
}