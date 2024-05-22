package com.hartwig.serve.ckb.datamodel.variant;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.hartwig.serve.ckb.datamodel.reference.ReferenceFactory;
import com.hartwig.serve.ckb.json.CkbJsonDatabase;
import com.hartwig.serve.ckb.json.common.GeneInfo;
import com.hartwig.serve.ckb.json.common.VariantInfo;
import com.hartwig.serve.ckb.json.gene.JsonGene;
import com.hartwig.serve.ckb.json.variant.JsonCategoryVariantPath;
import com.hartwig.serve.ckb.json.variant.JsonTranscriptCoordinate;
import com.hartwig.serve.ckb.json.variant.JsonVariant;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class VariantFactory {
    @NotNull
    private final CkbJsonDatabase ckbJsonDatabase;
    @NotNull
    private final Map<Integer, JsonVariant> variantsById;
    @NotNull
    private final Map<Integer, JsonGene> genesById;

    public VariantFactory(@NotNull CkbJsonDatabase ckbJsonDatabase) {
        this.ckbJsonDatabase = ckbJsonDatabase;
        variantsById = ckbJsonDatabase.variants().stream().collect(Collectors.toMap(JsonVariant::id, variant -> variant));
        genesById = ckbJsonDatabase.genes().stream().collect(Collectors.toMap(JsonGene::id, gene -> gene));
    }

    @NotNull
    public List<Variant> extractVariants(@NotNull List<VariantInfo> variantInfos) {
        return variantInfos.stream().map(this::resolveVariant).collect(Collectors.toList());
    }

    @NotNull
    private Variant resolveVariant(@NotNull VariantInfo variantInfo) {
        if (variantsById.containsKey(variantInfo.id())) {
            JsonVariant variant = variantsById.get(variantInfo.id());
            return ImmutableVariant.builder()
                    .id(variant.id())
                    .createDate(variant.createDate())
                    .updateDate(variant.updateDate())
                    .fullName(variant.fullName())
                    .variant(variant.variant())
                    .impact(variant.impact())
                    .proteinEffect(variant.proteinEffect())
                    .type(variant.type())
                    .associatedWithDrugResistance(variant.associatedWithDrugResistance())
                    .transformingActivity(variant.transformingActivity())
                    .polymorphism(variant.polymorphism())
                    .hotspotReference(variant.hotspotReference())
                    .isHotspot(variant.hotspotReference() != null)
                    .gene(resolveGene(variant.gene()))
                    .referenceTranscriptCoordinate(convertReferenceTranscriptCoordinate(variant.referenceTranscriptCoordinate()))
                    .allTranscriptCoordinates(convertAllTranscriptCoordinates(variant.allTranscriptCoordinates()))
                    .categoryVariantPaths(convertCategoryVariantPaths(variant.categoryVariantPaths()))
                    .memberVariants(convertMemberVariants(variant.memberVariants()))
                    .description(ReferenceFactory.extractDescription("variant", variant.id(), variant.descriptions()))
                    .references(ReferenceFactory.extractDescriptionReferences(ckbJsonDatabase, variant.descriptions()))
                    .build();
        }

        throw new IllegalStateException("Could not resolve CKB variant with id '" + variantInfo.id() + "'");
    }

    @NotNull
    private Gene resolveGene(@NotNull GeneInfo geneInfo) {
        if (genesById.containsKey(geneInfo.id())) {
            JsonGene gene = genesById.get(geneInfo.id());
            return ImmutableGene.builder()
                    .id(gene.id())
                    .createDate(gene.createDate())
                    .updateDate(gene.updateDate())
                    .geneSymbol(gene.geneSymbol())
                    .geneRole(gene.geneRole())
                    .entrezId(gene.entrezId())
                    .chromosome(gene.chromosome())
                    .mapLocation(gene.mapLocation())
                    .canonicalTranscript(gene.canonicalTranscript())
                    .terms(gene.terms())
                    .synonyms(gene.synonyms())
                    .description(ReferenceFactory.extractDescription("gene", gene.id(), gene.descriptions()))
                    .references(ReferenceFactory.extractDescriptionReferences(ckbJsonDatabase, gene.descriptions()))
                    .build();
        }

        throw new IllegalStateException("Could not resolve CKB gene with id '" + geneInfo.id() + "'");
    }

    @Nullable
    private static TranscriptCoordinate convertReferenceTranscriptCoordinate(@Nullable JsonTranscriptCoordinate coordinate) {
        if (coordinate == null) {
            return null;
        }

        return convertTranscriptCoordinate(coordinate);
    }

    @NotNull
    private static List<TranscriptCoordinate> convertAllTranscriptCoordinates(@NotNull List<JsonTranscriptCoordinate> coordinates) {
        return coordinates.stream().map(VariantFactory::convertTranscriptCoordinate).collect(Collectors.toList());
    }

    @NotNull
    private static TranscriptCoordinate convertTranscriptCoordinate(@NotNull JsonTranscriptCoordinate coordinate) {
        return ImmutableTranscriptCoordinate.builder()
                .id(coordinate.id())
                .transcript(coordinate.transcript())
                .gDna(coordinate.gDNA())
                .cDna(coordinate.cDNA())
                .protein(coordinate.protein())
                .sourceDb(coordinate.sourceDB())
                .refGenomeBuild(coordinate.refGenomeBuild())
                .build();
    }

    @NotNull
    private static List<String> convertCategoryVariantPaths(@NotNull List<JsonCategoryVariantPath> jsonCategoryVariantPaths) {
        return jsonCategoryVariantPaths.stream().map(JsonCategoryVariantPath::variantPath).collect(Collectors.toList());
    }

    @NotNull
    private static List<MemberVariant> convertMemberVariants(@NotNull List<VariantInfo> variantsMembers) {
        List<MemberVariant> memberVariants = Lists.newArrayList();

        for (VariantInfo memberVariant : variantsMembers) {
            memberVariants.add(ImmutableMemberVariant.builder()
                    .id(memberVariant.id())
                    .fullName(memberVariant.fullName())
                    .impact(memberVariant.impact())
                    .proteinEffect(memberVariant.proteinEffect())
                    .build());
        }

        return memberVariants;
    }
}