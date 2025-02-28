package com.hartwig.serve.datamodel.serialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.datamodel.ServeDatabase;
import com.hartwig.serve.datamodel.ServeRecord;
import com.hartwig.serve.datamodel.TestServeDatabaseFactory;

import org.jetbrains.annotations.NotNull;
import org.junit.Ignore;
import org.junit.Test;

public class ServeJsonTest {

    private static final String TEST_SERVE_JSON = ServeJsonTest.class.getResource("/example.serve.json").getPath();

    @Test
    public void canRoundTripEmptyDatabase() throws IOException {
        ServeDatabase inputDatabase = TestServeDatabaseFactory.createEmptyDatabase();
        ServeDatabase deserializedDatabase = roundTrip(inputDatabase);

        assertEquals(inputDatabase, deserializedDatabase);
    }

    @Test
    public void canRoundTripMinimalDatabase() throws IOException {
        ServeDatabase inputDatabase = TestServeDatabaseFactory.createMinimalDatabase();
        ServeDatabase deserializedDatabase = roundTrip(inputDatabase);

        assertEquals(inputDatabase, deserializedDatabase);
    }

    @Test
    public void canRoundTripExhaustiveDatabase() throws IOException {
        ServeDatabase inputDatabase = TestServeDatabaseFactory.createExhaustiveDatabase();
        ServeDatabase deserializedDatabase = roundTrip(inputDatabase);

        assertEquals(inputDatabase, deserializedDatabase);
    }

    @Ignore
    @Test
    public void canReadRealServeJson() throws IOException {
        ServeDatabase database = ServeJson.read(TEST_SERVE_JSON);

        assertEquals("local-SNAPSHOT", database.version());
        assertTrue(database.records().containsKey(RefGenome.V37));

        ServeRecord record37 = database.records().get(RefGenome.V37);
        assertEquals(2, record37.knownEvents().hotspots().size());
        assertEquals(2, record37.knownEvents().codons().size());
        assertEquals(2, record37.knownEvents().exons().size());
        assertEquals(1, record37.knownEvents().genes().size());
        assertEquals(2, record37.knownEvents().copyNumbers().size());
        assertEquals(2, record37.knownEvents().fusions().size());
        assertEquals(13, record37.evidences().size());
        assertEquals(7, record37.trials().size());
    }

    @NotNull
    private static ServeDatabase roundTrip(@NotNull ServeDatabase database) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ServeJson.writeToStream(database, outputStream);

        return ServeJson.readFromStream(new ByteArrayInputStream(outputStream.toByteArray()));
    }
}
