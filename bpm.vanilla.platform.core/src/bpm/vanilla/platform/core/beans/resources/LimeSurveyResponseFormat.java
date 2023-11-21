package bpm.vanilla.platform.core.beans.resources;

import java.util.HashMap;
import java.util.Map;

public enum LimeSurveyResponseFormat {
	CSV(0), PDF(1), XLS(2), DOC(3), JSON(4);

	private int type;

	private static Map<Integer, LimeSurveyResponseFormat> map = new HashMap<Integer, LimeSurveyResponseFormat>();
	static {
		for (LimeSurveyResponseFormat format : LimeSurveyResponseFormat.values()) {
			map.put(format.getType(), format);
		}
	}

	private LimeSurveyResponseFormat(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public static LimeSurveyResponseFormat valueOf(int format) {
		return map.get(format);
	}
}