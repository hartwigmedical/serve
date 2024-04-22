package com.hartwig.serve.sources.ckb;

import com.hartwig.serve.ckb.CkbEntryReader;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.sources.ckb.curation.CkbCurator;
import com.hartwig.serve.sources.ckb.filter.CkbBlacklistMolecularProfile;
import com.hartwig.serve.sources.ckb.filter.CkbBlacklistMolecularProfileEntry;
import com.hartwig.serve.sources.ckb.filter.CkbBlacklistMolecularProfileFile;
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
    public static List<CkbEntry> readAndCurate(@NotNull String ckbDir, @NotNull String ckbFilterTsv) throws IOException {
        LOGGER.info("Reading CKB database from {}", ckbDir);
        List<CkbEntry> ckbEntries = CkbEntryReader.read(ckbDir);
        LOGGER.info(" Read {} entries", ckbEntries.size());

        LOGGER.info("Reading CBK filter entries from {}", ckbFilterTsv);
        List<CkbBlacklistMolecularProfileEntry> ckbFilterEntries = CkbBlacklistMolecularProfileFile.read(ckbFilterTsv);
        LOGGER.info(" Read {} filter entries", ckbFilterEntries.size());

        return filter(curate(ckbEntries), ckbFilterEntries);
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
    private static List<CkbEntry> filter(@NotNull List<CkbEntry> entries, @NotNull List<CkbBlacklistMolecularProfileEntry> ckbFilterEntries) {
        CkbBlacklistMolecularProfile filter = new CkbBlacklistMolecularProfile(ckbFilterEntries);

        LOGGER.info("Filtering {} CKB entries", entries.size());
        List<CkbEntry> filteredEntries = filter.run(entries);
        LOGGER.info(" Finished CKB filtering. {} entries remaining, {} entries have been removed",
                filteredEntries.size(),
                entries.size() - filteredEntries.size());

        filter.reportUnusedFilterEntries();

        return filteredEntries;
    }
}