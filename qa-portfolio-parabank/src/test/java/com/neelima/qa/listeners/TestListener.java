package com.neelima.qa.listeners;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.neelima.qa.base.BaseTest;
import com.neelima.qa.utils.ExtentManager;
import com.neelima.qa.utils.ExtentTestManager;
import com.neelima.qa.utils.ScreenshotUtil;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG listener that integrates ExtentReports and captures screenshots on failure.
 */
public class TestListener implements ITestListener {
    @Override
    public void onStart(ITestContext context) {
        // initialize extent
        ExtentManager.getInstance();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String name = result.getMethod().getMethodName();
        ExtentTestManager.startTest(name);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTestManager.getTest().log(Status.PASS, "Test passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        Object instance = result.getInstance();
        if (instance instanceof BaseTest) {
            try {
                BaseTest bt = (BaseTest) instance;
                String path = ScreenshotUtil.takeScreenshot(bt.getDriver(), result.getMethod().getMethodName());
                ExtentTestManager.getTest().fail(result.getThrowable(), MediaEntityBuilder.createScreenCaptureFromPath(path).build());
                return;
            } catch (Exception e) {
                // fall through
            }
        }
        ExtentTestManager.getTest().fail(result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTestManager.getTest().log(Status.SKIP, "Test skipped");
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentTestManager.flush();
    }

    // other methods left with empty implementations
    @Override public void onTestFailedButWithinSuccessPercentage(ITestResult result) {}
    @Override public void onTestFailedWithTimeout(ITestResult result) { onTestFailure(result); }
}
