package bpm.fwr.api.beans.Constants;

import java.util.HashMap;

public final class Colors {
	public static HashMap<String, String> colors = new HashMap<String, String>();

	public static final String DEFAULT = "default";
	public static final String BLACK = "black";
	public static final String WHITE = "white";
	public static final String GRAY = "gray";
	public static final String BLUE = "blue";
	public static final String RED = "red";
	public static final String YELLOW = "yellow";
	public static final String GREEN = "green";
	public static final String MARRON_CLAIR = "marron clair";
	public static final String MARRON = "marron";
	public static final String MARRON_FONCE = "marron fonce";
	public static final String JAUNE_FWR = "jaune fwr";
	
	public static final String BLACK_VALUE = "0,0,0";
	public static final String WHITE_VALUE = "255,255,255";
	public static final String GRAY_VALUE = "128,128,128";
	public static final String BLUE_VALUE = "0,0,255";
	public static final String RED_VALUE = "255,0,0";
	public static final String YELLOW_VALUE = "255,255,0";
	public static final String GREEN_VALUE = "0,255,0";
	public static final String MARRON_CLAIR_VALUE = "251,241,208";
	public static final String MARRON_VALUE = "220,188,119";
	public static final String MARRON_FONCE_VALUE = "126,51,0";
	public static final String JAUNE_FWR_VALUE = "255,181,21";
	
	static {

		colors.put(Colors.BLACK, Colors.BLACK_VALUE);
		colors.put(Colors.WHITE, Colors.WHITE_VALUE);
		colors.put(Colors.GRAY, Colors.GRAY_VALUE);
		colors.put(Colors.BLUE, Colors.BLUE_VALUE);
		colors.put(Colors.RED, Colors.RED_VALUE);
		colors.put(Colors.YELLOW, Colors.YELLOW_VALUE);
		colors.put(Colors.GREEN, Colors.GREEN_VALUE);
		colors.put(Colors.MARRON_CLAIR, MARRON_CLAIR_VALUE);
		colors.put(Colors.MARRON, MARRON_VALUE);
		colors.put(Colors.MARRON_FONCE, MARRON_FONCE_VALUE);
		colors.put(Colors.JAUNE_FWR, JAUNE_FWR_VALUE);
	}
	
	public static final String[] getColorsArray() {
		return new String[]{BLACK,WHITE,GRAY,BLUE,RED,YELLOW,GREEN};
	}
	
	public static final String[] getColorsArrayWithDefault() {
		return new String[]{DEFAULT,BLACK,WHITE,GRAY,BLUE,RED,YELLOW,GREEN};
	}
	
}
