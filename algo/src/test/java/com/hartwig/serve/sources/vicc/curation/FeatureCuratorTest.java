package com.hartwig.serve.sources.vicc.curation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.hartwig.serve.sources.vicc.ViccTestFactory;
import com.hartwig.serve.vicc.datamodel.Feature;
import com.hartwig.serve.vicc.datamodel.ViccEntry;
import com.hartwig.serve.vicc.datamodel.ViccSource;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class FeatureCuratorTest {

    @Test
    public void canCurateFeatures() {
        FeatureCurationKey firstOncoKbKey = firstOncoKbMappingKey();
        String firstMappedFeature = FeatureCurationFactory.FEATURE_MAPPINGS.get(firstOncoKbKey).featureName();

        ViccEntry entry = ViccTestFactory.testEntryWithSourceAndTranscript(ViccSource.ONCOKB, firstOncoKbKey.transcript());

        Feature feature = ViccTestFactory.testFeatureWithGeneAndName(firstOncoKbKey.gene(), firstOncoKbKey.featureName());

        assertEquals(firstMappedFeature, new FeatureCurator().curate(entry, feature).name());
    }

    @Test
    public void canFilterFeatures() {
        FeatureCurationKey firstOncoKbKey = firstOncoKbFilterKey();
        ViccEntry entry = ViccTestFactory.testEntryWithSourceAndTranscript(ViccSource.ONCOKB, firstOncoKbKey.transcript());

        Feature feature = ViccTestFactory.testFeatureWithGeneAndName(firstOncoKbKey.gene(), firstOncoKbKey.featureName());
        assertNull(new FeatureCurator().curate(entry, feature));
    }

    @Test
    public void canKeepTrackOfFeatures() {
        FeatureCurator curator = new FeatureCurator();

        ViccEntry entry = ViccTestFactory.testEntryWithSourceAndTranscript(ViccSource.ONCOKB, "any");
        Feature feature = ViccTestFactory.testFeatureWithGeneAndName("any", "any");

        assertNotNull(curator.curate(entry, feature));

        FeatureCurationKey filterKey = firstOncoKbFilterKey();
        ViccEntry filterEntry = ViccTestFactory.testEntryWithSourceAndTranscript(ViccSource.ONCOKB, filterKey.transcript());

        Feature filterFeature = ViccTestFactory.testFeatureWithGeneAndName(filterKey.gene(), filterKey.featureName());

        assertNull(curator.curate(filterEntry, filterFeature));

        curator.reportUnusedCurationKeys();
    }

    @NotNull
    private static FeatureCurationKey firstOncoKbMappingKey() {
        for (FeatureCurationKey key : FeatureCurationFactory.FEATURE_MAPPINGS.keySet()) {
            if (key.source() == ViccSource.ONCOKB) {
                return key;
            }
        }
        throw new IllegalStateException("No OncoKB mapping keys found!");
    }

    @NotNull
    private static FeatureCurationKey firstOncoKbFilterKey() {
        for (FeatureCurationKey key : FeatureCurationFactory.FEATURE_FILTERS) {
            if (key.source() == ViccSource.ONCOKB) {
                return key;
            }
        }
        throw new IllegalStateException("No OncoKB filter keys found!");
    }
}