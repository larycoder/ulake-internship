package org.usth.ict.ingest.models.macro;

public class Record {
    // storing info configuration
    public static int PATH = 0;
    public static int HOST = 1;

    // processing info configuration
    public static int NAME = 2;
    public static int LINK = 3;
    public static int TOKEN = 4;
    public static int FILE_SIZE= 5;

    // buffer
    public static int MAX = 10*1024*1024;
}
