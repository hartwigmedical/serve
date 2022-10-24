package com.hartwig.serve.datamodel.util;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.ActionableEvent;
import com.hartwig.serve.datamodel.DatamodelTestBuilders;
import com.hartwig.serve.datamodel.DatamodelTestFactory;
import com.hartwig.serve.datamodel.EvidenceDirection;
import com.hartwig.serve.datamodel.EvidenceLevel;
import com.hartwig.serve.datamodel.Knowledgebase;

import org.junit.Test;

public class ActionableFileFunctionsTest {

    @Test
    public void canConvertActionableEvents() {
        ActionableEvent event = DatamodelTestFactory.createEvent(Knowledgebase.VICC_CGI,
                "source event",
                Sets.newHashSet(),
                DatamodelTestBuilders.treatmentBuilder()
                        .treament("treatment")
                        .addSourceRelevantTreatmentApproaches("drug classes")
                        .addRelevantTreatmentApproaches("drug classes")
                        .build(),
                DatamodelTestBuilders.cancerTypeBuilder().name("applicable name").doid("applicable doid").build(),
                Sets.newHashSet(DatamodelTestBuilders.cancerTypeBuilder().name("blacklist name").doid("blacklist doid").build()),
                EvidenceLevel.C,
                EvidenceDirection.RESISTANT,
                Sets.newHashSet("url1", "url2"));

        String line = ActionableFileFunctions.toLine(event);
        ActionableEvent convertedEvent = ActionableFileFunctions.fromLine(line.split(ActionableFileFunctions.FIELD_DELIMITER), 0);

        assertEquals(Knowledgebase.VICC_CGI, convertedEvent.source());
        assertEquals("treatment", convertedEvent.treatment().treament());
        assertEquals("applicable name", convertedEvent.applicableCancerType().name());
        assertEquals("applicable doid", convertedEvent.applicableCancerType().doid());
        assertEquals(EvidenceLevel.C, convertedEvent.level());
        assertEquals(EvidenceDirection.RESISTANT, convertedEvent.direction());
        assertEquals(Sets.newHashSet("url1", "url2"), convertedEvent.evidenceUrls());
    }
}