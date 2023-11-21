package bpm.vanilla.platform.core.beans.data;

import java.io.Serializable;
import java.util.List;

public class Dataset implements Serializable{

	private static final long serialVersionUID = 1L;

	private int id;
	private String name;
	private int datasourceId;
	private String request;
	private List<DataColumn> metacolumns;
	private boolean isRPackaged;
	private String rPackages;
	private Datasource datasource;
	private int idAuthor;
	private List<DatasetParameter> parameters;
	
	private boolean isRLoaded = false;
	
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
	
	public int getDatasourceId() {
		return datasourceId;
	}
	
	public void setDatasourceId(int datasourceId) {
		this.datasourceId = datasourceId;
	}
	
	public String getRequest() { //SQL request or R-package
		return request;
	}
	
	public void setRequest(String request) {
		this.request = request;
	}

	public List<DataColumn> getMetacolumns() {
		return metacolumns;
	}

	public void setMetacolumns(List<DataColumn> metacolumns) {
		this.metacolumns = metacolumns;
	}

	public Datasource getDatasource() {
		return datasource;
	}

	public void setDatasource(Datasource datasource) {
		this.datasource = datasource;
		this.datasourceId = datasource.getId();
	}

	public boolean getIsRPackaged() {
		return isRPackaged;
	}

	public void setIsRPackaged(boolean isRPackaged) {
		this.isRPackaged = isRPackaged;
	}

	public String getRPackages() {
		return rPackages;
	}

	public void setRPackages(String rPackages) {
		this.rPackages = rPackages;
	}

	public int getIdAuthor() {
		return idAuthor;
	}

	public void setIdAuthor(int idAuthor) {
		this.idAuthor = idAuthor;
	}

	@Override
	public String toString() {
		return getName();
	}

	public List<DatasetParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<DatasetParameter> parameters) {
		this.parameters = parameters;
	}
	
	public boolean getIsRLoaded() {
		return isRLoaded;
	}

	public void setIsRLoaded(boolean isRLoaded) {
		this.isRLoaded = isRLoaded;
	}
	
	@Override
	public boolean equals(Object obj) {
		return id == ((Dataset)obj).getId();
	}
	
	
}
