package com.hartwig.serve.datamodel.serialization;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.datamodel.ServeRecord;
import com.hartwig.serve.datamodel.TestServeRecordFactory;

import org.junit.Test;

public class ServeJsonTest {

    public static final String TEST_SERVE_JSON = Resources.getResource("serve.37.json").getPath();

    @Test
    public void canRoundTripServeJson() throws IOException {
        ServeRecord record = TestServeRecordFactory.create();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ServeJson.writeToStream(record, outputStream);
        ServeRecord deserializedRecord = ServeJson.readFromStream(new ByteArrayInputStream(outputStream.toByteArray()));

        assertEquals(record, deserializedRecord);
    }

    @Test
    public void canReadRealServeJson() throws IOException {
        ServeRecord record = ServeJson.read(TEST_SERVE_JSON);

        assertEquals(RefGenome.V37, record.refGenomeVersion());
        assertEquals("local-SNAPSHOT", record.serveVersion());
        assertEquals(2, record.knownEvents().hotspots().size());
        assertEquals(2, record.actionableEvents().hotspots().size());
    }
}
