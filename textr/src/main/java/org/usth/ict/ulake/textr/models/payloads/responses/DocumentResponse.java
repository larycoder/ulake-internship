package org.usth.ict.ulake.textr.models.payloads.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.usth.ict.ulake.textr.models.Documents;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
public class DocumentResponse {

    @NotNull
    private Documents document;

    @NotNull
    private Float score;

    @NotNull
    private String[] highlightContents;
}
