package co.edu.uptc.repository;

import com.google.gson.*;
import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class JsonRepository<T> {

    private final String filePath;
    private final Type type;
    private final Gson gson;

    public JsonRepository(String filePath, Type type) {
        this.filePath = filePath;
        this.type = type;
        this.gson = createGson();
    }

    private Gson createGson() {
        return new GsonBuilder()

                //LocalDate
                .registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
                    @Override
                    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                            throws JsonParseException {
                        return LocalDate.parse(json.getAsString());
                    }
                })

                //LocalTime
                .registerTypeAdapter(LocalTime.class, new JsonDeserializer<LocalTime>() {
                    @Override
                    public LocalTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                            throws JsonParseException {
                        return LocalTime.parse(json.getAsString());
                    }
                })

                .setPrettyPrinting()
                .create();
    }

    //OBTENER TODOS LOS DATOS
    public List<T> findAll() {

        try (Reader reader = new FileReader(filePath)) {

            List<T> data = gson.fromJson(reader, type);

            return data != null ? data : new ArrayList<>();

        } catch (FileNotFoundException e) {
            // Si el archivo no existe, retorna lista vacía
            return new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    //GUARDAR TODOS LOS DATOS
    public void saveAll(List<T> data) {

        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //AGREGAR UN ELEMENTO
    public void save(T element) {

        List<T> data = findAll();
        data.add(element);
        saveAll(data);
    }

    //ACTUALIZAR
    public void replaceAll(List<T> data) {
        saveAll(data);
    }
}