package org.usth.ict.ulake.common.model.acl.macro;

public enum FileType {
    file("f"),
    folder("d");

    public final String label;

    private FileType(String label) {
        this.label = label;
    }
}
