package com.neelima.qa.utils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * JDBC helpers for backend validation tests.
 * Uses an in-memory H2 database in Oracle compatibility mode so the SQL mirrors
 * what would be run against an Oracle core-banking schema (e.g. FLEXCUBE).
 */
public final class DBUtils {

    private static final String URL = ConfigLoader.get("db.url");
    private static final String USER = ConfigLoader.get("db.user", "sa");
    private static final String PASS = ConfigLoader.get("db.pass", "");

    private DBUtils() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    /** Drops and recreates the schema from db/schema.sql so every test class starts clean. */
    public static void resetSchema() {
        runScript("db/schema.sql");
    }

    public static BigDecimal getBalance(long accountId) {
        return queryForDecimal("SELECT balance FROM accounts WHERE account_id = ?", accountId);
    }

    public static BigDecimal totalBalance() {
        return queryForDecimal("SELECT SUM(balance) FROM accounts");
    }

    public static int countTransactions(long accountId) {
        return queryForDecimal("SELECT COUNT(*) FROM transactions WHERE from_acct = ? OR to_acct = ?",
                accountId, accountId).intValue();
    }

    /**
     * Debits one account and credits another inside a single DB transaction,
     * writing an audit row. Any failure (e.g. CHECK balance >= 0) rolls the whole thing back.
     */
    public static void applyTransfer(long fromAcct, long toAcct, BigDecimal amount, String description) {
        try (Connection c = getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement debit = c.prepareStatement(
                         "UPDATE accounts SET balance = balance - ? WHERE account_id = ?");
                 PreparedStatement credit = c.prepareStatement(
                         "UPDATE accounts SET balance = balance + ? WHERE account_id = ?");
                 PreparedStatement audit = c.prepareStatement(
                         "INSERT INTO transactions (from_acct, to_acct, amount, description) VALUES (?, ?, ?, ?)")) {

                debit.setBigDecimal(1, amount);
                debit.setLong(2, fromAcct);
                requireOneRow(debit.executeUpdate(), fromAcct);

                credit.setBigDecimal(1, amount);
                credit.setLong(2, toAcct);
                requireOneRow(credit.executeUpdate(), toAcct);

                audit.setLong(1, fromAcct);
                audit.setLong(2, toAcct);
                audit.setBigDecimal(3, amount);
                audit.setString(4, description);
                audit.executeUpdate();

                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new TransferFailedException(e);
        }
    }

    /** Thrown when the database rejects a transfer; the transfer has been rolled back. */
    public static class TransferFailedException extends RuntimeException {
        public TransferFailedException(Throwable cause) {
            super(cause.getMessage(), cause);
        }
    }

    private static void requireOneRow(int updated, long accountId) throws SQLException {
        if (updated != 1) {
            throw new SQLException("Account not found: " + accountId);
        }
    }

    private static BigDecimal queryForDecimal(String sql, Object... params) {
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next() || rs.getBigDecimal(1) == null) {
                    throw new IllegalStateException("No result for: " + sql);
                }
                return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void runScript(String resource) {
        try (InputStream in = DBUtils.class.getClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalStateException("SQL script not found: " + resource);
            }
            String script = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            try (Connection c = getConnection(); Statement s = c.createStatement()) {
                for (String statement : script.split(";")) {
                    String sql = statement.replaceAll("(?m)^--.*$", "").trim();
                    if (!sql.isEmpty()) {
                        s.execute(sql);
                    }
                }
            }
        } catch (IOException | SQLException e) {
            throw new IllegalStateException("Failed to run " + resource, e);
        }
    }
}
