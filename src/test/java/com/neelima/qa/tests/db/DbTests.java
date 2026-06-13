package com.neelima.qa.tests.db;

import com.neelima.qa.listeners.TestListener;
import com.neelima.qa.utils.DBUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners({TestListener.class})
public class DbTests {

    @BeforeClass
    public void setup() {
        DBUtils.initSchema();
    }

    @Test(groups = {"regression"})
    public void transferUpdatesBalances() {
        double beforeFrom = DBUtils.getBalance(1001);
        double beforeTo = DBUtils.getBalance(1002);
        DBUtils.applyTransfer(1001, 1002, 100.00, "Test transfer");
        double afterFrom = DBUtils.getBalance(1001);
        double afterTo = DBUtils.getBalance(1002);

        Assert.assertEquals(afterFrom, beforeFrom - 100.00, 0.01);
        Assert.assertEquals(afterTo, beforeTo + 100.00, 0.01);
    }
}
