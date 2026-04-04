package co.edu.uptc.repository;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.*;

public class JsonRepository<T> implements Repository<T> {

    private String filename;
    private Type type;
    private Gson gson;

    public JsonRepository(String filename, Type type) {
        this.filename = filename;
        this.type = type;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
                    public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
                        return new JsonPrimitive(src.toString());
                    }
                })
                .registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
                    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
                        return LocalDate.parse(json.getAsString());
                    }
                })
                .registerTypeAdapter(LocalTime.class, new JsonSerializer<LocalTime>() {
                    public JsonElement serialize(LocalTime src, Type typeOfSrc, JsonSerializationContext context) {
                        return new JsonPrimitive(src.toString());
                    }
                })
                .registerTypeAdapter(LocalTime.class, new JsonDeserializer<LocalTime>() {
                    public LocalTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
                        return LocalTime.parse(json.getAsString());
                    }
                })
                .setPrettyPrinting()
                .create();
    }

    // MÉTODOS AUXILIARES
    private String getId(T entity) {
        try {
            Method method = entity.getClass().getMethod("getId");
            return (String) method.invoke(entity);
        } catch (Exception e) {
            throw new RuntimeException("La entidad no tiene método getId()");
        }
    }

    private List<T> readFile() {
        try (FileReader reader = new FileReader(filename)) {
            List<T> data = gson.fromJson(reader, type);
            return data != null ? data : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private void writeFile(List<T> data) {
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            throw new RuntimeException("Error escribiendo archivo JSON");
        }
    }

    // CRUD
    @Override
    public void save(T entity) {
        List<T> data = readFile();
        data.add(entity);
        writeFile(data);
    }

    @Override
    public List<T> findAll() {
        return readFile();
    }

    @Override
    public Optional<T> findById(String id) {
        return readFile().stream()
                .filter(e -> getId(e).equals(id))
                .findFirst();
    }

    @Override
    public void update(T entity) {
        List<T> data = readFile();

        for (int i = 0; i < data.size(); i++) {
            if (getId(data.get(i)).equals(getId(entity))) {
                data.set(i, entity);
                writeFile(data);
                return;
            }
        }

        throw new RuntimeException("Entidad no encontrada para actualizar");
    }

    @Override
    public void deleteById(String id) {
        List<T> data = readFile();
        data.removeIf(e -> getId(e).equals(id));
        writeFile(data);
    }

    @Override
    public void replaceAll(List<T> data) {
        writeFile(data);
    }
}