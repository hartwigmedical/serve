package com.hartwig.serve.sources.vicc;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.hartwig.serve.sources.vicc.curation.FeatureCurator;
import com.hartwig.serve.sources.vicc.filter.ViccFilter;
import com.hartwig.serve.vicc.datamodel.ViccEntry;
import com.hartwig.serve.vicc.datamodel.ViccSource;
import com.hartwig.serve.vicc.reader.ViccJsonReader;
import com.hartwig.serve.vicc.selection.ImmutableViccQuerySelection;
import com.hartwig.serve.vicc.selection.ViccQuerySelection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ViccReader {

    private static final Logger LOGGER = LogManager.getLogger(ViccReader.class);

    private ViccReader() {
    }

    @NotNull
    public static List<ViccEntry> readAndCurateRelevantEntries(@NotNull String viccJson, @NotNull Set<ViccSource> sourcesToFilterOn,
            @Nullable Integer maxEntries) throws IOException {
        ViccQuerySelection querySelection =
                ImmutableViccQuerySelection.builder().sourcesToFilterOn(sourcesToFilterOn).maxEntriesToInclude(maxEntries).build();

        LOGGER.info("Reading VICC json from '{}' with sources '{}'", viccJson, querySelection.sourcesToFilterOn());
        List<ViccEntry> entries = ViccJsonReader.buildProductionReader().readSelection(viccJson, querySelection);
        LOGGER.info(" Read {} entries", entries.size());

        return filter(curate(entries));
    }

    @NotNull
    private static List<ViccEntry> curate(@NotNull List<ViccEntry> entries) {
        FeatureCurator curator = new FeatureCurator();

        LOGGER.info("Curating {} VICC entries", entries.size());
        List<ViccEntry> curatedEntries = curator.run(entries);
        LOGGER.info(" Finished VICC curation. {} entries remaining, {} entries have been removed",
                curatedEntries.size(),
                entries.size() - curatedEntries.size());

        curator.reportUnusedCurationKeys();

        return curatedEntries;
    }

    @NotNull
    private static List<ViccEntry> filter(@NotNull List<ViccEntry> entries) {
        ViccFilter filter = new ViccFilter();

        LOGGER.info("Filtering {} VICC entries", entries.size());
        List<ViccEntry> filteredEntries = filter.run(entries);
        LOGGER.info(" Finished VICC filtering. {} entries remaining, {} entries have been removed",
                filteredEntries.size(),
                entries.size() - filteredEntries.size());

        filter.reportUnusedFilterEntries();

        return filteredEntries;
    }
}
