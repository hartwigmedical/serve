package com.hartwig.serve.dao;

import static com.hartwig.serve.database.Tables.ACTIONABLECHARACTERISTIC;
import static com.hartwig.serve.database.Tables.ACTIONABLECODON;
import static com.hartwig.serve.database.Tables.ACTIONABLEEXON;
import static com.hartwig.serve.database.Tables.ACTIONABLEFUSION;
import static com.hartwig.serve.database.Tables.ACTIONABLEGENE;
import static com.hartwig.serve.database.Tables.ACTIONABLEHLA;
import static com.hartwig.serve.database.Tables.ACTIONABLEHOTSPOT;
import static com.hartwig.serve.database.Tables.MOLECULARCRITERIUM;

import java.sql.Timestamp;

import com.hartwig.serve.datamodel.molecular.MolecularCriterium;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

class MolecularCriteriumDAO {

    @NotNull
    private final DSLContext context;

    public MolecularCriteriumDAO(@NotNull final DSLContext context) {
        this.context = context;
    }

    public void deleteAll() {
        context.deleteFrom(MOLECULARCRITERIUM).execute();
        context.deleteFrom(ACTIONABLEHOTSPOT).execute();
        context.deleteFrom(ACTIONABLECODON).execute();
        context.deleteFrom(ACTIONABLEEXON).execute();
        context.deleteFrom(ACTIONABLEGENE).execute();
        context.deleteFrom(ACTIONABLEFUSION).execute();
        context.deleteFrom(ACTIONABLECHARACTERISTIC).execute();
        context.deleteFrom(ACTIONABLEHLA).execute();
    }

    public int write(@NotNull Timestamp timestamp, @NotNull MolecularCriterium molecularCriterium) {
        return context.insertInto(MOLECULARCRITERIUM, MOLECULARCRITERIUM.MODIFIED)
                .values(timestamp.toLocalDateTime())
                .returning(MOLECULARCRITERIUM.ID)
                .fetchOne()
                .getValue(MOLECULARCRITERIUM.ID);

        // TODO Insert all actionable fields
    }
}
