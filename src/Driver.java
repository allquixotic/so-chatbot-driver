import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.io.TemporaryFilesystem;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.ScreenshotException;

public class Driver 
{
	private final boolean useFirefox;
	private final RemoteWebDriver dri;
	private final String browserPath;
	public static final String programName = "so-chatbot-driver";
	public static boolean displayScreenshots = false;
	
	@SuppressWarnings("static-access")
	public static void main(String[] args) throws FileNotFoundException, InterruptedException, IOException
	{
		CommandLineParser parser = new GnuParser();
		Options options = new Options();
		CommandLine line = null;
		HelpFormatter hf = new HelpFormatter();
		OptionGroup og = new OptionGroup();
		Option opjs = OptionBuilder.withLongOpt("phantomjs").withDescription("Use PhantomJS").create('j');
		Option off = OptionBuilder.withLongOpt("firefox").withDescription("Use Firefox").create('f');
		og.setRequired(true);
		og.addOption(opjs);
		og.addOption(off);
		
		options.addOption("s", "screenshot", false, "Display screenshots at checkpoints");
		options.addOption("h",  "help", false, "Print this message" );
		options.addOptionGroup(og);
		options.addOption(OptionBuilder.withLongOpt( "bot-script" ).withDescription( "Required: Bot script file name" ).hasArg().withArgName("FILE").create('b'));
		options.addOption("u", "username", true, "Required: StackExchange username or email");
		options.addOption("p", "password", true, "Required: StackExchange password");
		options.addOption(OptionBuilder.withLongOpt("chat-url").withDescription("Required: Chatroom URL").hasArg().withArgName("URL").create('c'));
		options.addOption(OptionBuilder.withLongOpt("browser-path").withDescription("Optional: Browser binary path").hasArg().withArgName("PATH").create('l'));
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
		
		if(!line.hasOption("j") && !line.hasOption("f"))
		{
			hf.printHelp(programName, options);
			return;
		}
		
		boolean hff = false;
		if(line.hasOption("f"))
			hff = true;
		
		String bp = null;
		if(line.hasOption("l"))
		{
			bp = line.getOptionValue('l');
		}
		
		System.out.println("Arg processing finished; instantiating Driver object");
		Driver d = new Driver(hff, bp);
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
		TakesScreenshot ts = (TakesScreenshot) dri;
		dri.navigate().to(chatURL);
		Thread.sleep(rand(5000, 10000));
		System.out.println(dri.getCurrentUrl() + ": In the chatroom! Determining if we need to login...");
		ImagePanel.displayImage(ts.getScreenshotAs(OutputType.BASE64));
		
		WebElement loginLink = null;
		try
		{
			loginLink = dri.findElement(By.xpath("//a[starts-with(@href, '/login/global') and text() = 'logged in' and not(ancestor::div[contains(@style,'display:none')]) and not(ancestor::div[contains(@style,'display: none')])]"));
			System.out.println("Crap. We need to login.");
			loginLink.click();
		}
		catch(NoSuchElementException nsee)
		{
			System.out.println("We don't need to login!");
		}
		
		if(loginLink != null)
		{
			//Need to login
			Thread.sleep(rand(5000, 6500));
			System.out.println(dri.getCurrentUrl() + ": Performing stage 1 chat-login auth link");
			ImagePanel.displayImage(ts.getScreenshotAs(OutputType.BASE64));
			WebElement midpointLink = null;
			try
			{
				midpointLink = dri.findElement(By.xpath("//a[contains(@href, '/users/chat-login')]"));
				System.out.println("We don't have a network cookie at all.");
				midpointLink.click();
			}
			catch(NoSuchElementException nsee)
			{
				System.out.println("Sweet! We're logged in!");
			}
			if(midpointLink != null)
			{
				Thread.sleep(rand(3000, 6500));
				System.out.println(dri.getCurrentUrl() + ": We should be at the page that asks you to pick a sign-in method now...");
				ImagePanel.displayImage(ts.getScreenshotAs(OutputType.BASE64));
				
				dri.executeScript("openid.signin('stack_exchange');");
				Thread.sleep(rand(2000, 6500));
				ImagePanel.displayImage(ts.getScreenshotAs(OutputType.BASE64));
				System.out.println(dri.getCurrentUrl() + ": We should be at the sign-in page now...");
				
				dri.switchTo().frame(dri.findElementById("affiliate-signin-iframe"));
				WebElement email = dri.findElement(By.id("email"));
				email.click();
				email.sendKeys(s_username);
				WebElement password = dri.findElement(By.id("password"));
				password.click();
				password.sendKeys(s_password);
				ImagePanel.displayImage(ts.getScreenshotAs(OutputType.BASE64));
				WebElement submitButton = dri.findElement(By.className("affiliate-button"));
				if(submitButton == null)
					throw new IOException("Couldn't find the affiliate-button classed submit button!");
				submitButton.click();
				Thread.sleep(rand(5000, 6500));
				System.out.println(dri.getCurrentUrl() + ": We should be logged in now...");
				ImagePanel.displayImage(ts.getScreenshotAs(OutputType.BASE64));
				dri.switchTo().defaultContent();
				
				dri.navigate().to(chatURL);
				Thread.sleep(rand(8000, 10000));
				System.out.println(dri.getCurrentUrl() + ": In the chatroom! Authenticating...");
				ImagePanel.displayImage(ts.getScreenshotAs(OutputType.BASE64));
				
				loginLink = dri.findElement(By.xpath("//a[starts-with(@href, '/login/global') and text() = 'logged in' and not(ancestor::div[contains(@style,'display:none')]) and not(ancestor::div[contains(@style,'display: none')])]"));
				loginLink.click();
				Thread.sleep(rand(8000, 10000));
			}
		}
		
		String content = readFile(scriptPath);
		if(content == null || content.length() == 0)
			throw new IOException("Couldn't load " + scriptPath);
		System.out.println(dri.getCurrentUrl() + ": Executing bot script...");
		dri.executeScript(content);
		
		System.out.println("Running...");
		ImagePanel.displayImage(ts.getScreenshotAs(OutputType.BASE64));
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		for(String input = br.readLine(); input != null; input = br.readLine())
		{
			dri.executeScript(input);
		}
		
	}
	
	public static String readFile(String path) 
			  throws IOException 
	{
	  byte[] encoded = Files.readAllBytes(Paths.get(path));
	  return StandardCharsets.UTF_8.decode(ByteBuffer.wrap(encoded)).toString();
	}
	
	public Driver(boolean hff, String _browserPath) 
	{
		browserPath = _browserPath;
		this.useFirefox = hff;
		if(useFirefox)
		{
			//NOTE: Xvfb needs to be started if this is headless Linux. We don't start Xvfb here, do it in a script, it's a lot less code in Bash.
			
			//Here we set the temporary directory to ~/so-chatbot-profile to eliminate potential problems with /tmp permissions, space, etc.
			File fff = new File(System.getProperty("user.home") + File.separator + "so-chatbot-profile");
			if(!fff.exists())
			{
				fff.mkdirs();
			}
			TemporaryFilesystem.setTemporaryDirectory(fff);
			DesiredCapabilities caps = new DesiredCapabilities(DesiredCapabilities.firefox());
			if(browserPath != null && browserPath.length() > 0)
			{
				caps.setCapability(FirefoxDriver.BINARY, browserPath);
			}
			System.out.println("Instantiating FirefoxDriver");
			dri = new FirefoxDriver(caps);
		}
		else
		{
			File fil = new File("cookies.txt");
			fil.delete();
			DesiredCapabilities caps = new DesiredCapabilities(DesiredCapabilities.phantomjs());
			//caps.setCapability("phantomjs.page.settings.userAgent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.95 Safari/537.36");
			caps.setCapability("phantomjs.page.settings.userAgent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/537.13+ (KHTML, like Gecko) Version/5.1.7 Safari/534.57.2");
			caps.setCapability("phantomjs.cli.args", new String[] { "--cookies-file=" + fil.getAbsolutePath() });
			System.out.println("Instantiating PhantomJSDriver");
			dri = new PhantomJSDriver(caps);
		}

		dri.manage().deleteAllCookies();
		dri.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	}
}
