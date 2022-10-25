package com.hartwig.serve.common.variant.impact;

import static com.hartwig.serve.common.variant.CodingEffect.UNDEFINED;

import java.util.List;

import com.hartwig.serve.common.variant.CodingEffect;

import org.jetbrains.annotations.NotNull;

import htsjdk.variant.variantcontext.VariantContext;

public final class VariantImpactSerialiser {

    private VariantImpactSerialiser() {
    }

    public static final String VAR_IMPACT = "IMPACT";

    @NotNull
    public static VariantImpact fromVariantContext(@NotNull VariantContext context) {
        return fromAttributeValues(context.getAttributeAsStringList(VAR_IMPACT, ""));
    }

    @NotNull
    public static VariantImpact fromAttributeValues(@NotNull List<String> impactValues) {
        if (impactValues.size() != 10) {
            return new VariantImpact("", "", "", UNDEFINED, "", "", false, "", UNDEFINED, 0);
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

        return new VariantImpact(canonicalGeneName,
                canonicalTranscript,
                canonicalEffect,
                canonicalCodingEffect,
                canonicalHgvsCodingImpact,
                canonicalHgvsProteinImpact,
                canonicalSpliceRegion,
                otherReportableEffects,
                worstCodingEffect,
                genesAffected);
    }
}
