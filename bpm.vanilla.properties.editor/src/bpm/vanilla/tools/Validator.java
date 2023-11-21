package bpm.vanilla.tools;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;







public class Validator {
	
	public static final int LICENCE_ANONYMOUS = 0;
	public static final int LICENCE_IDENTIFIED = 1;
	public static final int LICENCE_SUPPORTED = 2;
	public static final int LICENCE_GOLD = 3;
	public static final String[] LICENCE_TYPES = new String[]{"Anonymous", "Identified", "Supported", "Gold"};
	public static final int GRANTED = 0;
	public static final int EXPIRED = 1;
	public static final int NEARLY_EXPIRED = 2;
	public static final int INVALID_KEY = 3;
	
	private String key = null;
	private String company;
	protected int year;
	protected int month;
	
	protected int licenceType = -1;
	private String vanillaUrl;
	
	public Validator(String vanillaUrl){
		this.vanillaUrl = vanillaUrl;
	}
	
	
	public String updateLicence(String key, String company) throws Exception{
		URL url = new URL(vanillaUrl + "/Validation");
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();
		sock.setDoInput(true);
		sock.setDoOutput(true);
		sock.setRequestMethod("POST");
		sock.setRequestProperty("Content-type", "text/xml;charset=UTF-8");

		PrintWriter pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(), "UTF-8"));

		String toSend = "<update><key>" + key + "</key><company>" + company + "</company></update>";
		
		pw.write(toSend);
		pw.close();
		
		
		InputStream is = sock.getInputStream();
		String result = IOUtils.toString(is, "UTF-8");
		is.close();
		//
		sock.disconnect();
		//error catching
		if (result.contains("<error>")){
			throw new Exception(result.substring(result.indexOf("<error>") + 7, result.indexOf("</error>")));
		}
		Document d = DocumentHelper.parseText(result);
		Element root = d.getRootElement();
		return root.getStringValue();
	}
	
	public int check()throws Exception{

		URL url = new URL(vanillaUrl + "/Validation");
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();
		sock.setDoInput(true);
		sock.setDoOutput(true);
		sock.setRequestMethod("POST");
		sock.setRequestProperty("Content-type", "text/xml;charset=UTF-8");

		PrintWriter pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(), "UTF-8"));

		String toSend = "<check></check>";
		
		pw.write(toSend);
		pw.close();
		
		
		InputStream is = sock.getInputStream();
		String result = IOUtils.toString(is, "UTF-8");
		is.close();
		//
		sock.disconnect();
		//error catching
		if (result.contains("<error>")){
			throw new Exception(result.substring(result.indexOf("<error>") + 7, result.indexOf("</error>")));
		}
		
		try{
			Document d = DocumentHelper.parseText(result);
			Element root = d.getRootElement();
			
			for(int i = 0; i < Validator.LICENCE_TYPES.length; i++){
				if (Validator.LICENCE_TYPES[i].equals(root.element("licenceType").getStringValue())){
					licenceType = i ;
					break;
				}
			}
			
			;
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM");
			Date date = sdf.parse(root.element("expirationDate").getStringValue());
			
			year = date.getYear() + 1900;
			month = date.getMonth();
			key = root.element("key").getStringValue();
			company = root.element("company").getStringValue();
			return Integer.parseInt(root.element("result").getStringValue());
			
			
			
			
		}catch(Exception ex){
			ex.printStackTrace();
			throw new Exception("Error when parsing server response:" + ex.getMessage());
		}
		
		
	}

	public int getLicenceType(){
		return licenceType;
	}
	
	public String getKey() {
		return key;
	}

	public String getCompany() {
		return company;
	}
	
	public String getExpiratrionDate(){
		
		if (year != -1 && month != -1){
			return "" + year + "/" + new DecimalFormat("00").format((month + 1));
		}
		return "expired";
	}
}
