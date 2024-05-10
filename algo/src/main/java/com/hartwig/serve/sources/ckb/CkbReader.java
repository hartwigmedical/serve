package com.hartwig.serve.sources.ckb;

import java.io.IOException;
import java.util.List;

import com.hartwig.serve.ckb.CkbEntryReader;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.sources.ckb.blacklist.CkbBlacklistMolecularProfileEntry;
import com.hartwig.serve.sources.ckb.blacklist.CkbBlacklistMolecularProfileFile;
import com.hartwig.serve.sources.ckb.blacklist.CkbMolecularProfileBlacklistModel;
import com.hartwig.serve.sources.ckb.curation.CkbCurator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class CkbReader {

    private static final Logger LOGGER = LogManager.getLogger(CkbReader.class);

    private CkbReader() {
    }

    @NotNull
    public static List<CkbEntry> readAndCurate(@NotNull String ckbDir, @NotNull String ckbBlacklistMolecularProfileTsv) throws IOException {
        LOGGER.info("Reading CKB database from {}", ckbDir);
        List<CkbEntry> blacklistEntries = CkbEntryReader.read(ckbDir);
        LOGGER.info(" Read {} entries", blacklistEntries.size());

        LOGGER.info("Reading CBK blacklist molecular profile entries from {}", ckbBlacklistMolecularProfileTsv);
        List<CkbBlacklistMolecularProfileEntry> ckbBlacklistMolecularProfileEntries =
                CkbBlacklistMolecularProfileFile.read(ckbBlacklistMolecularProfileTsv);
        LOGGER.info(" Read {} blacklist molecular profile entries entries", ckbBlacklistMolecularProfileEntries.size());

        return whitelist(curate(blacklistEntries), ckbBlacklistMolecularProfileEntries);
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
    private static List<CkbEntry> whitelist(@NotNull List<CkbEntry> entries,
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