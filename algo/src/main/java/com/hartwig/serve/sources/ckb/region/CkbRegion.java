package com.hartwig.serve.sources.ckb.region;

import java.util.Optional;
import java.util.Set;

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
        boolean isCountryMatch = country().equals(toLowercaseNullable(location.country()));
        boolean isStateMatch = states().isEmpty() || states().contains(toLowercaseNullable(location.state()));
        return isCountryMatch && isStateMatch;
    }

    @NotNull
    private static String toLowercaseNullable(@Nullable String string) {
        return Optional.ofNullable(string).map(String::toLowerCase).orElse("null");
    }
}
