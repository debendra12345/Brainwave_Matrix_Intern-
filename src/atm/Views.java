package atm;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.Font;

public class Views {

    public static BorderPane rootLayout() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));
        return root;
    }

    public static VBox leftMenu() {
        VBox v = new VBox(12);
        v.setPrefWidth(220);
        v.setPadding(new Insets(12));
        v.getStyleClass().add("menu-box");
        Text title = new Text("JavaATM");
        title.setFont(Font.font("Arial", 20));
        HBox brand = new HBox(10, makeIcon(), title);
        brand.setAlignment(Pos.CENTER_LEFT);
        v.getChildren().add(brand);

        Button btnDash = new Button("Dashboard");
        btnDash.setId("btnDashboard");
        Button btnBalance = new Button("Balance");
        btnBalance.setId("btnBalance");
        Button btnWithdraw = new Button("Withdraw");
        btnWithdraw.setId("btnWithdraw");
        Button btnDeposit = new Button("Deposit");
        btnDeposit.setId("btnDeposit");
        Button btnTransfer = new Button("Transfer");
        btnTransfer.setId("btnTransfer");
        Button btnStatement = new Button("Mini-statement");
        btnStatement.setId("btnStatement");
        Button btnChangePin = new Button("Change PIN");
        btnChangePin.setId("btnChangePin");
        Button btnLogout = new Button("Logout");
        btnLogout.setId("btnLogout");

        v.getChildren().addAll(btnDash, btnBalance, btnWithdraw, btnDeposit, btnTransfer, btnStatement, btnChangePin, new Separator(), btnLogout);
        return v;
    }

    private static Node makeIcon() {
        Circle c = new Circle(18);
        c.setFill(Color.web("#2c3e50"));
        Text t = new Text("ATM");
        t.setFill(Color.WHITE);
        t.setFont(Font.font(12));
        StackPane s = new StackPane(c, t);
        return s;
    }

    public static VBox loginPane() {
        VBox v = new VBox(10);
        v.setPadding(new Insets(20));
        v.setAlignment(Pos.CENTER);
        v.getStyleClass().add("card");
        Label title = new Label("Login to your account");
        title.setStyle("-fx-font-size:16px; -fx-font-weight: bold;");
        TextField accField = new TextField();
        accField.setPromptText("Account number");
        PasswordField pinField = new PasswordField();
        pinField.setPromptText("PIN");
        Button loginBtn = new Button("Login");
        loginBtn.setDefaultButton(true);
        Button createBtn = new Button("Create new account (Admin)");
        v.getChildren().addAll(title, accField, pinField, loginBtn, createBtn);
        accField.setId("loginAcc");
        pinField.setId("loginPin");
        loginBtn.setId("loginBtn");
        createBtn.setId("adminCreateBtn");
        return v;
    }

    public static VBox dashboardPane(Account acc) {
        VBox v = new VBox(12);
        v.setPadding(new Insets(16));
        v.getStyleClass().add("card");
        Label header = new Label("Welcome, " + acc.getHolderName());
        header.setStyle("-fx-font-size:18px; -fx-font-weight:bold;");
        Label accLabel = new Label("Account: " + acc.getAccountNumber());
        Label balLabel = new Label(String.format("Balance: ₹ %.2f", acc.getBalance()));
        balLabel.setStyle("-fx-font-size:16px; -fx-font-weight:600;");
        v.getChildren().addAll(header, accLabel, balLabel);
        return v;
    }

    public static VBox formPane(String titleText) {
        VBox v = new VBox(8);
        v.setPadding(new Insets(12));
        v.getStyleClass().add("card");
        Label head = new Label(titleText);
        head.setStyle("-fx-font-size:14px; -fx-font-weight:bold;");
        v.getChildren().add(head);
        return v;
    }
}
