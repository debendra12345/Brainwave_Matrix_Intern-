package atm;

import java.io.*;
import java.util.*;

public class Bank implements Serializable {
    private static final long serialVersionUID = 2L;
    private Map<String, Account> accounts = new HashMap<>();

    public synchronized Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }

    public synchronized boolean accountExists(String number) {
        return accounts.containsKey(number);
    }

    public synchronized Account createAccount(String number, String holder, String pin, double initial) {
        if (accounts.containsKey(number)) return null;
        Account a = new Account(number, holder, pin, initial);
        accounts.put(number, a);
        return a;
    }

    public synchronized Collection<Account> allAccounts() {
        return accounts.values();
    }

    public synchronized void saveToFile(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(this);
        } catch (IOException e) {
            System.err.println("Failed to save bank data: " + e.getMessage());
        }
    }

    public static Bank loadFromFile(String filename) {
        File f = new File(filename);
        if (!f.exists()) return null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return (Bank) ois.readObject();
        } catch (Exception e) {
            System.err.println("Load failed: " + e.getMessage());
            return null;
        }
    }
}
