package com.hartwig.serve.datamodel.serialization;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.DatamodelTestFactory;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.immuno.ActionableHLA;
import com.hartwig.serve.datamodel.serialization.util.ActionableFileUtil;
import com.hartwig.serve.datamodel.serialization.util.SerializationUtil;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ActionableHotspotFileTest {

    private static final String ACTIONABLE_HOTSPOT_TSV = Resources.getResource("actionable/ActionableHotspots.37.tsv").getPath();

    @Test
    public void canReadFromFileAndConvert() throws IOException {
        List<ActionableHotspot> hotspots = ActionableHotspotFile.read(ACTIONABLE_HOTSPOT_TSV);

        assertActionableHotspots(hotspots);

        Map<String, Integer> fields = SerializationUtil.createFields(ActionableHotspotFile.header(), ActionableFileUtil.FIELD_DELIMITER);
        List<ActionableHotspot> regeneratedHotspots = ActionableHotspotFile.fromLines(ActionableHotspotFile.toLines(hotspots), fields);

        assertEquals(hotspots, regeneratedHotspots);
    }

    private static void assertActionableHotspots(@NotNull List<ActionableHotspot> hotspots) {
        assertEquals(2, hotspots.size());

        ActionableHotspot hotspot1 = findBySource(hotspots, Knowledgebase.CKB_EVIDENCE);
        assertEquals("BRAF", hotspot1.gene());
        assertEquals("7", hotspot1.chromosome());
        assertEquals(140453136, hotspot1.position());
        assertEquals("A", hotspot1.ref());
        assertEquals("T", hotspot1.alt());
        assertEquals("Vemurafenib", DatamodelTestFactory.treatmentBuilder(hotspot1).name());
        assertEquals("Skin Melanoma", hotspot1.applicableCancerType().name());

        ActionableHotspot hotspot2 = findBySource(hotspots, Knowledgebase.CKB_TRIAL);
        assertEquals("BRAF", hotspot2.gene());
        assertEquals("7", hotspot2.chromosome());
        assertEquals(140453137, hotspot2.position());
        assertEquals("A", hotspot2.ref());
        assertEquals("T", hotspot2.alt());
        assertEquals("Vemurafenib", DatamodelTestFactory.clinicalTrialBuilder(hotspot2).therapyName());
        assertEquals("Solid tumor", hotspot2.applicableCancerType().name());
    }

    @NotNull
    private static ActionableHotspot findBySource(@NotNull List<ActionableHotspot> hotspots,
            @NotNull Knowledgebase sourceToFind) {
        for (ActionableHotspot hotspot : hotspots) {
            if (hotspot.source() == sourceToFind) {
                return hotspot;
            }
        }

        throw new IllegalStateException("Could not find actionable characteristic with source: " + sourceToFind);
    }
}