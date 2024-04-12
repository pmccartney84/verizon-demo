
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.AbstractDriverOptions;
import org.openqa.selenium.remote.Browser;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.*;
import java.util.HashMap;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;


public class ExampleTest {

	private static final Logger log = LoggerFactory.getLogger(ExampleTest.class);

	RemoteWebDriver driver;
	public static String username = System.getenv("LT_USERNAME");
	public static String access_key = System.getenv("LT_ACCESS_KEY");
	public static Map<String, String> params;
	
	@BeforeTest
	void loadConfigs(ITestContext context){
		params = context.getCurrentXmlTest().getAllParameters();
	}

	enum BROWSERS {Chrome, Firefox, Safari, Edge};
	enum OS {Windows, Linux, macOS};

	void  createDriver(BROWSERS targetBrowser, OS targetOS) {
	}
	
	@BeforeTest
	void setEnvironment(){
	}

	@Test
	void test(ITestContext context) throws MalformedURLException, InterruptedException {
//		System.setProperty("webdriver.http.factory", "jdk-http-client");


		long start = System.currentTimeMillis();
		String targetOS = context.getCurrentXmlTest().getParameter("targetOS");
		String targetBrowser = context.getCurrentXmlTest().getParameter("targetBrowser");
		String delay = context.getCurrentXmlTest().getParameter("delay");
		int waitTime = Integer.parseInt(delay);
		String testName = context.getCurrentXmlTest().getName();



		System.out.println("TEST NAME: " + testName + System.lineSeparator()
			+ "\t Browser: " + targetBrowser + System.lineSeparator()
			+ "\t OS: " + targetOS + System.lineSeparator()
			+ "\t Test Wait Time: " + waitTime);

		
		AbstractDriverOptions<?> driverOptions = null;
		HashMap<String, Object> ltOptions = new HashMap<String, Object>();
		switch(targetBrowser){
			case "Chrome":
				driverOptions = new ChromeOptions();
				driverOptions.setBrowserVersion("120.0");
				driverOptions.setPlatformName(targetOS);
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
			
				break;
			case "Firefox":
				driverOptions = new FirefoxOptions();
//				driverOptions.setBrowserVersion("117.0");
				driverOptions.setPlatformName(targetOS );
				driverOptions.setAcceptInsecureCerts(true);
				break;
		}
		
		//Selenium Config
		ltOptions.put("selenium_version", "4.13.0");
		ltOptions.put("w3c", true);

		//Test Meta Data
		ltOptions.put("project", "Demo");
		ltOptions.put("name", context.getName() + params.get("targetBrowser"));
		ltOptions.put("build", "HyEx Demo 2");
		String[] customBuildTags = {"HyEx Build", "Demo"};
		ltOptions.put("buildTags", customBuildTags);


		//Test Enhancements
		ltOptions.put("autoHeal", true);
		ltOptions.put("console", true);
		ltOptions.put("visual", true);
		ltOptions.put("network", true);
//		ltOptions.put("performance", true);

		ltOptions.put("smartUI.project", "Demo HyEx");

		ltOptions.put("terminal", true);
		driverOptions.setCapability("LT:Options", ltOptions);

		//Remote Hub
		driver = new RemoteWebDriver(new URL("https://" + username + ":" + access_key + "@hub.lambdatest.com/wd/hub"), driverOptions);

		System.out.println("Driver Open Time: - " + driverOptions.getBrowserName() + " - " + (System.currentTimeMillis() - start));




		driver.get("https://the-internet.herokuapp.com/add_remove_elements/");

		String fullTestName = context.getName() + params.get("targetBrowser");
		((JavascriptExecutor) driver).executeScript("lambda-name=" + fullTestName);

//		driver.executeScript("lambdatest_executor: {\"action\": \"stepcontext\", \"arguments\": {\"data\": \"Create Data\", \"level\": \"info\"}}");
		for (int i = 0; i < 100; i++) {
			driver.findElement(By.cssSelector("#content > div > button")).click();
		}





        visualTest("Full of Elements");

//		String guid = java.util.UUID.randomUUID().toString().replace("-", "");
//        guid = guid.replace("-", "");
//		System.out.println("GUID: " + guid);
//		String modifyScript = "document.getElementById('password').id='" + guid + "'";
//		driver.executeScript(modifyScript);


//		driver.executeScript("lambdatest_executorx: {\"action\": \"stepcontext\", \"arguments\": {\"data\": \"Delete Data\", \"level\": \"info\"}}");
		List<WebElement> deleteButtons = driver.findElements(By.xpath("//*[text()='Delete']"));
		for (WebElement button: deleteButtons) {
			button.click();
		}

		visualTest("Elements Deleted");


		int longCounts = waitTime/60;
		int remainder = waitTime % 60;

		if(longCounts == 0){
			System.out.println("TEST NAME: " + testName + System.lineSeparator()
					+ "\t Browser: " + targetBrowser + System.lineSeparator()
					+ "\t OS: " + targetOS + System.lineSeparator()
					+ "\t ====> Waiting for: " + waitTime + " seconds ");

			Thread.sleep(waitTime * 1000);
		} else{
			for (int i = 0; i < longCounts; i++) {
				if(i == 0) {
					System.out.println("TEST NAME: " + testName + System.lineSeparator()
							+ "\t Browser: " + targetBrowser + System.lineSeparator()
							+ "\t OS: " + targetOS + System.lineSeparator()
							+ "\t ====> Waiting for: 60 seconds");

				} else {
					System.out.println("TEST NAME: " + testName + System.lineSeparator()
							+ "\t Browser: " + targetBrowser + System.lineSeparator()
							+ "\t OS: " + targetOS + System.lineSeparator()
							+ "\t ====> Waiting for: additional 60 seconds");
				}

				Thread.sleep(60000);

				//Keep session alive gracefully
//				driver.executeScript("lambdatest_executor: {\"action\": \"stepcontext\", \"arguments\": {\"data\": \"Session Keep Alive\", \"level\": \"info\"}}");
				driver.executeScript("document.getElementsByTagName(\'title\')[0]");
			}

			if(remainder > 0){
				System.out.println("TEST NAME: " + testName + System.lineSeparator()
						+ "\t Browser: " + targetBrowser + System.lineSeparator()
						+ "\t OS: " + targetOS + System.lineSeparator()
						+ "\t ====> Waiting for: additional + " + remainder + " seconds");
				Thread.sleep(remainder * 1000);
			}
		}

		long finish = System.currentTimeMillis();
		System.out.println("Total Test Time - " + driverOptions.getBrowserName() + " - " + (finish - start));
	}

	@AfterMethod
	void tearDown(ITestResult result) {

		switch (result.getStatus()) {
			case ITestResult.SUCCESS:
				((JavascriptExecutor) driver).executeScript("lambda-status=" + "passed");
				break;
			case ITestResult.FAILURE:
				((JavascriptExecutor) driver).executeScript("lambda-status=" + "failed");
				break;
		}

		driver.quit();


	}

	void visualTest(String screenName){
		Map<String, Object> config = new HashMap<>();
		config.put("screenshotName", screenName);
//		((JavascriptExecutor)driver).executeScript("smartui.takeScreenshot", config);
	}




}
