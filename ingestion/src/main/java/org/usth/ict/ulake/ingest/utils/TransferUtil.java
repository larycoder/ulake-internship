package org.usth.ict.ulake.ingest.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
public class TransferUtil {
    /**
     * Transfer from input stream to output stream.
     * Behavior: method will not close input/output stream.
     * */
    public static void streamIO(InputStream is, OutputStream os, byte[] buf) {
        try {
            is.transferTo(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void streamOutputFile(InputStream is, File file, byte[] bytes) {
        streamOutputFile(is, file, bytes, true);
    }

    public static void streamOutputFile(
        InputStream is, File file, byte[] buf, boolean createFile) {
        try {
            if (createFile) {
                Path path = Paths.get(file.toString());
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            }

            OutputStream os = new FileOutputStream(file);
            streamIO(is, os, buf);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String streamToString(InputStream is) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
            is.transferTo(baos);
            return baos.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<String, Object> streamToJson(InputStream is) {
        String jsonString = streamToString(is);
        ObjectMapper om = new ObjectMapper();
        Map<String, Object> mapResult = new HashMap<>();
        try {
            if (jsonString.strip().startsWith("[")) {
                var listResult = om.readValue(jsonString, List.class);
                mapResult.put("data", listResult);
            } else {
                var tempResult = om.readValue(jsonString, Map.class);
                mapResult.put("data", tempResult);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mapResult;
    }

    public static InputStream streamFromFile(String path) {
        try {
            File file = new File(path);
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<Object, Object> stringToMap(String text) {
        try {
            ObjectMapper om = new ObjectMapper();
            var type = new TypeReference<Map<Object, Object>>() {};
            return om.readValue(text, type);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String mapToString(Map<?, ?> data) {
        try {
            ObjectMapper om = new ObjectMapper();
            return om.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
