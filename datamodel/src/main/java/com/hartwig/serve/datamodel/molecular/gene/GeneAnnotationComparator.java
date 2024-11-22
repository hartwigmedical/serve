package com.hartwig.serve.datamodel.molecular.gene;

import java.util.Comparator;

import org.jetbrains.annotations.NotNull;

public class GeneAnnotationComparator implements Comparator<GeneAnnotation> {

    @Override
    public int compare(@NotNull GeneAnnotation annotation1, @NotNull GeneAnnotation annotation2) {
        int geneCompare = annotation1.gene().compareTo(annotation2.gene());
        if (geneCompare != 0) {
            return geneCompare;
        }

        return annotation1.event().toString().compareTo(annotation2.event().toString());
    }
}
