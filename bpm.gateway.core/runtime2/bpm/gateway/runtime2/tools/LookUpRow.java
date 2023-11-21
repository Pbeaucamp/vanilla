package bpm.gateway.runtime2.tools;

import bpm.gateway.runtime2.internal.Row;

public class LookUpRow implements Comparable<LookUpRow> {

	private Object key;
	private Row row;
	
	public LookUpRow(Object key, Row row) {
		this.key = key;
		this.row = row;
	}

	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
		this.key = key;
	}

	public Row getRow() {
		return row;
	}

	public void setRow(Row row) {
		this.row = row;
	}
	
	@Override
	public boolean equals(Object obj) {
		return key.equals(((LookUpRow)obj).getKey());
	}

	@Override
	public int compareTo(LookUpRow o) {
		if(this.getKey().getClass().getName().equals("java.lang.Integer")) {
			return ((Integer)this.getKey()).compareTo(((Integer)((LookUpRow)o).getKey()));
		}
		else if(this.getKey().getClass().getName().equals("java.lang.Long")) {
			return ((Long)this.getKey()).compareTo(((Long)((LookUpRow)o).getKey()));
		}
		else if(this.getKey().getClass().getName().equals("java.lang.Float")) {
			return ((Float)this.getKey()).compareTo(((Float)((LookUpRow)o).getKey()));
		}
		else if(this.getKey().getClass().getName().equals("java.lang.Double")) {
			return ((Double)this.getKey()).compareTo(((Double)((LookUpRow)o).getKey()));
		}
		else {
			return ((String)this.getKey()).compareTo(((String)((LookUpRow)o).getKey()));
		}
	}
	
}
