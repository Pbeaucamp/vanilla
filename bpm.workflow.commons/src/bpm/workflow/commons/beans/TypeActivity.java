package bpm.workflow.commons.beans;

import java.util.HashMap;
import java.util.Map;


public enum TypeActivity {
	START(0),
	STOP(1),
	
	SOURCE(2),
	VALIDATION(3),
	COMPRESSION(4),
	ENCRYPTAGE(5),
	CIBLE(6),
	MAIL(7),
	CONNECTOR(8),
	ACTION(9),
	DATA_SERVICE(10),
	VANILLA_ITEM(11),
	CRAWL(12),
	SOCIAL(13),
	OPEN_DATA(14),
	
	RSCRIPT(15),
	RECODE(16),
	OUTPUT_FILE(17), 
	CHART(18), 
	FIELD_SELECTION(19),
	HEAD(20),
	SORTING_ACTIVITY(21), 
	FILTER_ACTIVITY(22),
	SIMPLE_LINEAR_REG(23),
	CORRELATION_MATRIX(24),
	DECISION_TREE(25),
	KMEANS(26),
	HAC(27),
	MDM(28),
	AKLABOX(29),
	GEOJSON(30),
	PRECLUSTER(31),
	OPENDATA_CRAWL(32),
	MDM_INPUT(33),
	LIMESURVEY_INPUT(34),
	MERGE_FILES(35);
	
	private int type;

	private static Map<Integer, TypeActivity> map = new HashMap<Integer, TypeActivity>();
	static {
        for (TypeActivity serverType : TypeActivity.values()) {
            map.put(serverType.getType(), serverType);
        }
    }
	
	private TypeActivity(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
	
	public static TypeActivity valueOf(int serverType) {
        return map.get(serverType);
    }
}