package atm;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 4L;
    private final String type;
    private final double amount;
    private final LocalDateTime timestamp;
    private final double balanceAfter;

    public Transaction(String type, double amount, double balanceAfter) {
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.balanceAfter = balanceAfter;
    }

    public String toDisplayString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String amt = String.format("%+.2f", amount);
        return String.format("%s | %-20s | %8s | Bal: %.2f", timestamp.format(fmt), type, amt, balanceAfter);
    }

    @Override
    public String toString() {
        return toDisplayString();
    }
}
