package com.hartwig.serve;

import java.io.IOException;

import com.hartwig.serve.common.commandline.CliAndPropertyParser;
import com.hartwig.serve.curation.DoidLookup;
import com.hartwig.serve.curation.DoidLookupFactory;
import com.hartwig.serve.extraction.ExtractionResultWriter;
import com.hartwig.serve.refgenome.RefGenomeManager;
import com.hartwig.serve.refgenome.RefGenomeManagerFactory;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class ServeApplication {

    private static final Logger LOGGER = LogManager.getLogger(ServeApplication.class);

    private static final String VERSION = ServeApplication.class.getPackage().getImplementationVersion();

    public static void main(String[] args) throws IOException {
        LOGGER.info("Running SERVE v{}", VERSION);

        Options options = ServeConfig.createOptions();

        ServeConfig config = null;
        try {
            config = ServeConfig.createConfig(new CliAndPropertyParser().parse(options, args));
        } catch (ParseException exception) {
            LOGGER.warn(exception);
            new HelpFormatter().printHelp("SERVE", options);
            System.exit(1);
        }

        RefGenomeManager refGenomeManager = RefGenomeManagerFactory.createFromServeConfig(config);

        ServeAlgo algo = new ServeAlgo(refGenomeManager, buildDoidLookup(config.missingDoidsMappingTsv()));

        ExtractionResultWriter writer = new ExtractionResultWriter(VERSION, refGenomeManager, config.outputDir());
        writer.write(algo.run(config));

        LOGGER.info("Complete!");
    }

    @NotNull
    private static DoidLookup buildDoidLookup(@NotNull String missingDoidsMappingTsv) throws IOException {
        LOGGER.info("Creating missing doid lookup mapping from {}", missingDoidsMappingTsv);
        return DoidLookupFactory.buildFromMappingTsv(missingDoidsMappingTsv);
    }
}
