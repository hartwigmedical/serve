package com.hartwig.serve.ckb.json.treatmentapproach;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hartwig.serve.ckb.json.CkbJsonDirectoryReader;
import com.hartwig.serve.ckb.json.common.DrugClassInfo;
import com.hartwig.serve.ckb.json.common.ImmutableDrugClassInfo;
import com.hartwig.serve.ckb.json.common.ImmutableReferenceInfo;
import com.hartwig.serve.ckb.json.common.ImmutableTherapyInfo;
import com.hartwig.serve.ckb.json.common.ReferenceInfo;
import com.hartwig.serve.ckb.json.common.TherapyInfo;
import com.hartwig.serve.ckb.util.DateConverter;
import com.hartwig.serve.common.json.JsonDatamodelChecker;
import com.hartwig.serve.common.json.JsonFunctions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TreatmentApproachReader extends CkbJsonDirectoryReader<JsonTreatmentApproach> {

    public TreatmentApproachReader(@Nullable final Integer maxFilesToRead) {
        super(maxFilesToRead);
    }

    @NotNull
    @Override
    protected JsonTreatmentApproach read(@NotNull final JsonObject object) {
        JsonDatamodelChecker treatmentApproachObjectChecker = TreatmentApproachDataModelChecker.treatmentApproachObjectChecker();
        treatmentApproachObjectChecker.check(object);

        return ImmutableJsonTreatmentApproach.builder()
                .id(JsonFunctions.integer(object, "id"))
                .name(JsonFunctions.string(object, "name"))
                .profileName(JsonFunctions.string(object, "profileName"))
                .drugClass(object.has("drugClass") && !object.get("drugClass").isJsonNull() ? extractDrugClass(object.getAsJsonObject(
                        "drugClass")) : null)
                .therapy(object.has("therapy") && !object.get("therapy").isJsonNull()
                        ? extractTherapy(object.getAsJsonObject("therapy"))
                        : null)
                .references(extractReferences(object.getAsJsonArray("references")))
                .createDate(DateConverter.toDate(JsonFunctions.string(object, "createDate")))
                .updateDate(DateConverter.toDate(JsonFunctions.string(object, "updateDate")))
                .build();
    }

    @NotNull
    private static DrugClassInfo extractDrugClass(@NotNull JsonObject jsonObject) {
        JsonDatamodelChecker drugClassObjectChecker = TreatmentApproachDataModelChecker.drugClassObjectChecker();
        drugClassObjectChecker.check(jsonObject);

        return ImmutableDrugClassInfo.builder()
                .id(JsonFunctions.integer(jsonObject, "id"))
                .drugClass(JsonFunctions.string(jsonObject, "drugClass"))
                .build();
    }

    @NotNull
    private static TherapyInfo extractTherapy(@NotNull JsonObject jsonObject) {
        JsonDatamodelChecker therapyObjectChecker = TreatmentApproachDataModelChecker.therapyObjectChecker();
        therapyObjectChecker.check(jsonObject);

        return ImmutableTherapyInfo.builder()
                .id(JsonFunctions.integer(jsonObject, "id"))
                .therapyName(JsonFunctions.string(jsonObject, "therapyName"))
                .synonyms(JsonFunctions.nullableStringList(jsonObject, "synonyms"))
                .build();
    }

    @NotNull
    private static List<ReferenceInfo> extractReferences(@NotNull JsonArray jsonArray) {
        List<ReferenceInfo> references = Lists.newArrayList();
        JsonDatamodelChecker referenceObjectChecker = TreatmentApproachDataModelChecker.referenceObjectChecker();

        for (JsonElement reference : jsonArray) {
            JsonObject referenceJsonObject = reference.getAsJsonObject();
            referenceObjectChecker.check(referenceJsonObject);

            references.add(ImmutableReferenceInfo.builder()
                    .id(JsonFunctions.integer(referenceJsonObject, "id"))
                    .pubMedId(JsonFunctions.nullableString(referenceJsonObject, "pubMedId"))
                    .title(JsonFunctions.string(referenceJsonObject, "title"))
                    .url(JsonFunctions.nullableString(referenceJsonObject, "url"))
                    .build());
        }
        return references;
    }
}
