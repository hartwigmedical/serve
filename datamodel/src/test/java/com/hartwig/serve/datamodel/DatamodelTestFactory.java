package com.hartwig.serve.datamodel;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.range.RangeTestFactory;
import com.hartwig.serve.datamodel.hotspot.HotspotTestFactory;
import com.hartwig.serve.datamodel.fusion.FusionTestFactory;
import com.hartwig.serve.datamodel.characteristic.CharacteristicTestFactory;
import com.hartwig.serve.datamodel.gene.GeneTestFactory;
import com.hartwig.serve.datamodel.immuno.ImmunoTestFactory;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class DatamodelTestFactory {

    private DatamodelTestFactory() {
    }

    @NotNull
    public static ImmutableTreatment.Builder extractTreatment() {
        return ImmutableTreatment.builder().name(Strings.EMPTY);
    }

    @NotNull
    public static ImmutableCancerType.Builder cancerTypeBuilder() {
        return ImmutableCancerType.builder().name(Strings.EMPTY).doid(Strings.EMPTY);
    }

    @NotNull
    public static ImmutableIndication.Builder indicationBuilder(@NotNull String applicableCancerType, @NotNull String ignoredCancerType) {
        return ImmutableIndication.builder()
                .applicableCancerType(cancerTypeBuilder().name(applicableCancerType).build())
                .ignoredCancerTypes(Set.of(cancerTypeBuilder().name(ignoredCancerType).build()));
    }

    @NotNull
    public static ImmutableCountry.Builder countryBuilder(@NotNull String country) {
        return ImmutableCountry.builder()
                .countryName(country)
                .hospitalsPerCity(Map.of("city",
                        Set.of(ImmutableHospital.builder().isChildrensHospital(false).name("hospital name").build())));
    }

    @NotNull
    public static ImmutableMolecularProfile.Builder molecularProfileBuilder() {
        return ImmutableMolecularProfile.builder()
                .addHotspots(HotspotTestFactory.createTestActionableHotspotForSource())
                .addCodons(RangeTestFactory.createTestActionableRangeForSource())
                .addExons(RangeTestFactory.createTestActionableRangeForSource())
                .addGenes(GeneTestFactory.createTestActionableGeneForSource())
                .addFusions(FusionTestFactory.createTestActionableFusionForSource())
                .addCharacteristics(CharacteristicTestFactory.createTestActionableCharacteristicForSource())
                .addHla(ImmunoTestFactory.createTestActionableHLAForSource());
    }

    @NotNull
    public static EfficacyEvidence createTestEfficacyEvidence() {
        return createEfficacyEvidence(Knowledgebase.CKB_EVIDENCE,
                Strings.EMPTY,
                2024,
                EvidenceLevel.A,
                EvidenceLevelDetails.FDA_APPROVED,
                EvidenceDirection.NO_BENEFIT,
                Sets.newHashSet(),
                Strings.EMPTY);
    }

    @NotNull
    public static ClinicalTrial createTestClinicalTrial() {
        return ImmutableClinicalTrial.builder()
                .nctId(Strings.EMPTY)
                .acronym(Strings.EMPTY)
                .title(Strings.EMPTY)
                .countries(Sets.newHashSet())
                .genderCriterium(GenderCriterium.BOTH)
                .therapyNames(Sets.newHashSet())
                .urls(Sets.newHashSet())
                .indications(Sets.newHashSet())
                .molecularProfiles(Set.of(molecularProfileBuilder().build()))
                .build();
    }

    @NotNull
    public static EfficacyEvidence createEfficacyEvidence(@NotNull Knowledgebase source, @NotNull String efficacyDescription,
            @NotNull Integer efficacyDescriptionYear, @NotNull EvidenceLevel level, @NotNull EvidenceLevelDetails evidenceLevelDetails,
            @NotNull EvidenceDirection direction, @NotNull Set<String> evidenceUrls, @NotNull String applicableCancerType) {
        return ImmutableEfficacyEvidence.builder()
                .treatment(extractTreatment().build())
                .indication(indicationBuilder(applicableCancerType, Strings.EMPTY).build())
                .source(source)
                .evidenceDirection(direction)
                .evidenceLevel(level)
                .evidenceLevelDetails(evidenceLevelDetails)
                .evidenceYear(efficacyDescriptionYear)
                .efficacyDescription(efficacyDescription)
                .evidenceUrls(evidenceUrls)
                .molecularProfile(molecularProfileBuilder().build())
                .build();
    }

    @NotNull
    public static ClinicalTrial createClinicalTrial(@NotNull String nctId, @NotNull String title, @NotNull String countryName,
            @NotNull Set<String> therapyNames, @NotNull GenderCriterium genderCriterium, @NotNull String applicableCancerType,
            @NotNull String ignoredCancerType) {
        return ImmutableClinicalTrial.builder()
                .nctId(nctId)
                .title(title)
                .countries(Set.of(countryBuilder(countryName).build()))
                .therapyNames(therapyNames)
                .genderCriterium(genderCriterium)
                .indications(Set.of(indicationBuilder(applicableCancerType, Strings.EMPTY).build()))
                .build();
    }

    @NotNull
    public static ActionableEvent createTestActionableEvent() {
        return new ActionableEventImpl(LocalDate.EPOCH, Strings.EMPTY, Sets.newHashSet());
    }

    private static class ActionableEventImpl implements ActionableEvent {

        @NotNull
        private final LocalDate sourceDate;
        @NotNull
        private final String sourceEvent;
        @NotNull
        private final Set<String> sourceUrls;

        public ActionableEventImpl(@NotNull LocalDate entryDate, @NotNull String sourceEvent, @NotNull Set<String> sourceUrls) {
            this.sourceEvent = sourceEvent;
            this.sourceDate = entryDate;
            this.sourceUrls = sourceUrls;
        }

        @NotNull
        @Override
        public String sourceEvent() {
            return sourceEvent;
        }

        @NotNull
        @Override
        public Set<String> sourceUrls() {
            return sourceUrls;
        }

        @NotNull
        @Override
        public LocalDate sourceDate() {
            return sourceDate;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ActionableEventImpl that = (ActionableEventImpl) o;
            return Objects.equals(sourceEvent, that.sourceEvent) && Objects.equals(sourceDate, that.sourceDate)
                    && Objects.equals(sourceUrls, that.sourceUrls);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sourceEvent, sourceDate, sourceUrls);
        }

        @Override
        public String toString() {
            return "ActionableEventImpl{" + "sourceEvent='" + sourceEvent + '\'' + ", sourceDate" + sourceDate + '\'' + ", sourceUrls="
                    + sourceUrls + '}';
        }
    }
}