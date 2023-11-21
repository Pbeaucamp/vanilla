package bpm.gwt.commons.shared.fmdt;

import java.io.Serializable;

import bpm.metadata.layer.logical.Relation.Cardinality;

public class FmdtRelation implements Serializable{

	
	public enum Cardinality{
		C_0_1("(0,1)"), C_0_n("(0,n)"), C_1_n("(1,n)"), C_1_1("(1,1)");
		
		private String label;
		
		Cardinality(String label){
			this.label = label;
		}
		public String getLabel(){
			return label;
		}
	}
	
	private int outer = 0;
	
	private String left, right;
	
	private String leftTableName, rightTableName;
	
	private Cardinality cardinality = Cardinality.C_0_n;

	
	public FmdtRelation() {
		super();
	}
	
	public FmdtRelation(int outer, String left, String right,
			String leftTableName, String rightTableName, Cardinality cardinality) {
		super();
		this.outer = outer;
		this.left = left;
		this.right = right;
		this.leftTableName = leftTableName;
		this.rightTableName = rightTableName;
		this.cardinality = cardinality;
	}

	public int getOuter() {
		return outer;
	}

	public void setOuter(int outer) {
		this.outer = outer;
	}

	public String getLeft() {
		return left;
	}

	public void setLeft(String left) {
		this.left = left;
	}

	public String getRight() {
		return right;
	}

	public void setRight(String right) {
		this.right = right;
	}

	public String getLeftTableName() {
		return leftTableName;
	}

	public void setLeftTableName(String leftTableName) {
		this.leftTableName = leftTableName;
	}

	public String getRightTableName() {
		return rightTableName;
	}

	public void setRightTableName(String rightTableName) {
		this.rightTableName = rightTableName;
	}

	public Cardinality getCardinality() {
		return cardinality;
	}

	public void setCardinality(Cardinality cardinality) {
		this.cardinality = cardinality;
	}
	
	
}
