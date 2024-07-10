package com.hartwig.serve.sources.ckb.region;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.hartwig.serve.ckb.datamodel.clinicaltrial.Location;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class CkbRegion {

    @NotNull
    public abstract String country();

    @NotNull
    public abstract Set<String> states();

    public boolean includes(@NotNull Location location) {
        return country().equals(toLowercaseNullable(location.country())) && (states().isEmpty() || states().contains(toLowercaseNullable(
                location.state())));
    }

    @NotNull
    public static CkbRegion of(@NotNull String country, @NotNull Set<String> states) {
        return ImmutableCkbRegion.builder()
                .country(country.toLowerCase())
                .states(states.stream().map(String::toLowerCase).collect(Collectors.toSet()))
                .build();
    }

    @NotNull
    private static String toLowercaseNullable(@Nullable String string) {
        return Optional.ofNullable(string).map(String::toLowerCase).orElse("null");
    }
}
