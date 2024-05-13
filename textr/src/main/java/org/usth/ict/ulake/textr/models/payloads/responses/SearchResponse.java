package org.usth.ict.ulake.textr.models.payloads.responses;

import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchResponse {

    @NotNull
    private int indexed;

    @NotNull
    private List<DocumentResponse> docs;
}
