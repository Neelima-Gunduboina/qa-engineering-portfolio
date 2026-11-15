package com.neelima.qa.tests.db;

import com.neelima.qa.listeners.TestListener;
import com.neelima.qa.utils.DBUtils;
import com.neelima.qa.utils.DBUtils.TransferFailedException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.math.BigDecimal;

/**
 * Backend (SQL) validation of transfer business rules: balances, audit trail,
 * atomic rollback and data-integrity constraints.
 * Each test starts from a fresh schema (src/test/resources/db/schema.sql).
 */
@Listeners(TestListener.class)
public class TransactionDbTest {

    private static final long CHECKING = 1001;
    private static final long SAVINGS = 1002;
    private static final long EMPTY = 1003;
    private static final long MISSING = 9999;

    @BeforeMethod(alwaysRun = true)
    public void freshSchema() {
        DBUtils.resetSchema();
    }

    @Test(groups = {"smoke", "regression", "db"}, description = "Transfer debits source and credits target")
    public void transferUpdatesBothBalances() {
        DBUtils.applyTransfer(CHECKING, SAVINGS, new BigDecimal("100.00"), "Rent");

        Assert.assertEquals(DBUtils.getBalance(CHECKING), new BigDecimal("900.00"));
        Assert.assertEquals(DBUtils.getBalance(SAVINGS), new BigDecimal("600.00"));
    }

    @Test(groups = {"regression", "db"}, description = "Total money in the bank is unchanged by a transfer")
    public void transferPreservesTotalBalance() {
        BigDecimal before = DBUtils.totalBalance();

        DBUtils.applyTransfer(CHECKING, EMPTY, new BigDecimal("250.75"), "Gift");

        Assert.assertEquals(DBUtils.totalBalance(), before);
    }

    @Test(groups = {"regression", "db"}, description = "Every transfer writes exactly one audit row")
    public void transferWritesAuditRow() {
        DBUtils.applyTransfer(CHECKING, SAVINGS, new BigDecimal("10.00"), "Audit check");

        Assert.assertEquals(DBUtils.countTransactions(CHECKING), 1);
        Assert.assertEquals(DBUtils.countTransactions(SAVINGS), 1);
    }

    @Test(groups = {"regression", "db", "negative"},
            description = "Insufficient funds: whole transfer rolls back, no partial update")
    public void insufficientFundsRollsBack() {
        Assert.assertThrows(TransferFailedException.class,
                () -> DBUtils.applyTransfer(EMPTY, CHECKING, new BigDecimal("1.00"), "Overdraw"));

        Assert.assertEquals(DBUtils.getBalance(EMPTY), new BigDecimal("0.00"));
        Assert.assertEquals(DBUtils.getBalance(CHECKING), new BigDecimal("1000.00"), "Credit leg must roll back");
        Assert.assertEquals(DBUtils.countTransactions(EMPTY), 0, "No audit row for a failed transfer");
    }

    @Test(groups = {"regression", "db", "negative"}, description = "Transfer to a missing account rolls back")
    public void unknownTargetAccountRollsBack() {
        Assert.assertThrows(TransferFailedException.class,
                () -> DBUtils.applyTransfer(CHECKING, MISSING, new BigDecimal("50.00"), "Ghost"));

        Assert.assertEquals(DBUtils.getBalance(CHECKING), new BigDecimal("1000.00"), "Debit leg must roll back");
    }

    @Test(groups = {"regression", "db", "negative"}, description = "Negative amounts are blocked at DB level")
    public void negativeAmountIsRejected() {
        Assert.assertThrows(TransferFailedException.class,
                () -> DBUtils.applyTransfer(CHECKING, SAVINGS, new BigDecimal("-5.00"), "Reverse"));

        Assert.assertEquals(DBUtils.getBalance(CHECKING), new BigDecimal("1000.00"));
        Assert.assertEquals(DBUtils.getBalance(SAVINGS), new BigDecimal("500.00"));
    }
}
