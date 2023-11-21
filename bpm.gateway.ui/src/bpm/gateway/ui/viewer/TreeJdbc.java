package bpm.gateway.ui.viewer;

/**
 * This class is for reprensentation of a DataBase Element
 * The type defines is nature
 * @author LCA
 *
 */
public class TreeJdbc extends TreeParent{
	public static final int SCHEMA = 1;
	public static final int TABLE = 2;
	public static final int VIEW = 3;
	public static final int COLUMN = 4;
	
	public int type = 0;
	public TreeJdbc(String name, int type) {
		super(name);
		this.type = type;
		
	}
}