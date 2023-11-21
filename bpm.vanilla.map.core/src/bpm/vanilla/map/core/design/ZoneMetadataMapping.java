package bpm.vanilla.map.core.design;

import java.io.Serializable;

public class ZoneMetadataMapping implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	private int metadataId;
	private int datasetId;
	private String datastreamName;

	private String zoneId;
	private String metadataElementId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMetadataId() {
		return metadataId;
	}

	public void setMetadataId(int metadataId) {
		this.metadataId = metadataId;
	}

	public int getDatasetId() {
		return datasetId;
	}

	public void setDatasetId(int datasetId) {
		this.datasetId = datasetId;
	}

	public String getDatastreamName() {
		return datastreamName;
	}

	public void setDatastreamName(String datastreamName) {
		this.datastreamName = datastreamName;
	}

	public String getZoneId() {
		return zoneId;
	}

	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}

	public String getMetadataElementId() {
		return metadataElementId;
	}

	public void setMetadataElementId(String metadataElementId) {
		this.metadataElementId = metadataElementId;
	}

}
