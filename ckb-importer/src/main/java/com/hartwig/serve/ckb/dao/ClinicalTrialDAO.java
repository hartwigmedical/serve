package com.hartwig.serve.ckb.dao;

import com.hartwig.serve.ckb.database.tables.Agegroup;
import com.hartwig.serve.ckb.database.tables.Clinicaltrial;
import com.hartwig.serve.ckb.database.tables.Contact;
import com.hartwig.serve.ckb.database.tables.Indicationclinicaltrial;
import com.hartwig.serve.ckb.database.tables.Location;
import com.hartwig.serve.ckb.database.tables.Therapyclinicaltrial;
import com.hartwig.serve.ckb.database.tables.Variantrequirementdetail;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ClinicalTrial;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.VariantRequirementDetail;
import com.hartwig.serve.ckb.datamodel.indication.Indication;
import com.hartwig.serve.ckb.datamodel.therapy.Therapy;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

class ClinicalTrialDAO {

    @NotNull
    private final DSLContext context;
    @NotNull
    private final TherapyDAO therapyDAO;
    @NotNull
    private final IndicationDAO indicationDAO;

    public ClinicalTrialDAO(@NotNull final DSLContext context, @NotNull final TherapyDAO therapyDAO,
            @NotNull final IndicationDAO indicationDAO) {
        this.context = context;
        this.therapyDAO = therapyDAO;
        this.indicationDAO = indicationDAO;
    }

    public void deleteAll() {
        // Note that deletions should go from branch to root
        context.deleteFrom(Contact.CONTACT).execute();
        context.deleteFrom(Location.LOCATION).execute();

        context.deleteFrom(Variantrequirementdetail.VARIANTREQUIREMENTDETAIL).execute();
        context.deleteFrom(Agegroup.AGEGROUP).execute();

        context.deleteFrom(Indicationclinicaltrial.INDICATIONCLINICALTRIAL).execute();
        context.deleteFrom(Therapyclinicaltrial.THERAPYCLINICALTRIAL).execute();

        context.deleteFrom(Clinicaltrial.CLINICALTRIAL).execute();
    }

    public void write(@NotNull ClinicalTrial clinicalTrial, int ckbEntryId) {
        int id = context.insertInto(Clinicaltrial.CLINICALTRIAL,
                Clinicaltrial.CLINICALTRIAL.CKBENTRYID,
                Clinicaltrial.CLINICALTRIAL.UPDATEDATE,
                Clinicaltrial.CLINICALTRIAL.NCTID,
                Clinicaltrial.CLINICALTRIAL.TITLE,
                Clinicaltrial.CLINICALTRIAL.ACRONYM,
                Clinicaltrial.CLINICALTRIAL.PHASE,
                Clinicaltrial.CLINICALTRIAL.RECRUITMENT,
                Clinicaltrial.CLINICALTRIAL.GENDER,
                Clinicaltrial.CLINICALTRIAL.SPONSORS,
                Clinicaltrial.CLINICALTRIAL.VARIANTREQUIREMENT)
                .values(ckbEntryId,
                        clinicalTrial.updateDate(),
                        clinicalTrial.nctId(),
                        clinicalTrial.title(),
                        clinicalTrial.acronym(),
                        clinicalTrial.phase(),
                        clinicalTrial.recruitment(),
                        clinicalTrial.gender(),
                        clinicalTrial.sponsors(),
                        clinicalTrial.variantRequirement())
                .returning(Clinicaltrial.CLINICALTRIAL.ID)
                .fetchOne()
                .getValue(Clinicaltrial.CLINICALTRIAL.ID);

        for (Therapy therapy : clinicalTrial.therapies()) {
            int therapyId = therapyDAO.write(therapy);
            context.insertInto(Therapyclinicaltrial.THERAPYCLINICALTRIAL, Therapyclinicaltrial.THERAPYCLINICALTRIAL.CLINICALTRIALID, Therapyclinicaltrial.THERAPYCLINICALTRIAL.THERAPYID)
                    .values(id, therapyId)
                    .execute();
        }

        for (Indication indication : clinicalTrial.indications()) {
            int indicationId = indicationDAO.write(indication);
            context.insertInto(Indicationclinicaltrial.INDICATIONCLINICALTRIAL, Indicationclinicaltrial.INDICATIONCLINICALTRIAL.CLINICALTRIALID, Indicationclinicaltrial.INDICATIONCLINICALTRIAL.INDICATIONID)
                    .values(id, indicationId)
                    .execute();
        }

        for (String ageGroup : clinicalTrial.ageGroups()) {
            context.insertInto(Agegroup.AGEGROUP, Agegroup.AGEGROUP.CLINICALTRIALID, Agegroup.AGEGROUP.AGEGROUP_).values(id, ageGroup).execute();
        }

        for (VariantRequirementDetail variantRequirementDetail : clinicalTrial.variantRequirementDetails()) {
            context.insertInto(Variantrequirementdetail.VARIANTREQUIREMENTDETAIL,
                    Variantrequirementdetail.VARIANTREQUIREMENTDETAIL.CLINICALTRIALID,
                    Variantrequirementdetail.VARIANTREQUIREMENTDETAIL.CKBPROFILEID,
                    Variantrequirementdetail.VARIANTREQUIREMENTDETAIL.REQUIREMENTTYPE)
                    .values(id, variantRequirementDetail.profileId(), variantRequirementDetail.requirementType())
                    .execute();
        }

        for (com.hartwig.serve.ckb.datamodel.clinicaltrial.Location location : clinicalTrial.locations()) {
            writeLocation(location, id);
        }

    }

    private void writeLocation(@NotNull com.hartwig.serve.ckb.datamodel.clinicaltrial.Location location, int clinicalTrialId) {
        int id = context.insertInto(Location.LOCATION,
                Location.LOCATION.CLINICALTRIALID,
                Location.LOCATION.NCTID,
                Location.LOCATION.STATUS,
                Location.LOCATION.FACILITY,
                Location.LOCATION.CITY,
                Location.LOCATION.STATE,
                Location.LOCATION.ZIP,
                Location.LOCATION.COUNTRY)
                .values(clinicalTrialId,
                        location.nctId(),
                        location.status(),
                        location.facility(),
                        location.city(),
                        location.state(),
                        location.zip(),
                        location.country())
                .returning(Location.LOCATION.ID)
                .fetchOne()
                .getValue(Location.LOCATION.ID);

        for (com.hartwig.serve.ckb.datamodel.clinicaltrial.Contact contact : location.contacts()) {
            context.insertInto(Contact.CONTACT, Contact.CONTACT.LOCATIONID, Contact.CONTACT.NAME, Contact.CONTACT.EMAIL, Contact.CONTACT.PHONE, Contact.CONTACT.PHONEEXT, Contact.CONTACT.ROLE)
                    .values(id, contact.name(), contact.email(), contact.phone(), contact.phoneExt(), contact.role())
                    .execute();
        }
    }
}
