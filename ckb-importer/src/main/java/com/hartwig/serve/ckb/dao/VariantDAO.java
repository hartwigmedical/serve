package com.hartwig.serve.ckb.dao;

import com.hartwig.serve.ckb.database.Tables;
import com.hartwig.serve.ckb.database.tables.Categoryvariantpath;
import com.hartwig.serve.ckb.database.tables.Gene;
import com.hartwig.serve.ckb.database.tables.Genesynonym;
import com.hartwig.serve.ckb.database.tables.Geneterm;
import com.hartwig.serve.ckb.database.tables.Membervariant;
import com.hartwig.serve.ckb.database.tables.Transcriptcoordinate;
import com.hartwig.serve.ckb.database.tables.Variant;
import com.hartwig.serve.ckb.datamodel.reference.Reference;
import com.hartwig.serve.ckb.datamodel.variant.MemberVariant;
import com.hartwig.serve.ckb.datamodel.variant.TranscriptCoordinate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;

class VariantDAO {

    @NotNull
    private final DSLContext context;

    public VariantDAO(@NotNull final DSLContext context) {
        this.context = context;
    }

    public void deleteAll() {
        // Note that deletions should go from branch to root
        context.deleteFrom(Membervariant.MEMBERVARIANT).execute();
        context.deleteFrom(Categoryvariantpath.CATEGORYVARIANTPATH).execute();
        context.deleteFrom(Transcriptcoordinate.TRANSCRIPTCOORDINATE).execute();
        context.deleteFrom(Tables.VARIANTREFERENCE).execute();

        context.deleteFrom(Tables.GENEREFERENCE).execute();
        context.deleteFrom(Genesynonym.GENESYNONYM).execute();
        context.deleteFrom(Geneterm.GENETERM).execute();
        context.deleteFrom(Gene.GENE).execute();

        context.deleteFrom(Variant.VARIANT).execute();
    }

    public void write(@NotNull com.hartwig.serve.ckb.datamodel.variant.Variant variant, int ckbEntryId) {
        int id = context.insertInto(Variant.VARIANT,
                        Variant.VARIANT.CKBENTRYID,
                        Variant.VARIANT.CKBVARIANTID,
                        Variant.VARIANT.CREATEDATE,
                        Variant.VARIANT.UPDATEDATE,
                        Variant.VARIANT.FULLNAME,
                        Variant.VARIANT.VARIANT_,
                        Variant.VARIANT.IMPACT,
                        Variant.VARIANT.PROTEINEFFECT,
                        Variant.VARIANT.TYPE,
                        Variant.VARIANT.ASSOCIATEDWITHDRUGRESISTANCE,
                        Variant.VARIANT.TRANSFORMINGACTIVITY,
                        Variant.VARIANT.POLYMORPHISM,
                        Variant.VARIANT.HOTSPOTREFERENCE,
                        Variant.VARIANT.ISHOTSPOT,
                        Variant.VARIANT.DESCRIPTION)
                .values(ckbEntryId,
                        variant.id(),
                        variant.createDate(),
                        variant.updateDate(),
                        variant.fullName(),
                        variant.variant(),
                        variant.impact(),
                        variant.proteinEffect(),
                        variant.type(),
                        variant.associatedWithDrugResistance(),
                        variant.transformingActivity(),
                        variant.polymorphism(),
                        variant.hotspotReference(),
                        Util.toByte(variant.isHotspot()),
                        variant.description())
                .returning(Variant.VARIANT.ID)
                .fetchOne()
                .getValue(Variant.VARIANT.ID);

        writeGene(variant.gene(), id);

        writeTranscriptCoordinate(variant.referenceTranscriptCoordinate(), id, true);
        for (TranscriptCoordinate transcriptCoordinate : variant.allTranscriptCoordinates()) {
            writeTranscriptCoordinate(transcriptCoordinate, id, false);
        }

        for (String categoryVariantPath : variant.categoryVariantPaths()) {
            context.insertInto(Categoryvariantpath.CATEGORYVARIANTPATH,
                    Categoryvariantpath.CATEGORYVARIANTPATH.VARIANTID,
                    Categoryvariantpath.CATEGORYVARIANTPATH.VARIANTPATH).values(id, categoryVariantPath).execute();
        }

        for (MemberVariant memberVariant : variant.memberVariants()) {
            writeMemberVariant(memberVariant, id);
        }

        for (Reference variantReference : variant.references()) {
            writeVariantReference(variantReference, id);
        }
    }

    private void writeGene(@NotNull com.hartwig.serve.ckb.datamodel.variant.Gene gene, int variantId) {
        int id = context.insertInto(Gene.GENE,
                        Gene.GENE.VARIANTID,
                        Gene.GENE.CKBGENEID,
                        Gene.GENE.CREATEDATE,
                        Gene.GENE.UPDATEDATE,
                        Gene.GENE.GENESYMBOL,
                        Gene.GENE.GENEROLE,
                        Gene.GENE.ENTREZID,
                        Gene.GENE.CHROMOSOME,
                        Gene.GENE.MAPLOCATION,
                        Gene.GENE.CANONICALTRANSCRIPT,
                        Gene.GENE.DESCRIPTION)
                .values(variantId,
                        gene.id(),
                        gene.createDate(),
                        gene.updateDate(),
                        gene.geneSymbol(),
                        gene.geneRole(),
                        gene.entrezId(),
                        gene.chromosome(),
                        gene.mapLocation(),
                        gene.canonicalTranscript(),
                        gene.description())
                .returning(Gene.GENE.ID)
                .fetchOne()
                .getValue(Gene.GENE.ID);

        for (String term : gene.terms()) {
            context.insertInto(Geneterm.GENETERM, Geneterm.GENETERM.GENEID, Geneterm.GENETERM.TERM).values(id, term).execute();
        }

        for (String synonym : gene.synonyms()) {
            context.insertInto(Genesynonym.GENESYNONYM, Genesynonym.GENESYNONYM.GENEID, Genesynonym.GENESYNONYM.SYNONYM)
                    .values(id, synonym)
                    .execute();
        }

        for (Reference geneReference : gene.references()) {
            writeGeneReference(geneReference, id);
        }
    }

    private void writeGeneReference(@NotNull Reference reference, int geneId) {
        context.insertInto(Tables.GENEREFERENCE,
                        Tables.GENEREFERENCE.GENEID,
                        Tables.GENEREFERENCE.CKBREFERENCEID,
                        Tables.GENEREFERENCE.PUBMEDID,
                        Tables.GENEREFERENCE.TITLE,
                        Tables.GENEREFERENCE.SHORTJOURNALTITLE,
                        Tables.GENEREFERENCE.PAGES,
                        Tables.GENEREFERENCE.ABSTRACTTEXT,
                        Tables.GENEREFERENCE.URL,
                        Tables.GENEREFERENCE.JOURNAL,
                        Tables.GENEREFERENCE.AUTHORS,
                        Tables.GENEREFERENCE.VOLUME,
                        Tables.GENEREFERENCE.ISSUE,
                        Tables.GENEREFERENCE.DATE,
                        Tables.GENEREFERENCE.YEAR)
                .values(geneId,
                        reference.id(),
                        reference.pubMedId(),
                        reference.title(),
                        reference.shortJournalTitle(),
                        reference.pages(),
                        reference.abstractText(),
                        reference.url(),
                        reference.journal(),
                        reference.authors(),
                        reference.volume(),
                        reference.issue(),
                        reference.date(),
                        reference.year())
                .execute();
    }

    private void writeVariantReference(@NotNull Reference reference, int variantId) {
        context.insertInto(Tables.VARIANTREFERENCE,
                        Tables.VARIANTREFERENCE.VARIANTID,
                        Tables.VARIANTREFERENCE.CKBREFERENCEID,
                        Tables.VARIANTREFERENCE.PUBMEDID,
                        Tables.VARIANTREFERENCE.TITLE,
                        Tables.VARIANTREFERENCE.SHORTJOURNALTITLE,
                        Tables.VARIANTREFERENCE.PAGES,
                        Tables.VARIANTREFERENCE.ABSTRACTTEXT,
                        Tables.VARIANTREFERENCE.URL,
                        Tables.VARIANTREFERENCE.JOURNAL,
                        Tables.VARIANTREFERENCE.AUTHORS,
                        Tables.VARIANTREFERENCE.VOLUME,
                        Tables.VARIANTREFERENCE.ISSUE,
                        Tables.VARIANTREFERENCE.DATE,
                        Tables.VARIANTREFERENCE.YEAR)
                .values(variantId,
                        reference.id(),
                        reference.pubMedId(),
                        reference.title(),
                        reference.shortJournalTitle(),
                        reference.pages(),
                        reference.abstractText(),
                        reference.url(),
                        reference.journal(),
                        reference.authors(),
                        reference.volume(),
                        reference.issue(),
                        reference.date(),
                        reference.year())
                .execute();
    }

    private void writeTranscriptCoordinate(@Nullable TranscriptCoordinate transcriptCoordinate, int variantId,
            boolean isReferenceTranscriptCoordinate) {
        if (transcriptCoordinate != null) {
            context.insertInto(Transcriptcoordinate.TRANSCRIPTCOORDINATE,
                            Transcriptcoordinate.TRANSCRIPTCOORDINATE.VARIANTID,
                            Transcriptcoordinate.TRANSCRIPTCOORDINATE.ISREFERENCETRANSCRIPTCOORDINATE,
                            Transcriptcoordinate.TRANSCRIPTCOORDINATE.TRANSCRIPT,
                            Transcriptcoordinate.TRANSCRIPTCOORDINATE.GDNA,
                            Transcriptcoordinate.TRANSCRIPTCOORDINATE.CDNA,
                            Transcriptcoordinate.TRANSCRIPTCOORDINATE.PROTEIN,
                            Transcriptcoordinate.TRANSCRIPTCOORDINATE.SOURCEDB,
                            Transcriptcoordinate.TRANSCRIPTCOORDINATE.REFGENOMEBUILD)
                    .values(variantId,
                            Util.toByte(isReferenceTranscriptCoordinate),
                            transcriptCoordinate.transcript(),
                            transcriptCoordinate.gDna(),
                            transcriptCoordinate.cDna(),
                            transcriptCoordinate.protein(),
                            transcriptCoordinate.sourceDb(),
                            transcriptCoordinate.refGenomeBuild())
                    .execute();
        }
    }

    private void writeMemberVariant(@NotNull MemberVariant memberVariant, int variantId) {
        context.insertInto(Membervariant.MEMBERVARIANT,
                        Membervariant.MEMBERVARIANT.VARIANTID,
                        Membervariant.MEMBERVARIANT.CKBVARIANTID,
                        Membervariant.MEMBERVARIANT.FULLNAME,
                        Membervariant.MEMBERVARIANT.IMPACT,
                        Membervariant.MEMBERVARIANT.PROTEINEFFECT)
                .values(variantId, memberVariant.id(), memberVariant.fullName(), memberVariant.impact(), memberVariant.proteinEffect())
                .execute();
    }
}
