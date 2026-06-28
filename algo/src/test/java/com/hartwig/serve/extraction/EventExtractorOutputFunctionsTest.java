package com.hartwig.serve.extraction;

import static org.junit.Assert.assertEquals;

import com.hartwig.serve.datamodel.molecular.characteristic.CharacteristicTestFactory;
import com.hartwig.serve.datamodel.molecular.characteristic.TumorCharacteristicType;
import com.hartwig.serve.datamodel.molecular.fusion.FusionTestFactory;
import com.hartwig.serve.datamodel.molecular.gene.GeneTestFactory;
import com.hartwig.serve.datamodel.molecular.hotspot.ImmutableVariantAnnotation;
import com.hartwig.serve.datamodel.molecular.hotspot.VariantAnnotation;
import com.hartwig.serve.datamodel.molecular.range.RangeTestFactory;
import com.hartwig.serve.extraction.codon.CodonAnnotation;
import com.hartwig.serve.extraction.codon.ImmutableCodonAnnotation;
import com.hartwig.serve.extraction.exon.ExonAnnotation;
import com.hartwig.serve.extraction.exon.ImmutableExonAnnotation;
import com.hartwig.serve.extraction.immuno.ImmutableImmunoHLA;

import org.junit.Test;

public class EventExtractorOutputFunctionsTest {

    private static final VariantAnnotation TEST_VARIANT_1 =
            ImmutableVariantAnnotation.builder().gene("A").chromosome("1").position(1).ref("A").alt("T").build();
    private static final VariantAnnotation TEST_VARIANT_2 =
            ImmutableVariantAnnotation.builder().gene("B").chromosome("2").position(1).ref("A").alt("T").build();
    
    private static final CodonAnnotation TEST_CODON_1 = ImmutableCodonAnnotation.builder()
            .from(RangeTestFactory.createTestRangeAnnotation())
            .inputTranscript("1")
            .inputCodonRank(1)
            .build();
    private static final CodonAnnotation TEST_CODON_2 = ImmutableCodonAnnotation.builder()
            .from(RangeTestFactory.createTestRangeAnnotation())
            .inputTranscript("2")
            .inputCodonRank(2)
            .build();

    private static final ExonAnnotation TEST_EXON_1 = ImmutableExonAnnotation.builder()
            .from(RangeTestFactory.createTestRangeAnnotation())
            .inputTranscript("1")
            .inputExonRank(1)
            .build();
    private static final ExonAnnotation TEST_EXON_2 = ImmutableExonAnnotation.builder()
            .from(RangeTestFactory.createTestRangeAnnotation())
            .inputTranscript("2")
            .inputExonRank(2)
            .build();

    @Test
    public void canCountEvents() {
        ImmutableEventExtractorOutput.Builder builder = ImmutableEventExtractorOutput.builder();
        assertEquals(0, EventExtractorOutputFunctions.eventCount(builder.build()));

        builder.addVariants(TEST_VARIANT_1, TEST_VARIANT_2);
        assertEquals(2, EventExtractorOutputFunctions.eventCount(builder.build()));
        
        builder.addCodons(TEST_CODON_1, TEST_CODON_2);
        assertEquals(4, EventExtractorOutputFunctions.eventCount(builder.build()));

        builder.addExons(TEST_EXON_1, TEST_EXON_2);
        assertEquals(6, EventExtractorOutputFunctions.eventCount(builder.build()));
        
        builder.geneLevel(GeneTestFactory.createTestGeneAnnotation());
        assertEquals(7, EventExtractorOutputFunctions.eventCount(builder.build()));

        builder.copyNumber(GeneTestFactory.createTestGeneAnnotation());
        assertEquals(8, EventExtractorOutputFunctions.eventCount(builder.build()));

        builder.fusionPair(FusionTestFactory.createFusionPair("up", "down", null, null));
        assertEquals(9, EventExtractorOutputFunctions.eventCount(builder.build()));
        
        builder.characteristic(CharacteristicTestFactory.createTumorCharacteristic(TumorCharacteristicType.EBV_POSITIVE, null, null));
        assertEquals(10, EventExtractorOutputFunctions.eventCount(builder.build()));
        
        builder.hla(ImmutableImmunoHLA.builder().gene("1").alleleGroup("group").build());
        assertEquals(11, EventExtractorOutputFunctions.eventCount(builder.build()));
    }
}