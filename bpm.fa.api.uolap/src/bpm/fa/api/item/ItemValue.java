package bpm.fa.api.item;

import java.util.ArrayList;

import bpm.fa.api.olap.OLAPMember;

public class ItemValue implements Item {
	private String label;
	private String value;
	
	private ArrayList<ItemDependent> dependent = new ArrayList<ItemDependent>();
	
	private String sql = null;
	private int drillUrlRow;
	private int drillUrlCell;
	
	public ItemValue(String lbl, Object value, String sql) {
		this.label = lbl;	
		this.sql = sql;
		
		if (value instanceof Double) {
			this.value = ((Double) value).toString();
		}
		if (value instanceof String && !((String)value).endsWith("null")){
			this.value = (String)value;
		}
	}
	
	public ItemValue(String lbl, String sql) {
		this.label = lbl;
		this.sql = sql;
	}

	public String getDrillThroughSql()
	{
		return sql;
	}
	public String getLabel() {
		return label;
	}

	public String getValue() {
		return value;
	}
	
	/**
	 * Add dependencies
	 * @param hiera
	 * @param level
	 * @param member
	 * @param label
	 */
	public void addMemberDep(String hiera, String level, String member, String label) {
		dependent.add(new ItemDependent(hiera, level, member, label, this.label));
	}
	
	/**
	 * Get Dependent items
	 * @return dep items
	 */
	public ArrayList<ItemDependent> getDependent() {
		return dependent;
	}

	public void addMemberDep(OLAPMember member) {
		dependent.add(new ItemDependent("", "", member.getUniqueName(), member.getName(), member.getCaption()));
		
	}

	public void setPosition(int row, int cell) {
		this.drillUrlRow = row;
		this.drillUrlCell = cell;
	}
	
	/**
	 * Only used to get drillUrl
	 * @return
	 */
	public int getDrillUrlRow() {
		return drillUrlRow;
	}
	
	/**
	 * Only used to get drillUrl
	 * @return
	 */
	public int getDrillUrlCell() {
		return drillUrlCell;
	}
}
