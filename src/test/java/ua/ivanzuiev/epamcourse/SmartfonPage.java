package ua.ivanzuiev.epamcourse;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Ivan on 05.02.2016.
 */
public class SmartfonPage extends Page {

    public SmartfonPage(WebDriver driver){
        super(driver);
    }

    private final static String URL="http://rozetka.com.ua/mobile-phones/c80003/filter/preset=smartfon/";

    @FindBy(xpath = "//div[@class='filter-parametrs-i']")
    List<WebElement> listOfFilters;

    List<WebElement> listOfPages;
    String xpathForListOfPages="//ul[@name='paginator']/li";

    WebElement lastItem;
    String xpathForLastItemListOfPages="//ul[@name='paginator']/li[last()]";

    List<WebElement> listOfItems;
    String xpathForListOfItems="//div[@class='g-i-tile g-i-tile-catalog']";
    String cssForHiddenText="ul.g-i-tile-short-detail";




    public void open(){
        _driver.get(URL);
    }
    public WebElement getWebElement(By by){
        return _driver.findElement(by);
    }
    public WebElement getFilter(String filterName){
        WebElement result=null;
        for(WebElement el:listOfFilters){
            if(el.getText().contains(filterName)){
                result=el;
            }
        }
        return  result;
    }

    //@param filter - catagory of filter
    //@param str - item of filter
    public SmartfonPage executeFiltering(WebElement filter,String str){
        List<WebElement> listOfColors=filter.findElements(By.tagName("i"));
        for(WebElement el:listOfColors){
            if(el.getText().contains(str)){
                el.click();
                return PageFactory.initElements(_driver,SmartfonPage.class);
            }
        }
        return this;
    }

    public boolean checkResults(String str) throws IOException {
        int last=1;
        boolean moreThanOnePage=true;

       try{WebElement lastItem=(new WebDriverWait(_driver,3).until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathForLastItemListOfPages))));
           last=Integer.parseInt(lastItem.getText());
       }catch(Exception e){
           moreThanOnePage=false;
       }

        // цикл прохождения по страницам результата
        for(int i=1;i<=last;i++){
            // цикл прохождения по карточкам на странице результатов
            try{listOfItems=(new WebDriverWait(_driver,3).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(xpathForListOfItems))));

            }catch(Exception e){}
            if(listOfItems==null)break;
            for(WebElement element:listOfItems){

                //код имитирующий наведение мыши на элемент для появления полной информации элемента
                Actions actions=new Actions(_driver);
                actions.moveToElement(element);
                String itemText=element.getText();
//                System.out.print(itemText);
                actions.perform();
                WebElement subElement=element.findElement(By.cssSelector(cssForHiddenText));
                String subItemText=subElement.getText();
//                System.out.print(itemText);

                if(!((itemText.contains(str))||(subItemText.contains(str)))){
                    //снимаем скриншот и рисуем рамку вокруг проблемного элемента
                    File screenshot = ((TakesScreenshot)_driver).getScreenshotAs(OutputType.FILE);
                    FileUtils.copyFile(addBorder(screenshot, element), new File("C:\\Selenium\\Screenshots\\ScreenRozetka.jpeg"));

                    return false;
                }
            }

            if(moreThanOnePage) {
                listOfPages = _driver.findElements(By.xpath(xpathForListOfPages));
                //цикл ищет ссылку следующей страницы и нажимает её
                for (WebElement el : listOfPages) {
                    System.out.println(el.getText());
                if(el.getText().contains((new Integer(i+1)).toString())){
                    el.click();
                    try{
                        Thread.sleep(2000);
                    }catch(InterruptedException ex){
                        ex.printStackTrace();
                    }
                    break;
                }
                }
            }
        }
        return true;
    }

    private File addBorder(File imageFile, WebElement element) throws IOException {
        BufferedImage image= ImageIO.read(imageFile);

        int width=element.getSize().getWidth()+6;
        int height=element.getSize().getHeight()+6;
        Point point=element.getLocation();
        int x=point.getX()-3;
        int y=point.getY()-3;

        WritableRaster raster=image.getRaster();

        int[] colors=new int[4*width*3];
        int[] colorsSide=new int[4*3*height];

        for(int i=0;i<colors.length/4;i++){
            colors[i*4]=229;
            colors[i*4+1]=0;
            colors[i*4+2]=0;
            colors[i*4+3]=255;
        }
        for(int i=0;i<colorsSide.length/4;i++){
            colorsSide[i*4]=229;
            colorsSide[i*4+1]=0;
            colorsSide[i*4+2]=0;
            colorsSide[i*4+3]=255;
        }

        raster.setPixels(x,y,width,3,colors);
        raster.setPixels(x,y+height-3,width,3,colors);
        raster.setPixels(x,y,3,height,colorsSide);
        raster.setPixels(x+width-3,y,3,height,colorsSide);

        ImageIO.write(image,"png",imageFile);
        return imageFile;
    }
}
