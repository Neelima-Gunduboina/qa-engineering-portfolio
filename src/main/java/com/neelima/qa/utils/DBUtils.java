package com.neelima.qa.utils;

import java.sql.*;

/**
 * Simple JDBC utilities for H2 in-memory DB used by DB tests.
 */
public class DBUtils {
    private static String url = ConfigLoader.get("db.url", "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
    private static String user = ConfigLoader.get("db.user", "sa");
    private static String pass = ConfigLoader.get("db.pass", "");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }

    public static void initSchema() {
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            s.execute("CREATE TABLE IF NOT EXISTS accounts (id INT PRIMARY KEY, owner VARCHAR(100), balance DECIMAL(15,2));");
            s.execute("CREATE TABLE IF NOT EXISTS transactions (id IDENTITY PRIMARY KEY, from_acct INT, to_acct INT, amount DECIMAL(15,2), description VARCHAR(255), created TIMESTAMP DEFAULT CURRENT_TIMESTAMP());");
            // seed sample accounts
            s.execute("MERGE INTO accounts (id, owner, balance) KEY(id) VALUES (1001, 'Auto Tester', 1000.00);");
            s.execute("MERGE INTO accounts (id, owner, balance) KEY(id) VALUES (1002, 'Auto Tester', 500.00);");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static double getBalance(int accountId) {
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("SELECT balance FROM accounts WHERE id = ?")) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
                throw new RuntimeException("Account not found: " + accountId);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void applyTransfer(int fromAcct, int toAcct, double amount, String desc) {
        try (Connection c = getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement withdraw = c.prepareStatement("UPDATE accounts SET balance = balance - ? WHERE id = ?");
                 PreparedStatement deposit = c.prepareStatement("UPDATE accounts SET balance = balance + ? WHERE id = ?");
                 PreparedStatement ins = c.prepareStatement("INSERT INTO transactions (from_acct,to_acct,amount,description) VALUES (?,?,?,?)")) {
                withdraw.setDouble(1, amount);
                withdraw.setInt(2, fromAcct);
                withdraw.executeUpdate();

                deposit.setDouble(1, amount);
                deposit.setInt(2, toAcct);
                deposit.executeUpdate();

                ins.setInt(1, fromAcct);
                ins.setInt(2, toAcct);
                ins.setDouble(3, amount);
                ins.setString(4, desc);
                ins.executeUpdate();

                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
