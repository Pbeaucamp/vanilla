package bpm.forms.runtime.submission;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import bpm.forms.core.design.IFormDefinition;
import bpm.forms.core.design.IFormFieldMapping;
import bpm.forms.core.design.ITargetTable;
import bpm.forms.core.runtime.IFormInstance;
import bpm.forms.core.runtime.IFormInstanceFieldState;
import bpm.forms.core.runtime.IValidator;
import bpm.forms.runtime.VanillaFormsRuntimeComponent;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.beans.User;

public class Validator implements IValidator{
	private String clientIp;
	private User user;
	private VanillaFormsRuntimeComponent component;
	
	public Validator(VanillaFormsRuntimeComponent component, String clientIp, User user){
		this.clientIp = clientIp;
		this.user = user;
		this.component = component;
	}
	/**
	 * @return the clientIp
	 */
	public String getClientIp() {
		return clientIp;
	}


	public void validate(IFormInstance formInstance, Properties fieldValues, IRepositoryContext profil) throws Exception{
		
		IFormDefinition formDefinition = component.getFormServiceProvider().getDefinitionService().getFormDefinition(formInstance.getFormDefinitionId());
		
		if (formDefinition == null){
			throw new Exception("Unable to find FormDefinition for the FormInstance with id " + formInstance.getId());
		}
		
		
		for(ITargetTable table : formDefinition.getITargetTables()){
			/*
			 * check if the tables exists in the database 
			 */
			List<IFormFieldMapping> mappings = new ArrayList<IFormFieldMapping>();
			
			for(IFormFieldMapping fieldMap : formDefinition.getIFormFieldMappings()){
				if (fieldMap.getTargetTableId() != null && fieldMap.getTargetTableId() == table.getId()){
					mappings.add(fieldMap);
				}
			}
			
			checkTable(table, mappings);
			
			/*
			 * insert the values
			 */
			HashMap<IFormFieldMapping, String> values = new HashMap<IFormFieldMapping, String>();
			
			for(IFormFieldMapping fieldMap : formDefinition.getIFormFieldMappings()){
				if (fieldMap.getTargetTableId() != null && fieldMap.getTargetTableId() == table.getId()){
					
					Enumeration<String> en = (Enumeration)fieldValues.keys();
					
					while(en.hasMoreElements()){
						String key = en.nextElement();
						
						if (key.equalsIgnoreCase(fieldMap.getFormFieldId())){
							values.put(fieldMap, fieldValues.getProperty(key));
							break;
						}
					}
				}
			}
			
			insertValues(formInstance, values);
			
		}
		formInstance.setValidatorIp(getClientIp());
		if (user != null){
			formInstance.setValidatorUserId(user.getId());
		}
		
		formInstance.setLastValidationDate(new Date());

		formInstance.setIsValidated(true);
		component.getFormServiceProvider().getInstanceService().update(formInstance);

		
		for(IFormInstanceFieldState s : component.getFormServiceProvider().getInstanceService().getFieldsState(formInstance.getId())){
			s.setValidated(1);
			component.getFormServiceProvider().getInstanceService().update(s);
			
		}
	}
	
	
	private void insertValues(IFormInstance formInstance, HashMap<IFormFieldMapping, String> values /*, VanillaProfil profil*/) throws Exception{
		
		HashMap<Long, Object[]> queries = new HashMap<Long, Object[]>();
		
		List<IFormInstanceFieldState> states = component.getFormServiceProvider().getInstanceService().getFieldsState(formInstance.getId());
		for(IFormFieldMapping key : values.keySet()){
			IFormInstanceFieldState state = null;
			
			
			for(IFormInstanceFieldState fs : states){
				if (fs.getFormFieldMappingId() == key.getId()){
					state = fs;
					break;
				}
			}
			
			if (state == null){
				continue;
			}
			
			if (queries.get(key.getTargetTableId()) == null){
				StringBuffer buf = new StringBuffer();
				
				
				ITargetTable table = component.getFormServiceProvider().getDefinitionService().getTargetTable(key.getTargetTableId());
				buf.append("insert into " + table.getDatabasePhysicalName() + " (");
				buf.append(key.getDatabasePhysicalName());
				queries.put(key.getTargetTableId(), new Object[]{buf, 1, "'" + state.getValue() + "'"});
				
			}
			else{
				Integer i = (Integer)queries.get(key.getTargetTableId())[1];
				queries.get(key.getTargetTableId())[1] = i+1;
				
				((StringBuffer)queries.get(key.getTargetTableId())[0]).append(", " + key.getDatabasePhysicalName());
				queries.get(key.getTargetTableId())[2] = ((String)queries.get(key.getTargetTableId())[2]) + ", '" + state.getValue() + "'"; 
			}
			
			
//			Activator.getServiceProvider().getInstanceService().save(state);
			
		}
		Connection con = null;
		Statement stmt = null;
		
		try{
			con = getJdbcConnection();
			stmt = con.createStatement();
			
			for (Long key : queries.keySet()){
				String q = ((StringBuffer)queries.get(key)[0]).toString() + ") values(" + ((String)queries.get(key)[2]) + ")";
				
				
				try{
					stmt.execute(q); 
				}catch (Exception e) {
					component.getLogger().error("Error when Validator performing insert : " + q, e);
					e.printStackTrace();
					
				}
			}
		}catch(SQLException ex){
			component.getLogger().error("Error on Validation", ex);
		}finally{
			if (stmt != null){
				stmt.close();
				
			}
			if (con != null){
				con.close();
			}
		}
		
		
	}


	private Connection getJdbcConnection() throws Exception{
		DataSource dataSource = component.getFormDataSourceProvider().getDataSource();
		if (dataSource == null){
			throw new Exception("The ServiceProvider cannot find the DataBase DataSource");
		}
		
		return dataSource.getConnection();
	}
	
	
	private void checkTable(ITargetTable table, List<IFormFieldMapping> fields) throws Exception{
		
		Connection sock = null;
		try{
			sock = getJdbcConnection();
		}catch(SQLException ex){
			throw new Exception("Unable to get the DataBase JDBC Connection from the DataSource Connection");
		}catch(Exception ex){
			throw ex;
		}
	
		DatabaseMetaData dbmd = sock.getMetaData();
		ResultSet rsTable = null;
		
		boolean tableFound = false;
		
		
		component.getLogger().info("Looking for table " + table.getDatabasePhysicalName() + " in database ...");
		try{
			
			if (table.getDatabasePhysicalName().contains(".")){
				String[] tableNameParts = table.getDatabasePhysicalName().split(".");
				rsTable = dbmd.getTables(null, tableNameParts[0], tableNameParts[0], null);
			}
			else{
				rsTable = dbmd.getTables(null, "", table.getDatabasePhysicalName(), null);
			}
			
			while(rsTable.next()){
				tableFound = true;
			}
		}catch(SQLException ex){
			component.getLogger().error("Error when looking for table " + table.getDatabasePhysicalName(), ex);
		}
		finally{
			if (rsTable != null){
				rsTable.close();
			}
		}
		
		if (!tableFound){
			component.getLogger().info("Table " +  table.getDatabasePhysicalName() + " not found in database.");
			createTable(table, fields);
			
		}
		else{
			component.getLogger().info("Table " +  table.getDatabasePhysicalName() + " found in database.");
			
			/*
			 * check the columns
			 */
			List<IFormFieldMapping> missingColumns = new ArrayList<IFormFieldMapping>();
			
			for(IFormFieldMapping field : fields){
				ResultSet rsColumn = null;
				boolean columnFound = false;
				
				component.getLogger().info("Looking for column " + field.getDatabasePhysicalName() + " in table " + table.getDatabasePhysicalName() + " ...");
				
				
				try{
					if (table.getDatabasePhysicalName().contains(".")){
						String[] tableNameParts = table.getDatabasePhysicalName().split(".");
						rsColumn = dbmd.getColumns(null, tableNameParts[0], tableNameParts[0], field.getDatabasePhysicalName());
					}
					else{
						rsColumn = dbmd.getColumns(null, "", table.getDatabasePhysicalName(), field.getDatabasePhysicalName());
					}
					
					while(rsColumn.next()){
						columnFound = true;
					}
				}catch(SQLException ex){
					component.getLogger().error("Error when looking for column " + field.getDatabasePhysicalName() + " from table " + table.getDatabasePhysicalName(), ex);
					ex.printStackTrace();
				}finally{
					if (rsColumn != null){
						rsColumn.close();
					}
					
				}
				
				if (!columnFound){
					missingColumns.add(field);
				}
				
			}
			
			if (!missingColumns.isEmpty()){
				createMissingColumns(table, missingColumns);
			}
			
		}
	}

	private void createMissingColumns(ITargetTable table, List<IFormFieldMapping> missingColumns) throws Exception{
		
		/*
		 * build query
		 */
		StringBuffer buf = new StringBuffer();
		
		for(IFormFieldMapping m : missingColumns){
			buf.append("ALTER TABLE ");
			buf.append(table.getDatabasePhysicalName());
			buf.append(" ADD ");
			buf.append(m.getDatabasePhysicalName());
			buf.append(" " + m.getSqlDataType() +";\n");
		}
		
		
		
		
		/*
		 * alter the Table
		 */
		Connection sock = null;
		try{
			sock = getJdbcConnection();
		}catch(SQLException ex){
			throw new Exception("Unable to get the DataBase JDBC Connection from the DataSource Connection");
		}catch(Exception ex){
			throw ex;
		}
		Statement stmt = null;
		
		try{
			component.getLogger().info("Executing DDL script : " + buf.toString());
			stmt = sock.createStatement();
			stmt.execute(buf.toString());
		}catch(Exception ex){
			
			if (stmt != null){
				stmt.close();
			}
			
			throw new Exception("Error when altering Table " + table.getDatabasePhysicalName() + " : " + ex.getMessage(), ex);
		}
		
	}


	private void createTable(ITargetTable table, List<IFormFieldMapping> fields) throws Exception{
		
		StringBuffer buf = new StringBuffer();
		
		/*
		 * build the query
		 */
		buf.append("CREATE TABLE ");
		buf.append(table.getDatabasePhysicalName());
		buf.append("(");
		
		boolean first = true;
		for(IFormFieldMapping m : fields){
			if (first){
				first = false;
			}
			else{
				buf.append(", ");
			}
			buf.append(m.getDatabasePhysicalName() );
			buf.append(" " + m.getSqlDataType() +" ");
		}
		buf.append(")");
		
		/*
		 * create the Table
		 */
		Connection sock = null;
		try{
			sock = getJdbcConnection();
		}catch(SQLException ex){
			throw new Exception("Unable to get the DataBase JDBC Connection from the DataSource Connection");
		}catch(Exception ex){
			throw ex;
		}
		Statement stmt = null;
		
		try{
			component.getLogger().info("Executing DDL script : " + buf.toString());
			stmt = sock.createStatement();
			stmt.execute(buf.toString());
		}catch(Exception ex){
			
			if (stmt != null){
				stmt.close();
			}
			
			throw new Exception("Error when creating Table " + table.getDatabasePhysicalName() + " : " + ex.getMessage(), ex);
		}
		
	}


	public void invalidate(IFormInstance formInstance, Properties properties, IRepositoryContext vanillaContext) throws Exception{
		formInstance.setIsValidated(false);
		formInstance.setIsSubmited(false);
		formInstance.setValidatorIp(getClientIp());
		formInstance.setLastValidationDate(new Date());
		component.getFormServiceProvider().getInstanceService().update(formInstance);
	}
	
}
