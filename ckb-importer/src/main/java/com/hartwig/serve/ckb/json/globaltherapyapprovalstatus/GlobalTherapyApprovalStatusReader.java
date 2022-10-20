package com.hartwig.serve.ckb.json.globaltherapyapprovalstatus;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hartwig.serve.ckb.json.CkbJsonDirectoryReader;
import com.hartwig.serve.ckb.json.common.GlobalApprovalStatusInfo;
import com.hartwig.serve.ckb.json.common.ImmutableGlobalApprovalStatusInfo;
import com.hartwig.serve.ckb.json.common.ImmutableIndicationInfo;
import com.hartwig.serve.ckb.json.common.ImmutableMolecularProfileInfo;
import com.hartwig.serve.ckb.json.common.ImmutableTherapyInfo;
import com.hartwig.serve.ckb.json.common.IndicationInfo;
import com.hartwig.serve.ckb.json.common.MolecularProfileInfo;
import com.hartwig.serve.ckb.json.common.TherapyInfo;
import com.hartwig.serve.common.json.Json;
import com.hartwig.serve.common.json.JsonDatamodelChecker;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GlobalTherapyApprovalStatusReader extends CkbJsonDirectoryReader<JsonGlobalTherapyApprovalStatus> {

    public GlobalTherapyApprovalStatusReader(@Nullable final Integer maxFilesToRead) {
        super(maxFilesToRead);
    }

    @NotNull
    @Override
    protected JsonGlobalTherapyApprovalStatus read(@NotNull final JsonObject object) {
        JsonDatamodelChecker statusChecker = GlobalTherapyApprovalStatusDatamodelChecker.globalTherapyApprovalStatusObjectChecker();
        statusChecker.check(object);

        return ImmutableJsonGlobalTherapyApprovalStatus.builder()
                .totalCount(Json.integer(object, "totalCount"))
                .globalApprovalStatuses(extractStatuses(object.getAsJsonArray("globalTherapyApprovalStatuses")))
                .build();
    }

    @NotNull
    private static List<GlobalApprovalStatusInfo> extractStatuses(@NotNull JsonArray jsonArray) {
        List<GlobalApprovalStatusInfo> statuses = Lists.newArrayList();
        JsonDatamodelChecker listChecker = GlobalTherapyApprovalStatusDatamodelChecker.listObjectChecker();

        for (JsonElement status : jsonArray) {
            JsonObject statusJsonObject = status.getAsJsonObject();
            listChecker.check(statusJsonObject);

            statuses.add(ImmutableGlobalApprovalStatusInfo.builder()
                    .id(Json.integer(statusJsonObject, "id"))
                    .therapy(extractTherapy(statusJsonObject.getAsJsonObject("therapy")))
                    .indication(extractIndication(statusJsonObject.getAsJsonObject("indication")))
                    .molecularProfile(extractMolecularProfile(statusJsonObject.getAsJsonObject("molecularProfile")))
                    .approvalAuthority(Json.string(statusJsonObject, "approvalAuthority"))
                    .approvalStatus(Json.string(statusJsonObject, "approvalStatus"))
                    .build());
        }
        return statuses;
    }

    @NotNull
    private static TherapyInfo extractTherapy(@NotNull JsonObject jsonObject) {
        JsonDatamodelChecker therapyChecker = GlobalTherapyApprovalStatusDatamodelChecker.therapyObjectChecker();
        therapyChecker.check(jsonObject);

        return ImmutableTherapyInfo.builder()
                .id(Json.integer(jsonObject, "id"))
                .therapyName(Json.string(jsonObject, "therapyName"))
                .synonyms(Json.optionalStringList(jsonObject, "synonyms"))
                .build();
    }

    @NotNull
    private static IndicationInfo extractIndication(@NotNull JsonObject jsonObject) {
        JsonDatamodelChecker indicationChecker = GlobalTherapyApprovalStatusDatamodelChecker.indicationObjectChecker();
        indicationChecker.check(jsonObject);

        return ImmutableIndicationInfo.builder()
                .id(Json.integer(jsonObject, "id"))
                .name(Json.string(jsonObject, "name"))
                .source(Json.string(jsonObject, "source"))
                .build();
    }

    @NotNull
    private static MolecularProfileInfo extractMolecularProfile(@NotNull JsonObject jsonObject) {
        JsonDatamodelChecker molecularProfileChecker = GlobalTherapyApprovalStatusDatamodelChecker.molecularProfileObjectChecker();
        molecularProfileChecker.check(jsonObject);

        return ImmutableMolecularProfileInfo.builder()
                .id(Json.integer(jsonObject, "id"))
                .profileName(Json.string(jsonObject, "profileName"))
                .build();
    }
}
