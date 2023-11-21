package bpm.vanilla.platform.core.repository;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table (name = "rpy_datasource_impact")
public class DataSourceImpact {
	
	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column(name = "item_id")
	private int directoryItemId;
	
	@Column(name = "datasource_id")
	private int datasourceId;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getDirectoryItemId() {
		return directoryItemId;
	}
	public void setDirectoryItemId(int directoryItemId) {
		this.directoryItemId = directoryItemId;
	}
	public int getDatasourceId() {
		return datasourceId;
	}
	public void setDatasourceId(int datasourceId) {
		this.datasourceId = datasourceId;
	}
}
