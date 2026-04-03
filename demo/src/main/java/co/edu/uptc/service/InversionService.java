package co.edu.uptc.service;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import com.google.gson.reflect.TypeToken;
import co.edu.uptc.model.Inversion;
import co.edu.uptc.model.enums.AssetType;
import co.edu.uptc.model.enums.RiskProfile;
import co.edu.uptc.repository.JsonRepository;

public class InversionService { //Cálculos individuales
    JsonRepository<Inversion> repo;

    public InversionService() {
        Type type=new TypeToken<List<Inversion>>(){}.getType();
        repo=new JsonRepository<>("inversions.json", type);
    }

    //Crear inversion
    public void createInversion(String id, String inversionistId, String assetId, double amount, double purchasePrice, 
            LocalDate date, LocalTime time, double availableCapital, RiskProfile riskProfile, AssetType assetType){
        
        double initialInvestment = calculateInitialInvestment(purchasePrice, amount);

        // validar capital
        if (!validateAvailableCapital(availableCapital, initialInvestment)) {
            throw new RuntimeException("Insufficient capital");
        }

        // validar riesgo
        validateRiskProfile(riskProfile, assetType);

        Inversion inversion=new Inversion(id, inversionistId, assetId, amount, purchasePrice, date, time);

        repo.save(inversion);
    }

    public List<Inversion> listInversions(){
        return repo.findAll();
    } 

    
    public double calculateActualValue(double actualPrice, double amount){ //Calcular Valor Actual
        return actualPrice*amount;
    }

    public double calculateInitialInvestment(double purchasePrice, double amount){  //Calcular Valor de Compra
        return purchasePrice*amount;
    };

    public double calculateEarnings(double actualValue, double initialInvestment){  //Calcular Ganancias
        return actualValue-initialInvestment;
    }

    //Calcular Pérdida monetaria (Falta)

    //Consultar historial de inversiones de un inversionista (Falta)

    public double calculatePerformance(double earning, double initialInvestment){   //Calcular Rendimiento
        if (initialInvestment==0) {
            throw new ArithmeticException("Purchase Cannot be Zero");
        }
        return (earning/initialInvestment)*100;
    }

    //No permitir invertir más capital del disponible
    public boolean validateAvailableCapital(double availableCapital, double initialInvestment){
        return availableCapital>=initialInvestment;
    }

    //Validar perfil de ries
    public void validateRiskProfile(RiskProfile riskProfile, AssetType assetType){
        if (assetType.getRiskLevel()>riskProfile.getMaxRisk()) {
            throw new RuntimeException("Risk Profile "+riskProfile+" does not allows to investing in "+assetType);
        }
    }
}
