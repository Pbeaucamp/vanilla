import java.io.File;

public class CleanerAklabox {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File root = new File("C:/BPM/workspaces/workspace_aklabox");

		for (String s : root.list()) {
			if (!s.contains("web") && !s.equals("bpm.fwr") 
					&& !s.equals("bpm.tsbn.oru_CMA") && !s.equals("bpm.update.manager") 
					&& !s.equals("bpm.vanilla.feedback") && !s.equals("bpm.vanilla.workplace") 
					&& !s.equals("VanillaPortail") && !s.equals("vanilla_build") 
					&& !s.equals("aklabox_build") && !s.equals("vanilla_clean")) {

				File folder = new File(root, s);
				for (String n : folder.list()) {
					if (n.equals("@dot.log") || n.equals("build.xml") || n.equals("javaCompiler...args")) {
						File f = new File(folder, n);
						System.out.println("Delete " + f.getAbsolutePath());
						f.delete();
					}
					else if (n.equals("@dot")) {
						File f = new File(folder, n);
						System.out.println("Delete " + f.getAbsolutePath());
						deleteDirectory(f);
						System.out.println(" -> Ok");
					}
				}
			}
		}

	}

	public static void deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				}
				else {
					files[i].delete();
				}
			}
		}
		path.delete();
	}
}
