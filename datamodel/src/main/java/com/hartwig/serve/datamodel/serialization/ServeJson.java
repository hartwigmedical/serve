package com.hartwig.serve.datamodel.serialization;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.datamodel.ServeRecord;

import org.jetbrains.annotations.NotNull;

public final class ServeJson {

    private static final JsonMapper MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .addModule(new SimpleModule()
                    .addSerializer(LocalDate.class, new LocalDateSerializer())
                    .addDeserializer(LocalDate.class, new LocalDateDeserializer())
            )
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

    static class LocalDateSerializer extends StdSerializer<LocalDate> {

        public LocalDateSerializer() {
            super(LocalDate.class);
        }

        @Override
        public void serialize(LocalDate date, JsonGenerator gen, SerializerProvider provider) throws IOException {
            Map<String, Integer> dateMap = new LinkedHashMap<>();
            dateMap.put("year", date.getYear());
            dateMap.put("month", date.getMonthValue());
            dateMap.put("day", date.getDayOfMonth());
            gen.writeObject(dateMap);
        }
    }

    static class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

        @Override
        public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            Map<String, Integer> dateMap = p.readValueAs(Map.class);
            Integer year = dateMap.get("year");
            Integer month = dateMap.get("month");
            Integer day = dateMap.get("day");

            return LocalDate.of(year, month, day);
        }
    }
}