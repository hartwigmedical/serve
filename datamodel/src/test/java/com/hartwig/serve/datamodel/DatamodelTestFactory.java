package com.hartwig.serve.datamodel;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.characteristic.CharacteristicTestFactory;
import com.hartwig.serve.datamodel.fusion.FusionTestFactory;
import com.hartwig.serve.datamodel.gene.GeneTestFactory;
import com.hartwig.serve.datamodel.hotspot.HotspotTestFactory;
import com.hartwig.serve.datamodel.immuno.ImmunoTestFactory;
import com.hartwig.serve.datamodel.range.RangeTestFactory;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class DatamodelTestFactory {

    private DatamodelTestFactory() {
    }

    @NotNull
    public static ImmutableTreatment.Builder treatmentBuilder() {
        return ImmutableTreatment.builder().name(Strings.EMPTY);
    }

    @NotNull
    public static Treatment createTestTreatment() {
        return treatmentBuilder().build();
    }

    @NotNull
    public static ImmutableCancerType.Builder cancerTypeBuilder() {
        return ImmutableCancerType.builder().name(Strings.EMPTY).doid(Strings.EMPTY);
    }

    @NotNull
    public static CancerType createTestCancerBuilder() {
        return cancerTypeBuilder().build();
    }

    @NotNull
    public static ImmutableIndication.Builder indicationBuilder() {
        return ImmutableIndication.builder().applicableType(createTestCancerBuilder());
    }

    @NotNull
    public static ImmutableIndication.Builder createTestIndication(@NotNull String applicableType, @NotNull String nonApplicableSubType) {
        return indicationBuilder().applicableType(cancerTypeBuilder().name(applicableType).build())
                .nonApplicableSubTypes(Set.of(cancerTypeBuilder().name(nonApplicableSubType).build()));
    }

    @NotNull
    public static ImmutableCountry.Builder countryBuilder() {
        return ImmutableCountry.builder().countryName(Strings.EMPTY).hospitalsPerCity(Maps.newHashMap());
    }

    @NotNull
    public static Country createTestCountry(@NotNull String countryName) {
        return countryBuilder().countryName(countryName)
                .hospitalsPerCity(Map.of("city",
                        Set.of(ImmutableHospital.builder().isChildrensHospital(false).name("hospital name").build())))
                .build();
    }

    @NotNull
    public static MolecularCriterium createTestMolecularCriterium() {
        return ImmutableMolecularCriterium.builder()
                .addHotspots(HotspotTestFactory.createTestActionableHotspot())
                .addCodons(RangeTestFactory.createTestActionableRange())
                .addExons(RangeTestFactory.createTestActionableRange())
                .addGenes(GeneTestFactory.createTestActionableGene())
                .addFusions(FusionTestFactory.createTestActionableFusion())
                .addCharacteristics(CharacteristicTestFactory.createTestActionableCharacteristic())
                .addHla(ImmunoTestFactory.createTestActionableHLA())
                .build();
    }

    @NotNull
    public static ImmutableEfficacyEvidence.Builder efficacyEvidenceBuilder() {
        return ImmutableEfficacyEvidence.builder()
                .source(Knowledgebase.UNKNOWN)
                .treatment(treatmentBuilder().build())
                .indication(indicationBuilder().build())
                .molecularCriterium(createTestMolecularCriterium())
                .efficacyDescription(Strings.EMPTY)
                .evidenceLevel(EvidenceLevel.A)
                .evidenceLevelDetails(EvidenceLevelDetails.FDA_APPROVED)
                .evidenceDirection(EvidenceDirection.RESPONSIVE)
                .evidenceYear(2024)
                .urls(Sets.newHashSet());
    }

    @NotNull
    public static EfficacyEvidence createTestEfficacyEvidence(@NotNull Knowledgebase source, @NotNull String applicableCancerType,
            @NotNull String efficacyDescription, @NotNull EvidenceLevel evidenceLevel, @NotNull EvidenceLevelDetails evidenceLevelDetails,
            @NotNull EvidenceDirection evidenceDirection, @NotNull Integer evidenceYear, @NotNull Set<String> urls) {
        return efficacyEvidenceBuilder().source(source)
                .indication(createTestIndication(applicableCancerType, Strings.EMPTY).build())
                .efficacyDescription(efficacyDescription)
                .evidenceLevel(evidenceLevel)
                .evidenceLevelDetails(evidenceLevelDetails)
                .evidenceDirection(evidenceDirection)
                .evidenceYear(evidenceYear)
                .urls(urls)
                .build();
    }

    @NotNull
    public static ImmutableClinicalTrial.Builder clinicalTrialBuilder() {
        return ImmutableClinicalTrial.builder()
                .nctId(Strings.EMPTY)
                .title(Strings.EMPTY)
                .acronym(Strings.EMPTY)
                .countries(Sets.newHashSet())
                .therapyNames(Sets.newHashSet())
                .genderCriterium(GenderCriterium.BOTH)
                .indications(Sets.newHashSet())
                .molecularCriteria(Sets.newHashSet())
                .urls(Sets.newHashSet());
    }

    @NotNull
    public static ClinicalTrial createTestClinicalTrial(@NotNull String nctId, @NotNull String title, @NotNull String countryName,
            @NotNull Set<String> therapyNames, @NotNull GenderCriterium genderCriterium, @NotNull String applicableCancerType,
            @NotNull String ignoredCancerType) {
        return clinicalTrialBuilder().nctId(nctId)
                .title(title)
                .countries(Set.of(createTestCountry(countryName)))
                .therapyNames(therapyNames)
                .genderCriterium(genderCriterium)
                .indications(Set.of(createTestIndication(applicableCancerType, Strings.EMPTY).build()))
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

        public ActionableEventImpl(@NotNull final LocalDate sourceDate, @NotNull final String sourceEvent,
                @NotNull final Set<String> sourceUrls) {
            this.sourceDate = sourceDate;
            this.sourceEvent = sourceEvent;
            this.sourceUrls = sourceUrls;
        }

        @NotNull
        @Override
        public LocalDate sourceDate() {
            return sourceDate;
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

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final ActionableEventImpl that = (ActionableEventImpl) o;
            return Objects.equals(sourceDate, that.sourceDate) && Objects.equals(sourceEvent, that.sourceEvent)
                    && Objects.equals(sourceUrls, that.sourceUrls);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sourceDate, sourceEvent, sourceUrls);
        }

        @Override
        public String toString() {
            return "ActionableEventImpl{" + "sourceDate=" + sourceDate + ", sourceEvent='" + sourceEvent + '\'' + ", sourceUrls="
                    + sourceUrls + '}';
        }
    }
}