package com.hartwig.serve.datamodel.serialization;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.annotations.VisibleForTesting;
import com.hartwig.serve.datamodel.ServeDatabase;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("ALL")
public final class ServeJson {

    private static final JsonMapper MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .addModule(new SimpleModule()
                    .addSerializer(LocalDate.class, new LocalDateSerializer())
                    .addDeserializer(LocalDate.class, new LocalDateDeserializer())
                    .addSerializer(Set.class, new SortedSetJsonSerializer())
                    .addSerializer(Map.class, new SortedMapJsonSerializer())
                    .addSerializer(List.class, new SortedListJsonSerializer())
            )
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .build();

    @NotNull
    public static String jsonFilePath(@NotNull String outputDir) {
        return outputDir + File.separator + "serve.json";
    }

    @NotNull
    public static ServeDatabase read(@NotNull String filePath) throws IOException {
        return MAPPER.readValue(new File(filePath), ServeDatabase.class);
    }

    public static void write(@NotNull ServeDatabase database, @NotNull String filePath) throws IOException {
        MAPPER.writeValue(new File(filePath), database);
    }

    @NotNull
    @VisibleForTesting
    static ServeDatabase readFromStream(@NotNull InputStream inputStream) throws IOException {
        return MAPPER.readValue(inputStream, ServeDatabase.class);
    }

    @VisibleForTesting
    static void writeToStream(@NotNull ServeDatabase record, @NotNull OutputStream outputStream) throws IOException {
        MAPPER.writeValue(outputStream, record);
    }

    private static class LocalDateSerializer extends StdSerializer<LocalDate> {

        public LocalDateSerializer() {
            super(LocalDate.class);
        }

        @Override
        public void serialize(LocalDate date, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            gen.writeNumberField("year", date.getYear());
            gen.writeNumberField("month", date.getMonthValue());
            gen.writeNumberField("day", date.getDayOfMonth());
            gen.writeEndObject();
        }
    }

    private static class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

        @Override
        public LocalDate deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
            Map<String, Integer> dateMap = parser.readValueAs(Map.class);
            Integer year = dateMap.get("year");
            Integer month = dateMap.get("month");
            Integer day = dateMap.get("day");

            return LocalDate.of(year, month, day);
        }
    }

    private static class SortedSetJsonSerializer extends JsonSerializer<Set> {

        @Override
        public void serialize(Set set, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (set == null) {
                gen.writeNull();
                return;
            }

            gen.writeStartArray();
            if (!set.isEmpty()) {
                if (!SortedSet.class.isAssignableFrom(set.getClass())) {
                    Object item = set.iterator().next();
                    if (Comparable.class.isAssignableFrom(item.getClass())) {
                        set = new TreeSet(set);
                    }
                }
                for (Object item : set) {
                    gen.writeObject(item);
                }
            }
            gen.writeEndArray();
        }
    }

    private static class SortedMapJsonSerializer extends JsonSerializer<Map> {

        @Override
        public void serialize(Map map, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (map == null) {
                gen.writeNull();
                return;
            }

            gen.writeStartObject();
            if (!map.isEmpty()) {
                if (!(map instanceof SortedMap)) {
                    Object key = map.keySet().iterator().next();
                    if (key instanceof Comparable) {
                        map = new TreeMap<>(map);
                    }
                }
                for (Object entryObj : map.entrySet()) {
                    Map.Entry entry = (Map.Entry) entryObj;
                    Object key = entry.getKey();
                    Object value = entry.getValue();

                    String fieldName = key.toString();
                    gen.writeFieldName(fieldName);
                    gen.writeObject(value);
                }
            }
            gen.writeEndObject();
        }
    }

    private static class SortedListJsonSerializer extends JsonSerializer<List> {

        @Override
        public void serialize(List list, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (list == null) {
                gen.writeNull();
                return;
            }

            gen.writeStartArray();
            if (!list.isEmpty()) {
                Object item = list.get(0);
                if (item instanceof Comparable) {
                    List<Object> sortedList = new ArrayList<>(list);
                    Collections.sort(sortedList, Comparator.comparing(o -> (Comparable) o));
                    for (Object element : sortedList) {
                        gen.writeObject(element);
                    }
                } else {
                    for (Object element : list) {
                        gen.writeObject(element);
                    }
                }
            }
            gen.writeEndArray();
        }
    }
}