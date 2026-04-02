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

    
    public void createAsset(String id, String name, AssetType assetType, double actualPrice, double volatility){
        repo.save(new Asset(id, name, assetType, actualPrice, volatility));
    }
    


}
