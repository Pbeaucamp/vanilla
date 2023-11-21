package bpm.faweb.shared.utils;

import com.google.gwt.user.client.ui.Label;

public final class Colors {

	public static final String[] COLORS = { "BLACK", "WHITE", "GRAY", "BLUE", "RED", "YELLOW", "GREEN" };
	public static boolean defined = false;

	public static void setBackgroundColor(Label lbl, String color) {

		for (int k = 0; k < COLORS.length; k++) {
			if (color.equalsIgnoreCase(COLORS[k])) {
				lbl.addStyleName("main");
				lbl.addStyleName(COLORS[k]);
				defined = true;
			}
		}

		if (!defined) {
			lbl.addStyleName("ItemValue");
			defined = false;
		}

	}

}
