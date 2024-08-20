package com.hartwig.serve.sources.ckb.curation;

import java.util.Map;

import com.google.common.collect.Maps;
import com.hartwig.serve.ckb.classification.CkbConstants;

public final class CkbVariantCurationFactory {

    static final Map<CkbVariantCurationEntry, CkbVariantCurationEntry> VARIANT_MAPPINGS = Maps.newHashMap();

    private CkbVariantCurationFactory() {
    }

    static {
        // CKB uses "genes" to model evidence on characteristics. We map this away from genes.
        VARIANT_MAPPINGS.put(new CkbVariantCurationEntry("HRD", "positive"),
                new CkbVariantCurationEntry(CkbConstants.NO_GENE, CkbConstants.HRD_POSITIVE));
        VARIANT_MAPPINGS.put(new CkbVariantCurationEntry("HRD", "negative"),
                new CkbVariantCurationEntry(CkbConstants.NO_GENE, CkbConstants.HRD_NEGATIVE));
        VARIANT_MAPPINGS.put(new CkbVariantCurationEntry("MSI", "high"),
                new CkbVariantCurationEntry(CkbConstants.NO_GENE, CkbConstants.MSI_HIGH));
        VARIANT_MAPPINGS.put(new CkbVariantCurationEntry("MSI", "low"),
                new CkbVariantCurationEntry(CkbConstants.NO_GENE, CkbConstants.MSI_LOW));
        VARIANT_MAPPINGS.put(new CkbVariantCurationEntry("MSI", "negative"),
                new CkbVariantCurationEntry(CkbConstants.NO_GENE, CkbConstants.MSI_NEGATIVE));
        VARIANT_MAPPINGS.put(new CkbVariantCurationEntry("TMB", "high"),
                new CkbVariantCurationEntry(CkbConstants.NO_GENE, CkbConstants.TMB_HIGH));
        VARIANT_MAPPINGS.put(new CkbVariantCurationEntry("TMB", "low"),
                new CkbVariantCurationEntry(CkbConstants.NO_GENE, CkbConstants.TMB_LOW));
    }
}
