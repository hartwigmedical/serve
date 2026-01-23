package com.hartwig.serve.ckb.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.hartwig.serve.ckb.database.tables.Ckbentry;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.ckb.datamodel.clinicaltrial.ClinicalTrial;
import com.hartwig.serve.ckb.datamodel.evidence.Evidence;
import com.hartwig.serve.ckb.datamodel.variant.Variant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.MappedSchema;
import org.jooq.conf.RenderMapping;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

public final class CkbDAO {

    private static final Logger LOGGER = LogManager.getLogger(CkbDAO.class);

    private static final String DEV_CATALOG = "ckb_test";

    @NotNull
    private final DSLContext context;
    @NotNull
    private final Connection connection;
    @NotNull
    private final BatchInserter batchInserter;
    @NotNull
    private final TherapyDAO therapyDAO;
    @NotNull
    private final IndicationDAO indicationDAO;
    @NotNull
    private final VariantDAO variantDAO;
    @NotNull
    private final EvidenceDAO evidenceDAO;
    @NotNull
    private final TreatmentApproachDAO treatmentApproachDAO;
    @NotNull
    private final ClinicalTrialDAO clinicalTrialDAO;

    @NotNull
    public static CkbDAO connectToCkbDAO(@NotNull String userName, @NotNull String password, @NotNull String url, int batchSize)
            throws SQLException {
        System.setProperty("org.jooq.no-logo", "true");
        System.setProperty("org.jooq.no-tips", "true");

        Connection conn = DriverManager.getConnection(url, userName, password);
        String catalog = conn.getCatalog();
        LOGGER.info("Connecting to database '{}'", catalog);

        DSLContext context = DSL.using(conn, SQLDialect.MYSQL, settings(catalog));
        return new CkbDAO(conn, context, new BatchInserter(context, batchSize));
    }

    @Nullable
    private static Settings settings(@NotNull String catalog) {
        if (catalog.equals(DEV_CATALOG)) {
            return null;
        }

        return new Settings().withRenderMapping(new RenderMapping().withSchemata(new MappedSchema().withInput(DEV_CATALOG)
                .withOutput(catalog)));
    }

    private CkbDAO(@NotNull final Connection connection, @NotNull final DSLContext context, @NotNull final BatchInserter batchInserter) {
        this.context = context;
        this.connection = connection;
        this.batchInserter = batchInserter;
        this.therapyDAO = new TherapyDAO(context, batchInserter);
        this.indicationDAO = new IndicationDAO(context, batchInserter);
        this.variantDAO = new VariantDAO(context, batchInserter);
        this.treatmentApproachDAO = new TreatmentApproachDAO(context, batchInserter);
        this.evidenceDAO = new EvidenceDAO(context, therapyDAO, indicationDAO, treatmentApproachDAO, batchInserter);
        this.clinicalTrialDAO = new ClinicalTrialDAO(context, therapyDAO, indicationDAO, batchInserter);
    }

    public void deleteAll() {
        // Note that deletions should go from branch to root
        variantDAO.deleteAll();

        // The evidence and trial DAOs need to emptied first before therapy and indication, to avoid key violations.
        evidenceDAO.deleteAll();
        clinicalTrialDAO.deleteAll();

        therapyDAO.deleteAll();
        indicationDAO.deleteAll();
        treatmentApproachDAO.deleteAll();

        context.deleteFrom(Ckbentry.CKBENTRY).execute();
    }

    public void write(@NotNull CkbEntry ckbEntry) {
        int id = context.insertInto(Ckbentry.CKBENTRY,
                        Ckbentry.CKBENTRY.CKBPROFILEID,
                        Ckbentry.CKBENTRY.CREATEDATE,
                        Ckbentry.CKBENTRY.UPDATEDATE,
                        Ckbentry.CKBENTRY.PROFILENAME)
                .values(ckbEntry.profileId(), ckbEntry.createDate(), ckbEntry.updateDate(), ckbEntry.profileName())
                .returning(Ckbentry.CKBENTRY.ID)
                .fetchOne()
                .getValue(Ckbentry.CKBENTRY.ID);

        for (Variant variant : ckbEntry.variants()) {
            variantDAO.write(variant, id);
        }

        for (Evidence evidence : ckbEntry.evidences()) {
            evidenceDAO.write(evidence, id);
        }

        for (ClinicalTrial clinicalTrial : ckbEntry.clinicalTrials()) {
            clinicalTrialDAO.write(clinicalTrial, id);
        }
    }

    public void flushBatches() {
        batchInserter.flush();
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        connection.setAutoCommit(autoCommit);
    }

    public void commit() throws SQLException {
        connection.commit();
    }

    public void rollback() throws SQLException {
        connection.rollback();
    }
}
