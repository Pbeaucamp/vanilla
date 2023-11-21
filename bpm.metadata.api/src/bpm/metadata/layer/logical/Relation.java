package bpm.metadata.layer.logical;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import bpm.metadata.layer.logical.sql.SQLRelation;
import bpm.vanilla.platform.core.utils.MD5Helper;

/**
 * This class represent a join between two tables(or one if this is a parent child join)
 * The join can be on more than one column from the tables
 * @author LCA
 *
 */
public abstract class  Relation {

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
	
	
	protected List<Join> joins = new ArrayList<Join>();
	
	
	protected IDataStream leftTable, rightTable;
	protected String leftTableName, rightTableName;
		
	protected Cardinality cardinality = Cardinality.C_0_n;
	
	protected String finalRelation;
	
	public Relation(){
		
	}
	
	
	
	
	/**
	 * @return the cardinality
	 */
	public Cardinality getCardinality() {
		return cardinality;
	}




	/**
	 * @param cardinality the cardinality to set
	 */
	public void setCardinality(Cardinality cardinality) {
		this.cardinality = cardinality;
	}


	/**
	 * @param cardinality the cardinality to set
	 */
	public void setCardinality(String cardinality) {
		try{
			this.cardinality = Cardinality.valueOf(cardinality);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}


	/**
	 * add a join to the list
	 * @param join
	 * @throws ClassNotFoundException 
	 * @throws RelationException 
	 */
	public void add(IDataStreamElement left, IDataStreamElement right, int outer) throws ClassNotFoundException, RelationException{
		
		//check if the left and right are from the same tables than the rest joins
		if (!joins.isEmpty()){
			try{
				if (joins.get(0).getLeftElement().getOrigin().getTable() != left.getOrigin().getTable()
						||
						joins.get(0).getRightElement().getOrigin().getTable() != right.getOrigin().getTable()){
					throw new RelationException("Cannot create join that keys aren't on the same tables than existing joins");
				}
			}catch (RelationException e) {
				throw e;
			}catch(Exception ex){
				
			}

		}
		
		Class<?> cL = null;
		Class<?> cR = null;
		
		if (left instanceof ICalculatedElement){
			cL = Object.class;
		}
		else{
			try{
				cL = Class.forName(left.getOrigin().getClassName());
			}catch(Exception ex){
				ex.printStackTrace();
				cL = Object.class;
			}
		}
		
		if (right instanceof ICalculatedElement){
			cR = Object.class;
		}
		else{
			try{
				cR = Class.forName(right.getOrigin().getClassName());
			}catch(Exception ex){
				ex.printStackTrace();
				cR = Object.class;
			}
			
		}
		
		
//		if (cL.isAssignableFrom(cR) || cR.isAssignableFrom(cL)){
			joins.add(new Join(left, right, outer));
			leftTable = left.getDataStream();
			rightTable = right.getDataStream();
//		}
//		else{
//			throw new RelationException("Classes are not compatible");
//		}
	}
	
	public void addJoin(Join j){
		joins.add(j);
	}
	
	
	/**
	 * remove a join from the relation
	 * @param join
	 */
	public void remove(Join join){
		joins.remove(join);
	}
	
	
	/**
	 * return the where clause for a request
	 * @return
	 */
	public abstract String getWhereClause();
	
	
	public List<Join> getJoins(){
		return joins;
	}
	
	/**
	 * return true if the relation is on one unique table
	 * @return
	 */
	public boolean isParentChildRelation(){
		if (joins.isEmpty() || joins.get(0).getLeftElement().getOrigin() != joins.get(0).getRightElement().getOrigin()){
			return false;
		}
		return true;
	}
	
	public IDataStream getLeftTable(){
		return leftTable;
	}
	
	public IDataStream getRightTable(){
		return rightTable;
	}
	
	public String getName(){
		StringBuffer buf = new StringBuffer();
		if (joins == null || joins.size() == 0){
			if (leftTable == null){
				buf.append("???");
			}
			else{
				buf.append(leftTable.getName());
			}
			buf.append("<-->");
			if (rightTable == null){
				buf.append("???");
			}
			else{
				buf.append(rightTable.getName());
			}
			return buf.toString();
		}
		Join j = joins.get(0);
		if (j == null){
			if (leftTable == null){
				buf.append("???");
			}
			else{
				buf.append(leftTable.getName());
			}
			buf.append("<-->");
			if (rightTable == null){
				buf.append("???");
			}
			else{
				buf.append(rightTable.getName());
			}
			return buf.toString();
		}
		else{
			
			if (j.getOuter() == Join.INNER){
				if (leftTable != null){
					buf.append(leftTable.getName());
				}
				else{
					buf.append("???");
				}
				
				buf.append("=");
				if (rightTable != null){
					buf.append(rightTable.getName());
				}
				else{
					buf.append("???");
				}
			}
			else if (j.getOuter() == Join.LEFT_OUTER){
				if (leftTable != null){
					buf.append(leftTable.getName());
				}
				else{
					buf.append("???");
				}
				buf.append("=(+)");
				if (rightTable != null){
					buf.append(rightTable.getName());
				}
				else{
					buf.append("???");
				}
			}
			else if (j.getOuter() == Join.RIGHT_OUTER){
				if (leftTable != null){
					buf.append(leftTable.getName());
				}
				else{
					buf.append("???");
				}
				buf.append("(+)=");
				if (rightTable != null){
					buf.append(rightTable.getName());
				}
				else{
					buf.append("???");
				}
			}
			else if (j.getOuter() == Join.FULL_OUTER){
				if (leftTable != null){
					buf.append(leftTable.getName());
				}
				else{
					buf.append("???");
				}
				buf.append("(+)");
				if (rightTable != null){
					buf.append(rightTable.getName());
				}
				else{
					buf.append("???");
				}
			}
			return buf.toString();
		}
		
	}


	public String getXml() {
		StringBuffer buf = new StringBuffer();
		try{
			buf.append("            <relation>\n");
			
			buf.append("                <leftDataStream>" + (leftTable != null ? leftTable.getName() : "") + "</leftDataStream>\n");
			buf.append("                <rightDataStream>" + (rightTable != null ? rightTable.getName() : "") + "</rightDataStream>\n");
			buf.append("                <cardinality>" + cardinality.name() + "</cardinality>\n");
			
			for(Join j : joins){
				buf.append(j.getXml());
			}
			
			buf.append("                <finalRelation>" + finalRelation + "</finalRelation>\n");
			
			buf.append("            </relation>\n");
		}catch(Exception e){
			e.printStackTrace();	
			return "";
		}
		
		
		return buf.toString();
	}


	/**
	 * establish references after a load
	 * @param ds
	 */
	public void setDataStreams(IDataSource ds) {
		if (rightTableName != null){
			this.rightTable = ds.getDataStreamNamed(rightTableName);
		}
		
		if (leftTableName != null){
			this.leftTable = ds.getDataStreamNamed(leftTableName);
		}
		
		
		for(Join j : joins){
			try {
				j.setElements(leftTable, rightTable);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	

	/**
	 * do not use
	 * @param leftTableName
	 */
	public void setLeftTableName(String leftTableName) {
		this.leftTableName = leftTableName;
	}
	/**
	 * do not use
	 * @param leftTableName
	 */
	
	public void setRightTableName(String rightTableName) {
		this.rightTableName = rightTableName;
	}
	
	public boolean isOuterJoin(){
		if (joins.size() > 0){
			return joins.get(0).getOuter() != Join.INNER;
		}
		
		
		return false;
	}
	
	public String getLeftTableName(){
		return leftTableName;
	}
	
	public String getRightTableName(){
		return rightTableName;
	}
	
	/**
	 * return the DataSource of the Relation
	 * @return
	 */
	public IDataSource getDataSource(){
		if (leftTable != null){
			return leftTable.getDataSource();
		}
		else if (rightTable != null){
			return rightTable.getDataSource();
		}
		
		return null;
	}
	
	public Relation copy(){
		Relation r = null;
		
		//if (r instanceof SQLRelation){
			r = new SQLRelation();
		//}

		r.leftTable = leftTable;
		r.rightTable = rightTable;
		
		r.finalRelation = finalRelation;
		
		for(Join j : joins){
			r.joins.add(j.copy());
		}
		
		return r;
	}


	public boolean isUsingTable(IDataStream table) {
		if (table == null){
			return false;
		}
		else if (rightTable == null || leftTable == null){
		
			setDataStreams(table.getDataSource());
			
			if (rightTable == null || leftTable == null){
				return false;
			}
		}
		
		return rightTable.getName().equals(table.getName()) || leftTable.getName().equals(table.getName());
	}
	
	/**
	 * This is used to identify a relation.
	 * 
	 * @return
	 */
	public String getRelationKey() {
		StringBuffer buf = new StringBuffer();
		buf.append(leftTableName);
		buf.append(rightTableName);
		for(Join j : joins) {
			buf.append(j.toString());
		}
		
		return MD5Helper.encode(buf.toString());
	}
	
	public abstract String getBasicWhereClause();

	public String getFinalRelation() {
		return finalRelation;
	}
	
	public void setFinalRelation(String finalRelation) {
		this.finalRelation = finalRelation;
	}
	
}

