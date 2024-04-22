package com.hartwig.serve.sources.ckb;

import com.hartwig.serve.ckb.CkbEntryReader;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.sources.ckb.curation.CkbCurator;
import com.hartwig.serve.sources.ckb.blacklist.CkbBlacklistMolecularProfile;
import com.hartwig.serve.sources.ckb.blacklist.CkbBlacklistMolecularProfileEntry;
import com.hartwig.serve.sources.ckb.blacklist.CkbBlacklistMolecularProfileFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public final class CkbReader {

    private static final Logger LOGGER = LogManager.getLogger(CkbReader.class);

    private CkbReader() {
    }

    @NotNull
    public static List<CkbEntry> readAndCurate(@NotNull String ckbDir, @NotNull String ckbBlacklistMolecularProfileTsv) throws IOException {
        LOGGER.info("Reading CKB database from {}", ckbDir);
        List<CkbEntry> ckbEntries = CkbEntryReader.read(ckbDir);
        LOGGER.info(" Read {} entries", ckbEntries.size());

        LOGGER.info("Reading CBK blacklist molecular profile entries from {}", ckbBlacklistMolecularProfileTsv);
        List<CkbBlacklistMolecularProfileEntry> ckbBlacklistMolecularProfileEntries = CkbBlacklistMolecularProfileFile.read(ckbBlacklistMolecularProfileTsv);
        LOGGER.info(" Read {} blacklist molecular profile entries entries", ckbBlacklistMolecularProfileEntries.size());

        return blacklist(curate(ckbEntries), ckbBlacklistMolecularProfileEntries);
    }

    @NotNull
    private static List<CkbEntry> curate(@NotNull List<CkbEntry> ckbEntries) {
        CkbCurator curator = new CkbCurator();

        LOGGER.info("Curating {} CKB entries", ckbEntries.size());
        List<CkbEntry> curatedEntries = curator.run(ckbEntries);

        curator.reportUnusedCurationEntries();

        return curatedEntries;
    }

    @NotNull
    private static List<CkbEntry> blacklist(@NotNull List<CkbEntry> entries, @NotNull List<CkbBlacklistMolecularProfileEntry> ckbFilterEntries) {
        CkbBlacklistMolecularProfile blacklist = new CkbBlacklistMolecularProfile(ckbFilterEntries);

        LOGGER.info("Blacklisting {} CKB entries", entries.size());
        List<CkbEntry> blacklistedEntries = blacklist.run(entries);
        LOGGER.info(" Finished CKB blacklisting. {} entries remaining, {} entries have been removed",
                blacklistedEntries.size(),
                entries.size() - blacklistedEntries.size());

        blacklist.reportUnusedBlacklistEntries();

        return blacklistedEntries;
    }
}