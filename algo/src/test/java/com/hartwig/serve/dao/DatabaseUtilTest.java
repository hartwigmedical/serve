package com.hartwig.serve.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashSet;
import java.util.Set;

import com.hartwig.serve.datamodel.common.CancerType;
import com.hartwig.serve.datamodel.common.DatamodelTestFactory;
import com.hartwig.serve.datamodel.common.Indication;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class DatabaseUtilTest {

    @Test
    public void shouldConvertBooleanToByte() {
        Byte isTrue = 1;
        assertEquals(isTrue, DatabaseUtil.toByte(true));

        Byte isFalse = 0;
        assertEquals(isFalse, DatabaseUtil.toByte(false));

        assertNull(DatabaseUtil.toByte(null));
    }

    @Test
    public void shouldConcatStrings() {
        assertEquals("", DatabaseUtil.concat(new HashSet<>()));
        assertEquals("test", DatabaseUtil.concat(Set.of("test")));
        assertEquals("test, test2", DatabaseUtil.concat(Set.of("test", "test2")));
        assertEquals("test, test2", DatabaseUtil.concat(Set.of("test2", "test")));
    }

    @Test
    public void shouldFormatIndication() {
        CancerType applicable = DatamodelTestFactory.cancerTypeBuilder().name("name").doid("doid").build();
        assertEquals("name (doid)", DatabaseUtil.formatIndication(withCancerType(applicable)));

        CancerType excluded1 = DatamodelTestFactory.cancerTypeBuilder().name("excluded1").doid("doid").build();
        CancerType excluded2 = DatamodelTestFactory.cancerTypeBuilder().name("excluded2").doid("doid").build();
        Indication complex = withCancerTypeAndExcluded(applicable, excluded1, excluded2);
        assertEquals("name (doid) (excluding excluded1 (doid), excluded2 (doid))", DatabaseUtil.formatIndication(complex));
    }

    @NotNull
    private static Indication withCancerType(@NotNull CancerType applicable) {
        return DatamodelTestFactory.indicationBuilder().applicableType(applicable).build();
    }

    @NotNull
    private static Indication withCancerTypeAndExcluded(@NotNull CancerType applicable, @NotNull CancerType excluded1,
            @NotNull CancerType excluded2) {
        return DatamodelTestFactory.indicationBuilder().applicableType(applicable).addExcludedSubTypes(excluded1, excluded2).build();
    }
}