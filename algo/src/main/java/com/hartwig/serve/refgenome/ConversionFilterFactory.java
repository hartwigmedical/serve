package com.hartwig.serve.refgenome;

import java.util.Set;

import com.google.common.collect.Sets;

final class ConversionFilterFactory {

    static final Set<String> GENES_TO_EXCLUDE_FOR_CONVERSION = Sets.newHashSet();

    static {
        populateGenesToFilter();
    }

    private static void populateGenesToFilter() {
        // These genes lie on a part of the ref genome that has been flipped.
        // As a result these genes flipped strand between ref genome versions and are filtered for ref genome conversion
        GENES_TO_EXCLUDE_FOR_CONVERSION.add("MAGEA1");
        GENES_TO_EXCLUDE_FOR_CONVERSION.add("NCOA4");
        GENES_TO_EXCLUDE_FOR_CONVERSION.add("PDE4DIP");
    }

    private ConversionFilterFactory() {
    }
}
