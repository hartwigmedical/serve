package com.hartwig.serve.datamodel.serialization;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.hartwig.serve.datamodel.ServeRecord;
import com.hartwig.serve.datamodel.TestServeRecordFactory;

import org.junit.Test;

public class ServeJsonTest {

    @Test
    public void testJson() throws IOException {
        ServeRecord record = TestServeRecordFactory.create();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ServeJson.writeToStream(record, outputStream);
        ServeRecord deserializedRecord = ServeJson.readFromStream(new ByteArrayInputStream(outputStream.toByteArray()));

        assertEquals(record, deserializedRecord);
    }
}
