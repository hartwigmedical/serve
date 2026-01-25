package com.hartwig.serve.ckb.dao;

import com.hartwig.serve.ckb.database.tables.Indication;
import com.hartwig.serve.ckb.database.tables.Indicationaltid;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

class IndicationDAO {

    @NotNull
    private final DSLContext context;
    @NotNull
    private final BatchInserter batchInserter;
    @NotNull
    private final IdAllocator idAllocator;

    public IndicationDAO(@NotNull final DSLContext context, @NotNull final BatchInserter batchInserter, @NotNull final IdAllocator idAllocator) {
        this.context = context;
        this.batchInserter = batchInserter;
        this.idAllocator = idAllocator;
    }

    public void deleteAll() {
        // Note that deletions should go from branch to root
        context.deleteFrom(Indicationaltid.INDICATIONALTID).execute();
        context.deleteFrom(Indication.INDICATION).execute();
    }

    public int write(@NotNull com.hartwig.serve.ckb.datamodel.indication.Indication indication) {
        int id = idAllocator.nextIndicationId();
        batchInserter.add(context.insertInto(Indication.INDICATION,
                        Indication.INDICATION.ID,
                        Indication.INDICATION.CKBINDICATIONID,
                        Indication.INDICATION.NAME,
                        Indication.INDICATION.SOURCE,
                        Indication.INDICATION.DEFINITION,
                        Indication.INDICATION.CURRENTPREFERREDTERM,
                        Indication.INDICATION.LASTUPDATEDATEFROMDO,
                        Indication.INDICATION.TERMID)
                .values(id,
                        indication.id(),
                        indication.name(),
                        indication.source(),
                        indication.definition(),
                        indication.currentPreferredTerm(),
                        indication.lastUpdateDateFromDO(),
                        indication.termId()));

        for (String altId : indication.altIds()) {
            batchInserter.add(context.insertInto(Indicationaltid.INDICATIONALTID,
                    Indicationaltid.INDICATIONALTID.INDICATIONID,
                    Indicationaltid.INDICATIONALTID.ALTID).values(id, altId));
        }

        return id;
    }
}
