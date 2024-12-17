package com.hartwig.serve.dao;

import java.util.List;

import com.hartwig.serve.datamodel.ServeRecord;
import com.hartwig.serve.extraction.events.EventInterpretation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

class ServeDAO {

    private static final Logger LOGGER = LogManager.getLogger(ServeDAO.class);

    @NotNull
    private final KnownEventsDAO knownEventsDAO;
    @NotNull
    private final EventInterpretationDAO eventInterpretationDAO;
    @NotNull
    private final MolecularCriteriumDAO molecularCriteriumDAO;
    @NotNull
    private final EfficacyEvidenceDAO efficacyEvidenceDAO;
    @NotNull
    private final ActionableTrialDAO actionableTrialDAO;

    @NotNull
    public static ServeDAO create(@NotNull DSLContext context) {
        KnownEventsDAO knownEventsDAO = new KnownEventsDAO(context);
        EventInterpretationDAO eventInterpretationDAO = new EventInterpretationDAO(context);
        MolecularCriteriumDAO molecularCriteriumDAO = new MolecularCriteriumDAO(context);
        EfficacyEvidenceDAO efficacyEvidenceDAO = new EfficacyEvidenceDAO(context, molecularCriteriumDAO);
        ActionableTrialDAO actionableTrialDAO = new ActionableTrialDAO(context, molecularCriteriumDAO);

        return new ServeDAO(knownEventsDAO, eventInterpretationDAO, molecularCriteriumDAO, efficacyEvidenceDAO, actionableTrialDAO);
    }

    private ServeDAO(@NotNull final KnownEventsDAO knownEventsDAO, @NotNull final EventInterpretationDAO eventInterpretationDAO,
            @NotNull final MolecularCriteriumDAO molecularCriteriumDAO, @NotNull final EfficacyEvidenceDAO efficacyEvidenceDAO,
            @NotNull final ActionableTrialDAO actionableTrialDAO) {
        this.knownEventsDAO = knownEventsDAO;
        this.eventInterpretationDAO = eventInterpretationDAO;
        this.molecularCriteriumDAO = molecularCriteriumDAO;
        this.efficacyEvidenceDAO = efficacyEvidenceDAO;
        this.actionableTrialDAO = actionableTrialDAO;
    }

    public void repopulate(@NotNull ServeRecord serveRecord, @NotNull List<EventInterpretation> eventInterpretations) {
        LOGGER.info("Deleting all data from SERVE database");
        knownEventsDAO.deleteAll();
        eventInterpretationDAO.deleteAll();
        molecularCriteriumDAO.deleteAll();
        efficacyEvidenceDAO.deleteAll();
        actionableTrialDAO.deleteAll();

        LOGGER.info("Writing known events");
        knownEventsDAO.write(serveRecord.knownEvents());

        LOGGER.info("Writing event interpretations");
        eventInterpretationDAO.write(eventInterpretations);

        LOGGER.info("Writing evidences");
        efficacyEvidenceDAO.write(serveRecord.evidences());

        LOGGER.info("Writing trials");
        actionableTrialDAO.write(serveRecord.trials());
    }
}