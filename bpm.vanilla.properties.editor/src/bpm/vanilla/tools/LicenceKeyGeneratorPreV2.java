package bpm.vanilla.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class LicenceKeyGeneratorPreV2 {
	private String version;
	private String name;
	private String shuffleKey = "c5257713a547dc9156fccd0bd9ba68d7";
	
	public String getShuffleKey(){
		return shuffleKey;
	}
	
	public String generateKey(String year, String month, String version, String name){
		this.version = version;
		this.name = name;
		
		
		String tmp = month + version + year + name;
		StringBuffer buf = new StringBuffer();
		
		for(int i = 0; i < tmp.length(); i++){
			buf.append(tmp.charAt(i));
			buf.append(shuffleKey.charAt(i % shuffleKey.length()));
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
		LicenceKeyGeneratorPreV2 gen = new LicenceKeyGeneratorPreV2();
		String key = MD5Helper.encode(gen.generateKey("2010", "6", "3", "Parkeon"));
		System.out.println(key);
		gen.generateLicenceFile(key);
	}
	
	
	public void generateLicenceFile(String key){
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("d:\\Licence_base.txt")));
			
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
			
			
			PrintWriter pw = new PrintWriter("d:\\Licence_V2.txt");
			pw.write(buf.toString());
			pw.close();
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}

