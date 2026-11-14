package com.neelima.qa.retry;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retries a failed test once.
 *
 * Used only on the long end-to-end UI flows. The public ParaBank demo is shared by many
 * users and keeps some server-side state across sessions (see DEF-002), so another visitor's
 * activity can occasionally change what a test sees mid-flow. A single retry separates that
 * environment noise from a real regression: a genuine defect fails both attempts.
 * Retried attempts are still visible in the TestNG and Extent reports.
 */
public class RetryOnce implements IRetryAnalyzer {

    private static final int MAX_RETRIES = 1;
    private int attempts = 0;

    @Override
    public boolean retry(ITestResult result) {
        return attempts++ < MAX_RETRIES;
    }
}
