package org.fasd.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.eclipse.core.runtime.Platform;
import org.fasd.i18N.LanguageText;

public class Repository {
	/**
	 * key is the project name value is the file name
	 */
	private HashMap<String, String> projects = new HashMap<String, String>();

	public Repository() {

	}

	/**
	 * insert the file to the given key. if the key ever exist an exception is
	 * thrown
	 * 
	 * @param name
	 * @param file
	 * @throws Exception
	 */
	public void addProject(String name, String file) throws Exception {
		for (String s : projects.keySet()) {
			if (s.equals(name))
				throw new Exception(LanguageText.Repository_TheProjectAlreadyExsistinTheRepository);
		}

		projects.put(name, file);

	}

	/**
	 * if the given is in the repository, remove it
	 * 
	 * @param name
	 */
	public void removeProject(String name) {
		for (String s : projects.keySet()) {
			if (s.equals(name)) {
				projects.remove(s);
				break;
			}
		}
	}

	public void save() throws IOException {
		StringBuffer buf = new StringBuffer();
		buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n"); //$NON-NLS-1$
		buf.append("<repository>\n"); //$NON-NLS-1$
		for (String s : projects.keySet()) {
			buf.append("    <project>\n"); //$NON-NLS-1$
			buf.append("        <name>" + s + "</name>\n"); //$NON-NLS-1$ //$NON-NLS-2$
			buf.append("        <file>" + projects.get(s) + "</file>\n"); //$NON-NLS-1$ //$NON-NLS-2$
			buf.append("    </project>\n"); //$NON-NLS-1$
		}
		buf.append("</repository>\n"); //$NON-NLS-1$

		FileWriter fw = new FileWriter(Platform.getInstallLocation().getURL().getPath() + "\\datas\\repository.xml"); //$NON-NLS-1$ //$NON-NLS-2$
		fw.write(buf.toString());
		fw.close();

	}
}
