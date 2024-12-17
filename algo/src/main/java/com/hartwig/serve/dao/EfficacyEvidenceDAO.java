package com.hartwig.serve.dao;

import static com.hartwig.serve.database.Tables.EFFICACYEVIDENCE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Iterables;
import com.hartwig.serve.datamodel.efficacy.EfficacyEvidence;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;
import org.jooq.InsertValuesStep12;

@SuppressWarnings("ResultOfMethodCallIgnored")
class EfficacyEvidenceDAO {

    @NotNull
    private final DSLContext context;
    @NotNull
    private final MolecularCriteriumDAO molecularCriteriumDAO;

    public EfficacyEvidenceDAO(@NotNull final DSLContext context, @NotNull final MolecularCriteriumDAO molecularCriteriumDAO) {
        this.context = context;
        this.molecularCriteriumDAO = molecularCriteriumDAO;
    }

    public void deleteAll() {
        context.deleteFrom(EFFICACYEVIDENCE).execute();
    }

    public void write(@NotNull List<EfficacyEvidence> efficacyEvidences) {
        Map<EfficacyEvidence, Integer> molecularCriteriumIdPerEfficacyEvidence = new HashMap<>();
        for (EfficacyEvidence efficacyEvidence : efficacyEvidences) {
            molecularCriteriumIdPerEfficacyEvidence.put(efficacyEvidence,
                    molecularCriteriumDAO.write(efficacyEvidence.molecularCriterium()));
        }

        for (List<EfficacyEvidence> batch : Iterables.partition(efficacyEvidences, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep12 inserter = context.insertInto(EFFICACYEVIDENCE,
                    EFFICACYEVIDENCE.SOURCE,
                    EFFICACYEVIDENCE.TREATMENT,
                    EFFICACYEVIDENCE.TREATMENTAPPROACHESDRUGCLASS,
                    EFFICACYEVIDENCE.TREATMENTAPPROACHESTHERAPY,
                    EFFICACYEVIDENCE.INDICATION,
                    EFFICACYEVIDENCE.MOLECULARCRITERIUMID,
                    EFFICACYEVIDENCE.EFFICACYDESCRIPTION,
                    EFFICACYEVIDENCE.EVIDENCELEVEL,
                    EFFICACYEVIDENCE.EVIDENCELEVELDETAILS,
                    EFFICACYEVIDENCE.EVIDENCEDIRECTION,
                    EFFICACYEVIDENCE.EVIDENCEYEAR,
                    EFFICACYEVIDENCE.EVIDENCEURLS);
            batch.forEach(entry -> writeEfficacyEvidence(inserter, entry, molecularCriteriumIdPerEfficacyEvidence.get(entry)));
            inserter.execute();
        }
    }

    private void writeEfficacyEvidence(@NotNull InsertValuesStep12 inserter, @NotNull EfficacyEvidence efficacyEvidence,
            int molecularCriteriumId) {
        inserter.values(efficacyEvidence.source().name(),
                efficacyEvidence.treatment().name(),
                formatTreatmentApproachField(efficacyEvidence.treatment().treatmentApproachesDrugClass()),
                formatTreatmentApproachField(efficacyEvidence.treatment().treatmentApproachesTherapy()),
                DatabaseUtil.formatIndication(efficacyEvidence.indication()),
                molecularCriteriumId,
                efficacyEvidence.efficacyDescription(),
                efficacyEvidence.evidenceLevel().name(),
                efficacyEvidence.evidenceLevelDetails().name(),
                efficacyEvidence.evidenceDirection().name(),
                efficacyEvidence.evidenceYear(),
                DatabaseUtil.concat(efficacyEvidence.urls()));
    }

    @Nullable
    private static String formatTreatmentApproachField(@NotNull Set<String> fields) {
        return !fields.isEmpty() ? DatabaseUtil.concat(fields) : null;
    }
}
