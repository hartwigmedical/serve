package com.hartwig.serve.iclusion.api;

import com.squareup.moshi.Json;

class IclusionToken {

    @Json(name = "access_token")
    public String accessToken;
}
