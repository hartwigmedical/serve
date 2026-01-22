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

    public IndicationDAO(@NotNull final DSLContext context, @NotNull final BatchInserter batchInserter) {
        this.context = context;
        this.batchInserter = batchInserter;
    }

    public void deleteAll() {
        // Note that deletions should go from branch to root
        context.deleteFrom(Indicationaltid.INDICATIONALTID).execute();
        context.deleteFrom(Indication.INDICATION).execute();
    }

    public int write(@NotNull com.hartwig.serve.ckb.datamodel.indication.Indication indication) {
        int id = context.insertInto(Indication.INDICATION,
                        Indication.INDICATION.CKBINDICATIONID,
                        Indication.INDICATION.NAME,
                        Indication.INDICATION.SOURCE,
                        Indication.INDICATION.DEFINITION,
                        Indication.INDICATION.CURRENTPREFERREDTERM,
                        Indication.INDICATION.LASTUPDATEDATEFROMDO,
                        Indication.INDICATION.TERMID)
                .values(indication.id(),
                        indication.name(),
                        indication.source(),
                        indication.definition(),
                        indication.currentPreferredTerm(),
                        indication.lastUpdateDateFromDO(),
                        indication.termId())
                .returning(Indication.INDICATION.ID)
                .fetchOne()
                .getValue(Indication.INDICATION.ID);

        for (String altId : indication.altIds()) {
            batchInserter.add(context.insertInto(Indicationaltid.INDICATIONALTID,
                    Indicationaltid.INDICATIONALTID.INDICATIONID,
                    Indicationaltid.INDICATIONALTID.ALTID).values(id, altId));
        }

        return id;
    }
}
