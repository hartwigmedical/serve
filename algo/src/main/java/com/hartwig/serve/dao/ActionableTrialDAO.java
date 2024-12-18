package com.hartwig.serve.dao;

import static com.hartwig.serve.database.Tables.ACTIONABLETRIAL;
import static com.hartwig.serve.database.Tables.TRIALMOLECULARCRITERIUM;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import com.hartwig.serve.datamodel.molecular.MolecularCriterium;
import com.hartwig.serve.datamodel.trial.ActionableTrial;
import com.hartwig.serve.datamodel.trial.Country;
import com.hartwig.serve.datamodel.trial.Hospital;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

class ActionableTrialDAO {

    @NotNull
    private final DSLContext context;
    @NotNull
    private final MolecularCriteriumDAO molecularCriteriumDAO;

    public ActionableTrialDAO(@NotNull final DSLContext context, @NotNull final MolecularCriteriumDAO molecularCriteriumDAO) {
        this.context = context;
        this.molecularCriteriumDAO = molecularCriteriumDAO;
    }

    public void deleteAll() {
        context.deleteFrom(ACTIONABLETRIAL).execute();
        context.deleteFrom(TRIALMOLECULARCRITERIUM).execute();
    }

    public void write(@NotNull List<ActionableTrial> actionableTrials) {
        for (ActionableTrial actionableTrial : actionableTrials) {
            int actionableTrialId = context.insertInto(ACTIONABLETRIAL,
                            ACTIONABLETRIAL.SOURCE,
                            ACTIONABLETRIAL.NCTID,
                            ACTIONABLETRIAL.TITLE,
                            ACTIONABLETRIAL.ACRONYM,
                            ACTIONABLETRIAL.COUNTRIESANDCITIES,
                            ACTIONABLETRIAL.HOSPITALSPERCITY,
                            ACTIONABLETRIAL.THERAPYNAMES,
                            ACTIONABLETRIAL.GENDERCRITERIUM,
                            ACTIONABLETRIAL.INDICATIONS,
                            ACTIONABLETRIAL.URLS)
                    .values(actionableTrial.source().name(),
                            actionableTrial.nctId(),
                            actionableTrial.title(),
                            actionableTrial.acronym(),
                            extractCountriesWithCities(actionableTrial.countries()),
                            extractHospitalsPerCity(actionableTrial.countries()),
                            DatabaseUtil.concat(actionableTrial.therapyNames()),
                            actionableTrial.genderCriterium().name(),
                            DatabaseUtil.concat(actionableTrial.indications()
                                    .stream()
                                    .map(DatabaseUtil::formatIndication)
                                    .collect(Collectors.toSet())),
                            DatabaseUtil.concat(actionableTrial.urls()))
                    .returning(ACTIONABLETRIAL.ID)
                    .fetchOne()
                    .getValue(ACTIONABLETRIAL.ID);

            for (MolecularCriterium molecularCriterium : actionableTrial.anyMolecularCriteria()) {
                int molecularCriteriumId = molecularCriteriumDAO.write(molecularCriterium);

                context.insertInto(TRIALMOLECULARCRITERIUM,
                        TRIALMOLECULARCRITERIUM.ACTIONABLETRIALID,
                        TRIALMOLECULARCRITERIUM.MOLECULARCRITERIUMID).values(actionableTrialId, molecularCriteriumId).execute();
            }
        }

    }

    @NotNull
    private static String extractCountriesWithCities(@NotNull Set<Country> countries) {
        return countries.stream()
                .map(country -> country.name() + "(" + String.join(DatabaseUtil.SUB_JOINER, country.hospitalsPerCity().keySet()) + ")")
                .collect(Collectors.joining(DatabaseUtil.MAIN_JOINER));
    }

    @NotNull
    private static String extractHospitalsPerCity(@NotNull Set<Country> countries) {
        StringJoiner joiner = new StringJoiner(DatabaseUtil.MAIN_JOINER);
        for (Country country : countries) {
            for (Map.Entry<String, Set<Hospital>> entry : country.hospitalsPerCity().entrySet()) {
                String city = entry.getKey();
                Set<String> hospitalNames = entry.getValue().stream().map(Hospital::name).collect(Collectors.toSet());
                joiner.add(city + "(" + String.join(DatabaseUtil.SUB_JOINER, hospitalNames) + ")");
            }
        }
        return joiner.toString();
    }
}
