package com.hartwig.serve.dao;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.List;

import com.hartwig.serve.datamodel.ActionableEvents;
import com.hartwig.serve.datamodel.ActionableEventsLoader;
import com.hartwig.serve.datamodel.genome.refgenome.RefGenomeVersion;
import com.hartwig.serve.extraction.KnownEvents;
import com.hartwig.serve.extraction.KnownEventsLoader;
import com.hartwig.serve.extraction.events.EventInterpretation;
import com.hartwig.serve.extraction.events.EventInterpretationFile;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class LoadServeDatabase {

    private static final Logger LOGGER = LogManager.getLogger(LoadServeDatabase.class);

    private static final String REF_GENOME_VERSION = "ref_genome_version";

    private static final String SERVE_ACTIONABILITY_DIRECTORY = "serve_actionability_dir";

    public static void main(@NotNull String[] args) throws ParseException, SQLException, IOException {
        Options options = createOptions();
        CommandLine cmd = new DefaultParser().parse(options, args);

        String serveActionabilityDir = nonOptionalDir(cmd, SERVE_ACTIONABILITY_DIRECTORY);
        RefGenomeVersion refGenomeVersion = resolveRefGenomeVersion(nonOptionalValue(cmd, REF_GENOME_VERSION));
        ActionableEvents actionableEvents = ActionableEventsLoader.readFromDir(serveActionabilityDir, refGenomeVersion);
        KnownEvents knownEvents = KnownEventsLoader.readFromDir(serveActionabilityDir, refGenomeVersion);
        List<EventInterpretation> eventInterpretation =
                EventInterpretationFile.read(EventInterpretationFile.eventInterpretationTsv(serveActionabilityDir));

        LOGGER.info(" Loaded {} event interpretations from {}",
                eventInterpretation.size(),
                EventInterpretationFile.eventInterpretationTsv(serveActionabilityDir));

        ServeDatabaseAccess dbWriter = ServeDatabaseAccess.databaseAccess(cmd);

        dbWriter.writeServeDAO(actionableEvents, knownEvents, eventInterpretation);
        LOGGER.info("Written SERVE output to database");
    }

    @NotNull
    private static Options createOptions() {
        Options options = new Options();
        options.addOption(SERVE_ACTIONABILITY_DIRECTORY, true, "Path towards the SERVE actionability directory.");
        options.addOption(REF_GENOME_VERSION, true, "Ref genome version to use (either '37' or '38')");

        ServeDatabaseAccess.addDatabaseCmdLineArgs(options);
        return options;
    }

    @NotNull
    static String nonOptionalDir(@NotNull CommandLine cmd, @NotNull String param) throws ParseException {
        String value = nonOptionalValue(cmd, param);

        if (!pathExists(value) || !pathIsDirectory(value)) {
            throw new ParseException("Parameter '" + param + "' must be an existing directory: " + value);
        }

        return value;
    }

    @NotNull
    static String nonOptionalValue(@NotNull CommandLine cmd, @NotNull String param) throws ParseException {
        String value = cmd.getOptionValue(param);
        if (value == null) {
            throw new ParseException("Parameter must be provided: " + param);
        }

        return value;
    }

    static boolean pathExists(@NotNull String path) {
        return Files.exists(new File(path).toPath());
    }

    static boolean pathIsDirectory(@NotNull String path) {
        return Files.isDirectory(new File(path).toPath());
    }

    @NotNull
    static RefGenomeVersion resolveRefGenomeVersion(@NotNull String version) {
        if (version.equals(RefGenomeVersion.V37.toString()) || version.equals("37") || version.equals("HG37")) {
            return RefGenomeVersion.V37;
        } else if (version.equals(RefGenomeVersion.V38.toString()) || version.equals("38") || version.equals("HG38")) {
            return RefGenomeVersion.V38;
        }

        throw new IllegalArgumentException("Cannot resolve ref genome version: " + version);
    }
}