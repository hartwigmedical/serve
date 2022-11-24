package com.hartwig.serve.transvar;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.serve.common.ensemblcache.EnsemblDataCache;
import com.hartwig.serve.datamodel.RefGenome;
import com.hartwig.serve.extraction.hotspot.Hotspot;
import com.hartwig.serve.extraction.hotspot.ProteinResolver;
import com.hartwig.serve.extraction.util.EnsemblFunctions;
import com.hartwig.serve.extraction.util.HmfTranscriptRegion;
import com.hartwig.serve.extraction.util.KeyFormatter;
import com.hartwig.serve.transvar.datamodel.TransvarRecord;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Transvar implements ProteinResolver {

    private static final Logger LOGGER = LogManager.getLogger(Transvar.class);

    @NotNull
    private final TransvarProcess process;
    @NotNull
    private final TransvarInterpreter interpreter;
    @NotNull
    private final EnsemblDataCache ensemblDataCache;
    @NotNull
    private final Set<String> unresolvedProteinAnnotations = Sets.newHashSet();

    @NotNull
    public static Transvar withRefGenome(@NotNull RefGenome refGenome, @NotNull String refGenomeFastaFile,
            @NotNull EnsemblDataCache ensemblDataCache) throws FileNotFoundException {
        return new Transvar(new TransvarProcessImpl(refGenome, refGenomeFastaFile),
                TransvarInterpreter.withRefGenome(refGenome, refGenomeFastaFile),
                ensemblDataCache);
    }

    @VisibleForTesting
    Transvar(@NotNull final TransvarProcess process, @NotNull final TransvarInterpreter interpreter,
            @NotNull final EnsemblDataCache ensemblDataCache) {
        this.process = process;
        this.interpreter = interpreter;
        this.ensemblDataCache = ensemblDataCache;
    }

    @Override
    @NotNull
    public List<Hotspot> resolve(@NotNull String gene, @Nullable String specificTranscript, @NotNull String proteinAnnotation) {
        List<Hotspot> hotspots = extractHotspotsForAnnotation(gene, specificTranscript, proteinAnnotation);

        String proteinKey = KeyFormatter.toProteinKey(gene, specificTranscript, proteinAnnotation);
        LOGGER.debug("Converted '{}' to {} hotspot(s)", proteinKey, hotspots.size());
        if (hotspots.isEmpty()) {
            unresolvedProteinAnnotations.add(proteinKey);
        }

        return hotspots;
    }

    @Override
    @NotNull
    public Set<String> unresolvedProteinAnnotations() {
        return unresolvedProteinAnnotations;
    }

    @NotNull
    private List<Hotspot> extractHotspotsForAnnotation(@NotNull String gene, @Nullable String specificTranscript,
            @NotNull String proteinAnnotation) {
        List<TransvarRecord> records = runTransvarProcess(gene, proteinAnnotation);

        if (records.isEmpty()) {
            LOGGER.warn("Transvar could not resolve any genomic coordinates for '{}:p.{}'", gene, proteinAnnotation);
            return Lists.newArrayList();
        }

        HmfTranscriptRegion canonicalTranscript = EnsemblFunctions.findCanonicalTranscript(ensemblDataCache, gene);
        if (canonicalTranscript == null) {
            LOGGER.warn("Could not find canonical transcript for '{}' in ensembl data cache. Skipping hotspot extraction for 'p.{}'",
                    gene,
                    proteinAnnotation);
            return Lists.newArrayList();
        }

        TransvarRecord best = pickBestRecord(records, specificTranscript, canonicalTranscript.transcriptId());
        if (specificTranscript != null && !best.transcript().equals(specificTranscript)) {
            LOGGER.warn("No record found on specific transcript '{}'. "
                            + "Instead a record was resolved for '{}' for {}:p.{}. Skipping interpretation",
                    specificTranscript,
                    best.transcript(),
                    gene,
                    proteinAnnotation);
            return Lists.newArrayList();
        }

        LOGGER.debug("Interpreting transvar record: '{}'", best);
        // This is assuming every transcript on a gene lies on the same strand.
        List<Hotspot> hotspots = interpreter.convertRecordToHotspots(best, canonicalTranscript.strand());

        if (hotspots.isEmpty()) {
            LOGGER.warn("Could not derive any hotspots from record {} for '{}:p.{} - {}'",
                    best,
                    gene,
                    proteinAnnotation,
                    specificTranscript);
        }

        return hotspots;
    }

    @NotNull
    private List<TransvarRecord> runTransvarProcess(@NotNull String gene, @NotNull String proteinAnnotation) {
        List<TransvarRecord> records;
        try {
            records = process.runTransvarPanno(gene, proteinAnnotation);
        } catch (InterruptedException | IOException e) {
            LOGGER.error("Exception thrown by transvar");
            throw new RuntimeException(e);
        }
        return records;
    }

    @NotNull
    private static TransvarRecord pickBestRecord(@NotNull List<TransvarRecord> records, @Nullable String specificTranscript,
            @NotNull String canonicalTranscript) {
        assert !records.isEmpty();

        TransvarRecord specificRecord = null;
        TransvarRecord canonicalRecord = null;
        TransvarRecord bestRecord = null;
        for (TransvarRecord record : records) {
            if (specificTranscript != null && record.transcript().equals(specificTranscript)) {
                specificRecord = record;
            } else if (record.transcript().equals(canonicalTranscript)) {
                canonicalRecord = record;
            } else {
                bestRecord = record;
            }
        }

        if (specificRecord != null) {
            return specificRecord;
        }

        if (canonicalRecord != null) {
            return canonicalRecord;
        }

        return bestRecord;
    }
}
