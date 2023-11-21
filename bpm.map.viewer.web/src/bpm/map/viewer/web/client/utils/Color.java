/*
	Copyright 2008 Marco Mustapic
	
    This file is part of Agilar GWT Widgets.

    Agilar GWT Widgets is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Agilar GWT Widgets is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with Agilar GWT Widgets.  If not, see <http://www.gnu.org/licenses/>.
*/

package bpm.map.viewer.web.client.utils;

public class Color
{
	public static void HSVToRGB(float [] hsv, float [] rgb)
	{
		
		float r = 0.0f;
		float g = 0.0f;
		float b = 0.0f;
		float h = hsv[0];
		float s = hsv[1];
		float v = hsv[2];
		
		float hf = h / 60.0f;
		int i = (int) Math.floor(hf);
		float f = hf - i;
		float pv = v * (1 - s);
		float qv = v * (1 - s * f);
		float tv = v * (1 - s * (1 - f));
		
		switch (i)
		{
		// Red is the dominant color
		case 0:
			r = v;
			g = tv;
			b = pv;
			break;
			
			// Green is the dominant color
		case 1:
			r = qv;
			g = v;
			b = pv;
			break;
		case 2:
			r = pv;
			g = v;
			b = tv;
			break;
			
			// Blue is the dominant color
		case 3:
			r = pv;
			g = qv;
			b = v;
			break;
		case 4:
			r = tv;
			g = pv;
			b = v;
			break;
			
			// Red is the dominant color
		case 5:
			r = v;
			g = pv;
			b = qv;
			break;
			
			// Just in case we overshoot on our math by a little, we put these here. Since its a switch it won't slow us down at all to put these here.
		case 6:
			r = v;
			g = tv;
			b = pv;
			break;
		case -1:
			r = v;
			g = pv;
			b = qv;
			break;
		}
		
		rgb[0] = r;
		rgb[1] = g;
		rgb[2] = b;
	}

	public static void RGBToHSV(float [] rgb, float[] hsv)
	{
		float r = rgb[0];
		float g = rgb[1];
		float b = rgb[2];
		float h, s, v;
		
		float min = min3(r, g, b);
		float max = max3(r, g, b);
		
		// calculate value
		v = max; // value
		if (v == 0)
		{
			h = 0;
			s = 0;
		}
		
		// calculate saturation
		s = max - min;
		if (s == 0)
		{
			h = 0;
		}
		
		// calculate hue
		if (max == r)
		{
			h = 0.0f + 60.0f * (g - b);
			if (h < 0.0)
			{
				h += 360.0f;
			}
		}
		else if (max == g)
		{
			h = 120.0f + 60.0f * (b - r);
		}
		else
		/* max == b */{
			h = 240.0f + 60.0f * (r - g);
		}
		
		hsv[0] = h;
		hsv[1] = s;
		hsv[2] = v;
	}

	public static float min3(float v1, float v2, float v3)
	{
		float min;

		min = v1 < v2 ? v1 : v2;
		min = min < v3 ? min : v3;

		return min;
	}

	public static float max3(float v1, float v2, float v3)
	{
		float max;

		max = v1 > v2 ? v1 : v2;
		max = max > v3 ? max : v3;

		return max;
	}
	
	public static String toHex(int [] rgb)
	{
		String hex = "";
		hex += rgb[0]>=16?Integer.toHexString(rgb[0]):"0"+Integer.toHexString(rgb[0]);
		hex += rgb[1]>=16?Integer.toHexString(rgb[1]):"0"+Integer.toHexString(rgb[1]);
		hex += rgb[2]>=16?Integer.toHexString(rgb[2]):"0"+Integer.toHexString(rgb[2]);
		
		return hex;
	}
	
	public static void toInt(float [] frgb, int [] irgb)
	{
		irgb[0] = Math.round(frgb[0] * 255.0f);
		irgb[1] = Math.round(frgb[1] * 255.0f);
		irgb[2] = Math.round(frgb[2] * 255.0f);
	}

	public static void toFloat(int [] irgb, float [] frgb)
	{
		frgb[0] = ((float)irgb[0]) / 255.0f;
		frgb[1] = ((float)irgb[1]) / 255.0f;
		frgb[2] = ((float)irgb[2]) / 255.0f;
	}
	
	public static String getDarkerColor(String hexa) {
		hexa = hexa.replace("#", "");
		String rs = hexa.substring(0, 2);
		String gs = hexa.substring(2, 4);
		String bs = hexa.substring(4, 6);
		
		int r = Integer.parseInt(rs, 16);
		int g = Integer.parseInt(gs, 16);
		int b = Integer.parseInt(bs, 16);
		
//		if(r >= g && r >= b) {
//			g = (int) (g * 0.8) > 255 ? 255 : (int) (g * 0.8);
//			b = (int) (b * 0.8) > 255 ? 255 : (int) (b * 0.8);
//			g = Math.max(Math.min(Math.round(g*95/100), 255), 0);
//			b = Math.max(Math.min(Math.round(b*95/100), 255), 0);
//		}
//		else if(g >= r && g >= b) {
//			r = (int) (r * 0.8) > 255 ? 255 : (int) (r * 0.8);
//			b = (int) (b * 0.8) > 255 ? 255 : (int) (b * 0.8);
//			r = Math.max(Math.min(Math.round(r*95/100), 255), 0);
//			b = Math.max(Math.min(Math.round(b*95/100), 255), 0);
//		}
//		else {
//			r = (int) (r * 0.8) > 255 ? 255 : (int) (r * 0.8);
//			g = (int) (g * 0.8) > 255 ? 255 : (int) (g * 0.8);
			r = Math.max(Math.min(Math.round(r*90/100), 255), 0);
			g = Math.max(Math.min(Math.round(g*90/100), 255), 0);
			b = Math.max(Math.min(Math.round(b*90/100), 255), 0);
//		}
//		r = (int) (r * 0.8) > 255 ? 255 : (int) (r * 0.8);
//		g = (int) (g * 0.8) > 255 ? 255 : (int) (g * 0.8);
//		b = (int) (b * 0.8) > 255 ? 255 : (int) (b * 0.8);
//		Math.max(Math.min(Math.round(r*90/100), 255), 0));
		return "#" + Integer.toHexString(r) + Integer.toHexString(g) + Integer.toHexString(b);
		
	}
}
