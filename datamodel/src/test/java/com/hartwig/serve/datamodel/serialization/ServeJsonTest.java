package com.hartwig.serve.datamodel.serialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.google.common.io.Resources;
import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.datamodel.ServeDatabase;
import com.hartwig.serve.datamodel.TestServeDatabaseFactory;

import org.junit.Test;

public class ServeJsonTest {

    private static final String TEST_SERVE_JSON = Resources.getResource("example.serve.json").getPath();

    @Test
    public void canRoundTripServeJson() throws IOException {
        ServeDatabase database = TestServeDatabaseFactory.create();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ServeJson.writeToStream(database, outputStream);
        ServeDatabase deserializedRecord = ServeJson.readFromStream(new ByteArrayInputStream(outputStream.toByteArray()));

        assertEquals(database, deserializedRecord);
    }

    @Test
    public void canReadRealServeJson() throws IOException {
        ServeDatabase record = ServeJson.read(TEST_SERVE_JSON);

        assertEquals("local-SNAPSHOT", record.version());
        assertTrue(record.records().containsKey(RefGenome.V37));
        assertEquals(2, record.records().get(RefGenome.V37).knownEvents().hotspots().size());
        assertEquals(13, record.records().get(RefGenome.V37).efficacyEvidences().size());
        assertEquals(7, record.records().get(RefGenome.V37).clinicalTrials().size());
    }
}
