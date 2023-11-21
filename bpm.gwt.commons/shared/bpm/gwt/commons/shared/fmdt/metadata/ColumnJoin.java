package bpm.gwt.commons.shared.fmdt.metadata;

import java.util.HashMap;
import java.util.Map;

import bpm.vanilla.platform.core.beans.data.DatabaseColumn;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ColumnJoin implements IsSerializable {
	
	public static final int FULL_OUTER = 3;
	public static final int LEFT_OUTER = 2;
	public static final int RIGHT_OUTER = 1;
	public static final int INNER = 0;
	
	public enum Outer {
		FULL_OUTER(3), 
		LEFT_OUTER(2), 
		RIGHT_OUTER(1), 
		INNER(0);
		
		private int value;

		private static Map<Integer, Outer> map = new HashMap<Integer, Outer>();
		static {
			for (Outer outer : Outer.values()) {
				map.put(outer.getValue(), outer);
			}
		}
		
		private Outer(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}

		public static Outer valueOf(int value) {
			return map.get(value);
		}
	}
	
	private DatabaseColumn leftColumn, rightColumn;
	private Outer outer;
	
	public ColumnJoin() { }
	
	public ColumnJoin(DatabaseColumn leftColumn, DatabaseColumn rightColumn, Outer outer) {
		this.leftColumn = leftColumn;
		this.rightColumn = rightColumn;
		this.outer = outer;
	}
	
	public Outer getOuter() {
		return outer;
	}
	
	public DatabaseColumn getLeftColumn() {
		return leftColumn;
	}
	
	public DatabaseColumn getRightColumn() {
		return rightColumn;
	}
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(leftColumn.toString());
		
		switch (outer) {
		case INNER:
			buf.append(" = ");
			break;
		case LEFT_OUTER:
			buf.append(" =(+) ");
			break;
		case RIGHT_OUTER:
			buf.append(" (+)= ");
			break;
		case FULL_OUTER:
			buf.append(" (+) ");
			break;

		default:
			break;
		}
		buf.append(rightColumn.toString());
		return buf.toString();
	}
}
