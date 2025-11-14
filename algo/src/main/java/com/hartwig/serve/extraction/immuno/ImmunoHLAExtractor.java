package com.hartwig.serve.extraction.immuno;

import com.hartwig.serve.common.classification.EventType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ImmunoHLAExtractor {

    private static final Logger LOGGER = LogManager.getLogger(ImmunoHLAExtractor.class);

    public ImmunoHLAExtractor() {
    }

    @Nullable
    public ImmunoHLA extract(@NotNull String gene, @NotNull EventType type, @NotNull String event) {
        if (type == EventType.IMMUNO_HLA) {
            if (event.contains(":")) {
                String alleleGroup = event.split(":")[0].substring(1);

                String[] eventParts = event.split(":");
                String hlaProtein = eventParts.length > 1 ? eventParts[1] : null;
                String synonymousDnaChange = eventParts.length > 2 ? eventParts[2] : null;
                String nonCodingDifferences = eventParts.length > 3 ? eventParts[3] : null;

                String expressionStatus = null;

                if (hlaProtein != null && hlaProtein.chars().anyMatch(Character::isLetter)) {
                    expressionStatus = hlaProtein.substring(hlaProtein.length() - 1);
                    hlaProtein = hlaProtein.substring(0, hlaProtein.length() - 1);
                } else if (synonymousDnaChange != null && synonymousDnaChange.chars().anyMatch(Character::isLetter)) {
                    expressionStatus = synonymousDnaChange.substring(synonymousDnaChange.length() - 1);
                    synonymousDnaChange = synonymousDnaChange.substring(0, synonymousDnaChange.length() - 1);
                } else if (nonCodingDifferences != null && nonCodingDifferences.chars().anyMatch(Character::isLetter)) {
                    expressionStatus = nonCodingDifferences.substring(nonCodingDifferences.length() - 1);
                    nonCodingDifferences = nonCodingDifferences.substring(0, nonCodingDifferences.length() - 1);
                }

                return ImmutableImmunoHLA.builder()
                        .gene(gene)
                        .alleleGroup(alleleGroup)
                        .hlaProtein(hlaProtein)
                        .synonymousDnaChange(synonymousDnaChange)
                        .nonCodingDifferences(nonCodingDifferences)
                        .expressionStatus(expressionStatus)
                        .build();
            } else {
                return ImmutableImmunoHLA.builder().gene(gene).alleleGroup(event.substring(1)).build();
            }
        }
        return null;
    }
}