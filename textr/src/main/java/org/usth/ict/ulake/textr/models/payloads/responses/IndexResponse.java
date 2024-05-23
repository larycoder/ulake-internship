package org.usth.ict.ulake.textr.models.payloads.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;

@Getter
@Setter
@AllArgsConstructor
public class IndexResponse {

    @Min(value = 0, message = "No document found")
    private Integer indexed;
}
