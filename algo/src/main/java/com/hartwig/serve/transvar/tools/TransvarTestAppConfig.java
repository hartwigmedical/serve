package com.hartwig.serve.transvar.tools;

import java.io.File;
import java.nio.file.Files;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public interface TransvarTestAppConfig {

    String ENSEMBL_DATA_DIR_37 = "ensembl_data_dir_37";
    String ENSEMBL_DATA_DIR_38 = "ensembl_data_dir_38";

    String REF_GENOME_37_FASTA_FILE = "ref_genome_37_fasta_file";
    String REF_GENOME_38_FASTA_FILE = "ref_genome_38_fasta_file";

    String GENE_37 = "gene_37";
    String GENE_38 = "gene_38";

    String PROTEIN_37 = "protein_37";
    String PROTEIN_38 = "protein_38";

    String TRANSCRIPT_37 = "transcript_37";
    String TRANSCRIPT_38 = "transcript_38";

    @NotNull
    static Options createOptions() {
        Options options = new Options();

        options.addOption(ENSEMBL_DATA_DIR_37, true, "Ensembl data file directory for ref genome V37");
        options.addOption(ENSEMBL_DATA_DIR_38, true, "Ensembl data file directory for ref genome V38");

        options.addOption(REF_GENOME_37_FASTA_FILE, true, "Path to the V37 ref genome fasta file");
        options.addOption(REF_GENOME_38_FASTA_FILE, true, "Path to the V38 ref genome fasta file");

        options.addOption(GENE_37, true, "String gene name of V37");
        options.addOption(GENE_38, true, "String gene name of V38");

        options.addOption(PROTEIN_37, true, "String protein annotation of V37");
        options.addOption(PROTEIN_38, true, "String protein annotation of V38");

        options.addOption(TRANSCRIPT_37, true, "String transcript gene of V37");
        options.addOption(TRANSCRIPT_38, true, "String transcript gene of V38");

        return options;
    }

    @NotNull
    String ensemblDataDir37();

    @NotNull
    String ensemblDataDir38();

    @NotNull
    String refGenome37FastaFile();

    @NotNull
    String refGenome38FastaFile();

    @NotNull
    String gene37();

    @NotNull
    String gene38();

    @NotNull
    String protein37();

    @NotNull
    String protein38();

    @Nullable
    String transcript37();

    @Nullable
    String transcript38();

    @NotNull
    static TransvarTestAppConfig createConfig(@NotNull CommandLine cmd) throws ParseException {
        return ImmutableTransvarTestAppConfig.builder()
                .ensemblDataDir37(nonOptionalDir(cmd, ENSEMBL_DATA_DIR_37))
                .ensemblDataDir38(nonOptionalDir(cmd, ENSEMBL_DATA_DIR_38))
                .refGenome37FastaFile(nonOptionalFile(cmd, REF_GENOME_37_FASTA_FILE))
                .refGenome38FastaFile(nonOptionalFile(cmd, REF_GENOME_38_FASTA_FILE))
                .gene37(nonOptionalValue(cmd, GENE_37))
                .gene38(nonOptionalValue(cmd, GENE_38))
                .protein37(nonOptionalValue(cmd, PROTEIN_37))
                .protein38(nonOptionalValue(cmd, PROTEIN_38))
                .transcript37(optionalValue(cmd, TRANSCRIPT_37))
                .transcript38(optionalValue(cmd, TRANSCRIPT_38))
                .build();
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
    static String nonOptionalFile(@NotNull CommandLine cmd, @NotNull String param) throws ParseException {
        String value = nonOptionalValue(cmd, param);

        if (!pathExists(value)) {
            throw new ParseException("Parameter '" + param + "' must be an existing file: " + value);
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

    @Nullable
    static String optionalValue(@NotNull CommandLine cmd, @NotNull String param) {
        return cmd.getOptionValue(param);
    }

    static boolean pathExists(@NotNull String path) {
        return Files.exists(new File(path).toPath());
    }

    static boolean pathIsDirectory(@NotNull String path) {
        return Files.isDirectory(new File(path).toPath());
    }
}
