import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Backend Service Controller: BankManager
 * Manages account registry, authentication, and all 5 core banking operations.
 */
public class BankManager {
    private final Map<String, BankAccount> accounts = new LinkedHashMap<>();
    private final String adminUsername = "admin";
    private final String adminPasswordHash = BankAccount.hashPassword("admin123");

    public BankManager() {
        seedDemoAccounts();
    }

    private void seedDemoAccounts() {
        createAccount("SB1001", "Dipan Das", "Savings", 25000.00, "dipan123");
        createAccount("CA1002", "Suman Pal", "Current", 50000.00, "suman123");
        createAccount("SB1003", "Debashis Roy", "Savings", 18500.00, "debashis123");
        createAccount("SB1004", "Swattik Malik", "Savings", 32000.00, "swattik123");
        createAccount("CA1005", "Aanshu Kumar Yadav", "Current", 45000.00, "aanshu123");
    }

    // Operation 1: Create a new bank account (Dipan Das)
    public synchronized boolean createAccount(String accountNumber, String customerName,
                                              String accountType, double initialBalance, String password) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            return false;
        }
        String key = accountNumber.trim().toUpperCase();
        if (accounts.containsKey(key)) {
            return false;
        }
        if ("Savings".equalsIgnoreCase(accountType) && initialBalance < 1000.00) {
            return false;
        }
        BankAccount newAccount;
        if ("Current".equalsIgnoreCase(accountType)) {
            newAccount = new CurrentAccount(key, customerName, initialBalance, password);
        } else {
            newAccount = new SavingsAccount(key, customerName, initialBalance, password);
        }
        accounts.put(key, newAccount);
        return true;
    }

    // Backend Authentication (Customer or Admin)
    public boolean authenticateAdmin(String username, String password) {
        if (username == null || password == null) return false;
        return adminUsername.equalsIgnoreCase(username.trim())
                && adminPasswordHash.equals(BankAccount.hashPassword(password));
    }

    public BankAccount authenticateCustomer(String accountNumber, String password) {
        if (accountNumber == null || password == null) return null;
        BankAccount acc = accounts.get(accountNumber.trim().toUpperCase());
        if (acc != null && acc.verifyPassword(password)) {
            return acc;
        }
        return null;
    }

    public BankAccount getAccount(String accountNumber) {
        if (accountNumber == null) return null;
        return accounts.get(accountNumber.trim().toUpperCase());
    }

    public Collection<BankAccount> getAllAccounts() {
        return accounts.values();
    }

    public int getTotalAccounts() {
        return accounts.size();
    }

    public double getTotalBankBalance() {
        double sum = 0.0;
        for (BankAccount acc : accounts.values()) {
            sum += acc.getBalance();
        }
        return sum;
    }

    public int getTotalTransactionsCount() {
        int count = 0;
        for (BankAccount acc : accounts.values()) {
            count += acc.getTransactionHistory().size();
        }
        return count;
    }
}