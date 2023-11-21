package bpm.vanilla.starter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class VanillaConfUpdater {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String s = System.getProperty("bpm.vanilla.configurationFile");
		if (s == null ||s.equals("")){
			System.out.println("property bpm.vanilla.configurationFile not set, cannot update paths.");
			System.exit(0);
		}
		
		System.out.println("ConfigFile = " + s);
		File f = new File(s);

		if (!f.exists()){
			System.out.println("The file " + f.getAbsolutePath() + " does not exist, cannot update paths.");
			System.exit(0);
		}
		
		
		if (!f.isFile()){
			System.out.println("The file " + f.getAbsolutePath() + " is a folder. Specify a valid configurationFile.");
			System.exit(-1);
		}
		
		String currentPath = new File(".").getAbsolutePath();
		currentPath = currentPath.replace("\\", "/");
		
		System.out.println("Current Path = " + currentPath);
		
		StringBuilder content = new StringBuilder();
		
		try {
			System.out.println("Reading VanillaProperties File = " + f.getCanonicalPath());
		} catch (IOException e1) {
			
			e1.printStackTrace();
			throw e1;
		}
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String buf = "";
			
			
			
			while ((buf = br.readLine()) != null){
				
				if (buf.startsWith("bpm.vanilla.norparena.kmlFilesLocation=")){
					content.append("bpm.vanilla.norparena.kmlFilesLocation=");
					content.append(currentPath);
					content.append("/webapps/vanilla_files/vanilla_kml");
					
					System.out.println("set bpm.vanilla.norparena.kmlFilesLocation=" + currentPath + "/webapps/vanilla_files/vanilla_kml");
				}
				else if (buf.startsWith("bpm.vanilla.norparena.fusionMapFilesLocation=")){
					content.append("bpm.vanilla.norparena.fusionMapFilesLocation=");
					content.append(currentPath);
					content.append("/webapps/vanilla_files/vanilla_maps");
					
					System.out.println("set bpm.vanilla.norparena.fusionMapFilesLocation=" + currentPath + "/webapps/vanilla_files/vanilla_maps");
				}
				else{
					content.append(buf);
					
				}
				content.append("\n");
			}
			
			br.close();
			
			
			System.out.println("Overriding file " + f.getAbsolutePath());
			PrintWriter pw = new PrintWriter(f);
			
			pw.write(content.toString());
			pw.close();
			System.out.println("Updated ConfigurationFile");
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
	}
}
