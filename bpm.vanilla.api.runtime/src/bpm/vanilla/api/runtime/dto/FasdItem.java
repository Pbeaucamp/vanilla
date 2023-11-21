package bpm.vanilla.api.runtime.dto;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.fasd.inport.DigesterFasd;
import org.fasd.olap.FAModel;
import org.fasd.olap.ICube;

import bpm.vanilla.platform.core.repository.RepositoryItem;

public class FasdItem extends RepositoryComponent {
	public List<FasdCube> children;

	public FasdItem(RepositoryItem it, String fasdxml) {
		super(it.getId(), it.getName(), false,"fasd");
		loadCubes(it, fasdxml);
	}

	public List<FasdCube> getChildren() {
		return children;
	}

	public void loadCubes(RepositoryItem it, String fasdxml) {

		children = new ArrayList<FasdCube>();
		// String xml = "";
		// try {
		// xml = api.getRepositoryService().loadModel(it);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		DigesterFasd dig = null;
		try {
			dig = new DigesterFasd(IOUtils.toInputStream(fasdxml));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		FAModel faModel = dig.getFAModel();
		List<ICube> cubes = faModel.getCubes();
		for (ICube cube : cubes) {
			children.add(new FasdCube(this.getId(), cube.getName()));
		}

	}
}
