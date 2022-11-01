package com.hartwig.serve.extraction.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.hartwig.serve.datamodel.common.ProteinEffect;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ProteinEffectConsolidationTest {

    @Test
    public void canConsolidateProteinEffects() {
        assertEquals(ProteinEffect.UNKNOWN, evaluate(ProteinEffect.UNKNOWN, ProteinEffect.UNKNOWN));
        assertEquals(ProteinEffect.GAIN_OF_FUNCTION, evaluate(ProteinEffect.GAIN_OF_FUNCTION, ProteinEffect.UNKNOWN));
        assertEquals(ProteinEffect.GAIN_OF_FUNCTION, evaluate(ProteinEffect.UNKNOWN, ProteinEffect.GAIN_OF_FUNCTION));
        assertEquals(ProteinEffect.AMBIGUOUS, evaluate(ProteinEffect.AMBIGUOUS, ProteinEffect.GAIN_OF_FUNCTION));
        assertEquals(ProteinEffect.AMBIGUOUS, evaluate(ProteinEffect.GAIN_OF_FUNCTION, ProteinEffect.AMBIGUOUS));

        assertEquals(ProteinEffect.GAIN_OF_FUNCTION, evaluate(ProteinEffect.GAIN_OF_FUNCTION_PREDICTED, ProteinEffect.GAIN_OF_FUNCTION));
        assertEquals(ProteinEffect.GAIN_OF_FUNCTION, evaluate(ProteinEffect.GAIN_OF_FUNCTION, ProteinEffect.GAIN_OF_FUNCTION_PREDICTED));
        assertEquals(ProteinEffect.LOSS_OF_FUNCTION, evaluate(ProteinEffect.LOSS_OF_FUNCTION_PREDICTED, ProteinEffect.LOSS_OF_FUNCTION));
        assertEquals(ProteinEffect.LOSS_OF_FUNCTION, evaluate(ProteinEffect.LOSS_OF_FUNCTION, ProteinEffect.LOSS_OF_FUNCTION_PREDICTED));
        assertEquals(ProteinEffect.NO_EFFECT, evaluate(ProteinEffect.NO_EFFECT_PREDICTED, ProteinEffect.NO_EFFECT));
        assertEquals(ProteinEffect.NO_EFFECT, evaluate(ProteinEffect.NO_EFFECT, ProteinEffect.NO_EFFECT_PREDICTED));
    }

    @Test
    public void canMergeEveryProteinEffect() {
        for (ProteinEffect effect1 : ProteinEffect.values()) {
            for (ProteinEffect effect2 : ProteinEffect.values()) {
                assertNotNull(evaluate(effect1, effect2));
            }
        }
    }

    @NotNull
    private static ProteinEffect evaluate(@NotNull ProteinEffect effect1, @NotNull ProteinEffect effect2) {
        return ProteinEffectConsolidation.merge(effect1, effect2);
    }

}