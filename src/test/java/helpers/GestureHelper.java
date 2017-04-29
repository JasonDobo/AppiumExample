package helpers;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import org.junit.Assert;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;

import java.util.HashMap;

public class GestureHelper {

    private final int DEFAULT_SWIPE_DISTANCE = 500;
    private final int DEFAULT_SWIPE_DURATION = 500; // milleseconds: must be between 500ms or less than 60000ms
    private final int DEFAULT_TAP_DURATION = 250;
    private final int SWIPE_PADDING = 10;
    private AppiumDriver appiumDriver;

    public GestureHelper(AppiumDriver driver) {
        this.appiumDriver = driver;
    }

    public void tap(MobileElement element) {
        appiumDriver.tap(1, element, DEFAULT_TAP_DURATION);
    }

    public void tap(int x, int y) {
        if (isPointInScreenBounds(new Point(x, y))) {
            new TouchAction(appiumDriver).tap(x, y).perform();
        } else {
            throw new IllegalArgumentException("Tap coordinates must be within the bounds of the screen.");
        }
    }

    public void swipe(MobileElement element, String direction) {
        swipe(element, direction, DEFAULT_SWIPE_DISTANCE);
    }

    private Point getNormal(String direction) {
        switch (direction) {
            case "UP":
                return new Point(0, -1);
            case "DOWN":
                return new Point(0, 1);
            case "LEFT":
                return new Point(-1, 0);
            case "RIGHT":
                return new Point(1, 0);
            default:
                throw new IllegalArgumentException();
        }
    }

    private String reverse(String direction) {
        switch (direction) {
            case "UP":
                return "DOWN";
            case "DOWN":
                return "UP";
            case "LEFT":
                return "RIGHT";
            default:
                return "LEFT";
        }
    }

    public void swipe(MobileElement element, String direction, int distance) {
        Point startPoint = getStartPointForDirection(element, direction);
        Point endPoint;

        if (distance <= 0) { // make the dimension of the component/device
            endPoint = getEndPointForDirection(element, direction);
        } else { // apply user distance
            Point offset = multiplyMagnitude(getNormal(direction), distance);
            endPoint = startPoint.moveBy(offset.getX(), offset.getY());
            if (!isPointInDeviceBounds(endPoint)) {
                endPoint = getEndPointForDirection(element, direction);
            }
        }

        appiumDriver.swipe(startPoint.x, startPoint.y, endPoint.x, endPoint.y, DEFAULT_SWIPE_DURATION);
    }

    public void swipeFromCentreOfScreen(String direction, int distance) {
        if (distance <= 0) {
            Assert.fail("Cannot perform swipeFromCentreOfScreen with a 0 or negative distance");
        }

        Point startPoint = getDeviceCenter();
        Point offset = multiplyMagnitude(getNormal(direction), distance);
        Point endPoint = startPoint.moveBy(offset.getX(), offset.getY());

        appiumDriver.swipe(startPoint.x, startPoint.y, endPoint.x, endPoint.y, DEFAULT_SWIPE_DURATION);
    }

    public void moveElementBelowElement(MobileElement elementToMove, MobileElement element) {
        int distanceDifference = getMaxY(element) - getMinY(elementToMove);

        String directionToMove = "DOWN";
        if (distanceDifference < 0) {
            directionToMove = "UP";
            distanceDifference = Math.abs(distanceDifference);
        }

        if (distanceDifference != 0) {
            swipeFromCentreOfScreen(directionToMove, distanceDifference);
        }
    }

    public void scrollToElement(MobileElement element) {
        JavascriptExecutor js = appiumDriver;
        HashMap<String, String> scrollObject = new HashMap<>();
        scrollObject.put("direction", "down");
        scrollObject.put("element", element.getId());

        js.executeScript("mobile: scroll", scrollObject);
    }

    public void tryScrollToElement(MobileElement element) {
        try {
            scrollToElement(element);
        } catch (Exception e) {
            System.out.println("Try scroll to element failed, continuing...");
        }
    }

    private Point multiplyMagnitude(final Point point, int magnitude) {
        return new Point(point.x * magnitude, point.y * magnitude);
    }

    private Point getStartPointForDirection(MobileElement element, final String direction) {
        return getFurthestPointOnElement(element, reverse(direction));
    }

    private Point getEndPointForDirection(MobileElement element, String direction) {
        return getFurthestPointOnElement(element, direction);
    }

    /**
     * Gets the centered, furthest point in the given direction.
     *
     * @param element   The element to measure. If null, the device coordinates are used.
     * @param direction The direction of the furthest point.
     */

    private Point getFurthestPointOnElement(MobileElement element, String direction) {
        int x = 0;
        int y = 0;
        int xCenter;
        int yCenter;
        int width;
        int height;

        if (element != null) {
            width = element.getSize().width;
            height = element.getSize().height;
            x = element.getLocation().x;
            y = element.getLocation().y;
            xCenter = element.getCenter().getX();
            yCenter = element.getCenter().getY();
        } else {
            width = getDeviceWidth();
            height = getDeviceHeight();
            xCenter = getDeviceCenter().getX();
            yCenter = getDeviceCenter().getY();
        }

        switch (direction) {
            case "UP":
                x = xCenter;
                y += SWIPE_PADDING;
                if (!isYInDeviceBounds(y)) {
                    y = SWIPE_PADDING;
                }
                break;
            case "DOWN":
                x = xCenter;
                y += height - SWIPE_PADDING;
                if (!isYInDeviceBounds(y)) {
                    y = getDeviceHeight() - SWIPE_PADDING;
                }
                break;
            case "LEFT":
                x += SWIPE_PADDING;
                y = yCenter;
                if (!isXInDeviceBounds(x)) {
                    x = SWIPE_PADDING;
                }
                break;
            case "RIGHT":
                x += width - SWIPE_PADDING;
                y = yCenter;
                if (!isXInDeviceBounds(x)) {
                    x = getDeviceWidth() - SWIPE_PADDING;
                }
                break;
        }

        return new Point(x, y);
    }

    private int getMaxY(MobileElement element) {
        return element.getLocation().getY() + element.getSize().getHeight();
    }

    private int getMinY(MobileElement element) {
        return element.getLocation().getY();
    }

    private boolean isPointInDeviceBounds(final Point point) {
        return (isXInDeviceBounds(point.x) && isYInDeviceBounds(point.y));
    }

    private boolean isXInDeviceBounds(int x) {
        return !(x < 0 || x > getDeviceWidth());
    }

    private boolean isYInDeviceBounds(int y) {
        return !(y < 0 || y > getDeviceHeight());
    }

    private int getDeviceWidth() {
        return appiumDriver.manage().window().getSize().getWidth();
    }

    private int getDeviceHeight() {
        return appiumDriver.manage().window().getSize().getHeight();
    }

    private boolean isPointInScreenBounds(Point point) {
        Dimension size = appiumDriver.manage().window().getSize();
        return !(point.getX() > size.getWidth() || point.getY() > size.getHeight()
                || point.getX() < 0 || point.getY() < 0);
    }

    private Point getDeviceCenter() {
        return new Point((int) (getDeviceWidth() / 2.0), (int) (getDeviceHeight() / 2.0));
    }

}
