package com.hartwig.serve.datamodel.serialization;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.datamodel.ServeRecord;

import org.jetbrains.annotations.NotNull;

public final class ServeJson {
    private static final JsonMapper MAPPER = JsonMapper.builder()
            .addModule(new GuavaModule())
            .addModule(new JavaTimeModule())
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
            .build();

    public static void write(@NotNull ServeRecord record, @NotNull String filePath) throws IOException {
        MAPPER.writeValue(new File(filePath), record);
    }

    @NotNull
    public static ServeRecord read(@NotNull String filePath) throws IOException {
        return MAPPER.readValue(new File(filePath), ServeRecord.class);
    }

    public static void writeToStream(@NotNull ServeRecord record, @NotNull OutputStream outputStream) throws IOException {
        MAPPER.writeValue(outputStream, record);
    }

    @NotNull
    public static ServeRecord readFromStream(@NotNull InputStream inputStream) throws IOException {
        return MAPPER.readValue(inputStream, ServeRecord.class);
    }

    @NotNull
    public static String jsonFilePath(@NotNull String outputDir, @NotNull RefGenome refGenome) {
        return refGenome.addVersionToFilePath(outputDir + "/serve.json");
    }
}
