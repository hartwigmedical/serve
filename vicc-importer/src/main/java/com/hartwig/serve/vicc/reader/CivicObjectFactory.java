package com.hartwig.serve.vicc.reader;

import static com.hartwig.serve.common.json.Json.nullableString;
import static com.hartwig.serve.common.json.Json.optionalArray;
import static com.hartwig.serve.common.json.Json.optionalNullableString;
import static com.hartwig.serve.common.json.Json.optionalObject;
import static com.hartwig.serve.common.json.Json.optionalString;
import static com.hartwig.serve.common.json.Json.string;
import static com.hartwig.serve.common.json.Json.stringList;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hartwig.serve.common.json.JsonDatamodelChecker;
import com.hartwig.serve.vicc.datamodel.civic.Civic;
import com.hartwig.serve.vicc.datamodel.civic.CivicAvatars;
import com.hartwig.serve.vicc.datamodel.civic.CivicClinicalTrial;
import com.hartwig.serve.vicc.datamodel.civic.CivicCoordinates;
import com.hartwig.serve.vicc.datamodel.civic.CivicDisease;
import com.hartwig.serve.vicc.datamodel.civic.CivicDrug;
import com.hartwig.serve.vicc.datamodel.civic.CivicEvidenceItem;
import com.hartwig.serve.vicc.datamodel.civic.CivicLastCommentedOn;
import com.hartwig.serve.vicc.datamodel.civic.CivicLastModified;
import com.hartwig.serve.vicc.datamodel.civic.CivicLastReviewed;
import com.hartwig.serve.vicc.datamodel.civic.CivicLifecycleActions;
import com.hartwig.serve.vicc.datamodel.civic.CivicOrganization;
import com.hartwig.serve.vicc.datamodel.civic.CivicProfileImage;
import com.hartwig.serve.vicc.datamodel.civic.CivicProvisionalValue;
import com.hartwig.serve.vicc.datamodel.civic.CivicPublicationDate;
import com.hartwig.serve.vicc.datamodel.civic.CivicSource;
import com.hartwig.serve.vicc.datamodel.civic.CivicUser;
import com.hartwig.serve.vicc.datamodel.civic.CivicVariant;
import com.hartwig.serve.vicc.datamodel.civic.CivicVariantGroup;
import com.hartwig.serve.vicc.datamodel.civic.CivicVariantType;
import com.hartwig.serve.vicc.datamodel.civic.ImmutableCivic;
import com.hartwig.serve.vicc.datamodel.civic.ImmutableCivicAvatars;
import com.hartwig.serve.vicc.datamodel.civic.ImmutableCivicClinicalTrial;
import com.hartwig.serve.vicc.datamodel.civic.ImmutableCivicCoordinates;
import com.hartwig.serve.vicc.datamodel.civic.ImmutableCivicDisease;
import com.hartwig.serve.vicc.datamodel.civic.ImmutableCivicDrug;
import com.hartwig.serve.vicc.datamodel.civic.ImmutableCivicEvidenceItem;
import com.hartwig.serve.vicc.datamodel.civic.ImmutableCivicLastCommentedOn;
import com.hartwig.serve.vicc.datamodel.civic.ImmutableCivicLastModified;
import com.hartwig.serve.vicc.datamodel.civic.ImmutableCivicLastReviewed;
import com.hartwig.serve.vicc.datamodel.civic.ImmutableCivicLifecycleActions;
import com.hartwig.serve.vicc.datamodel.civic.ImmutableCivicOrganization;
import com.hartwig.serve.vicc.datamodel.civic.ImmutableCivicProfileImage;
import com.hartwig.serve.vicc.datamodel.civic.ImmutableCivicProvisionalValue;
import com.hartwig.serve.vicc.datamodel.civic.ImmutableCivicPublicationDate;
import com.hartwig.serve.vicc.datamodel.civic.ImmutableCivicSource;
import com.hartwig.serve.vicc.datamodel.civic.ImmutableCivicUser;
import com.hartwig.serve.vicc.datamodel.civic.ImmutableCivicVariant;
import com.hartwig.serve.vicc.datamodel.civic.ImmutableCivicVariantGroup;
import com.hartwig.serve.vicc.datamodel.civic.ImmutableCivicVariantType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class CivicObjectFactory {

    private static final Logger LOGGER = LogManager.getLogger(CivicObjectFactory.class);

    private CivicObjectFactory() {
    }

    @NotNull
    static Civic create(@NotNull JsonObject civicObject) {
        ViccDatamodelCheckerFactory.civicEntryChecker().check(civicObject);

        CivicCoordinates coordinates = createCoordinates(civicObject.getAsJsonObject("coordinates"));
        assert coordinates != null;

        return ImmutableCivic.builder()
                .entrezId(string(civicObject, "entrez_id"))
                .entrezName(string(civicObject, "entrez_name"))
                .name(string(civicObject, "name"))
                .type(string(civicObject, "type"))
                .coordinates(coordinates)
                .sources(createSources(civicObject.getAsJsonArray("sources")))
                .variantAliases(stringList(civicObject, "variant_aliases"))
                .variantGroups(createVariantGroups(optionalArray(civicObject, "variant_groups")))
                .variantTypes(createVariantTypes(civicObject.getAsJsonArray("variant_types")))
                .hgvsExpressions(stringList(civicObject, "hgvs_expressions"))
                .evidenceItem(createEvidenceItem(civicObject.getAsJsonArray("evidence_items")))
                .assertions(stringList(civicObject, "assertions"))
                .civicActionabilityScore(nullableString(civicObject, "civic_actionability_score"))
                .clinVarEntries(stringList(civicObject, "clinvar_entries"))
                .alleleRegistryId(nullableString(civicObject, "allele_registry_id"))
                .provisionalValue(createProvisionalValue(civicObject.getAsJsonObject("provisional_values")))
                .lifecycleActions(createLifecycleActions(civicObject.getAsJsonObject("lifecycle_actions")))
                .id(string(civicObject, "id"))
                .geneId(string(civicObject, "gene_id"))
                .description(string(civicObject, "description"))
                .build();
    }

    @Nullable
    private static CivicCoordinates createCoordinates(@Nullable JsonObject coordinatesObject) {
        if (coordinatesObject == null) {
            return null;
        }

        ViccDatamodelCheckerFactory.civicCoordinatesChecker().check(coordinatesObject);

        return ImmutableCivicCoordinates.builder()
                .chromosome(nullableString(coordinatesObject, "chromosome"))
                .start(nullableString(coordinatesObject, "start"))
                .stop(nullableString(coordinatesObject, "stop"))
                .referenceBases(nullableString(coordinatesObject, "reference_bases"))
                .variantBases(nullableString(coordinatesObject, "variant_bases"))
                .representativeTranscript(nullableString(coordinatesObject, "representative_transcript"))
                .ensemblVersion(nullableString(coordinatesObject, "ensembl_version"))
                .referenceBuild(nullableString(coordinatesObject, "reference_build"))
                .chromosome2(nullableString(coordinatesObject, "chromosome2"))
                .start2(nullableString(coordinatesObject, "start2"))
                .stop2(nullableString(coordinatesObject, "stop2"))
                .representativeTranscript2(nullableString(coordinatesObject, "representative_transcript2"))
                .build();
    }

    @NotNull
    private static List<CivicSource> createSources(@NotNull JsonArray sourceArray) {
        List<CivicSource> sourceList = Lists.newArrayList();

        for (JsonElement sourceElement : sourceArray) {
            sourceList.add(createSource(sourceElement.getAsJsonObject()));
        }

        return sourceList;
    }

    @Nullable
    private static CivicProvisionalValue createProvisionalValue(@NotNull JsonObject provisionalValueObject) {
        ViccDatamodelCheckerFactory.civicProvisionalValueChecker().check(provisionalValueObject);

        JsonObject descriptionObject = optionalObject(provisionalValueObject, "description");
        if (descriptionObject == null) {
            return null;
        }

        ViccDatamodelCheckerFactory.civicProvisionalValueDescriptionChecker().check(descriptionObject);

        return ImmutableCivicProvisionalValue.builder()
                .revisionId(optionalNullableString(descriptionObject, "revision_id"))
                .value(optionalNullableString(descriptionObject, "value"))
                .build();
    }

    @NotNull
    private static List<CivicVariantGroup> createVariantGroups(@Nullable JsonArray variantGroupArray) {
        if (variantGroupArray == null) {
            return Lists.newArrayList();
        }

        List<CivicVariantGroup> variantGroupList = Lists.newArrayList();
        JsonDatamodelChecker variantGroupChecker = ViccDatamodelCheckerFactory.civicVariantGroupChecker();

        for (JsonElement variantGroupElement : variantGroupArray) {
            JsonObject variantGroupObject = variantGroupElement.getAsJsonObject();
            variantGroupChecker.check(variantGroupObject);

            variantGroupList.add(ImmutableCivicVariantGroup.builder()
                    .name(string(variantGroupObject, "name"))
                    .type(string(variantGroupObject, "type"))
                    .description(string(variantGroupObject, "description"))
                    .variants(createVariants(variantGroupObject.getAsJsonArray("variants")))
                    .id(string(variantGroupObject, "id"))
                    .build());
        }
        return variantGroupList;
    }

    @NotNull
    private static List<CivicVariant> createVariants(@NotNull JsonArray variantArray) {
        List<CivicVariant> variantList = Lists.newArrayList();
        JsonDatamodelChecker variantChecker = ViccDatamodelCheckerFactory.civicVariantChecker();

        for (JsonElement variantElement : variantArray) {
            JsonObject variantObject = variantElement.getAsJsonObject();
            variantChecker.check(variantObject);

            variantList.add(ImmutableCivicVariant.builder()
                    .entrezId(string(variantObject, "entrez_id"))
                    .entrezName(string(variantObject, "entrez_name"))
                    .name(string(variantObject, "name"))
                    .type(string(variantObject, "type"))
                    .variantTypes(createVariantTypes(variantObject.getAsJsonArray("variant_types")))
                    .civicActionabilityScore(optionalNullableString(variantObject, "civic_actionability_score"))
                    .coordinates(createCoordinates(optionalObject(variantObject, "coordinates")))
                    .id(string(variantObject, "id"))
                    .geneId(string(variantObject, "gene_id"))
                    .description(string(variantObject, "description"))
                    .build());
        }

        return variantList;
    }

    @NotNull
    private static List<CivicVariantType> createVariantTypes(@NotNull JsonArray variantTypeArray) {
        List<CivicVariantType> variantTypeList = Lists.newArrayList();
        JsonDatamodelChecker variantTypeChecker = ViccDatamodelCheckerFactory.civicVariantTypeChecker();

        for (JsonElement variantTypeElement : variantTypeArray) {
            JsonObject variantTypeObject = variantTypeElement.getAsJsonObject();
            variantTypeChecker.check(variantTypeObject);

            variantTypeList.add(ImmutableCivicVariantType.builder()
                    .name(string(variantTypeObject, "name"))
                    .displayName(string(variantTypeObject, "display_name"))
                    .description(string(variantTypeObject, "description"))
                    .url(string(variantTypeObject, "url"))
                    .soId(string(variantTypeObject, "so_id"))
                    .id(string(variantTypeObject, "id"))
                    .build());
        }
        return variantTypeList;
    }

    @NotNull
    private static CivicEvidenceItem createEvidenceItem(@NotNull JsonArray evidenceItemArray) {
        List<CivicEvidenceItem> evidenceItemList = Lists.newArrayList();
        JsonDatamodelChecker evidenceItemChecker = ViccDatamodelCheckerFactory.civicEvidenceItemChecker();

        for (JsonElement evidenceItemElement : evidenceItemArray) {
            JsonObject evidenceItemObject = evidenceItemElement.getAsJsonObject();
            evidenceItemChecker.check(evidenceItemObject);

            evidenceItemList.add(ImmutableCivicEvidenceItem.builder()
                    .name(string(evidenceItemObject, "name"))
                    .type(string(evidenceItemObject, "type"))
                    .status(string(evidenceItemObject, "status"))
                    .rating(nullableString(evidenceItemObject, "rating"))
                    .evidenceType(string(evidenceItemObject, "evidence_type"))
                    .evidenceLevel(string(evidenceItemObject, "evidence_level"))
                    .evidenceDirection(nullableString(evidenceItemObject, "evidence_direction"))
                    .drugInteractionType(nullableString(evidenceItemObject, "drug_interaction_type"))
                    .drugs(createDrugs(evidenceItemObject.getAsJsonArray("drugs")))
                    .disease(createDisease(evidenceItemObject.getAsJsonObject("disease")))
                    .variantOrigin(nullableString(evidenceItemObject, "variant_origin"))
                    .source(createSource(evidenceItemObject.getAsJsonObject("source")))
                    .clinicalSignificance(nullableString(evidenceItemObject, "clinical_significance"))
                    .openChangeCount(string(evidenceItemObject, "open_change_count"))
                    .description(string(evidenceItemObject, "description"))
                    .variantId(optionalString(evidenceItemObject, "variant_id"))
                    .id(string(evidenceItemObject, "id"))
                    .build());
        }

        // In practice there is a 1-1 relation between civic entry and evidence item even though this is modeled as an array.
        if (evidenceItemList.isEmpty()) {
            throw new IllegalStateException("No evidence items found in " + evidenceItemArray);
        } else if (evidenceItemList.size() > 1) {
            LOGGER.warn("More than 1 evidence item found for civic record. Count={}", evidenceItemList.size());
        }

        return evidenceItemList.get(0);
    }

    @NotNull
    private static CivicSource createSource(@NotNull JsonObject sourceObject) {
        ViccDatamodelCheckerFactory.civicSourceChecker().check(sourceObject);

        return ImmutableCivicSource.builder()
                .name(nullableString(sourceObject, "name"))
                .status(string(sourceObject, "status"))
                .openAccess(nullableString(sourceObject, "open_access"))
                .journal(nullableString(sourceObject, "journal"))
                .fullJournalTitle(nullableString(sourceObject, "full_journal_title"))
                .citation(string(sourceObject, "citation"))
                .pmcId(nullableString(sourceObject, "pmc_id"))
                .sourceUrl(string(sourceObject, "source_url"))
                .clinicalTrials(createClinicalTrials(sourceObject.getAsJsonArray("clinical_trials")))
                .pubmedId(string(sourceObject, "pubmed_id"))
                .isReview(string(sourceObject, "is_review"))
                .publicationDate(createPublicationDate(sourceObject.getAsJsonObject("publication_date")))
                .id(string(sourceObject, "id"))
                .build();
    }

    @NotNull
    private static List<CivicClinicalTrial> createClinicalTrials(@NotNull JsonArray clinicalTrialArray) {
        List<CivicClinicalTrial> clinicalTrialList = Lists.newArrayList();
        JsonDatamodelChecker clinicalTrialChecker = ViccDatamodelCheckerFactory.civicClinicalTrialChecker();

        for (JsonElement clinicalTrialElement : clinicalTrialArray) {
            JsonObject clinicalTrialObject = clinicalTrialElement.getAsJsonObject();
            clinicalTrialChecker.check(clinicalTrialObject);

            clinicalTrialList.add(ImmutableCivicClinicalTrial.builder()
                    .name(string(clinicalTrialObject, "name"))
                    .nctId(string(clinicalTrialObject, "nct_id"))
                    .clinicalTrialUrl(string(clinicalTrialObject, "clinical_trial_url"))
                    .description(string(clinicalTrialObject, "description"))
                    .build());
        }
        return clinicalTrialList;
    }

    @NotNull
    private static CivicPublicationDate createPublicationDate(@NotNull JsonObject publicationDateObject) {
        ViccDatamodelCheckerFactory.civicPublicationDateChecker().check(publicationDateObject);

        return ImmutableCivicPublicationDate.builder()
                .year(optionalString(publicationDateObject, "year"))
                .month(optionalString(publicationDateObject, "month"))
                .day(optionalString(publicationDateObject, "day"))
                .build();
    }

    @NotNull
    private static CivicDisease createDisease(@NotNull JsonObject diseaseObject) {
        ViccDatamodelCheckerFactory.civicDiseaseChecker().check(diseaseObject);

        return ImmutableCivicDisease.builder()
                .name(string(diseaseObject, "name"))
                .displayName(string(diseaseObject, "display_name"))
                .doid(nullableString(diseaseObject, "doid"))
                .url(string(diseaseObject, "url"))
                .id(string(diseaseObject, "id"))
                .build();
    }

    @NotNull
    private static List<CivicDrug> createDrugs(@NotNull JsonArray drugArray) {
        List<CivicDrug> drugList = Lists.newArrayList();
        JsonDatamodelChecker drugChecker = ViccDatamodelCheckerFactory.civicDrugChecker();

        for (JsonElement drugElement : drugArray) {
            JsonObject drugObject = drugElement.getAsJsonObject();
            drugChecker.check(drugObject);

            drugList.add(ImmutableCivicDrug.builder()
                    .name(string(drugObject, "name"))
                    .pubchemId(nullableString(drugObject, "pubchem_id"))
                    .id(string(drugObject, "id"))
                    .build());
        }
        return drugList;
    }

    @NotNull
    private static CivicLifecycleActions createLifecycleActions(@NotNull JsonObject lifecycleActionsObject) {
        ViccDatamodelCheckerFactory.civicLifecycleActionsChecker().check(lifecycleActionsObject);

        return ImmutableCivicLifecycleActions.builder()
                .lastCommentedOn(createLastCommentedOn(optionalObject(lifecycleActionsObject, "last_commented_on")))
                .lastModified(createLastModified(optionalObject(lifecycleActionsObject, "last_modified")))
                .lastReviewed(createLastReviewed(optionalObject(lifecycleActionsObject, "last_reviewed")))
                .build();
    }

    @Nullable
    private static CivicLastCommentedOn createLastCommentedOn(@Nullable JsonObject lastCommentedOnObject) {
        if (lastCommentedOnObject == null) {
            return null;
        }

        ViccDatamodelCheckerFactory.civicLastCommentedOnChecker().check(lastCommentedOnObject);

        return ImmutableCivicLastCommentedOn.builder()
                .timestamp(string(lastCommentedOnObject, "timestamp"))
                .user(createUser(lastCommentedOnObject.getAsJsonObject("user")))
                .build();
    }

    @Nullable
    private static CivicLastModified createLastModified(@Nullable JsonObject lastModifiedObject) {
        if (lastModifiedObject == null) {
            return null;
        }

        ViccDatamodelCheckerFactory.civicLastModifiedChecker().check(lastModifiedObject);

        return ImmutableCivicLastModified.builder()
                .timestamp(string(lastModifiedObject, "timestamp"))
                .user(createUser(lastModifiedObject.getAsJsonObject("user")))
                .build();
    }

    @Nullable
    private static CivicLastReviewed createLastReviewed(@Nullable JsonObject lastReviewedObject) {
        if (lastReviewedObject == null) {
            return null;
        }

        ViccDatamodelCheckerFactory.civicLastReviewedChecker().check(lastReviewedObject);

        return ImmutableCivicLastReviewed.builder()
                .timestamp(string(lastReviewedObject, "timestamp"))
                .user(createUser(lastReviewedObject.getAsJsonObject("user")))
                .build();
    }

    @NotNull
    private static CivicUser createUser(@NotNull JsonObject userObject) {
        ViccDatamodelCheckerFactory.civicUserChecker().check(userObject);

        return ImmutableCivicUser.builder()
                .username(string(userObject, "username"))
                .name(string(userObject, "name"))
                .displayName(string(userObject, "display_name"))
                .role(string(userObject, "role"))
                .organization(createOrganization(userObject.getAsJsonObject("organization")))
                .affiliation(nullableString(userObject, "affiliation"))
                .featuredExpert(string(userObject, "featured_expert"))
                .areaOfExpertise(nullableString(userObject, "area_of_expertise"))
                .bio(nullableString(userObject, "bio"))
                .url(nullableString(userObject, "url"))
                .createdAt(string(userObject, "created_at"))
                .lastSeenAt(nullableString(userObject, "last_seen_at"))
                .avatars(createAvatars(userObject.getAsJsonObject("avatars")))
                .avatarUrl(string(userObject, "avatar_url"))
                .twitterHandle(nullableString(userObject, "twitter_handle"))
                .facebookProfile(nullableString(userObject, "facebook_profile"))
                .linkedinProfile(nullableString(userObject, "linkedin_profile"))
                .orcid(nullableString(userObject, "orcid"))
                .signupComplete(nullableString(userObject, "signup_complete"))
                .acceptedLicense(nullableString(userObject, "accepted_license"))
                .id(string(userObject, "id"))
                .build();
    }

    @NotNull
    private static CivicOrganization createOrganization(@NotNull JsonObject organizationObject) {
        ViccDatamodelCheckerFactory.civicOrganizationChecker().check(organizationObject);

        return ImmutableCivicOrganization.builder()
                .name(optionalString(organizationObject, "name"))
                .url(optionalString(organizationObject, "url"))
                .profileImage(createProfileImage(optionalObject(organizationObject, "profile_image")))
                .id(optionalString(organizationObject, "id"))
                .description(optionalString(organizationObject, "description"))
                .build();
    }

    @Nullable
    private static CivicProfileImage createProfileImage(@Nullable JsonObject profileImageObject) {
        if (profileImageObject == null) {
            return null;
        }

        ViccDatamodelCheckerFactory.civicProfileImageChecker().check(profileImageObject);

        return ImmutableCivicProfileImage.builder()
                .x14(string(profileImageObject, "x14"))
                .x32(string(profileImageObject, "x32"))
                .x64(string(profileImageObject, "x64"))
                .x128(string(profileImageObject, "x128"))
                .x256(string(profileImageObject, "x256"))
                .build();
    }

    @NotNull
    private static CivicAvatars createAvatars(@NotNull JsonObject avatarsObject) {
        ViccDatamodelCheckerFactory.civicAvatarsChecker().check(avatarsObject);

        return ImmutableCivicAvatars.builder()
                .x14(string(avatarsObject, "x14"))
                .x32(string(avatarsObject, "x32"))
                .x64(string(avatarsObject, "x64"))
                .x128(string(avatarsObject, "x128"))
                .build();
    }
}
