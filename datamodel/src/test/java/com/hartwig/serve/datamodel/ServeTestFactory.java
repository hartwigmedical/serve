package com.hartwig.serve.datamodel;

import com.google.common.collect.Sets;
import com.hartwig.serve.datamodel.actionability.ActionabilityTestUtil;
import com.hartwig.serve.datamodel.actionability.ActionableEvent;
import com.hartwig.serve.datamodel.actionability.EvidenceDirection;
import com.hartwig.serve.datamodel.actionability.EvidenceLevel;
import com.hartwig.serve.datamodel.actionability.ImmutableTreatment;
import com.hartwig.serve.datamodel.actionability.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.actionability.characteristic.ImmutableActionableCharacteristic;
import com.hartwig.serve.datamodel.actionability.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.actionability.fusion.ImmutableActionableFusion;
import com.hartwig.serve.datamodel.actionability.gene.ActionableGene;
import com.hartwig.serve.datamodel.actionability.gene.ImmutableActionableGene;
import com.hartwig.serve.datamodel.actionability.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.actionability.hotspot.ImmutableActionableHotspot;
import com.hartwig.serve.datamodel.actionability.immuno.ActionableHLA;
import com.hartwig.serve.datamodel.actionability.immuno.ImmutableActionableHLA;
import com.hartwig.serve.datamodel.actionability.range.ActionableRange;
import com.hartwig.serve.datamodel.actionability.range.ImmutableActionableRange;
import com.hartwig.serve.datamodel.actionability.range.RangeType;
import com.hartwig.serve.datamodel.cancertype.ImmutableCancerType;
import com.hartwig.serve.datamodel.characteristic.TumorCharacteristicAnnotation;
import com.hartwig.serve.datamodel.gene.GeneLevelEvent;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class ServeTestFactory {

    private ServeTestFactory() {
    }

    @NotNull
    public static ActionableHotspot createTestActionableHotspotForSource(@NotNull Knowledgebase source) {
        return ImmutableActionableHotspot.builder().from(createTestActionableHotspot()).source(source).build();
    }

    @NotNull
    public static ActionableHotspot createTestActionableHotspot() {
        return ImmutableActionableHotspot.builder()
                .from(createTestBaseEvent())
                .chromosome(Strings.EMPTY)
                .position(0)
                .ref(Strings.EMPTY)
                .alt(Strings.EMPTY)
                .build();
    }

    @NotNull
    public static ActionableRange createTestActionableRangeForSource(@NotNull Knowledgebase source) {
        return ImmutableActionableRange.builder().from(createTestActionableRange()).source(source).build();
    }

    @NotNull
    public static ActionableRange createTestActionableRange() {
        return ImmutableActionableRange.builder()
                .from(createTestBaseEvent())
                .gene(Strings.EMPTY)
                .transcript(Strings.EMPTY)
                .chromosome(Strings.EMPTY)
                .start(0)
                .end(0)
                .mutationType(MutationTypeFilter.ANY)
                .rangeType(RangeType.EXON)
                .rank(0)
                .build();
    }

    @NotNull
    public static ActionableGene createTestActionableGeneForSource(@NotNull Knowledgebase source) {
        return ImmutableActionableGene.builder().from(createTestActionableGene()).source(source).build();
    }

    @NotNull
    public static ActionableGene createTestActionableGene() {
        return ImmutableActionableGene.builder()
                .from(createTestBaseEvent())
                .gene(Strings.EMPTY)
                .event(GeneLevelEvent.AMPLIFICATION)
                .build();
    }

    @NotNull
    public static ActionableFusion createTestActionableFusionForSource(@NotNull Knowledgebase source) {
        return ImmutableActionableFusion.builder().from(createTestActionableFusion()).source(source).build();
    }

    @NotNull
    public static ActionableFusion createTestActionableFusion() {
        return ImmutableActionableFusion.builder().from(createTestBaseEvent()).geneUp(Strings.EMPTY).geneDown(Strings.EMPTY).build();
    }

    @NotNull
    public static ActionableCharacteristic createTestActionableCharacteristicForSource(@NotNull Knowledgebase source) {
        return ImmutableActionableCharacteristic.builder().from(createTestActionableCharacteristic()).source(source).build();
    }

    @NotNull
    public static ActionableCharacteristic createTestActionableCharacteristic() {
        return ImmutableActionableCharacteristic.builder()
                .from(createTestBaseEvent())
                .name(TumorCharacteristicAnnotation.MICROSATELLITE_UNSTABLE)
                .build();
    }

    @NotNull
    public static ActionableHLA createTestActionableImmunoHLAForSource(@NotNull Knowledgebase source) {
        return ImmutableActionableHLA.builder().from(createTestActionableHLA()).source(source).build();
    }

    @NotNull
    public static ActionableHLA createTestActionableHLA() {
        return ImmutableActionableHLA.builder().from(createTestBaseEvent()).hlaType(Strings.EMPTY).build();
    }

    @NotNull
    private static ActionableEvent createTestBaseEvent() {
        return createTestBaseEvent(Knowledgebase.HARTWIG_CURATED);
    }

    @NotNull
    private static ActionableEvent createTestBaseEvent(@NotNull Knowledgebase source) {
        return ActionabilityTestUtil.create(source,
                "source event",
                Sets.newHashSet(),
                ImmutableTreatment.builder()
                        .treament("treatment")
                        .sourceRelevantTreatmentApproaches(Sets.newHashSet("drugClasses"))
                        .relevantTreatmentApproaches(Sets.newHashSet("drugClasses"))
                        .build(),
                ImmutableCancerType.builder().name("applicable name").doid("applicable doid").build(),
                Sets.newHashSet(ImmutableCancerType.builder().name("blacklist name").doid("blacklist doid").build()),
                EvidenceLevel.A,
                EvidenceDirection.RESPONSIVE,
                Sets.newHashSet());
    }
}
