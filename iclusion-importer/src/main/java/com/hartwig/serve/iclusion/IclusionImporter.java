package com.hartwig.serve.iclusion;

import java.io.IOException;
import java.util.List;

import com.hartwig.serve.iclusion.api.IclusionApiMain;
import com.hartwig.serve.iclusion.datamodel.IclusionTrial;
import com.hartwig.serve.iclusion.io.IclusionTrialFile;
import com.hartwig.serve.iclusion.qc.IclusionTrialChecker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class IclusionImporter {

    private static final Logger LOGGER = LogManager.getLogger(IclusionImporter.class);

    @NotNull
    private final String apiEndPoint;
    @NotNull
    private final IclusionCredentials credentials;

    public IclusionImporter(@NotNull final String apiEndPoint, @NotNull final IclusionCredentials credentials) {
        this.apiEndPoint = apiEndPoint;
        this.credentials = credentials;
    }

    public void importToTsv(@NotNull String iClusionTrialTsv) throws IOException {
        List<IclusionTrial> trials = IclusionApiMain.readIclusionTrials(apiEndPoint, credentials);

        IclusionTrialChecker.check(trials);

        IclusionTrialFile.write(iClusionTrialTsv, trials);

        LOGGER.info("iClusion Importer has written {} trials to {}", trials.size(), iClusionTrialTsv);
    }
}
