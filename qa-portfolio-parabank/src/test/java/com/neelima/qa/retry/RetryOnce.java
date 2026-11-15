package com.neelima.qa.retry;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retries a failed test once.
 *
 * Used only on the long end-to-end UI flows. The public ParaBank demo is shared by many
 * visitors and is reset without notice, so a request can occasionally fail or see stale data
 * for reasons unrelated to the code under test. A single retry separates that
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
