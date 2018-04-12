package burp;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Menu implements IContextMenuFactory {
    private final IExtensionHelpers m_helpers;
    private IBurpExtenderCallbacks m_callback;
    private PrintWriter stderr;
    public Menu(IBurpExtenderCallbacks callbacks) {
        m_callback = callbacks;
        m_helpers = callbacks.getHelpers();
        stderr = new PrintWriter(m_callback.getStderr(),true);
    }

    public List<JMenuItem> createMenuItems(final IContextMenuInvocation invocation) {
        List<JMenuItem> menus = new ArrayList();

        JMenuItem miCookiePorter = new JMenuItem("CookiePorter");
        
        miCookiePorter.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						IHttpRequestResponse iReqResp = invocation.getSelectedMessages()[0];
		                try{
						Gui gui = new Gui(m_callback,iReqResp);
						m_callback.customizeUiComponent(gui);
		                gui.setVisible(true);
		                }catch (Exception e1) {
							stderr.println(e1.getMessage());
						}
					}
				}
            );

        menus.add(miCookiePorter);
        return menus;
    }

}