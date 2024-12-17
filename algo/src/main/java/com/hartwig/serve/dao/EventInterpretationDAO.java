package com.hartwig.serve.dao;

import static com.hartwig.serve.database.Tables.EVENTINTERPRETATION;

import java.sql.Timestamp;
import java.util.List;

import com.google.common.collect.Iterables;
import com.hartwig.serve.extraction.events.EventInterpretation;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.InsertValuesStep6;

@SuppressWarnings({ "unchecked", "ResultOfMethodCallIgnored" })
class EventInterpretationDAO {

    @NotNull
    private final DSLContext context;

    public EventInterpretationDAO(@NotNull final DSLContext context) {
        this.context = context;
    }

    public void deleteAll() {
        context.deleteFrom(EVENTINTERPRETATION).execute();
    }

    public void write(@NotNull Timestamp timestamp, @NotNull List<EventInterpretation> eventInterpretations) {
        for (List<EventInterpretation> batch : Iterables.partition(eventInterpretations, DatabaseUtil.DB_BATCH_INSERT_SIZE)) {
            InsertValuesStep6 inserter = context.insertInto(EVENTINTERPRETATION,
                    EVENTINTERPRETATION.MODIFIED,
                    EVENTINTERPRETATION.SOURCE,
                    EVENTINTERPRETATION.SOURCEEVENT,
                    EVENTINTERPRETATION.INTERPRETEDGENE,
                    EVENTINTERPRETATION.INTERPRETEDEVENT,
                    EVENTINTERPRETATION.INTERPRETEDEVENTTYPE);
            batch.forEach(entry -> writeEventInterpretationBatch(timestamp, inserter, entry));
            inserter.execute();
        }
    }

    private static void writeEventInterpretationBatch(@NotNull Timestamp timestamp, @NotNull InsertValuesStep6 inserter,
            @NotNull EventInterpretation eventInterpretation) {
        inserter.values(timestamp,
                eventInterpretation.source(),
                eventInterpretation.sourceEvent(),
                eventInterpretation.interpretedGene(),
                eventInterpretation.interpretedEvent(),
                eventInterpretation.interpretedEventType());
    }
}
