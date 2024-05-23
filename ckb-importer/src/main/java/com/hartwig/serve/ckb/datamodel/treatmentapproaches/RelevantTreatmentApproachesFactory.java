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
    private static RelevantTreatmentApproaches resolveRelevantTreatmentApproaches(@NotNull CkbJsonDatabase ckbJsonDatabase,
            @NotNull TreatmentApproachInfo treatmentApproachInfo) {
        for (JsonTreatmentApproach treatmentApproach : ckbJsonDatabase.treatmentApproaches()) {
            if (treatmentApproach.id() == treatmentApproachInfo.id()) {

                return ImmutableRelevantTreatmentApproaches.builder()
                        .id(treatmentApproach.id())
                        .name(treatmentApproach.name())
                        .drugClass(treatmentApproach.drugClass() != null ? DrugFactory.resolveDrugClass(ckbJsonDatabase,
                                Objects.requireNonNull(treatmentApproach.drugClass())) : null)
                        .therapy(treatmentApproach.therapy() != null ? TherapyFactory.resolveTherapy(ckbJsonDatabase,
                                Objects.requireNonNull(treatmentApproach.therapy())) : null)
                        .references(ReferenceFactory.extractReferences(ckbJsonDatabase, treatmentApproach.references()))
                        .createDate(treatmentApproach.createDate())
                        .updateDate(treatmentApproach.updateDate())
                        .build();
            }
        }
        throw new IllegalStateException("Could not resolve CKB treatment approach with id '" + treatmentApproachInfo.id() + "'");
    }
}