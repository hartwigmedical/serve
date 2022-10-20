package com.hartwig.serve.extraction;

import java.util.List;

import com.hartwig.serve.datamodel.characteristic.TumorCharacteristic;
import com.hartwig.serve.datamodel.gene.GeneLevelAnnotation;
import com.hartwig.serve.datamodel.hotspot.VariantHotspot;
import com.hartwig.serve.extraction.codon.CodonAnnotation;
import com.hartwig.serve.extraction.copynumber.KnownCopyNumber;
import com.hartwig.serve.extraction.exon.ExonAnnotation;
import com.hartwig.serve.extraction.fusion.KnownFusionPair;
import com.hartwig.serve.extraction.immuno.ImmunoHLA;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(allParameters = true,
             passAnnotations = { NotNull.class, Nullable.class })
public abstract class EventExtractorOutput {

    @Nullable
    public abstract List<VariantHotspot> hotspots();

    @Nullable
    public abstract List<CodonAnnotation> codons();

    @Nullable
    public abstract List<ExonAnnotation> exons();

    @Nullable
    public abstract GeneLevelAnnotation geneLevelEvent();

    @Nullable
    public abstract KnownCopyNumber knownCopyNumber();

    @Nullable
    public abstract KnownFusionPair knownFusionPair();

    @Nullable
    public abstract TumorCharacteristic characteristic();

    @Nullable
    public abstract ImmunoHLA hla();
}
