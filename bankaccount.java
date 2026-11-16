import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Core OOP Backend Model: BankAccount (Abstract Base Class)
 * Demonstrates Encapsulation, Abstraction, Inheritance, and Polymorphism.
 * Core Class Design by: Dipan Das (Roll No: 27600124136)
 */
public abstract class BankAccount {
    // Encapsulated (private) attributes
    private final String accountNumber;
    private String customerName;
    private final String accountType;
    protected double balance;
    private String passwordHash;
    private final List<TransactionRecord> transactionHistory;

    public static class TransactionRecord {
        private final String timestamp;
        private final String type;
        private final double amount;
        private final double balanceAfter;
        private final String note;

        public TransactionRecord(String type, double amount, double balanceAfter, String note) {
            this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            this.type = type;
            this.amount = amount;
            this.balanceAfter = balanceAfter;
            this.note = note;
        }

        public String getTimestamp() { return timestamp; }
        public String getType() { return type; }
        public double getAmount() { return amount; }
        public double getBalanceAfter() { return balanceAfter; }
        public String getNote() { return note; }

        @Override
        public String toString() {
            return String.format("[%s] %-10s | Rs. %10.2f | Bal: Rs. %10.2f | %s",
                    timestamp, type, amount, balanceAfter, note);
        }
    }

    public BankAccount(String accountNumber, String customerName, String accountType,
                       double initialBalance, String rawPassword) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be empty.");
        }
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty.");
        }
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Opening balance cannot be negative.");
        }
        if (rawPassword == null || rawPassword.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters.");
        }

        this.accountNumber = accountNumber.trim().toUpperCase();
        this.customerName = customerName.trim();
        this.accountType = accountType;
        this.balance = initialBalance;
        this.passwordHash = hashPassword(rawPassword);
        this.transactionHistory = new ArrayList<>();
        this.transactionHistory.add(new TransactionRecord("OPEN", initialBalance, initialBalance, "Account opened"));
    }

    // Secure SHA-256 Password Hashing
    public static String hashPassword(String rawPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] encoded = md.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : encoded) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    public boolean verifyPassword(String rawPassword) {
        if (rawPassword == null) return false;
        return this.passwordHash.equals(hashPassword(rawPassword));
    }

    public boolean changePassword(String oldPassword, String newPassword) {
        if (!verifyPassword(oldPassword)) {
            return false;
        }
        if (newPassword == null || newPassword.length() < 4) {
            return false;
        }
        this.passwordHash = hashPassword(newPassword);
        this.transactionHistory.add(new TransactionRecord("SECURITY", 0.0, this.balance, "Password updated"));
        return true;
    }

    // Getters
    public String getAccountNumber() { return accountNumber; }
    public String getCustomerName() { return customerName; }
    public String getAccountType() { return accountType; }
    public double getBalance() { return balance; }
    public List<TransactionRecord> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory);
    }

    public void setCustomerName(String customerName) {
        if (customerName != null && !customerName.trim().isEmpty()) {
            this.customerName = customerName.trim();
        }
    }

    // Operation 2: Display account details (Suman Pal)
    public String getFormattedDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("+--------------------------------------------------+\n");
        sb.append(String.format("| %-20s : %-25s |\n", "Account Number", accountNumber));
        sb.append(String.format("| %-20s : %-25s |\n", "Customer Name", customerName));
        sb.append(String.format("| %-20s : %-25s |\n", "Account Type", accountType));
        sb.append(String.format("| %-20s : Rs. %-21.2f |\n", "Current Balance", balance));
        sb.append(String.format("| %-20s : Rs. %-21.2f |\n", "Min Balance Rule", getMinimumBalance()));
        sb.append("+--------------------------------------------------+");
        return sb.toString();
    }

    // Operation 3: Deposit money into an account (Debashis Roy)
    public synchronized boolean deposit(double amount) {
        if (amount <= 0) {
            return false;
        }
        balance += amount;
        transactionHistory.add(new TransactionRecord("DEPOSIT", amount, balance, "Cash/Online Deposit"));
        return true;
    }

    // Operation 4: Withdraw money from an account (Swattik Malik)
    public synchronized boolean withdraw(double amount) {
        if (amount <= 0) {
            return false;
        }
        if ((balance - amount) < getMinimumBalance()) {
            return false;
        }
        balance -= amount;
        transactionHistory.add(new TransactionRecord("WITHDRAW", amount, balance, "Account Withdrawal"));
        return true;
    }

    // Operation 5: Check current account balance (Aanshu Kumar Yadav)
    public String getFormattedBalanceCheck() {
        return String.format("Account No: %s | Holder: %s | Available Balance: Rs. %.2f",
                accountNumber, customerName, balance);
    }

    // Abstract methods demonstrating Abstraction & Polymorphism
    public abstract double getMinimumBalance();
    public abstract double calculateAnnualInterest();
}

/**
 * SavingsAccount subclass (Inheritance + Polymorphism)
 * Enforces Rs. 1,000 minimum balance and 4.0% annual interest.
 */
class SavingsAccount extends BankAccount {
    private static final double MIN_BALANCE = 1000.00;
    private static final double INTEREST_RATE = 0.04;

    public SavingsAccount(String accountNumber, String customerName, double initialBalance, String rawPassword) {
        super(accountNumber, customerName, "Savings", initialBalance, rawPassword);
    }

    @Override
    public double getMinimumBalance() {
        return MIN_BALANCE;
    }

    @Override
    public double calculateAnnualInterest() {
        return balance * INTEREST_RATE;
    }
}

/**
 * CurrentAccount subclass (Inheritance + Polymorphism)
 * Allows Rs. 0.00 minimum balance (zero-balance business account) and 0% interest.
 */
class CurrentAccount extends BankAccount {
    private static final double MIN_BALANCE = 0.00;

    public CurrentAccount(String accountNumber, String customerName, double initialBalance, String rawPassword) {
        super(accountNumber, customerName, "Current", initialBalance, rawPassword);
    }

    @Override
    public double getMinimumBalance() {
        return MIN_BALANCE;
    }

    @Override
    public double calculateAnnualInterest() {
        return 0.0;
    }
}