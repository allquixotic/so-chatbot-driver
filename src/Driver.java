import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.ScreenshotException;

public class Driver 
{
	private final PhantomJSDriver dri;
	public static final String programName = "so-chatbot-driver";
	public static boolean displayScreenshots = false;
	
	@SuppressWarnings("static-access")
	public static void main(String[] args) throws FileNotFoundException, InterruptedException, IOException
	{
		CommandLineParser parser = new GnuParser();
		Options options = new Options();
		CommandLine line = null;
		HelpFormatter hf = new HelpFormatter();
		
		options.addOption("s", "screenshot", false, "Display screenshots at checkpoints");
		options.addOption("h",  "help", false, "Print this message" );
		options.addOption(OptionBuilder.withLongOpt( "bot-script" ).withDescription( "Required: Bot script file name" ).hasArg().withArgName("FILE").create('b'));
		options.addOption("u", "username", true, "Required: StackExchange username or email");
		options.addOption("p", "password", true, "Required: StackExchange password");
		options.addOption(OptionBuilder.withLongOpt("chat-url").withDescription("Required: Chatroom URL").hasArg().withArgName("URL").create('c'));
		try
		{
			line = parser.parse(options, args);
		}
		catch(ParseException pe)
		{
			hf.printHelp(programName, options);
			return;
		}
		
		if(line.hasOption("h"))
		{
			hf.printHelp(programName, options);
		}
		
		if(line.hasOption("s"))
		{
			Driver.displayScreenshots = true;
		}
		
		//Check for required options
		for(String s : new String[]{"u", "p", "b", "c"})
		{
			if(!line.hasOption(s))
			{
				hf.printHelp(programName, options);
				return;
			}
		}
		
		Driver d = new Driver();
		try
		{
			d.go(line.getOptionValue('u'), line.getOptionValue('p'), line.getOptionValue('b'), line.getOptionValue('c'));
		}
		catch(ScreenshotException sse)
		{
			ImagePanel.displayImage(sse.getBase64EncodedScreenshot());
		}
		catch(Exception se)
		{
			se.printStackTrace();
		}
		finally
		{
			d.dri.quit();
			System.exit(0);
		}
	}
	
	private static int rand(int min, int max)
	{
		return min + (int)(Math.random() * ((max - min) + 1));
		
	}
	public void go(String s_username, String s_password, String scriptPath, String chatURL) throws InterruptedException, IOException, FileNotFoundException
	{
		dri.navigate().to("http://superuser.com");
		dri.manage().deleteAllCookies();
		Thread.sleep(rand(2000, 6500));
		System.out.println(dri.getCurrentUrl() + ": We should be at superuser.com now...");
		ImagePanel.displayImage(dri.getScreenshotAs(OutputType.BASE64));
		
		dri.navigate().to("http://superuser.com/users/login");
		Thread.sleep(rand(2000, 6500));
		System.out.println(dri.getCurrentUrl() + ": We should be at the page that asks you to pick a sign-in method now...");
		ImagePanel.displayImage(dri.getScreenshotAs(OutputType.BASE64));
		
		dri.executeScript("openid.signin('stack_exchange');");
		Thread.sleep(rand(2000, 6500));
		ImagePanel.displayImage(dri.getScreenshotAs(OutputType.BASE64));
		System.out.println(dri.getCurrentUrl() + ": We should be at the sign-in page now...");
		
		dri.switchTo().frame(dri.findElementById("affiliate-signin-iframe"));
		WebElement email = dri.findElement(By.id("email"));
		email.click();
		email.sendKeys(s_username);
		WebElement password = dri.findElement(By.id("password"));
		password.click();
		password.sendKeys(s_password);
		ImagePanel.displayImage(dri.getScreenshotAs(OutputType.BASE64));
		WebElement submitButton = dri.findElement(By.className("affiliate-button"));
		if(submitButton == null)
			throw new IOException("Couldn't find the affiliate-button classed submit button!");
		submitButton.click();
		Thread.sleep(rand(2000, 6500));
		System.out.println(dri.getCurrentUrl() + ": We should be logged in now...");
		ImagePanel.displayImage(dri.getScreenshotAs(OutputType.BASE64));
		dri.switchTo().defaultContent();
		dri.navigate().to(chatURL);
		Thread.sleep(10000);
		System.out.println(dri.getCurrentUrl() + ": In the chatroom! Reading from the bot script file...");
		ImagePanel.displayImage(dri.getScreenshotAs(OutputType.BASE64));
		
		String content = readFile(scriptPath);
		if(content == null || content.length() == 0)
			throw new IOException("Couldn't load " + scriptPath);
		
		System.out.println(dri.getCurrentUrl() + ": Executing bot script...");
		dri.executeScript(content);
		
		System.out.println("Running...");
		while(true)
		{
			Thread.sleep(1000);
		}
		
	}
	
	public static String readFile(String path) 
			  throws IOException 
	{
	  byte[] encoded = Files.readAllBytes(Paths.get(path));
	  return StandardCharsets.UTF_8.decode(ByteBuffer.wrap(encoded)).toString();
	}
	
	public Driver() 
	{
		DesiredCapabilities caps = new DesiredCapabilities(DesiredCapabilities.phantomjs());
		caps.setCapability("phantomjs.page.settings.userAgent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.95 Safari/537.36");
		dri = new PhantomJSDriver(caps);
		dri.manage().deleteAllCookies();
		dri.manage().timeouts().implicitlyWait(120, TimeUnit.SECONDS);
	}
}
