package com.hartwig.serve.datamodel;

import java.util.Comparator;

import org.jetbrains.annotations.NotNull;

public class EfficacyEvidenceComparator implements Comparator<EfficacyEvidence> {

    @Override
    public int compare(@NotNull EfficacyEvidence event1, @NotNull EfficacyEvidence event2) {
        int sourceCompare = event1.source().toString().compareTo(event2.source().toString());
        if (sourceCompare != 0) {
            return sourceCompare;
        }

        int treatmentCompare = event1.treatment().name().compareTo(event2.treatment().name());
        if (treatmentCompare != 0) {
            return treatmentCompare;
        }

        int efficacyDescriptionCompare = event1.efficacyDescription().compareTo(event2.efficacyDescription());
        if (efficacyDescriptionCompare != 0) {
            return efficacyDescriptionCompare;
        }

        int levelCompare = event1.evidenceLevel().toString().compareTo(event2.evidenceLevel().toString());
        if (levelCompare != 0) {
            return levelCompare;
        }

        int evidenceLevelDetailsCompare = event1.evidenceLevelDetails().toString().compareTo(event2.evidenceLevelDetails().toString());
        if (evidenceLevelDetailsCompare != 0) {
            return evidenceLevelDetailsCompare;
        }

        int directionCompare = event1.evidenceDirection().toString().compareTo(event2.evidenceDirection().toString());
        if (directionCompare != 0) {
            return directionCompare;
        }

        int yearCompare = Integer.compare(event1.evidenceYear(), event2.evidenceYear());
        if (yearCompare != 0) {
            return yearCompare;
        }

        return compareCancerTypes(event1.indication().applicableCancerType(), event2.indication().applicableCancerType());
    }

    private static int compareCancerTypes(@NotNull CancerType cancerType1, @NotNull CancerType cancerType2) {
        int nameCompare = cancerType1.name().compareTo(cancerType2.name());
        if (nameCompare != 0) {
            return nameCompare;
        }

        return cancerType1.doid().compareTo(cancerType2.doid());
    }
}