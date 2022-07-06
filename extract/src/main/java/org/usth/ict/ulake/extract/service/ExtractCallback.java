package org.usth.ict.ulake.extract.service;

import org.usth.ict.ulake.extract.model.ExtractResultFile;
import org.usth.ict.ulake.extract.model.ExtractResult;

public interface ExtractCallback {
    public void callback(ExtractResultFile file, boolean success, ExtractResult result);
}
