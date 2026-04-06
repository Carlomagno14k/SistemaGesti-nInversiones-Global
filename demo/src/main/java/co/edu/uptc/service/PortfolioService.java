package co.edu.uptc.service;

import java.time.LocalDate;
import java.util.List;

import co.edu.uptc.model.Asset;
import co.edu.uptc.model.Investment;
import co.edu.uptc.model.Investor;

/**
 * Servicio de agregación de portafolio: cálculos sobre conjuntos de inversiones,
 * como el total de ganancias o pérdidas en un intervalo de fechas (reporte por periodo).
 */
public class PortfolioService {
    private final InvestorService investorService;
    private final InvestmentService inversionService;
    private final AssetService assetService;

    /**
     * Construye el servicio de portafolio con las dependencias necesarias para valorar
     * cada inversión con el precio actual del activo.
     *
     * @param inversionService servicio que aporta fórmulas de valor, inversión inicial y ganancia
     * @param assetService servicio que resuelve el precio actual por activo
     */
    public PortfolioService(InvestmentService inversionService, AssetService assetService, InvestorService investorService) {
        this.inversionService = inversionService;
        this.assetService = assetService;
        this.investorService = investorService; 
    }

    /**
     * Calcula la suma de las ganancias o pérdidas monetarias de las inversiones cuya fecha
     * cae dentro del periodo indicado (inclusive en los extremos). Para cada operación se usa
     * el precio actual del activo frente al valor de compra, cumpliendo el reporte de
     * ganancias totales en un rango de tiempo.
     *
     * @param inversions lista de inversiones a considerar (habitualmente todas o las filtradas)
     * @param startDate fecha inicial del periodo (inclusive)
     * @param endDate fecha final del periodo (inclusive)
     * @return suma de (valor actual − inversión inicial) de las inversiones en el periodo
     * @throws IllegalArgumentException si la fecha de inicio es posterior a la fecha final
     */
    public double calculateEarningsByPeriod(List<Investment> inversions, LocalDate startDate, LocalDate endDate) {
        double total = 0;

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }


        for (Investment inv : inversions) {
            if ((inv.getDate().isEqual(startDate) || inv.getDate().isAfter(startDate))
                    && (inv.getDate().isEqual(endDate) || inv.getDate().isBefore(endDate))) {


                double actualValue = inversionService.calculateCurrentValue(inv);
                double purchaseValue = inversionService.calculatePurchasePrice(inv.getPurchasePrice(), inv.getAmount());

                total += inversionService.calculateEarnings(actualValue, purchaseValue);
            }
        }

        return total;

    }
    public List<Investor> getTop5InvestorsByYield() {
    List<Investor> allInvestors = investorService.listInversionists();

    return allInvestors.stream()
            .filter(inv -> inv.getInvestments() != null && !inv.getInvestments().isEmpty())
            .sorted((inv1, inv2) -> {
                double yield1 = calculateYieldPercentage(inv1);
                double yield2 = calculateYieldPercentage(inv2);
                return Double.compare(yield2, yield1); // Orden descendente
            })
            .limit(5)
            .toList();
}
public double calculateTotalInvested(Investor investor) {
        if (investor.getInvestments() == null || investor.getInvestments().isEmpty()) {
            return 0.0;
        }
        return investor.getInvestments().stream()
                .mapToDouble(Investment::getPurchasePrice)
                .sum();
    }
    public double calculateCurrentPortfolioValue(Investor investor) {
        if (investor.getInvestments() == null || investor.getInvestments().isEmpty()) {
            return 0.0;
        }
        
        return investor.getInvestments().stream()
                .mapToDouble(investment -> {
                    Asset asset = assetService.findById(investment.getAssetId());
                    if (asset != null) {
                        return investment.getAmount() * asset.getActualPrice();
                    }
                    return 0.0; 
                })
                .sum();
    }

public double calculateYieldPercentage(Investor investor) {
    double totalInvested = calculateTotalInvested(investor); 
    double currentValue = calculateCurrentPortfolioValue(investor);

    if (totalInvested == 0) return 0.0;
    
    // Formula: ((Valor Actual - Inversion Inicial) / Inversion Inicial) * 100
    return ((currentValue - totalInvested) / totalInvested) * 100.0;
}
public double calculatePortfolioRisk(Investor investor) {
        double totalValue = calculateCurrentPortfolioValue(investor);
        
        // Evitamos división por cero si el portafolio está vacío
        if (totalValue == 0) return 0.0;

        double weightedRiskSum = 0.0;
        
        List<Investment> investments = investor.getInvestments();
        if (investments != null) {
            for (Investment inv : investments) {
                Asset asset = assetService.findById(inv.getAssetId());
                if (asset != null) {
                    double currentInvValue = inv.getAmount() * asset.getActualPrice();
                    // Multiplicamos el valor que tiene invertido por la volatilidad (riesgo) de ese activo
                    weightedRiskSum += (currentInvValue * asset.getVolatility());
                }
            }
        }
        // Dividimos entre el total para sacar el promedio ponderado
        return weightedRiskSum / totalValue;
    }
}
