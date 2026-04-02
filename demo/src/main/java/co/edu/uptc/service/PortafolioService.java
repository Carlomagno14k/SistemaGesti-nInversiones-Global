package co.edu.uptc.service;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import co.edu.uptc.model.Inversion;
import co.edu.uptc.repository.JsonRepository;

public class PortafolioService {
    JsonRepository<Inversion> repo;

    public PortafolioService() {
        Type type=new TypeToken<List<Inversion>>(){}.getType();
        repo=new JsonRepository<>("inversions.json", type);
    }

}
