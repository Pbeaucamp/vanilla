package bpm.fd.runtime.engine.utils;

import java.awt.Color;

public class ColorParser {

	public static Color convert(String value){
		
		try{
			String r = value.substring(0, 2);
			String g = value.substring(2, 4);
			String b = value.substring(4);
			
			int[] rgb = new int[3];
			rgb[0] = Integer.parseInt(r, 16);
			rgb[1] = Integer.parseInt(g, 16);
			rgb[2] = Integer.parseInt(b, 16);
			
			return new Color(rgb[0], rgb[1], rgb[2]);
		}catch(Exception ex){
			return Color.white;
		}
		
	}
}
