package loginfunctionality;

	import org.openqa.selenium.By;
	import org.openqa.selenium.WebDriver;
	import org.openqa.selenium.WebElement;
	import org.openqa.selenium.chrome.ChromeDriver;
	import org.openqa.selenium.support.ui.Select;

	public class dropdownexample {
	    public static void main(String[] args) {

	        WebDriver driver = new ChromeDriver();

	        driver.get("https://www.saucedemo.com");

	        driver.findElement(By.id("user-name")).sendKeys("standard_user");
	        driver.findElement(By.id("password")).sendKeys("secret_sauce");
	        driver.findElement(By.id("login-button")).click();

	        WebElement dropdown = driver.findElement(By.className("product_sort_container"));

	        Select select = new Select(dropdown);
	        select.selectByVisibleText("Price (low to high)");

	        System.out.println("Dropdown selected");

	    }
	}