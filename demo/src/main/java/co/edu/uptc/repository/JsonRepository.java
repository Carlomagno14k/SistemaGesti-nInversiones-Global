package co.edu.uptc.repository;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class JsonRepository<T> implements Repository<T> { //AMBOS TIENEN <T>
    private String filename;
    private Type type;
    private Gson gson;
    
    public JsonRepository(String filename, Type type) {
        this.filename = filename;
        this.type = type;
        this.gson = new Gson();
    }

    @Override
    public void save(T entity) {
        List<T>data=findAll();
        data.add(entity);
        try (FileWriter writer= new FileWriter(filename)){
            gson.toJson(data,writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<T> findAll() {
        try (FileReader reader= new FileReader(filename)){
            List<T>data=gson.fromJson(reader, type);
            if (data==null) {
                return new ArrayList<>();
            }
            return data;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void replaceAll(List<T> data) {
        try (FileWriter writer= new FileWriter(filename)){
            gson.toJson(data,writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
