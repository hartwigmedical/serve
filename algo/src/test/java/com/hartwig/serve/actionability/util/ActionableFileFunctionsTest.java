package com.hartwig.serve.actionability.util;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.Sets;
import com.hartwig.serve.actionability.ActionabilityTestUtil;
import com.hartwig.serve.actionability.ActionableEvent;
import com.hartwig.serve.cancertype.ImmutableCancerType;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.actionability.EvidenceDirection;
import com.hartwig.serve.datamodel.actionability.EvidenceLevel;
import com.hartwig.serve.datamodel.actionability.ImmutableTreatment;

import org.junit.Assert;
import org.junit.Test;

public class ActionableFileFunctionsTest {

    @Test
    public void canConvertActionableEvents() {
        ActionableEvent event = ActionabilityTestUtil.create(Knowledgebase.VICC_CGI,
                "source event",
                Sets.newHashSet(),
                ImmutableTreatment.builder()
                        .treament("treatment")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet("drugClasses"))
                        .relevantTreatmentApproaches(Sets.newHashSet("drugClasses"))
                        .build(),
                ImmutableCancerType.builder().name("applicable name").doid("applicable doid").build(),
                Sets.newHashSet(ImmutableCancerType.builder().name("blacklist name").doid("blacklist doid").build()),
                EvidenceLevel.C,
                EvidenceDirection.RESISTANT,
                Sets.newHashSet("url1", "url2"));

        String line = ActionableFileFunctions.toLine(event);
        ActionableEvent convertedEvent = ActionableFileFunctions.fromLine(line.split(ActionableFileFunctions.FIELD_DELIMITER), 0);

        assertEquals(Knowledgebase.VICC_CGI, convertedEvent.source());
        assertEquals("treatment", convertedEvent.treatment().treament());
        Assert.assertEquals("applicable name", convertedEvent.applicableCancerType().name());
        Assert.assertEquals("applicable doid", convertedEvent.applicableCancerType().doid());
        assertEquals(EvidenceLevel.C, convertedEvent.level());
        assertEquals(EvidenceDirection.RESISTANT, convertedEvent.direction());
        assertEquals(Sets.newHashSet("url1", "url2"), convertedEvent.evidenceUrls());
    }
}