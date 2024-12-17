package com.hartwig.serve.dao;

import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import com.hartwig.serve.datamodel.common.CancerType;
import com.hartwig.serve.datamodel.common.Indication;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DatabaseUtil {

    public static final int DB_BATCH_INSERT_SIZE = 1000;

    public static final String MAIN_JOINER = ",";
    public static final String SUB_JOINER = ";";

    private DatabaseUtil() {
    }

    @Nullable
    public static Byte toByte(@Nullable Boolean bool) {
        return bool != null ? (byte) (bool ? 1 : 0) : null;
    }

    @NotNull
    public static String concat(@NotNull Set<String> strings) {
        StringJoiner joiner = new StringJoiner(MAIN_JOINER);
        for (String string : strings) {
            joiner.add(string);
        }
        return joiner.toString();
    }

    @NotNull
    public static String formatIndication(@NotNull Indication indication) {
        String base = formatCancerType(indication.applicableType());
        String addition = "";

        Set<String> formattedExcludedTypes =
                indication.excludedSubTypes().stream().map(DatabaseUtil::formatCancerType).collect(Collectors.toSet());
        if (!formattedExcludedTypes.isEmpty()) {
            addition = " excluding " + concat(formattedExcludedTypes);
        }
        return base + addition;
    }

    @NotNull
    private static String formatCancerType(@NotNull CancerType cancerType) {
        return cancerType.name() + " (" + cancerType.doid() + ")";
    }
}
