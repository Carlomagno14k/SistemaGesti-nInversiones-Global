package co.edu.uptc.service;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.reflect.*;

import co.edu.uptc.model.Asset;
import co.edu.uptc.model.enums.AssetType;
import co.edu.uptc.repository.JsonRepository;

public class AssetService {
    JsonRepository<Asset> repo;

    public AssetService() {
        Type type=new TypeToken<List<Asset>>(){}.getType();
        repo=new JsonRepository<>("assets.json", type);
    }

    // Registrar 
    public void createAsset(String id, String name, AssetType assetType, double actualPrice, double volatility){
        repo.save(new Asset(id, name, assetType, actualPrice, volatility));
    }
    
    //Consultar Activos
    public List<Asset> listAssets(){
        return repo.findAll();
    }

    //Consultar activos filtrando por tipo o rango de precio (Falta)

    //Actualizar precio del activo y recalcular rendimientos automáticamente (Falta)

    public double getPrice(String assetId) { //Trae el precio Actual Por Id

    List<Asset> assets = repo.findAll();

    for (Asset asset : assets) {
        if (asset.getId().equals(assetId)) {
            return asset.getActualPrice();
        }
    }

    throw new RuntimeException("Asset not found");
}



}
