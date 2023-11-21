package bpm.metadata.layer.logical;

import java.util.ArrayList;
import java.util.List;

import bpm.metadata.MetaData;

public class MultiDSRelation  {
	
	
	private String leftDataSourceName;
	private String rightDataSourceName;
	private String leftDataStreamName;
	private String rightDataStreamName;
	

	private IDataSource leftDataSource;
	private IDataSource rightDataSource;
	
	private IDataStream rightTable;
	private IDataStream leftTable;
	
	private List<Join> joins = new ArrayList<Join>();

	public IDataSource getLeftDataSource() {
		return leftDataSource;
	}

	public void setLeftDataSource(IDataSource leftDataSource) {
		this.leftDataSource = leftDataSource;
	}

	public IDataSource getRightDataSource() {
		return rightDataSource;
	}

	public void setRightDataSource(IDataSource rightDataSource) {
		this.rightDataSource = rightDataSource;
	}

	public IDataStream getRightTable() {
		return rightTable;
	}

	public void setRightTable(IDataStream rightTable) {
		this.rightTable = rightTable;
	}

	public IDataStream getLeftTable() {
		return leftTable;
	}

	public void setLeftTable(IDataStream leftTable) {
		this.leftTable = leftTable;
	}

	public List<Join> getJoins() {
		return joins;
	}
	
	
	public void addJoin(Join join){
		joins.add(join);
	}
	
	public void removeJoin(Join join){
		joins.remove(join);
	}
	
	public void addJoin(IDataStreamElement left, IDataStreamElement right) throws RelationException, ClassNotFoundException{
		//check if the left and right are from the same tables than the rest joins
		if (!joins.isEmpty()){
			if (joins.get(0).getLeftElement().getOrigin().getTable() != left.getOrigin().getTable()
					||
					joins.get(0).getRightElement().getOrigin().getTable() != right.getOrigin().getTable()
					||
					leftDataSource != left.getDataStream().getDataSource()
					||
					rightDataSource != right.getDataStream().getDataSource()){
				throw new RelationException("Cannot create join that keys aren't on the same tables the existing joins");
			}
		}else {
			
		}
		
		
		Class<?> cL = Class.forName(left.getOrigin().getClassName());
		Class<?> cR = Class.forName(right.getOrigin().getClassName());
		
		if (cL.isAssignableFrom(cR) || cR.isAssignableFrom(cL)){
			joins.add(new Join(left, right, Join.INNER));
			leftTable = left.getDataStream();
			leftDataSource = leftTable.getDataSource();
			rightTable = right.getDataStream();
			rightDataSource = rightTable.getDataSource();
		}
		else{
			throw new RelationException("Classes are not compatible");
		}
	}
	
	public String getName(){
		return leftDataSource.getName() + "." + leftTable.getName() + "<-->" + rightDataSource.getName() + "." +rightTable.getName();
	}
	
	public String getXml(){
		StringBuffer buf = new StringBuffer();
		
		buf.append("    <multiRelation>\n");
		
		buf.append("        <leftDataSource>" + leftDataSource.getName() + "</leftDataSource>\n");
		buf.append("        <leftDataStream>" + leftTable.getName() + "</leftDataStream>\n");
		
		buf.append("        <rightDataSource>" + rightDataSource.getName() + "</rightDataSource>\n");
		buf.append("        <rightDataStream>" + rightTable.getName() + "</rightDataStream>\n");
		
		for(Join j : joins){
			buf.append(j.getXml());
		}
		
		buf.append("    </multiRelation>\n");
		
		return buf.toString();
	}
	
	/**
	 * set the specified datasource/streams from datasourcenames parameter
	 * do not use, it just called after parsing xml
	 * @param model
	 * @throws Exception 
	 */
	public void setDatas(MetaData model) throws Exception{
		leftDataSource = model.getDataSource(leftDataSourceName);
		rightDataSource = model.getDataSource(rightDataSourceName);
		
		if (leftDataSource == null){
			throw new Exception("Unable to set Multireltion left DataSource");
		}
		leftTable = leftDataSource.getDataStreamNamed(leftDataStreamName);
		
		if (rightDataSource == null){
			throw new Exception("Unable to set Multireltion left DataSource");
		}
		rightTable = rightDataSource.getDataStreamNamed(rightDataStreamName);
	
	}

	public void setLeftDataSourceName(String leftDataSourceName) {
		this.leftDataSourceName = leftDataSourceName;
	}

	public void setRightDataSourceName(String rightDataSourceName) {
		this.rightDataSourceName = rightDataSourceName;
	}

	public void setLeftDataStreamName(String leftDataStreamName) {
		this.leftDataStreamName = leftDataStreamName;
	}

	public void setRightDataStreamName(String rightDataStreamName) {
		this.rightDataStreamName = rightDataStreamName;
	}

}
