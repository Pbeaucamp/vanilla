package bpm.gwt.commons.shared.repository;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class PortailItemKpiTheme extends PortailRepositoryItem {

	private static final long serialVersionUID = 1L;
	
	private int themeId;
	
	public PortailItemKpiTheme() { }
	
	public PortailItemKpiTheme(RepositoryItem item, String typeName) {
		super(item, typeName);
	}
	
	@Override
	public int getType() {
		return IRepositoryApi.KPI_THEME;
	}
	
	public void setThemeId(int themeId) {
		this.themeId = themeId;
	}
	
	public int getThemeId() {
		return themeId;
	}
}
