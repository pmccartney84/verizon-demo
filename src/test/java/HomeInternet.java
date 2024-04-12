import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.AbstractDriverOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.openqa.selenium.WebElement;

public class HomeInternet {

    private RemoteWebDriver driver;
    private String Status = "failed";

    @BeforeMethod
    public void setup(Method m, ITestContext ctx) throws MalformedURLException {
        String username = System.getenv("LT_USERNAME") == null ? "Your LT Username" : System.getenv("LT_USERNAME");
        String authkey = System.getenv("LT_ACCESS_KEY") == null ? "Your LT AccessKey" : System.getenv("LT_ACCESS_KEY");

        String hub = "@hub.lambdatest.com/wd/hub";


        AbstractDriverOptions<?> driverOptions = null;
        HashMap<String, Object> ltOptions = new HashMap<String, Object>();
        switch(ctx.getCurrentXmlTest().getParameter("targetBrowser")){
            case "Chrome":
                driverOptions = new ChromeOptions();
                driverOptions.setBrowserVersion("123.0");
                break;
            case "Safari":
                driverOptions = new SafariOptions();
//				driverOptions.setBrowserVersion("17.0");
                ltOptions.put("platform", "macOS Sonoma");
                ltOptions.put("browserVersion", "17.0");
                ltOptions.put("browserName", "Safari");
//				driverOptions.setPlatformName(targetOS);
                break;
            case "Edge":
                driverOptions = new EdgeOptions();
                break;
            case "Firefox":
                driverOptions = new FirefoxOptions();
//				driverOptions.setBrowserVersion("117.0");
                driverOptions.setAcceptInsecureCerts(true);
                break;
        }

        //Selenium Config
        ltOptions.put("selenium_version", "4.13.0");
        ltOptions.put("w3c", true);

        //Test Meta Data
        ltOptions.put("project", "Verizon");
        ltOptions.put("name", ctx.getName() + " - " + ctx.getCurrentXmlTest().getParameter("targetBrowser"));
        ltOptions.put("build", "Verizon HyEx");
        String[] customBuildTags = {"HyEx Build", "Verizon"};
        ltOptions.put("buildTags", customBuildTags);


        //Test Enhancements
        ltOptions.put("autoHeal", true);
        ltOptions.put("console", true);
        ltOptions.put("visual", true);
        ltOptions.put("network", true);
//		ltOptions.put("performance", true);


        ltOptions.put("terminal", true);
        driverOptions.setCapability("LT:Options", ltOptions);

        driver = new RemoteWebDriver(new URL("https://" + username + ":" + authkey + hub), driverOptions);
    }

    @Test
    public void basicTest() throws InterruptedException {
        String spanText;
        System.out.println("Loading Url");
        Thread.sleep(100);
        driver.get("https://www.verizon.com/");
        WebElement element = driver.findElement(By.xpath("//*[@id=\"vz-gh20\"]/div/div/div[1]/div/div[1]/div[2]/div/div[1]/div[2]/div[2]/div/div/div[2]/div/a[1]"));
        element.click();
        // Thread.sleep(3000);
        WebElement element1 = driver.findElement(By.xpath("//*[@id=\"streetaddress\"]"));
        element1.click();
        //Thread.sleep(5000);
        element1.sendKeys("1355 Windward Conc, Alpharetta, GA, 30005, USA");
        WebElement element2 = driver.findElement(By.xpath("//*[@id=\"selectedOption\"]/a"));
        element2.click();
        // Locate the textbox using the provided XPath
        WebElement apartmentNumberElement = driver.findElement(By.xpath("//*[@id='apartmentNumber']"));

// Type "326" into the textbox using the sendKeys() method
        apartmentNumberElement.sendKeys("326");
        WebElement apartmentNumberElement1 = driver.findElement(By.xpath("//*[@id=\"selectedOption\"]/a"));
        apartmentNumberElement1.click();
        WebElement element3 = driver.findElement(By.xpath("//*[@id=\"stickyBar-CTA\"]"));
        element3.click();





        System.out.println("TestFinished");

    }

    @AfterMethod(alwaysRun = true)
    void afterTest(ITestResult result){
        switch (result.getStatus()) {
            case ITestResult.SUCCESS:
                driver.executeScript("lambda-status=" + "passed");
                break;
            case ITestResult.FAILURE:
                Map<String, String> action = new HashMap();
                action.put("status", "failed");
                action.put("reason", result.getThrowable().getMessage());
                driver.executeScript("lambda-action", action);

                List<String> exceptionCapture = new ArrayList<String>();
                StringWriter sw = new StringWriter();
                PrintWriter printWriter = new PrintWriter(sw);
                result.getThrowable().printStackTrace(printWriter);
                String sStackTrace = sw.toString();
                exceptionCapture.add(sStackTrace);

                try{
                    System.out.println(result.getThrowable().getStackTrace().toString());
                    driver.executeScript("lambda-exceptions", exceptionCapture);
                }catch (Exception ex) { }

                break;
        }

        driver.quit();
    }

}