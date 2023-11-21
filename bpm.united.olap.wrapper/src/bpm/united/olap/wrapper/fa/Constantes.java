package bpm.united.olap.wrapper.fa;

import java.util.ArrayList;
import java.util.List;

public class Constantes {

	public static String USER = "_user";
	public static String PASSWORD = "_password";
	public static String GROUP = "_group";
	public static String VIEWID = "_viewId";
	public static String REPID = "_repId";
	public static String WIDTH = "_width";
	public static String ENCRYPTED = "_encrypted";
	public static List<String> infos;
	
	public static List<String> getViewInfos() {
		if(infos == null) {
			infos = new ArrayList<String>();
			infos.add(USER);
			infos.add(PASSWORD);
			infos.add(GROUP);
			infos.add(VIEWID);
			infos.add(REPID);
			infos.add(WIDTH);
			infos.add(ENCRYPTED);
		}
		return infos;
	}
}
