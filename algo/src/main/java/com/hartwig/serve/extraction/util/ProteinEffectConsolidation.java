package com.hartwig.serve.extraction.util;

import com.hartwig.serve.datamodel.common.ProteinEffect;

import org.jetbrains.annotations.NotNull;

public final class ProteinEffectConsolidation {

    private ProteinEffectConsolidation() {
    }

    @NotNull
    public static ProteinEffect merge(@NotNull ProteinEffect effect1, @NotNull ProteinEffect effect2) {
        if (effect1 == effect2) {
            return effect1;
        }

        if (effect1 == ProteinEffect.UNKNOWN) {
            return effect2;
        } else if (effect2 == ProteinEffect.UNKNOWN) {
            return effect1;
        }

        if (effect1 == ProteinEffect.AMBIGUOUS || effect2 == ProteinEffect.AMBIGUOUS) {
            return ProteinEffect.AMBIGUOUS;
        }

        switch (effect1) {
            case NO_EFFECT: {
                return effect2 == ProteinEffect.NO_EFFECT_PREDICTED ? ProteinEffect.NO_EFFECT : ProteinEffect.AMBIGUOUS;
            }
            case NO_EFFECT_PREDICTED: {
                return effect2 == ProteinEffect.NO_EFFECT ? ProteinEffect.NO_EFFECT : ProteinEffect.AMBIGUOUS;
            }
            case GAIN_OF_FUNCTION: {
                return effect2 == ProteinEffect.GAIN_OF_FUNCTION_PREDICTED ? ProteinEffect.GAIN_OF_FUNCTION : ProteinEffect.AMBIGUOUS;
            }
            case GAIN_OF_FUNCTION_PREDICTED: {
                return effect2 == ProteinEffect.GAIN_OF_FUNCTION ? ProteinEffect.GAIN_OF_FUNCTION : ProteinEffect.AMBIGUOUS;
            }
            case LOSS_OF_FUNCTION: {
                return effect2 == ProteinEffect.LOSS_OF_FUNCTION_PREDICTED ? ProteinEffect.LOSS_OF_FUNCTION : ProteinEffect.AMBIGUOUS;
            }
            case LOSS_OF_FUNCTION_PREDICTED: {
                return effect2 == ProteinEffect.LOSS_OF_FUNCTION ? ProteinEffect.LOSS_OF_FUNCTION : ProteinEffect.AMBIGUOUS;
            }
            default: {
                throw new IllegalStateException("Could not consolidate protein effects: " + effect1 + " and " + effect2);
            }
        }
    }

}
