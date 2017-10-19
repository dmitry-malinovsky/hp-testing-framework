package com.dima.hptf.ui.utils;

import com.dima.hptf.ui.browser.Browser;
import com.dima.hptf.cucumber.ext.logging.TestLogHelper;
import com.dima.hptf.cucumber.ext.reporting.CukeScenarioContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class ScreenshotUtils {
    private static final String screenshotProperty = "screenshoting";
    private static final String runFromProperty = "runfrom";
    public static String folderPath;
    private static Logger logger = LoggerFactory.getLogger(ScreenshotUtils.class);
    private static int screenshotIndex = 1;
    private static String currentLogName = "";

    private ScreenshotUtils() {
    }

    public static void highlightElement(Browser browser, WebElement element) {
        browser.executeJavaScript("arguments[0].style.border=\'3px solid red\'", new WebElement[]{element});

    }

    public static void unhighlightElement(Browser browser, WebElement element) {
        browser.executeJavaScript("arguments[0].style.removeProperty(\'border\');", new WebElement[]{element});

    }

    public static void makeAScreenshot(WebDriver driver, String directory, String fileName) throws IOException {
        updateScreenshotIndexIfNewTest();
        String imageFormat = "PNG";
        String imageFileExtension = ".png";
        byte[] imageInByte;
        if(driver instanceof PhantomJSDriver) {
            imageInByte = (byte[])((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES);
        } else {
            Screenshot file = (new AShot()).shootingStrategy(new ViewportPastingStrategy(500)).takeScreenshot(driver);
            BufferedImage fos = file.getImage();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(fos, imageFormat, baos);
            baos.flush();
            imageInByte = baos.toByteArray();
        }

        CukeScenarioContext.getInstance().attachScreenShot(imageInByte);
        if("local".equals(System.getProperty("runfrom"))) {
            Files.createDirectories(Paths.get(directory, new String[0]), new FileAttribute[0]);
            File file1 = new File(directory + String.format("%03d", new Object[]{Integer.valueOf(screenshotIndex++)}) + fileName + imageFileExtension);
            FileOutputStream fos1 = new FileOutputStream(file1);

            try {
                fos1.write(imageInByte);
            } finally {
                fos1.close();
            }
        }

    }

    public static void makeAScreenshot(Browser browser, String fileName) throws IOException {
        makeAScreenshot(browser.getDriver(), getCurrentScreenshotFolderPath(), fileName);
    }

    public static void makeAScreenshot(WebDriver driver, String fileName) throws IOException {
        makeAScreenshot(driver, getCurrentScreenshotFolderPath(), fileName);
    }

    public static String getCurrentScreenshotFolderPath() {
        String logFilePath = TestLogHelper.getCurrentLogName();
        String logFolderPath = logFilePath.equals("test")?"test":logFilePath;
        logFolderPath = String.format(folderPath, new Object[]{logFolderPath});
        return logFolderPath;
    }

    public static void highlightAndScreenshot(Browser browser, String fileName, WebElement... elements) {
        if(makeFullScreenshot().booleanValue() || makePartialScreenshot().booleanValue()) {
            WebElement[] e = elements;
            int var4 = elements.length;

            int var5;
            WebElement we;
            for(var5 = 0; var5 < var4; ++var5) {
                we = e[var5];
                highlightElement(browser, we);
            }

            boolean var14 = false;

            label133: {
                try {
                    var14 = true;
                    makeAScreenshot(browser.getDriver(), fileName);
                    var14 = false;
                    break label133;
                } catch (IOException var15) {
                    logger.warn("Could not take screenshot with name: " + fileName);
                    var14 = false;
                } finally {
                    if(var14) {
                        WebElement[] var8 = elements;
                        int var9 = elements.length;

                        for(int var10 = 0; var10 < var9; ++var10) {
                            WebElement we1 = var8[var10];
                            unhighlightElement(browser, we1);
                        }

                    }
                }

                e = elements;
                var4 = elements.length;

                for(var5 = 0; var5 < var4; ++var5) {
                    we = e[var5];
                    unhighlightElement(browser, we);
                }

                return;
            }

            e = elements;
            var4 = elements.length;

            for(var5 = 0; var5 < var4; ++var5) {
                we = e[var5];
                unhighlightElement(browser, we);
            }

        }
    }

    private static void updateScreenshotIndexIfNewTest() {
        if(!currentLogName.equals(TestLogHelper.getCurrentLogName())) {
            currentLogName = TestLogHelper.getCurrentLogName();
            screenshotIndex = 1;
        }

    }

    public static Boolean makeFullScreenshot() {
        return Boolean.valueOf(ScreenshotUtils.ScreenshotingType.FULL.getValue().equalsIgnoreCase(System.getProperty("screenshoting")));
    }

    public static Boolean makePartialScreenshot() {
        return Boolean.valueOf(ScreenshotUtils.ScreenshotingType.PARTIAL.getValue().equalsIgnoreCase(System.getProperty("screenshoting")) || System.getProperty("screenshoting") == null);
    }

    public static String getCurrentTimeStamp() {
        SimpleDateFormat df = new SimpleDateFormat("MM_dd_HH_mm_ss");
        return df.format(new Date());
    }

    @Value("${folder.path.screenshot}")
    public void setFolderPath(String path) {
        folderPath = path;
    }

    public static enum ScreenshotingType {
        FULL("full"),
        PARTIAL("partial"),
        NO("no");

        private String value;

        private ScreenshotingType(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }
