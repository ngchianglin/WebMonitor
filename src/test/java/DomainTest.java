/*
* MIT License
*
*Copyright (c) 2018 Ng Chiang Lin
*
*Permission is hereby granted, free of charge, to any person obtaining a copy
*of this software and associated documentation files (the "Software"), to deal
*in the Software without restriction, including without limitation the rights
*to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
*copies of the Software, and to permit persons to whom the Software is
*furnished to do so, subject to the following conditions:
*
*The above copyright notice and this permission notice shall be included in all
*copies or substantial portions of the Software.
*
*THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
*IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
*FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
*AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
*LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
*OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
*SOFTWARE.
*
*/

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import sg.nighthour.crypto.CryptoUtil;
import sg.nighthour.crypto.TimeBaseOTP;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DomainTest {
	
	private  String GECKODRIVER_PATH = TestConstants.GECKODRIVER_PATH;
	private String GECKODRIVER = TestConstants.GECKODRIVER;
	private String userid= TestConstants.TESTUSER;
	private String password= TestConstants.TESTPASSWORD;
	private String loginurl = TestConstants.LOGIN;
	private String domainlink = TestConstants.DOMAIN;
	
	private String testdomain1 = "nighthour1.sg";
	private String testdomain2 = "nighthour2.sg"; 


	@Before
	public void setUp() throws Exception {
		
		System.setProperty(GECKODRIVER,GECKODRIVER_PATH);
	}

	@Test(timeout=60000)
	public void testAddDomain() throws InterruptedException {
		
		WebDriver driver = new FirefoxDriver();
		driver.get(loginurl);
		Thread.sleep(TestConstants.SLEEP_INTERVAL);
		driver.findElement(By.name("userid")).sendKeys(userid);
		WebElement element = driver.findElement(By.name("password"));
		element.sendKeys(password);
		element.submit();
		Thread.sleep(TestConstants.SLEEP_INTERVAL);
		
		String otpresult = TimeBaseOTP.generateOTP(CryptoUtil.hexStringToByteArray(TestConstants.TESTOTPSECRET));
		WebElement otpelement = driver.findElement(By.name("totp"));
		otpelement.sendKeys(otpresult);
		otpelement.submit();
		Thread.sleep(TestConstants.SLEEP_INTERVAL);
		
		WebElement domainurl = driver.findElement(By.linkText(domainlink));
		domainurl.click();
		
		Thread.sleep(TestConstants.SLEEP_INTERVAL);
		driver.findElement(By.id("domain")).sendKeys(testdomain1);
		driver.findElement(By.id("add")).click();
		Thread.sleep(TestConstants.SLEEP_INTERVAL);
		String domainlists =  driver.findElement(By.id("domainlist")).getText();
		String msg = driver.findElement(By.id("msg")).getText();
		driver.close();
		
		boolean result = false ;
		
		if(domainlists.contains(testdomain1) && msg.equals(""))
		{
			result=true; 
		}
		
		assertTrue("Cannot add domain ",result);
		
		
	}
	
	@Test(timeout=60000)
	public void testDeleteDomain() throws InterruptedException
	{
		WebDriver driver = new FirefoxDriver();
		driver.get(loginurl);
		Thread.sleep(TestConstants.SLEEP_INTERVAL);
		driver.findElement(By.name("userid")).sendKeys(userid);
		WebElement element = driver.findElement(By.name("password"));
		element.sendKeys(password);
		element.submit();
		Thread.sleep(TestConstants.SLEEP_INTERVAL);
		
		String otpresult = TimeBaseOTP.generateOTP(CryptoUtil.hexStringToByteArray(TestConstants.TESTOTPSECRET));
		WebElement otpelement = driver.findElement(By.name("totp"));
		otpelement.sendKeys(otpresult);
		otpelement.submit();
		Thread.sleep(TestConstants.SLEEP_INTERVAL);
		
		WebElement domainurl = driver.findElement(By.linkText(domainlink));
	    domainurl.click();
		Thread.sleep(TestConstants.SLEEP_INTERVAL);
		driver.findElement(By.id("domain")).sendKeys(testdomain1);
		driver.findElement(By.id("delete")).click();
		Thread.sleep(TestConstants.SLEEP_INTERVAL);
		
		String domainlists =  driver.findElement(By.id("domainlist")).getText();
		
		driver.close();
		
		
		assertFalse("Cannot delete domain ",domainlists.contains(testdomain1));
				
		
		
	}
	
	@Test(timeout=60000)
	public void testAddDomainTwice() throws InterruptedException
	{
		WebDriver driver = new FirefoxDriver();
		driver.get(loginurl);
		Thread.sleep(TestConstants.SLEEP_INTERVAL);
		driver.findElement(By.name("userid")).sendKeys(userid);
		WebElement element = driver.findElement(By.name("password"));
		element.sendKeys(password);
		element.submit();
		Thread.sleep(TestConstants.SLEEP_INTERVAL);
		
		String otpresult = TimeBaseOTP.generateOTP(CryptoUtil.hexStringToByteArray(TestConstants.TESTOTPSECRET));
		WebElement otpelement = driver.findElement(By.name("totp"));
		otpelement.sendKeys(otpresult);
		otpelement.submit();
		Thread.sleep(TestConstants.SLEEP_INTERVAL);
		
		WebElement domainurl = driver.findElement(By.linkText(domainlink));
	    domainurl.click();
		Thread.sleep(TestConstants.SLEEP_INTERVAL);
		
		//Add same domain twice
		driver.findElement(By.id("domain")).clear();
		driver.findElement(By.id("domain")).sendKeys(testdomain2);
		driver.findElement(By.id("add")).click();
		Thread.sleep(TestConstants.SLEEP_INTERVAL);
		
		driver.findElement(By.id("domain")).clear();
		driver.findElement(By.id("domain")).sendKeys(testdomain2);
		driver.findElement(By.id("add")).click();
		Thread.sleep(TestConstants.SLEEP_INTERVAL);
		
		
		String msg = driver.findElement(By.id("msg")).getText();
		driver.close();
		assertTrue("Duplicate domain can be added ",msg.equals("Error: Domain exists"));
		
	}
	
	@Test(timeout=60000)
	public void testDeleteDomainTwice() throws InterruptedException
	{
		WebDriver driver = new FirefoxDriver();
		driver.get(loginurl);
		Thread.sleep(TestConstants.SLEEP_INTERVAL);
		driver.findElement(By.name("userid")).sendKeys(userid);
		WebElement element = driver.findElement(By.name("password"));
		element.sendKeys(password);
		element.submit();
		Thread.sleep(TestConstants.SLEEP_INTERVAL);
		
		String otpresult = TimeBaseOTP.generateOTP(CryptoUtil.hexStringToByteArray(TestConstants.TESTOTPSECRET));
		WebElement otpelement = driver.findElement(By.name("totp"));
		otpelement.sendKeys(otpresult);
		otpelement.submit();
		Thread.sleep(TestConstants.SLEEP_INTERVAL);
		
		WebElement domainurl = driver.findElement(By.linkText(domainlink));
	    domainurl.click();
		Thread.sleep(TestConstants.SLEEP_INTERVAL);
		
		//Delete domain twice
		driver.findElement(By.id("domain")).clear();
		driver.findElement(By.id("domain")).sendKeys(testdomain2);	
		driver.findElement(By.id("delete")).click();
		Thread.sleep(TestConstants.SLEEP_INTERVAL);
		
		driver.findElement(By.id("domain")).clear();
		driver.findElement(By.id("domain")).sendKeys(testdomain2);
		driver.findElement(By.id("delete")).click();
		Thread.sleep(TestConstants.SLEEP_INTERVAL);
		
		String msg = driver.findElement(By.id("msg")).getText();
		driver.close();
		
		assertTrue("Domain can be deleted twice ",msg.equals("Error: Domain does not exist"));
		
		
	}
	

}
