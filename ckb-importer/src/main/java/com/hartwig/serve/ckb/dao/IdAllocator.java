package com.hartwig.serve.ckb.dao;

import com.hartwig.serve.ckb.database.tables.Clinicaltrial;
import com.hartwig.serve.ckb.database.tables.Ckbentry;
import com.hartwig.serve.ckb.database.tables.Drug;
import com.hartwig.serve.ckb.database.tables.Evidence;
import com.hartwig.serve.ckb.database.tables.Gene;
import com.hartwig.serve.ckb.database.tables.Indication;
import com.hartwig.serve.ckb.database.tables.Location;
import com.hartwig.serve.ckb.database.tables.Therapy;
import com.hartwig.serve.ckb.database.tables.Treatmentapproach;
import com.hartwig.serve.ckb.database.tables.Variant;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;

final class IdAllocator {

    private int nextCkbEntryId;
    private int nextVariantId;
    private int nextGeneId;
    private int nextEvidenceId;
    private int nextTherapyId;
    private int nextDrugId;
    private int nextIndicationId;
    private int nextClinicalTrialId;
    private int nextLocationId;
    private int nextTreatmentApproachId;

    IdAllocator(@NotNull DSLContext context) {
        resetFromDatabase(context);
    }

    void resetFromDatabase(@NotNull DSLContext context) {
        nextCkbEntryId = nextId(context, Ckbentry.CKBENTRY, Ckbentry.CKBENTRY.ID);
        nextVariantId = nextId(context, Variant.VARIANT, Variant.VARIANT.ID);
        nextGeneId = nextId(context, Gene.GENE, Gene.GENE.ID);
        nextEvidenceId = nextId(context, Evidence.EVIDENCE, Evidence.EVIDENCE.ID);
        nextTherapyId = nextId(context, Therapy.THERAPY, Therapy.THERAPY.ID);
        nextDrugId = nextId(context, Drug.DRUG, Drug.DRUG.ID);
        nextIndicationId = nextId(context, Indication.INDICATION, Indication.INDICATION.ID);
        nextClinicalTrialId = nextId(context, Clinicaltrial.CLINICALTRIAL, Clinicaltrial.CLINICALTRIAL.ID);
        nextLocationId = nextId(context, Location.LOCATION, Location.LOCATION.ID);
        nextTreatmentApproachId = nextId(context, Treatmentapproach.TREATMENTAPPROACH, Treatmentapproach.TREATMENTAPPROACH.ID);
    }

    int nextCkbEntryId() {
        return nextCkbEntryId++;
    }

    int nextVariantId() {
        return nextVariantId++;
    }

    int nextGeneId() {
        return nextGeneId++;
    }

    int nextEvidenceId() {
        return nextEvidenceId++;
    }

    int nextTherapyId() {
        return nextTherapyId++;
    }

    int nextDrugId() {
        return nextDrugId++;
    }

    int nextIndicationId() {
        return nextIndicationId++;
    }

    int nextClinicalTrialId() {
        return nextClinicalTrialId++;
    }

    int nextLocationId() {
        return nextLocationId++;
    }

    int nextTreatmentApproachId() {
        return nextTreatmentApproachId++;
    }

    private static int nextId(@NotNull DSLContext context, @NotNull Table<?> table, @NotNull TableField<?, Integer> idField) {
        Integer maxId = context.select(DSL.max(idField)).from(table).fetchOne(0, Integer.class);
        return (maxId == null ? 1 : maxId + 1);
    }
}