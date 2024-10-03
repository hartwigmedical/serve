package com.hartwig.serve.datamodel;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ImmutableClinicalTrial.class,
                           name = "ClinicalTrial"),
        @JsonSubTypes.Type(value = ImmutableTreatment.class,
                           name = "Treatment")
})
public interface Intervention {
}
