package com.hartwig.serve.ckb.json;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CkbJsonDirectoryReader<T extends CkbJsonObject> {

    private static final Logger LOGGER = LogManager.getLogger(CkbJsonDirectoryReader.class);

    @Nullable
    private final Integer maxFilesToRead;

    public CkbJsonDirectoryReader(@Nullable final Integer maxFilesToRead) {
        this.maxFilesToRead = maxFilesToRead;
    }

    @NotNull
    public List<T> read(@NotNull String dir) throws IOException {
        File[] files = new File(dir).listFiles();

        LOGGER.debug(" {} files found in directory {}", files.length, dir);

        Stream<File> fileStream = (maxFilesToRead == null) ? Arrays.stream(files) : Arrays.stream(files).limit(maxFilesToRead);
        List<T> entries = fileStream.parallel().flatMap(file -> {
            try {
                JsonReader reader = new JsonReader(new FileReader(file));
                reader.setLenient(true);

                List<T> entriesForFile = new ArrayList<>();
                while (reader.peek() != JsonToken.END_DOCUMENT) {
                    entriesForFile.add(read(JsonParser.parseReader(reader).getAsJsonObject()));
                }
                reader.close();
                return entriesForFile.stream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

        LOGGER.debug("  Done reading {} files ", entries.size());
        return entries;
    }

    @NotNull
    protected abstract T read(@NotNull JsonObject object);
}
