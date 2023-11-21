package bpm.fm.api.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import bpm.vanilla.platform.core.beans.data.Datasource;

@Entity
@Table (name = "fm_level")
public class Level implements Serializable, HasItemLinked {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column(name = "level_name")
	private String name;
	
	@Column (name = "creation_date")
	private Date creationDate;
	
	@Column(name = "table_name")
	private String tableName;
	
	@Column(name = "column_name")
	private String columnName;
	
	@Column(name = "column_id")
	private String columnId;
	
	@Column (name = "datasource_id")
	private int datasourceId;
	
	@Column (name = "parent_id")
	private int parentId;
	
	@Column (name = "level_index")
	private int levelIndex;
	
	@Column (name = "parent_column_id")
	private String parentColumnId;
	
	@Column (name = "geo_column_id")
	private String geoColumnId;
	
	@Column (name = "map_dataset_id")
	private Integer mapDatasetId;
	
	@Column (name = "linked_item_id")
	private Integer linkedItemId;
	
	@Transient
	private Axis parent;
	
	@Transient
	private Datasource datasource;

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

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnId() {
		return columnId;
	}

	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}

	public int getDatasourceId() {
		return datasourceId;
	}

	public void setDatasourceId(int datasourceId) {
		this.datasourceId = datasourceId;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public int getLevelIndex() {
		return levelIndex;
	}

	public void setLevelIndex(int levelIndex) {
		this.levelIndex = levelIndex;
	}

	public Axis getParent() {
		return parent;
	}

	public void setParent(Axis parent) {
		this.parent = parent;
	}

	public String getDatasourceName() {
		return datasource.getName();
	}

	public Datasource getDatasource() {
		return datasource;
	}

	public void setDatasource(Datasource datasource) {
		this.datasource = datasource;
	}
	
	public String getParentColumnId() {
		return parentColumnId;
	}

	public void setParentColumnId(String parentColumnId) {
		this.parentColumnId = parentColumnId;
	}

	@Override
	public boolean equals(Object obj) {
		return id == ((Level)obj).getId();
	}

	public String getGeoColumnId() {
		return geoColumnId;
	}

	public void setGeoColumnId(String geoColumnId) {
		this.geoColumnId = geoColumnId;
	}

	public Integer getMapDatasetId() {
		return mapDatasetId;
	}

	public void setMapDatasetId(Integer mapDatasetId) {
		this.mapDatasetId = mapDatasetId;
	}
	
	@Override
	public Integer getLinkedItemId() {
		return linkedItemId;
	}
	
	@Override
	public void setLinkedItemId(Integer linkedItemId) {
		this.linkedItemId = linkedItemId;
	}
	
}
