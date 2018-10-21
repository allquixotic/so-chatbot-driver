package com.superuser.cavil;
import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
public class Config extends OptionsBase {
	@Option(
		      name = "help",
		      abbrev = 'h',
		      help = "Prints usage info.",
		      defaultValue = "false"
		    )
	public boolean help = false;
	
	@Option(
		      name = "roomUrl",
		      abbrev = 'r',
		      help = "Room URL",
		      defaultValue = "https://chat.stackexchange.com/rooms/118"
		    )
	public String roomUrl = "https://chat.stackexchange.com/rooms/118";
	
	@Option(
		      name = "loginUrl",
		      abbrev = 'l',
		      help = "Login URL",
		      defaultValue = "http://stackexchange.com/users/login"
		    )
	public String loginUrl = "http://stackexchange.com/users/login";
	
	@Option(
		      name = "siteUrl",
		      abbrev = 's',
		      help = "Site URL",
		      defaultValue = "https://stackexchange.com"
		    )
	public String siteUrl = "https://stackexchange.com";
	
	@Option(
		      name = "email",
		      abbrev = 'e',
		      help = "Email",
		      defaultValue = ""
		    )
	public String email = null;
	
	@Option(
		      name = "password",
		      abbrev = 'p',
		      help = "Password",
		      defaultValue = ""
		    )
	public String password = null;
	
	@Option(
		      name = "scriptUrl",
		      abbrev = 'c',
		      help = "Script URL",
		      defaultValue = "http://localhost/master.js"
		    )
	public String scriptUrl = "http://localhost/master.js";
	
	@Option(
		      name = "profile",
		      abbrev = 'f',
		      help = "Profile",
		      defaultValue = ""
		    )
	public String profile = null;
	
	@Option(
		      name = "binary",
		      abbrev = 'b',
		      help = "Binary",
		      defaultValue = ""
		    )
	public String binary = null;
	
	@Option(
		      name = "headless",
		      abbrev = 'a',
		      help = "Headless",
		      defaultValue = "true"
		    )
	public boolean headless;

}
