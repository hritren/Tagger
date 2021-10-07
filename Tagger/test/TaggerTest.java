import bg.sofia.uni.fmi.mjt.tagger.Tagger;
import org.junit.Test;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;


public class TaggerTest {

    @Test
    public void testTagCitites() {
        try (Reader in = new FileReader("cities.txt");
             Reader text = new FileReader("text.txt");
             Writer output = new FileWriter("tagCities.txt")) {
            Tagger tagger = new Tagger(in);
            tagger.tagCities(text, output);
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while reading form a file", e);
        }
        try (BufferedReader f1 = new BufferedReader(new FileReader("tagCities.txt"));
                BufferedReader f2 = new BufferedReader(new FileReader("result.txt"))) {
            String first;
            String second;
            while ((first = f1.readLine()) != null && (second = f2.readLine()) != null) {
                assertEquals(first, second);
            }
            assertTrue((first = f1.readLine()) == null  && (second = f1.readLine()) == null);
        }
        catch (IOException e) {
            throw new IllegalStateException("A problem occurred while reading form a file", e);
        }
    }

    @Test
    public void testGetNMostTaggedCities() {
        try (Reader in = new FileReader("cities.txt");
                Reader text = new FileReader("text.txt");
                Writer output = new FileWriter("tagCities.txt")) {
            Tagger tagger = new Tagger(in);
            Collection<String> res = tagger.getNMostTaggedCities(2);
            assertTrue(res.isEmpty());
            tagger.tagCities(text, output);
            res = tagger.getNMostTaggedCities(2);
            System.out.println(res);
            Collection<String> expected = new ArrayList<>();
            expected.add("Sofia");
            expected.add("Rome");
            assertTrue(res.containsAll(expected) && expected.containsAll(res));
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while reading form a file", e);
        }
    }

    @Test
    public void testGetAllTaggedCities() {
        try (Reader in = new FileReader("cities.txt");
                Reader text = new FileReader("text.txt");
                Writer output = new FileWriter("tagCities.txt")) {
            Tagger tagger = new Tagger(in);
            Collection<String> res = tagger.getAllTaggedCities();
            assertTrue(res.isEmpty());
            tagger.tagCities(text, output);
            res = tagger.getAllTaggedCities();
            System.out.println(res);
            Collection<String> expected = new ArrayList<>();
            expected.add("Sofia");
            expected.add("Rome");
            expected.add("Plovdiv");
            assertTrue(res.containsAll(expected) && expected.containsAll(res));
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while reading form a file", e);
        }
    }

    @Test
    public void testGetAllTagsCount() {
        try (Reader in = new FileReader("cities.txt");
                Reader text = new FileReader("text.txt");
                Writer output = new FileWriter("tagCities.txt")) {
            Tagger tagger = new Tagger(in);
            assertEquals(tagger.getAllTagsCount(), 0);
            tagger.tagCities(text, output);
            assertEquals(tagger.getAllTagsCount(), 5);
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while reading form a file", e);
        }
    }
}
