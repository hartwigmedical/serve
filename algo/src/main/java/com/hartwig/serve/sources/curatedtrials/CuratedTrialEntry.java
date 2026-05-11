package com.hartwig.serve.sources.curatedtrials;

import java.util.List;
import java.util.Set;

import com.hartwig.serve.datamodel.common.Indication;
import com.hartwig.serve.datamodel.molecular.MolecularCriterium;
import com.hartwig.serve.datamodel.trial.Country;
import com.hartwig.serve.datamodel.trial.GenderCriterium;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class CuratedTrialEntry {

    @NotNull
    public abstract String trialId();

    @NotNull
    public abstract String title();

    @NotNull
    public abstract String acronym();

    @NotNull
    public abstract Set<String> therapyNames();

    @NotNull
    public abstract Set<Country> countries();

    @Nullable
    public abstract GenderCriterium genderCriterium();

    @NotNull
    public abstract Set<Indication> indications();

    @NotNull
    public abstract List<MolecularCriterium> anyMolecularCriteria();

    @NotNull
    public abstract Set<String> urls();
}