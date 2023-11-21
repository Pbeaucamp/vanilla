package bpm.vanilla.workplace.core.datasource;

import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table (name = "rpy_relational_datasources_fd")
@PrimaryKeyJoinColumn(name="dbId")
public class ModelDatasourceFD extends AbstractDatasource {
	
	@Column(name = "modelRepositoryItemId")
	private String modelRepositoryItemId;
	
	@Column(name = "dictionaryRepositoryItemId")
	private String dictionaryRepositoryItemId;
	
	@Transient
	private HashMap<Integer, Integer> dependancies = new HashMap<Integer, Integer>();
	
	public ModelDatasourceFD() { }

	public void setModelRepositoryItemId(String modelRepositoryItemId) {
		this.modelRepositoryItemId = modelRepositoryItemId;
	}

	public String getModelRepositoryItemId() {
		return modelRepositoryItemId;
	}

	public void setDictionaryRepositoryItemId(String dictionaryRepositoryItemId) {
		this.dictionaryRepositoryItemId = dictionaryRepositoryItemId;
	}

	public String getDictionaryRepositoryItemId() {
		return dictionaryRepositoryItemId;
	}

	public void setDependancies(HashMap<Integer, Integer> dependancies) {
		this.dependancies = dependancies;
	}

	public HashMap<Integer, Integer> getDependancies() {
		return dependancies;
	}
}
