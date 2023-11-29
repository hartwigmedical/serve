package com.hartwig.serve.datamodel.serialization.util;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import com.google.common.collect.Lists;
import com.hartwig.serve.datamodel.ActionableEvent;
import com.hartwig.serve.datamodel.Knowledgebase;
import com.hartwig.serve.datamodel.KnownEvent;
import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristic;
import com.hartwig.serve.datamodel.characteristic.ActionableCharacteristicComparator;
import com.hartwig.serve.datamodel.characteristic.ImmutableActionableCharacteristic;
import com.hartwig.serve.datamodel.fusion.ActionableFusion;
import com.hartwig.serve.datamodel.fusion.ActionableFusionComparator;
import com.hartwig.serve.datamodel.fusion.ImmutableActionableFusion;
import com.hartwig.serve.datamodel.fusion.ImmutableKnownFusion;
import com.hartwig.serve.datamodel.fusion.KnownFusion;
import com.hartwig.serve.datamodel.fusion.KnownFusionComparator;
import com.hartwig.serve.datamodel.gene.ActionableGene;
import com.hartwig.serve.datamodel.gene.ActionableGeneComparator;
import com.hartwig.serve.datamodel.gene.ImmutableActionableGene;
import com.hartwig.serve.datamodel.gene.ImmutableKnownCopyNumber;
import com.hartwig.serve.datamodel.gene.ImmutableKnownGene;
import com.hartwig.serve.datamodel.gene.KnownCopyNumber;
import com.hartwig.serve.datamodel.gene.KnownCopyNumberComparator;
import com.hartwig.serve.datamodel.gene.KnownGene;
import com.hartwig.serve.datamodel.gene.KnownGeneComparator;
import com.hartwig.serve.datamodel.hotspot.ActionableHotspot;
import com.hartwig.serve.datamodel.hotspot.ActionableHotspotComparator;
import com.hartwig.serve.datamodel.hotspot.ImmutableActionableHotspot;
import com.hartwig.serve.datamodel.hotspot.ImmutableKnownHotspot;
import com.hartwig.serve.datamodel.hotspot.KnownHotspot;
import com.hartwig.serve.datamodel.hotspot.KnownHotspotComparator;
import com.hartwig.serve.datamodel.immuno.ActionableHLA;
import com.hartwig.serve.datamodel.immuno.ActionableHLAComparator;
import com.hartwig.serve.datamodel.immuno.ImmutableActionableHLA;
import com.hartwig.serve.datamodel.range.ActionableRange;
import com.hartwig.serve.datamodel.range.ActionableRangeComparator;
import com.hartwig.serve.datamodel.range.ImmutableActionableRange;
import com.hartwig.serve.datamodel.range.ImmutableKnownCodon;
import com.hartwig.serve.datamodel.range.ImmutableKnownExon;
import com.hartwig.serve.datamodel.range.KnownCodon;
import com.hartwig.serve.datamodel.range.KnownCodonComparator;
import com.hartwig.serve.datamodel.range.KnownExon;
import com.hartwig.serve.datamodel.range.KnownExonComparator;

import org.jetbrains.annotations.NotNull;

public final class BackwardsCompatibilityUtil {

    // TODO This class is tied to deprecated CKB knowledgebase, and can be removed (included usages)
    //  once all downstream users switched from "CKB" to "CKB_EVIDENCE"

    private BackwardsCompatibilityUtil() {
    }

    public static void verifyActionableEventsBeforeWrite(@NotNull Iterable<? extends ActionableEvent> actionableEvents) {
        for (ActionableEvent event : actionableEvents) {
            if (event.source() == Knowledgebase.CKB) {
                throw new IllegalStateException("Not allowed to write actionable events with source 'CKB'!");
            }
        }
    }

    public static void verifyKnownEventsBeforeWrite(@NotNull Iterable<? extends KnownEvent> knownEvents) {
        for (KnownEvent event : knownEvents) {
            if (event.sources().contains(Knowledgebase.CKB)) {
                throw new IllegalStateException("Not allowed to write known events with source 'CKB'!");
            }
        }
    }

    @NotNull
    public static List<ActionableCharacteristic> expandActionableCharacteristics(@NotNull List<ActionableCharacteristic> characteristics) {
        return expandActionableEvents(characteristics,
                characteristic -> ImmutableActionableCharacteristic.builder().from(characteristic).source(Knowledgebase.CKB).build(),
                new ActionableCharacteristicComparator());
    }

    @NotNull
    public static List<ActionableFusion> expandActionableFusions(@NotNull List<ActionableFusion> fusions) {
        return expandActionableEvents(fusions,
                fusion -> ImmutableActionableFusion.builder().from(fusion).source(Knowledgebase.CKB).build(),
                new ActionableFusionComparator());
    }

    @NotNull
    public static List<ActionableGene> expandActionableGenes(@NotNull List<ActionableGene> genes) {
        return expandActionableEvents(genes,
                gene -> ImmutableActionableGene.builder().from(gene).source(Knowledgebase.CKB).build(),
                new ActionableGeneComparator());
    }

    @NotNull
    public static List<ActionableHLA> expandActionableHLA(@NotNull List<ActionableHLA> hla) {
        return expandActionableEvents(hla,
                event -> ImmutableActionableHLA.builder().from(event).source(Knowledgebase.CKB).build(),
                new ActionableHLAComparator());
    }

    @NotNull
    public static List<ActionableHotspot> expandActionableHotspots(@NotNull List<ActionableHotspot> hotspots) {
        return expandActionableEvents(hotspots,
                hotspot -> ImmutableActionableHotspot.builder().from(hotspot).source(Knowledgebase.CKB).build(),
                new ActionableHotspotComparator());
    }

    @NotNull
    public static List<ActionableRange> expandActionableRanges(@NotNull List<ActionableRange> ranges) {
        return expandActionableEvents(ranges,
                range -> ImmutableActionableRange.builder().from(range).source(Knowledgebase.CKB).build(),
                new ActionableRangeComparator());
    }

    @NotNull
    public static List<KnownCodon> patchKnownCodons(@NotNull List<KnownCodon> codons) {
        return patchKnownEvents(codons,
                codon -> ImmutableKnownCodon.builder().from(codon).addSources(Knowledgebase.CKB).build(),
                new KnownCodonComparator());
    }

    @NotNull
    public static List<KnownCopyNumber> patchKnownCopyNumbers(@NotNull List<KnownCopyNumber> copyNumbers) {
        return patchKnownEvents(copyNumbers,
                copyNumber -> ImmutableKnownCopyNumber.builder().from(copyNumber).addSources(Knowledgebase.CKB).build(),
                new KnownCopyNumberComparator());
    }

    @NotNull
    public static List<KnownExon> patchKnownExons(@NotNull List<KnownExon> exons) {
        return patchKnownEvents(exons,
                exon -> ImmutableKnownExon.builder().from(exon).addSources(Knowledgebase.CKB).build(),
                new KnownExonComparator());
    }

    @NotNull
    public static List<KnownFusion> patchKnownFusions(@NotNull List<KnownFusion> fusions) {
        return patchKnownEvents(fusions,
                fusion -> ImmutableKnownFusion.builder().from(fusion).addSources(Knowledgebase.CKB).build(),
                new KnownFusionComparator());
    }

    @NotNull
    public static List<KnownGene> patchKnownGenes(@NotNull List<KnownGene> genes) {
        return patchKnownEvents(genes,
                gene -> ImmutableKnownGene.builder().from(gene).addSources(Knowledgebase.CKB).build(),
                new KnownGeneComparator());
    }

    @NotNull
    public static List<KnownHotspot> patchKnownHotspots(@NotNull List<KnownHotspot> hotspots) {
        return patchKnownEvents(hotspots,
                hotspot -> ImmutableKnownHotspot.builder().from(hotspot).addSources(Knowledgebase.CKB).build(),
                new KnownHotspotComparator());
    }

    @NotNull
    private static <T extends ActionableEvent> List<T> expandActionableEvents(@NotNull List<T> events, @NotNull Function<T, T> factory,
            @NotNull Comparator<T> comparator) {
        List<T> expanded = Lists.newArrayList(events);
        for (T event : events) {
            if (event.source() == Knowledgebase.CKB_EVIDENCE) {
                expanded.add(factory.apply(event));
            }
        }
        expanded.sort(comparator);
        return expanded;
    }

    @NotNull
    private static <T extends KnownEvent> List<T> patchKnownEvents(@NotNull List<T> events, @NotNull Function<T, T> factory,
            @NotNull Comparator<T> comparator) {
        List<T> patched = Lists.newArrayList();
        for (T event : events) {
            T patch = event;
            if (event.sources().contains(Knowledgebase.CKB_EVIDENCE)) {
                patch = factory.apply(event);
            }
            patched.add(patch);
        }
        patched.sort(comparator);
        return patched;
    }

}
