package loginfunctionality;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class explicitwait {

    public static void main(String[] args) throws InterruptedException {

        WebDriver driver = new ChromeDriver();

        driver.get("https://www.saucedemo.com/");
        driver.manage().window().maximize();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement username = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("user-name")));
        username.sendKeys("standard_user");

        WebElement password = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("password")));
        password.sendKeys("secret_sauce");

        WebElement loginButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("login-button")));
        loginButton.click();

        String currentUrl = driver.getCurrentUrl();

        if (currentUrl.contains("inventory")) {
            System.out.println("Login successful");
        } else {
            System.out.println("Login failed");
        }

       
    }
}