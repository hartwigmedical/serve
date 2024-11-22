package com.hartwig.serve.dao;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.List;

import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.datamodel.ServeDatabase;
import com.hartwig.serve.datamodel.ServeRecord;
import com.hartwig.serve.datamodel.serialization.ServeJson;
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
        RefGenome refGenome = resolveRefGenomeVersion(nonOptionalValue(cmd, REF_GENOME_VERSION));

        ServeRecord serveRecord = loadServeRecord(serveActionabilityDir, refGenome);
        List<EventInterpretation> eventInterpretations = loadEventInterpretations(serveActionabilityDir);

        ServeDatabaseAccess dbWriter = ServeDatabaseAccess.databaseAccess(cmd);

        dbWriter.writeServeRecord(serveRecord, eventInterpretations);
        LOGGER.info("Written SERVE output to database");
    }

    @NotNull
    private static ServeRecord loadServeRecord(@NotNull String serveActionabilityDir, @NotNull RefGenome refGenome) throws IOException {
        String serveJsonFile = ServeJson.jsonFilePath(serveActionabilityDir);
        LOGGER.info("Loading SERVE from {} for ref genome {}", serveJsonFile, refGenome);
        ServeDatabase serveDatabase = ServeJson.read(serveJsonFile);
        ServeRecord serveRecord = serveDatabase.records().get(refGenome);

        if (serveRecord == null) {
            throw new IllegalStateException("Could not find SERVE record for ref genome version '" + refGenome + "'");
        }

        LOGGER.info(" Loaded {} known hotspots", serveRecord.knownEvents().hotspots().size());
        LOGGER.info(" Loaded {} known codons", serveRecord.knownEvents().codons().size());
        LOGGER.info(" Loaded {} known exons", serveRecord.knownEvents().exons().size());
        LOGGER.info(" Loaded {} known genes", serveRecord.knownEvents().genes().size());
        LOGGER.info(" Loaded {} known copy numbers", serveRecord.knownEvents().copyNumbers().size());
        LOGGER.info(" Loaded {} known fusions", serveRecord.knownEvents().fusions().size());
        LOGGER.info(" Loaded {} efficacy evidences", serveRecord.evidences().size());
        LOGGER.info(" Loaded {} actionable trials", serveRecord.trials().size());

        return serveRecord;
    }

    @NotNull
    private static List<EventInterpretation> loadEventInterpretations(@NotNull String serveActionabilityDir) throws IOException {
        String eventInterpretationTsv = EventInterpretationFile.eventInterpretationTsv(serveActionabilityDir);

        LOGGER.info("Loading event interpretation from {}", eventInterpretationTsv);
        List<EventInterpretation> eventInterpretations = EventInterpretationFile.read(eventInterpretationTsv);
        LOGGER.info(" Loaded {} event interpretations", eventInterpretations.size());

        return eventInterpretations;
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
    private static String nonOptionalDir(@NotNull CommandLine cmd, @NotNull String param) throws ParseException {
        String value = nonOptionalValue(cmd, param);

        if (!pathExists(value) || !pathIsDirectory(value)) {
            throw new ParseException("Parameter '" + param + "' must be an existing directory: " + value);
        }

        return value;
    }

    @NotNull
    private static String nonOptionalValue(@NotNull CommandLine cmd, @NotNull String param) throws ParseException {
        String value = cmd.getOptionValue(param);
        if (value == null) {
            throw new ParseException("Parameter must be provided: " + param);
        }

        return value;
    }

    private static boolean pathExists(@NotNull String path) {
        return Files.exists(new File(path).toPath());
    }

    static boolean pathIsDirectory(@NotNull String path) {
        return Files.isDirectory(new File(path).toPath());
    }

    @NotNull
    private static RefGenome resolveRefGenomeVersion(@NotNull String version) {
        if (version.equals(RefGenome.V37.toString()) || version.equals("37")) {
            return RefGenome.V37;
        } else if (version.equals(RefGenome.V38.toString()) || version.equals("38")) {
            return RefGenome.V38;
        }

        throw new IllegalArgumentException("Cannot resolve ref genome version: " + version);
    }
}