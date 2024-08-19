package com.hartwig.serve.sources.ckb;

import java.io.IOException;
import java.util.List;

import com.hartwig.serve.ckb.CkbEntryReader;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.sources.ckb.blacklist.CkbBlacklistMolecularProfileEntry;
import com.hartwig.serve.sources.ckb.blacklist.CkbBlacklistMolecularProfileFile;
import com.hartwig.serve.sources.ckb.blacklist.CkbMolecularProfileBlacklistModel;
import com.hartwig.serve.sources.ckb.curation.CkbCurator;
import com.hartwig.serve.sources.ckb.curation.CkbFacilityCurationManualEntry;
import com.hartwig.serve.sources.ckb.curation.CkbFacilityCurationManualFile;
import com.hartwig.serve.sources.ckb.curation.CkbFacilityCurationNameEntry;
import com.hartwig.serve.sources.ckb.curation.CkbFacilityCurationNameFile;
import com.hartwig.serve.sources.ckb.curation.CkbFacilityCurationZipEntry;
import com.hartwig.serve.sources.ckb.curation.CkbFacilityCurationZipFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class CkbReader {

    private static final Logger LOGGER = LogManager.getLogger(CkbReader.class);

    private CkbReader() {
    }

    @NotNull
    public static List<CkbEntry> readAndCurate(@NotNull String ckbDir, @NotNull String ckbBlacklistMolecularProfileTsv,
            @NotNull String ckbFacilityCurationNameTsv, @NotNull String ckbFacilityCurationZipTsv,
            @NotNull String ckbFacilityCurationManualTsv) throws IOException {
        LOGGER.info("Reading CKB database from {}", ckbDir);
        List<CkbEntry> ckbEntries = CkbEntryReader.read(ckbDir);
        LOGGER.info(" Read {} entries", ckbEntries.size());

        LOGGER.info("Reading CBK blacklist molecular profile entries from {}", ckbBlacklistMolecularProfileTsv);
        List<CkbBlacklistMolecularProfileEntry> ckbBlacklistMolecularProfileEntries =
                CkbBlacklistMolecularProfileFile.read(ckbBlacklistMolecularProfileTsv);
        LOGGER.info(" Read {} blacklist molecular profile entries", ckbBlacklistMolecularProfileEntries.size());

        LOGGER.info("Reading facility name curations from {}", ckbFacilityCurationNameTsv);
        List<CkbFacilityCurationNameEntry> facilityNameCurations = CkbFacilityCurationNameFile.read(ckbFacilityCurationNameTsv);
        LOGGER.info(" Read {} facility name curations to include", facilityNameCurations.size());

        LOGGER.info("Reading facility zip curations from {}", ckbFacilityCurationZipTsv);
        List<CkbFacilityCurationZipEntry> facilityZipCurations = CkbFacilityCurationZipFile.read(ckbFacilityCurationZipTsv);
        LOGGER.info(" Read {} facility zip curations to include", facilityZipCurations.size());

        LOGGER.info("Reading facility manual curations from {}", ckbFacilityCurationManualTsv);
        List<CkbFacilityCurationManualEntry> facilityManualCurations = CkbFacilityCurationManualFile.read(ckbFacilityCurationManualTsv);
        LOGGER.info(" Read {} facility filter curations to include", facilityManualCurations.size());

        return removeBlacklistedEntries(curate(ckbEntries, facilityNameCurations, facilityZipCurations, facilityManualCurations),
                ckbBlacklistMolecularProfileEntries);
    }

    @NotNull
    private static List<CkbEntry> curate(@NotNull List<CkbEntry> ckbEntries,
            @NotNull List<CkbFacilityCurationNameEntry> facilityNameCurations,
            @NotNull List<CkbFacilityCurationZipEntry> facilityZipCurations,
            @NotNull List<CkbFacilityCurationManualEntry> facilityManualCurations) {
        CkbCurator curator = new CkbCurator(facilityNameCurations, facilityZipCurations, facilityManualCurations);

        LOGGER.info("Curating {} CKB entries", ckbEntries.size());
        List<CkbEntry> curatedEntries = curator.run(ckbEntries);

        curator.reportUnusedCurationEntries();
        curator.reportUnusedFacilityCurationManualEntries();

        return curatedEntries;
    }

    @NotNull
    private static List<CkbEntry> removeBlacklistedEntries(@NotNull List<CkbEntry> entries,
            @NotNull List<CkbBlacklistMolecularProfileEntry> ckbBlacklistEntries) {
        CkbMolecularProfileBlacklistModel blacklist = new CkbMolecularProfileBlacklistModel(ckbBlacklistEntries);

        LOGGER.info("Blacklisting {} CKB entries", entries.size());
        List<CkbEntry> whitelistedEntries = blacklist.run(entries);
        LOGGER.info(" Finished CKB blacklisting. {} entries remaining, {} entries have been removed",
                whitelistedEntries.size(),
                entries.size() - whitelistedEntries.size());

        blacklist.reportUnusedBlacklistEntries();

        return whitelistedEntries;
    }
}