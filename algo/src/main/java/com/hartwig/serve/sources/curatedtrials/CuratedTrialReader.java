package com.hartwig.serve.sources.curatedtrials;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hartwig.serve.datamodel.common.CancerType;
import com.hartwig.serve.datamodel.common.ImmutableCancerType;
import com.hartwig.serve.datamodel.common.ImmutableIndication;
import com.hartwig.serve.datamodel.common.Indication;
import com.hartwig.serve.datamodel.molecular.ImmutableMolecularCriterium;
import com.hartwig.serve.datamodel.molecular.MolecularCriterium;
import com.hartwig.serve.datamodel.molecular.gene.GeneEvent;
import com.hartwig.serve.datamodel.molecular.gene.ImmutableActionableGene;
import com.hartwig.serve.datamodel.molecular.gene.ActionableGene;
import com.hartwig.serve.datamodel.trial.Country;
import com.hartwig.serve.datamodel.trial.GenderCriterium;
import com.hartwig.serve.datamodel.trial.Hospital;
import com.hartwig.serve.datamodel.trial.ImmutableCountry;
import com.hartwig.serve.datamodel.trial.ImmutableHospital;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CuratedTrialReader {

    private static final Logger LOGGER = LogManager.getLogger(CuratedTrialReader.class);

    private CuratedTrialReader() {
    }

    @NotNull
    public static List<CuratedTrialEntry> read(@NotNull String jsonFilePath) throws IOException {
        List<CuratedTrialEntry> entries = new ArrayList<>();

        JsonObject root;
        try (FileReader reader = new FileReader(jsonFilePath)) {
            root = JsonParser.parseReader(reader).getAsJsonObject();
        }

        JsonArray trials = root.getAsJsonArray("trials");
        if (trials == null) {
            LOGGER.warn("No 'trials' array found in curated trials JSON at {}", jsonFilePath);
            return entries;
        }

        for (JsonElement element : trials) {
            JsonObject trial = element.getAsJsonObject();

            // Skip trials that have an nctId
            if (!trial.get("nctId").isJsonNull()) {
                continue;
            }

            // Skip trials that are not open
            if (trial.has("open") && !trial.get("open").getAsBoolean()) {
                continue;
            }

            CuratedTrialEntry entry = parseTrial(trial);
            if (entry != null) {
                entries.add(entry);
            }
        }

        LOGGER.info(" Read {} curated trials without nctId from {}", entries.size(), jsonFilePath);
        return entries;
    }

    @Nullable
    private static CuratedTrialEntry parseTrial(@NotNull JsonObject trial) {
        String trialId = trial.has("trialId") ? trial.get("trialId").getAsString() : "";
        String title   = trial.has("title")   ? trial.get("title").getAsString()   : "";
        String acronym = trial.has("acronym") ? trial.get("acronym").getAsString() : "";

        // Gender
        GenderCriterium gender = null;
        if (trial.has("genderCriterium") && !trial.get("genderCriterium").isJsonNull()) {
            try {
                gender = GenderCriterium.valueOf(trial.get("genderCriterium").getAsString().toUpperCase());
            } catch (IllegalArgumentException e) {
                LOGGER.warn("Unrecognized genderCriterium '{}' in trial '{}'",
                        trial.get("genderCriterium").getAsString(), trialId);
            }
        }

        // Therapy names
        Set<String> therapyNames = new HashSet<>();
        if (trial.has("therapyNames")) {
            for (JsonElement t : trial.getAsJsonArray("therapyNames")) {
                therapyNames.add(t.getAsString());
            }
        }

        // Countries
        Set<Country> countries = parseCountries(trial);

        // Indications
        Set<Indication> indications = parseIndications(trial);

        // Molecular criteria
        List<MolecularCriterium> anyMolecularCriteria = parseMolecularCriteria(trial, trialId);

        // URLs
        Set<String> urls = new HashSet<>();
        if (trial.has("urls")) {
            for (JsonElement u : trial.getAsJsonArray("urls")) {
                urls.add(u.getAsString());
            }
        }

        return ImmutableCuratedTrialEntry.builder()
                .trialId(trialId)
                .title(title)
                .acronym(acronym)
                .genderCriterium(gender)
                .therapyNames(therapyNames)
                .countries(countries)
                .indications(indications)
                .anyMolecularCriteria(anyMolecularCriteria)
                .urls(urls)
                .build();
    }

    @NotNull
    private static Set<Country> parseCountries(@NotNull JsonObject trial) {
        Set<Country> countries = new HashSet<>();
        if (!trial.has("countries")) {
            return countries;
        }

        for (JsonElement countryEl : trial.getAsJsonArray("countries")) {
            JsonObject countryObj = countryEl.getAsJsonObject();
            String countryName = countryObj.get("name").getAsString();

            Map<String, Set<Hospital>> hospitalsPerCity = new HashMap<>();
            if (countryObj.has("hospitalsPerCity")) {
                JsonObject hpc = countryObj.getAsJsonObject("hospitalsPerCity");
                for (Map.Entry<String, JsonElement> cityEntry : hpc.entrySet()) {
                    String city = cityEntry.getKey();
                    Set<Hospital> hospitals = new HashSet<>();
                    for (JsonElement hospitalEl : cityEntry.getValue().getAsJsonArray()) {
                        String hospitalName = hospitalEl.getAsJsonObject().get("name").getAsString();
                        hospitals.add(ImmutableHospital.builder()
                                .name(hospitalName)
                                .isChildrensHospital(null)
                                .build());
                    }
                    hospitalsPerCity.put(city, hospitals);
                }
            }

            countries.add(ImmutableCountry.builder()
                    .name(countryName)
                    .hospitalsPerCity(hospitalsPerCity)
                    .build());
        }
        return countries;
    }

    @NotNull
    private static Set<Indication> parseIndications(@NotNull JsonObject trial) {
        Set<Indication> indications = new HashSet<>();
        if (!trial.has("indications")) {
            return indications;
        }

        for (JsonElement indicationEl : trial.getAsJsonArray("indications")) {
            JsonObject indicationObj = indicationEl.getAsJsonObject();
            if (!indicationObj.has("applicableType")) {
                continue;
            }
            JsonObject applicableType = indicationObj.getAsJsonObject("applicableType");
            String name = applicableType.has("name") ? applicableType.get("name").getAsString() : "";
            String doid = applicableType.has("doid") ? applicableType.get("doid").getAsString() : "";

            indications.add(ImmutableIndication.builder()
                    .applicableType(ImmutableCancerType.builder().name(name).doid(doid).build())
                    .excludedSubTypes(new HashSet<>())
                    .build());
        }
        return indications;
    }

    @NotNull
    private static List<MolecularCriterium> parseMolecularCriteria(@NotNull JsonObject trial, @NotNull String trialId) {
        List<MolecularCriterium> criteria = new ArrayList<>();
        if (!trial.has("anyMolecularCriteria")) {
            return criteria;
        }

        for (JsonElement criteriumEl : trial.getAsJsonArray("anyMolecularCriteria")) {
            JsonObject criteriumObj = criteriumEl.getAsJsonObject();

            Set<ActionableGene> genes = new HashSet<>();
            if (criteriumObj.has("genes")) {
                for (JsonElement geneEl : criteriumObj.getAsJsonArray("genes")) {
                    JsonObject geneObj = geneEl.getAsJsonObject();
                    String geneName    = geneObj.has("gene")        ? geneObj.get("gene").getAsString()        : "";
                    String eventStr    = geneObj.has("event")       ? geneObj.get("event").getAsString()       : "";
                    String sourceEvent = geneObj.has("sourceEvent") ? geneObj.get("sourceEvent").getAsString() : "";

                    GeneEvent geneEvent;
                    try {
                        String normalised = eventStr.toUpperCase().replace(" ", "_").replace("-", "_");
                        geneEvent = GeneEvent.valueOf(normalised);
                    } catch (IllegalArgumentException e) {
                        LOGGER.warn("Unrecognized gene event '{}' in trial '{}', skipping gene '{}'",
                                eventStr, trialId, geneName);
                        continue;
                    }

                    // sourceDate: read from JSON if present, otherwise use today
                    LocalDate sourceDate = LocalDate.now();
                    if (geneObj.has("sourceDate") && !geneObj.get("sourceDate").isJsonNull()) {
                        JsonObject sd = geneObj.getAsJsonObject("sourceDate");
                        sourceDate = LocalDate.of(
                                sd.get("year").getAsInt(),
                                sd.get("month").getAsInt(),
                                sd.get("day").getAsInt());
                    }

                    // sourceUrls
                    Set<String> sourceUrls = new HashSet<>();
                    if (geneObj.has("sourceUrls")) {
                        for (JsonElement u : geneObj.getAsJsonArray("sourceUrls")) {
                            sourceUrls.add(u.getAsString());
                        }
                    }

                    genes.add(ImmutableActionableGene.builder()
                            .gene(geneName)
                            .event(geneEvent)
                            .sourceDate(sourceDate)
                            .sourceEvent(sourceEvent)
                            .sourceUrls(sourceUrls)
                            .build());
                }
            }

            // Only add this criteria block if it has at least one gene
            if (!genes.isEmpty()) {
                criteria.add(ImmutableMolecularCriterium.builder()
                        .genes(genes)
                        .hotspots(new HashSet<>())
                        .codons(new HashSet<>())
                        .exons(new HashSet<>())
                        .fusions(new HashSet<>())
                        .characteristics(new HashSet<>())
                        .hla(new HashSet<>())
                        .build());
            }
        }
        return criteria;
    }
}