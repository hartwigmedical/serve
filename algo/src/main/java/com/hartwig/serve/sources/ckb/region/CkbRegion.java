package com.hartwig.serve.sources.ckb.region;

import java.util.List;

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
    public abstract List<String> states();

    public boolean includes(@NotNull Location location) {
        // TODO (PW): Shouldn't this be "states.isEmpty || states.contains()"? Or how would someone configure all states for a country?
        return location.country().equals(country()) && (!states().isEmpty() && states().contains(location.state()));
    }
}
