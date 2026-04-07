package co.edu.uptc.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.google.gson.reflect.TypeToken;

import co.edu.uptc.exception.IncompatibleRiskProfileException;
import co.edu.uptc.exception.InsufficientCapitalException;
import co.edu.uptc.model.Asset;
import co.edu.uptc.model.Investment;
import co.edu.uptc.model.enums.AssetType;
import co.edu.uptc.model.enums.RiskProfile;
import co.edu.uptc.repository.JsonRepository;

class InvestmentServiceTest {

    @TempDir
    private Path tempDir;

    private InvestmentService service;

    @BeforeEach
    void setUp() {
        Type assetType = new TypeToken<List<Asset>>() {}.getType();
        JsonRepository<Asset> assetRepo = new JsonRepository<>(tempDir.resolve("assets.json").toString(), assetType);
        AssetService assetService = new AssetService(assetRepo);
        assetService.createAsset("A010", "Activo test", AssetType.BOND, 10.0, 0.0);
        assetService.createAsset("A011", "Yield", AssetType.BOND, 50.0, 0.0);
        assetService.createAsset("A012", "Crypto test", AssetType.CRYPTO, 100.0, 0.5);

        Type type = new TypeToken<List<Investment>>() {}.getType();
        JsonRepository<Investment> repo1 = new JsonRepository<>(tempDir.resolve("inversions.json").toString(), type);
        service = new InvestmentService(repo1, assetService);
    }

    @Test
    void createInversion_persistsWhenCapitalAndRiskValid() {
        LocalDate d = LocalDate.of(2026, 1, 10);
        LocalTime t = LocalTime.of(10, 0);

        Investment inv = service.createInvestment("INV001", "I001", "A010", 10, 5.0, d, t, 500.0, RiskProfile.MODERATE);
        assertEquals("INV001", inv.getId());
        assertEquals(100.0, inv.getPurchasePrice(), 0.0001);
        assertEquals(1, service.listInvestments().size());
        assertEquals("INV001", service.listInvestments().get(0).getId());
    }

    @Test
    void createInversion_throwsInsufficientCapital() {
        LocalDate d = LocalDate.of(2026, 1, 10);
        LocalTime t = LocalTime.of(10, 0);

        assertThrows(InsufficientCapitalException.class,
                () -> service.createInvestment("INV002", "I001", "A010", 10, 5.0, d, t, 10.0, RiskProfile.MODERATE));
    }

    @Test
    void createInversion_throwsIncompatibleRiskProfile() {
        LocalDate d = LocalDate.of(2026, 1, 10);
        LocalTime t = LocalTime.of(10, 0);

        assertThrows(IncompatibleRiskProfileException.class,
                () -> service.createInvestment("INV003", "I001", "A012", 2, 100.0, d, t, 500.0, RiskProfile.CONSERVATIVE));
    }

    @Test
    void calculateActualValue_multipliesPriceByAmount() {
        LocalDate d = LocalDate.of(2026, 1, 10);
        LocalTime t = LocalTime.NOON;
        Investment inv = new Investment("INV099", "I001", "A010", 10, 5.0, d, t);
        assertEquals(100.0, service.calculateCurrentValue(inv), 0.0001);
    }

    @Test
    void calculateEarnings_returnsDifference() {
        assertEquals(10.0, service.calculateEarnings(50.0, 40.0), 0.0001);
        assertEquals(-5.0, service.calculateEarnings(35.0, 40.0), 0.0001);
    }

    @Test
    void calculateYieldPercentage_returnsPercent() {
        LocalDate d = LocalDate.of(2026, 1, 10);
        LocalTime t = LocalTime.NOON;
        Investment inv = new Investment("INV100", "I001", "A011", 1, 40.0, d, t);
        assertEquals(25.0, service.calculateYieldPercentage(inv), 0.0001);
    }

    @Test
    void calculateYieldPercentage_returnsZeroWhenPurchasePriceZero() {
        LocalDate d = LocalDate.of(2026, 1, 10);
        LocalTime t = LocalTime.NOON;
        Investment inv = new Investment("INV101", "I001", "A010", 10, 0.0, d, t);
        assertEquals(0.0, service.calculateYieldPercentage(inv), 0.0001);
    }

    @Test
    void validateAvailableCapital_returnsTrueWhenEnough() {
        assertEquals(true, service.validateAvailableCapital(100.0, 100.0));
        assertEquals(true, service.validateAvailableCapital(200.0, 100.0));
        assertEquals(false, service.validateAvailableCapital(50.0, 100.0));
    }

    @Test
    void validateRiskProfile_doesNotThrowWhenCompatible() {
        service.validateRiskProfile(RiskProfile.MODERATE, AssetType.ETF);
    }

    @Test
    void validateRiskProfile_throwsWhenIncompatible() {
        assertThrows(IncompatibleRiskProfileException.class,
                () -> service.validateRiskProfile(RiskProfile.CONSERVATIVE, AssetType.CRYPTO));
    }

    @Test
    void getInvestmentsByInvestorId_returnsOnlyMatchingInvestor() {
        LocalDate d = LocalDate.of(2026, 3, 1);
        LocalTime t = LocalTime.NOON;
        service.createInvestment("INV010", "I020", "A010", 1, 0.0, d, t, 500.0, RiskProfile.MODERATE);
        service.createInvestment("INV011", "I021", "A010", 1, 0.0, d, t, 500.0, RiskProfile.MODERATE);

        assertEquals(1, service.getInvestmentsByInvestorId("I020").size());
        assertEquals("INV010", service.getInvestmentsByInvestorId("I020").get(0).getId());
    }
}
