package com.hartwig.serve.refgenome;

import java.util.List;
import java.util.Map;

import com.hartwig.serve.common.drivergene.DriverGene;
import com.hartwig.serve.common.ensemblcache.EnsemblDataCache;
import com.hartwig.serve.common.knownfusion.KnownFusionCache;
import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.extraction.variant.ProteinResolver;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class RefGenomeResource {

    @NotNull
    public abstract IndexedFastaSequenceFile refSequence();

    @NotNull
    public abstract List<DriverGene> driverGenes();

    @NotNull
    public abstract KnownFusionCache knownFusionCache();

    @NotNull
    public abstract EnsemblDataCache ensemblDataCache();

    @NotNull
    public abstract Map<RefGenome, String> chainToOtherRefGenomeMap();

    @NotNull
    public abstract ProteinResolver proteinResolver();

}
