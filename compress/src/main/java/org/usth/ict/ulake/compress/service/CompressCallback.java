package org.usth.ict.ulake.compress.service;

import org.usth.ict.ulake.compress.model.RequestFile;
import org.usth.ict.ulake.compress.model.Result;

public interface CompressCallback {
    public void callback(RequestFile file, boolean success, Result result);
}
