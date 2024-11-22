package com.hartwig.serve.sources.ckb;

import java.io.IOException;
import java.util.List;

import com.hartwig.serve.ckb.CkbEntryReader;
import com.hartwig.serve.ckb.datamodel.CkbEntry;
import com.hartwig.serve.sources.ckb.curation.CkbCurator;
import com.hartwig.serve.sources.ckb.curation.CkbFacilityCurationManualEntry;
import com.hartwig.serve.sources.ckb.curation.CkbFacilityCurationManualFile;
import com.hartwig.serve.sources.ckb.curation.CkbFacilityCurationNameEntry;
import com.hartwig.serve.sources.ckb.curation.CkbFacilityCurationNameFile;
import com.hartwig.serve.sources.ckb.curation.CkbFacilityCurationZipEntry;
import com.hartwig.serve.sources.ckb.curation.CkbFacilityCurationZipFile;
import com.hartwig.serve.sources.ckb.filter.CkbMolecularProfileFilterEntry;
import com.hartwig.serve.sources.ckb.filter.CkbMolecularProfileFilterFile;
import com.hartwig.serve.sources.ckb.filter.CkbMolecularProfileFilterModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class CkbReader {

    private static final Logger LOGGER = LogManager.getLogger(CkbReader.class);

    private CkbReader() {
    }

    @NotNull
    public static List<CkbEntry> readAndCurate(@NotNull String ckbDir, @NotNull String ckbMolecularProfileFilterTsv,
            @NotNull String ckbFacilityCurationNameTsv, @NotNull String ckbFacilityCurationZipTsv,
            @NotNull String ckbFacilityCurationManualTsv) throws IOException {
        LOGGER.info("Reading CKB database from {}", ckbDir);
        List<CkbEntry> ckbEntries = CkbEntryReader.read(ckbDir);
        LOGGER.info(" Read {} entries", ckbEntries.size());

        LOGGER.info("Reading CKB molecular profile filter entries from {}", ckbMolecularProfileFilterTsv);
        List<CkbMolecularProfileFilterEntry> ckbMolecularProfileFilterEntries =
                CkbMolecularProfileFilterFile.read(ckbMolecularProfileFilterTsv);
        LOGGER.info(" Read {} molecular profile filter entries", ckbMolecularProfileFilterEntries.size());

        LOGGER.info("Reading facility name curations from {}", ckbFacilityCurationNameTsv);
        List<CkbFacilityCurationNameEntry> facilityNameCurations = CkbFacilityCurationNameFile.read(ckbFacilityCurationNameTsv);
        LOGGER.info(" Read {} facility name curations to include", facilityNameCurations.size());

        LOGGER.info("Reading facility zip curations from {}", ckbFacilityCurationZipTsv);
        List<CkbFacilityCurationZipEntry> facilityZipCurations = CkbFacilityCurationZipFile.read(ckbFacilityCurationZipTsv);
        LOGGER.info(" Read {} facility zip curations to include", facilityZipCurations.size());

        LOGGER.info("Reading facility manual curations from {}", ckbFacilityCurationManualTsv);
        List<CkbFacilityCurationManualEntry> facilityManualCurations = CkbFacilityCurationManualFile.read(ckbFacilityCurationManualTsv);
        LOGGER.info(" Read {} facility manual curations to include", facilityManualCurations.size());

        return removeFilteredEntries(curate(ckbEntries, facilityNameCurations, facilityZipCurations, facilityManualCurations),
                ckbMolecularProfileFilterEntries);
    }

    @NotNull
    private static List<CkbEntry> curate(@NotNull List<CkbEntry> ckbEntries,
            @NotNull List<CkbFacilityCurationNameEntry> facilityNameCurations,
            @NotNull List<CkbFacilityCurationZipEntry> facilityZipCurations,
            @NotNull List<CkbFacilityCurationManualEntry> facilityManualCurations) {
        CkbCurator curator = new CkbCurator(facilityNameCurations, facilityZipCurations, facilityManualCurations);

        LOGGER.info("Curating {} CKB entries", ckbEntries.size());
        List<CkbEntry> curatedEntries = curator.run(ckbEntries);

        curator.reportUnusedVariantCurationEntries();
        curator.reportUnusedFacilityCurationManualEntries();

        return curatedEntries;
    }

    @NotNull
    private static List<CkbEntry> removeFilteredEntries(@NotNull List<CkbEntry> inputEntries,
            @NotNull List<CkbMolecularProfileFilterEntry> ckbFilterEntries) {
        CkbMolecularProfileFilterModel filterModel = new CkbMolecularProfileFilterModel(ckbFilterEntries);

        LOGGER.info("Filtering {} CKB entries", inputEntries.size());
        List<CkbEntry> cleanedEntries = filterModel.run(inputEntries);
        LOGGER.info(" Finished CKB filtering. {} entries remaining, {} entries have been removed",
                cleanedEntries.size(),
                inputEntries.size() - cleanedEntries.size());

        filterModel.reportUnusedFilterEntries();

        return cleanedEntries;
    }
}