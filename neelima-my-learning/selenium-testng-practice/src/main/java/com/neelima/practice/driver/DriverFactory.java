package com.neelima.practice.driver;

import com.neelima.practice.config.Config;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * Creates WebDriver instances. Selenium Manager (built into Selenium 4.6+)
 * downloads the matching driver binary automatically, so no manual chromedriver setup is needed.
 */
public final class DriverFactory {

    private DriverFactory() {
    }

    public static WebDriver create() {
        String browser = Config.get("browser").toLowerCase();
        boolean headless = Config.getBoolean("headless");

        switch (browser) {
            case "chrome": {
                ChromeOptions options = new ChromeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }
                options.addArguments("--window-size=1920,1080", "--disable-search-engine-choice-screen");
                return new ChromeDriver(options);
            }
            case "firefox": {
                FirefoxOptions options = new FirefoxOptions();
                if (headless) {
                    options.addArguments("-headless");
                }
                return new FirefoxDriver(options);
            }
            case "edge": {
                EdgeOptions options = new EdgeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }
                return new EdgeDriver(options);
            }
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
    }
}
