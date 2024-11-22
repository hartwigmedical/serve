package com.hartwig.serve.ckb.dao;

import com.hartwig.serve.ckb.database.tables.Evidence;
import com.hartwig.serve.ckb.database.tables.Evidencereference;
import com.hartwig.serve.ckb.database.tables.Indicationevidence;
import com.hartwig.serve.ckb.database.tables.Therapyevidence;
import com.hartwig.serve.ckb.database.tables.Treatmentapproachevidence;
import com.hartwig.serve.ckb.datamodel.reference.Reference;
import com.hartwig.serve.ckb.datamodel.treatmentapproaches.DrugClassTreatmentApproach;
import com.hartwig.serve.ckb.datamodel.treatmentapproaches.TherapyTreatmentApproach;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

class EvidenceDAO {

    @NotNull
    private final DSLContext context;
    @NotNull
    private final TherapyDAO therapyDAO;
    @NotNull
    private final IndicationDAO indicationDAO;
    @NotNull
    private final TreatmentApproachDAO treatmentApproachDAO;

    public EvidenceDAO(@NotNull final DSLContext context, @NotNull final TherapyDAO therapyDAO, @NotNull final IndicationDAO indicationDAO,
            @NotNull final TreatmentApproachDAO treatmentApproachDAO) {
        this.context = context;
        this.therapyDAO = therapyDAO;
        this.indicationDAO = indicationDAO;
        this.treatmentApproachDAO = treatmentApproachDAO;
    }

    public void deleteAll() {
        // Note that deletions should go from branch to root
        context.deleteFrom(Evidencereference.EVIDENCEREFERENCE).execute();
        context.deleteFrom(Therapyevidence.THERAPYEVIDENCE).execute();
        context.deleteFrom(Indicationevidence.INDICATIONEVIDENCE).execute();
        context.deleteFrom(Treatmentapproachevidence.TREATMENTAPPROACHEVIDENCE).execute();
        context.deleteFrom(Evidence.EVIDENCE).execute();
    }

    public void write(@NotNull com.hartwig.serve.ckb.datamodel.evidence.Evidence evidence, int ckbEntryId) {
        int id = context.insertInto(Evidence.EVIDENCE,
                        Evidence.EVIDENCE.CKBENTRYID,
                        Evidence.EVIDENCE.CKBEVIDENCEID,
                        Evidence.EVIDENCE.RESPONSETYPE,
                        Evidence.EVIDENCE.EVIDENCETYPE,
                        Evidence.EVIDENCE.EFFICACYEVIDENCE,
                        Evidence.EVIDENCE.APPROVALSTATUS,
                        Evidence.EVIDENCE.AMPCAPASCOEVIDENCELEVEL,
                        Evidence.EVIDENCE.AMPCAPASCOINFERREDTIER)
                .values(ckbEntryId,
                        evidence.id(),
                        evidence.responseType(),
                        evidence.evidenceType(),
                        evidence.efficacyEvidence(),
                        evidence.approvalStatus(),
                        evidence.ampCapAscoEvidenceLevel(),
                        evidence.ampCapAscoInferredTier())
                .returning(Evidence.EVIDENCE.ID)
                .fetchOne()
                .getValue(Evidence.EVIDENCE.ID);

        int therapyId = therapyDAO.write(evidence.therapy());
        context.insertInto(Therapyevidence.THERAPYEVIDENCE,
                Therapyevidence.THERAPYEVIDENCE.EVIDENCEID,
                Therapyevidence.THERAPYEVIDENCE.THERAPYID).values(id, therapyId).execute();

        int indicationId = indicationDAO.write(evidence.indication());
        context.insertInto(Indicationevidence.INDICATIONEVIDENCE,
                Indicationevidence.INDICATIONEVIDENCE.EVIDENCEID,
                Indicationevidence.INDICATIONEVIDENCE.INDICATIONID).values(id, indicationId).execute();

        for (DrugClassTreatmentApproach treatmentApproaches : evidence.drugTreatmentApproaches()) {
            int treatmentApproachId = treatmentApproachDAO.write(treatmentApproaches);

            context.insertInto(Treatmentapproachevidence.TREATMENTAPPROACHEVIDENCE,
                            Treatmentapproachevidence.TREATMENTAPPROACHEVIDENCE.EVIDENCEID,
                            Treatmentapproachevidence.TREATMENTAPPROACHEVIDENCE.TREATMENTAPPROACHEVIDENCEID)
                    .values(id, treatmentApproachId)
                    .execute();
        }

        for (TherapyTreatmentApproach treatmentApproaches : evidence.therapyTreatmentApproaches()) {
            int treatmentApproachId = treatmentApproachDAO.write(treatmentApproaches);

            context.insertInto(Treatmentapproachevidence.TREATMENTAPPROACHEVIDENCE,
                            Treatmentapproachevidence.TREATMENTAPPROACHEVIDENCE.EVIDENCEID,
                            Treatmentapproachevidence.TREATMENTAPPROACHEVIDENCE.TREATMENTAPPROACHEVIDENCEID)
                    .values(id, treatmentApproachId)
                    .execute();
        }

        for (Reference reference : evidence.references()) {
            writeReference(reference, id);
        }
    }

    private void writeReference(@NotNull Reference reference, int evidenceId) {
        context.insertInto(Evidencereference.EVIDENCEREFERENCE,
                        Evidencereference.EVIDENCEREFERENCE.EVIDENCEID,
                        Evidencereference.EVIDENCEREFERENCE.CKBREFERENCEID,
                        Evidencereference.EVIDENCEREFERENCE.PUBMEDID,
                        Evidencereference.EVIDENCEREFERENCE.TITLE,
                        Evidencereference.EVIDENCEREFERENCE.SHORTJOURNALTITLE,
                        Evidencereference.EVIDENCEREFERENCE.PAGES,
                        Evidencereference.EVIDENCEREFERENCE.ABSTRACTTEXT,
                        Evidencereference.EVIDENCEREFERENCE.URL,
                        Evidencereference.EVIDENCEREFERENCE.JOURNAL,
                        Evidencereference.EVIDENCEREFERENCE.AUTHORS,
                        Evidencereference.EVIDENCEREFERENCE.VOLUME,
                        Evidencereference.EVIDENCEREFERENCE.ISSUE,
                        Evidencereference.EVIDENCEREFERENCE.DATE,
                        Evidencereference.EVIDENCEREFERENCE.YEAR)
                .values(evidenceId,
                        reference.id(),
                        reference.pubMedId(),
                        reference.title(),
                        reference.shortJournalTitle(),
                        reference.pages(),
                        reference.abstractText(),
                        reference.url(),
                        reference.journal(),
                        reference.authors(),
                        reference.volume(),
                        reference.issue(),
                        reference.date(),
                        reference.year())
                .execute();
    }
}
