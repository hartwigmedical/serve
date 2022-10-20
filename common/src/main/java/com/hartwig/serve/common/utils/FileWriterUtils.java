package com.hartwig.serve.common.utils;

import java.io.File;

import org.jetbrains.annotations.NotNull;

public final class FileWriterUtils {

    private FileWriterUtils() {
    }

    public static String checkAddDirSeparator(@NotNull String outputDir) {
        if (outputDir.endsWith(File.separator)) {
            return outputDir;
        }

        return outputDir + File.separator;
    }
}
