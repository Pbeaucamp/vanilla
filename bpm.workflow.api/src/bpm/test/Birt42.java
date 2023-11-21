package bpm.test;

import java.io.File;

public class Birt42 {

	private static boolean IS_FOR_RUNTIME = false;

	public static void main(String[] args) {
//		File f = new File("D:\\DATA\\Vanilla\\7\\OSGI\\vanilla-jetty-7_v0.1\\vanilla\\VanillaRuntime");
		File f = new File("D:\\DATA\\Vanilla\\7\\GA\\Softs\\eclipse\\plugins");

		StringBuilder b = new StringBuilder();

		for (String fname : f.list()) {
			if (IS_FOR_RUNTIME) {
				b.append(",");
				b.append(fname);
				b.append("@start");
			}
			else {
				if (fname.startsWith("bpm.") || fname.startsWith("freemetrics") || fname.startsWith("admin")) {
					b.append(",reference\\:file\\:");
					b.append(fname);
					b.append("@4");
				}
			}
		}

		if (!IS_FOR_RUNTIME) {
			//Birt 4.3
//			b.append(",reference\\:file\\:org.apache.log4j_1.2.13.v200903072027.jar@4");
			//Birt 4.7
			b.append(",reference\\:file\\:org.apache.log4j_1.2.15.v201012070815.jar@4");
		}

		System.out.println(b.toString());
	}

}
