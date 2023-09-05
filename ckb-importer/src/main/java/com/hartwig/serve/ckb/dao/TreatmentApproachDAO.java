package com.hartwig.serve.ckb.dao;

import com.hartwig.serve.ckb.database.Tables;
import com.hartwig.serve.ckb.database.tables.Treatmentapproach;
import com.hartwig.serve.ckb.datamodel.drug.DrugClass;
import com.hartwig.serve.ckb.datamodel.reference.Reference;
import com.hartwig.serve.ckb.datamodel.treatmentapproaches.RelevantTreatmentApproaches;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;

class TreatmentApproachDAO {

    @NotNull
    private final DSLContext context;

    public TreatmentApproachDAO(@NotNull final DSLContext context) {
        this.context = context;
    }

    public void deleteAll() {
        // Note that deletions should go from branch to root
        context.deleteFrom(Tables.TREATMENTAPPROACHDRUGCLASS).execute();
        context.deleteFrom(Tables.TREATMENTAPPROACHREFERENCE).execute();

        context.deleteFrom(Treatmentapproach.TREATMENTAPPROACH).execute();

    }

    public int write(@NotNull RelevantTreatmentApproaches treatmentApproaches) {
        int id = context.insertInto(Treatmentapproach.TREATMENTAPPROACH,
                        Treatmentapproach.TREATMENTAPPROACH.TREATMENTAPPROACHID,
                        Treatmentapproach.TREATMENTAPPROACH.CREATEDATE,
                        Treatmentapproach.TREATMENTAPPROACH.UPDATEDATE)
                .values(treatmentApproaches.id(), treatmentApproaches.createDate(), treatmentApproaches.updateDate())
                .returning(Treatmentapproach.TREATMENTAPPROACH.ID)
                .fetchOne()
                .getValue(Treatmentapproach.TREATMENTAPPROACH.ID);

        if (treatmentApproaches.drugClass() != null) {
            writeTreatmentDrugClass(treatmentApproaches.drugClass(), id);
        }

        for (Reference reference : treatmentApproaches.references()) {
            writeTreatmentReference(reference, id);
        }

        return id;
    }

    private void writeTreatmentDrugClass(@Nullable DrugClass drugClass, int treatmentApproachDrugClassId) {
        context.insertInto(Tables.TREATMENTAPPROACHDRUGCLASS,
                        Tables.TREATMENTAPPROACHDRUGCLASS.TREATMENTAPPROACHID,
                        Tables.TREATMENTAPPROACHDRUGCLASS.DRUGCLASSID,
                        Tables.TREATMENTAPPROACHDRUGCLASS.CREATEDATE,
                        Tables.TREATMENTAPPROACHDRUGCLASS.DRUGCLASS)
                .values(treatmentApproachDrugClassId, drugClass.id(), drugClass.createDate(), drugClass.drugClass())
                .execute();
    }

    private void writeTreatmentReference(@NotNull Reference reference, int treatmentApproachReferenceId) {
        context.insertInto(Tables.TREATMENTAPPROACHREFERENCE,
                        Tables.TREATMENTAPPROACHREFERENCE.TREATMENTAPPROACHID,
                        Tables.TREATMENTAPPROACHREFERENCE.REFERENCEID,
                        Tables.TREATMENTAPPROACHREFERENCE.PUBMEDID,
                        Tables.TREATMENTAPPROACHREFERENCE.TITLE,
                        Tables.TREATMENTAPPROACHREFERENCE.SHORTJOURNALTITLE,
                        Tables.TREATMENTAPPROACHREFERENCE.PAGES,
                        Tables.TREATMENTAPPROACHREFERENCE.ABSTRACTTEXT,
                        Tables.TREATMENTAPPROACHREFERENCE.URL,
                        Tables.TREATMENTAPPROACHREFERENCE.JOURNAL,
                        Tables.TREATMENTAPPROACHREFERENCE.AUTHORS,
                        Tables.TREATMENTAPPROACHREFERENCE.VOLUME,
                        Tables.TREATMENTAPPROACHREFERENCE.ISSUE,
                        Tables.TREATMENTAPPROACHREFERENCE.DATE,
                        Tables.TREATMENTAPPROACHREFERENCE.YEAR)
                .values(treatmentApproachReferenceId,
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
