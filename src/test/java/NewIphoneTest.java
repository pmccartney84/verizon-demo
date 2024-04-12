import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class NewIphoneTest {

    private RemoteWebDriver driver;
    private String Status = "failed";

    @BeforeMethod
    public void setup(Method m, ITestContext ctx) throws MalformedURLException {
        String username = System.getenv("LT_USERNAME") == null ? "Your LT Username" : System.getenv("LT_USERNAME");
        String authkey = System.getenv("LT_ACCESS_KEY") == null ? "Your LT AccessKey" : System.getenv("LT_ACCESS_KEY");

        
        /*
        Steps to run Smart UI project (https://beta-smartui.lambdatest.com/)
        Step - 1 : Change the hub URL to @beta-smartui-hub.lambdatest.com/wd/hub
        Step - 2 : Add "smartUI.project": "<Project Name>" as a capability above
        Step - 3 : Add "((JavascriptExecutor) driver).executeScript("smartui.takeScreenshot");" code wherever you need to take a screenshot
        Note: for additional capabilities navigate to https://www.lambdatest.com/support/docs/test-settings-options/
        */

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

        driver.get("https://verizon.com"); // Replace with the URL of the webpage you want to test

        // Thread.sleep(3000);
        // Locate the first element by its class and click on it
        clickElement(By.xpath("//*[@id=\"vz-gh20\"]/div/div/div[1]/div/div[1]/div[2]/div/div[1]/div[2]/div[2]/div/div/div[4]/div/a[1]"));

        clickElement(By.xpath("//*[@id='section_copy']/div/div[1]/nav/div/div/ul/li[3]/a"));
        clickElement(By.xpath("//*[@id=\"section\"]/div/div[1]/div/div/div/div[1]/div[2]/a"));
        clickElement(By.xpath("//*[@id='color-swatch-group']/div/div/label[3]/span"));
        clickElement(By.xpath("//span[contains(text(), '1 TB')]"));
        clickElement(By.xpath("//span[contains(text(), 'New customer')]"));
        clickElement(By.xpath("//p[contains(text(), '18.88')]"));
        clickElement(By.cssSelector("#cta-btn > div:nth-child(1) > button"));
        inputText(By.xpath("//input[contains(@data-testid, 'zipInput')]"), "16701");
        clickElement(By.xpath("//button[contains(@data-testid, 'zipConfirm')]"));
        clickElement(By.xpath("//*[@id=\"goToCartCTA\"]"));



        System.out.println("TestFinished");

    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
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

    void clickElement(By by){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.elementToBeClickable(by)).click();
    }

    void inputText(By by, String text){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.elementToBeClickable(by)).sendKeys(text);
    }

}