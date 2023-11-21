package bpm.metadata.digester;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.commons.digester3.Digester;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.query.QuerySql;
import bpm.metadata.query.UnitedOlapQuery;

public class SqlQueryDigester {
	private class MyErrorHandler implements ErrorHandler{

		public void error(SAXParseException arg0) throws SAXException {
			arg0.printStackTrace();
			throw new SAXException("erreur de parse", arg0);
		}

		public void fatalError(SAXParseException arg0) throws SAXException {
			arg0.printStackTrace();
			throw new SAXException("fatal error", arg0);
		}


		public void warning(SAXParseException arg0) throws SAXException {
			arg0.printStackTrace();
			throw new SAXException("warning", arg0);
		}
		
	}
	

	private QuerySql model;
	private Digester dig;
	
	private BeanFmdtQuery bean;
	private String groupName;
	private IBusinessPackage pack;
	
	public SqlQueryDigester(InputStream input, String groupName, IBusinessPackage pack) throws Exception{
		this.groupName = groupName;
		this.pack = pack;
		dig = new Digester();
		dig.setErrorHandler(new MyErrorHandler());
		createCallbacks(dig);
		bean = ((BeanFmdtQuery)dig.parse(input));
		
	}
	
	public SqlQueryDigester(File file, String groupName, IBusinessPackage pack) throws Exception{
		this.groupName = groupName;
		this.pack = pack;
		dig = new Digester();
		dig.setErrorHandler(new MyErrorHandler());
		createCallbacks(dig);
		bean = (BeanFmdtQuery) dig.parse(file);
		
	}
	
	public SqlQueryDigester(String path, String groupName, IBusinessPackage pack) throws Exception{
		this.groupName = groupName;
		this.pack = pack;
		dig = new Digester();
		dig.setErrorHandler(new MyErrorHandler());
		createCallbacks(dig);
		File file = new File(path);
		bean = (BeanFmdtQuery) dig.parse(file);
	}
	
	
	protected void createCallbacks(Digester dig){
		dig.setValidating(false);
		
		String root = "freeMetaDataQuery";
		
		dig.addObjectCreate(root , BeanFmdtQuery.class);
		dig.addCallMethod(root + "/sqlQuery/prompt", "addPrompt", 0);

		dig.addCallMethod(root + "/sqlQuery/formula", "addFormula", 3);
		dig.addCallParam(root + "/sqlQuery/formula/name", 0);
		dig.addCallParam(root + "/sqlQuery/formula/script", 1);
		dig.addCallParam(root + "/sqlQuery/formula/dataStreamNames", 2);
		
		dig.addCallMethod(root + "/sqlQuery/orderBy", "addOrder", 2);
		dig.addCallParam(root + "/sqlQuery/orderBy/dataStreamName", 0);
		dig.addCallParam(root + "/sqlQuery/orderBy/dataStreamElementName", 1);
		
		dig.addCallMethod(root + "/sqlQuery/orderByAgg", "addOrder", 1);
		dig.addCallParam(root + "/sqlQuery/orderByAgg/agg", 0);
		
		dig.addCallMethod(root + "/sqlQuery/orderByForm", "addOrderForm", 3);
		dig.addCallParam(root + "/sqlQuery/orderByForm/name", 0);
		dig.addCallParam(root + "/sqlQuery/orderByForm/script", 1);
		dig.addCallParam(root + "/sqlQuery/orderByForm/dataStreamNames", 2);
		
		dig.addCallMethod(root + "/sqlQuery/distinct", "setDistinct", 0);
		dig.addCallMethod(root + "/sqlQuery/hideNull", "setHideNull", 0);
		dig.addCallMethod(root + "/sqlQuery/limit", "setLimit", 0);
		
		dig.addCallMethod(root + "/sqlQuery/select", "addSelect", 2);
		dig.addCallParam(root + "/sqlQuery/select/dataStreamElementName", 0);
		dig.addCallParam(root + "/sqlQuery/select/dataStreamName", 1);
		
		dig.addCallMethod(root + "/sqlQuery/selectFromBusinessTable", "addSelectFromBusinessTable", 2);
		dig.addCallParam(root + "/sqlQuery/selectFromBusinessTable/dataStreamElementName", 0);
		dig.addCallParam(root + "/sqlQuery/selectFromBusinessTable/businessTableName", 1);
		
		dig.addCallMethod(root + "/sqlQuery/agg", "addAgg", 5, new Class[]{String.class, String.class, String.class, String.class, String.class});
		
		dig.addCallParam(root + "/sqlQuery/agg/function", 0);
		dig.addCallParam(root + "/sqlQuery/agg/col", 1);
		
		dig.addCallParam(root + "/sqlQuery/agg/dataStreamName", 2);
		dig.addCallParam(root + "/sqlQuery/agg/outputName", 3);
		dig.addCallParam(root + "/sqlQuery/agg/formula", 4);

		dig.addCallMethod(root + "/sqlQuery/where", "addFilter", 3, new Class[]{ArrayList.class, String.class, String.class});
		
		dig.addObjectCreate(root + "/sqlQuery/where/values", ArrayList.class);
		dig.addCallMethod(root + "/sqlQuery/where/values/value", "add", 0);
		dig.addCallParam(root + "/sqlQuery/where/values", 0, true);
		dig.addCallParam(root + "/sqlQuery/where/dataStream", 1);
		dig.addCallParam(root + "/sqlQuery/where/dataStreamElement",2);
		
		dig.addCallMethod(root + "/sqlQuery/sqlqueryfilter", "addSqlQueryFilter", 3, new Class[]{String.class, String.class, String.class});
		
		dig.addCallParam(root + "/sqlQuery/sqlqueryfilter/query", 0);
		dig.addCallParam(root + "/sqlQuery/sqlqueryfilter/dataStream", 1);
		dig.addCallParam(root + "/sqlQuery/sqlqueryfilter/dataStreamElement",2);
		
		
		dig.addCallMethod(root + "/sqlQuery/filter", "addFilter", 0);

		
		dig.addCallMethod(root + "/sqlQuery/relationClause", "addRelation", 4, new Class[]{String.class, String.class, String.class, String.class});

		dig.addCallParam(root + "/sqlQuery/relationClause/leftDataStream",0);
		dig.addCallParam(root + "/sqlQuery/relationClause/leftDataStreamElement",1);
		dig.addCallParam(root + "/sqlQuery/relationClause/rightDataStream", 2);
		dig.addCallParam(root + "/sqlQuery/relationClause/rightDataStreamElement", 3);
		
		dig.addCallMethod(root + "/sqlQuery/additionalResource","addResourceDefinition", 0);
		
		dig.addCallMethod(root + "/sqlQuery/relationStrategy","addRelationStrategy", 0);
	
	}
	
	public QuerySql getModel() throws Exception{
		
		if(model == null) {
			model = bean.getQuerySql(groupName, pack);
		}
		
		return model;
	}
	
	public UnitedOlapQuery getUOlapModel() throws Exception {
		return bean.getUnitedOlapQuery(groupName, pack);
	}
	
	
	
}
