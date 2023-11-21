package bpm.metadata.jdbc.driver;

import java.io.Serializable;
import java.sql.Connection ;
import java.util.ArrayList;

public class Table implements Serializable {
	
	private String name;
	private ArrayList<Column> colList = new ArrayList<Column>(); 
	
	public Table(String name, ArrayList<Column> colList) {		
		this.name = name;
		this.colList = colList;		
	}

	public String getName() {
		return name;
	}

	public ArrayList<Column> getColList() {
		return colList;
	}

	public void setColList(ArrayList<Column> colList) {
		this.colList = colList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Table other = (Table) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
}
