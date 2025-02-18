package com.hartwig.serve.extraction.variant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.serve.DriverGenesTestFactory;
import com.hartwig.serve.common.classification.EventType;
import com.hartwig.serve.common.drivergene.DriverCategory;
import com.hartwig.serve.common.drivergene.DriverGene;
import com.hartwig.serve.extraction.util.DriverInconsistencyMode;
import com.hartwig.serve.extraction.util.GeneChecker;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

public class VariantExtractorTest {

    private static final GeneChecker GENE_CHECKER = new GeneChecker(Sets.newHashSet("BRAF", "KRAS"));

    private static final Variant TEST_VARIANT = ImmutableVariant.builder().chromosome("1").position(10).ref("A").alt("T").build();

    private static final VariantAnnotation TEST_BRAF_VARIANT =
            ImmutableVariantAnnotation.builder().gene("BRAF").chromosome("1").position(10).ref("A").alt("T").build();

    private static final VariantAnnotation TEST_KRAS_VARIANT =
            ImmutableVariantAnnotation.builder().gene("KRAS").chromosome("1").position(10).ref("A").alt("T").build();

    @Test
    public void canFindGene() {
        List<DriverGene> driverGenes = DriverGenesTestFactory.createDriverGenes("BRAF", "KIT");
        assertEquals(DriverCategory.TSG, VariantExtractor.findByGene(driverGenes, "BRAF"));
        assertEquals(DriverCategory.ONCO, VariantExtractor.findByGene(driverGenes, "KIT"));
        assertNull(VariantExtractor.findByGene(driverGenes, "KRAS"));
    }

    @Test
    public void canFilterInCatalog() {
        String protein = "V600E";
        VariantExtractor variantExtractorFilter = createWithProtein(protein, DriverInconsistencyMode.FILTER);
        List<VariantAnnotation> variantsFilterList = variantExtractorFilter.extract("BRAF", null, EventType.VARIANT, "V600E");
        assertEquals(1, variantsFilterList.size());
        assertEquals(TEST_BRAF_VARIANT, variantsFilterList.get(0));

        VariantExtractor variantExtractorIgnore = createWithProtein(protein, DriverInconsistencyMode.IGNORE);
        List<VariantAnnotation> variantsIgnoreList = variantExtractorIgnore.extract("BRAF", null, EventType.VARIANT, "V600E");
        assertEquals(1, variantsIgnoreList.size());
        assertEquals(TEST_BRAF_VARIANT, variantsIgnoreList.get(0));

        VariantExtractor variantExtractorWarn = createWithProtein(protein, DriverInconsistencyMode.WARN_ONLY);
        List<VariantAnnotation> variantsWarnList = variantExtractorWarn.extract("BRAF", null, EventType.VARIANT, "V600E");
        assertEquals(1, variantsWarnList.size());
        assertEquals(TEST_BRAF_VARIANT, variantsWarnList.get(0));
    }

    @Test
    public void canFilterNotInCatalog() {
        String protein = "V600E";
        VariantExtractor variantExtractorFilter = createWithProtein(protein, DriverInconsistencyMode.FILTER);
        List<VariantAnnotation> variantsFilterList = variantExtractorFilter.extract("KRAS", null, EventType.VARIANT, "V600E");
        assertNull(variantsFilterList);

        VariantExtractor variantExtractorIgnore = createWithProtein(protein, DriverInconsistencyMode.IGNORE);
        List<VariantAnnotation> variantsIgnoreList = variantExtractorIgnore.extract("KRAS", null, EventType.VARIANT, "V600E");
        assertEquals(1, variantsIgnoreList.size());
        assertEquals(TEST_KRAS_VARIANT, variantsIgnoreList.get(0));

        VariantExtractor variantExtractorWarn = createWithProtein(protein, DriverInconsistencyMode.WARN_ONLY);
        List<VariantAnnotation> variantsWarnList = variantExtractorWarn.extract("KRAS", null, EventType.VARIANT, "V600E");
        assertEquals(1, variantsWarnList.size());
        assertEquals(TEST_KRAS_VARIANT, variantsWarnList.get(0));
    }

    @Test
    public void canExtractSimpleVariant() {
        String protein = "V600E";

        VariantExtractor variantExtractor = createWithProtein(protein, DriverInconsistencyMode.IGNORE);
        List<VariantAnnotation> variants = variantExtractor.extract("BRAF", null, EventType.VARIANT, "V600E");

        assertEquals(1, variants.size());
        assertEquals(TEST_BRAF_VARIANT, variants.get(0));
    }

    @Test
    public void skipsVariantsForInvalidGenes() {
        String protein = "V600E";

        VariantExtractor variantExtractor = createWithProtein(protein, DriverInconsistencyMode.IGNORE);

        assertNull(variantExtractor.extract("NOT-A-GENE", null, EventType.VARIANT, "V600E"));
    }

    @NotNull
    private static VariantExtractor createWithProtein(@NotNull String protein, @NotNull DriverInconsistencyMode annotation) {
        return new VariantExtractor(GENE_CHECKER,
                new TestProteinResolver(protein),
                event -> event,
                annotation,
                DriverGenesTestFactory.createDriverGenes("BRAF", "KIT"));
    }

    private static class TestProteinResolver implements ProteinResolver {

        @NotNull
        private final String protein;

        public TestProteinResolver(@NotNull final String protein) {
            this.protein = protein;
        }

        @NotNull
        @Override
        public List<Variant> resolve(@NotNull final String gene, @Nullable final String specificTranscript,
                @NotNull final String proteinAnnotation) {
            return proteinAnnotation.equals(protein) ? Lists.newArrayList(TEST_VARIANT) : Lists.newArrayList();
        }

        @NotNull
        @Override
        public Set<String> unresolvedProteinAnnotations() {
            return Sets.newHashSet();
        }
    }
}