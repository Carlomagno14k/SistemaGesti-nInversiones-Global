package co.edu.uptc.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.google.gson.reflect.TypeToken;

import co.edu.uptc.exception.AssetNotFoundException;
import co.edu.uptc.model.Asset;
import co.edu.uptc.model.enums.AssetType;
import co.edu.uptc.repository.JsonRepository;

class AssetServiceTest {

    @TempDir
    Path tempDir;

    private AssetService service;

    @BeforeEach
    void setUp() {
        Type type = new TypeToken<List<Asset>>() {}.getType();
        JsonRepository<Asset> repo = new JsonRepository<>(tempDir.resolve("assets.json").toString(), type);
        service = new AssetService(repo);
    }

    @Test
    void getPrice_returnsPriceWhenAssetExists() {
        service.createAsset("A001", "Test Bond", AssetType.BOND, 12.5, 0.02);

        assertEquals(12.5, service.getPrice("A001"), 0.0001);
    }

    @Test
    void getPrice_throwsWhenAssetMissing() {
        assertThrows(AssetNotFoundException.class, () -> service.getPrice("A998"));
    }

    @Test
    void findById_returnsNullWhenMissing() {
        assertNull(service.findById("A997"));
    }

    @Test
    void listAssets_returnsAllCreated() {
        service.createAsset("A001", "One", AssetType.BOND, 1.0, 0.0);
        service.createAsset("A002", "Two", AssetType.STOCK, 2.0, 0.1);

        assertEquals(2, service.listAssets().size());
    }

    @Test
    void findByType_filtersCorrectly() {
        service.createAsset("A010", "Bond", AssetType.BOND, 5.0, 0.0);
        service.createAsset("A011", "Stock", AssetType.STOCK, 8.0, 0.0);

        List<Asset> bonds = service.findByType(AssetType.BOND);
        assertEquals(1, bonds.size());
        assertEquals("A010", bonds.get(0).getId());
    }

    @Test
    void findByPriceRange_includesOnlyInRange() {
        service.createAsset("A020", "Low", AssetType.BOND, 10.0, 0.0);
        service.createAsset("A021", "Mid", AssetType.BOND, 25.0, 0.0);
        service.createAsset("A022", "High", AssetType.BOND, 100.0, 0.0);

        List<Asset> mid = service.findByPriceRange(20.0, 30.0);
        assertEquals(1, mid.size());
        assertEquals("A021", mid.get(0).getId());
    }

    @Test
    void updAssetPrice_updatesStoredPrice() {
        service.createAsset("A030", "Up", AssetType.ETF, 30.0, 0.0);
        service.updAssetPrice("A030", 45.5);
        assertEquals(45.5, service.getPrice("A030"), 0.0001);
    }

    @Test
    void updAssetPrice_throwsWhenIdUnknown() {
        assertThrows(AssetNotFoundException.class, () -> service.updAssetPrice("A996", 1.0));
    }
}
