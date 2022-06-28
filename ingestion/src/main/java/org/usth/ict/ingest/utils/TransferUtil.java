package org.usth.ict.ingest.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransferUtil {
    public void streamIO(InputStream is, OutputStream os, byte[] buf) {
        try {
            int len;
            while ((len = is.read(buf)) > 0) {
                os.write(buf, 0, len);
            }
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void streamOutputFile(InputStream is, File file, byte[] bytes) {
        streamOutputFile(is, file, bytes, true);
    }

    public void streamOutputFile(InputStream is, File file, byte[] buf, boolean createFile) {
        try {
            if(createFile) {
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

    public String streamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8));
        String text = "";
        try {
            int c;
            while((c = reader.read()) != -1) {
                text += (char) c;
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    public Map streamToJson(InputStream is) {
        String jsonString = streamToString(is);
        ObjectMapper om = new ObjectMapper();
        Map mapResult = new HashMap();

        List listResult;
        Map tempResult;

        try {
            if (jsonString.strip().startsWith("[")) {
                listResult = om.readValue(jsonString, List.class);
                mapResult.put("data", listResult);
            } else {
                tempResult = om.readValue(jsonString, Map.class);
                mapResult.put("data", tempResult);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return mapResult;
    }

    public InputStream streamFromFile(String path) {
        try {
            File file = new File(path);
            return new FileInputStream(file);
        } catch(FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map stringToMap(String text) {
        Map map = null;
        try {
            ObjectMapper om = new ObjectMapper();
            map = om.readValue(text, Map.class);
        } catch(JsonProcessingException e) {
            e.printStackTrace();
        }
        return map;
    }

    public String mapToString(Map data) {
        String text = null;
        try {
            ObjectMapper om = new ObjectMapper();
            text = om.writeValueAsString(data);
        } catch(JsonProcessingException e) {
            e.printStackTrace();
        }
        return text;
    }
}
