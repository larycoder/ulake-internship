package org.usth.ict.ulake.textr.models.payloads.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.usth.ict.ulake.common.model.folder.FileModel;
import org.usth.ict.ulake.textr.models.IndexFiles;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class DocumentResponse {
    
    @NotNull
    private IndexFiles document;
    
    private FileModel fileModel;
    
    @NotNull
    private Float score;
    
    @NotNull
    private String[] highlightContents;
    
    public  DocumentResponse(IndexFiles document, Float score, String[] highlightContents) {
        this.document = document;
        this.score = score;
        this.highlightContents = highlightContents;
    }
}
