package org.usth.ict.ulake.textr.models.payloads.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.StreamingOutput;

@AllArgsConstructor
@Setter
@Getter
public class FileResponse {

    @NotNull
    private String fileName;

    @NotNull
    private StreamingOutput fileStream;
}
