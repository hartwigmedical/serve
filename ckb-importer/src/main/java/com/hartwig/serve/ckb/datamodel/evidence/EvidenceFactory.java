package com.hartwig.serve.ckb.datamodel.evidence;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.serve.ckb.datamodel.indication.IndicationFactory;
import com.hartwig.serve.ckb.datamodel.reference.ReferenceFactory;
import com.hartwig.serve.ckb.datamodel.therapy.TherapyFactory;
import com.hartwig.serve.ckb.datamodel.treatmentapproaches.RelevantTreatmentApproachesFactory;
import com.hartwig.serve.ckb.json.CkbJsonDatabase;
import com.hartwig.serve.ckb.json.common.EvidenceInfo;

import org.jetbrains.annotations.NotNull;

public final class EvidenceFactory {

    private EvidenceFactory() {
    }

    @NotNull
    public static List<Evidence> extractEvidences(@NotNull CkbJsonDatabase ckbJsonDatabase, @NotNull List<EvidenceInfo> evidenceInfos) {
        List<Evidence> evidences = Lists.newArrayList();
        for (EvidenceInfo evidenceInfo : evidenceInfos) {
            evidences.add(ImmutableEvidence.builder()
                    .id(evidenceInfo.id())
                    .therapy(TherapyFactory.resolveTherapy(ckbJsonDatabase, evidenceInfo.therapy()))
                    .indication(IndicationFactory.resolveIndication(ckbJsonDatabase, evidenceInfo.indication()))
                    .responseType(evidenceInfo.responseType())
                    .relevantTreatmentApproaches(RelevantTreatmentApproachesFactory.extractRelevantTreatmentApproaches(ckbJsonDatabase,
                            evidenceInfo.treatmentApproaches()))
                    .evidenceType(evidenceInfo.evidenceType())
                    .efficacyEvidence(evidenceInfo.efficacyEvidence())
                    .approvalStatus(evidenceInfo.approvalStatus())
                    .ampCapAscoEvidenceLevel(evidenceInfo.ampCapAscoEvidenceLevel())
                    .ampCapAscoInferredTier(evidenceInfo.ampCapAscoInferredTier())
                    .references(ReferenceFactory.extractReferences(ckbJsonDatabase, evidenceInfo.references()))
                    .build());
        }
        return evidences;
    }
}