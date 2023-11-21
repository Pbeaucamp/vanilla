package org.fasd.inport;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.apache.commons.digester3.Digester;
import org.fasd.utils.Repository;

public class DigesterRepository {

	private Repository repository;

	public DigesterRepository(String path) throws FileNotFoundException, Exception {
		FileReader f = new FileReader(path);

		Digester dig = new Digester();
		dig.setValidating(false);

		String root = "repository"; //$NON-NLS-1$

		dig.addObjectCreate(root, Repository.class);

		dig.addCallMethod(root + "/project", "addProject", 2); //$NON-NLS-1$ //$NON-NLS-2$
		dig.addCallParam(root + "/project/name", 0); //$NON-NLS-1$
		dig.addCallParam(root + "/project/file", 1); //$NON-NLS-1$

		try {
			repository = (Repository) dig.parse(f);

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	public Repository getRepository() {
		return repository;
	}
}
