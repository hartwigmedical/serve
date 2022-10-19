package com.hartwig.serve.vicc.reader;

import static com.hartwig.serve.common.utils.json.JsonFunctions.nullableString;
import static com.hartwig.serve.common.utils.json.JsonFunctions.optionalJsonArray;
import static com.hartwig.serve.common.utils.json.JsonFunctions.optionalJsonObject;
import static com.hartwig.serve.common.utils.json.JsonFunctions.optionalNullableString;
import static com.hartwig.serve.common.utils.json.JsonFunctions.optionalString;
import static com.hartwig.serve.common.utils.json.JsonFunctions.optionalStringList;
import static com.hartwig.serve.common.utils.json.JsonFunctions.string;
import static com.hartwig.serve.common.utils.json.JsonFunctions.stringList;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.hartwig.serve.common.utils.json.JsonDatamodelChecker;
import com.hartwig.serve.vicc.annotation.TranscriptExtractor;
import com.hartwig.serve.vicc.datamodel.Association;
import com.hartwig.serve.vicc.datamodel.EnvironmentalContext;
import com.hartwig.serve.vicc.datamodel.Evidence;
import com.hartwig.serve.vicc.datamodel.EvidenceInfo;
import com.hartwig.serve.vicc.datamodel.EvidenceType;
import com.hartwig.serve.vicc.datamodel.Feature;
import com.hartwig.serve.vicc.datamodel.FeatureAttribute;
import com.hartwig.serve.vicc.datamodel.FeatureInfo;
import com.hartwig.serve.vicc.datamodel.GeneIdentifier;
import com.hartwig.serve.vicc.datamodel.ImmutableAssociation;
import com.hartwig.serve.vicc.datamodel.ImmutableEnvironmentalContext;
import com.hartwig.serve.vicc.datamodel.ImmutableEvidence;
import com.hartwig.serve.vicc.datamodel.ImmutableEvidenceInfo;
import com.hartwig.serve.vicc.datamodel.ImmutableEvidenceType;
import com.hartwig.serve.vicc.datamodel.ImmutableFeature;
import com.hartwig.serve.vicc.datamodel.ImmutableFeatureAttribute;
import com.hartwig.serve.vicc.datamodel.ImmutableFeatureInfo;
import com.hartwig.serve.vicc.datamodel.ImmutableGeneIdentifier;
import com.hartwig.serve.vicc.datamodel.ImmutablePhenotype;
import com.hartwig.serve.vicc.datamodel.ImmutablePhenotypeType;
import com.hartwig.serve.vicc.datamodel.ImmutableSequenceOntology;
import com.hartwig.serve.vicc.datamodel.ImmutableTaxonomy;
import com.hartwig.serve.vicc.datamodel.ImmutableViccEntry;
import com.hartwig.serve.vicc.datamodel.KbSpecificObject;
import com.hartwig.serve.vicc.datamodel.Phenotype;
import com.hartwig.serve.vicc.datamodel.PhenotypeType;
import com.hartwig.serve.vicc.datamodel.SequenceOntology;
import com.hartwig.serve.vicc.datamodel.Taxonomy;
import com.hartwig.serve.vicc.datamodel.ViccEntry;
import com.hartwig.serve.vicc.datamodel.ViccSource;
import com.hartwig.serve.vicc.selection.ImmutableViccQuerySelection;
import com.hartwig.serve.vicc.selection.ViccQuerySelection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class ViccJsonReader {

    private static final Logger LOGGER = LogManager.getLogger(ViccJsonReader.class);

    @NotNull
    private final TranscriptExtractor transcriptExtractor;

    @NotNull
    public static ViccJsonReader buildProductionReader() {
        return new ViccJsonReader(new TranscriptExtractor());
    }

    public ViccJsonReader(@NotNull final TranscriptExtractor transcriptExtractor) {
        this.transcriptExtractor = transcriptExtractor;
    }

    @NotNull
    public List<ViccEntry> readAll(@NotNull String jsonPath) throws IOException {
        ViccQuerySelection includeAll = ImmutableViccQuerySelection.builder().build();
        return readSelection(jsonPath, includeAll);
    }

    @NotNull
    public List<ViccEntry> readSelection(@NotNull String jsonPath, @NotNull ViccQuerySelection querySelection) throws IOException {
        List<ViccEntry> entries = Lists.newArrayList();

        JsonParser parser = new JsonParser();
        JsonReader reader = new JsonReader(new FileReader(jsonPath));
        reader.setLenient(true);

        while (reader.peek() != JsonToken.END_DOCUMENT && (querySelection.maxEntriesToInclude() == null
                || entries.size() < querySelection.maxEntriesToInclude())) {
            JsonObject viccEntryObject = parser.parse(reader).getAsJsonObject();
            ViccSource source = ViccSource.fromViccKnowledgebaseString(string(viccEntryObject, "source"));
            if (querySelection.sourcesToFilterOn() == null || querySelection.sourcesToFilterOn().contains(source)) {
                entries.add(createViccEntry(source, viccEntryObject));
            }
        }

        reader.close();

        return entries;
    }

    @NotNull
    private ViccEntry createViccEntry(@NotNull ViccSource source, @NotNull JsonObject viccEntryObject) {
        ViccDatamodelCheckerFactory.viccEntryChecker().check(viccEntryObject);

        ImmutableViccEntry.Builder viccEntryBuilder = ImmutableViccEntry.builder();
        viccEntryBuilder.source(source);
        viccEntryBuilder.genes(stringList(viccEntryObject, "genes"));
        viccEntryBuilder.geneIdentifiers(createGeneIdentifiers(viccEntryObject.getAsJsonArray("gene_identifiers")));
        viccEntryBuilder.featureNames(optionalStringList(viccEntryObject, "feature_names"));
        viccEntryBuilder.features(createFeatures(viccEntryObject.getAsJsonArray("features")));
        viccEntryBuilder.association(createAssociation(viccEntryObject.getAsJsonObject("association")));
        viccEntryBuilder.tags(stringList(viccEntryObject, "tags"));
        viccEntryBuilder.devTags(stringList(viccEntryObject, "dev_tags"));

        KbSpecificObject kbSpecificObject;
        if (viccEntryObject.has("cgi")) {
            kbSpecificObject = CgiObjectFactory.create(viccEntryObject.getAsJsonObject("cgi"));
        } else if (viccEntryObject.has("brca")) {
            kbSpecificObject = BRCAObjectFactory.create(viccEntryObject.getAsJsonObject("brca"));
        } else if (viccEntryObject.has("sage")) {
            kbSpecificObject = SageObjectFactory.create(viccEntryObject.getAsJsonObject("sage"));
        } else if (viccEntryObject.has("pmkb")) {
            kbSpecificObject = PmkbObjectFactory.create(viccEntryObject.getAsJsonObject("pmkb"));
        } else if (viccEntryObject.has("oncokb")) {
            kbSpecificObject = OncokbObjectFactory.create(viccEntryObject.getAsJsonObject("oncokb"));
        } else if (viccEntryObject.has("jax")) {
            kbSpecificObject = JaxObjectFactory.create(viccEntryObject.getAsJsonObject("jax"));
        } else if (viccEntryObject.has("jax_trials")) {
            kbSpecificObject = JaxTrialsObjectFactory.create(viccEntryObject.getAsJsonObject("jax_trials"));
        } else if (viccEntryObject.has("molecularmatch")) {
            kbSpecificObject = MolecularMatchObjectFactory.create(viccEntryObject.getAsJsonObject("molecularmatch"));
        } else if (viccEntryObject.has("molecularmatch_trials")) {
            kbSpecificObject = MolecularMatchTrialsObjectFactory.create(viccEntryObject.getAsJsonObject("molecularmatch_trials"));
        } else if (viccEntryObject.has("civic")) {
            kbSpecificObject = CivicObjectFactory.create(viccEntryObject.getAsJsonObject("civic"));
        } else {
            throw new IllegalStateException("Could not resolve kb specific object for " + viccEntryObject);
        }

        viccEntryBuilder.kbSpecificObject(kbSpecificObject);
        viccEntryBuilder.transcriptId(transcriptExtractor.extractTranscriptId(source, kbSpecificObject));

        return viccEntryBuilder.build();
    }

    @NotNull
    private static List<GeneIdentifier> createGeneIdentifiers(@NotNull JsonArray geneIdentifierArray) {
        List<GeneIdentifier> geneIdentifierList = Lists.newArrayList();
        JsonDatamodelChecker geneIdentifierChecker = ViccDatamodelCheckerFactory.geneIdentifierChecker();

        for (JsonElement geneIdentifierElement : geneIdentifierArray) {
            JsonObject geneIdentifierObject = geneIdentifierElement.getAsJsonObject();
            geneIdentifierChecker.check(geneIdentifierObject);

            geneIdentifierList.add(ImmutableGeneIdentifier.builder()
                    .symbol(string(geneIdentifierObject, "symbol"))
                    .entrezId(string(geneIdentifierObject, "entrez_id"))
                    .ensemblGeneId(nullableString(geneIdentifierObject, "ensembl_gene_id"))
                    .build());
        }

        return geneIdentifierList;
    }

    @NotNull
    private static List<Feature> createFeatures(@NotNull JsonArray featureArray) {
        List<Feature> featureList = Lists.newArrayList();
        JsonDatamodelChecker featureChecker = ViccDatamodelCheckerFactory.featureChecker();

        for (JsonElement featureElement : featureArray) {
            JsonObject featureObject = featureElement.getAsJsonObject();
            featureChecker.check(featureObject);

            featureList.add(ImmutableFeature.builder()
                    .name(string(featureObject, "name"))
                    .biomarkerType(optionalString(featureObject, "biomarker_type"))
                    .referenceName(optionalString(featureObject, "referenceName"))
                    .chromosome(optionalString(featureObject, "chromosome"))
                    .start(optionalNullableString(featureObject, "start"))
                    .end(optionalNullableString(featureObject, "end"))
                    .ref(optionalNullableString(featureObject, "ref"))
                    .alt(optionalNullableString(featureObject, "alt"))
                    .provenance(optionalStringList(featureObject, "provenance"))
                    .provenanceRule(optionalString(featureObject, "provenance_rule"))
                    .geneSymbol(optionalNullableString(featureObject, "geneSymbol"))
                    .synonyms(optionalStringList(featureObject, "synonyms"))
                    .entrezId(optionalString(featureObject, "entrez_id"))
                    .sequenceOntology(createSequenceOntology(optionalJsonObject(featureObject, "sequence_ontology")))
                    .links(optionalStringList(featureObject, "links"))
                    .description(optionalString(featureObject, "description"))
                    .info(createFeatureInfo(optionalJsonObject(featureObject, "info")))
                    .attribute(createFeatureAttribute(optionalJsonObject(featureObject, "attributes")))
                    .build());
        }

        return featureList;
    }

    @Nullable
    private static FeatureInfo createFeatureInfo(@Nullable JsonObject featureInfoObject) {
        if (featureInfoObject == null) {
            return null;
        }

        ViccDatamodelCheckerFactory.featureInfoChecker().check(featureInfoObject);

        return ImmutableFeatureInfo.builder().germlineOrSomatic(string(featureInfoObject, "germline_or_somatic")).build();
    }

    @Nullable
    private static FeatureAttribute createFeatureAttribute(@Nullable JsonObject featureAttributeObject) {
        if (featureAttributeObject == null) {
            return null;
        }

        ViccDatamodelCheckerFactory.featureAttributeChecker().check(featureAttributeObject);

        return ImmutableFeatureAttribute.builder()
                .aminoAcidChange(extractStringValueFromAttribute(featureAttributeObject.getAsJsonObject("amino_acid_change")))
                .germline(extractStringValueFromAttribute(featureAttributeObject.getAsJsonObject("germline")))
                .partnerGene(extractStringValueFromAttribute(featureAttributeObject.getAsJsonObject("partner_gene")))
                .description(extractStringValueFromAttribute(featureAttributeObject.getAsJsonObject("description")))
                .exons(extractStringValueFromAttribute(featureAttributeObject.getAsJsonObject("exons")))
                .notes(extractStringValueFromAttribute(featureAttributeObject.getAsJsonObject("notes")))
                .cosmic(extractStringValueFromAttribute(featureAttributeObject.getAsJsonObject("cosmic")))
                .effect(extractStringValueFromAttribute(featureAttributeObject.getAsJsonObject("effect")))
                .cnvType(extractStringValueFromAttribute(featureAttributeObject.getAsJsonObject("cnv_type")))
                .id(extractStringValueFromAttribute(featureAttributeObject.getAsJsonObject("id")))
                .cytoband(extractStringValueFromAttribute(featureAttributeObject.getAsJsonObject("cytoband")))
                .variantType(extractStringValueFromAttribute(featureAttributeObject.getAsJsonObject("variant_type")))
                .dnaChange(extractStringValueFromAttribute(featureAttributeObject.getAsJsonObject("dna_change")))
                .codons(extractStringValueFromAttribute(featureAttributeObject.getAsJsonObject("codons")))
                .chromosomeBasedCnv(extractStringValueFromAttribute(featureAttributeObject.getAsJsonObject("chromosome_based_cnv")))
                .transcript(extractStringValueFromAttribute(featureAttributeObject.getAsJsonObject("transcript")))
                .descriptionType(extractStringValueFromAttribute(featureAttributeObject.getAsJsonObject("description_type")))
                .chromosome(extractStringValueFromAttribute(featureAttributeObject.getAsJsonObject("chromosome")))
                .build();
    }

    @Nullable
    private static String extractStringValueFromAttribute(@NotNull JsonObject attributeObject) {
        ViccDatamodelCheckerFactory.featureAttributeObjectChecker().check(attributeObject);

        return nullableString(attributeObject, "string_value");
    }

    @Nullable
    private static SequenceOntology createSequenceOntology(@Nullable JsonObject sequenceOntologyObject) {
        if (sequenceOntologyObject == null) {
            return null;
        }

        ViccDatamodelCheckerFactory.sequenceOntologyChecker().check(sequenceOntologyObject);

        return ImmutableSequenceOntology.builder()
                .hierarchy(optionalStringList(sequenceOntologyObject, "hierarchy"))
                .soid(string(sequenceOntologyObject, "soid"))
                .parentSoid(string(sequenceOntologyObject, "parent_soid"))
                .name(string(sequenceOntologyObject, "name"))
                .parentName(string(sequenceOntologyObject, "parent_name"))
                .build();
    }

    @NotNull
    private static Association createAssociation(@NotNull JsonObject associationObject) {
        ViccDatamodelCheckerFactory.associationChecker().check(associationObject);

        return ImmutableAssociation.builder()
                .variantNames(optionalStringList(associationObject, "variant_name"))
                .evidence(createEvidence(associationObject.getAsJsonArray("evidence")))
                .evidenceLevel(optionalString(associationObject, "evidence_level"))
                .evidenceLabel(optionalNullableString(associationObject, "evidence_label"))
                .responseType(optionalNullableString(associationObject, "response_type"))
                .drugLabels(optionalString(associationObject, "drug_labels"))
                .sourceLink(optionalString(associationObject, "source_link"))
                .publicationUrls(optionalStringList(associationObject, "publication_url"))
                .phenotype(createPhenotype(optionalJsonObject(associationObject, "phenotype")))
                .description(string(associationObject, "description"))
                .environmentalContexts(createEnvironmentalContexts(optionalJsonArray(associationObject, "environmentalContexts")))
                .oncogenic(optionalString(associationObject, "oncogenic"))
                .build();
    }

    @NotNull
    private static Evidence createEvidence(@NotNull JsonArray evidenceArray) {
        // There is a 1-1 relation between association and evidence.
        if (evidenceArray.size() != 1) {
            LOGGER.warn("Evidence array with size unequal to 1 found: {}", evidenceArray);
        }

        JsonObject evidenceObject = evidenceArray.get(0).getAsJsonObject();
        ViccDatamodelCheckerFactory.evidenceChecker().check(evidenceObject);

        return ImmutableEvidence.builder()
                .info(createEvidenceInfo(optionalJsonObject(evidenceObject, "info")))
                .evidenceType(createEvidenceType(evidenceObject.getAsJsonObject("evidenceType")))
                .description(nullableString(evidenceObject, "description"))
                .build();
    }

    @Nullable
    private static EvidenceInfo createEvidenceInfo(@Nullable JsonObject evidenceInfoObject) {
        if (evidenceInfoObject == null) {
            return null;
        }

        ViccDatamodelCheckerFactory.evidenceInfoChecker().check(evidenceInfoObject);

        return ImmutableEvidenceInfo.builder().publications(stringList(evidenceInfoObject, "publications")).build();
    }

    @NotNull
    private static EvidenceType createEvidenceType(@NotNull JsonObject evidenceTypeObject) {
        ViccDatamodelCheckerFactory.evidenceTypeChecker().check(evidenceTypeObject);

        return ImmutableEvidenceType.builder()
                .sourceName(string(evidenceTypeObject, "sourceName"))
                .id(optionalString(evidenceTypeObject, "id"))
                .build();
    }

    @Nullable
    private static List<EnvironmentalContext> createEnvironmentalContexts(@Nullable JsonArray environmentalContextArray) {
        if (environmentalContextArray == null) {
            return null;
        }

        List<EnvironmentalContext> environmentalContextList = Lists.newArrayList();
        JsonDatamodelChecker environmentalContextChecker = ViccDatamodelCheckerFactory.environmentalContextChecker();

        for (JsonElement environmentalContextElement : environmentalContextArray) {
            JsonObject environmentalContextObject = environmentalContextElement.getAsJsonObject();
            environmentalContextChecker.check(environmentalContextObject);

            environmentalContextList.add(ImmutableEnvironmentalContext.builder()
                    .term(optionalString(environmentalContextObject, "term"))
                    .description(string(environmentalContextObject, "description"))
                    .taxonomy(createTaxonomy(optionalJsonObject(environmentalContextObject, "taxonomy")))
                    .source(optionalString(environmentalContextObject, "source"))
                    .usanStem(optionalString(environmentalContextObject, "usan_stem"))
                    .approvedCountries(optionalStringList(environmentalContextObject, "approved_countries"))
                    .toxicity(optionalString(environmentalContextObject, "toxicity"))
                    .id(optionalNullableString(environmentalContextObject, "id"))
                    .build());
        }
        return environmentalContextList;
    }

    @Nullable
    private static Taxonomy createTaxonomy(@Nullable JsonObject taxonomyObject) {
        if (taxonomyObject == null) {
            return null;
        }

        ViccDatamodelCheckerFactory.taxonomyChecker().check(taxonomyObject);

        return ImmutableTaxonomy.builder()
                .kingdom(string(taxonomyObject, "kingdom"))
                .directParent(string(taxonomyObject, "direct-parent"))
                .classs(string(taxonomyObject, "class"))
                .subClass(optionalString(taxonomyObject, "subclass"))
                .superClass(string(taxonomyObject, "superclass"))
                .build();
    }

    @Nullable
    private static Phenotype createPhenotype(@Nullable JsonObject phenotypeObject) {
        if (phenotypeObject == null) {
            return null;
        }

        ViccDatamodelCheckerFactory.phenotypeChecker().check(phenotypeObject);

        return ImmutablePhenotype.builder()
                .type(createPhenotypeType(optionalJsonObject(phenotypeObject, "type")))
                .description(string(phenotypeObject, "description"))
                .family(string(phenotypeObject, "family"))
                .id(optionalString(phenotypeObject, "id"))
                .build();
    }

    @Nullable
    private static PhenotypeType createPhenotypeType(@Nullable JsonObject phenotypeTypeObject) {
        if (phenotypeTypeObject == null) {
            return null;
        }

        ViccDatamodelCheckerFactory.phenotypeTypeChecker().check(phenotypeTypeObject);

        return ImmutablePhenotypeType.builder()
                .source(string(phenotypeTypeObject, "source"))
                .term(string(phenotypeTypeObject, "term"))
                .id(string(phenotypeTypeObject, "id"))
                .build();
    }
}
