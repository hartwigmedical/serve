package com.hartwig.serve.datamodel;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public class ActionableEventComparator implements Comparator<ActionableEvent> {

    @Override
    public int compare(@NotNull ActionableEvent event1, @NotNull ActionableEvent event2) {
        int sourceCompare = event1.source().toString().compareTo(event2.source().toString());
        if (sourceCompare != 0) {
            return sourceCompare;
        }

        int levelCompare = event1.level().toString().compareTo(event2.level().toString());
        if (levelCompare != 0) {
            return levelCompare;
        }

        int directionCompare = event1.direction().toString().compareTo(event2.direction().toString());
        if (directionCompare != 0) {
            return directionCompare;
        }

        ClinicalTrial clinicalTrialEvent1 = null;
        ClinicalTrial clinicalTrialEvent2 = null;
        Treatment treatmentEvent1 = null;
        Treatment treatmentEvent2 = null;

        if (event1.intervention() instanceof ClinicalTrial) {
            clinicalTrialEvent1 = (ClinicalTrial) event1.intervention();
        }

        if (event2.intervention() instanceof ClinicalTrial) {
            clinicalTrialEvent2 = (ClinicalTrial) event2.intervention();
        }
        if (event1.intervention() instanceof Treatment) {
            treatmentEvent1 = (Treatment) event1.intervention();
        }
        if (event2.intervention() instanceof Treatment) {
            treatmentEvent2 = (Treatment) event2.intervention();
        }

        if (clinicalTrialEvent1 != null && clinicalTrialEvent2 != null) {
            int trialCompare = clinicalTrialEvent1.studyNctId().compareTo(clinicalTrialEvent2.studyNctId());
            if (trialCompare != 0) {
                return trialCompare;
            }
        }

        if (treatmentEvent1 != null && treatmentEvent2 != null) {
            int treatmentCompare = treatmentEvent1.name().compareTo(treatmentEvent2.name());
            if (treatmentCompare != 0) {
                return treatmentCompare;
            }
        }

        return compareCancerTypes(event1.applicableCancerType(), event2.applicableCancerType());
    }

    private static int compareCancerTypes(@NotNull CancerType cancerType1, @NotNull CancerType cancerType2) {
        int nameCompare = cancerType1.name().compareTo(cancerType2.name());
        if (nameCompare != 0) {
            return nameCompare;
        }

        return cancerType1.doid().compareTo(cancerType2.doid());
    }
}