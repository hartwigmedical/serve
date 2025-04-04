package com.hartwig.serve.sources.vicc;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hartwig.serve.datamodel.efficacy.EfficacyEvidence;
import com.hartwig.serve.datamodel.molecular.characteristic.TumorCharacteristic;
import com.hartwig.serve.datamodel.molecular.fusion.FusionPair;
import com.hartwig.serve.datamodel.molecular.gene.GeneAnnotation;
import com.hartwig.serve.datamodel.molecular.hotspot.VariantAnnotation;
import com.hartwig.serve.extraction.codon.CodonAnnotation;
import com.hartwig.serve.extraction.events.EventInterpretation;
import com.hartwig.serve.extraction.exon.ExonAnnotation;
import com.hartwig.serve.extraction.immuno.ImmunoHLA;
import com.hartwig.serve.vicc.datamodel.Feature;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class ViccExtractionResult {

    @NotNull
    public abstract Map<Feature, EventInterpretation> eventInterpretationPerFeature();

    @NotNull
    public abstract Map<Feature, List<VariantAnnotation>> variantsPerFeature();

    @NotNull
    public abstract Map<Feature, List<CodonAnnotation>> codonsPerFeature();

    @NotNull
    public abstract Map<Feature, List<ExonAnnotation>> exonsPerFeature();

    @NotNull
    public abstract Map<Feature, GeneAnnotation> geneLevelEventsPerFeature();

    @NotNull
    public abstract Map<Feature, GeneAnnotation> ampsDelsPerFeature();

    @NotNull
    public abstract Map<Feature, FusionPair> fusionsPerFeature();

    @NotNull
    public abstract Map<Feature, TumorCharacteristic> characteristicsPerFeature();

    @NotNull
    public abstract Map<Feature, ImmunoHLA> HLAPerFeature();

    @NotNull
    public abstract Set<EfficacyEvidence> efficacyEvidences();
}