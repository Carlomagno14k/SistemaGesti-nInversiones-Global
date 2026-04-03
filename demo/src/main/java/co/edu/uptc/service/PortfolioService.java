package co.edu.uptc.service;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import co.edu.uptc.model.Inversion;
import co.edu.uptc.repository.JsonRepository;

public class PortfolioService {
    private InversionService inversionService;
    private AssetService assetService;

    public PortfolioService(InversionService inversionService, AssetService assetService) {
        this.inversionService = inversionService;
        this.assetService = assetService;
    }

    public double calculateEarningsByPeriod(List<Inversion> inversions, LocalDate startDate, LocalDate endDate) {
        double total = 0;

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        for (Inversion inv : inversions) {
            if ((inv.getDate().isEqual(startDate) || inv.getDate().isAfter(startDate)) &&
                (inv.getDate().isEqual(endDate) || inv.getDate().isBefore(endDate))) {

                double actualPrice = assetService.getPrice(inv.getAssetId());

                double actualValue = inversionService.calculateActualValue(actualPrice, inv.getAmount());
                double purchaseValue = inversionService.calculateInitialInvestment(inv.getPurchasePrice(), inv.getAmount());

                total += inversionService.calculateEarnings(actualValue, purchaseValue);
            }
        }

        return total;
    }
}

/*PortafolioService:
- valorTotal
- gananciaTotal
- rendimientoTotal
- riesgo promedio
- ranking
*/