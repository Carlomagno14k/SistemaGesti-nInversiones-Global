package co.edu.uptc.view;

import co.edu.uptc.control.*;
import co.edu.uptc.service.*;
import co.edu.uptc.view.ConsoleView;

public class Main {

    private static ConsoleView view;
    private static AssetController assetController;
    private static InvestorController investorController;
    private static InvestmentController investmentController;
    private static PortfolioController portfolioController;

    public static void main(String[] args) {
        view = new ConsoleView();

        AssetService assetService = new AssetService();
        InvestorService investorService = new InvestorService();
        InvestmentService investmentService = new InvestmentService();
        PortfolioService portfolioService = new PortfolioService(investmentService, assetService);

        assetController = new AssetController(assetService, view);
        investorController = new InvestorController(investorService, view);
        investmentController = new InvestmentController(investmentService, assetService, investorService, view);
        portfolioController = new PortfolioController(portfolioService, investmentService, view);

        runStartMenu();
    }

    private static void runStartMenu() {
        boolean exit = false;
        while (!exit) {
            view.showStartMenu();
            int option = view.readIntInput("msg.input.select");

            switch (option) {
                case 1:
                    investorController.handleCreateInvestor();
                    break;
                case 2:
                    String loggedInId = investorController.handleLogin();
                    if (loggedInId != null) {
                        runInvestorMenu(loggedInId);
                    }
                    break;
                case 3:
                    runAdminMenu();
                    break;
                case 4:
                    changeLanguage();
                    break;
                case 0:
                    view.showMessageByKey("msg.goodbye");
                    exit = true;
                    break;
                default:
                    view.showMessageByKey("msg.error.invalid");
            }
        }
    }

    private static void runInvestorMenu(String currentInvestorId) {
        boolean logout = false;
        while (!logout) {
            view.showInvestorDashboard();
            int option = view.readIntInput("msg.input.select");

            switch (option) {
                case 1:
                    assetController.handleListAssets();
                    break;
                case 2:
                    // Concatenamos el mensaje traído del properties con el ID del usuario
                    view.showMessageByKey("msg.tip.yourId"); 
                    view.printText(currentInvestorId); // Imprime el ID justo debajo
                    investmentController.handleCreateInvestment();
                    break;
                case 3:
                    view.showMessageByKey("msg.tip.enterIdPortfolio");
                    view.printText("(" + currentInvestorId + ")");
                    investmentController.handleListInvestmentsByInvestor();
                    break;
                case 4:
                    view.showMessageByKey("msg.tip.enterIdReport");
                    view.printText("(" + currentInvestorId + ")");
                    portfolioController.handleInvestorEarningsReport();
                    break;
                case 0:
                    view.showMessageByKey("msg.success.logout");
                    logout = true;
                    break;
                default:
                    view.showMessageByKey("msg.error.invalid");
            }
        }
    }

    private static void runAdminMenu() {
        boolean back = false;
        while (!back) {
            view.showAdminMenu();
            int option = view.readIntInput("msg.input.select");

            switch (option) {
                case 1:
                    manageInvestorsSubMenu();
                    break;
                case 2:
                    manageAssetsSubMenu();
                    break;
                case 3:
                    investmentController.handleListAllInvestments();
                    break;
                case 4:
                    portfolioController.handleGlobalEarningsReport();
                    break;
                case 0:
                    view.showMessageByKey("msg.info.returning");
                    back = true;
                    break;
                default:
                    view.showMessageByKey("msg.error.invalid");
            }
        }
    }

    private static void manageInvestorsSubMenu() {
        view.printText(""); // Salto de línea estético
        view.showMessageByKey("menu.admin.investors.title");
        view.showMessageByKey("menu.admin.investors.options");
        
        int opt = view.readIntInput("msg.input.select");
        if (opt == 1) investorController.handleListInvestors();
        else if (opt == 2) investorController.handleUpdateInvestor();
        else if (opt == 3) investorController.handleDeleteInvestor();
    }

    private static void manageAssetsSubMenu() {
        view.printText(""); // Salto de línea estético
        view.showMessageByKey("menu.admin.assets.title");
        view.showMessageByKey("menu.admin.assets.options");
        
        int opt = view.readIntInput("msg.input.select");
        if (opt == 1) assetController.handleCreateAsset();
        else if (opt == 2) assetController.handleListAssets();
        else if (opt == 3) investmentController.handleUpdateAssetPriceAndRecalculate();
        else if (opt == 4) assetController.handleFindByPriceRange();
    }

    private static void changeLanguage() {
        view.printText(""); // Salto de línea
        view.showMessageByKey("menu.lang.options");
        
        int langOpt = view.readIntInput("menu.lang.select");
        if (langOpt == 1) {
            view.loadLanguage("es");
            view.showMessageByKey("msg.success.langChanged");
        } else if (langOpt == 2) {
            view.loadLanguage("en");
            view.showMessageByKey("msg.success.langChanged");
        }
    }
}