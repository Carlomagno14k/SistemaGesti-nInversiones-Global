package co.edu.uptc.view;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import co.edu.uptc.view.ConsoleView;
//Axel Rincón
//arepa e queso 009
//el BARTO
public class Main {
    public static void main(String[] args) {
        // 1. Creamos la Vista (la consola)
        ConsoleView view = new ConsoleView();
        // 2. Creamos los Servicios (la lógica)
        // AssetService assetService = new AssetService();
        // PortfolioService portfolioService = new PortfolioService();
        
        // 3. Creamos el Controlador (el director de orquesta) pasándole la vista y los servicios
        // Controller controller = new Controller(view, assetService, portfolioService);
        
        // 4. ¡Arrancamos el programa!
        // controller.start(); 
        
        // (Nota: Por ahora, si solo quieres probar que tu menú sirve, puedes hacer esto:)
        view.showMainMenu();
        int opcion = view.readOption();
        System.out.println("Elegiste la opción: " + opcion);
    }

    }
