package com.hartwig.serve.iclusion;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import com.hartwig.serve.iclusion.datamodel.IclusionTrial;
import com.hartwig.serve.iclusion.io.IclusionTrialFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class IclusionImporterLocalApp {

    private static final Logger LOGGER = LogManager.getLogger(IclusionImporterLocalApp.class);

    public static void main(@NotNull final String[] args) throws IOException {
        String apiEndPoint = "https://iclusion.org/api";

        String config = System.getProperty("user.home") + "/hmf/tmp/iclusion";
        List<String> lines = Files.readAllLines(new File(config).toPath());

        String iClusionTrialTsv = System.getProperty("user.home") + "/hmf/tmp/iclusionTrials.tsv";

        IclusionCredentials credentials = ImmutableIclusionCredentials.builder()
                .clientId(lines.get(0))
                .clientSecret(lines.get(1))
                .username(lines.get(2))
                .password(lines.get(3))
                .build();

        new IclusionImporter(apiEndPoint, credentials).importToTsv(iClusionTrialTsv);

        List<IclusionTrial> trials = IclusionTrialFile.read(iClusionTrialTsv);

        LOGGER.info("Read {} trials from {}", trials.size(), iClusionTrialTsv);
    }
}
