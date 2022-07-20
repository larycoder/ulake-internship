package org.usth.ict.ulake.compress.service;

import org.usth.ict.ulake.compress.model.CompressRequestFile;
import org.usth.ict.ulake.compress.model.CompressResult;

public interface CompressCallback {
    public void callback(CompressRequestFile file, boolean success, CompressResult result);
}
