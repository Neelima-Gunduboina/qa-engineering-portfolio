package com.neelima.qa.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ExtentManager {
    private static ExtentReports extent;

    public synchronized static ExtentReports getInstance() {
        if (extent == null) {
            Path reports = Paths.get("reports");
            try {
                Files.createDirectories(reports);
            } catch (Exception ignored) {}
            String reportPath = reports.resolve("extent-report.html").toString();
            ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Environment", ConfigLoader.get("env", "qa"));
            extent.setSystemInfo("Base URL", ConfigLoader.get("base.url", ""));
        }
        return extent;
    }
}
