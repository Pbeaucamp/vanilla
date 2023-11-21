package bpm.vanilla.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class LicenceKeyGenerator {

	private String version;
	private String name;
	
	private String[] shuffleKey = new String[]{
			"c5257713a547dc9156fccd0bd9ba68d7",
			"12574361637e182cac7c704a3ca29152",
			"eaa4dc5078017a5c5bebf383ab6f5124",
			"9768feb3fdb1f267b06093bc572952dd"};
	
	public String generateKey(String year, String month, String version, String name, int type){
		this.version = version;
		this.name = name;

		
		String tmp = month + version + year + name;
		StringBuffer buf = new StringBuffer();
		
		for(int i = 0; i < tmp.length(); i++){
			buf.append(tmp.charAt(i));
			buf.append(shuffleKey[type].charAt(i % shuffleKey[type].length()));
		}
		
		return buf.toString();
		
	}
	public  String readline(BufferedReader b, String name)throws IOException{
		String input ="";
		//Buffer is empty at the moment
		while(true){
			System.out.print(name + "<");
			input = b.readLine();
			if ("".equals(input)){
				break;
			}
			
//			System.out.println("Echo>" + input.toUpperCase());
			break;
			
		}
		
		return input;
	}

	//String key = MD5.encode(gen.generateKey("2009", "9", "2", "Ingeniosa"));
	//String key = MD5.encode(gen.generateKey("2009", "9", "2", "Bull"));

	public static void main(String[] args){
		LicenceKeyGenerator gen = new LicenceKeyGenerator();
		String key = MD5Helper.encode(gen.generateKey("2010", "6", "3", "Company Trial", Validator.LICENCE_ANONYMOUS));
		System.out.println(key);
//		gen.generateLicenceFile(key);
	}
	
	
	public void generateLicenceFile(String key){
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("Licence_base.txt")));
			
			String tmp = "";
			StringBuffer buf = new StringBuffer();
			int i = 0;
			
			
			while( (tmp = br.readLine())!= null){
				i ++;
				buf.append(tmp +"\n");
				if (i == 1){
					buf.append("Version " + version + "\n");
					buf.append("Company " + name + "\n");
					buf.append("LicenceKey " + key + "\n");
				}
			}
			
			br.close();
			
			
			PrintWriter pw = new PrintWriter("Licence_supp.txt");
			pw.write(buf.toString());
			pw.close();
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
