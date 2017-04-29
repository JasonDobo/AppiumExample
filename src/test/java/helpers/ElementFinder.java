package helpers;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.util.concurrent.TimeUnit;

public class ElementFinder {

    private int DEFAULT_TIMEOUT = 5;
    private AppiumDriver appiumDriver;

    public ElementFinder(AppiumDriver driver) {
        this.appiumDriver = driver;
    }

    private Wait<AppiumDriver> fluentWait(long timeout) {
        return new FluentWait<>(appiumDriver)
                .withTimeout(timeout, TimeUnit.SECONDS)
                .pollingEvery(500, TimeUnit.MILLISECONDS)
                .ignoring(StaleElementReferenceException.class)
                .ignoring(NoSuchElementException.class);
    }

    private MobileElement waitForVisibilityOfElement(By foundBy, int seconds) {
        MobileElement element;
        ExpectedCondition<WebElement> elementExpectedCondition  = ExpectedConditions.visibilityOfElementLocated(foundBy);

        try {
            element = (MobileElement) fluentWait(seconds).until(elementExpectedCondition);
        } catch (TimeoutException e) {
            System.out.println("No visible element found with " + foundBy.toString());
            throw new NoSuchElementException(foundBy.toString());
        }

        return element;
    }

    private MobileElement tryWaitForVisibilityOfElement(By foundBy, int seconds) {
        MobileElement element = null;
        ExpectedCondition<WebElement> elementExpectedCondition  = ExpectedConditions.visibilityOfElementLocated(foundBy);

        try {
            element = (MobileElement) fluentWait(seconds).until(elementExpectedCondition);
        } catch (TimeoutException e) {
            System.out.println("No visible element found with " + foundBy.toString());
        }

        return element;
    }

    private MobileElement waitForElement(By by, int seconds) {
        MobileElement element;
        try {
            element = (MobileElement) fluentWait(seconds).until(ExpectedConditions.visibilityOfElementLocated(by));
        } catch (TimeoutException e) {
            System.out.println("Try find element " + by.toString() + ", not found");
            throw new NoSuchElementException(by.toString());
        }

        return element;
    }

    private MobileElement tryWaitForElement(By by, int seconds) {
        MobileElement element = null;
        try {
            element = (MobileElement) fluentWait(seconds).until(ExpectedConditions.visibilityOfElementLocated(by));
        } catch (NoSuchElementException e) {
            System.out.println("Try find element " + by.toString() + ", not found");
        }

        return element; // Return null as nothing was found
    }

    public MobileElement findElementById(String string, int seconds) {
        return waitForElement(MobileBy.id(string), seconds);
    }

    public MobileElement tryFindElementById(String string, int seconds) {
        return tryWaitForElement(MobileBy.id(string), seconds);
    }

    public MobileElement waitFindElementByClassName(String className, int seconds) {
        return waitForElement(MobileBy.className(className), seconds);
    }

    public MobileElement tryFindElementByClassName(String className, int seconds) {
        return tryWaitForElement(MobileBy.className(className), seconds);
    }

    public MobileElement waitFindElementByXPath(String string, int seconds) {
        return waitForElement(MobileBy.xpath(string), seconds);
    }

    public MobileElement tryFindElementByXPath(String string, int seconds) {
        return tryWaitForElement(MobileBy.xpath(string), seconds);
    }

    // iOS specific find methods
    private MobileElement waitForOffScreenElement(By by, int seconds) {
        MobileElement element;
        try {
            element = (MobileElement) fluentWait(seconds).until(ExpectedConditions.presenceOfElementLocated(by));
        } catch (TimeoutException e) {
            System.out.println("Try find element " + by.toString() + ", not found");
            throw new NoSuchElementException(by.toString());
        }

        return element;
    }

    private MobileElement tryWaitForOffScreenElement(By by, int seconds) {
        MobileElement element = null;
        try {
            element = (MobileElement) fluentWait(seconds).until(ExpectedConditions.presenceOfElementLocated(by));
        } catch (NoSuchElementException e) {
            System.out.println("Try find element " + by.toString() + ", not found");
        }

        return element;
    }

    public MobileElement waitFindElementByAccessibilityId(String string, int seconds) {
        return waitForElement(MobileBy.AccessibilityId(string), seconds);
    }

    public MobileElement tryFindElementByAccessibilityId(String string, int seconds) {
        return waitForOffScreenElement(MobileBy.AccessibilityId(string), seconds);
    }

    // Additional expected conditions
    public Alert waitUntilAlertIsPresent() {
        return fluentWait(DEFAULT_TIMEOUT).until(ExpectedConditions.alertIsPresent());
    }

    public boolean waitUntilInvisibilityOfElement(By findBy, int seconds) {
        return fluentWait(seconds).until(ExpectedConditions.invisibilityOfElementLocated(findBy));
    }
}
