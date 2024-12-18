package com.hartwig.serve.refgenome.liftover.tools;

import java.io.File;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.jetbrains.annotations.NotNull;

import htsjdk.samtools.liftover.LiftOver;
import htsjdk.samtools.util.Interval;

public class LiftOverTestApplication {

    private static final Logger LOGGER = LogManager.getLogger(LiftOverTestApplication.class);

    private static final String LIFT_OVER_37_TO_38_CHAIN =
            System.getProperty("user.home") + "/hmf/refgenomes/liftover/hg19ToHg38.over.chain";
    private static final String LIFT_OVER_38_TO_37_CHAIN =
            System.getProperty("user.home") + "/hmf/refgenomes/liftover/hg38ToHg19.over.chain";

    public static void main(String[] args) {
        Configurator.setRootLevel(Level.DEBUG);

        // X:1314965 is a hotspot in 37 on CRLF2 that can't be lifted over to 38.
        Interval interval1 = new Interval("chrX", 1314965, 1314965);
        from37To38(interval1);

        // This ATM variant has changed ref
        Interval interval2 = new Interval("chr11", 108312440, 108312440);
        from38To37(interval2);
    }

    private static void from37To38(@NotNull Interval original) {
        LOGGER.debug("Starting interval 37 is {}", original);

        LiftOver liftOver37To38 = new LiftOver(new File(LIFT_OVER_37_TO_38_CHAIN));
        Interval result = liftOver37To38.liftOver(original);
        LOGGER.debug("Interval lifted from 37 to 38 is {}", result);

        if (result != null) {
            LiftOver liftOver38to37 = new LiftOver(new File(LIFT_OVER_38_TO_37_CHAIN));
            Interval backToOriginal = liftOver38to37.liftOver(result);
            LOGGER.debug("Interval lifted back to 37 is {}", backToOriginal);
        }
    }

    private static void from38To37(@NotNull Interval original) {
        LOGGER.debug("Starting interval 38 is {}", original);

        LiftOver liftOver38to37 = new LiftOver(new File(LIFT_OVER_38_TO_37_CHAIN));
        Interval result = liftOver38to37.liftOver(original);
        LOGGER.debug("Interval lifted from 38 to 37 is {}", result);

        if (result != null) {
            LiftOver liftOver37To38 = new LiftOver(new File(LIFT_OVER_37_TO_38_CHAIN));
            Interval backToOriginal = liftOver37To38.liftOver(result);
            LOGGER.debug("Interval lifted back to 38 is {}", backToOriginal);
        }
    }
}
