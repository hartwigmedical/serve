package com.hartwig.serve;

import java.io.IOException;
import java.util.Map;

import com.hartwig.serve.curation.DoidLookup;
import com.hartwig.serve.curation.DoidLookupFactory;
import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.extraction.ExtractionResult;
import com.hartwig.serve.extraction.ExtractionResultWriter;
import com.hartwig.serve.refgenome.RefGenomeManager;
import com.hartwig.serve.refgenome.RefGenomeManagerFactory;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;

public class ServeApplication {

    private static final Logger LOGGER = LogManager.getLogger(ServeApplication.class);

    private static final String VERSION = ServeApplication.class.getPackage().getImplementationVersion();

    public static void main(String[] args) throws IOException {
        LOGGER.info("Running SERVE v{}", VERSION);

        Options options = ServeConfig.createOptions();

        ServeConfig config = null;
        try {
            config = ServeConfig.createConfig(new DefaultParser().parse(options, args));
        } catch (ParseException exception) {
            LOGGER.warn(exception);
            new HelpFormatter().printHelp("SERVE", options);
            System.exit(1);
        }

        RefGenomeManager refGenomeManager = RefGenomeManagerFactory.createFromServeConfig(config);

        ServeAlgo algo = new ServeAlgo(refGenomeManager, buildDoidLookup(config.missingDoidsMappingTsv()));
        Map<RefGenome, ExtractionResult> resultMap = algo.run(config);

        for (Map.Entry<RefGenome, ExtractionResult> entry : resultMap.entrySet()) {
            RefGenome version = entry.getKey();
            IndexedFastaSequenceFile refSequence = refGenomeManager.refSequenceForRefGenome(version);
            new ExtractionResultWriter(config.outputDir(), version, refSequence).write(entry.getValue());
        }

        LOGGER.info("Complete!");
    }

    @NotNull
    private static DoidLookup buildDoidLookup(@NotNull String missingDoidsMappingTsv) throws IOException {
        LOGGER.info("Creating missing doid lookup mapping from {}", missingDoidsMappingTsv);
        return DoidLookupFactory.buildFromMappingTsv(missingDoidsMappingTsv);
    }
}
