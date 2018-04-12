package burp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.alibaba.fastjson.JSON;

public class Gui extends JFrame {
	private IHttpRequestResponse reqresp;
	private IBurpExtenderCallbacks m_callback;
	private IExtensionHelpers helpers;
	private PrintWriter stderr;
	private PrintWriter stdout;
	private String strDomain;
	private String strCookie;
	private String strJson;
	private String strJs;
	
	public Gui(IBurpExtenderCallbacks callbacks,IHttpRequestResponse reqresp) {
		this.m_callback = callbacks;
		this.reqresp = reqresp;
		this.helpers = m_callback.getHelpers();
		this.stdout = new PrintWriter(m_callback.getStdout(),true);
		this.stderr = new PrintWriter(m_callback.getStderr(),true);
		
		strDomain = reqresp.getHttpService().getHost();
		List<String> l = helpers.analyzeRequest(reqresp.getRequest()).getHeaders();
		
		for (String s : l) {
			int n = helpers.indexOf(s.getBytes(), "Cookie:".getBytes(), false, 0, s.length());
			if(n>=0){
				strCookie = s.substring(8);
				stdout.println(strCookie);
			}
		}
		strJson = ToJson(strCookie);
		strJs = ToJs(strCookie);
		
		initialze();
	}
	
	private void initialze(){
		this.setTitle("CookiePorter 0.1");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setSize(350,540);
		//居中显示
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		int x = (int)(toolkit.getScreenSize().getWidth()-this.getWidth())/2;
		int y = (int)(toolkit.getScreenSize().getHeight()-this.getHeight())/2;
		this.setLocation(x,y);
		this.setLayout(new BorderLayout());
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				
				JPanel Panel = new JPanel();
				Panel.setLayout(new GridBagLayout());
				
				JLabel lbDomain = new JLabel("Domain:");
				//lbDomain.setForeground(new Color(253,128,49));
				JTextField tfDomain = new JTextField(20);
				tfDomain.setText(strDomain);
				JPanel plDomain =new JPanel();
				plDomain.add(lbDomain);
				plDomain.add(tfDomain);
				
				JButton btnConvert = new JButton("convert");
				JLabel lbRaw = new JLabel("Raw");
				JButton btnCopyRawToClipboard = new JButton("Copy to Clipboard");
				JTextArea taRaw = new JTextArea();
				taRaw.setLineWrap(true);//激活自动换行功能   
				taRaw.setWrapStyleWord(true);//激活断行不断字功能  
				taRaw.setText(strCookie);
				JScrollPane spRaw= new JScrollPane(taRaw);				
				
				JLabel lbJson = new JLabel("Json");
				JButton btnCopyJsonToClipboard = new JButton("Copy to Clipboard");
				JTextArea taJson = new JTextArea();
				taJson.setLineWrap(true);//激活自动换行功能   
				taJson.setWrapStyleWord(true);//激活断行不断字功能  
				taJson.setEditable(false);
				taJson.setText(strJson);
				JScrollPane spJson= new JScrollPane(taJson);
				
				JLabel lbJs = new JLabel("JavaScript");
				JButton btnCopyJsToClipboard = new JButton("Copy to Clipboard");
				JTextArea taJs = new JTextArea();
				taJs.setLineWrap(true);//激活自动换行功能   
				taJs.setWrapStyleWord(true);//激活断行不断字功能  
				taJs.setEditable(false);
				taJs.setText(strJs);
				JScrollPane spJs= new JScrollPane(taJs);
				
				Panel.add(lbDomain,new GBC(0,0,1,1).setFill(GBC.HORIZONTAL).setIpad(10, 10).setInsets(5));
				Panel.add(tfDomain,new GBC(1,0,1,1).setFill(GBC.HORIZONTAL).setWeight(100, 0));
				Panel.add(btnConvert,new GBC(2,0,1,1).setFill(GBC.HORIZONTAL).setInsets(5));
				
				Panel.add(lbRaw,new GBC(0,2,1,1).setFill(GBC.HORIZONTAL).setIpad(10, 10).setInsets(5));
				Panel.add(btnCopyRawToClipboard,new GBC(2,2,1,1).setInsets(5));
				Panel.add(spRaw,new GBC(0,3,3,5).setFill(GBC.BOTH).setIpad(100, 80).setWeight(100,0).setInsets(5));
				
				Panel.add(lbJson,new GBC(0,9,1,1).setFill(GBC.HORIZONTAL).setIpad(10, 10).setInsets(5));
				Panel.add(btnCopyJsonToClipboard,new GBC(2,9,1,1).setInsets(5));
				Panel.add(spJson,new GBC(0,10,3,5).setFill(GBC.BOTH).setIpad(100, 80).setWeight(100,0).setInsets(5));
				
				Panel.add(lbJs,new GBC(0,16,1,1).setFill(GBC.HORIZONTAL).setIpad(10, 10).setInsets(5));
				Panel.add(btnCopyJsToClipboard,new GBC(2,16,1,1).setInsets(5));
				Panel.add(spJs,new GBC(0,17,3,5).setFill(GBC.BOTH).setIpad(100, 80).setWeight(100,0).setInsets(5));
				
				Gui.this.getContentPane().add(Panel);
				//Gui.this.add(Panel,BorderLayout.CENTER);
				
				btnCopyRawToClipboard.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						setSysClipboardText(taRaw.getText());
					}
				});
				
				btnCopyJsonToClipboard.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						setSysClipboardText(taJson.getText());
					}
				});

				btnCopyJsToClipboard.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						setSysClipboardText(taJs.getText());
					}
				});
				
				btnConvert.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						strCookie = taRaw.getText();
						try{
							strDomain = tfDomain.getText();
							strJson = ToJson(strCookie);
							strJs = ToJs(strCookie);
							taJson.setText(strJson);
							taJs.setText(strJs);
						}catch (Exception e1) {
							JOptionPane.showMessageDialog(Gui.this,e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				});
			}
		});
	}
	public static void main(String[] args) {
		//String cookie = "gwid=f5cfc41282bde500a21d71bbbc0ebd3f; isatt=1; isbind=1; ishn=1; isok=1; target=WX; Hm_lpvt_187b1ec211aed4d0ef690293a1ca9fb8=1523506782; Hm_lvt_187b1ec211aed4d0ef690293a1ca9fb8=1523499710; Hm_lpvt_7209c6479c497d8fc9ca601041416326=1523506754; Hm_lvt_7209c6479c497d8fc9ca601041416326=1523506754; od=619FB2F6-CB53-4D07-B09E-9E1635DBD6F6; phonenum=13755020277; koa.sid=ZjDU-E5f4-HM9gcmGj-OmhfqtvIFgrbi; lastUrl=%2Fwebsite%2FpersonalHome%2Fnew%2Findex%3Fcode%3D061GSrkA081Uyg1tzKiA03c8kA0GSrk6%26state%3D; CNZZDATA1261663426=2048276297-1523493469-%7C1523493469; a34b7f54afa06d53_gr_cs1=oJAS9uI5atE2ONp7juVFye0xOeiA; a34b7f54afa06d53_gr_last_sent_cs1=oJAS9uI5atE2ONp7juVFye0xOeiA; a34b7f54afa06d53_gr_last_sent_sid_with_cs1=4533a4a0-7e77-4bcb-8570-7d68f6de9d9b; gr_user_id=3fb8b989-e85e-4db3-9eff-f748ca797a70; UM_distinctid=162b79b5a9341f-09b59148cc94328-4e721266-3d10d-162b79b5a94385";
		//System.out.println(Gui.ToJson(cookie));
		System.out.println("CookiePorter");
	}
	
	public String ToJson(String strCookie){
		String strJson = null;
		String[] listCookie  = strCookie.split(";");
		List<Map> listJson = new ArrayList<>();
		for (String cookie:listCookie) {
			Map map = new HashMap();
			map.put("domain", strDomain);
			map.put("path","/");
			String[] c = cookie.split("=");
			map.put(c[0], c[1]);
			listJson.add(map);
		}
		strJson = JSON.toJSONString(listJson); 
		return strJson;
	}
	
	private String ToJs(String strCookie){
		String strJs = null;
		String[] listCookie  = strCookie.split(";");
		StringBuffer sb = new StringBuffer();
		for (String cookie:listCookie) {
			sb.append(String.format("document.cookie =\"%s\";",cookie));
		}
		strJs = sb.toString();
		return strJs;
	}
	
	public static void setSysClipboardText(String writeMe) {  
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();  
        Transferable tText = new StringSelection(writeMe);  
        clip.setContents(tText, null);  
    } 
}
