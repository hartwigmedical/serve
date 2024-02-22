package com.hartwig.serve.sources.ckb;

import com.hartwig.serve.ckb.CkbEntryReader;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.sources.ckb.blacklist.*;
import com.hartwig.serve.sources.ckb.curation.CkbCurator;
import com.hartwig.serve.sources.ckb.filter.CkbFilter;
import com.hartwig.serve.sources.ckb.filter.CkbFilterEntry;
import com.hartwig.serve.sources.ckb.filter.CkbFilterFile;
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
        List<CkbFilterEntry> ckbFilterEntries = CkbFilterFile.read(ckbFilterTsv);
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
    private static List<CkbEntry> filter(@NotNull List<CkbEntry> entries, @NotNull List<CkbFilterEntry> ckbFilterEntries) {
        CkbFilter filter = new CkbFilter(ckbFilterEntries);

        LOGGER.info("Filtering {} CKB entries", entries.size());
        List<CkbEntry> filteredEntries = filter.run(entries);
        LOGGER.info(" Finished CKB filtering. {} entries remaining, {} entries have been removed",
                filteredEntries.size(),
                entries.size() - filteredEntries.size());

        filter.reportUnusedFilterEntries();

        return filteredEntries;
    }

    @NotNull
    public static List<CkbEntry> blacklistEvidence(@NotNull List<CkbEntry> entries,
                                                @NotNull String ckbBlacklistEvidenceTsv) throws IOException{
        LOGGER.info("Reading CBK blacklist evidence entries from {}", ckbBlacklistEvidenceTsv);
        List<CkbBlacklistEvidenceEntry> ckbBlacklistEvidenceEntriesEntries = CkbBlacklistEvidenceFile.read(ckbBlacklistEvidenceTsv);
        LOGGER.info(" Read {} filter entries", ckbBlacklistEvidenceEntriesEntries.size());

        CkbBlacklistEvidence blacklistEvidence = new CkbBlacklistEvidence(ckbBlacklistEvidenceEntriesEntries);

        LOGGER.info("Blacklisting {} CKB evidence entries", entries.size());
        List<CkbEntry> filteredEvidenceEntries = blacklistEvidence.run(entries);
        LOGGER.info(" Finished CKB filtering studies. {} entries remaining, {} entries have been removed",
                filteredEvidenceEntries.size(),
                entries.size() - filteredEvidenceEntries.size());

        blacklistEvidence.reportUnusedBlacklistEntries();

        return filteredEvidenceEntries;
    }
}
