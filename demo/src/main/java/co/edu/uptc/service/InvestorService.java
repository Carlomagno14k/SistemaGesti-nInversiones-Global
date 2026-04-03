package co.edu.uptc.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.*;

import co.edu.uptc.model.Inversion;
import co.edu.uptc.model.Investor;
import co.edu.uptc.model.enums.RiskProfile;
import co.edu.uptc.repository.JsonRepository;

public class InvestorService {
    JsonRepository<Investor> repo;

    public InvestorService() {
        Type type=new TypeToken<List<Investor>>(){}.getType();
        repo=new JsonRepository<>("investors.json", type);
    }

    //Registrar 
    public void createInvestor(String id, String name, String email, double availableCapital, RiskProfile riskProfile,
            List<Inversion> inversions){
                repo.save(new Investor(id, name, email, availableCapital, riskProfile, new ArrayList<>()));
    }
    
    //Consultar
    public List<Investor> listInversionists(){
        return repo.findAll();
    }

    //Modificar (Falta)
    

    //Eliminar (Falta)


    public Investor findById(String id){
        return repo.findAll()
                   .stream()
                   .filter(i -> i.getId().equals(id))
                   .findFirst()
                   .orElse(null);
    }

    public void updateCapital(String id, double purchaseValue){
        List<Investor> investors = repo.findAll();

        for (Investor inv : investors) {
            if (inv.getAvailableCapital() < purchaseValue) throw new RuntimeException("Insufficient capital");

            if (inv.getId().equals(id)) {

                double newCapital = inv.getAvailableCapital() - purchaseValue;
                inv.setAvailableCapital(newCapital);

                repo.replaceAll(investors);
                return;
            }
        }

        throw new RuntimeException("Inverstor not found.");
    }

    public RiskProfile getRiskProfile(String id){
        Investor inv = findById(id);

        if (inv == null) {
            throw new RuntimeException("Investor not found.");
        }

        return inv.getRiskProfile();
    }

    public double getAvailableCapital(String id){ //Para evitar pasar el capital directamente, obtiene el capital por id
        Investor inv = findById(id);

        if (inv == null) {
            throw new RuntimeException("Inversionist not found.");
        }

        return inv.getAvailableCapital();
    }
}
