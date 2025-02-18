package com.hartwig.serve.extraction.hotspot;

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
import com.hartwig.serve.datamodel.molecular.hotspot.VariantHotspot;
import com.hartwig.serve.extraction.util.DriverInconsistencyMode;
import com.hartwig.serve.extraction.util.GeneChecker;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

public class VariantExtractorTest {

    private static final GeneChecker GENE_CHECKER = new GeneChecker(Sets.newHashSet("BRAF", "KRAS"));

    private static final Variant TEST_VARIANT = ImmutableVariant.builder().chromosome("1").position(10).ref("A").alt("T").build();

    private static final VariantHotspot TEST_BRAF_VARIANT_HOTSPOT =
            ImmutableVariantHotspotImpl.builder().gene("BRAF").chromosome("1").position(10).ref("A").alt("T").build();

    private static final VariantHotspot TEST_KRAS_VARIANT_HOTSPOT =
            ImmutableVariantHotspotImpl.builder().gene("KRAS").chromosome("1").position(10).ref("A").alt("T").build();

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
        List<VariantHotspot> hotspotExtractorFilterList = variantExtractorFilter.extract("BRAF", null, EventType.HOTSPOT, "V600E");
        assertEquals(1, hotspotExtractorFilterList.size());
        assertEquals(TEST_BRAF_VARIANT_HOTSPOT, hotspotExtractorFilterList.get(0));

        VariantExtractor variantExtractorIgnore = createWithProtein(protein, DriverInconsistencyMode.IGNORE);
        List<VariantHotspot> hotspotExtractorIgnoreList = variantExtractorIgnore.extract("BRAF", null, EventType.HOTSPOT, "V600E");
        assertEquals(1, hotspotExtractorIgnoreList.size());
        assertEquals(TEST_BRAF_VARIANT_HOTSPOT, hotspotExtractorIgnoreList.get(0));

        VariantExtractor variantExtractorWarn = createWithProtein(protein, DriverInconsistencyMode.WARN_ONLY);
        List<VariantHotspot> hotspotExtractorWarnList = variantExtractorWarn.extract("BRAF", null, EventType.HOTSPOT, "V600E");
        assertEquals(1, hotspotExtractorWarnList.size());
        assertEquals(TEST_BRAF_VARIANT_HOTSPOT, hotspotExtractorWarnList.get(0));
    }

    @Test
    public void canFilterNotInCatalog() {
        String protein = "V600E";
        VariantExtractor variantExtractorFilter = createWithProtein(protein, DriverInconsistencyMode.FILTER);
        List<VariantHotspot> hotspotExtractorFilterList = variantExtractorFilter.extract("KRAS", null, EventType.HOTSPOT, "V600E");
        assertNull(hotspotExtractorFilterList);

        VariantExtractor variantExtractorIgnore = createWithProtein(protein, DriverInconsistencyMode.IGNORE);
        List<VariantHotspot> hotspotExtractorIgnoreList = variantExtractorIgnore.extract("KRAS", null, EventType.HOTSPOT, "V600E");
        assertEquals(1, hotspotExtractorIgnoreList.size());
        assertEquals(TEST_KRAS_VARIANT_HOTSPOT, hotspotExtractorIgnoreList.get(0));

        VariantExtractor variantExtractorWarn = createWithProtein(protein, DriverInconsistencyMode.WARN_ONLY);
        List<VariantHotspot> hotspotExtractorWarnList = variantExtractorWarn.extract("KRAS", null, EventType.HOTSPOT, "V600E");
        assertEquals(1, hotspotExtractorWarnList.size());
        assertEquals(TEST_KRAS_VARIANT_HOTSPOT, hotspotExtractorWarnList.get(0));
    }

    @Test
    public void canExtractSimpleHotspot() {
        String protein = "V600E";

        VariantExtractor variantExtractor = createWithProtein(protein, DriverInconsistencyMode.IGNORE);
        List<VariantHotspot> hotspots = variantExtractor.extract("BRAF", null, EventType.HOTSPOT, "V600E");

        assertEquals(1, hotspots.size());
        assertEquals(TEST_BRAF_VARIANT_HOTSPOT, hotspots.get(0));
    }

    @Test
    public void skipsHotspotsForInvalidGenes() {
        String protein = "V600E";

        VariantExtractor variantExtractor = createWithProtein(protein, DriverInconsistencyMode.IGNORE);

        assertNull(variantExtractor.extract("NOT-A-GENE", null, EventType.HOTSPOT, "V600E"));
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