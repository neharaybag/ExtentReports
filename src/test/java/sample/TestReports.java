package sample;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

/**
 * @author neha.raybagkar
 *
 */


public class TestReports {

  private WebDriver driver;

  ExtentReports extent;
  ExtentTest logger;

  /**
   * This is before test methode
   */
  @BeforeTest
  public void before() {
    extent = new ExtentReports(System.getProperty("user.dir") + "\\extentReport.html", true);
    extent.loadConfig(new File(System.getProperty("user.dir") + "\\extent-config.xml"));
    ChromeOptions options = new ChromeOptions();
    options.addArguments("start-maximized");
    driver = new ChromeDriver(options);
  }

  @AfterMethod
  public void getResult(ITestResult result) throws IOException {

    String screenshotPath = null;
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

    if (result.getStatus() == ITestResult.FAILURE) {

      screenshotPath = capture(driver, timeStamp + ".png");
      logger.log(LogStatus.FAIL, "Test Case Failed is " + result.getName());
      logger.log(LogStatus.FAIL, "Test Case Failed is " + result.getThrowable());
      logger.log(LogStatus.FAIL, "Snapshot below: " + logger.addScreenCapture(screenshotPath));
    } else if (result.getStatus() == ITestResult.SKIP) {
      logger.log(LogStatus.SKIP, "Test Case Skipped is " + result.getName());
    } else if (result.getStatus() == ITestResult.SUCCESS) {
      screenshotPath = capture(driver, timeStamp + ".png");
      logger.log(LogStatus.INFO, "Snapshot below: " + logger.addScreenCapture(screenshotPath));
      logger.log(LogStatus.PASS, "Test case passed is " + result.getName());
    }
    // ending test
    extent.endTest(logger);

  }

  
  /**
   * This method captures screenshot
   * @param driver
   * @param screenShotName
   * @return screenhotpath
   * @throws IOException
   */
  public static String capture(WebDriver driver, String screenShotName) throws IOException {

    File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
    String dest = System.getProperty("user.dir") + "\\Screenshots\\" + screenShotName + ".png";
    TakesScreenshot ts = (TakesScreenshot) driver;
    FileUtils.copyFile(source, new File(dest));
    return dest;
  }

  /**
   * Sample failed test
   */
  @Test
  public void testcaseFailed() {
    driver.get("test.com");
    
    logger = extent.startTest("testcaseFailed"); // this is name of test (you can pass name or get from testng method
    Assert.fail();
  }

  /**
   * Sample passed test
   */
  @Test
  public void testcasePassed() {
    logger = extent.startTest("testcasePassed"); // this is name of test (you can pass name or get from testng method

  }

  @AfterTest
  public void after() {

    extent.flush();
    extent.close();


  }



}
