package bpm.fd.api.core.model.structure;

public class FactoryStructure {
	private  long cellCount = 0;
	private  long drillDrivencellCount = 0;
	private  long tableCount = 0;
	private  long folderCount = 0;
	private  long folderPageCount = 0;
	private  long stackableCellCount = 0;
	private int divCellCount = 0;
	
	public Cell createCell(String name, int colSpan, int rowSpan){
		Cell c = new Cell("" + ++cellCount , name);
		c.setRowSpan(rowSpan);
		c.setColSpan(colSpan);
		return c;
	}
	public DrillDrivenStackableCell createDrillDrivenStackableCell(String name, int colSpan, int rowSpan){
		DrillDrivenStackableCell c = new DrillDrivenStackableCell("" + ++drillDrivencellCount , name);
		c.setRowSpan(rowSpan);
		c.setColSpan(colSpan);
		return c;
	}
	
	public Folder createFolder(String name){
		Folder c = new Folder("" + ++folderCount, name);
		
		return c;
	}
	
	public FolderPage createFolderPage(String name){
		FolderPage c = new FolderPage("" + ++folderPageCount, name);
		
		return c;
	}
	
	
	public Cell createCell(String name, int id, int colSpan, int rowSpan){
		Cell c = new Cell("" + id , name);
		c.setRowSpan(rowSpan);
		c.setColSpan(colSpan);
		
		if (id > cellCount){
			cellCount = id;
		}
		return c;
	}
	
	public Table createTable(String name){
		Table t = new Table( "" + ++tableCount , name);
		return t;
	}
	
	public Table createTable(String name, int id){
		Table t = new Table("" + ++tableCount , name);
		
		if (id > tableCount){
			tableCount = id;
		}
		return t;
	}

	public Object create(Class<?> structureClass) {
		if (structureClass == Cell.class){
			return createCell("Cell", 1, 1);
		}
		else if (structureClass == Table.class){
			return createTable("Table");
		}
		else if (structureClass == Folder.class){
			return createFolder("Folder");
		}	
		else if (structureClass == FolderPage.class){
			return createFolderPage("FolderPage");
		}
		else if(structureClass == StackableCell.class) {
			return createStackableCell("StackableCell", 1, 1);
		}
		else if(structureClass == DrillDrivenStackableCell.class) {
			return createDrillDrivenStackableCell("DrillDrivenStackableCell", 1, 1);
		}
		else if(structureClass == DivCell.class) {
			return createDivCell("DivCell");
		}

		return null;
	}


	public StackableCell createStackableCell(String name, int rowSpan, int colSpan) {
		StackableCell c = new StackableCell("" + ++stackableCellCount , name);
		c.setRowSpan(rowSpan);
		c.setColSpan(colSpan);
		return c;
	}
	
	public StackableCell createStackableCell(String name, int id, int rowSpan, int colSpan) {
		StackableCell c = new StackableCell("" + id , name);
		c.setRowSpan(rowSpan);
		c.setColSpan(colSpan);
		if(id > stackableCellCount) {
			stackableCellCount = id;
		}
		return c;
	}

	public Folder createFolder(String name, int id) {
		Folder t = new Folder("" + ++folderCount , name);
		
		if (id > folderCount){
			folderCount = id;
		}
		return t;
	}
	
	public FolderPage createFolderPage(String name, int id) {
		FolderPage t = new FolderPage("" + ++folderPageCount , name);
		
		if (id > folderPageCount){
			folderPageCount = id;
		}
		return t;
	}
	public DrillDrivenStackableCell createDrillDrivenStackableCell(
			String name, int id) {
		DrillDrivenStackableCell c = new DrillDrivenStackableCell("" + ++drillDrivencellCount , name);
		return c;
	}
	public DivCell createDivCell(String name, int id) {
		if(id > divCellCount) {
			divCellCount = id;
		}
		return new DivCell("" + ++divCellCount , name);
	}
	
	private Object createDivCell(String name) {
		return new DivCell("" + ++divCellCount , name);
	}
}
