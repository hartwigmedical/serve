package com.hartwig.serve.datamodel.efficacy;

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

        int treatmentCompare = evidence1.treatment().compareTo(evidence2.treatment());
        if (treatmentCompare != 0) {
            return treatmentCompare;
        }

        int indicationCompare = evidence1.indication().compareTo(evidence2.indication());
        if (indicationCompare != 0) {
            return indicationCompare;
        }

        int molecularCriteriumCompare = evidence1.molecularCriterium().compareTo(evidence2.molecularCriterium());
        if (molecularCriteriumCompare != 0) {
            return molecularCriteriumCompare;
        }

        int efficacyDescriptionCompare = evidence1.efficacyDescription().compareTo(evidence2.efficacyDescription());
        if (efficacyDescriptionCompare != 0) {
            return efficacyDescriptionCompare;
        }

        int evidenceLevelCompare = evidence1.evidenceLevel().compareTo(evidence2.evidenceLevel());
        if (evidenceLevelCompare != 0) {
            return evidenceLevelCompare;
        }

        int evidenceLevelDetailsCompare = evidence1.evidenceLevelDetails().compareTo(evidence2.evidenceLevelDetails());
        if (evidenceLevelDetailsCompare != 0) {
            return evidenceLevelDetailsCompare;
        }

        int evidenceDirectionCompare = evidence1.evidenceDirection().compareTo(evidence2.evidenceDirection());
        if (evidenceDirectionCompare != 0) {
            return evidenceDirectionCompare;
        }

        int evidenceYearCompare = Integer.compare(evidence1.evidenceYear(), evidence2.evidenceYear());
        if (evidenceYearCompare != 0) {
            return evidenceYearCompare;
        }

        return CompareFunctions.compareSetOfComparable(evidence1.urls(), evidence2.urls());
    }
}