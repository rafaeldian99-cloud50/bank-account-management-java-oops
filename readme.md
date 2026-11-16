# Bank Account Management System (Java OOPs Mini Project)

A full-stack **Java Object-Oriented Programming (OOPs)** desktop application featuring a modular OOP backend (`BankAccount`, `SavingsAccount`, `CurrentAccount`, `BankManager`) and a professional **Java Swing Dashboard UI** (`BankingDashboardUI`) with **SHA-256 password authentication**.

## Key Features
- **Four Pillars of OOP:**
  - **Encapsulation:** Private fields (`balance`, `passwordHash`, `transactionHistory`) protected behind validated methods.
  - **Abstraction:** Abstract `BankAccount` class defining core banking contracts.
  - **Inheritance:** `SavingsAccount` and `CurrentAccount` subclasses extending `BankAccount`.
  - **Polymorphism:** Dynamic minimum-balance rules (`Rs. 1,000` for Savings, `Rs. 0` for Current) and annual interest calculation (`4%` vs `0%`).
- **Security & Authentication:**
  - SHA-256 password hashing via `java.security.MessageDigest`.
  - Role-based login for **Customers** and **System Admin**.
  - Password re-verification on customer withdrawals and live password rotation.
- **Java Swing Dashboard UI:**
  - Real-time KPI summary cards (Total Accounts, Total Bank Balance, Active Session, Transactions Logged).
  - Sidebar navigation for all 5 core banking operations + live Java OOP execution console.

## Team Members & Module Ownership
| Sl. No. | Name | Roll Number | Assigned Module |
| :--- | :--- | :--- | :--- |
| 1 | Dipan Das | 27600124136 | Core OOP Architecture, Auth & Operation 1: Create Account |
| 2 | Suman Pal | 27600124152 | Operation 2: Display Account Details & History |
| 3 | Debashis Roy | 27600124165 | Operation 3: Deposit Money & Audit Log |
| 4 | Swattik Malik | 27600124176 | Operation 4: Withdraw Money with Password & Min Balance Check |
| 5 | Aanshu Kumar Yadav | 27600124186 | Operation 5: Check Balance & Password Update |

## Demo Credentials
| Account / Username | Role | Type | Password |
| :--- | :--- | :--- | :--- |
| `SB1001` | Dipan Das | Savings | `dipan123` |
| `CA1002` | Suman Pal | Current | `suman123` |
| `SB1003` | Debashis Roy | Savings | `debashis123` |
| `SB1004` | Swattik Malik | Savings | `swattik123` |
| `CA1005` | Aanshu Kumar Yadav | Current | `aanshu123` |
| `admin` | Administrator | Admin Mode | `admin123` |

## How to Compile and Run Locally
Make sure you have **Java JDK 8 or higher** installed.

```bash
# 1. Compile all Java files
javac BankAccount.java BankManager.java BankingDashboardUI.java

# 2. Run the Java Swing Dashboard
java BankingDashboardUI
```

## How to Publish to GitHub
Create a new repository on GitHub (e.g., `bank-account-management-java-oops`), then run these commands in your project folder:

```bash
git init
git add BankAccount.java BankManager.java BankingDashboardUI.java README.md
git commit -m "Initial commit: Java OOP Bank Account Management System with Swing UI"
git branch -M main
git remote add origin https://github.com/<your-username>/bank-account-management-java-oops.git
git push -u origin main
```
