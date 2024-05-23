package com.hartwig.serve.ckb.datamodel.treatmentapproaches;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;
import com.hartwig.serve.ckb.datamodel.drug.DrugFactory;
import com.hartwig.serve.ckb.datamodel.reference.ReferenceFactory;
import com.hartwig.serve.ckb.datamodel.therapy.TherapyFactory;
import com.hartwig.serve.ckb.json.CkbJsonDatabase;
import com.hartwig.serve.ckb.json.common.TreatmentApproachInfo;
import com.hartwig.serve.ckb.json.treatmentapproach.JsonTreatmentApproach;

import org.jetbrains.annotations.NotNull;

public final class RelevantTreatmentApproachesFactory {

    private RelevantTreatmentApproachesFactory() {
    }

    @NotNull
    public static List<RelevantTreatmentApproaches> extractRelevantTreatmentApproaches(@NotNull CkbJsonDatabase ckbJsonDatabase,
            @NotNull List<TreatmentApproachInfo> treatmentApproachInfos) {
        List<RelevantTreatmentApproaches> relevantTreatmentApproach = Lists.newArrayList();

        for (TreatmentApproachInfo treatmentApproachInfo : treatmentApproachInfos) {
            RelevantTreatmentApproaches resolvedRelevantTreatmentApproaches =
                    resolveRelevantTreatmentApproaches(ckbJsonDatabase, treatmentApproachInfo);
            relevantTreatmentApproach.add(resolvedRelevantTreatmentApproaches);
        }
        return relevantTreatmentApproach;
    }

    @NotNull
    private static TreatmentApproachIntervation extractTreatmentApproachIntervation(@NotNull CkbJsonDatabase ckbJsonDatabase,
            @NotNull JsonTreatmentApproach treatmentApproach) {
        boolean isDrugClass = treatmentApproach.drugClass() != null;
        boolean isTherapy = treatmentApproach.therapy() != null;

        if (isDrugClass && isTherapy) {
            throw new IllegalStateException("An treatment approach cannot be both a drug class and therapy");
        }

        if (isDrugClass) {
            return ImmutableTherapyTreatmentApproach.builder()
                    .therapy(TherapyFactory.resolveTherapy(ckbJsonDatabase, Objects.requireNonNull(treatmentApproach.therapy())))
                    .build();
        } else if (isTherapy) {
            return ImmutableDrugClassTreatmentApproach.builder()
                    .drugClass(DrugFactory.resolveDrugClass(ckbJsonDatabase, Objects.requireNonNull(treatmentApproach.drugClass())))
                    .build();
        } else {
            throw new IllegalStateException("An treatment approach cannot be both a drug class and therapy!");
        }
    }

    @NotNull
    private static RelevantTreatmentApproaches resolveRelevantTreatmentApproaches(@NotNull CkbJsonDatabase ckbJsonDatabase,
            @NotNull TreatmentApproachInfo treatmentApproachInfo) {
        for (JsonTreatmentApproach treatmentApproach : ckbJsonDatabase.treatmentApproaches()) {
            if (treatmentApproach.id() == treatmentApproachInfo.id()) {
                return ImmutableRelevantTreatmentApproaches.builder()
                        .id(treatmentApproach.id())
                        .treatmentApproachIntervation(extractTreatmentApproachIntervation(ckbJsonDatabase, treatmentApproach))
                        .references(ReferenceFactory.extractReferences(ckbJsonDatabase, treatmentApproach.references()))
                        .createDate(treatmentApproach.createDate())
                        .updateDate(treatmentApproach.updateDate())
                        .build();
            }
        }
        throw new IllegalStateException("Could not resolve CKB treatment approach with id '" + treatmentApproachInfo.id() + "'");
    }
}