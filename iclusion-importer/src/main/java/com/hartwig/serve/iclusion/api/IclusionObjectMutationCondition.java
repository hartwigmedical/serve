package com.hartwig.serve.iclusion.api;

import java.util.List;

import com.squareup.moshi.Json;

public class IclusionObjectMutationCondition {

    @Json(name = "mutations")
    public List<IclusionObjectMutation> mutations;

    @Json(name = "logic_type")
    public String logicType;
}
