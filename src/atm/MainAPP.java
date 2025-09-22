package atm;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.File;

public class MainApp extends Application {
    public static final String DATA_DIR = "data";
    public static final String DATA_FILE = DATA_DIR + File.separator + "accounts.dat";

    private Bank bank;
    private UIController controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        new File(DATA_DIR).mkdirs();

        bank = Bank.loadFromFile(DATA_FILE);
        if (bank == null) {
            bank = new Bank();
            bank.createAccount("1001", "Alice", "1234", 5000.00);
            bank.createAccount("1002", "Bob", "4321", 2500.50);
            bank.createAccount("1003", "Charlie", "0000", 100.00);
            bank.saveToFile(DATA_FILE);
            System.out.println("Created sample accounts: 1001/1234, 1002/4321, 1003/0000");
        }

        controller = new UIController(bank, primaryStage, DATA_FILE);
        Scene scene = new Scene(controller.getRoot(), 900, 600);
        // load css from resources
        try {
            scene.getStylesheets().add(getClass().getResource("/atm.css").toExternalForm());
        } catch (Exception ignored) {}
        primaryStage.setTitle("JavaATM — JavaFX ATM Interface");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(520);
        try {
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/atm_icon.png")));
        } catch (Exception ignored) {}
        primaryStage.show();
    }

    @Override
    public void stop() {
        bank.saveToFile(DATA_FILE);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
