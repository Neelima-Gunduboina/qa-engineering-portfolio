package loginfunctionality;

	import org.openqa.selenium.By;
	import org.openqa.selenium.WebDriver;
	import org.openqa.selenium.WebElement;
	import org.openqa.selenium.chrome.ChromeDriver;

	public class negativetesterrormessage {

	    public static void main(String[] args) throws InterruptedException {

	        WebDriver driver = new ChromeDriver();

	        driver.manage().window().maximize();

	        driver.get("https://www.saucedemo.com/");

	        driver.findElement(By.id("user-name")).sendKeys("wrong_user");
	        driver.findElement(By.id("password")).sendKeys("wrong_password");
	        driver.findElement(By.id("login-button")).click();

	        Thread.sleep(2000);

	        String errorMessage =
	                driver.findElement(By.cssSelector("[data-test='error']")).getText();

	        System.out.println("Actual Error Message: " + errorMessage);

	        if (errorMessage.contains("Username and password do not match")) {
	            System.out.println("Negative Test Passed");
	        } else {
	            System.out.println("Negative Test Failed");
	        }

	      
	    }
	}