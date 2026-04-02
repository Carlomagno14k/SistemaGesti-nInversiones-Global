package co.edu.uptc.service;


import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import co.edu.uptc.model.Asset;
import co.edu.uptc.model.Inversion;
import co.edu.uptc.repository.JsonRepository;

public class InversionService {
    JsonRepository<Inversion> repo;

    public InversionService() {
        Type type=new TypeToken<List<Inversion>>(){}.getType();
        repo=new JsonRepository<>("inversions.json", type);
    }

    public void createInversion(String id, String inversionistId, String assetId, double amount, double purchaseAmount,
            LocalDate date, LocalTime time){
        repo.save(new Inversion(id, inversionistId, assetId, amount, purchaseAmount, date, time));
    }
    
}
