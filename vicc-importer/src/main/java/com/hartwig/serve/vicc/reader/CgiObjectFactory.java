package com.hartwig.serve.vicc.reader;

import static com.hartwig.serve.common.json.Json.string;
import static com.hartwig.serve.common.json.Json.stringList;

import com.google.gson.JsonObject;
import com.hartwig.serve.vicc.datamodel.cgi.Cgi;
import com.hartwig.serve.vicc.datamodel.cgi.ImmutableCgi;

import org.jetbrains.annotations.NotNull;

final class CgiObjectFactory {

    private CgiObjectFactory() {
    }

    @NotNull
    static Cgi create(@NotNull JsonObject cgiObject) {
        ViccDatamodelCheckerFactory.cgiEntryChecker().check(cgiObject);

        return ImmutableCgi.builder()
                .gene(string(cgiObject, "Gene"))
                .biomarker(string(cgiObject, "Biomarker"))
                .alteration(string(cgiObject, "Alteration"))
                .alterationType(string(cgiObject, "Alteration type"))
                .transcripts(stringList(cgiObject, "transcript"))
                .individualMutations(stringList(cgiObject, "individual_mutation"))
                .gDNA(stringList(cgiObject, "gDNA"))
                .cDNA(stringList(cgiObject, "cDNA"))
                .info(stringList(cgiObject, "info"))
                .regions(stringList(cgiObject, "region"))
                .strands(stringList(cgiObject, "strand"))
                .association(string(cgiObject, "Association"))
                .drug(string(cgiObject, "Drug"))
                .drugFamily(string(cgiObject, "Drug family"))
                .drugFullName(string(cgiObject, "Drug full name"))
                .drugStatus(string(cgiObject, "Drug status"))
                .targeting(string(cgiObject, "Targeting"))
                .primaryTumorType(string(cgiObject, "Primary Tumor type"))
                .metastaticTumorType(string(cgiObject, "Metastatic Tumor Type"))
                .evidenceLevel(string(cgiObject, "Evidence level"))
                .source(string(cgiObject, "Source"))
                .curator(string(cgiObject, "Curator"))
                .assayType(string(cgiObject, "Assay type"))
                .build();
    }
}
