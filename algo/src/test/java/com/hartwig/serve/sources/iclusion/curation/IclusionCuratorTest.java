package com.hartwig.serve.sources.iclusion.curation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.common.collect.Lists;
import com.hartwig.serve.iclusion.datamodel.IclusionMutation;
import com.hartwig.serve.iclusion.datamodel.IclusionTrial;
import com.hartwig.serve.iclusion.datamodel.ImmutableIclusionMutation;
import com.hartwig.serve.sources.iclusion.IclusionTestFactory;

import org.junit.Test;

public class IclusionCuratorTest {

    @Test
    public void canCurateIclusionTrialsOnEntry() {
        CurationEntry firstEntry = CurationFactory.MUTATION_MAPPINGS.keySet().iterator().next();

        IclusionMutation mutation =
                ImmutableIclusionMutation.builder().gene(firstEntry.gene()).name(firstEntry.name()).negation(false).build();

        IclusionTrial trial = IclusionTestFactory.trial("test", Lists.newArrayList(IclusionTestFactory.or(Lists.newArrayList(mutation))));

        IclusionCurator curator = new IclusionCurator();
        IclusionTrial curatedTrial = curator.run(Lists.newArrayList(trial)).get(0);
        IclusionMutation curatedMutation = curatedTrial.mutationConditions().get(0).mutations().get(0);
        assertNotNull(curatedMutation);

        CurationEntry expected = CurationFactory.MUTATION_MAPPINGS.get(firstEntry);
        assertEquals(expected.gene(), curatedMutation.gene());
        assertEquals(expected.name(), curatedMutation.name());
        assertEquals(mutation.negation(), curatedMutation.negation());

        curator.reportUnusedCurationEntries();
    }

    @Test
    public void canCurateIclusionTrialsOnGenes() {
        String firstGene = CurationFactory.GENE_MAPPINGS.keySet().iterator().next();

        IclusionMutation mutation = ImmutableIclusionMutation.builder().gene(firstGene).name("any").negation(false).build();

        IclusionTrial trial = IclusionTestFactory.trial("test", Lists.newArrayList(IclusionTestFactory.or(Lists.newArrayList(mutation))));

        IclusionCurator curator = new IclusionCurator();
        IclusionTrial curatedTrial = curator.run(Lists.newArrayList(trial)).get(0);
        IclusionMutation curatedMutation = curatedTrial.mutationConditions().get(0).mutations().get(0);
        assertNotNull(curatedMutation);

        String expectedGene = CurationFactory.GENE_MAPPINGS.get(firstGene);
        assertEquals(expectedGene, curatedMutation.gene());
        assertEquals(mutation.name(), curatedMutation.name());
        assertEquals(mutation.negation(), curatedMutation.negation());

        curator.reportUnusedCurationEntries();
    }
}