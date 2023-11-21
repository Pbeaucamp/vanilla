package bpm.data.viz.core.preparation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import bpm.data.viz.core.preparation.PreparationRule.RuleType;
import bpm.vanilla.map.core.design.MapDataSet;
import bpm.vanilla.map.core.design.MapInformation;
import bpm.vanilla.map.core.design.MapVanilla;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.data.Dataset;

@Entity
@Table(name = "data_preparation")
public class DataPreparation implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column(name = "id")
	private int id;

	@Column(name = "dpname")
	private String name = "Nouvelle DataPreparation";

	@Column(name = "description")
	private String description;

	@Column(name = "creationDate")
	private Date creationDate = new Date();

	@Column(name = "datasetId")
	private int datasetId;

	@Column(name = "userId")
	private int userId;

	@Column(name = "ruleModel", length = 10000000)
	private String ruleModel;

	@Transient
	private Dataset dataset;

	@Transient
	private List<PreparationRule> rules = new ArrayList<>();

	@Transient
	private User user;

	@Transient
	private MapVanilla map;

	@Column(name = "mapId")
	private Integer mapId;
	
	@Transient
	private MapDataSet mapDataset;
	
	@Column(name = "mapDatasetId")
	private Integer mapDatasetId;

	@Column(name = "mapModel", length = 10000000)
	private String mapModel;
	
	@Transient
	private MapInformation mapInformation;

	@Column(name = "linkedItemsModel", length = 10000000)
	private String linkedItemsModel;
	
	@Transient
	private List<LinkItem> linkedItems = new ArrayList<LinkItem>();

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

	public Dataset getDataset() {
		return dataset;
	}

	public void setDataset(Dataset dataset) {
		if(dataset != null) {
			this.datasetId = dataset.getId();
		}
		this.dataset = dataset;
	}

	public List<PreparationRule> getRules() {
		return rules;
	}

	public void addRule(PreparationRule rule) {
		if(rule.getType() == RuleType.SORT) {
			for(PreparationRule r : rules) {
				if(r.getType() == RuleType.SORT) {
					r.getColumns().add(rule.getColumn());
					return;
				}
			}
		}
		this.rules.add(rule);
	}

	public void setRules(List<PreparationRule> rules) {
		this.rules = rules;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getDatasetId() {
		return datasetId;
	}

	public void setDatasetId(int datasetId) {
		this.datasetId = datasetId;
	}

	public String getRuleModel() {
		return ruleModel;
	}

	public void setRuleModel(String ruleModel) {
		this.ruleModel = ruleModel;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getNbColumns() {
		int nbCol = dataset.getMetacolumns().size();
		for(PreparationRule r : rules) {
			if(r.isNewColumn()) {
				nbCol++;
			}
		}
		return nbCol;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + datasetId;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		DataPreparation other = (DataPreparation) obj;
		if(datasetId != other.datasetId)
			return false;
		if(id != other.id)
			return false;
		return true;
	}

	public MapVanilla getMap() {
		return map;
	}

	public void setMap(MapVanilla map) {
		this.map = map;
		if(map != null) {
			mapId = map.getId();
		}
	}

	public Integer getMapId() {
		return mapId;
	}

	public void setMapId(Integer mapId) {
		this.mapId = mapId;
	}

	public MapDataSet getMapDataset() {
		return mapDataset;
	}

	public void setMapDataset(MapDataSet mapDataset) {
		this.mapDataset = mapDataset;
		if(mapDataset != null) {
			mapDatasetId = mapDataset.getId();
		}
	}

	public Integer getMapDatasetId() {
		return mapDatasetId;
	}

	public void setMapDatasetId(Integer mapDatasetId) {
		this.mapDatasetId = mapDatasetId;
	}
	
	public MapInformation getMapInformation() {
		return mapInformation;
	}
	
	public void setMapInformation(MapInformation mapInformation) {
		this.mapInformation = mapInformation;
	}
	
	public String getMapModel() {
		return mapModel;
	}
	
	public void setMapModel(String mapModel) {
		this.mapModel = mapModel;
	}
	
	public List<LinkItem> getLinkedItems() {
		return linkedItems;
	}
	
	public void setLinkedItems(List<LinkItem> linkedItems) {
		this.linkedItems = linkedItems;
	}
	
	public String getLinkedItemsModel() {
		return linkedItemsModel;
	}
	
	public void setLinkedItemsModel(String linkedItemsModel) {
		this.linkedItemsModel = linkedItemsModel;
	}

}
