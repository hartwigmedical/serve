package com.hartwig.serve.sources.iclusion.filter;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.serve.iclusion.datamodel.IclusionMutation;
import com.hartwig.serve.iclusion.datamodel.IclusionMutationCondition;
import com.hartwig.serve.iclusion.datamodel.IclusionMutationLogicType;
import com.hartwig.serve.iclusion.datamodel.IclusionTrial;
import com.hartwig.serve.iclusion.datamodel.ImmutableIclusionMutationCondition;
import com.hartwig.serve.iclusion.datamodel.ImmutableIclusionTrial;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class IclusionFilter {

    private static final Logger LOGGER = LogManager.getLogger(IclusionFilter.class);

    @NotNull
    private final List<IclusionFilterEntry> filters;
    @NotNull
    private final Set<IclusionFilterEntry> usedFilters = Sets.newHashSet();

    public IclusionFilter(@NotNull final List<IclusionFilterEntry> filters) {
        this.filters = filters;
    }

    @NotNull
    public List<IclusionTrial> run(@NotNull List<IclusionTrial> trials) {
        // At this point we filter the following types of trials and conditions:
        //  - Trials without molecular inclusion criteria
        //  - Trials with empty acronyms.
        //  - Conditions that are grouped with a logic type other than OR (no combined events supported yet)
        //  - Conditions that are negated (not yet supported).

        List<IclusionTrial> filteredTrials = Lists.newArrayList();
        for (IclusionTrial trial : trials) {
            if (!trial.acronym().isEmpty()) {
                List<IclusionMutationCondition> filteredConditions = Lists.newArrayList();

                for (IclusionMutationCondition condition : trial.mutationConditions()) {
                    List<IclusionMutation> filteredMutations = Lists.newArrayList();
                    if (condition.logicType() == IclusionMutationLogicType.OR) {
                        for (IclusionMutation mutation : condition.mutations()) {
                            if (!mutation.negation()) {
                                if (include(mutation)) {
                                    filteredMutations.add(mutation);
                                } else {
                                    LOGGER.debug("Filtering mutation from {}: '{}' on '{}'",
                                            trial.acronym(),
                                            mutation.name(),
                                            mutation.gene());
                                }
                            } else {
                                LOGGER.debug("Filtering negated mutation from {}: '{}'", trial.acronym(), mutation);
                            }
                        }

                        if (!filteredMutations.isEmpty()) {
                            filteredConditions.add(ImmutableIclusionMutationCondition.builder()
                                    .from(condition)
                                    .mutations(filteredMutations)
                                    .build());
                        }
                    } else {
                        LOGGER.debug("Filtering non-OR mutation condition from {}: '{}'", trial.acronym(), condition);
                    }
                }

                if (!filteredConditions.isEmpty()) {
                    filteredTrials.add(ImmutableIclusionTrial.builder().from(trial).mutationConditions(filteredConditions).build());
                }
            } else {
                LOGGER.debug("Filtering trial for missing an acronym: '{}'", trial);
            }
        }

        return filteredTrials;
    }

    public void reportUnusedFilterEntries() {
        int unusedFilterEntryCount = 0;
        for (IclusionFilterEntry filter : filters) {
            if (!usedFilters.contains(filter)) {
                unusedFilterEntryCount++;
                LOGGER.warn(" Filter entry '{}' hasn't been used for iClusion filtering", filter);
            }
        }

        LOGGER.debug(" Found {} unused filter entries during iClusion filtering", unusedFilterEntryCount);
    }

    private boolean include(@NotNull IclusionMutation mutation) {
        for (IclusionFilterEntry filter : filters) {
            boolean filterMatches = isMatch(filter, mutation);
            if (filterMatches) {
                usedFilters.add(filter);
                return false;
            }
        }

        return true;
    }

    private boolean isMatch(@NotNull IclusionFilterEntry filter, @NotNull IclusionMutation mutation) {
        switch (filter.type()) {
            case FILTER_EVENT_WITH_KEYWORD: {
                String evaluation = mutation.name();
                return evaluation.equals(filter.value());
            }
            case FILTER_VARIANT_ON_GENE: {
                String evaluation = mutation.gene() + " " + mutation.name();
                return evaluation.equals(filter.value());
            }
            default: {
                LOGGER.warn("Filter entry found with unrecognized type: {}", filter);
                return false;
            }
        }
    }
}