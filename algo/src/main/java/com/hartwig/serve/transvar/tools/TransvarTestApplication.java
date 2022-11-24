package com.hartwig.serve.transvar.tools;

import java.io.IOException;
import java.util.List;

import com.hartwig.serve.common.ensemblcache.EnsemblDataLoader;
import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.extraction.hotspot.Hotspot;
import com.hartwig.serve.transvar.Transvar;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TransvarTestApplication {

    private static final Logger LOGGER = LogManager.getLogger(TransvarTestApplication.class);

    public static void main(String[] args) throws IOException {
        Configurator.setRootLevel(Level.DEBUG);

        Options options = TransvarTestAppConfig.createOptions();

        TransvarTestAppConfig config = null;
        try {
            config = TransvarTestAppConfig.createConfig(new DefaultParser().parse(options, args));
        } catch (ParseException exception) {
            LOGGER.warn(exception);
            new HelpFormatter().printHelp("TransvarTestApp", options);
            System.exit(1);
        }

        Transvar transvar37 = Transvar.withRefGenome(RefGenome.V37,
                config.refGenome37FastaFile(),
                EnsemblDataLoader.load(config.ensemblDataDir37(), RefGenome.V37));

        extractAndPrintVariants(transvar37, config.gene37(), config.transcript37(), config.protein37());

        Transvar transvar38 = Transvar.withRefGenome(RefGenome.V38,
                config.refGenome38FastaFile(),
                EnsemblDataLoader.load(config.ensemblDataDir38(), RefGenome.V38));

        extractAndPrintVariants(transvar38, config.gene38(), config.transcript38(), config.protein38());
    }

    private static void extractAndPrintVariants(@NotNull Transvar transvar, @NotNull String gene, @Nullable String specificTranscript,
            @NotNull String proteinAnnotation) {
        List<Hotspot> hotspots = transvar.resolve(gene, specificTranscript, proteinAnnotation);

        LOGGER.info("Printing hotspots for '{}:p.{}' on transcript {}", gene, proteinAnnotation, specificTranscript);
        for (Hotspot hotspot : hotspots) {
            LOGGER.info(" {}", hotspot);
        }
    }
}
