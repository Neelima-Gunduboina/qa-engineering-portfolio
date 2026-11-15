package com.neelima.qa.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotUtil {
    private static final Path REPORTS = Paths.get("reports", "screenshots");

    public static String takeScreenshot(WebDriver driver, String name) {
        try {
            Files.createDirectories(REPORTS);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = name + "_" + timestamp + ".png";
            Path dst = REPORTS.resolve(filename);
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(src.toPath(), dst);
            return dst.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to capture screenshot", e);
        }
    }
}
