package org.usth.ict.ulake.ingest.model.macro;

public enum Record {
    // local file
    FILE_PATH, FILE_SIZE, FILE_NAME,

    // ulake
    TOKEN, STORAGE_DIR, OBJECT_ID,

    // crawl process
    STATUS
}
