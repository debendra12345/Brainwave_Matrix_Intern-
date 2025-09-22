package atm;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class UIController {

    private final Bank bank;
    private final Stage stage;
    private final BorderPane root;
    private Account currentAccount;
    private final String dataFile;

    public UIController(Bank bank, Stage stage, String dataFile) {
        this.bank = bank;
        this.stage = stage;
        this.dataFile = dataFile;
        this.root = Views.rootLayout();
        buildInitialScene();
    }

    public Node getRoot() { return root; }

    private void buildInitialScene() {
        VBox left = Views.leftMenu();
        root.setLeft(left);

        VBox login = Views.loginPane();
        root.setCenter(login);

        TextField accField = (TextField) login.lookup("#loginAcc");
        PasswordField pinField = (PasswordField) login.lookup("#loginPin");
        Button loginBtn = (Button) login.lookup("#loginBtn");
        Button adminCreateBtn = (Button) login.lookup("#adminCreateBtn");

        loginBtn.setOnAction(e -> {
            String accNum = accField.getText().trim();
            String pin = pinField.getText().trim();
            if (accNum.isEmpty() || pin.isEmpty()) { alert("Please enter account number and PIN"); return; }
            Account a = bank.getAccount(accNum);
            if (a == null) { alert("Account not found."); return; }
            if (!a.checkPin(pin)) { alert("Incorrect PIN."); return; }
            this.currentAccount = a;
            showDashboard();
        });

        adminCreateBtn.setOnAction(e -> showAdminCreate());

        left.lookup("#btnDashboard").setOnMouseClicked(e -> showDashboard());
        left.lookup("#btnBalance").setOnMouseClicked(e -> showBalance());
        left.lookup("#btnWithdraw").setOnMouseClicked(e -> showWithdraw());
        left.lookup("#btnDeposit").setOnMouseClicked(e -> showDeposit());
        left.lookup("#btnTransfer").setOnMouseClicked(e -> showTransfer());
        left.lookup("#btnStatement").setOnMouseClicked(e -> showStatement());
        left.lookup("#btnChangePin").setOnMouseClicked(e -> showChangePin());
        left.lookup("#btnLogout").setOnMouseClicked(e -> doLogout());
    }

    private void showDashboard() {
        if (!ensureLoggedIn()) return;
        VBox dash = Views.dashboardPane(currentAccount);
        root.setCenter(dash);
    }

    private void showBalance() {
        if (!ensureLoggedIn()) return;
        VBox v = Views.formPane("Balance Enquiry");
        Label bal = new Label(String.format("Current balance: ₹ %.2f", currentAccount.getBalance()));
        bal.setStyle("-fx-font-size:18px; -fx-font-weight:600;");
        v.getChildren().add(bal);
        root.setCenter(v);
    }

    private void showWithdraw() {
        if (!ensureLoggedIn()) return;
        VBox v = Views.formPane("Withdraw");
        TextField amt = new TextField();
        amt.setPromptText("Amount to withdraw");
        Button doIt = new Button("Withdraw");
        v.getChildren().addAll(amt, doIt);
        doIt.setOnAction(e -> {
            try {
                double a = Double.parseDouble(amt.getText().trim());
                if (currentAccount.withdraw(a)) {
                    alert("Withdraw successful. New balance: ₹ " + String.format("%.2f", currentAccount.getBalance()));
                    bank.saveToFile(dataFile);
                    showBalance();
                } else alert("Withdraw failed (insufficient funds or invalid amount).");
            } catch (NumberFormatException ex) { alert("Enter a valid number."); }
        });
        root.setCenter(v);
    }

    private void showDeposit() {
        if (!ensureLoggedIn()) return;
        VBox v = Views.formPane("Deposit");
        TextField amt = new TextField();
        amt.setPromptText("Amount to deposit");
        Button doIt = new Button("Deposit");
        v.getChildren().addAll(amt, doIt);
        doIt.setOnAction(e -> {
            try {
                double a = Double.parseDouble(amt.getText().trim());
                if (currentAccount.deposit(a)) {
                    alert("Deposit successful. New balance: ₹ " + String.format("%.2f", currentAccount.getBalance()));
                    bank.saveToFile(dataFile);
                    showBalance();
                } else alert("Deposit failed (invalid amount).");
            } catch (NumberFormatException ex) { alert("Enter a valid number."); }
        });
        root.setCenter(v);
    }

    private void showTransfer() {
        if (!ensureLoggedIn()) return;
        VBox v = Views.formPane("Transfer to another account");
        TextField dest = new TextField();
        dest.setPromptText("Destination account number");
        TextField amt = new TextField();
        amt.setPromptText("Amount to transfer");
        Button doIt = new Button("Transfer");
        v.getChildren().addAll(dest, amt, doIt);
        doIt.setOnAction(e -> {
            try {
                String toAcc = dest.getText().trim();
                double a = Double.parseDouble(amt.getText().trim());
                Account d = bank.getAccount(toAcc);
                if (d == null) { alert("Destination account not found."); return; }
                if (currentAccount.transferTo(d, a)) {
                    alert("Transfer successful. New balance: ₹ " + String.format("%.2f", currentAccount.getBalance()));
                    bank.saveToFile(dataFile);
                    showBalance();
                } else alert("Transfer failed (insufficient funds or invalid amount).");
            } catch (NumberFormatException ex) { alert("Enter a valid number."); }
        });
        root.setCenter(v);
    }

    private void showStatement() {
        if (!ensureLoggedIn()) return;
        VBox v = Views.formPane("Mini-statement (last 10 transactions)");
        ListView<String> list = new ListView<>();
        List<Transaction> txs = currentAccount.getRecentTransactions(10);
        list.setItems(FXCollections.observableArrayList(
                txs.stream().map(Transaction::toDisplayString).toList()
        ));
        v.getChildren().add(list);
        root.setCenter(v);
    }

    private void showChangePin() {
        if (!ensureLoggedIn()) return;
        VBox v = Views.formPane("Change PIN");
        PasswordField cur = new PasswordField(); cur.setPromptText("Current PIN");
        PasswordField np = new PasswordField(); np.setPromptText("New PIN");
        Button doIt = new Button("Change PIN");
        doIt.setOnAction(e -> {
            if (!currentAccount.checkPin(cur.getText())) { alert("Current PIN incorrect."); return; }
            if (np.getText().trim().isEmpty()) { alert("Enter new PIN."); return; }
            currentAccount.setPin(np.getText().trim());
            bank.saveToFile(dataFile);
            alert("PIN changed.");
        });
        v.getChildren().addAll(cur, np, doIt);
        root.setCenter(v);
    }

    private void showAdminCreate() {
        VBox v = Views.formPane("Admin - Create Account");
        TextField acc = new TextField(); acc.setPromptText("Account number");
        TextField name = new TextField(); name.setPromptText("Holder name");
        PasswordField pin = new PasswordField(); pin.setPromptText("Initial PIN");
        TextField bal = new TextField(); bal.setPromptText("Initial balance");
        Button create = new Button("Create Account");
        create.setOnAction(e -> {
            try {
                String aNum = acc.getText().trim();
                if (aNum.isEmpty() || name.getText().trim().isEmpty() || pin.getText().trim().isEmpty()) { alert("Fill all fields."); return; }
                double b = Double.parseDouble(bal.getText().trim());
                if (bank.accountExists(aNum)) { alert("Account already exists."); return; }
                bank.createAccount(aNum, name.getText().trim(), pin.getText().trim(), b);
                bank.saveToFile(dataFile);
                alert("Account created.");
            } catch (NumberFormatException ex) { alert("Enter a valid balance."); }
        });
        v.getChildren().addAll(acc, name, pin, bal, create);
        root.setCenter(v);
    }

    private void doLogout() {
        currentAccount = null;
        root.setCenter(Views.loginPane());
        buildInitialScene();
    }

    private boolean ensureLoggedIn() {
        if (currentAccount == null) { alert("Please login first (use the login panel)."); return false; }
        return true;
    }

    private void alert(String text) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, text, ButtonType.OK);
        a.initOwner(stage);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
