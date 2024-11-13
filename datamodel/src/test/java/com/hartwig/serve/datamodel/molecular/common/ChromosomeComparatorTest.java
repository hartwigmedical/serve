package com.hartwig.serve.datamodel.molecular.common;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.Lists;

import org.junit.Test;

public class ChromosomeComparatorTest {

    @Test
    public void canSortChromosomes() {
        String chromosome1 = "1";
        String chromosome2 = "chr2";
        String chromosome3 = "12";
        String chromosome4 = "X";
        String chromosome5 = "Y";
        String chromosome6 = "chrM";

        List<String> chromosomes = Lists.newArrayList(chromosome5, chromosome4, chromosome1, chromosome6, chromosome2, chromosome3);
        chromosomes.sort(new ChromosomeComparator());

        assertEquals(chromosome1, chromosomes.get(0));
        assertEquals(chromosome2, chromosomes.get(1));
        assertEquals(chromosome3, chromosomes.get(2));
        assertEquals(chromosome4, chromosomes.get(3));
        assertEquals(chromosome5, chromosomes.get(4));
        assertEquals(chromosome6, chromosomes.get(5));
    }
}