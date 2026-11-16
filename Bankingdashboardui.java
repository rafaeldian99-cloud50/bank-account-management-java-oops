import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Frontend Java Swing Dashboard UI: BankingDashboardUI
 * Connects directly to BankManager & BankAccount OOP backend.
 * Features secure login, role-based access, KPI cards, all 5 banking operations,
 * password change, and live Java OOP console log.
 */
public class BankingDashboardUI extends JFrame {
    // Design Palette
    private static final Color SIDEBAR_BG = new Color(15, 23, 42);
    private static final Color SIDEBAR_ACTIVE = new Color(30, 58, 138);
    private static final Color CANVAS_BG = new Color(248, 250, 252);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color PRIMARY_BLUE = new Color(30, 64, 175);
    private static final Color SUCCESS_GREEN = new Color(5, 150, 105);
    private static final Color DANGER_RED = new Color(220, 38, 38);
    private static final Color TEXT_DARK = new Color(15, 23, 42);
    private static final Color TEXT_MUTED = new Color(100, 116, 139);
    private static final Color BORDER_LIGHT = new Color(226, 232, 240);

    private final BankManager bankManager;
    private BankAccount loggedInCustomer = null;
    private boolean isAdminSession = false;

    private final CardLayout rootLayout = new CardLayout();
    private final JPanel rootPanel = new JPanel(rootLayout);

    // Dashboard Components
    private final CardLayout contentLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(contentLayout);
    private JLabel kpiAccountsVal, kpiBalanceVal, kpiSessionVal, kpiTxVal;
    private DefaultTableModel accountsTableModel;
    private JTextArea consoleArea;

    // Operation Panel Inputs
    private JTextField createAccNoField, createNameField, createBalField;
    private JComboBox<String> createTypeBox;
    private JPasswordField createPassField;

    private JTextField detailsAccField;
    private JTextArea detailsOutputArea;

    private JTextField depAccField, depAmtField;
    private JTextField witAccField, witAmtField;
    private JPasswordField witPassField;

    private JTextField balAccField;
    private JLabel balResultLabel;
    private JPasswordField oldPassField, newPassField;

    public BankingDashboardUI() {
        this.bankManager = new BankManager();
        setTitle("Bank Account Management System — Java OOPs Mini Project");
        setSize(1120, 720);
        setMinimumSize(new Dimension(980, 640));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        rootPanel.add(buildLoginScreen(), "LOGIN");
        rootPanel.add(buildDashboardScreen(), "DASHBOARD");
        add(rootPanel);

        rootLayout.show(rootPanel, "LOGIN");
    }

    private JPanel buildLoginScreen() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(SIDEBAR_BG);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER_LIGHT, 1, true),
                new EmptyBorder(32, 36, 32, 36)
        ));
        card.setPreferredSize(new Dimension(430, 460));

        JLabel badge = new JLabel("JAVA OOPs MINI PROJECT");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setForeground(PRIMARY_BLUE);
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Bank Account Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Sign in with Account No. or Admin credentials");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(TEXT_MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField userField = createStyledTextField("SB1001");
        JPasswordField passField = createStyledPasswordField("dipan123");

        JLabel hint = new JLabel("<html><font color='#64748b'>Demo Customer: <b>SB1001</b> / <b>dipan123</b> &nbsp;|&nbsp; Admin: <b>admin</b> / <b>admin123</b></font></html>");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton loginBtn = createStyledButton("Sign In to Dashboard", PRIMARY_BLUE);
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        loginBtn.addActionListener(e -> {
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword());
            if (bankManager.authenticateAdmin(user, pass)) {
                isAdminSession = true;
                loggedInCustomer = null;
                logConsole("[AUTH] Admin logged in successfully.");
                refreshDashboardData();
                rootLayout.show(rootPanel, "DASHBOARD");
            } else {
                BankAccount acc = bankManager.authenticateCustomer(user, pass);
                if (acc != null) {
                    isAdminSession = false;
                    loggedInCustomer = acc;
                    logConsole("[AUTH] Customer authenticated: " + acc.getAccountNumber() + " (" + acc.getCustomerName() + ")");
                    refreshDashboardData();
                    rootLayout.show(rootPanel, "DASHBOARD");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Invalid Account Number/Username or Password.",
                            "Authentication Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        card.add(badge);
        card.add(Box.createVerticalStrut(6));
        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(22));
        card.add(createFieldLabel("Account Number / Username"));
        card.add(Box.createVerticalStrut(6));
        card.add(userField);
        card.add(Box.createVerticalStrut(14));
        card.add(createFieldLabel("Password"));
        card.add(Box.createVerticalStrut(6));
        card.add(passField);
        card.add(Box.createVerticalStrut(12));
        card.add(hint);
        card.add(Box.createVerticalStrut(22));
        card.add(loginBtn);

        wrapper.add(card);
        return wrapper;
    }

    private JPanel buildDashboardScreen() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(CANVAS_BG);

        // Left Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(235, 0));
        sidebar.setBorder(new EmptyBorder(20, 14, 20, 14));

        JLabel brand = new JLabel("BAMS Banking UI");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 18));
        brand.setForeground(Color.WHITE);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subBrand = new JLabel("OOPS Java Edition");
        subBrand.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subBrand.setForeground(new Color(148, 163, 184));
        subBrand.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(brand);
        sidebar.add(subBrand);
        sidebar.add(Box.createVerticalStrut(24));

        sidebar.add(createNavButton("Dashboard Overview", "OVERVIEW"));
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(createNavButton("1. Create Account", "CREATE"));
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(createNavButton("2. Display Details", "DETAILS"));
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(createNavButton("3. Deposit Money", "DEPOSIT"));
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(createNavButton("4. Withdraw Money", "WITHDRAW"));
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(createNavButton("5. Balance & Security", "BALANCE"));

        sidebar.add(Box.createVerticalGlue());

        JButton logoutBtn = createStyledButton("Logout", DANGER_RED);
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        logoutBtn.addActionListener(e -> {
            loggedInCustomer = null;
            isAdminSession = false;
            rootLayout.show(rootPanel, "LOGIN");
        });
        sidebar.add(logoutBtn);

        // Right Main Area (Top KPIs + Center CardLayout + Bottom Console)
        JPanel rightArea = new JPanel(new BorderLayout(0, 14));
        rightArea.setBackground(CANVAS_BG);
        rightArea.setBorder(new EmptyBorder(16, 18, 16, 18));

        rightArea.add(buildKpiBar(), BorderLayout.NORTH);

        contentPanel.setBackground(CANVAS_BG);
        contentPanel.add(buildOverviewPanel(), "OVERVIEW");
        contentPanel.add(buildCreateAccountPanel(), "CREATE");
        contentPanel.add(buildDisplayDetailsPanel(), "DETAILS");
        contentPanel.add(buildDepositPanel(), "DEPOSIT");
        contentPanel.add(buildWithdrawPanel(), "WITHDRAW");
        contentPanel.add(buildBalanceSecurityPanel(), "BALANCE");
        rightArea.add(contentPanel, BorderLayout.CENTER);

        rightArea.add(buildConsolePanel(), BorderLayout.SOUTH);

        main.add(sidebar, BorderLayout.WEST);
        main.add(rightArea, BorderLayout.CENTER);
        return main;
    }

    private JPanel buildKpiBar() {
        JPanel grid = new JPanel(new GridLayout(1, 4, 12, 0));
        grid.setOpaque(false);

        kpiAccountsVal = new JLabel("5");
        kpiBalanceVal = new JLabel("Rs. 1,70,500.00");
        kpiSessionVal = new JLabel("Admin");
        kpiTxVal = new JLabel("5");

        grid.add(createKpiCard("TOTAL ACCOUNTS", kpiAccountsVal, PRIMARY_BLUE));
        grid.add(createKpiCard("TOTAL BANK BALANCE", kpiBalanceVal, SUCCESS_GREEN));
        grid.add(createKpiCard("ACTIVE SESSION", kpiSessionVal, TEXT_DARK));
        grid.add(createKpiCard("TRANSACTIONS LOGGED", kpiTxVal, new Color(124, 58, 237)));
        return grid;
    }

    private JPanel createKpiCard(String label, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER_LIGHT, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(TEXT_MUTED);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(accent);
        card.add(lbl);
        card.add(Box.createVerticalStrut(4));
        card.add(valueLabel);
        return card;
    }

    private JPanel buildOverviewPanel() {
        JPanel panel = createCardPanel("Customer Bank Accounts Directory");
        String[] cols = {"Account No", "Customer Name", "Account Type", "Balance (Rs.)", "Min Balance (Rs.)"};
        accountsTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(accountsTableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.setSelectionBackground(new Color(219, 234, 254));

        DefaultTableCellRenderer rightAlign = new DefaultTableCellRenderer();
        rightAlign.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(3).setCellRenderer(rightAlign);
        table.getColumnModel().getColumn(4).setCellRenderer(rightAlign);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildCreateAccountPanel() {
        JPanel panel = createCardPanel("Operation 1: Create New Bank Account (Dipan Das)");
        JPanel form = new JPanel(new GridLayout(6, 2, 12, 12));
        form.setOpaque(false);

        createAccNoField = createStyledTextField("SB1006");
        createNameField = createStyledTextField("");
        createTypeBox = new JComboBox<>(new String[]{"Savings", "Current"});
        createBalField = createStyledTextField("5000.00");
        createPassField = createStyledPasswordField("");

        form.add(createFieldLabel("Account Number (e.g. SB1006):")); form.add(createAccNoField);
        form.add(createFieldLabel("Customer Full Name:")); form.add(createNameField);
        form.add(createFieldLabel("Account Type:")); form.add(createTypeBox);
        form.add(createFieldLabel("Opening Balance (Rs.):")); form.add(createBalField);
        form.add(createFieldLabel("Account Password (min 4 chars):")); form.add(createPassField);

        JButton submitBtn = createStyledButton("Create Account", PRIMARY_BLUE);
        submitBtn.addActionListener(e -> {
            try {
                String accNo = createAccNoField.getText().trim();
                String name = createNameField.getText().trim();
                String type = (String) createTypeBox.getSelectedItem();
                double bal = Double.parseDouble(createBalField.getText().trim());
                String pass = new String(createPassField.getPassword());

                boolean ok = bankManager.createAccount(accNo, name, type, bal, pass);
                if (ok) {
                    logConsole("[SUCCESS] Created " + type + " Account " + accNo.toUpperCase() + " for " + name + " (Rs. " + String.format("%.2f", bal) + ")");
                    refreshDashboardData();
                    JOptionPane.showMessageDialog(this, "Account created successfully!");
                } else {
                    logConsole("[ERROR] Account creation failed (duplicate ID or Savings min balance < Rs. 1000).");
                    JOptionPane.showMessageDialog(this, "Creation failed. Check duplicate ID or Savings min balance (Rs. 1000).", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        form.add(new JLabel("")); form.add(submitBtn);
        panel.add(form, BorderLayout.NORTH);
        return panel;
    }

    private JPanel buildDisplayDetailsPanel() {
        JPanel panel = createCardPanel("Operation 2: Display Account Details & History (Suman Pal)");
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        top.setOpaque(false);
        detailsAccField = createStyledTextField("SB1001");
        detailsAccField.setPreferredSize(new Dimension(160, 32));
        JButton fetchBtn = createStyledButton("Fetch Account Details", PRIMARY_BLUE);

        detailsOutputArea = new JTextArea(11, 50);
        detailsOutputArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        detailsOutputArea.setEditable(false);
        detailsOutputArea.setBackground(new Color(248, 250, 252));

        fetchBtn.addActionListener(e -> {
            BankAccount acc = bankManager.getAccount(detailsAccField.getText().trim());
            if (acc == null) {
                detailsOutputArea.setText("[ERROR] Account not found.");
                return;
            }
            StringBuilder sb = new StringBuilder(acc.getFormattedDetails());
            sb.append("\n\nAnnual Interest Estimate: Rs. ").append(String.format("%.2f", acc.calculateAnnualInterest()));
            sb.append("\n\n--- Transaction History ---\n");
            for (BankAccount.TransactionRecord tx : acc.getTransactionHistory()) {
                sb.append(tx.toString()).append("\n");
            }
            detailsOutputArea.setText(sb.toString());
            logConsole("[INFO] Displayed details for Account " + acc.getAccountNumber());
        });

        top.add(createFieldLabel("Account No:"));
        top.add(detailsAccField);
        top.add(fetchBtn);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(detailsOutputArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildDepositPanel() {
        JPanel panel = createCardPanel("Operation 3: Deposit Money into Account (Debashis Roy)");
        JPanel form = new JPanel(new GridLayout(3, 2, 12, 12));
        form.setOpaque(false);

        depAccField = createStyledTextField("SB1003");
        depAmtField = createStyledTextField("6500.00");
        JButton depBtn = createStyledButton("Deposit Funds", SUCCESS_GREEN);

        depBtn.addActionListener(e -> {
            try {
                BankAccount acc = bankManager.getAccount(depAccField.getText().trim());
                double amt = Double.parseDouble(depAmtField.getText().trim());
                if (acc != null && acc.deposit(amt)) {
                    logConsole(String.format("[SUCCESS] Deposited Rs. %.2f into %s. New Balance: Rs. %.2f",
                            amt, acc.getAccountNumber(), acc.getBalance()));
                    refreshDashboardData();
                    JOptionPane.showMessageDialog(this, "Deposit successful! Updated Balance: Rs. " + String.format("%.2f", acc.getBalance()));
                } else {
                    logConsole("[ERROR] Deposit failed. Verify account number and positive amount.");
                    JOptionPane.showMessageDialog(this, "Deposit failed. Check account number and amount > 0.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid numeric amount.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        form.add(createFieldLabel("Account Number:")); form.add(depAccField);
        form.add(createFieldLabel("Deposit Amount (Rs.):")); form.add(depAmtField);
        form.add(new JLabel("")); form.add(depBtn);

        panel.add(form, BorderLayout.NORTH);
        return panel;
    }

    private JPanel buildWithdrawPanel() {
        JPanel panel = createCardPanel("Operation 4: Withdraw Money with Password Verification (Swattik Malik)");
        JPanel form = new JPanel(new GridLayout(4, 2, 12, 12));
        form.setOpaque(false);

        witAccField = createStyledTextField("SB1004");
        witAmtField = createStyledTextField("7000.00");
        witPassField = createStyledPasswordField("");
        JButton witBtn = createStyledButton("Verify & Withdraw", DANGER_RED);

        witBtn.addActionListener(e -> {
            try {
                BankAccount acc = bankManager.getAccount(witAccField.getText().trim());
                double amt = Double.parseDouble(witAmtField.getText().trim());
                String pass = new String(witPassField.getPassword());

                if (acc == null) {
                    JOptionPane.showMessageDialog(this, "Account not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!isAdminSession && !acc.verifyPassword(pass)) {
                    logConsole("[SECURITY] Withdrawal blocked on " + acc.getAccountNumber() + " due to invalid password.");
                    JOptionPane.showMessageDialog(this, "Incorrect account password!", "Security Alert", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (acc.withdraw(amt)) {
                    logConsole(String.format("[SUCCESS] Withdrew Rs. %.2f from %s. Remaining Balance: Rs. %.2f",
                            amt, acc.getAccountNumber(), acc.getBalance()));
                    refreshDashboardData();
                    JOptionPane.showMessageDialog(this, "Withdrawal successful! Remaining Balance: Rs. " + String.format("%.2f", acc.getBalance()));
                } else {
                    logConsole("[ERROR] Insufficient balance or minimum balance rule violated on " + acc.getAccountNumber());
                    JOptionPane.showMessageDialog(this, "Insufficient balance (Min Balance: Rs. " + acc.getMinimumBalance() + ").", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid numeric amount.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        form.add(createFieldLabel("Account Number:")); form.add(witAccField);
        form.add(createFieldLabel("Withdrawal Amount (Rs.):")); form.add(witAmtField);
        form.add(createFieldLabel("Account Password:")); form.add(witPassField);
        form.add(new JLabel("")); form.add(witBtn);

        panel.add(form, BorderLayout.NORTH);
        return panel;
    }

    private JPanel buildBalanceSecurityPanel() {
        JPanel panel = createCardPanel("Operation 5: Check Balance & Update Password (Aanshu Kumar Yadav)");
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setOpaque(false);

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        row1.setOpaque(false);
        balAccField = createStyledTextField("CA1005");
        balAccField.setPreferredSize(new Dimension(150, 32));
        JButton chkBtn = createStyledButton("Check Balance", PRIMARY_BLUE);
        balResultLabel = new JLabel("Enter account number to check balance.");
        balResultLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        balResultLabel.setForeground(SUCCESS_GREEN);

        chkBtn.addActionListener(e -> {
            BankAccount acc = bankManager.getAccount(balAccField.getText().trim());
            if (acc != null) {
                String msg = acc.getFormattedBalanceCheck();
                balResultLabel.setText(msg);
                logConsole("[BALANCE] " + msg);
            } else {
                balResultLabel.setText("[ERROR] Account not found.");
            }
        });

        row1.add(createFieldLabel("Account No:"));
        row1.add(balAccField);
        row1.add(chkBtn);

        JPanel passForm = new JPanel(new GridLayout(3, 2, 10, 10));
        passForm.setOpaque(false);
        passForm.setBorder(new TitledBorder("Change Account Password"));
        oldPassField = createStyledPasswordField("");
        newPassField = createStyledPasswordField("");
        JButton chgPassBtn = createStyledButton("Update Password", SIDEBAR_ACTIVE);

        chgPassBtn.addActionListener(e -> {
            BankAccount acc = bankManager.getAccount(balAccField.getText().trim());
            if (acc != null && acc.changePassword(new String(oldPassField.getPassword()), new String(newPassField.getPassword()))) {
                logConsole("[SECURITY] Password updated for Account " + acc.getAccountNumber());
                refreshDashboardData();
                JOptionPane.showMessageDialog(this, "Password updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Password update failed. Verify old password and min 4 chars.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        passForm.add(createFieldLabel("Current Password:")); passForm.add(oldPassField);
        passForm.add(createFieldLabel("New Password (min 4 chars):")); passForm.add(newPassField);
        passForm.add(new JLabel("")); passForm.add(chgPassBtn);

        box.add(row1);
        box.add(Box.createVerticalStrut(8));
        box.add(balResultLabel);
        box.add(Box.createVerticalStrut(18));
        box.add(passForm);

        panel.add(box, BorderLayout.NORTH);
        return panel;
    }

    private JPanel buildConsolePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBackground(new Color(15, 23, 42));
        panel.setBorder(new EmptyBorder(10, 12, 10, 12));
        panel.setPreferredSize(new Dimension(0, 145));

        JLabel title = new JLabel("LIVE JAVA OOP EXECUTION CONSOLE");
        title.setFont(new Font("Consolas", Font.BOLD, 11));
        title.setForeground(new Color(56, 189, 248));

        consoleArea = new JTextArea();
        consoleArea.setEditable(false);
        consoleArea.setBackground(new Color(15, 23, 42));
        consoleArea.setForeground(new Color(74, 222, 128));
        consoleArea.setFont(new Font("Consolas", Font.PLAIN, 12));

        panel.add(title, BorderLayout.NORTH);
        panel.add(new JScrollPane(consoleArea), BorderLayout.CENTER);
        return panel;
    }

    private void refreshDashboardData() {
        kpiAccountsVal.setText(String.valueOf(bankManager.getTotalAccounts()));
        kpiBalanceVal.setText(String.format("Rs. %,.2f", bankManager.getTotalBankBalance()));
        kpiSessionVal.setText(isAdminSession ? "Admin Mode" : (loggedInCustomer != null ? loggedInCustomer.getAccountNumber() : "-"));
        kpiTxVal.setText(String.valueOf(bankManager.getTotalTransactionsCount()));

        accountsTableModel.setRowCount(0);
        for (BankAccount acc : bankManager.getAllAccounts()) {
            if (!isAdminSession && loggedInCustomer != null && !acc.getAccountNumber().equals(loggedInCustomer.getAccountNumber())) {
                continue;
            }
            accountsTableModel.addRow(new Object[]{
                    acc.getAccountNumber(),
                    acc.getCustomerName(),
                    acc.getAccountType(),
                    String.format("%.2f", acc.getBalance()),
                    String.format("%.2f", acc.getMinimumBalance())
            });
        }
        if (loggedInCustomer != null) {
            detailsAccField.setText(loggedInCustomer.getAccountNumber());
            depAccField.setText(loggedInCustomer.getAccountNumber());
            witAccField.setText(loggedInCustomer.getAccountNumber());
            balAccField.setText(loggedInCustomer.getAccountNumber());
        }
    }

    private void logConsole(String message) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        if (consoleArea != null) {
            consoleArea.append("[" + time + "] " + message + "\n");
            consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
        }
    }

    private JButton createNavButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(30, 41, 59));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 14, 10, 14));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> contentLayout.show(contentPanel, cardName));
        return btn;
    }

    private JPanel createCardPanel(String heading) {
        JPanel card = new JPanel(new BorderLayout(0, 14));
        card.setBackground(CARD_BG);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER_LIGHT, 1, true),
                new EmptyBorder(18, 20, 18, 20)
        ));
        JLabel title = new JLabel(heading);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(TEXT_DARK);
        card.add(title, BorderLayout.NORTH);
        return card;
    }

    private JLabel createFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(TEXT_DARK);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField createStyledTextField(String initial) {
        JTextField tf = new JTextField(initial);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        return tf;
    }

    private JPasswordField createStyledPasswordField(String initial) {
        JPasswordField pf = new JPasswordField(initial);
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        pf.setAlignmentX(Component.LEFT_ALIGNMENT);
        return pf;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(170, 36));
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new BankingDashboardUI().setVisible(true);
        });
    }
}
