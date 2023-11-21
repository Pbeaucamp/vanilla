package bpm.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import bpm.fa.api.olap.unitedolap.UnitedOlapLoaderFactory;
import bpm.fa.api.repository.FaApiHelper;
import bpm.metadata.layer.business.AbstractBusinessTable;
import bpm.metadata.layer.business.BusinessModel;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.business.SQLBusinessTable;
import bpm.metadata.layer.logical.AbstractDataSource;
import bpm.metadata.layer.logical.ICalculatedElement;
import bpm.metadata.layer.logical.IDataSource;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.Join;
import bpm.metadata.layer.logical.MultiDSRelation;
import bpm.metadata.layer.logical.Relation;
import bpm.metadata.layer.logical.sql.SQLDataStream;
import bpm.metadata.layer.physical.IConnection;
import bpm.metadata.layer.physical.olap.UnitedOlapConnection;
import bpm.metadata.layer.physical.olap.UnitedOlapFactoryConnection;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.metadata.layer.physical.sql.SQLTable;
import bpm.metadata.resource.ComplexFilter;
import bpm.metadata.resource.Filter;
import bpm.metadata.resource.IFilter;
import bpm.metadata.resource.IResource;
import bpm.metadata.resource.ListOfValue;
import bpm.metadata.resource.Prompt;
import bpm.metadata.resource.SqlQueryFilter;
import bpm.metadata.resource.complex.FmdtDimension;
import bpm.metadata.resource.complex.FmdtMeasure;
import bpm.metadata.tools.Log;
import bpm.vanilla.platform.core.IRepositoryApi;
/**
 * This class is meant to connect the MetaData model to the Database/FACube 
 * @author ludo
 *
 */
public class MetaDataBuilder {
	

	protected StringBuffer errorBuffer = new StringBuffer();
	private FaApiHelper faHelper;
	
	public MetaDataBuilder(IRepositoryApi sock) {
		if (sock != null){
			try{
				this.faHelper = new FaApiHelper(sock.getContext().getVanillaContext().getVanillaUrl(), UnitedOlapLoaderFactory.getLoader());
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		
	}
	
	/**
	 * connect the FMDT model to its DataBases Connection or FASD Cube
	 * @param model : the metadata model to build
	 * @param sock : used if the MetaData model is atatch to an FASD Cube on the repository(may be null if not)
	 * @param groupName : used if the MetaData model is atatch to an FASD Cube on the repository(may be null if not)
	 * @throws BuilderException : if no Connection within dataSource can be established
	 * @throws Exception
	 */
	public void build(MetaData model, IRepositoryApi sock, String groupName) throws BuilderException, Exception{
		
		List<AbstractDataSource> dataSources = new ArrayList<AbstractDataSource>();
		for(AbstractDataSource ds : model.getDataSources()){
			
			boolean connectionOk = false;
			for(IConnection c : ds.getConnections()){
				c.configure(faHelper);
				try{
					c.test();
					connectionOk = true;
					break;
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
			}
			if (!connectionOk){
				dataSources.add(ds);
			}
		}
		
		if (!dataSources.isEmpty()){
//			throw new BuilderException(dataSources);
		}
		
		try{
			
			errorBuffer = new StringBuffer();

			// set the DataStream Origins
			for(AbstractDataSource ds : model.getDataSources()){
				
				int i = -1;
				for(IConnection con : ds.getConnections()){
//					IConnection con = ds.getConnection();
					try{
						i++;
						if (con instanceof UnitedOlapConnection){
							ds.setConnection(i, UnitedOlapFactoryConnection.createUnitedOlapConnection((UnitedOlapConnection)con));
							
							con = ds.getConnection();

						}
						
						for(IDataStream t : ds.getDataStreams()){
							t.setOrigin(con.getTable(t.getOriginName()));
							if (t.getOrigin() == null){
								
								t.setOriginName("public." + t.getOriginName());
								t.setOrigin(con.getTable(t.getOriginName()));
								
								if (t.getOrigin() == null){
									t.setOriginName(t.getOriginName().substring("public.".length()));
								}
								
							}
							
							if (t.getOrigin() == null){
								if (con instanceof SQLConnection){
									SQLConnection sds = (SQLConnection)con;
									try{
										t.setOrigin(sds.createTableFromQuery(/*t.getName(),*/ ((SQLDataStream)t).getSql()));
									}catch(Exception e){
										Log.info("Error on DataStream " + t.getName() + " from DataSource " + ds.getName() + " where originName=" + t.getOriginName() );
										errorBuffer.append("Error on DataStream " + t.getName() + " from DataSource " + ds.getName() + " where originName=" + t.getOriginName() + "\r\n" );
										e.printStackTrace();
										
									}
									
								}
								else if (con instanceof UnitedOlapConnection){
									UnitedOlapConnection sds = (UnitedOlapConnection)con;
									t.setOrigin(sds.createFromUniqueName(t.getOriginName()));
								}
								
								
							}
							for(IDataStreamElement c : t.getElements()){
								if (! (c instanceof ICalculatedElement)){
									if (t.getOrigin() != null){
										c.setOrigin(t.getOrigin().getElementNamed(c.getOriginName()));
										
										if (c.getOrigin() == null && t.getOrigin() instanceof SQLTable){
											errorBuffer.append("\t- Error on  " + c.getName() + " because cant found table in Connection\r\n" );
										}
									}
									else if (t.getOrigin() instanceof SQLTable){
										Log.info("Error on DataStreamElement " + c.getName() + " because cant found table in Connection");
										errorBuffer.append("\t- Error on  " + c.getName() + " because cant found table in Connection\r\n" );
									}
									
								}
							}	
						}
					}catch(Exception ex){
						ex.printStackTrace();
					}
				
				}
				for(Relation r : ds.getRelations()){
					r.setDataStreams(ds);
					
					for(Join j : r.getJoins()){
						j.setElements(ds.getDataStreamNamed(r.getLeftTableName()),
										ds.getDataStreamNamed(r.getRightTableName()));
					}
				}
				
			}

			clean(model);
			
			
			
			
			model.setBuilt();
		}catch(Exception e){
			Log.info("Unable to rebuild datasource ");
			throw e;
		}
		
		
	
	}
	
	
	
	
	
	
	public  void clean(MetaData model) throws Exception{
		for(IResource r : model.getResources()){
			if (r instanceof FmdtDimension){
				((FmdtDimension)r).build(model);
			}
			else if (r instanceof FmdtMeasure){
				((FmdtMeasure)r).build(model);
			}
			else if(r instanceof Prompt) {
				Prompt res = (Prompt) r;
				if(((Prompt)res).isChildPrompt()) {
					try {
						res.setParentPrompt((Prompt) model.getResource(res.getParentPromptName()));
					} catch(Exception e) {
						errorBuffer.append("-Error on Prompt " + r.getName() + " unable to find the parent prompt " + res.getParentPromptName() + "\n");
					}
				}
			}
			
			
			
		}
		//Relation extra DS
		for(MultiDSRelation r : model.getMultiDataSourceRelations()){
			r.setDatas(model);
		}
		
		

		for(IBusinessModel m : model.getBusinessModels()){
			//set the BusinessTables
			for(IBusinessTable t : ((BusinessModel)m).getBusinessTables()){
				//find the dataStream
				if (t instanceof AbstractBusinessTable){
					AbstractBusinessTable bT = (AbstractBusinessTable)t;
					for(String tName : bT.getColumnsNames().keySet()){
						IDataStream table = findDataStream(model, tName);
						if (table == null){
							errorBuffer.append("-Error for BusinessTable " +  t.getName() + " in Model " + m.getName() + " contains columns based on the  DataStream " + tName + " which does not exist\n");
							continue;
						}
						for(String cName : bT.getColumnsNames().get(tName)){
							try{
								Integer i = bT.getOrder(tName, cName);
								
								bT.addColumn(table.getElementNamed(cName));
								bT.order(table.getElementNamed(cName), i == null ? -1 : i );
								if (table.getElementNamed(cName) == null){
									errorBuffer.append("-Error for BusinessTable " +  t.getName() + " in Model " + m.getName() + " cant find column " + cName + " from datatstream" + tName + " \n");
								}
							}catch(Exception ex){
								ex.printStackTrace();
								if (table.getElementNamed(cName) == null){
									errorBuffer.append("-Error for BusinessTable " +  t.getName() + " in Model " + m.getName() + " cant find column " + cName + " from datatstream" + tName + " \n");
								}
							}
							
							
							
						}
					}
				}
//				else if (t instanceof UnitedOlapBusinessTable){
//					String dsName = ((UnitedOlapBusinessTable)t).getDataSourceName(); 
//					((UnitedOlapBusinessTable)t).setOlapDataSource((UnitedOlapDatasource)model.getDataSource(dsName));
//				}
				
			}
			
			//looking for the DataSource coming from the businessModel
			//businessTables may be empty
			IDataSource ds = null;
			for(IBusinessTable t : ((BusinessModel)m).getBusinessTables()){
				for(IDataStreamElement c : t.getColumns("none")){
					ds = c.getDataStream().getDataSource();
					if (ds != null){
						break;
					}
				}
				if (ds != null){
					break;
				}
				else if (!t.getChilds("none").isEmpty()){
					ds = ((SQLBusinessTable)t).getDataSource();
					if (ds != null){
						break;
					}
				}
			}
			for(Relation r : ((BusinessModel)m).getRelations()){
				try{
					r.setDataStreams(ds);
					
					for(Join j : r.getJoins()){
						j.setElements(ds.getDataStreamNamed(r.getLeftTableName()),
										ds.getDataStreamNamed(r.getRightTableName()));
					}
				}catch(Exception e){
					e.printStackTrace();
					errorBuffer.append("-Error for Relation in Model " + m.getName() + " where between" + r.getLeftTableName() + " and " + r.getRightTableName() + " : " + e.getMessage() + "\n");
				}
				
			}
			
			//set the listsOfValues
			for(IResource l : ((BusinessModel)m).getResources()){
				if (l instanceof ListOfValue){
					IDataStream t =findDataStream(model, ((ListOfValue)l).getDataStreamName());
					if (t != null){
						IDataStreamElement c = t.getElementNamed(((ListOfValue)l).getDataStreamElementName());
						((ListOfValue)l).setOrigin(c);
					}
					else{
						errorBuffer.append("-Error on LoV " + l.getName() + " in BusinessModel "  + m.getName() + " unable to find its DataStream " + ((ListOfValue)l).getDataStreamName() +  " containing column " + ((ListOfValue)l).getDataStreamElementName() + "\n");
					}
				}
				else if (l instanceof FmdtDimension){
					((FmdtDimension)l).build(model);
				}
				else if (l instanceof FmdtMeasure){
					((FmdtMeasure)l).build(model);
				}
				else if (l instanceof Filter){
					IDataStream t =findDataStream(model, ((Filter)l).getDataStreamName());
					if (t != null){
						IDataStreamElement c = t.getElementNamed(((Filter)l).getDataStreamElementName());
						((Filter)l).setOrigin(c);
					}
					else{
						errorBuffer.append("-Error on Filter " + l.getName() + " in BusinessModel "  + m.getName() + " unable to find its DataStream " + ((Filter)l).getDataStreamName() +  " containing column " + ((Filter)l).getDataStreamElementName() + "\n");
					}
				}
				else if (l instanceof SqlQueryFilter){
					IDataStream t =findDataStream(model, ((SqlQueryFilter)l).getDataStreamName());
					if (t != null){
						IDataStreamElement c = t.getElementNamed(((SqlQueryFilter)l).getDataStreamElementName());
						((SqlQueryFilter)l).setOrigin(c);
					}
					else{
						errorBuffer.append("-Error on SqlQueryFilter " + l.getName() + " in BusinessModel "  + m.getName() + " unable to find its DataStream " + ((SqlQueryFilter)l).getDataStreamName() +  " containing column " + ((SqlQueryFilter)l).getDataStreamElementName() + "\n");
					}
				}
				else if (l instanceof ComplexFilter){
					IDataStream t =findDataStream(model, ((ComplexFilter)l).getDataStreamName());
					if (t != null){
						IDataStreamElement c = t.getElementNamed(((ComplexFilter)l).getDataStreamElementName());
						((ComplexFilter)l).setOrigin(c);
					}
					else{
						errorBuffer.append("-Error on ComplexFilter " + l.getName() + " in BusinessModel "  + m.getName() + " unable to find its DataStream " + ((ComplexFilter)l).getDataStreamName() +  " containing column " + ((ComplexFilter)l).getDataStreamElementName() + "\n");
					}
				}
				else if (l instanceof Prompt){					
					Prompt res = (Prompt) l;
					if(((Prompt)res).isChildPrompt()) {
						try {
							res.setParentPrompt((Prompt) model.getResource(res.getParentPromptName()));
						} catch(Exception e) {
							errorBuffer.append("-Error on Prompt " + l.getName() + " unable to find the parent prompt " + res.getParentPromptName() + "\n");
						}
					}
					IDataStream t =findDataStream(model, ((Prompt)l).getOriginDataStreamName());
					if (t != null){
						IDataStreamElement c = t.getElementNamed(((Prompt)l).getOriginDataStreamElementName());
						((Prompt)l).setOrigin(c);
					}
					else{
						errorBuffer.append("-Error on Prompt " + l.getName() + " in BusinessModel "  + m.getName() + " unable to find its DataStream " + ((Prompt)l).getOriginDataStreamElementName() +  " containing column " + ((Prompt)l).getOriginDataStreamElementName() + "\n");
					}
					
					t =findDataStream(model, ((Prompt)l).getGotoDataStreamName());
					if (t != null){
						IDataStreamElement c = t.getElementNamed(((Prompt)l).getGotoDataStreamElementName());
						((Prompt)l).setGotoDataStreamElement(c);
					}
					else{
						errorBuffer.append("-Error on Prompt " + l.getName() + " in BusinessModel "  + m.getName() + " unable to find its DataStream " + ((Prompt)l).getGotoDataStreamElementName() +  " containing column " + ((Prompt)l).getGotoDataStreamElementName() + "\n");
					}
				}
				model.addResource(l);
			}
			
			//set the packages
			for(IBusinessPackage p : m.getBusinessPackages("none")){
				for(String s : p.getBusinessTableName()){
					p.addBusinessTable(((BusinessModel)m).getBusinessTable(s));
					
				}
				p.cleanBusinessTableContent();
				/*
				 * order
				 */
				for(String s : p.getBusinessTableName()){
					Integer i = p.getOrderPosition(s); 
					p.order(s, i);
				}
				for(String s : p.getResourceName()){
					IResource res = model.getResource(s);
					p.addResource(res);
				}
			}	
		}
		
		//filter on datastreams
		for(AbstractDataSource ds : model.getDataSources()){
			for(IDataStream st : ds.getDataStreams()){
				/*
				 *clean generic filters 
				 */
				for(IFilter f : st.getGenericFilters()){
					
					if (f instanceof Filter){
						try{
							((Filter)f).setOrigin(ds.getDataStreamNamed(((Filter)f).getDataStreamName()).getElementNamed(((Filter)f).getDataStreamElementName()));
						}catch(Exception ex){
							if (ds.getDataStreamNamed(((Filter)f).getDataStreamName()) == null){
								errorBuffer.append("-Error for Generic Filter " +  f.getName() + " " + " cant find Table " + ((Filter)f).getDataStreamName() + " from datatstream " + st.getName());
							}
							ex.printStackTrace();
						}
						
					}
					else if (f instanceof ComplexFilter){
						IDataStream t =findDataStream(model, ((ComplexFilter)f).getDataStreamName());
						if (t != null){
							IDataStreamElement c = t.getElementNamed(((ComplexFilter)f).getDataStreamElementName());
							((ComplexFilter)f).setOrigin(c);
						}
						else{
							errorBuffer.append("-Error for Generic ComplexFilter " +  f.getName() + " " + " cant find Table " + ((ComplexFilter)f).getDataStreamName() + " from datatstream " + st.getName());
						}
					}
					else if (f instanceof SqlQueryFilter){
						IDataStream t =findDataStream(model, ((SqlQueryFilter)f).getDataStreamName());
						if (t != null){
							IDataStreamElement c = t.getElementNamed(((SqlQueryFilter)f).getDataStreamElementName());
							((SqlQueryFilter)f).setOrigin(c);
						}
						else{
							errorBuffer.append("-Error for Generic SqlQueryFilter " +  f.getName() + " " + " cant find Table " + ((SqlQueryFilter)f).getDataStreamName() + " from datatstream " + st.getName());
						}
					}
					
				}
				/*
				 * clean securityFilters
				 */
				for(IFilter f : st.getFilters()){
					
					if (f instanceof Filter){
						try{
							((Filter)f).setOrigin(ds.getDataStreamNamed(((Filter)f).getDataStreamName()).getElementNamed(((Filter)f).getDataStreamElementName()));
						}catch(Exception ex){
							if (ds.getDataStreamNamed(((Filter)f).getDataStreamName()) == null){
								errorBuffer.append("-Error for Filter " +  f.getName() + " " + " cant find Table " + ((Filter)f).getDataStreamName() + " from datatstream " + st.getName());
							}
							ex.printStackTrace();
						}
						
					}
					else if (f instanceof ComplexFilter){
						IDataStream t =findDataStream(model, ((ComplexFilter)f).getDataStreamName());
						if (t != null){
							IDataStreamElement c = t.getElementNamed(((ComplexFilter)f).getDataStreamElementName());
							((ComplexFilter)f).setOrigin(c);
						}
						else{
							errorBuffer.append("-Error for ComplexFilter " +  f.getName() + " " + " cant find Table " + ((ComplexFilter)f).getDataStreamName() + " from datatstream " + st.getName());
						}
					}
					else if (f instanceof SqlQueryFilter){
						IDataStream t =findDataStream(model, ((SqlQueryFilter)f).getDataStreamName());
						if (t != null){
							IDataStreamElement c = t.getElementNamed(((SqlQueryFilter)f).getDataStreamElementName());
							((SqlQueryFilter)f).setOrigin(c);
						}
						else{
							errorBuffer.append("-Error for SqlQueryFilter " +  f.getName() + " " + " cant find Table " + ((SqlQueryFilter)f).getDataStreamName() + " from datatstream " + st.getName());
						}
					}
					
				}
			}
		}
		
		
		//filter on businesstables
		for(IBusinessModel m : model.getBusinessModels()){
			for(IBusinessTable st : ((BusinessModel)m).getBusinessTables()){
				for(IFilter f : st.getFilters()){
					
					if (f instanceof Filter){
//						((Filter)f).setOrigin(ds.getDataStreamNamed(((Filter)f).getDataStreamName()).getElementNamed(((Filter)f).getDataStreamElementName()));
						((Filter)f).setOrigin(st.getColumn("none", ((Filter)f).getDataStreamElementName()));//.getDataStreamNamed(((Filter)f).getDataStreamName()).getElementNamed(((Filter)f).getDataStreamElementName()));
					}
					else if (f instanceof ComplexFilter){
						IDataStream t =findDataStream(model, ((ComplexFilter)f).getDataStreamName());
						if (t != null){
							IDataStreamElement c = t.getElementNamed(((ComplexFilter)f).getDataStreamElementName());
							((ComplexFilter)f).setOrigin(c);
						}
						else{
							errorBuffer.append("-Error for ComplexFilter " +  f.getName() + " in BusinessTable " + st.getName() + " cant find Table " + ((ComplexFilter)f).getDataStreamName());
						}
					}
					else if (f instanceof SqlQueryFilter){
						IDataStream t =findDataStream(model, ((SqlQueryFilter)f).getDataStreamName());
						if (t != null){
							IDataStreamElement c = t.getElementNamed(((SqlQueryFilter)f).getDataStreamElementName());
							((SqlQueryFilter)f).setOrigin(c);
						}
						else{
							errorBuffer.append("-Error for SqlQueryFilter " +  f.getName() + " in BusinessTable " + st.getName() + " cant find Table " + ((SqlQueryFilter)f).getDataStreamName());
						}
					}
					
				}
			}
		}
		
		
		//find the relations between businessTables
		for(IBusinessModel m : model.getBusinessModels()){
			((BusinessModel)m).updateRelations(true);
		}

		for(IResource l : model.getResources()){
			if (l instanceof ListOfValue){
				IDataStream t =findDataStream(model, ((ListOfValue)l).getDataStreamName());
				if (t != null){
					IDataStreamElement c = t.getElementNamed(((ListOfValue)l).getDataStreamElementName());
					((ListOfValue)l).setOrigin(c);
				}
				else{
					errorBuffer.append("-Error for ListOfValue " +  l.getName()  + " cant find Table " + ((ListOfValue)l).getDataStreamName());
				}
			}
			else if (l instanceof Filter){
				IDataStream t =findDataStream(model, ((Filter)l).getDataStreamName());
				if (t != null){
					IDataStreamElement c = t.getElementNamed(((Filter)l).getDataStreamElementName());
					((Filter)l).setOrigin(c);
				}
				else{
					errorBuffer.append("-Error for Filter " +  l.getName()  + " cant find Table " + ((Filter)l).getDataStreamName());
				}
			}
			else if (l instanceof ComplexFilter){
				IDataStream t =findDataStream(model, ((ComplexFilter)l).getDataStreamName());
				if (t != null){
					IDataStreamElement c = t.getElementNamed(((ComplexFilter)l).getDataStreamElementName());
					((ComplexFilter)l).setOrigin(c);
				}
				else{
					errorBuffer.append("-Error for ComplexFilter " +  l.getName()  + " cant find Table " + ((ComplexFilter)l).getDataStreamName());
				}
			}
			else if (l instanceof Prompt){
				IDataStream t =findDataStream(model, ((Prompt)l).getOriginDataStreamName());
				if (t != null){
					IDataStreamElement c = t.getElementNamed(((Prompt)l).getOriginDataStreamElementName());
					((Prompt)l).setOrigin(c);
				}
				else{
					errorBuffer.append("-Error for Prompt " +  l.getName()  + " cant find Table " + ((Prompt)l).getOriginDataStreamElementName());
				}
				
				t =findDataStream(model, ((Prompt)l).getGotoDataStreamName());
				if (t != null){
					IDataStreamElement c = t.getElementNamed(((Prompt)l).getGotoDataStreamElementName());
					((Prompt)l).setGotoDataStreamElement(c);
				}
				else{
					errorBuffer.append("-Error for Prompt " +  l.getName()  + " cant find Table " + ((Prompt)l).getGotoDataStreamElementName());
				}
				
			}
			else if (l instanceof SqlQueryFilter){
				IDataStream t =findDataStream(model, ((SqlQueryFilter)l).getDataStreamName());
				if (t != null){
					IDataStreamElement c = t.getElementNamed(((SqlQueryFilter)l).getDataStreamElementName());
					((SqlQueryFilter)l).setOrigin(c);
				}
				else{
					errorBuffer.append("-Error on SqlQueryFilter " + l.getName() + " unable to find its DataStream " + ((SqlQueryFilter)l).getDataStreamName() +  " containing column " + ((SqlQueryFilter)l).getDataStreamElementName() + "\n");
				}
			}
		}

		
		
		//build tree structure of businesstables

		for(IBusinessModel m : model.getBusinessModels()){
			List<IBusinessTable> orphans = getOrphans(((BusinessModel)m).getBusinessTables());
			
			for(IBusinessTable t : orphans){
				((BusinessModel)m).removeBusinessTable(t);
			}
			
			for(IBusinessTable t : orphans){
				IBusinessTable parent = ((BusinessModel)m).getBusinessTable(((AbstractBusinessTable)t).getParentName());
				if (parent != null){
					((AbstractBusinessTable)parent).addChild(t);
				}
				else{
					setParent((AbstractBusinessTable)t, orphans);
				}
				if (t.getParent() == null){
//					System.err.println("unable to find parent for a businesstable (tableName = " +t.getName() + " parentName=" + ((AbstractBusinessTable)t).getParentName());
				}
			}
			
			
		}
	}
	
	protected void setParent(AbstractBusinessTable orphan, List<IBusinessTable> list){
		IBusinessTable parent = null;
		
		for(IBusinessTable t : list){
			if (t.getName().equals(orphan.getParentName())){
				((AbstractBusinessTable)t).addChild(orphan);
				return;
			}
			else{
				setParent(orphan, t.getChilds("none"));
			}
		}
		
		if (orphan.getParent() == null){
//			System.err.println("unable to find parent for a businesstable (tableName = " +orphan.getName() + " parentName=" + orphan.getParentName());
		}
	}
	
	protected List<IBusinessTable> getOrphans(Collection<IBusinessTable> col){
		List<IBusinessTable> orphans = new ArrayList<IBusinessTable>();
		for(IBusinessTable t : col){
				if (t instanceof AbstractBusinessTable && ((AbstractBusinessTable)t).getParentName() != null){
					orphans.add(t);
				}
				//else if ()
		}
		
		return orphans;
		
	}
	
	protected IDataStream findDataStream(MetaData model, String name){
		for(IDataSource ds : model.getDataSources()){
			for(IDataStream s : ds.getDataStreams()){
				if (s.getName().equals(name)){
					return s;
				}
			}
		}
		return null;
	}
	
	public String getErrorsBuffer(){
		return errorBuffer.toString();
	}

	
}
