package com.hartwig.serve.ckb.dao;

import com.hartwig.serve.ckb.database.Tables;
import com.hartwig.serve.ckb.database.tables.Drug;
import com.hartwig.serve.ckb.database.tables.Drugclass;
import com.hartwig.serve.ckb.database.tables.Drugsynonym;
import com.hartwig.serve.ckb.database.tables.Drugterm;
import com.hartwig.serve.ckb.database.tables.Globalapprovalstatus;
import com.hartwig.serve.ckb.database.tables.Therapy;
import com.hartwig.serve.ckb.database.tables.Therapysynonym;
import com.hartwig.serve.ckb.datamodel.drug.DrugClass;
import com.hartwig.serve.ckb.datamodel.reference.Reference;
import com.hartwig.serve.ckb.datamodel.therapy.GlobalApprovalStatus;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

class TherapyDAO {

    @NotNull
    private final DSLContext context;
    @NotNull
    private final BatchInserter batchInserter;

    public TherapyDAO(@NotNull final DSLContext context, @NotNull final BatchInserter batchInserter) {
        this.context = context;
        this.batchInserter = batchInserter;
    }

    public void deleteAll() {
        // Note that deletions should go from branch to root
        context.deleteFrom(Tables.DRUGREFERENCE).execute();
        context.deleteFrom(Drugterm.DRUGTERM).execute();
        context.deleteFrom(Drugsynonym.DRUGSYNONYM).execute();
        context.deleteFrom(Drugclass.DRUGCLASS).execute();
        context.deleteFrom(Drug.DRUG).execute();

        context.deleteFrom(Therapysynonym.THERAPYSYNONYM).execute();
        context.deleteFrom(Tables.THERAPYREFERENCE).execute();

        context.deleteFrom(Globalapprovalstatus.GLOBALAPPROVALSTATUS).execute();

        context.deleteFrom(Therapy.THERAPY).execute();
    }

    public int write(@NotNull com.hartwig.serve.ckb.datamodel.therapy.Therapy therapy) {
        int id = context.insertInto(Therapy.THERAPY,
                        Therapy.THERAPY.CKBTHERAPYID,
                        Therapy.THERAPY.CREATEDATE,
                        Therapy.THERAPY.UPDATEDATE,
                        Therapy.THERAPY.THERAPYNAME,
                        Therapy.THERAPY.DESCRIPTION)
                .values(therapy.id(), therapy.createDate(), therapy.updateDate(), therapy.therapyName(), therapy.description())
                .returning(Therapy.THERAPY.ID)
                .fetchOne()
                .getValue(Therapy.THERAPY.ID);

        for (com.hartwig.serve.ckb.datamodel.drug.Drug drug : therapy.drugs()) {
            writeDrug(drug, id);
        }

        if (therapy.synonyms() != null) {
            for (String synonym : therapy.synonyms()) {
                batchInserter.add(context.insertInto(Therapysynonym.THERAPYSYNONYM,
                        Therapysynonym.THERAPYSYNONYM.THERAPYID,
                        Therapysynonym.THERAPYSYNONYM.SYNONYM).values(id, synonym));
            }
        }

        for (Reference reference : therapy.references()) {
            writeTherapyReference(reference, id);
        }

        for (GlobalApprovalStatus globalApprovalStatus : therapy.globalApprovalStatuses()) {
            batchInserter.add(context.insertInto(Globalapprovalstatus.GLOBALAPPROVALSTATUS,
                    Globalapprovalstatus.GLOBALAPPROVALSTATUS.THERAPYID,
                    Globalapprovalstatus.GLOBALAPPROVALSTATUS.CKBGLOBALAPPROVALSTATUSID,
                    Globalapprovalstatus.GLOBALAPPROVALSTATUS.CKBPROFILEID,
                    Globalapprovalstatus.GLOBALAPPROVALSTATUS.CKBINDICATIONID,
                    Globalapprovalstatus.GLOBALAPPROVALSTATUS.APPROVALSTATUS,
                    Globalapprovalstatus.GLOBALAPPROVALSTATUS.APPROVALAUTHORITY)
                    .values(id,
                            globalApprovalStatus.id(),
                            globalApprovalStatus.profileId(),
                            globalApprovalStatus.indicationId(),
                            globalApprovalStatus.approvalStatus(),
                            globalApprovalStatus.approvalAuthority()));
        }
        return id;
    }

    private void writeDrug(@NotNull com.hartwig.serve.ckb.datamodel.drug.Drug drug, int therapyId) {
        int id = context.insertInto(Drug.DRUG,
                        Drug.DRUG.THERAPYID,
                        Drug.DRUG.CKBDRUGID,
                        Drug.DRUG.CREATEDATE,
                        Drug.DRUG.DRUGNAME,
                        Drug.DRUG.TRADENAME,
                        Drug.DRUG.CASREGISTRYNUM,
                        Drug.DRUG.NCITID,
                        Drug.DRUG.DESCRIPTION)
                .values(therapyId,
                        drug.id(),
                        drug.createDate(),
                        drug.drugName(),
                        drug.tradeName(),
                        drug.casRegistryNum(),
                        drug.ncitId(),
                        drug.description())
                .returning(Drug.DRUG.ID)
                .fetchOne()
                .getValue(Drug.DRUG.ID);

        for (DrugClass drugClass : drug.drugClasses()) {
            batchInserter.add(context.insertInto(Drugclass.DRUGCLASS,
                    Drugclass.DRUGCLASS.DRUGID,
                    Drugclass.DRUGCLASS.CKBDRUGCLASSID,
                    Drugclass.DRUGCLASS.CREATEDATE,
                    Drugclass.DRUGCLASS.DRUGCLASS_)
                    .values(id, drugClass.id(), drugClass.createDate(), drugClass.drugClass()));
        }

        for (String term : drug.terms()) {
            batchInserter.add(context.insertInto(Drugterm.DRUGTERM, Drugterm.DRUGTERM.DRUGID, Drugterm.DRUGTERM.TERM)
                    .values(id, term));
        }

        for (String synonym : drug.synonyms()) {
            batchInserter.add(context.insertInto(Drugsynonym.DRUGSYNONYM, Drugsynonym.DRUGSYNONYM.DRUGID, Drugsynonym.DRUGSYNONYM.SYNONYM)
                    .values(id, synonym));
        }

        for (Reference reference : drug.references()) {
            writeDrugReference(reference, id);
        }
    }

    private void writeDrugReference(@NotNull Reference reference, int drugId) {
        batchInserter.add(context.insertInto(Tables.DRUGREFERENCE,
                Tables.DRUGREFERENCE.DRUGID,
                Tables.DRUGREFERENCE.CKBREFERENCEID,
                Tables.DRUGREFERENCE.PUBMEDID,
                Tables.DRUGREFERENCE.TITLE,
                Tables.DRUGREFERENCE.SHORTJOURNALTITLE,
                Tables.DRUGREFERENCE.PAGES,
                Tables.DRUGREFERENCE.ABSTRACTTEXT,
                Tables.DRUGREFERENCE.URL,
                Tables.DRUGREFERENCE.JOURNAL,
                Tables.DRUGREFERENCE.AUTHORS,
                Tables.DRUGREFERENCE.VOLUME,
                Tables.DRUGREFERENCE.ISSUE,
                Tables.DRUGREFERENCE.DATE,
                Tables.DRUGREFERENCE.YEAR)
                .values(drugId,
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
                        reference.year()));
    }

    private void writeTherapyReference(@NotNull Reference reference, int therapyId) {
        batchInserter.add(context.insertInto(Tables.THERAPYREFERENCE,
                Tables.THERAPYREFERENCE.THERAPYID,
                Tables.THERAPYREFERENCE.CKBREFERENCEID,
                Tables.THERAPYREFERENCE.PUBMEDID,
                Tables.THERAPYREFERENCE.TITLE,
                Tables.THERAPYREFERENCE.SHORTJOURNALTITLE,
                Tables.THERAPYREFERENCE.PAGES,
                Tables.THERAPYREFERENCE.ABSTRACTTEXT,
                Tables.THERAPYREFERENCE.URL,
                Tables.THERAPYREFERENCE.JOURNAL,
                Tables.THERAPYREFERENCE.AUTHORS,
                Tables.THERAPYREFERENCE.VOLUME,
                Tables.THERAPYREFERENCE.ISSUE,
                Tables.THERAPYREFERENCE.DATE,
                Tables.THERAPYREFERENCE.YEAR)
                .values(therapyId,
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
                        reference.year()));
    }
}
