package com.hartwig.serve.vicc.reader;

import static com.hartwig.serve.common.json.Json.nullableString;
import static com.hartwig.serve.common.json.Json.string;
import static com.hartwig.serve.vicc.util.ViccJson.optionalOrNullableArray;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hartwig.serve.common.json.JsonDatamodelChecker;
import com.hartwig.serve.vicc.datamodel.jax.ImmutableJax;
import com.hartwig.serve.vicc.datamodel.jax.ImmutableJaxIndication;
import com.hartwig.serve.vicc.datamodel.jax.ImmutableJaxMolecularProfile;
import com.hartwig.serve.vicc.datamodel.jax.ImmutableJaxReference;
import com.hartwig.serve.vicc.datamodel.jax.ImmutableJaxTherapy;
import com.hartwig.serve.vicc.datamodel.jax.Jax;
import com.hartwig.serve.vicc.datamodel.jax.JaxIndication;
import com.hartwig.serve.vicc.datamodel.jax.JaxMolecularProfile;
import com.hartwig.serve.vicc.datamodel.jax.JaxReference;
import com.hartwig.serve.vicc.datamodel.jax.JaxTherapy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class JaxObjectFactory {

    private JaxObjectFactory() {
    }

    @NotNull
    static Jax create(@NotNull JsonObject jaxEntryObject) {
        ViccDatamodelCheckerFactory.jaxEntryChecker().check(jaxEntryObject);

        return ImmutableJax.builder()
                .molecularProfile(createMolecularProfile(jaxEntryObject.getAsJsonObject("molecularProfile")))
                .therapy(createJaxTherapy(jaxEntryObject.getAsJsonObject("therapy")))
                .responseType(string(jaxEntryObject, "responseType"))
                .evidenceType(string(jaxEntryObject, "evidenceType"))
                .approvalStatus(string(jaxEntryObject, "approvalStatus"))
                .efficacyEvidence(string(jaxEntryObject, "efficacyEvidence"))
                .indication(createJaxIndication(jaxEntryObject.getAsJsonObject("indication")))
                .references(createJaxReferences(optionalOrNullableArray(jaxEntryObject, "references")))
                .id(string(jaxEntryObject, "id"))
                .build();
    }

    @NotNull
    private static JaxMolecularProfile createMolecularProfile(@NotNull JsonObject molecularProfileObject) {
        ViccDatamodelCheckerFactory.jaxMolecularProfileChecker().check(molecularProfileObject);

        return ImmutableJaxMolecularProfile.builder()
                .profileName(string(molecularProfileObject, "profileName"))
                .id(string(molecularProfileObject, "id"))
                .build();
    }

    @NotNull
    private static JaxTherapy createJaxTherapy(@NotNull JsonObject therapyObject) {
        ViccDatamodelCheckerFactory.jaxTherapyChecker().check(therapyObject);

        return ImmutableJaxTherapy.builder().id(string(therapyObject, "id")).therapyName(string(therapyObject, "therapyName")).build();
    }

    @NotNull
    private static JaxIndication createJaxIndication(@NotNull JsonObject indicationObject) {
        ViccDatamodelCheckerFactory.jaxIndicationChecker().check(indicationObject);

        return ImmutableJaxIndication.builder()
                .source(string(indicationObject, "source"))
                .id(string(indicationObject, "id"))
                .name(string(indicationObject, "name"))
                .build();
    }

    @NotNull
    private static List<JaxReference> createJaxReferences(@Nullable JsonArray referenceArray) {
        if (referenceArray == null) {
            return Lists.newArrayList();
        }

        JsonDatamodelChecker referenceChecker = ViccDatamodelCheckerFactory.jaxReferenceChecker();
        List<JaxReference> referenceList = Lists.newArrayList();

        for (JsonElement referenceElement : referenceArray) {
            JsonObject referenceObject = referenceElement.getAsJsonObject();
            referenceChecker.check(referenceObject);

            referenceList.add(ImmutableJaxReference.builder()
                    .url(string(referenceObject, "url"))
                    .id(string(referenceObject, "id"))
                    .pubMedId(nullableString(referenceObject, "pubMedId"))
                    .title(string(referenceObject, "title"))
                    .build());
        }
        return referenceList;
    }
}
