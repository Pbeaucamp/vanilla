/**
 * 
 */
package bpm.freemetrics.api.treebeans;

import java.util.List;

import bpm.freemetrics.api.organisation.dashOrTheme.SubTheme;
import bpm.freemetrics.api.organisation.dashOrTheme.Theme;

/**
 * @author Belgarde
 *
 */
public class TreeThm {

	Theme theme;
	
	List<SubTheme> sthms;

	/**
	 * @return the theme
	 */
	public Theme getTheme() {
		return theme;
	}

	/**
	 * @param theme the theme to set
	 */
	public void setTheme(Theme theme) {
		this.theme = theme;
	}

	/**
	 * @return the sthms
	 */
	public List<SubTheme> getSthms() {
		return sthms;
	}

	/**
	 * @param sthms the sthms to set
	 */
	public void setSthms(List<SubTheme> sthms) {
		this.sthms = sthms;
	}
}
