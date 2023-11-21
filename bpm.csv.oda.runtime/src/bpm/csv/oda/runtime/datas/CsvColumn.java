package bpm.csv.oda.runtime.datas;


public class CsvColumn {
	private String name;
	private int position;
	private int sqlType = -1;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public int getType() {
		return sqlType;
	}
	public void setType(int type) {
		this.sqlType = type;
	}
	
	
	

}
