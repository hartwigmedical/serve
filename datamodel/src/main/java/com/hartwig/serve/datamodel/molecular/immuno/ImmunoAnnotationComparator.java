package com.hartwig.serve.datamodel.molecular.immuno;

import java.util.Comparator;

import org.jetbrains.annotations.NotNull;

public class ImmunoAnnotationComparator implements Comparator<ImmunoAnnotation> {

    @Override
    public int compare(@NotNull ImmunoAnnotation pair1, @NotNull ImmunoAnnotation pair2) {
        int geneCompare = pair1.gene().compareTo(pair2.gene());
        if (geneCompare != 0) {
            return geneCompare;
        }

        return pair1.alleleGroup().compareTo(pair2.alleleGroup());
    }
}
