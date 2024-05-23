package org.usth.ict.ulake.textr.services.engines;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@ApplicationScoped
public class TikaExtractor {

    private final Tika tika = new Tika();

    public String extractText(File file) throws TikaException, IOException {
        return tika.parseToString(file);
    }

    public String extractTextFromStream(InputStream stream) throws TikaException, IOException {
        return tika.parseToString(stream);
    }
}
