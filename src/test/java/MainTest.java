import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class MainTest {

    protected AndroidDriver driver = null;
    protected AppiumDriverLocalService appiumService = null;
    protected String appiumServiceUrl;

    @Before
    public void setUp() throws Exception {
        appiumService = AppiumDriverLocalService.buildService(new AppiumServiceBuilder()
                .usingDriverExecutable(getNodeExecutableFile())
                .withAppiumJS(getAppiumJSFileLocation())
                .withIPAddress("127.0.0.1")
                .usingAnyFreePort()
                .withStartUpTimeOut(30, TimeUnit.SECONDS));

        appiumService.start();
        appiumServiceUrl = appiumService.getUrl().toString();

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("platformVersion", "6.0");
        capabilities.setCapability("deviceName", "Nexus 5X API 23");
        capabilities.setCapability("app", "/Users/jason.dobo/dev/AndroidExample/app/build/outputs/apk/app-debug.apk");
        capabilities.setCapability("appActivity", "com.nbkuk.example.androidexample.MainActivity");

        // AppiumDriver is now an abstract class, use IOSDriver and AndroidDriver which both extend it
        driver = new AndroidDriver(new URL(appiumServiceUrl), capabilities);
        driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testLaunchOne() {
        MobileElement element = (MobileElement) driver.findElement(MobileBy.id("com.nbkuk.example.androidexample:id/toolbar"));
        Assert.assertNotNull(element);
    }

    @Test
    public void testLaunchTwo() {
        MobileElement element = (MobileElement) driver.findElement(MobileBy.id("com.nbkuk.example.androidexample:id/fab"));
        Assert.assertNotNull(element);
    }

    @Test
    public void testLaunchThree() {
        WebElement element = fluentWait(10).until(ExpectedConditions.visibilityOfElementLocated(MobileBy.id("com.nbkuk.example.androidexample:id/fab")));
        Assert.assertNotNull(element);
    }

    private File getNodeExecutableFile() {
        return new File(String.valueOf("/usr/local/bin/node"));
    }

    private File getAppiumJSFileLocation() {
        String appiumJSPath = "/usr/local/lib/node_modules/appium/build/lib/main.js";

        System.out.println("appiumJSPath: " + appiumJSPath);
        return new File(String.valueOf(appiumJSPath));
    }

    private Wait<AndroidDriver> fluentWait(long timeout) {
        return new FluentWait<>(driver)
                .withTimeout(timeout, TimeUnit.SECONDS)
                .pollingEvery(500, TimeUnit.MILLISECONDS)
                .ignoring(StaleElementReferenceException.class)
                .ignoring(NoSuchElementException.class);
    }
}