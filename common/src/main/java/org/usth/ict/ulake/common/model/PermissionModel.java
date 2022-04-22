package org.usth.ict.ulake.common.model;

/**
 * List permission of object.
 * */
public class PermissionModel {
    private final static Integer[] permissions = {1, 2, 4};

    public final static Integer EXECUTE = 1;
    public final static Integer WRITE = 2;
    public final static Integer READ = 4;

    /**
     * Validate permission value.
     * @param value permission value
     * */
    public static Boolean isPermission(Integer value) {
        for (int i = 0; i < permissions.length; i++)
            if (permissions[i] == value)
                return true;
        return false;
    }
}
