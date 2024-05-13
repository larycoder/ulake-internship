package org.usth.ict.ulake.textr.models.payloads.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
public class DocumentResponse {

    @NotNull
    private int id;

    @NotNull
    private String name;

    @NotNull
    private String path;

    @NotNull
    private Float score;
}
