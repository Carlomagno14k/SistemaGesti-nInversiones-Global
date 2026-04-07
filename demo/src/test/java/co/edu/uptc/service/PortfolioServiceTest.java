package co.edu.uptc.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.google.gson.reflect.TypeToken;

import co.edu.uptc.model.Asset;
import co.edu.uptc.model.Investment;
import co.edu.uptc.model.Investor;
import co.edu.uptc.model.enums.AssetType;
import co.edu.uptc.model.enums.RiskProfile;
import co.edu.uptc.repository.JsonRepository;

class PortfolioServiceTest {

    @TempDir
    private Path tempDir;

    private PortfolioService portfolioService;
    private AssetService assetService;

    @BeforeEach
    void setUp() {
        Type assetListType = new TypeToken<List<Asset>>() {}.getType();
        JsonRepository<Asset> assetRepo = new JsonRepository<>(tempDir.resolve("assets.json").toString(), assetListType);
        assetService = new AssetService(assetRepo);

        Type invListType = new TypeToken<List<Investment>>() {}.getType();
        JsonRepository<Investment> invRepo = new JsonRepository<>(tempDir.resolve("inversions.json").toString(), invListType);
        InvestmentService inversionService = new InvestmentService(invRepo, assetService);
        portfolioService = new PortfolioService(inversionService, assetService, null);

        assetService.createAsset("A001", "Activo prueba", AssetType.BOND, 10.0, 0.0);
    }

    @Test
    void calculateEarningsByPeriod_throwsWhenStartAfterEnd() {
        assertThrows(IllegalArgumentException.class,
                () -> portfolioService.calculateEarningsByPeriod(
                        List.of(),
                        LocalDate.of(2026, 2, 1),
                        LocalDate.of(2026, 1, 1)));
    }

    @Test
    void calculateEarningsByPeriod_sumsOnlyInversionsInRange() {
        Investment inside = new Investment("INV001", "I001", "A001", 2, 4.0, LocalDate.of(2026, 1, 15), LocalTime.NOON);
        Investment outside = new Investment("INV002", "I001", "A001", 1, 1.0, LocalDate.of(2025, 12, 1), LocalTime.NOON);

        double total = portfolioService.calculateEarningsByPeriod(
                List.of(inside, outside),
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31));

        assertEquals(12.0, total, 0.0001);
    }

    @Test
    void calculateEarningsByPeriod_returnsZeroWhenNoMatches() {
        Investment outside = new Investment("INV002", "I001", "A001", 1, 1.0, LocalDate.of(2025, 12, 1), LocalTime.NOON);

        double total = portfolioService.calculateEarningsByPeriod(
                List.of(outside),
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31));

        assertEquals(0.0, total, 0.0001);
    }

    @Test
    void calculateTotalInvested_emptyPortfolio_returnsZero() {
        Investor inv = new Investor("I001", "N", "e", 1000.0, RiskProfile.MODERATE, new ArrayList<>());
        assertEquals(0.0, portfolioService.calculateTotalInvested(inv), 0.0001);
    }

    @Test
    void calculateTotalInvested_sumsPurchasePrices() {
        List<Investment> list = List.of(
                new Investment("INV010", "I001", "A001", 2, 100.0, LocalDate.of(2026, 3, 1), LocalTime.NOON),
                new Investment("INV011", "I001", "A001", 1, 50.0, LocalDate.of(2026, 3, 2), LocalTime.NOON));
        Investor inv = new Investor("I001", "N", "e", 1000.0, RiskProfile.MODERATE, list);
        assertEquals(150.0, portfolioService.calculateTotalInvested(inv), 0.0001);
    }

    @Test
    void calculateCurrentPortfolioValue_usesAssetPrices() {
        List<Investment> list = List.of(
                new Investment("INV012", "I001", "A001", 3, 30.0, LocalDate.now(), LocalTime.NOON));
        Investor inv = new Investor("I001", "N", "e", 1000.0, RiskProfile.MODERATE, list);
        assertEquals(30.0, portfolioService.calculateCurrentPortfolioValue(inv), 0.0001);
    }

    @Test
    void calculateYieldPercentage_reflectsGain() {
        assetService.createAsset("A002", "Gain", AssetType.BOND, 100.0, 0.0);
        List<Investment> list = List.of(
                new Investment("INV013", "I001", "A002", 1, 50.0, LocalDate.now(), LocalTime.NOON));
        Investor inv = new Investor("I001", "N", "e", 1000.0, RiskProfile.MODERATE, list);
        assertEquals(100.0, portfolioService.calculateYieldPercentage(inv), 0.0001);
    }

    @Test
    void calculatePortfolioRisk_weightedByValueAndVolatility() {
        assetService.createAsset("A003", "Risky", AssetType.STOCK, 50.0, 0.2);
        List<Investment> list = List.of(
                new Investment("INV014", "I001", "A001", 2, 20.0, LocalDate.now(), LocalTime.NOON),
                new Investment("INV015", "I001", "A003", 1, 50.0, LocalDate.now(), LocalTime.NOON));
        Investor investor = new Investor("I001", "N", "e", 1000.0, RiskProfile.MODERATE, list);
        double expected = (20.0 * 0.0 + 50.0 * 0.2) / (20.0 + 50.0);
        assertEquals(expected, portfolioService.calculatePortfolioRisk(investor), 0.0001);
    }

    @Test
    void getTop5InvestorsByYield_ordersDescending() {
        Type assetListType = new TypeToken<List<Asset>>() {}.getType();
        JsonRepository<Asset> assetRepo = new JsonRepository<>(tempDir.resolve("assets_top5.json").toString(), assetListType);
        AssetService as = new AssetService(assetRepo);
        as.createAsset("A004", "A", AssetType.BOND, 100.0, 0.0);

        Type invListType = new TypeToken<List<Investment>>() {}.getType();
        JsonRepository<Investment> invRepo = new JsonRepository<>(tempDir.resolve("inv_top5.json").toString(), invListType);
        InvestmentService invSvc = new InvestmentService(invRepo, as);

        Type investorListType = new TypeToken<List<Investor>>() {}.getType();
        JsonRepository<Investor> investorRepo = new JsonRepository<>(tempDir.resolve("investors_top5.json").toString(), investorListType);
        InvestorService investorService = new InvestorService(investorRepo);

        PortfolioService ps = new PortfolioService(invSvc, as, investorService);

        investorService.createInvestor("I010", "Low", "l@test.com", 10000.0, RiskProfile.MODERATE, Collections.emptyList());
        Investor low = investorService.findById("I010");
        low.setInvestments(List.of(
                new Investment("INV050", "I010", "A004", 1, 100.0, LocalDate.now(), LocalTime.NOON)));
        investorService.updateInvestor(low);

        investorService.createInvestor("I011", "High", "h@test.com", 10000.0, RiskProfile.MODERATE, Collections.emptyList());
        Investor high = investorService.findById("I011");
        high.setInvestments(List.of(
                new Investment("INV051", "I011", "A004", 1, 50.0, LocalDate.now(), LocalTime.NOON)));
        investorService.updateInvestor(high);

        List<Investor> top = ps.getTop5InvestorsByYield();
        assertEquals(2, top.size());
        assertEquals("I011", top.get(0).getId());
        assertEquals("I010", top.get(1).getId());
    }
}
