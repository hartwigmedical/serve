package com.hartwig.serve.ckb.dao;

import com.hartwig.serve.ckb.database.Tables;
import com.hartwig.serve.ckb.database.tables.Treatmentapproach;
import com.hartwig.serve.ckb.datamodel.reference.Reference;
import com.hartwig.serve.ckb.datamodel.treatmentapproaches.DrugClassTreatmentApproach;
import com.hartwig.serve.ckb.datamodel.treatmentapproaches.TherapyTreatmentApproach;

import org.jetbrains.annotations.NotNull;
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
        context.deleteFrom(Tables.TREATMENTAPPROACHTHERAPY).execute();
        context.deleteFrom(Tables.TREATMENTAPPROACHREFERENCE).execute();

        context.deleteFrom(Treatmentapproach.TREATMENTAPPROACH).execute();

    }

    public int write(@NotNull DrugClassTreatmentApproach treatmentApproaches) {
        int id = context.insertInto(Treatmentapproach.TREATMENTAPPROACH,
                        Treatmentapproach.TREATMENTAPPROACH.TREATMENTAPPROACHID,
                        Treatmentapproach.TREATMENTAPPROACH.CREATEDATE,
                        Treatmentapproach.TREATMENTAPPROACH.UPDATEDATE)
                .values(treatmentApproaches.id(), treatmentApproaches.createDate(), treatmentApproaches.updateDate())
                .returning(Treatmentapproach.TREATMENTAPPROACH.ID)
                .fetchOne()
                .getValue(Treatmentapproach.TREATMENTAPPROACH.ID);

        writeTreatmentDrugClass(treatmentApproaches, id);

        for (Reference reference : treatmentApproaches.references()) {
            writeTreatmentReference(reference, id);
        }

        return id;
    }

    public int write(@NotNull TherapyTreatmentApproach treatmentApproaches) {
        int id = context.insertInto(Treatmentapproach.TREATMENTAPPROACH,
                        Treatmentapproach.TREATMENTAPPROACH.TREATMENTAPPROACHID,
                        Treatmentapproach.TREATMENTAPPROACH.CREATEDATE,
                        Treatmentapproach.TREATMENTAPPROACH.UPDATEDATE)
                .values(treatmentApproaches.id(), treatmentApproaches.createDate(), treatmentApproaches.updateDate())
                .returning(Treatmentapproach.TREATMENTAPPROACH.ID)
                .fetchOne()
                .getValue(Treatmentapproach.TREATMENTAPPROACH.ID);

        writeTreatmentTherapy(treatmentApproaches, id);

        for (Reference reference : treatmentApproaches.references()) {
            writeTreatmentReference(reference, id);
        }

        return id;
    }

    private void writeTreatmentDrugClass(@NotNull DrugClassTreatmentApproach drugClassTreatmentApproach, int treatmentApproachDrugClassId) {
        //Only written relevant drug class name for treatment approach and other redundant for table drugclass
        context.insertInto(Tables.TREATMENTAPPROACHDRUGCLASS,
                        Tables.TREATMENTAPPROACHDRUGCLASS.TREATMENTAPPROACHID,
                        Tables.TREATMENTAPPROACHDRUGCLASS.DRUGCLASSID,
                        Tables.TREATMENTAPPROACHDRUGCLASS.DRUGCLASS)
                .values(treatmentApproachDrugClassId, drugClassTreatmentApproach.drugClass().id(), drugClassTreatmentApproach.drugClass().drugClass())
                .execute();
    }

    private void writeTreatmentTherapy(@NotNull TherapyTreatmentApproach therapyTreatmentApproach, int treatmentApproachDrugClassId) {
        //Only written relevant therapy name for treatment approach and other redundant for tables therapy/therapy synonym
        context.insertInto(Tables.TREATMENTAPPROACHTHERAPY,
                        Tables.TREATMENTAPPROACHTHERAPY.TREATMENTAPPROACHID,
                        Tables.TREATMENTAPPROACHTHERAPY.THERAPYID,
                        Tables.TREATMENTAPPROACHTHERAPY.THERAPYNAME)
                .values(treatmentApproachDrugClassId,
                        therapyTreatmentApproach.therapy().id(),
                        therapyTreatmentApproach.therapy().therapyName())
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
