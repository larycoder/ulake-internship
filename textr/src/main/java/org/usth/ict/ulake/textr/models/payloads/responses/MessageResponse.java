package org.usth.ict.ulake.textr.models.payloads.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Getter
public class MessageResponse {

    @NotNull
    private int status;

    @NotNull
    private String msg;
}
