package com.hartwig.serve.ckb;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.hartwig.serve.ckb.dao.CkbDAO;
import com.hartwig.serve.ckb.datamodel.CkbEntry;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class CkbImporterApplication {

    private static final Logger LOGGER = LogManager.getLogger(CkbImporterApplication.class);
    private static final String VERSION = CkbImporterApplication.class.getPackage().getImplementationVersion();
    private static final int ENTRIES_PER_TRANSACTION = 100;

    public static void main(String[] args) throws IOException, SQLException {
        LOGGER.info("Running CKB importer v{}", VERSION);

        Options options = CkbImporterConfig.createOptions();

        CkbImporterConfig config = null;
        try {
            config = CkbImporterConfig.createConfig(new DefaultParser().parse(options, args));
        } catch (ParseException exception) {
            LOGGER.warn(exception);
            new HelpFormatter().printHelp("CKB Importer", options);
            System.exit(1);
        }

        LOGGER.info("Reading CKB database from {}", config.ckbDir());
        List<CkbEntry> ckbEntries = CkbEntryReader.read(config.ckbDir());
        LOGGER.info(" Read {} entries", ckbEntries.size());

        updateCkbSqlDatabase(config, ckbEntries);

        LOGGER.info("Complete!");
    }

    private static void updateCkbSqlDatabase(@NotNull CkbImporterConfig config, @NotNull List<CkbEntry> ckbEntries) throws SQLException {
        if (config.skipDatabaseWriting()) {
            LOGGER.info("Skipping DB writing");
        } else {
            CkbDAO ckbDAO = CkbDAO.connectToCkbDAO(config.dbUser(), config.dbPass(), "jdbc:" + config.dbUrl(), config.batchSize());

            LOGGER.info("Deleting all data from CKB database");
            ckbDAO.deleteAll();

            LOGGER.info("Inserting {} CKB entries", ckbEntries.size());
            int current = 0;
            int report = (int) Math.round(ckbEntries.size() / 10D);
            int entriesInTransaction = 0;
            ckbDAO.setAutoCommit(false);
            try {
                for (CkbEntry entry : ckbEntries) {
                    ckbDAO.write(entry);
                    current++;
                    entriesInTransaction++;
                    if (current % report == 0) {
                        LOGGER.debug(" Inserted {} of {} CKB entries", current, ckbEntries.size());
                    }
                    if (entriesInTransaction >= ENTRIES_PER_TRANSACTION) {
                        ckbDAO.flushBatches();
                        ckbDAO.commit();
                        entriesInTransaction = 0;
                    }
                }
                ckbDAO.flushBatches();
                if (entriesInTransaction > 0) {
                    ckbDAO.commit();
                }
            } catch (Exception exception) {
                try {
                    ckbDAO.rollback();
                } catch (SQLException rollbackException) {
                    LOGGER.warn("Failed to rollback transaction after import failure", rollbackException);
                }
                throw exception;
            } finally {
                ckbDAO.setAutoCommit(true);
            }
        }
    }
}
