package com.hartwig.serve.sources.actin.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.serve.sources.actin.ActinTestFactory;
import com.hartwig.serve.sources.actin.reader.ActinEntry;
import com.hartwig.serve.sources.actin.reader.ActinRule;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ActinFilterTest {

    @Test
    public void canFilter() {
        List<ActinFilterEntry> filterEntries = Lists.newArrayList();
        filterEntries.add(create(ActinFilterType.FILTER_MUTATION_ON_GENE, "BRAF V600E"));
        filterEntries.add(create(ActinFilterType.FILTER_RULE_ON_GENE, "BRAF " + ActinRule.ACTIVATING_MUTATION_IN_GENE_X.toString()));
        filterEntries.add(create(ActinFilterType.FILTER_EVERYTHING_FOR_GENE, "KRAS"));
        filterEntries.add(create(ActinFilterType.FILTER_EVERYTHING_FOR_RULE, ActinRule.WILDTYPE_OF_GENE_X.toString()));

        ActinFilter filter = new ActinFilter(filterEntries);

        ActinEntry brafV600E = ActinTestFactory.builder().gene("BRAF").mutation("V600E").build();
        assertTrue(filter.run(Lists.newArrayList(brafV600E)).isEmpty());

        ActinEntry brafActivation = ActinTestFactory.builder().rule(ActinRule.ACTIVATING_MUTATION_IN_GENE_X).gene("BRAF").build();
        assertTrue(filter.run(Lists.newArrayList(brafActivation)).isEmpty());

        ActinEntry kras = ActinTestFactory.builder().gene("KRAS").build();
        assertTrue(filter.run(Lists.newArrayList(kras)).isEmpty());

        ActinEntry wildtype = ActinTestFactory.builder().rule(ActinRule.WILDTYPE_OF_GENE_X).build();
        assertTrue(filter.run(Lists.newArrayList(wildtype)).isEmpty());

        ActinEntry tmb = ActinTestFactory.builder().rule(ActinRule.TMB_OF_AT_LEAST_X).build();
        assertFalse(filter.run(Lists.newArrayList(tmb)).isEmpty());

        filter.reportUnusedFilterEntries();
    }

    @NotNull
    private static ActinFilterEntry create(@NotNull ActinFilterType type, @NotNull String value) {
        return ImmutableActinFilterEntry.builder().type(type).value(value).build();
    }
}
