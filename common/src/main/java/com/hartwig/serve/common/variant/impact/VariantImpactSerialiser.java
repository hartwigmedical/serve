package com.hartwig.serve.common.variant.impact;

import java.util.List;

import com.hartwig.serve.common.variant.CodingEffect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import htsjdk.variant.variantcontext.VariantContext;

public final class VariantImpactSerialiser {

    private static final Logger LOGGER = LogManager.getLogger(VariantImpactSerialiser.class);

    private static final String VAR_IMPACT = "IMPACT";

    private VariantImpactSerialiser() {
    }

    @NotNull
    public static VariantImpact fromVariantContext(@NotNull VariantContext context) {
        return fromAttributeValues(context.getAttributeAsStringList(VAR_IMPACT, ""));
    }

    @NotNull
    public static VariantImpact fromAttributeValues(@NotNull List<String> impactValues) {
        if (impactValues.size() != 10) {
            LOGGER.warn("Could not resolve variant impact from values: " + impactValues);

            return ImmutableVariantImpact.builder()
                    .canonicalGeneName(Strings.EMPTY)
                    .canonicalEffect(Strings.EMPTY)
                    .canonicalTranscript(Strings.EMPTY)
                    .canonicalCodingEffect(CodingEffect.UNDEFINED)
                    .canonicalHgvsCoding(Strings.EMPTY)
                    .canonicalHgvsProtein(Strings.EMPTY)
                    .canonicalSpliceRegion(false)
                    .otherReportableEffects(Strings.EMPTY)
                    .worstCodingEffect(CodingEffect.UNDEFINED)
                    .genesAffected(0)
                    .build();
        }

        int index = 0;
        String canonicalGeneName = impactValues.get(index++);
        String canonicalTranscript = impactValues.get(index++);
        String canonicalEffect = impactValues.get(index++);
        CodingEffect canonicalCodingEffect = CodingEffect.valueOf(impactValues.get(index++));

        boolean canonicalSpliceRegion = Boolean.parseBoolean(impactValues.get(index++));
        String canonicalHgvsCodingImpact = impactValues.get(index++);
        String canonicalHgvsProteinImpact = impactValues.get(index++);

        String otherReportableEffects = impactValues.get(index++);

        CodingEffect worstCodingEffect = CodingEffect.valueOf(impactValues.get(index++));
        int genesAffected = Integer.parseInt(impactValues.get(index));

        return ImmutableVariantImpact.builder()
                .canonicalGeneName(canonicalGeneName)
                .canonicalEffect(canonicalEffect)
                .canonicalTranscript(canonicalTranscript)
                .canonicalCodingEffect(canonicalCodingEffect)
                .canonicalHgvsCoding(canonicalHgvsCodingImpact)
                .canonicalHgvsProtein(canonicalHgvsProteinImpact)
                .canonicalSpliceRegion(canonicalSpliceRegion)
                .otherReportableEffects(otherReportableEffects)
                .worstCodingEffect(worstCodingEffect)
                .genesAffected(genesAffected)
                .build();
    }
}
