package atm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Account implements Serializable {
    private static final long serialVersionUID = 3L;
    final String accountNumber;
    private final String holderName;
    private String pin;
    double balance;
    private final List<Transaction> transactions = new ArrayList<>();

    public Account(String accountNumber, String holderName, String pin, double initialBalance) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.pin = pin;
        this.balance = Math.max(0.0, initialBalance);
        transactions.add(new Transaction("Account created", initialBalance, this.balance));
    }

    public String getAccountNumber() { return accountNumber; }
    public String getHolderName() { return holderName; }
    public boolean checkPin(String attempt) { return pin.equals(attempt); }

    public synchronized void setPin(String newPin) {
        this.pin = newPin;
        transactions.add(new Transaction("PIN changed", 0.0, balance));
    }

    public synchronized double getBalance() { return balance; }

    public synchronized boolean withdraw(double amount) {
        if (amount <= 0 || amount > balance) return false;
        balance -= amount;
        transactions.add(new Transaction("Withdraw", -amount, balance));
        return true;
    }

    public synchronized boolean deposit(double amount) {
        if (amount <= 0) return false;
        balance += amount;
        transactions.add(new Transaction("Deposit", amount, balance));
        return true;
    }

    public synchronized boolean transferTo(Account dest, double amount) {
        if (dest == null || amount <= 0 || amount > this.balance) return false;
        this.balance -= amount;
        dest.balance += amount;
        this.transactions.add(new Transaction("Transfer to " + dest.accountNumber, -amount, this.balance));
        dest.transactions.add(new Transaction("Transfer from " + this.accountNumber, amount, dest.balance));
        return true;
    }

    public synchronized List<Transaction> getRecentTransactions(int n) {
        int sz = transactions.size();
        int start = Math.max(0, sz - n);
        return new ArrayList<>(transactions.subList(start, sz));
    }
}
