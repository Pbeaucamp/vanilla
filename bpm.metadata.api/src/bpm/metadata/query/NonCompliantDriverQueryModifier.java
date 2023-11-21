package bpm.metadata.query;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NonCompliantDriverQueryModifier {

	public static final String TYPE_HIVE2 = "jdbc:hive2://";
	
	public static String editQuery(String query, String jdbcPrefix) {
		
		if(jdbcPrefix.equals(TYPE_HIVE2)) {
			return editQueryForHive2(query);
		}
		
		return query;
	}

	private static String editQueryForHive2(String query) {
		String newQuery = query;
		//Remove the "
		newQuery = newQuery.replace("`", "");
		
		//Remove the "." in aliases, it will replace the alias name by the part after the last "."
		Pattern p = Pattern.compile("(?<=\\bAS\\s)(\\w+[.\\w+]*)");
		Matcher m = p.matcher(newQuery);
		while (m.find()) {
			newQuery = newQuery.replace("AS " + m.group(1), "AS " + m.group(1).substring(m.group(1).lastIndexOf(".")+1));
		}
		
		//Remove the table aliases
//		Pattern pt = Pattern.compile("(?<=\\bfrom\\s)(\\w+[.\\w+]*)");
//		
//		Matcher mt = pt.matcher(newQuery);
//		newQuery = mt.replaceAll("");
		
		//Remove the order by condition
		int orderIndex = newQuery.toLowerCase().indexOf("order by");
		if(orderIndex > 0) {
			newQuery = newQuery.substring(0, orderIndex);
		}
		
		return newQuery;
	}

	public static String changeTableName(String name, String jdbcPrefix) {
		if(jdbcPrefix.equals(TYPE_HIVE2)) {
			int index = name.lastIndexOf(".");
			if(index > 0) {
				return name.substring(index+1);
			}
		}
		return name;
	}
	
}
