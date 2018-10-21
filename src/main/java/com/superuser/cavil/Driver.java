package com.superuser.cavil;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.switchTo;

import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

import org.openqa.selenium.By;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.google.common.base.Strings;
import com.google.common.io.CharStreams;
import com.google.devtools.common.options.OptionsParser;

import io.github.bonigarcia.wdm.WebDriverManager;

public class Driver {

	private final Config conf;

	public Driver(String[] args) {
		this.conf = getConfig(args);
	}

	public void run() {
		System.out.println(conf.toString());
		var jsCode = getCode(conf);
		WebDriverManager.firefoxdriver().setup();
		var prof = conf.profile != null ? new FirefoxProfile(new File(conf.profile)) : new FirefoxProfile();
		var opts = new FirefoxOptions();
		opts.setProfile(prof);
		opts.addPreference("security.mixed_content.block_active_content", false);
		opts.setBinary(conf.binary);
		opts.setHeadless(conf.headless);
		opts.setAcceptInsecureCerts(true);
		opts.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.ACCEPT);

		var d = new FirefoxDriver(opts);
		WebDriverRunner.setWebDriver(d);
		login();

		Selenide.executeJavaScript(jsCode);
	}

	private Config getConfig(String[] args) {
		OptionsParser parser = OptionsParser.newOptionsParser(Config.class);
		parser.parseAndExitUponError(args);
		var conf = parser.getOptions(Config.class);
		if (args == null || args.length == 0 || conf.help
				|| Arrays.asList(new String[] { conf.binary, conf.email, conf.password, conf.scriptUrl, conf.roomUrl })
						.stream().anyMatch((s) -> Strings.isNullOrEmpty(s))) {
			printUsage(parser);
			System.exit(0);
		}
		return conf;
	}

	private void login() {
		for(int i = 0; i < 3 && alreadyLoggedIn() == false; i++) {
			s();
			open(conf.roomUrl);
			s();
			$(By.xpath("//a[.='logged in']")).click();
			s();
			$(By.xpath("//a[@title='log in with Stack_Exchange']")).click();
			s();
			switchTo().frame("affiliate-signin-iframe");
			$(By.id("email")).setValue(conf.email);
			$(By.id("password")).setValue(conf.password);
			$(By.id("email")).setValue(conf.email);
			$(By.id("password")).setValue(conf.password);
			s();
			$(By.cssSelector(".login-form form")).submit();
			switchTo().parentFrame();
			s();
		}
		if(alreadyLoggedIn() == false)
			throw new RuntimeException("UNABLE TO LOGIN!");
	}
	
	private void s() {
		//if(!conf.headless)
			try{Thread.sleep(3000);}catch(Exception e) {}
	}
	
	private boolean alreadyLoggedIn() {
		open(conf.roomUrl);
		try {
			$(By.xpath("//a[.='logged in']")).waitUntil(visible, 5000);
			return false;
		}
		catch(Throwable wde) {
			return true;
		}
	}

	private String getCode(Config conf) {
		try {
			return CharStreams.toString(new InputStreamReader(new URL(conf.scriptUrl).openStream()));
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	private void printUsage(OptionsParser parser) {
		System.out.println("Usage: java -jar su-chatbot.jar OPTIONS");
		System.out.println(
				parser.describeOptions(Collections.<String, String>emptyMap(), OptionsParser.HelpVerbosity.LONG));
	}

	public static void main(String[] args) {
		var d = new Driver(args);
		d.run();
	}
}
