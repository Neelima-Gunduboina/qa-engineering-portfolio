package loginfunctionality;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;


public class Googletest1 {

    public static void main(String[] args) throws InterruptedException {

       

        WebDriver driver = new ChromeDriver();

       

        driver.get("https://www.google.com");

        Thread.sleep(7000);

        System.out.println("URL is: " + driver.getCurrentUrl());
        System.out.println("Title is: " + driver.getTitle());
    }
}