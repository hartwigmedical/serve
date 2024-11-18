package com.hartwig.serve.datamodel.trial;

import java.util.Comparator;

import com.hartwig.serve.datamodel.util.CompareFunctions;

import org.jetbrains.annotations.NotNull;

public class CountryComparator implements Comparator<Country> {

    @Override
    public int compare(@NotNull Country country1, @NotNull Country country2) {
        int nameCompare = country1.name().compareTo(country2.name());
        if (nameCompare != 0) {
            return nameCompare;
        }

        // This is not exhaustively comparing hospitals per city but does compare cities and assumption is that this is sufficient.
        return CompareFunctions.compareSetOfComparable(country1.hospitalsPerCity().keySet(), country2.hospitalsPerCity().keySet());
    }
}
