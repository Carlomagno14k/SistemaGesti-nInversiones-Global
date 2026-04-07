package co.edu.uptc.control;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import co.edu.uptc.exception.OperationCancelledException;
import co.edu.uptc.model.Investment;
import co.edu.uptc.model.Investor;
import co.edu.uptc.service.InvestmentService;
import co.edu.uptc.service.InvestorService;
import co.edu.uptc.service.PortfolioService;
import co.edu.uptc.view.ConsoleView;

public class PortfolioController {

    private final PortfolioService portfolioService;
    private final InvestmentService investmentService;
    private final InvestorService investorService;
    private final ConsoleView view;

    public PortfolioController(PortfolioService portfolioService, InvestmentService investmentService, InvestorService investorService, ConsoleView view) {
        this.portfolioService = portfolioService;
        this.investmentService = investmentService;
        this.investorService = investorService;
        this.view = view;
    }

    public void handleTop5InvestorsReport() {
        try {
            view.showMessageByKey("msg.title.top5Investors");
            
            List<Investor> investors = investorService.listInversionists();
            if (investors.isEmpty()) {
                view.showMessageByKey("msg.error.notEnoughData");
                return;
            }

            view.showMessageByKey("msg.info.generatingTop5");
            view.printText("--------------------------------------------------");

            // 1. Ordenamos la lista de mayor a menor rendimiento usando Streams
            List<Investor> top5 = investors.stream()
                .sorted((inv1, inv2) -> Double.compare(
                    portfolioService.calculateYieldPercentage(inv2), 
                    portfolioService.calculateYieldPercentage(inv1)
                ))
                .limit(5) // 2. Cortamos para que solo sean los 5 primeros
                .toList();

            // 3. Imprimimos el ranking
            int rank = 1;
            for (Investor inv : top5) {
                double totalInvested = portfolioService.calculateTotalInvested(inv);
                double currentValue = portfolioService.calculateCurrentPortfolioValue(inv);
                double yield = portfolioService.calculateYieldPercentage(inv);

                // Llamamos a la llave del properties en lugar del texto fijo
                String reportLine = String.format(view.getLocalizedText("msg.format.top5Investor"), 
                                                  rank, inv.getName(), yield, currentValue);
                
                view.printText(reportLine);
                rank++;
            }

        } catch (OperationCancelledException e) {
            view.printText(e.getMessage());
        } catch (RuntimeException e) {
            view.printText(view.getLocalizedText("msg.error.system") + " " + e.getMessage());
        }
    }
    /**
     * Genera un reporte de ganancias y pérdidas de TODAS las inversiones del sistema en un rango de fechas.
     */
    public void handleGlobalEarningsReport() {
        try {
            view.showMessageByKey("msg.title.globalReport");
            
            LocalDate startDate = promptForDate("msg.input.startDate");
            LocalDate endDate = promptForDate("msg.input.endDate");

            if (startDate.isAfter(endDate)) {
                view.showMessageByKey("msg.error.invalidDateRange");
                return;
            }

            List<Investment> allInvestments = investmentService.listInvestments();
            double totalEarnings = portfolioService.calculateEarningsByPeriod(allInvestments, startDate, endDate);

            printReportResult(startDate, endDate, totalEarnings);

        } catch (OperationCancelledException e) {
            // Atrapa limpiamente la cancelación ("X")
            view.printText(e.getMessage());
        } catch (IllegalArgumentException e) {
            // Atrapa errores de negocio (fechas invertidas)
            view.printText("Error: " + e.getMessage()); 
        } catch (RuntimeException e) {
            // Atrapa cualquier otro fallo inesperado
            view.showMessageByKey("msg.error.system");
            view.printText(e.getMessage());
        }
    }

    /**
     * Genera un reporte de ganancias y pérdidas de UN SOLO INVERSIONISTA en un rango de fechas.
     */
    public void handleInvestorEarningsReport(String investorId) {
        try {
            view.showMessageByKey("msg.title.investorReport");
            
            Investor investor = investorService.findById(investorId);
            if (investor == null) {
                view.showMessageByKey("msg.error.investorNotFound");
                return;
            }

            double totalInvested = portfolioService.calculateTotalInvested(investor);
            double currentValue = portfolioService.calculateCurrentPortfolioValue(investor);
            double yield = portfolioService.calculateYieldPercentage(investor);
            double risk = portfolioService.calculatePortfolioRisk(investor);

            view.printText("\n" + view.getLocalizedText("msg.report.portfolioHeader"));
            view.printText(view.getLocalizedText("msg.report.investorName") + " " + investor.getName());
            view.printText(String.format(view.getLocalizedText("msg.report.initialInvested"), totalInvested));
            view.printText(String.format(view.getLocalizedText("msg.report.currentValue"), currentValue));
            view.printText(String.format(view.getLocalizedText("msg.report.netYield"), yield));
            view.printText(String.format(view.getLocalizedText("msg.report.averageRisk"), risk));

        } catch (OperationCancelledException e) {
            view.printText(e.getMessage());
        } catch (RuntimeException e) {
            view.printText(view.getLocalizedText("msg.error.system") + " " + e.getMessage());
        }
    }
    public void handleExportReportCSV() {
        try {
            view.printText("\n" + view.getLocalizedText("msg.report.exportingCsv"));
            List<Investor> investors = investorService.listInversionists();
            
            if (investors.isEmpty()) {
                view.showMessageByKey("msg.error.notEnoughData");
                return;
            }

            String fileName = "reporte_portafolio.csv";
            
            try (FileWriter writer = new FileWriter(fileName)) {
                // Las cabeceras del CSV las dejamos estándar (como llaves de datos)
                writer.write("ID,Nombre,Email,CapitalDisponible,ValorPortafolioActual,Rendimiento(%),RiesgoPromedio\n");
                
                for (Investor inv : investors) {
                    double currentVal = portfolioService.calculateCurrentPortfolioValue(inv);
                    double yield = portfolioService.calculateYieldPercentage(inv);
                    double risk = portfolioService.calculatePortfolioRisk(inv);
                    
                    writer.write(String.format("%s,%s,%s,%.2f,%.2f,%.2f,%.4f\n", 
                        inv.getId(), inv.getName(), inv.getEmail(), 
                        inv.getAvailableCapital(), currentVal, yield, risk));
                }
                view.printText(view.getLocalizedText("msg.success.exportCsv") + " " + fileName);
            } catch (IOException e) {
                view.printText(view.getLocalizedText("msg.error.exportCsv") + " " + e.getMessage());
            }

        } catch (Exception e) {
            view.printText(view.getLocalizedText("msg.error.system") + " " + e.getMessage());
        }
    }

    // ---------------- MÉTODOS AUXILIARES PRIVADOS ----------------

    /**
     * Método auxiliar para pedir una fecha y manejar el error de formato automáticamente.
     */
    private LocalDate promptForDate(String messageKey) {
        while (true) {
            String dateStr = view.readStringInput(messageKey);
            try {
                return LocalDate.parse(dateStr);
            } catch (DateTimeParseException e) {
                // Ahora usamos el properties en lugar del texto en duro
                view.showMessageByKey("msg.error.invalidDateFormat");
            }
        }
    }

    /**
     * Método auxiliar para imprimir el resultado de forma estilizada y 100% bilingüe.
     */
    private void printReportResult(LocalDate start, LocalDate end, double earnings) {
        view.printText("\n=========================================");
        
        // Imprime el titulo usando String.format para inyectar las fechas en el texto traducido
        view.printText(String.format(view.getLocalizedText("msg.report.header"), start, end));
        
        if (earnings > 0) {
            view.printText(String.format(view.getLocalizedText("msg.report.profit"), earnings));
            view.showMessageByKey("msg.report.profitDesc");
        } else if (earnings < 0) {
            view.printText(String.format(view.getLocalizedText("msg.report.loss"), Math.abs(earnings)));
            view.showMessageByKey("msg.report.lossDesc");
        } else {
            view.showMessageByKey("msg.report.even");
            view.showMessageByKey("msg.report.evenDesc");
        }
        
        view.printText("=========================================\n");
    }
}