package org.usth.ict.ulake.textr.engine;

import java.io.IOException;
import java.util.HashMap;

public abstract class RootEngine implements IndexSearchEngine{
    int index() throws IOException {
        return 0;
    }
    HashMap<String, Float> search(String term) throws IOException {
        return null;
    }
}
