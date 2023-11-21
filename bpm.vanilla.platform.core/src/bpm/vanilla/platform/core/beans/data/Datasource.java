package bpm.vanilla.platform.core.beans.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Datasource implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	private String name;
	private DatasourceType type;
	private String model;
	private IDatasourceObject object;
	private int idAuthor;
	
	private List<DatabaseTable> tables;
	
	private List<Dataset> datasets = new ArrayList<Dataset>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DatasourceType getType() {
		return type;
	}

	public void setType(DatasourceType type) {
		this.type = type;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public IDatasourceObject getObject() {
		return object;
	}

	public void setObject(IDatasourceObject object) {
		this.object = object;
	}

	public int getIdAuthor() {
		return idAuthor;
	}

	public void setIdAuthor(int idAuthor) {
		this.idAuthor = idAuthor;
	}
	
	public List<DatabaseTable> getTables() {
		return tables;
	}
	
	public void setTables(List<DatabaseTable> tables) {
		this.tables = tables;
	}

	@Override
	public String toString() {
		return getName();
	}

	public List<Dataset> getDatasets() {
		return datasets;
	}

	public void setDatasets(List<Dataset> datasets) {
		this.datasets = datasets;
	}
	
	@Override
	public boolean equals(Object obj) {
		return id == ((Datasource)obj).getId();
	}
}
