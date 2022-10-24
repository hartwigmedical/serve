package com.hartwig.serve.transvar.tools;

import java.io.IOException;
import java.util.List;

import com.hartwig.serve.datamodel.common.Variant;
import com.hartwig.serve.datamodel.refgenome.RefGenomeVersion;
import com.hartwig.serve.refgenome.EnsemblDataCacheLoader;
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

        Transvar transvar37 = Transvar.withRefGenome(RefGenomeVersion.V37,
                config.refGenome37FastaFile(),
                EnsemblDataCacheLoader.load(config.ensemblDataDir37(), RefGenomeVersion.V37));

        extractAndPrintVariants(transvar37, config.gene37(), config.transcript37(), config.protein37());

        Transvar transvar38 = Transvar.withRefGenome(RefGenomeVersion.V38,
                config.refGenome38FastaFile(),
                EnsemblDataCacheLoader.load(config.ensemblDataDir38(), RefGenomeVersion.V38));

        extractAndPrintVariants(transvar38, config.gene38(), config.transcript38(), config.protein38());
    }

    private static void extractAndPrintVariants(@NotNull Transvar transvar, @NotNull String gene, @Nullable String specificTranscript,
            @NotNull String proteinAnnotation) {
        List<Variant> variants = transvar.resolve(gene, specificTranscript, proteinAnnotation);

        LOGGER.info("Printing variants for '{}:p.{}' on transcript {}", gene, proteinAnnotation, specificTranscript);
        for (Variant variant : variants) {
            LOGGER.info(" {}", variant);
        }
    }
}
