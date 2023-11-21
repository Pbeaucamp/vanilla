import java.io.File;

public class BirtPluginsConfigurator {

	public static void main(String[] args) {
		File f = new File("D:\\DATA\\Vanilla\\7\\v0.4\\Softs\\eclipse\\plugins");
		
		StringBuilder b = new StringBuilder();
		
		for(String fname : f.list()) {
			if(fname.startsWith("bpm.") || fname.startsWith("freemetrics") || fname.startsWith("admin")) {
				b.append(",reference\\:file\\:");
				b.append(fname);
				b.append("@4");
			}
		}
		
		b.append(",reference\\:file\\:org.apache.log4j_1.2.15.v201012070815.jar@4");
		
		System.out.println(b.toString());
	}

}
