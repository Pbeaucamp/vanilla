package org.fasd.datasource;

/**
 * SQLColumn :
 * Internal representation of a sql column
 * @author manu
 *
 */
public class DataObjectItem {
	private static int counter = 0;
	public static void resetCounter(){
		counter =0;
	}

	private String name = "";
	private String id = "";
	private String origin = "";
	private String type = "physical";
	private String classe = "java.lang.Object";
	private String attribut = "U";
	private String desc = "";
	private DataObject parent;
	private String sqlType = "VARCHAR";
	private String oneColumnDatePart;
	
	public void setSqlType(String sqlType) {
		this.sqlType = sqlType;
	}
	
	public String getSqlType(){
		return sqlType;
	}

	public DataObject getParent() {
		return parent;
	}

	public void setParent(DataObject parent) {
		this.parent = parent;
	}

	public String getAttribut() {
		return attribut;
	}

	public void setAttribut(String attribut) {
		this.attribut = attribut;
	}

	public String getClasse() {
		return classe;
	}

	public void setClasse(String classe) {
		this.classe = classe;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public DataObjectItem(String name) {
		this.name = name;
		counter++;
		id = "l" + String.valueOf(counter);
	}

	public DataObjectItem() {
		counter++;
		id = "l" + String.valueOf(counter);
		//parsing purposes
	}
	

	public String getFAXML() {
		String tmp = "";
		
		tmp += "                <item>\n"; 
		tmp += "                    <id>" + id +"</id>\n";
		tmp += "                    <name>" + name + "</name>\n";
		tmp += "                    <origin>" + origin + "</origin>\n";
		tmp += "                    <type>" + type + "</type>\n";
		tmp += "                    <classe>" + classe + "</classe>\n";
		tmp += "                    <sql-type>" + sqlType + "</sql-type>\n";
		tmp += "                    <attribut>" + attribut + "</attribut>\n";
		tmp += "                    <description>" + desc + "</description>\n";
		tmp += "                </item>\n";
		
		return tmp;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
		if (Integer.parseInt(id.substring(1)) > counter){
			counter = Integer.parseInt(id.substring(1)) + 1;
		}
			
	}
	
	public String getFullName(){
		
		if (parent != null && parent.getDataSource() != null){
			return "[" + parent.getDataSource().getDSName() + "].[" + parent.getName() + "].[" + getName() + "]";
		}
		
		return name;
	}

	public void setOneColumnDatePart(String oneColumnDatePart) {
		this.oneColumnDatePart = oneColumnDatePart;
	}

	public String getOneColumnDatePart() {
		return oneColumnDatePart;
	}
	
}
