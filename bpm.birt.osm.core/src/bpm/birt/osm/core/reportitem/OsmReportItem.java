package bpm.birt.osm.core.reportitem;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.model.api.extension.ReportItem;

import com.thoughtworks.xstream.XStream;

public class OsmReportItem extends ReportItem {

	public static final String EXTENSION_NAME = "OsmMapItem";
	public static final String VANILLA_URL = "VanillaUrl";
	public static final String VANILLA_LOGIN = "VanillaLogin";
	public static final String VANILLA_PASSWORD = "VanillaPassword";
	public static final String MAP_ZONE_COLUMN = "ZoneColumn";
	public static final String MAP_VALUE_COLMUN = "ValueColumn";

	public static final String MAP_ID = "MapId";

	public static final String SERIE_LIST = "SerieList";

	private ExtendedItemHandle modelHandle;
	
	private XStream xstream = new XStream();

	OsmReportItem(ExtendedItemHandle modelHandle) {
		this.modelHandle = modelHandle;
	}

	public void setVanillaUrl(String vanillaUrl) throws SemanticException {
		this.modelHandle.setProperty(VANILLA_URL, vanillaUrl);
	}

	public void setVanillaLogin(String vanillaLogin) throws SemanticException {
		this.modelHandle.setProperty(VANILLA_LOGIN, vanillaLogin);
	}

	public void setVanillaPassword(String vanillaPassword) throws SemanticException {
		this.modelHandle.setProperty(VANILLA_PASSWORD, vanillaPassword);
	}

	public void setMapId(int mapId) throws SemanticException {
		this.modelHandle.setProperty(MAP_ID, mapId);
	}

	public void setSerieList(List series) throws SemanticException {
		List xstreamSeries = new ArrayList();
		for(Object o : series) {
			xstreamSeries.add(xstream.toXML(o));
		}
		this.modelHandle.setProperty(SERIE_LIST, xstreamSeries);
	}

	public void setZoneColumn(String zoneColumn) throws SemanticException {
		this.modelHandle.setProperty(MAP_ZONE_COLUMN, zoneColumn);
	}

	public void setValueColumn(String valueColumn) throws SemanticException {
		this.modelHandle.setProperty(MAP_VALUE_COLMUN, valueColumn);
	}

	public String getVanillaUrl() {
		return this.modelHandle.getStringProperty(VANILLA_URL);
	}

	public String getVanillaLogin() {
		return this.modelHandle.getStringProperty(VANILLA_LOGIN);
	}

	public String getVanillaPassword() {
		return this.modelHandle.getStringProperty(VANILLA_PASSWORD);
	}

	public int getMapId() {
		return this.modelHandle.getIntProperty(MAP_ID);
	}

	public List getSerieList() {
		List series = this.modelHandle.getListProperty(SERIE_LIST);
		if(series == null) {
			return new ArrayList();
		}
		List xstreamSeries = new ArrayList();
		for(Object o : series) {
			xstreamSeries.add(xstream.fromXML((String) o));
		}
		return xstreamSeries;
	}

	public String getZoneColumn() {
		return this.modelHandle.getStringProperty(MAP_ZONE_COLUMN);
	}

	public String getValueColumn() {
		return this.modelHandle.getStringProperty(MAP_VALUE_COLMUN);
	}
}
