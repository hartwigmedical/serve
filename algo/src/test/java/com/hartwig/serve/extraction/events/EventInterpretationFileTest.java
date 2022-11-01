package com.hartwig.serve.extraction.events;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import com.google.common.io.Resources;

import org.junit.Test;

public class EventInterpretationFileTest {

    private static final String TEST_EVENT_INTERPRETATION_DIR = Resources.getResource("event_interpretation").getPath();

    @Test
    public void canReadFromFileAndConvert() throws IOException {
        String eventInterpretationTsv = EventInterpretationFile.eventInterpretationTsv(TEST_EVENT_INTERPRETATION_DIR);
        List<EventInterpretation> eventInterpretations = EventInterpretationFile.read(eventInterpretationTsv);

        assertEquals(2, eventInterpretations.size());

        List<EventInterpretation> regeneratedEvents =
                EventInterpretationFile.fromLines(EventInterpretationFile.toLines(eventInterpretations));

        assertEquals(eventInterpretations, regeneratedEvents);
    }
}