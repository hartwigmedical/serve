package com.hartwig.serve.datamodel.serialization;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hartwig.serve.datamodel.ServeRecord;

import org.jetbrains.annotations.NotNull;

public final class ServeJson {
    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper()
                .registerModule(new GuavaModule())
                .registerModule(new JavaTimeModule())
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static void write(@NotNull ServeRecord record, @NotNull String filePath) throws IOException {
        mapper.writeValue(new File(filePath), record);
    }

    @NotNull
    public static ServeRecord read(@NotNull String filePath) throws IOException {
        return mapper.readValue(new File(filePath), ServeRecord.class);
    }

    public static void writeToStream(ServeRecord record, OutputStream outputStream) throws IOException {
        mapper.writeValue(outputStream, record);
    }

    @NotNull
    public static ServeRecord readFromStream(InputStream inputStream) throws IOException {
        return mapper.readValue(inputStream, ServeRecord.class);
    }
}
