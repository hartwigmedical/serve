package com.hartwig.serve.datamodel.gene;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;

import org.junit.Test;

public class GeneAnnotationComparatorTest {

    @Test
    public void canSortGeneAnnotations() {
        GeneAnnotation annotation1 = GeneTestFactory.createGeneAnnotation("gene A", GeneEvent.ANY_MUTATION);
        GeneAnnotation annotation2 = GeneTestFactory.createGeneAnnotation("gene A", GeneEvent.DELETION);
        GeneAnnotation annotation3 = GeneTestFactory.createGeneAnnotation("gene B", GeneEvent.ANY_MUTATION);

        List<GeneAnnotation> annotations = Lists.newArrayList(annotation1, annotation3, annotation2);
        annotations.sort(new GeneAnnotationComparator());

        assertEquals(annotation1, annotations.get(0));
        assertEquals(annotation2, annotations.get(1));
        assertEquals(annotation3, annotations.get(2));
    }
}