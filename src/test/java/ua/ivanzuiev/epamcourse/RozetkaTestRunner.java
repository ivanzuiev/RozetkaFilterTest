package ua.ivanzuiev.epamcourse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.PageFactory;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by Ivan on 05.02.2016.
 */
public class RozetkaTestRunner {

    private WebDriver driver;
    private String baseUrl;
    private boolean acceptNextAlert = true;
    private StringBuffer verificationErrors = new StringBuffer();
    private int count = 1;

    @Before
    public void setUp() throws Exception {
        driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(100, TimeUnit.SECONDS);
        driver.manage().window().maximize();
    }

    @Test
    public void testOscillograf() throws Exception {
        SmartfonPage page=PageFactory.initElements(driver,SmartfonPage.class);
        page.open();
        WebElement filter=page.getFilter("Цвет");//Или Производитель или Диагональ экрана и т.д.
//        String string="Golden";
//        page=page.executeFiltering(el,string);
//        assertTrue(page.checkResults("Gold"));
        String stringForFilteringExecution="White";
        page=page.executeFiltering(filter,stringForFilteringExecution);
        assertTrue(page.checkResults("White"));

    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
    }


}
