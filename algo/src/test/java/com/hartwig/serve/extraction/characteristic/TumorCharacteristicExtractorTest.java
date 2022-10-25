package com.hartwig.serve.extraction.characteristic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.google.common.collect.Sets;
import com.hartwig.serve.common.classification.EventType;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristic;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristicCutoffType;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristicType;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class TumorCharacteristicExtractorTest {

    private static final double EPSILON = 1e-10;

    private static final String MSI = "msi";
    private static final String MSS = "mss";
    private static final String HIGH_TML = "high_tml";
    private static final String LOW_TML = "low_tml";
    private static final String HIGH_TMB = "high_tmb";
    private static final String LOW_TMB = "low_tmb";
    private static final String HRD = "hrd";
    private static final String HPV = "hpv";
    private static final String EBV = "ebv";

    @Test
    public void canDetermineMSI() {
        TumorCharacteristicExtractor extractor = buildTestExtractor();

        TumorCharacteristic characteristic = extractor.extract(EventType.CHARACTERISTIC, MSI);

        assertEquals(TumorCharacteristicType.MICROSATELLITE_UNSTABLE, characteristic.type());
        assertNull(characteristic.cutoffType());
        assertNull(characteristic.cutoff());
    }

    @Test
    public void canDetermineMSS() {
        TumorCharacteristicExtractor extractor = buildTestExtractor();

        TumorCharacteristic characteristic = extractor.extract(EventType.CHARACTERISTIC, MSS);

        assertEquals(TumorCharacteristicType.MICROSATELLITE_STABLE, characteristic.type());
        assertNull(characteristic.cutoffType());
        assertNull(characteristic.cutoff());
    }

    @Test
    public void canDetermineTMLLow() {
        TumorCharacteristicExtractor extractor = buildTestExtractor();

        TumorCharacteristic characteristic = extractor.extract(EventType.CHARACTERISTIC, LOW_TML + " < 140");

        assertEquals(TumorCharacteristicType.LOW_TUMOR_MUTATIONAL_LOAD, characteristic.type());
        assertEquals(TumorCharacteristicCutoffType.LOWER, characteristic.cutoffType());
        assertEquals(140, characteristic.cutoff(), EPSILON);
    }

    @Test
    public void canDetermineTMLHigh() {
        TumorCharacteristicExtractor extractor = buildTestExtractor();

        TumorCharacteristic characteristic = extractor.extract(EventType.CHARACTERISTIC, HIGH_TML + " >= 140");

        assertEquals(TumorCharacteristicType.HIGH_TUMOR_MUTATIONAL_LOAD, characteristic.type());
        assertEquals(TumorCharacteristicCutoffType.EQUAL_OR_GREATER, characteristic.cutoffType());
        assertEquals(140, characteristic.cutoff(), EPSILON);
    }

    @Test
    public void canDetermineTMBLow() {
        TumorCharacteristicExtractor extractor = buildTestExtractor();

        TumorCharacteristic characteristic = extractor.extract(EventType.CHARACTERISTIC, LOW_TMB + " <= 3");

        assertEquals(TumorCharacteristicType.LOW_TUMOR_MUTATIONAL_BURDEN, characteristic.type());
        assertEquals(TumorCharacteristicCutoffType.EQUAL_OR_LOWER, characteristic.cutoffType());
        assertEquals(3, characteristic.cutoff(), EPSILON);
    }

    @Test
    public void canDetermineTMBHigh() {
        TumorCharacteristicExtractor extractor = buildTestExtractor();

        TumorCharacteristic characteristic = extractor.extract(EventType.CHARACTERISTIC, HIGH_TMB + " > 14.5");

        assertEquals(TumorCharacteristicType.HIGH_TUMOR_MUTATIONAL_BURDEN, characteristic.type());
        assertEquals(TumorCharacteristicCutoffType.GREATER, characteristic.cutoffType());
        assertEquals(14.5, characteristic.cutoff(), EPSILON);
    }

    @Test
    public void canDetermineHRD() {
        TumorCharacteristicExtractor extractor = buildTestExtractor();

        TumorCharacteristic characteristic = extractor.extract(EventType.CHARACTERISTIC, HRD);

        assertEquals(TumorCharacteristicType.HOMOLOGOUS_RECOMBINATION_DEFICIENT, characteristic.type());
        assertNull(characteristic.cutoffType());
        assertNull(characteristic.cutoff());
    }

    @Test
    public void canExtractHPVPositiveCharacteristic() {
        TumorCharacteristicExtractor extractor = buildTestExtractor();

        TumorCharacteristic characteristic = extractor.extract(EventType.CHARACTERISTIC, HPV);

        assertEquals(TumorCharacteristicType.HPV_POSITIVE, characteristic.type());
        assertNull(characteristic.cutoffType());
        assertNull(characteristic.cutoff());
    }

    @Test
    public void canExtractEBVPositiveCharacteristic() {
        TumorCharacteristicExtractor extractor = buildTestExtractor();

        TumorCharacteristic characteristic = extractor.extract(EventType.CHARACTERISTIC, EBV);

        assertEquals(TumorCharacteristicType.EBV_POSITIVE, characteristic.type());
        assertNull(characteristic.cutoffType());
        assertNull(characteristic.cutoff());
    }

    @Test
    public void canFilterUnknownCharacteristic() {
        TumorCharacteristicExtractor tumorCharacteristicExtractor = buildTestExtractor();

        assertNull(tumorCharacteristicExtractor.extract(EventType.CHARACTERISTIC, "Not a tumor characteristic"));
    }

    @Test
    public void canFilterWrongTypes() {
        TumorCharacteristicExtractor tumorCharacteristicExtractor = buildTestExtractor();

        assertNull(tumorCharacteristicExtractor.extract(EventType.COMPLEX, MSI));
    }

    @NotNull
    private static TumorCharacteristicExtractor buildTestExtractor() {
        return new TumorCharacteristicExtractor(Sets.newHashSet(MSI),
                Sets.newHashSet(MSS),
                Sets.newHashSet(HIGH_TML),
                Sets.newHashSet(LOW_TML),
                Sets.newHashSet(HIGH_TMB),
                Sets.newHashSet(LOW_TMB),
                Sets.newHashSet(HRD),
                Sets.newHashSet(HPV),
                Sets.newHashSet(EBV));
    }
}