package bpm.vanilla.workplace.core.datasource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import bpm.vanilla.workplace.core.datasource.IDatasourceType.DatasourceType;

@Entity
@Table (name = "rpy_relational_datasources")
@Inheritance(strategy=InheritanceType.JOINED)
public class AbstractDatasource implements IDatasource {

	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "dbId")
	private int dbId;
	
	@Column(name = "itemId")
	private int itemId;
	
	@Column(name = "id")
	private String id;
	
	@Column(name = "ds_name")
	private String name;
	
	@Transient
	private DatasourceType type;
	
	@Column(name = "extensionId")
	private String extensionId;
	
	@Column(name = "out_type")
	private boolean out = false;
	
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}
	
	public void setExtensionId(String extensionId){
		this.extensionId = extensionId;
	}

	@Override
	public String getExtensionId() {
		return extensionId;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setType(DatasourceType type) {
		this.type = type;
	}

	@Override
	public DatasourceType getType() {
		return type;
	}
	
	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public boolean isOut() {
		return out;
	}

	public void setOut(boolean out) {
		this.out = out;
	}

	public int getDbId() {
		return dbId;
	}

	public void setDbId(int dbId) {
		this.dbId = dbId;
	}
	
}
