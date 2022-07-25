package org.usth.ict.ulake.common.model.acl.macro;

public enum UserType {
    group("g"),
    user("u");

    public final String label;

    private UserType(String label) {
        this.label = label;
    }
}
