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
import org.jetbrains.annotations.Nullable;

public final class RelevantTreatmentApproachesFactory {

    private RelevantTreatmentApproachesFactory() {
    }

    @NotNull
    public static List<DrugClassTreatmentApproach> extractDrugTreatmentApproaches(@NotNull CkbJsonDatabase ckbJsonDatabase,
            @NotNull List<TreatmentApproachInfo> treatmentApproachInfos) {
        List<DrugClassTreatmentApproach> relevantTreatmentApproach = Lists.newArrayList();

        for (TreatmentApproachInfo treatmentApproachInfo : treatmentApproachInfos) {
            DrugClassTreatmentApproach resolvedRelevantTreatmentApproaches =
                    resolveDrugTreatmentApproaches(ckbJsonDatabase, treatmentApproachInfo);
            if (resolvedRelevantTreatmentApproaches != null) {
                relevantTreatmentApproach.add(resolvedRelevantTreatmentApproaches);
            }        }
        return relevantTreatmentApproach;
    }

    @NotNull
    public static List<TherapyTreatmentApproach> extractTherapyTreatmentApproaches(@NotNull CkbJsonDatabase ckbJsonDatabase,
            @NotNull List<TreatmentApproachInfo> treatmentApproachInfos) {
        List<TherapyTreatmentApproach> relevantTreatmentApproach = Lists.newArrayList();

        for (TreatmentApproachInfo treatmentApproachInfo : treatmentApproachInfos) {
            TherapyTreatmentApproach resolvedRelevantTreatmentApproaches =
                    resolveTherapyTreatmentApproaches(ckbJsonDatabase, treatmentApproachInfo);
            if (resolvedRelevantTreatmentApproaches != null) {
                relevantTreatmentApproach.add(resolvedRelevantTreatmentApproaches);
            }
        }
        return relevantTreatmentApproach;
    }

    @Nullable
    private static DrugClassTreatmentApproach resolveDrugTreatmentApproaches(@NotNull CkbJsonDatabase ckbJsonDatabase,
            @NotNull TreatmentApproachInfo treatmentApproachInfo) {

        for (JsonTreatmentApproach treatmentApproach : ckbJsonDatabase.treatmentApproaches()) {
            if (treatmentApproach.id() == treatmentApproachInfo.id()) {
                var drugClass = treatmentApproach.drugClass();
                if (drugClass != null) {
                    return ImmutableDrugClassTreatmentApproach.builder()
                            .id(treatmentApproach.id())
                            .drugClass(DrugFactory.resolveDrugClass(ckbJsonDatabase, Objects.requireNonNull(treatmentApproach.drugClass())))
                            .references(ReferenceFactory.extractReferences(ckbJsonDatabase, treatmentApproach.references()))
                            .createDate(treatmentApproach.createDate())
                            .updateDate(treatmentApproach.updateDate())
                            .build();
                } else {
                    return null;
                }
            }
        }
        throw new IllegalStateException("Could not resolve CKB treatment approach drug class with id '" + treatmentApproachInfo.id() + "'");
    }

    @Nullable
    private static TherapyTreatmentApproach resolveTherapyTreatmentApproaches(@NotNull CkbJsonDatabase ckbJsonDatabase,
            @NotNull TreatmentApproachInfo treatmentApproachInfo) {

        for (JsonTreatmentApproach treatmentApproach : ckbJsonDatabase.treatmentApproaches()) {
            if (treatmentApproach.id() == treatmentApproachInfo.id()) {
                var therapy = treatmentApproach.therapy();
                if (therapy != null) {
                    return ImmutableTherapyTreatmentApproach.builder()
                            .id(treatmentApproach.id())
                            .therapy(TherapyFactory.resolveTherapy(ckbJsonDatabase, therapy))
                            .references(ReferenceFactory.extractReferences(ckbJsonDatabase, treatmentApproach.references()))
                            .createDate(treatmentApproach.createDate())
                            .updateDate(treatmentApproach.updateDate())
                            .build();
                } else {
                    return null;
                }
            }
        }
        throw new IllegalStateException("Could not resolve CKB treatment approach therapy with id '" + treatmentApproachInfo.id() + "'");
    }
}