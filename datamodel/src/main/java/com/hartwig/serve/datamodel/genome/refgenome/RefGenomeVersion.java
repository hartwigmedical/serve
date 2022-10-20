package com.hartwig.serve.datamodel.genome.refgenome;

import org.jetbrains.annotations.NotNull;

public enum RefGenomeVersion {
    V37("37", true),
    V38("38", false);

    @NotNull
    private final String identifier;
    private final boolean is37;

    private static final String GZIP_EXTENSION = ".gz";

    @NotNull
    public static RefGenomeVersion from(@NotNull final String version) {
        if (version.equals(V37.toString()) || version.equals("37") || version.equals("HG37")) {
            return V37;
        } else if (version.equals(V38.toString()) || version.equals("38") || version.equals("HG38")) {
            return V38;
        }

        throw new IllegalArgumentException("Cannot resolve ref genome version: " + version);
    }

    RefGenomeVersion(@NotNull final String identifier, final boolean is37) {
        this.identifier = identifier;
        this.is37 = is37;
    }

    public boolean is37() {
        return is37;
    }

    public boolean is38() {
        return !is37;
    }

    @NotNull
    public String addVersionToFilePath(@NotNull String filePath) {
        String modifiedFilePath = filePath;
        if (filePath.endsWith(GZIP_EXTENSION)) {
            modifiedFilePath = filePath.substring(0, filePath.indexOf(GZIP_EXTENSION));
        }

        if (!modifiedFilePath.contains(".")) {
            throw new IllegalStateException("Cannot include ref genome version in file path that has no proper extension: " + filePath);
        }

        int extensionStart = modifiedFilePath.lastIndexOf(".");
        String versionedFilePath =
                modifiedFilePath.substring(0, extensionStart) + "." + this.identifier + modifiedFilePath.substring(extensionStart);

        if (filePath.endsWith(GZIP_EXTENSION)) {
            versionedFilePath = versionedFilePath + GZIP_EXTENSION;
        }

        return versionedFilePath;
    }
}
