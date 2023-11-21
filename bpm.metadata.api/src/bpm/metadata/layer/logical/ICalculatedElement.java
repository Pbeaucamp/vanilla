package bpm.metadata.layer.logical;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import bpm.metadata.layer.logical.sql.SQLDataSource;
import bpm.metadata.layer.physical.IColumn;
import bpm.metadata.layer.physical.sql.SQLColumn;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.metadata.layer.physical.sql.SQLTable;

public class ICalculatedElement extends IDataStreamElement {

	public static final String[] TYPES_NAMES = new String[]{"Double", "Integer", "String", "Date", "Object"};
	public static final  int DOUBLE = 0;
	public static final int INTEGER = 1;
	public static final int STRING = 2;
	public static final int DATE = 3;
	public static final int OBJECT = 4;
	
	
	private String formula;
	private String javaClassName;
	private int classType = 0;
	
	public ICalculatedElement(){}
	
	public ICalculatedElement(String formula){
		this.formula = formula;
	}
	
	@Override
	public List<String> getDistinctValues() {
		SQLConnection sock = (SQLConnection)((SQLDataSource)getDataStream().getDataSource()).getConnection();
		SQLTable table = (SQLTable)getDataStream().getOrigin();
		
		SQLTable t = null;
		
		
		if (table.isQuery()){
			t = new SQLTable(sock, SQLTable.QUERY);
		}
		else{
			t = new SQLTable(sock, SQLTable.TABLE);
		}
		t.setName(table.getName());
		
		SQLColumn col = new SQLColumn(t);
		col.setName(getFormula());
		t.addColumn(col);
		
		return col.getValues();
	}

	/**
	 * always return null
	 */
	@Override
	public IColumn getOrigin() {
		return origin;
	}

	@Override
	public boolean isCalculated() {
		return true;
	}
	
	public String getFormula(){
		return formula;
	}
	
	public void setFormula(String formula){
		this.formula = formula;
	}

	@Override
	public String getXml(){
		StringBuffer buf = new StringBuffer();
		buf.append("                <formulaElement>\n");
		buf.append("                     <name>" + name + "</name>\n");
		buf.append("                     <description>" + description + "</description>\n");
		buf.append("                     <fontName>" + fontName + "</fontName>\n");
		buf.append("                     <textColor>" + textColor + "</textColor>\n");
		buf.append("                     <backgroundColor>" + backgroundColor + "</backgroundColor>\n");
		buf.append("                     <classType>" + classType + "</classType>\n");
		buf.append("                     <type>" + type + "</type>\n");
		buf.append("                     <formula>" + formula.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;") + "</formula>\n");
		for(Locale l : outputName.keySet()){
			buf.append("            <outputname>\n");
			buf.append("                <country>" + l.getCountry() + "</country>\n");
			buf.append("                <language>" + l.getLanguage() + "</language>\n");
			buf.append("                <value>" + outputName.get(l) + "</value>\n");
			buf.append("            </outputname>\n");
		}
		buf.append("                </formulaElement>\n");

		return buf.toString();
	}
	
	public void setOutputName(String country, String language, String value){
		setOutputName(new Locale(language), value);
	}

	public void setClassType(String type){
		setClassType(Integer.parseInt(type));
	}
	public int getClassType(){
		return classType;
	}
	public void setClassType(int i){
		classType = i;
		switch (i){
		case INTEGER:
			javaClassName = "java.lang.Integer";
			break;
		case STRING:
			javaClassName = "java.lang.String";
			break;
		case DOUBLE:
			javaClassName = "java.lang.Double";
			break;
		case DATE:
			javaClassName = "java.util.Date";
			break;
		case OBJECT:
			javaClassName = "java.lang.Object";
			break;
		}
	}

	@Override
	public String getJavaClassName() throws Exception {
		
		return javaClassName;
	}

	@Override
	public List<String> getDistinctValues(HashMap<String, String> parentValues) throws Exception {
		return origin.getValues(parentValues);
	}


}
