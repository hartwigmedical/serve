package com.hartwig.serve.actionability;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.DatamodelTestFactory;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.ImmutableCancerType;
import com.hartwig.serve.datamodel.ImmutableTreatment;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.fusion.ImmutableActionableFusion;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

public class ActionableEventUrlMergerTest {

    private static final String GENE_1 = "gene1";
    private static final String GENE_2 = "gene2";

    @Test
    public void canMergeFusionUrls() {
        Set<ActionableFusion> fusions = Sets.newHashSet();
        fusions.add(createFusion(GENE_1, "url1"));
        fusions.add(createFusion(GENE_1, "url2"));
        fusions.add(createFusion(GENE_2, "url3"));

        Set<ActionableFusion> consolidated = ActionableEventUrlMerger.merge(fusions, new ActionableFusionUrlConsolidator());
        assertEquals(2, consolidated.size());
        Assert.assertEquals(Sets.newHashSet("url1", "url2"), findByGeneUp(consolidated, GENE_1).evidenceUrls());
        Assert.assertEquals(Sets.newHashSet("url3"), findByGeneUp(consolidated, GENE_2).evidenceUrls());
    }

    @NotNull
    private static ActionableFusion createFusion(@NotNull String gene, @NotNull String url) {
        return ImmutableActionableFusion.builder()
                .from(DatamodelTestFactory.createActionableEvent(Knowledgebase.VICC_CGI,
                        "source event",
                        Sets.newHashSet(),
                        ImmutableTreatment.builder()
                                .name("treatment")
                                .sourceRelevantTreatmentApproaches(Sets.newHashSet("drug classes"))
                                .build(),
                        ImmutableCancerType.builder().name("applicable cancerType").doid("applicable doid").build(),
                        Sets.newHashSet(ImmutableCancerType.builder().name("blacklist cancerType").doid("blacklist doid").build()),
                        EvidenceLevel.A,
                        EvidenceDirection.RESPONSIVE,
                        Sets.newHashSet(url)))
                .geneUp(gene)
                .geneDown(gene)
                .build();
    }

    @NotNull
    private static ActionableFusion findByGeneUp(@NotNull Iterable<ActionableFusion> fusions, @NotNull String geneUpToFind) {
        for (ActionableFusion fusion : fusions) {
            if (fusion.geneUp().equals(geneUpToFind)) {
                return fusion;
            }
        }

        throw new IllegalStateException("Could not find geneUp in fusions: " + geneUpToFind);
    }
}