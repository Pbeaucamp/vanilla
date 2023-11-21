package bpm.metadata.layer.logical.sql;


import bpm.metadata.layer.logical.ICalculatedElement;
import bpm.metadata.layer.logical.Join;
import bpm.metadata.layer.logical.Relation;
import bpm.metadata.layer.physical.IConnection;

public class SQLRelation extends Relation {

	
	
	@Override
	public String getBasicWhereClause() {
		boolean isOracle = false;
		
		StringBuffer buf = new StringBuffer();
		try {
			IConnection c = getJoins().get(0).getRightElement().getOrigin().getTable().getConnection();
			
//		if (c instanceof SQLConnection){
//			isOracle = ((SQLConnection)c).getDriverName().equalsIgnoreCase("oracle");
//		}
			
			
			if (isOracle){
				buf = getJoinForOracle();
			}
			else{
				buf = getJoinForStandard();
			}
		} catch(Exception e) {
			//it may be calculated, lets try it
			try {
				if(getJoins().get(0).getRightElement().isCalculated() || getJoins().get(0).getLeftElement().isCalculated()) {
					buf = getJoinForCalculated();
				}
				//That's the worst thing I have done... Make the damn thing crash because throws exception is too mainstream
				else {
					getJoins().get(0).getRightElement().getOrigin().getTable().getConnection();
				}
			} catch(Exception e1) {
			}
		}
		
		
		
		return buf.toString();
	}

	
	
	private StringBuffer getJoinForCalculated() {
		StringBuffer buf = new StringBuffer();
		boolean first = true;
		
		boolean isLeftOuter = false;
		boolean isInner = false;
		boolean isRightOuter = false;
		boolean isFullOuter = false;
		
		if (joins.size() != 0){
			switch(joins.get(0).getOuter()){
			case Join.INNER:
				isInner = true;
				break;
			case Join.LEFT_OUTER:
				isLeftOuter = true;
				break;
			case Join.RIGHT_OUTER:
				isRightOuter = true;
				break;
			case Join.FULL_OUTER:
				isFullOuter = true;
				break;
			}
			
			
			if (isLeftOuter){
				if(joins.get(0).getLeftElement().isCalculated()) {
					buf.append(joins.get(0).getLeftElement().getDataStream().getName() + " `" + joins.get(0).getLeftElement().getDataStream().getName() + "`");
				}
				else {
					buf.append(joins.get(0).getLeftElement().getOrigin().getTable().getName() + " `" + joins.get(0).getLeftElement().getDataStream().getName() + "`");
				}
				if(joins.get(0).getRightElement().isCalculated()) {
					buf.append(" LEFT OUTER JOIN " + joins.get(0).getRightElement().getDataStream().getName() + " `" + joins.get(0).getRightElement().getDataStream().getName() + "`");
				}
				else {
					buf.append(" LEFT OUTER JOIN " + joins.get(0).getRightElement().getOrigin().getTable().getName() + " `" + joins.get(0).getRightElement().getDataStream().getName() + "`");
				}
				
				
				buf.append(" ON (");
			}
			else if (isRightOuter){
				if(joins.get(0).getLeftElement().isCalculated()) {
					buf.append(joins.get(0).getLeftElement().getDataStream().getName() + " `" + joins.get(0).getLeftElement().getDataStream().getName() + "`");
				}
				else {
					buf.append(joins.get(0).getLeftElement().getOrigin().getTable().getName() + " `" + joins.get(0).getLeftElement().getDataStream().getName() + "`");
				}
				if(joins.get(0).getRightElement().isCalculated()) {
					buf.append(" RIGHT OUTER JOIN " + joins.get(0).getRightElement().getDataStream().getName() + " `" + joins.get(0).getRightElement().getDataStream().getName() + "`");
				}
				else {
					buf.append(" RIGHT OUTER JOIN " + joins.get(0).getRightElement().getOrigin().getTable().getName() + " `" + joins.get(0).getRightElement().getDataStream().getName() + "`");
				}
				buf.append(" ON (");
			}
			else if (isFullOuter){
				if(joins.get(0).getLeftElement().isCalculated()) {
					buf.append(joins.get(0).getLeftElement().getDataStream().getName() + " `" + joins.get(0).getLeftElement().getDataStream().getName() + "`");
				}
				else {
					buf.append(joins.get(0).getLeftElement().getOrigin().getTable().getName() + " `" + joins.get(0).getLeftElement().getDataStream().getName() + "`");
				}
				if(joins.get(0).getRightElement().isCalculated()) {
					buf.append(" FULL OUTER JOIN " + joins.get(0).getRightElement().getDataStream().getName() + " `" + joins.get(0).getRightElement().getDataStream().getName() + "`");
				}
				else {
					buf.append(" FULL OUTER JOIN " + joins.get(0).getRightElement().getOrigin().getTable().getName() + " `" + joins.get(0).getRightElement().getDataStream().getName() + "`");
				}
				buf.append(" ON (");
			}
			
			
		}
		
		
		
		
		for(Join j : joins){
			if (first){
				first = false;
			}
			else{
				buf.append(" AND ");
			}
			
			if(j.getLeftElement().isCalculated()) {
				buf.append( ((ICalculatedElement)j.getLeftElement()).getFormula());
			}
			else {
				buf.append( "`" + j.getLeftElement().getDataStream().getName() + "`." + j.getLeftElement().getOrigin().getShortName());
			}
			
			buf.append("=");
			
			if(j.getRightElement().isCalculated()) {
				buf.append( ((ICalculatedElement)j.getRightElement()).getFormula());
			}
			else {
				buf.append("`" + j.getRightElement().getDataStream().getName() + "`." + j.getRightElement().getOrigin().getShortName());
			}
			

		}
		
		if (!isInner){
			buf.append(")");
		}
		return buf;
	}



	private StringBuffer getJoinForOracle(){
		StringBuffer buf = new StringBuffer();
		boolean first = true;
		
		for(Join j : joins){
			if (first){
				first = false;
			}
			else{
				buf.append(" AND ");
			}
			
			
			
			if (j.getOuter() == Join.INNER){
				buf.append(j.getLeftElement().getOrigin().getName());
				buf.append("=");
				buf.append(j.getRightElement().getOrigin().getName());
			}
			else if (j.getOuter() == Join.LEFT_OUTER){
				buf.append(j.getLeftElement().getOrigin().getName());
				buf.append("(+)=");
				buf.append(j.getRightElement().getOrigin().getName());
			}
			else if (j.getOuter() == Join.RIGHT_OUTER){
				buf.append(j.getLeftElement().getOrigin().getName());
				buf.append("=");
				buf.append(j.getRightElement().getOrigin().getName());
				buf.append("(+)");
			}
			else if (j.getOuter() == Join.FULL_OUTER){
				buf.append(j.getLeftElement().getOrigin().getName());
				buf.append("(+)=");
				buf.append(j.getRightElement().getOrigin().getName());
				buf.append("(+)");
			}
			
			
		}
		return buf;
	}
	
	
	
	private StringBuffer getJoinForStandard(){
		StringBuffer buf = new StringBuffer();
		boolean first = true;
		
		boolean isLeftOuter = false;
		boolean isInner = false;
		boolean isRightOuter = false;
		boolean isFullOuter = false;
		
		if (joins.size() != 0){
			switch(joins.get(0).getOuter()){
			case Join.INNER:
				isInner = true;
				break;
			case Join.LEFT_OUTER:
				isLeftOuter = true;
				break;
			case Join.RIGHT_OUTER:
				isRightOuter = true;
				break;
			case Join.FULL_OUTER:
				isFullOuter = true;
				break;
			}
			
			
			if (isLeftOuter){
				buf.append(joins.get(0).getLeftElement().getOrigin().getTable().getName() + " `" + joins.get(0).getLeftElement().getDataStream().getName() + "`");
				buf.append(" LEFT OUTER JOIN " + joins.get(0).getRightElement().getOrigin().getTable().getName() + " `" + joins.get(0).getRightElement().getDataStream().getName() + "`");
				buf.append(" ON (");
			}
			else if (isRightOuter){
				buf.append(joins.get(0).getLeftElement().getOrigin().getTable().getName() + " `" + joins.get(0).getLeftElement().getDataStream().getName() + "`");
				buf.append(" RIGHT OUTER JOIN " + joins.get(0).getRightElement().getOrigin().getTable().getName() + " `" + joins.get(0).getRightElement().getDataStream().getName() + "`");
				buf.append(" ON (");
			}
			else if (isFullOuter){
				buf.append(joins.get(0).getLeftElement().getOrigin().getTable().getName() + " `" + joins.get(0).getLeftElement().getDataStream().getName() + "`");
				buf.append(" FULL OUTER JOIN " + joins.get(0).getRightElement().getOrigin().getTable().getName() + " `" + joins.get(0).getRightElement().getDataStream().getName() + "`");
				buf.append(" ON (");
			}
			
			
		}
		
		
		
		
		for(Join j : joins){
			if (first){
				first = false;
			}
			else{
				buf.append(" AND ");
			}
			
			buf.append(j.getDefaultOnStatement());
//			if(j.getLeftElement() instanceof ICalculatedElement) {
//				buf.append(((ICalculatedElement)j.getLeftElement()).getFormula());
//			}
//			else {
//				buf.append( "`" + j.getLeftElement().getDataStream().getName() + "`." + j.getLeftElement().getOrigin().getShortName());
//			}
//			buf.append("=");
//			if(j.getRightElement() instanceof ICalculatedElement) {
//				buf.append(((ICalculatedElement)j.getRightElement()).getFormula());
//			}
//			else {
//				buf.append("`" + j.getRightElement().getDataStream().getName() + "`." + j.getRightElement().getOrigin().getShortName());
//			}
			
			
			

		}
		
		if (!isInner){
			buf.append(")");
		}
		return buf;
	}



	@Override
	public String getWhereClause() {
		if(!isOuterJoin() && finalRelation != null && !finalRelation.isEmpty() && !finalRelation.equals("null")) {
			return finalRelation;
		}
		return getBasicWhereClause();
	}
	
}
