package com.hartwig.serve.ckb.json.variant;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hartwig.serve.ckb.json.CkbJsonDirectoryReader;
import com.hartwig.serve.ckb.json.common.DescriptionInfo;
import com.hartwig.serve.ckb.json.common.EvidenceInfo;
import com.hartwig.serve.ckb.json.common.GeneInfo;
import com.hartwig.serve.ckb.json.common.ImmutableDescriptionInfo;
import com.hartwig.serve.ckb.json.common.ImmutableEvidenceInfo;
import com.hartwig.serve.ckb.json.common.ImmutableGeneInfo;
import com.hartwig.serve.ckb.json.common.ImmutableIndicationInfo;
import com.hartwig.serve.ckb.json.common.ImmutableMolecularProfileInfo;
import com.hartwig.serve.ckb.json.common.ImmutableReferenceInfo;
import com.hartwig.serve.ckb.json.common.ImmutableTherapyInfo;
import com.hartwig.serve.ckb.json.common.ImmutableTreatmentApproachInfo;
import com.hartwig.serve.ckb.json.common.ImmutableVariantInfo;
import com.hartwig.serve.ckb.json.common.IndicationInfo;
import com.hartwig.serve.ckb.json.common.MolecularProfileInfo;
import com.hartwig.serve.ckb.json.common.ReferenceInfo;
import com.hartwig.serve.ckb.json.common.TherapyInfo;
import com.hartwig.serve.ckb.json.common.TreatmentApproachInfo;
import com.hartwig.serve.ckb.json.common.VariantInfo;
import com.hartwig.serve.ckb.util.DateConverter;
import com.hartwig.serve.common.utils.json.JsonDatamodelChecker;
import com.hartwig.serve.common.utils.json.JsonFunctions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VariantReader extends CkbJsonDirectoryReader<JsonVariant> {

    public VariantReader(@Nullable final Integer maxFilesToRead) {
        super(maxFilesToRead);
    }

    @NotNull
    @Override
    protected JsonVariant read(@NotNull final JsonObject object) {
        JsonDatamodelChecker variantObjectChecker = VariantDataModelChecker.variantObjectChecker();
        variantObjectChecker.check(object);

        return ImmutableJsonVariant.builder()
                .id(JsonFunctions.integer(object, "id"))
                .fullName(JsonFunctions.string(object, "fullName"))
                .impact(JsonFunctions.nullableString(object, "impact"))
                .proteinEffect(JsonFunctions.nullableString(object, "proteinEffect"))
                .descriptions(extractGeneDescriptions(object.getAsJsonArray("geneVariantDescriptions")))
                .type(JsonFunctions.nullableString(object, "type"))
                .gene(extractGene(object.getAsJsonObject("gene")))
                .variant(JsonFunctions.string(object, "variant"))
                .createDate(DateConverter.toDate(JsonFunctions.string(object, "createDate")))
                .updateDate(DateConverter.toDate(JsonFunctions.string(object, "updateDate")))
                .referenceTranscriptCoordinate(extractReferenceTranscriptCoordinate(JsonFunctions.optionalJsonObject(object,
                        "referenceTranscriptCoordinates")))
                .partnerGenes(extractPartnerGenes(object.getAsJsonArray("partnerGenes")))
                .categoryVariantPaths(extractCategoryVariantPaths(object.getAsJsonArray("categoryVariantPaths")))
                .evidence(extractEvidence(object.getAsJsonArray("evidence")))
                .extendedEvidence(extractExtendedEvidence(object.getAsJsonArray("extendedEvidence")))
                .molecularProfiles(extractMolecularProfiles(object.getAsJsonArray("molecularProfiles")))
                .allTranscriptCoordinates(extractAllTranscriptCoordinates(object.getAsJsonArray("allTranscriptCoordinates")))
                .memberVariants(extractMemberVariants(object.getAsJsonArray("memberVariants")))
                .build();
    }

    @NotNull
    private static List<DescriptionInfo> extractGeneDescriptions(@NotNull JsonArray jsonArray) {
        List<DescriptionInfo> geneVariantDescriptions = Lists.newArrayList();
        JsonDatamodelChecker geneVariantDescriptionObjectChecker = VariantDataModelChecker.geneVariantDescriptionObjectChecker();

        for (JsonElement geneVariantDescription : jsonArray) {
            JsonObject geneVariantDescriptionJsonObject = geneVariantDescription.getAsJsonObject();
            geneVariantDescriptionObjectChecker.check(geneVariantDescriptionJsonObject);

            geneVariantDescriptions.add(ImmutableDescriptionInfo.builder()
                    .description(JsonFunctions.string(geneVariantDescriptionJsonObject, "description"))
                    .references(extractReferences(geneVariantDescriptionJsonObject.getAsJsonArray("references")))
                    .build());
        }
        return geneVariantDescriptions;
    }

    @NotNull
    private static List<ReferenceInfo> extractReferences(@NotNull JsonArray jsonArray) {
        List<ReferenceInfo> references = Lists.newArrayList();
        JsonDatamodelChecker referenceObjectChecker = VariantDataModelChecker.referenceObjectChecker();

        for (JsonElement reference : jsonArray) {
            JsonObject referenceJsonObject = reference.getAsJsonObject();
            referenceObjectChecker.check(referenceJsonObject);

            references.add(ImmutableReferenceInfo.builder()
                    .id(JsonFunctions.integer(referenceJsonObject, "id"))
                    .pubMedId(JsonFunctions.nullableString(referenceJsonObject, "pubMedId"))
                    .title(JsonFunctions.nullableString(referenceJsonObject, "title"))
                    .url(JsonFunctions.nullableString(referenceJsonObject, "url"))
                    .build());
        }
        return references;
    }

    @NotNull
    private static GeneInfo extractGene(@NotNull JsonObject jsonObject) {
        JsonDatamodelChecker geneObjectChecker = VariantDataModelChecker.geneObjectChecker();
        geneObjectChecker.check(jsonObject);

        return ImmutableGeneInfo.builder()
                .id(JsonFunctions.integer(jsonObject, "id"))
                .geneSymbol(JsonFunctions.string(jsonObject, "geneSymbol"))
                .terms(JsonFunctions.stringList(jsonObject, "terms"))
                .build();
    }

    @Nullable
    private static JsonTranscriptCoordinate extractReferenceTranscriptCoordinate(@Nullable JsonObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }

        JsonDatamodelChecker referenceTranscriptCoordinateObjectChecker =
                VariantDataModelChecker.referenceTranscriptCoordinateObjectChecker();
        referenceTranscriptCoordinateObjectChecker.check(jsonObject);

        return ImmutableJsonTranscriptCoordinate.builder()
                .id(JsonFunctions.integer(jsonObject, "id"))
                .transcript(JsonFunctions.string(jsonObject, "transcript"))
                .gDNA(JsonFunctions.string(jsonObject, "gDna"))
                .cDNA(JsonFunctions.string(jsonObject, "cDna"))
                .protein(JsonFunctions.string(jsonObject, "protein"))
                .sourceDB(JsonFunctions.string(jsonObject, "sourceDb"))
                .refGenomeBuild(JsonFunctions.string(jsonObject, "refGenomeBuild"))
                .build();
    }

    @NotNull
    private static List<JsonVariantPartnerGene> extractPartnerGenes(@NotNull JsonArray jsonArray) {
        List<JsonVariantPartnerGene> partnerGenes = Lists.newArrayList();
        JsonDatamodelChecker partnerGeneObjectChecker = VariantDataModelChecker.partnerGeneObjectChecker();

        for (JsonElement partnerGene : jsonArray) {
            JsonObject partnerGenePathJsonObject = partnerGene.getAsJsonObject();
            partnerGeneObjectChecker.check(partnerGenePathJsonObject);

            partnerGenes.add(ImmutableJsonVariantPartnerGene.builder()
                    .gene(extractGene(partnerGenePathJsonObject.getAsJsonObject("gene")))
                    .build());
        }

        return partnerGenes;
    }

    @NotNull
    private static List<JsonCategoryVariantPath> extractCategoryVariantPaths(@NotNull JsonArray jsonArray) {
        List<JsonCategoryVariantPath> categoryVariantPaths = Lists.newArrayList();
        JsonDatamodelChecker categoryVariantPathObjectChecker = VariantDataModelChecker.categoryVariantPathObjectChecker();

        for (JsonElement categoryVariantPath : jsonArray) {
            JsonObject categoryVariantPathJsonObject = categoryVariantPath.getAsJsonObject();
            categoryVariantPathObjectChecker.check(categoryVariantPathJsonObject);

            categoryVariantPaths.add(ImmutableJsonCategoryVariantPath.builder()
                    .variantPath(JsonFunctions.string(categoryVariantPathJsonObject, "variantPath"))
                    .variants(extractVariants(categoryVariantPathJsonObject.getAsJsonArray("variants")))
                    .build());
        }

        return categoryVariantPaths;
    }

    @NotNull
    private static List<VariantInfo> extractVariants(@NotNull JsonArray jsonArray) {
        List<VariantInfo> variants = Lists.newArrayList();
        JsonDatamodelChecker variantObjectChecker = VariantDataModelChecker.variantVariantObjectChecker();

        for (JsonElement variant : jsonArray) {
            JsonObject variantJsonObject = variant.getAsJsonObject();
            variantObjectChecker.check(variantJsonObject);

            variants.add(ImmutableVariantInfo.builder()
                    .id(JsonFunctions.integer(variantJsonObject, "id"))
                    .fullName(JsonFunctions.string(variantJsonObject, "fullName"))
                    .impact(JsonFunctions.string(variantJsonObject, "impact"))
                    .proteinEffect(JsonFunctions.string(variantJsonObject, "proteinEffect"))
                    .build());
        }
        return variants;
    }

    @NotNull
    private static List<EvidenceInfo> extractEvidence(@NotNull JsonArray jsonArray) {
        List<EvidenceInfo> evidences = Lists.newArrayList();
        JsonDatamodelChecker evidenceChecker = VariantDataModelChecker.evidenceObjectChecker();

        for (JsonElement evidence : jsonArray) {
            JsonObject evidenceJsonObject = evidence.getAsJsonObject();
            evidenceChecker.check(evidenceJsonObject);

            evidences.add(ImmutableEvidenceInfo.builder()
                    .id(JsonFunctions.integer(evidenceJsonObject, "id"))
                    .approvalStatus(JsonFunctions.string(evidenceJsonObject, "approvalStatus"))
                    .evidenceType(JsonFunctions.string(evidenceJsonObject, "evidenceType"))
                    .efficacyEvidence(JsonFunctions.string(evidenceJsonObject, "efficacyEvidence"))
                    .molecularProfile(extractMolecularProfile(evidenceJsonObject.getAsJsonObject("molecularProfile")))
                    .therapy(extractTherapy(evidenceJsonObject.getAsJsonObject("therapy")))
                    .indication(extractIndication(evidenceJsonObject.getAsJsonObject("indication")))
                    .responseType(JsonFunctions.string(evidenceJsonObject, "responseType"))
                    .references(extractReferences(evidenceJsonObject.getAsJsonArray("references")))
                    .ampCapAscoEvidenceLevel(JsonFunctions.string(evidenceJsonObject, "ampCapAscoEvidenceLevel"))
                    .ampCapAscoInferredTier(JsonFunctions.string(evidenceJsonObject, "ampCapAscoInferredTier"))
                    .build());
        }
        return evidences;
    }

    @NotNull
    private static MolecularProfileInfo extractMolecularProfile(@NotNull JsonObject jsonObject) {
        JsonDatamodelChecker molecularProfileChecker = VariantDataModelChecker.molecularProfileObjectChecker();
        molecularProfileChecker.check(jsonObject);

        return ImmutableMolecularProfileInfo.builder()
                .id(JsonFunctions.integer(jsonObject, "id"))
                .profileName(JsonFunctions.string(jsonObject, "profileName"))
                .build();
    }

    @NotNull
    private static TherapyInfo extractTherapy(@NotNull JsonObject jsonObject) {
        JsonDatamodelChecker therapyChecker = VariantDataModelChecker.therapyChecker();
        therapyChecker.check(jsonObject);

        return ImmutableTherapyInfo.builder()
                .id(JsonFunctions.integer(jsonObject, "id"))
                .therapyName(JsonFunctions.string(jsonObject, "therapyName"))
                .synonyms(JsonFunctions.optionalStringList(jsonObject, "synonyms"))
                .build();
    }

    @NotNull
    private static IndicationInfo extractIndication(@NotNull JsonObject jsonObject) {
        JsonDatamodelChecker indicationChecker = VariantDataModelChecker.indicationChecker();
        indicationChecker.check(jsonObject);

        return ImmutableIndicationInfo.builder()
                .id(JsonFunctions.integer(jsonObject, "id"))
                .name(JsonFunctions.string(jsonObject, "name"))
                .source(JsonFunctions.string(jsonObject, "source"))
                .build();
    }

    @NotNull
    private static List<EvidenceInfo> extractExtendedEvidence(@NotNull JsonArray jsonArray) {
        List<EvidenceInfo> extendedEvidences = Lists.newArrayList();
        JsonDatamodelChecker extendedEvidenceChecker = VariantDataModelChecker.extendedEvidenceObjectChecker();

        for (JsonElement extendedEvidence : jsonArray) {
            JsonObject extendedEvidenceJsonObject = extendedEvidence.getAsJsonObject();
            extendedEvidenceChecker.check(extendedEvidenceJsonObject);

            extendedEvidences.add(ImmutableEvidenceInfo.builder()
                    .id(JsonFunctions.integer(extendedEvidenceJsonObject, "id"))
                    .approvalStatus(JsonFunctions.string(extendedEvidenceJsonObject, "approvalStatus"))
                    .evidenceType(JsonFunctions.string(extendedEvidenceJsonObject, "evidenceType"))
                    .efficacyEvidence(JsonFunctions.string(extendedEvidenceJsonObject, "efficacyEvidence"))
                    .molecularProfile(extractMolecularProfile(extendedEvidenceJsonObject.getAsJsonObject("molecularProfile")))
                    .therapy(extractTherapy(extendedEvidenceJsonObject.getAsJsonObject("therapy")))
                    .indication(extractIndication(extendedEvidenceJsonObject.getAsJsonObject("indication")))
                    .responseType(JsonFunctions.string(extendedEvidenceJsonObject, "responseType"))
                    .references(extractReferences(extendedEvidenceJsonObject.getAsJsonArray("references")))
                    .ampCapAscoEvidenceLevel(JsonFunctions.string(extendedEvidenceJsonObject, "ampCapAscoEvidenceLevel"))
                    .ampCapAscoInferredTier(JsonFunctions.string(extendedEvidenceJsonObject, "ampCapAscoInferredTier"))
                    .build());
        }
        return extendedEvidences;
    }

    @NotNull
    private static List<MolecularProfileInfo> extractMolecularProfiles(@NotNull JsonArray jsonArray) {
        List<MolecularProfileInfo> molecularProfiles = Lists.newArrayList();
        JsonDatamodelChecker molecularProfileChecker = VariantDataModelChecker.molecularProfileObjectChecker();

        for (JsonElement molecularProfile : jsonArray) {
            JsonObject molecularProfileJsonObject = molecularProfile.getAsJsonObject();
            molecularProfileChecker.check(molecularProfileJsonObject);
            molecularProfiles.add(ImmutableMolecularProfileInfo.builder()
                    .id(JsonFunctions.integer(molecularProfileJsonObject, "id"))
                    .profileName(JsonFunctions.string(molecularProfileJsonObject, "profileName"))
                    .treatmentApproaches(extractProfileTreatmentApproaches(molecularProfileJsonObject.getAsJsonArray(
                            "profileTreatmentApproaches")))
                    .build());
        }

        return molecularProfiles;
    }

    @NotNull
    private static List<TreatmentApproachInfo> extractProfileTreatmentApproaches(@NotNull JsonArray jsonArray) {
        List<TreatmentApproachInfo> profileTreatmentApproaches = Lists.newArrayList();
        JsonDatamodelChecker profileTreatmentApproachChecker = VariantDataModelChecker.profileTreatmentApproachObjectChecker();

        for (JsonElement profileTreatmentApproach : jsonArray) {
            JsonObject profileTreatmentApproachJsonObject = profileTreatmentApproach.getAsJsonObject();
            profileTreatmentApproachChecker.check(profileTreatmentApproachJsonObject);

            profileTreatmentApproaches.add(ImmutableTreatmentApproachInfo.builder()
                    .id(JsonFunctions.integer(profileTreatmentApproachJsonObject, "id"))
                    .name(JsonFunctions.string(profileTreatmentApproachJsonObject, "name"))
                    .profileName(JsonFunctions.string(profileTreatmentApproachJsonObject, "profileName"))
                    .build());
        }

        return profileTreatmentApproaches;
    }

    @NotNull
    private static List<JsonTranscriptCoordinate> extractAllTranscriptCoordinates(@NotNull JsonArray jsonArray) {
        List<JsonTranscriptCoordinate> allTranscriptCoordinates = Lists.newArrayList();
        JsonDatamodelChecker allTranscriptCoordinateChecker = VariantDataModelChecker.allTranscriptCoordinateObjectChecker();

        for (JsonElement allTranscriptCoordinate : jsonArray) {
            JsonObject allTranscriptCoordinatesJsonObject = allTranscriptCoordinate.getAsJsonObject();
            allTranscriptCoordinateChecker.check(allTranscriptCoordinatesJsonObject);

            allTranscriptCoordinates.add(ImmutableJsonTranscriptCoordinate.builder()
                    .id(JsonFunctions.integer(allTranscriptCoordinatesJsonObject, "id"))
                    .transcript(JsonFunctions.string(allTranscriptCoordinatesJsonObject, "transcript"))
                    .gDNA(JsonFunctions.string(allTranscriptCoordinatesJsonObject, "gDna"))
                    .cDNA(JsonFunctions.string(allTranscriptCoordinatesJsonObject, "cDna"))
                    .protein(JsonFunctions.string(allTranscriptCoordinatesJsonObject, "protein"))
                    .sourceDB(JsonFunctions.string(allTranscriptCoordinatesJsonObject, "sourceDb"))
                    .refGenomeBuild(JsonFunctions.string(allTranscriptCoordinatesJsonObject, "refGenomeBuild"))
                    .build());
        }

        return allTranscriptCoordinates;
    }

    @NotNull
    private static List<VariantInfo> extractMemberVariants(@NotNull JsonArray jsonArray) {
        List<VariantInfo> memberVariants = Lists.newArrayList();
        JsonDatamodelChecker memberVariantChecker = VariantDataModelChecker.memberVariantObjectChecker();

        for (JsonElement memberVariant : jsonArray) {
            JsonObject memberVariantObject = memberVariant.getAsJsonObject();
            memberVariantChecker.check(memberVariantObject);

            memberVariants.add(ImmutableVariantInfo.builder()
                    .id(JsonFunctions.integer(memberVariantObject, "id"))
                    .fullName(JsonFunctions.string(memberVariantObject, "fullName"))
                    .impact(JsonFunctions.string(memberVariantObject, "impact"))
                    .proteinEffect(JsonFunctions.string(memberVariantObject, "proteinEffect"))
                    .descriptions(extractGeneDescriptions(memberVariantObject.getAsJsonArray("geneVariantDescriptions")))
                    .build());
        }
        return memberVariants;
    }
}
