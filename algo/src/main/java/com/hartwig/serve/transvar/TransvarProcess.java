package com.hartwig.serve.transvar;

import java.io.IOException;
import java.util.List;

import com.hartwig.serve.transvar.datamodel.TransvarRecord;

import org.jetbrains.annotations.NotNull;

interface TransvarProcess {

    @NotNull
    List<TransvarRecord> runTransvarPanno(@NotNull String gene, @NotNull String proteinAnnotation)
            throws InterruptedException, IOException;
}
