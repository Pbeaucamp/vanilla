package bpm.vanilla.platform.core.repository;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import bpm.vanilla.platform.core.beans.data.Datasource;

/**
 * This class allows to link a repository item with a metadata table to allows
 * the user to modify values (initially from the viewer)
 */
@Entity
@Table(name = "rpy_item_metadata_table_link")
public class ItemMetadataTableLink implements IRepositoryObject {

	private static final long serialVersionUID = 4575157541160639952L;

	@Id
	@GeneratedValue(generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	@Column(name = "id")
	private int id;

	@Column(name = "item_id")
	private int itemId;

	@Column(name = "name")
	private String name;

	@Column(name = "datasource_id")
	private int datasourceId;

	@Column(name = "table_name")
	private String tableName;

	@Column(name = "creation_date")
	private Date creationDate;
	
	@Transient
	private Datasource datasource;

	public ItemMetadataTableLink() {
	}

	@Override
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public int getDatasourceId() {
		return datasourceId;
	}

	public void setDatasourceId(int datasourceId) {
		this.datasourceId = datasourceId;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Datasource getDatasource() {
		return datasource;
	}
	
	public void setDatasource(Datasource datasource) {
		this.datasource = datasource;
	}
	
}
