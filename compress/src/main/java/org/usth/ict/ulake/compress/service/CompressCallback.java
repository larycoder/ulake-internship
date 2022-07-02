package org.usth.ict.ulake.compress.service;

import org.usth.ict.ulake.compress.model.RequestFile;

public interface CompressCallback {
    public void callback(RequestFile file);
}
