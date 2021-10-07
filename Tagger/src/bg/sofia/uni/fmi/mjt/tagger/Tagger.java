package bg.sofia.uni.fmi.mjt.tagger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

public class Tagger {

    private final Map<String, String> cities = new HashMap<>();
    private final Map<String, Integer> tagCount = new HashMap<>();
    private boolean hasTagged;
    private int allTagsCount;

    /**
     * Creates a new instance of Tagger for a given list of city/country pairs
     *
     * @param citiesReader a java.io.Reader input stream containing list of cities and countries
     *                     in the specified CSV format
     */
    public Tagger(Reader citiesReader) {
        hasTagged = false;
        try (BufferedReader br = new BufferedReader(citiesReader)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tmp = line.split(",");
                cities.put(tmp[0], tmp[1]);
            }
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while reading form a file", e);
        }
    }

    private String createTag(String city) {
        return "<city country=\"" + cities.get(city) + "\">" + city + "</city>";
    }

    /**
     * Processes an input stream of a text file, tags any cities and outputs result
     * to a text output stream.
     *
     * @param text   a java.io.Reader input stream containing text to be processed
     * @param output a java.io.Writer output stream containing the result of tagging
     */
    public void tagCities(Reader text, Writer output) {
        allTagsCount = 0;
        try (BufferedReader br = new BufferedReader(text);
                BufferedWriter bw = new BufferedWriter(output)) {
            String textString;
            boolean isFirstIteration = true;
            while ((textString = br.readLine()) != null) {
                for (String city : cities.keySet()) {
                    int count = 0;
                    int fromIndex = 0;
                    while ((fromIndex = textString.indexOf(city, fromIndex)) != -1) {
                        count++;
                        fromIndex++;
                    }
                    allTagsCount += count;
                    textString = textString.replaceAll(city, createTag(city));
                    if (tagCount.containsKey(city)) {
                        tagCount.put(city, tagCount.get(city) + count);
                    } else {
                        tagCount.put(city, count);
                    }
                }
                if (isFirstIteration) {
                    bw.write(textString);
                    isFirstIteration = false;
                } else {
                    bw.write(System.lineSeparator() + textString);
                }
                bw.flush();
            }
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while reading form a file", e);
        }
        hasTagged = true;
    }

    /**
     * Returns a collection the top @n most tagged cities' unique names
     * from the last tagCities() invocation. Note that if a particular city has been tagged
     * more than once in the text, just one occurrence of its name should appear in the result.
     * If @n exceeds the total number of cities tagged, return as many as available
     * If tagCities() has not been invoked at all, return an empty collection.
     *
     * @param n the maximum number of top tagged cities to return
     * @return a collection the top @n most tagged cities' unique names
     * from the last tagCities() invocation.
     */
    public Collection<String> getNMostTaggedCities(int n) {
        if (!hasTagged) {
            return new ArrayList<>();
        }
        Map<String, Integer> copy = new HashMap<>(tagCount);
        List<String> result = new ArrayList<>();
        for (int i = 0; i < n; ++i) {
            int maxx = -1;
            String city = "";
            for (String c : copy.keySet()) {
                if (copy.get(c) > maxx) {
                    maxx = copy.get(c);
                    city = c;
                }
            }
            result.add(city);
            copy.remove(city);
        }
        return result;
    }

    /**
     * Returns a collection of all tagged cities' unique names
     * from the last tagCities() invocation. Note that if a particular city has been tagged
     * more than once in the text, just one occurrence of its name should appear in the result.
     * If tagCities() has not been invoked at all, return an empty collection.
     *
     * @return a collection of all tagged cities' unique names
     * from the last tagCities() invocation.
     */
    public Collection<String> getAllTaggedCities() {
        if (!hasTagged) {
            return new ArrayList<>();
        }
        List<String> result = new ArrayList<>();
        for (String city : tagCount.keySet()) {
            if (tagCount.get(city) > 0) {
                result.add(city);
            }
        }
        return result;
    }

    /**
     * Returns the total number of tagged cities in the input text
     * from the last tagCities() invocation
     * In case a particular city has been taged in several occurences, all must be counted.
     * If tagCities() has not been invoked at all, return 0.
     *
     * @return the total number of tagged cities in the input text
     */
    public long getAllTagsCount() {
        return allTagsCount;
    }
}