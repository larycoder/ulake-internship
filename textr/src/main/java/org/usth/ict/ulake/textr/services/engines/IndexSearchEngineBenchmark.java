package org.usth.ict.ulake.textr.services.engines;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

@Deprecated
@ApplicationScoped
public class IndexSearchEngineBenchmark {
    private static final Logger LOG = Logger.getLogger(String.valueOf(IndexSearchEngineBenchmark.class));
    FileHandler fileHandler;

    @Inject
    IndexSearchEngine indexSearchEngine;

    private File indexPath;
    private File dataPath;

//    private JsonObject calcResources(Boolean isIndexing, List<String> queries) throws IOException {
//        JsonObject output = new JsonObject();
//
////        Setup timer
//        long startTime = System.currentTimeMillis();
//
//        if (isIndexing) {
////            Start indexing
//            output = indexSearchEngine.index();
//        } else {
////            Start searching
//            JsonArray tmp = new JsonArray();
//            for (String query : queries)
//                tmp.add(indexSearchEngine.search(query));
//            output.put("queries", tmp);
//        }
//
////        Time calculate
//        long consumedTime =  System.currentTimeMillis() - startTime;
//
//        output.put("consumed_time_millis", consumedTime);
//        return output;
//    }
//
//    private JsonObject calcResources(List<String> queries) throws IOException {
//        return this.calcResources(false, queries);
//    }
//
//    private JsonObject calcResources() throws IOException {
//        return this.calcResources(true, null);
//    }

    private long getDirectorySize(File folder) {
        long length = 0;
        File[] files = folder.listFiles();

        assert files != null;
        for (File file : files) {
            if (file.isFile()) {
                length += file.length();
            } else {
                length += getDirectorySize(file);
            }
        }
        return length;
    }

    private long getDirectorySize(String path) {
        File file = new File(path);
        return this.getDirectorySize(file);
    }

    private List<String> getRandomStrings(long numStrings) throws FileNotFoundException {
        List<String> randomStrings = new ArrayList<>();

//        Russian roulette
        Random randomizer = new Random();

//        Get data
        File[] files = dataPath.listFiles();
        assert files != null;

        while (randomStrings.size() < numStrings) {
            int pickedFile = randomizer.nextInt(files.length);
            File file = files[pickedFile];

//            Pick random line from file
            String pickedLine = "";
            int n = 0;
            for (Scanner scanner = new Scanner(new FileReader(file)); scanner.hasNext(); ) {
                ++n;
                String line = scanner.nextLine();
                if (randomizer.nextInt(n) == 0)
                    pickedLine = line;
            }

//            Pick random sentence from picked line
            String[] words = pickedLine.split("\\s+");
            StringBuilder sentence = new StringBuilder();

            if (words.length > 1) {
//                Select random sentence
                int startIndex = randomizer.nextInt(words.length); // Random from 0 to length - 1
                int lastIndex = randomizer.nextInt(words.length - startIndex) + startIndex; // Random from start index to length - 1

                for (int i = startIndex; i <= lastIndex; i++) {
                    sentence.append(words[i]).append(" ");
                }

                randomStrings.add(sentence.toString());
            }
        }

        return randomStrings;
    }

//    private void initBenchmark() throws IOException {
//        this.indexPath = new File(indexSearchEngine.getIndexDir());
//        this.dataPath = new File(indexSearchEngine.getDataDir());
//    }
//
//    public JsonObject startBenchmark(long iteration, List<String> queries) throws IOException {
//        JsonObject output = new JsonObject();
//        long totalIndexTime = 0;
//        long totalSearchTime = 0;
//        long indexed = 0;
//        JsonObject docFound = new JsonObject();
//
//        for (int i = 0; i<iteration; i++) {
////            Clean indexed files each loops
//            FileUtils.cleanDirectory(indexPath);
//
////            Resources calculation
//            JsonObject indexObj = calcResources();
//            JsonObject searchObj = calcResources(queries);
//
////            Total resources consumption
//            totalIndexTime += indexObj.getLong("consumed_time_millis");
//            totalSearchTime += searchObj.getLong("consumed_time_millis");
//
////            Get indexed each iteration
//            indexed = indexObj.getLong("indexed");
//
////            Get found documents
//            docFound.put("iteration " + (i + 1), searchObj.getJsonArray("queries"));
//        }
//
////        Average resources consumption calculation
//        long avgIndexTime = totalIndexTime / iteration;
//        long avgSearchTime = totalSearchTime / iteration;
//
////        Disk measure
//        long dataSize = getDirectorySize(dataPath);
//        long indexSize = getDirectorySize(indexPath);
//
////        Build response
//        output.put("iteration", iteration);
//        output.put("indexed_each_iter", indexed);
//        output.put("queries", queries.toArray().length);
//        output.put("docs_found", docFound);
//        output.put("total_index_time_millis", totalIndexTime);
//        output.put("total_search_time_millis", totalSearchTime);
//        output.put("avg_index_time_millis", avgIndexTime);
//        output.put("avg_search_time_millis", avgSearchTime);
//        output.put("data_size_kb", dataSize / 1024);
//        output.put("indexed_size_kb", indexSize / 1024);
//        output.put("indexed_stored_percentage", indexSize * 100.0 / dataSize);
//
//        return output;
//    }
//
//    public JsonObject startBenchmarkV1(long iteration) throws IOException {
//        this.initBenchmark();
//        List<String> randomQueries = getRandomStrings(iteration);
//
//        return this.startBenchmark(iteration, randomQueries);
//    }
//
//    public JsonObject startBenchmarkV2(long iteration) throws IOException {
//        this.initBenchmark();
//        List<String> queries = new ArrayList<>();
//        File queryFile = new File("/home/malenquillaa/tmp/data/cran_qry/query.filtered.txt");
//        Scanner myReader = new Scanner(queryFile);
//        while (myReader.hasNextLine()) {
//            String data = myReader.nextLine();
//            if (data != null && !data.isEmpty())
//                queries.add(data);
//        }
//        myReader.close();
//
//        return this.startBenchmark(iteration, queries);
//    }
}
