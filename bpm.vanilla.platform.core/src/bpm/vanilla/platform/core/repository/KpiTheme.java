package bpm.vanilla.platform.core.repository;

import java.io.Serializable;

public class KpiTheme implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int themeId;
	
	public KpiTheme() { }
	
	public KpiTheme(int themeId) {
		this.themeId = themeId;
	}
	
	public int getThemeId() {
		return themeId;
	}
}
