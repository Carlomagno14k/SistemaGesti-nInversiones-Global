package co.edu.uptc.control;

import co.edu.uptc.model.Asset;
import co.edu.uptc.model.enums.AssetType;
import co.edu.uptc.service.AssetService;
import co.edu.uptc.view.ConsoleView;
import co.edu.uptc.exception.AssetNotFoundException;
import co.edu.uptc.exception.OperationCancelledException;

import java.util.List;

public class AssetController {
    
    private final AssetService assetService;
    private final ConsoleView view;

    public AssetController(AssetService assetService, ConsoleView view) {
        this.assetService = assetService;
        this.view = view;
    }

    /**
     * Maneja la creación de un nuevo activo.
     */
    public void handleCreateAsset() {
        try {
            view.showMessageByKey("msg.title.createAsset");
            
            String id = view.readStringInput("msg.input.assetId");
            String name = view.readStringInput("msg.input.assetName");
            String typeStr = view.readStringInput("msg.input.assetType");
             AssetType assetType;
                        switch (typeStr.toUpperCase()) {
                            case "BONO": case "BOND": 
                                assetType = AssetType.BOND; break;
                            case "ACCION": case "ACCIÓN": case "STOCK": 
                                assetType = AssetType.STOCK; break;
                            case "ETF": 
                                assetType = AssetType.ETF; break;
                            case "PROPIEDAD": case "INMUEBLE": case "PROPERTY": 
                                assetType = AssetType.PROPERTY; break;
                            case "DIVISA": case "BADGE": 
                                assetType = AssetType.BADGE; break;
                            case "CRIPTOMONEDA": case "CRIPTO": case "CRYPTO": 
                                assetType = AssetType.CRYPTO; break;
                            case "NFT": 
                                assetType = AssetType.NFT; break;
                            default: 
                                throw new IllegalArgumentException("Tipo de activo inválido / Invalid asset type");
                        }
            
            double price = view.readDoubleInput("msg.input.assetPrice");
            double volatility = view.readDoubleInput("msg.input.assetVolatility");

            assetService.createAsset(id, name, assetType, price, volatility);
            view.showMessageByKey("msg.success.assetCreated");

        } catch (OperationCancelledException e) {
            view.printText(e.getMessage());
        } catch (IllegalArgumentException e) {
            view.showMessageByKey("msg.error.invalidAssetType");
        } catch (RuntimeException e) {
            view.showMessageByKey("msg.error.system");
            view.printText(e.getMessage());
        }
    }

    /**
     * Maneja el listado de todos los activos.
     */
    public void handleListAssets() {
    try {
        view.showMessageByKey("msg.title.listAssets");
        List<Asset> assets = assetService.listAssets();

        if (assets.isEmpty()) {
            view.showMessageByKey("msg.error.noAssets");
        } else {
            for (Asset asset : assets) {
                // 1. Traducimos el Enum usando la convención de nombres
                String tipoTraducido = view.getLocalizedText("enum.AssetType." + asset.getAssetType().name());

                // 2. Pasamos 'tipoTraducido' en lugar de 'asset.getAssetType()'
                String detailLine = String.format(view.getLocalizedText("msg.format.assetDetail"), 
                    asset.getId(), 
                    asset.getName(), 
                    tipoTraducido, 
                    asset.getActualPrice());
                
                view.printText(detailLine);
            }
        }
    } catch (RuntimeException e) {
        view.showMessageByKey("msg.error.system");
        view.printText(e.getMessage());
    }
}

    /**
     * Maneja la actualización del precio de un activo.
     */
    public void handleUpdateAssetPrice() {
        try {
            view.showMessageByKey("msg.title.updatePrice");
            
            String id = view.readStringInput("msg.input.assetId");
            double newPrice = view.readDoubleInput("msg.input.newPrice");

            assetService.updAssetPrice(id, newPrice);
            view.showMessageByKey("msg.success.priceUpdated");

        } catch (OperationCancelledException e) {
            view.printText(e.getMessage());
        } catch (AssetNotFoundException e) {
            view.printText(e.getMessage());
        } catch (RuntimeException e) {
            view.showMessageByKey("msg.error.system");
            view.printText(e.getMessage());
        }
    }

    /**
     * Maneja la búsqueda por rango de precio.
     */
    public void handleFindByPriceRange() {
        try {
            view.showMessageByKey("msg.title.priceRange");
            
            double minPrice = view.readDoubleInput("msg.input.minPrice");
            double maxPrice = view.readDoubleInput("msg.input.maxPrice");

            List<Asset> assets = assetService.findByPriceRange(minPrice, maxPrice);

            if (assets.isEmpty()) {
                view.showMessageByKey("msg.error.noAssetsInRange");
            } else {
                for (Asset asset : assets) {
                    // Formato localizado para búsqueda simple
                    String line = String.format(view.getLocalizedText("msg.format.assetSimpleDetail"), 
                        asset.getName(), asset.getActualPrice());
                    view.printText(line);
                }
            }
        } catch (OperationCancelledException e) {
            view.printText(e.getMessage());
        } catch (RuntimeException e) {
            view.showMessageByKey("msg.error.system");
            view.printText(e.getMessage());
        }
    }
    public void handleFilterByType() {
        try {
            view.printText("\n--- BUSCAR ACTIVOS POR TIPO ---");
            String typeStr = view.readStringInput("Ingrese Tipo de Activo (BOND, STOCK, ETF, PROPERTY, BADGE, CRYPTO, NFT)");
            
            AssetType type = AssetType.valueOf(typeStr.toUpperCase());
            List<Asset> assets = assetService.findByType(type);

            if (assets.isEmpty()) {
                view.printText("No se encontraron activos de ese tipo.");
            } else {
                for (Asset asset : assets) {
                    view.printText(String.format("- %s | Precio: $%.2f | Riesgo (1-5): %d", 
                        asset.getName(), asset.getActualPrice(), asset.getAssetType().getRiskLevel()));
                }
            }
        } catch (IllegalArgumentException e) {
            view.printText("Error: Tipo de activo inválido. Debe escribir uno de los tipos permitidos.");
        } catch (OperationCancelledException e) {
            view.printText(e.getMessage());
        }
    }
}