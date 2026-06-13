package loginfunctionality;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class LoginUsingCSSSelector {

    public static void main(String[] args) throws InterruptedException {

        WebDriver driver = new ChromeDriver();

        driver.manage().window().maximize();

        driver.get("https://practicetestautomation.com/practice-test-login/");

        driver.findElement(By.cssSelector("input#username")).sendKeys("student");

        driver.findElement(By.cssSelector("input#password")).sendKeys("Password123");

        driver.findElement(By.cssSelector("button#submit")).click();

        Thread.sleep(3000);

        String title = driver.getTitle();

        System.out.println("Page Title: " + title);

        
    }
}
