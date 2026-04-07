package co.edu.uptc.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.google.gson.reflect.TypeToken;

class JsonRepositoryTest {

    @TempDir
    private Path tempDir;

    @Test
    void saveAndFindAll_persistsElements() {
        Type type = new TypeToken<List<Integer>>() {}.getType();
        JsonRepository<Integer> repo = new JsonRepository<>(tempDir.resolve("data.json").toString(), type);

        repo.save(7);
        repo.save(42);

        List<Integer> all = repo.findAll();
        assertEquals(2, all.size());
        assertTrue(all.contains(7));
        assertTrue(all.contains(42));
    }

    @Test
    void replaceAll_overwritesFile() {
        Type type = new TypeToken<List<Integer>>() {}.getType();
        JsonRepository<Integer> repo = new JsonRepository<>(tempDir.resolve("rep.json").toString(), type);
        repo.save(1);
        repo.replaceAll(List.of(9, 8));

        assertEquals(List.of(9, 8), repo.findAll());
    }
}
