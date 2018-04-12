package burp;

import java.io.PrintWriter;

public class BurpExtender implements IBurpExtender
{
	private String extensionName = "CookiePoter";
	private String version = "0.1";
	private PrintWriter stdout;
	
    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks)
    {
        IExtensionHelpers helpers = callbacks.getHelpers();
        stdout = new PrintWriter(callbacks.getStdout(),true);
		callbacks.setExtensionName(extensionName + " v" + version);	
        callbacks.registerContextMenuFactory(new Menu(callbacks));
        stdout.println("[+] " + extensionName + " is loaded");
        stdout.println("[+] ^_^");
        stdout.println(getBanner());
    }
    
	private String getBanner(){
		String bannerInfo = 
				    "[+]\n"
				  + "[+] #####################################\n"
				  + "[+]    CookiePorter v"+version+"\n"
				  + "[+]    anthor: c0ny1\n"
				  + "[+]    email:  root@gv7.me\n"
				  + "[+]    github: http://github.com/c0ny1/burp-cookie-porter\n"
				  + "[+] ####################################";
		return bannerInfo;
	}
}
