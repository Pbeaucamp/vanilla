package bpm.fd.runtime.engine.datas;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.DrillDrivenComponentConfig;
import bpm.fd.api.core.model.components.definition.IComponentDatas;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.IDimensionableDatas;
import bpm.fd.api.core.model.components.definition.OutputParameter;
import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.datas.DataSource;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.runtime.engine.VanillaProfil;

public class JSPDataGenerator {

	private StringBuffer packageImports = new StringBuffer();
	private StringBuffer dataSources = new StringBuffer();
	private StringBuffer javascriptParameters = new StringBuffer();
	private Dictionary dictionary;
	
	private List<DataSet> generatedDataSet = new ArrayList<DataSet>();
	private List<DataSet> generatedQueries = new ArrayList<DataSet>();
	private List<DataSource> generatedDataSource = new ArrayList<DataSource>();
	
	public JSPDataGenerator (Dictionary dictionary){
		this.dictionary = dictionary;
	}
	
	
	/**
	 * 
	 * @param components
	 * @return list of the generated ParametersNames
	 * @throws Exception
	 */
	public List<String> generateJSPCode(HashMap<IComponentDefinition, ComponentConfig> components, VanillaProfil vanillaProfil) throws Exception{
//		generatedDataSet.clear();
//		generatedDataSource.clear();
//		generatedQueries.clear();
		Logger.getLogger(getClass()).debug("generating datasJSp for item "  + vanillaProfil.getDirectoryItemId());
		
		generatePackageImport();
		
		
		dataSources.append("    Properties publicProperties = null;\n");
		dataSources.append("    Properties privateProperties = null;\n\n");
		dataSources.append("    String queryText = null;\n\n");
		
		
		dataSources.append("/*******************************\n");
		dataSources.append("* DataSources\n");
		dataSources.append("*******************************/\n");
		for(IComponentDefinition c : components.keySet()){
			if (c.getDatas() != null){
				generateDataSourceJSP(c.getDatas(), vanillaProfil);
				if (c.getDatas() instanceof IDimensionableDatas){
					generateDataSourceJSP(((IDimensionableDatas)c.getDatas()).getDimensionDatas(), vanillaProfil);
				}
			}
		}
		dataSources.append("\n\n\n");
		dataSources.append("/*******************************\n");
		dataSources.append("* DataSets\n");
		dataSources.append("*******************************/\n");
		for(IComponentDefinition c : components.keySet()){
			if (c.getDatas() != null){
				generateDataSetJSP(vanillaProfil.getDirectoryItemId(), c.getDatas());
				if (c.getDatas() instanceof IDimensionableDatas){
					generateDataSetJSP(vanillaProfil.getDirectoryItemId(), ((IDimensionableDatas)c.getDatas()).getDimensionDatas());
				}
			}
			else{
				Logger.getLogger(getClass()).info("skipped dataset generation for component " + c.getName() + "(" + c.getClass().getCanonicalName() + ") for item=" + vanillaProfil.getDirectoryItemId());
			}
		}
		
		
		
		dataSources.append("\n\n\n");
		dataSources.append("/*******************************\n");
		dataSources.append("* Queries\n");
		dataSources.append("*******************************/\n");
		for(IComponentDefinition c : components.keySet()){
			if (c.getDatas() != null){
				generateResultSetsJSP(vanillaProfil.getDirectoryItemId(), c.getDatas());
				if (c.getDatas() instanceof IDimensionableDatas){
					generateResultSetsJSP(vanillaProfil.getDirectoryItemId(), ((IDimensionableDatas)c.getDatas()).getDimensionDatas());
				}
			}
		}

		
		dataSources.append("\n\n\n");
		dataSources.append("/*******************************\n");
		dataSources.append("* Parameters\n");
		dataSources.append("*******************************/\n");
		List<String> pnames = generateParameters(vanillaProfil.getDirectoryItemId(), components.values());
		
		return pnames;
	
	}
	
	public String getParameterJavascript(){
		return javascriptParameters.toString();
	}
	
	public String getImports(){
		return packageImports.toString();
	}
	
	public String getJSPBlock(){
		return dataSources.toString();
	}
	
	private List<String> generateParameters(Integer directoryItemId, Collection<ComponentConfig> configs){
		List<String> added = new ArrayList<String>();
		
		StringBuffer parameterMap = new StringBuffer();
		dataSources.append("    HashMap<String, String> _parameterMap = new HashMap<String, String>();\n");
		
		for(ComponentConfig conf : configs){
			if (conf == null){
				continue;
			}
			
			if (conf instanceof DrillDrivenComponentConfig){
				
				DrillDrivenComponentConfig ddc = (DrillDrivenComponentConfig)conf;
				
				boolean found = false;
				for(String s : added){
					if (s.equals(ddc.getDrillDrivenCell().getId())){
						found = true;
						break;
					}
				}
				if (!found){
					dataSources.append("   String " + ddc.getDrillDrivenCell().getId() + " = null;\n");
					dataSources.append("   if(request.getParameter(\"" + ddc.getDrillDrivenCell().getId() + "\") != null) {\n");
					dataSources.append("   		" + ddc.getDrillDrivenCell().getId() + " = URLDecoder.decode(request.getParameter(\"" + ddc.getDrillDrivenCell().getId() + "\"), \"UTF-8\");\n");
					dataSources.append("   }\n");
					javascriptParameters.append("out.write(\"parameters[\\\"" + ddc.getDrillDrivenCell().getId() + "\\\"]='\" + " + ddc.getDrillDrivenCell().getId() + " + \"';\\n\");\n");
					added.add(ddc.getDrillDrivenCell().getId());
				}
				
				
			}
			
			
			for(ComponentParameter p : conf.getParameters()){
				boolean found = false;
				for(String s : added){
					if (s.equals(p.getName() + "Parameter")){
						found = true;
						break;
					}
				}
				if (found){
					Logger.getLogger(getClass()).info("skip Parameter " + p.getName() +  " for Target " + conf.getTargetComponent().getName() + " and item=" + directoryItemId);
					continue;
				}
				Logger.getLogger(getClass()).info("Generating Parameter " + p.getName() +  " for Target " + conf.getTargetComponent().getName()+ " and item=" + directoryItemId);
				
				
				
				
				
				dataSources.append("   String " + p.getName() + "Parameter = null;\n");
				dataSources.append("   if(request.getParameter(\"" + conf.getComponentNameFor(p).replace(" ", "_") + "\") != null) {\n");
				dataSources.append("   		" + p.getName() + "Parameter = URLDecoder.decode(request.getParameter(\"" + conf.getComponentNameFor(p).replace(" ", "_") + "\"), \"UTF-8\");\n");
				dataSources.append("   }\n");
				added.add(p.getName() + "Parameter");
				
				
				/*
				 * complex provide like DataGrid
				 */
				if (conf.getComponentOutputName(p) != null){
					dataSources.append("    if (" + p.getName() + "Parameter == null || \"\".equals(" + p.getName() + "Parameter) || \"null\".equals("+ p.getName() + "Parameter)){\n");
					dataSources.append("        " + p.getName() + "Parameter = request.getParameter(\"" + p.getId() + "\");\n");
					dataSources.append("}\n");
					
				}
				if (p.getDefaultValue() != null){
					dataSources.append("    if (" + p.getName() + "Parameter == null){\n");
					dataSources.append("        " + p.getName() + "Parameter = \"" + p.getDefaultValue() + "\";\n");
					dataSources.append("}\n");
				}
				
				dataSources.append("    _parameterMap.put(\"" + p.getName() + "\", " + p.getName() + "Parameter);\n");
				javascriptParameters.append("out.write(\"parameters[\\\"" + conf.getComponentNameFor(p).replace(" ", "_") + "\\\"]='\" + " + p.getName() + "Parameter + \"';\\n\");\n");
				
				if (p instanceof OutputParameter){
					javascriptParameters.append("if (" + p.getName() + "Parameter != null){\n");
					javascriptParameters.append("     out.write(\"parametersOut[\\\"" + conf.getComponentNameFor(p).replace(" ", "_") + "\\\"]='" + p.getName() + "';\\n\");\n");
					javascriptParameters.append("}else{\n");
					javascriptParameters.append("     out.write(\"parametersOut[\\\"" + conf.getComponentNameFor(p).replace(" ", "_") + "\\\"]=null;\\n\");\n");
					javascriptParameters.append("}\n");
				}
				
			}
		}
		
		
		
		dataSources.append("\n\n");
		
		
		return added;
	}
	
	private void generatePackageImport(){
		packageImports.append("java.util.Properties, \n");
		packageImports.append("java.util.List, \n");
		packageImports.append("java.util.Iterator, \n");
		packageImports.append("java.util.HashMap, \n");
		packageImports.append("java.util.Locale, \n");
		packageImports.append("java.io.FileInputStream, \n");
		
		packageImports.append("bpm.repository.api.axis.model.AxisRepositoryConnection, \n");
		packageImports.append("bpm.repository.api.model.FactoryRepository, \n");
		packageImports.append("bpm.repository.api.model.IComment, \n");
		packageImports.append("bpm.repository.api.model.IRepositoryConnection, \n");
		
		packageImports.append("bpm.fd.api.core.model.datas.DataSource, \n");
		packageImports.append("bpm.fd.api.core.model.datas.DataSet, \n");
		packageImports.append("bpm.fd.api.core.model.datas.QueryHelper, \n");
		packageImports.append("bpm.fd.runtime.engine.datas.helper.OlapViewHelper, \n");
		packageImports.append("bpm.fd.runtime.engine.datas.helper.ChartReportHelper, \n");
		packageImports.append("bpm.fd.runtime.engine.I18NReader, \n");
		packageImports.append("bpm.fd.runtime.engine.datas.helper.Aggregationer, \n");
		//XXX: add the connectionManager import
		packageImports.append("bpm.fd.runtime.engine.datas.helper.FdConnectionManager, \n");
		packageImports.append("bpm.fd.runtime.engine.datas.helper.Sorter, \n");
		packageImports.append("bpm.fd.runtime.engine.datas.helper.ReportHelper, \n");
		packageImports.append("bpm.fd.runtime.engine.chart.fusion.generator.FusionChartXmlGenerator, \n");
		packageImports.append("bpm.fd.runtime.engine.chart.fusion.generator.FusionChartMultiSeriesXmlGenerator, \n");
		packageImports.append("bpm.fd.runtime.engine.chart.freefusion.generator.FFCXmlGenerator, \n");
		packageImports.append("bpm.fd.runtime.engine.chart.freefusion.generator.FFCMultiSeriesXmlGenerator, \n");
		packageImports.append("bpm.fd.runtime.engine.chart.jfree.generator.*, \n");
		packageImports.append("bpm.fd.runtime.engine.map.fusionmap.MapDrillRuntime, \n");
		
		
		packageImports.append("org.eclipse.datatools.connectivity.oda.IResultSet, \n");
		packageImports.append("bpm.fd.runtime.engine.chart.ofc.generator.OpenFlashChartJsonGenerator, \n");
		packageImports.append("bpm.fd.runtime.engine.chart.ofc.generator.MultiOpenFlashChartJsonGenerator, \n");
		packageImports.append("bpm.vanilla.map.core.design.fusionmap.ColorRange, \n");
		packageImports.append("bpm.fd.runtime.engine.map.fusionmap.FusionMapXmlGenerator, \n");
		packageImports.append("bpm.fd.runtime.engine.map.fusionmap.VanillaMapXmlGenerator, \n");
		packageImports.append("bpm.fd.runtime.engine.map.googlemap.GoogleMapGenerator, \n");
		packageImports.append("bpm.fd.runtime.engine.map.freemetrics.MapFreeMetricsDataGenerator, \n");
		
		packageImports.append("bpm.vanilla.platform.core.beans.FMContext, \n");
		packageImports.append("bpm.vanilla.platform.core.beans.FMMetricBean, \n");
		
		packageImports.append("org.apache.log4j.Logger, \n");
//		packageImports.append("bpm.fd.runtime.engine.JSPHelper, \n");
		packageImports.append("java.net.*, \n");
		
		
		packageImports.append("java.io.PrintWriter, \n");
		packageImports.append("java.io.InputStream, \n");
//		packageImports.append("org.apache.commons.io.IOUtils, \n");
		packageImports.append("org.eclipse.datatools.connectivity.oda.IQuery\n");
		
	}
	
	private void generateDataSourceJSP(IComponentDatas datas, VanillaProfil vanillaProfil) throws Exception{
		if (datas.getDataSet() == null){
			return;
		}
		DataSource ds = dictionary.getDatasource(datas.getDataSet().getDataSourceName());
		
		if (ds == null){
			throw new Exception("Cannot find dataSource named " + datas.getDataSet().getDataSourceName() + " in rhe dictionary");
		}
		
		if (generatedDataSource.contains(ds)){
			return;
		}
		//public prop
		String varName = "publicProperties";
		
		dataSources.append("    " + varName + " = new Properties();\n");
		for(Object k : ds.getPublicProperties().keySet()){
			dataSources.append("    " + varName + ".setProperty(\"" + (String)k + "\", \"" + ds.getPublicProperties().getProperty((String)k) + "\");\n");
		}
		
		//privateprop
		varName = "privateProperties";
		dataSources.append("    " + varName + " = new Properties();\n");
		for(Object k : ds.getPrivateProperties().keySet()){
			dataSources.append("    " + varName + ".setProperty(\"" + (String)k + "\", \"" + ds.getPrivateProperties().getProperty((String)k) + "\");\n");
		}
		//XXX : change that to use the connectionManager
		dataSources.append("    DataSource " + ds.getId() + " = new  DataSource(\n");
		dataSources.append("        null, \"" + ds.getId() + "\", \n");
		dataSources.append("        \"" + ds.getOdaExtensionDataSourceId() + "\", \n");
		dataSources.append("        \"" + ds.getOdaExtensionId() + "\", \n");
		dataSources.append("        \"" + ds.getOdaDriverClassName() + "\", ");
		dataSources.append("        publicProperties, privateProperties);\n");
		dataSources.append("       FdConnectionManager.getInstance().addConnection("+ds.getId()+","+vanillaProfil.getDirectoryItemId()+","+ vanillaProfil.getRepositoryId()+");\n");
		dataSources.append("\n\n");
		
		generatedDataSource.add(ds);
	}
	
	private void generateDataSetJSP(Integer directoryItemId, IComponentDatas datas){
		DataSet ds = datas.getDataSet();
		
		if (generatedDataSet.contains(ds) || ds == null){
			Logger.getLogger(getClass()).warn("Data's dataSet is null "  + " for item=" + directoryItemId);
			return;
		}
		Logger.getLogger(getClass()).info("Generating DataSet " + ds.getId() + " for item=" + directoryItemId);
		//public prop
		String varName = "publicProperties";
		
		dataSources.append("    " + varName + " = new Properties();\n");
		for(Object k : ds.getPublicProperties().keySet()){
			dataSources.append("    " + varName + ".setProperty(\"" + (String)k + "\", \"" + ds.getPublicProperties().getProperty((String)k) + "\");\n");
		}
		//privateprop
		varName = "privateProperties";
		dataSources.append("    " + varName + " = new Properties();\n");
		for(Object k : ds.getPrivateProperties().keySet()){
			dataSources.append("    " + varName + ".setProperty(\"" + (String)k + "\", \"" + ds.getPrivateProperties().getProperty((String)k) + "\");\n");
		}
		
		//query
		String q = new String(ds.getQueryText());
		if (q.contains("\"")){
			q = q.replace("\"", "\\\"").replace("\n", "");
		}
		String _q = null;
		try{
			_q = q.replaceAll("\\s", " ");
		}catch(Exception ex){
			ex.printStackTrace();
			_q = q.replace("\r\n", " ").replace("\n", " ");
		}
		dataSources.append("    queryText = \"" + _q + "\";\n");
//		dataSources.append("    queryText = new String(\"" + _q + "\".getBytes(), \"UTF-8\");\n");
		dataSources.append("    DataSet " + ds.getId() + "DataSet = new  DataSet(\n");
		dataSources.append("        \"" + ds.getId() + "\", \n");
		if (ds.getOdaExtensionDataSetId() != null){
			dataSources.append("        \"" + ds.getOdaExtensionDataSetId() + "\", \n");
		}
		else{
			dataSources.append("        null\n,");
		}
		
		dataSources.append("        \"" + ds.getOdaExtensionDataSourceId() + "\", \n");
		dataSources.append("        publicProperties, privateProperties,\n");
		dataSources.append("        queryText,\n");
		dataSources.append("        " + ds.getDataSourceName() + ");\n");

		dataSources.append("\n\n");
		
		generatedDataSet.add(ds);
	}

	private void generateResultSetsJSP(Integer directoryItemId, IComponentDatas datas){
		
		DataSet ds = datas.getDataSet();
		
		
		if (generatedQueries.contains(ds) || ds == null){
			//Logger.getLogger(getClass()).info("skipped iquery for" + ds.getId()+ " and item=" + directoryItemId);
			return;
		}
		Logger.getLogger(getClass()).info("generate iquery for " + ds.getId() + " and item=" + directoryItemId);
		
		dataSources.append("    IQuery " + ds.getId() + "Query = null;\n");
		dataSources.append("    try{\n");
		dataSources.append("        " + ds.getId() + "Query = QueryHelper.buildquery(" + ds.getDataSourceName() + ", " + ds.getId() + "DataSet);\n");
		dataSources.append("    }catch(Exception ex){\n");
//		dataSources.append("        out.write(\"Cannot execute query on DataSet" + ds.getId() + "<br>\" + ex.getMessage() + \"<br>\");\n");
		dataSources.append("        ex.printStackTrace();\n");
		dataSources.append("    }\n");
		
		generatedQueries.add(ds);
	}


	public String getCloseQueries(VanillaProfil vanillaProfil){
		StringBuffer buf = new StringBuffer();
	
		for(DataSet ds : generatedQueries){
			buf.append("    try{\n" );
			buf.append("       " + ds.getId() + "Query.close();\n");
			buf.append("    }catch(Exception ex){\n" );
			buf.append("        ex.printStackTrace();\n");
			buf.append("    }\n");
		}
		
		for(DataSource ds : generatedDataSource){
			buf.append("    try{\n" );
			//XXX : change that to use the connectionManager
			buf.append("	FdConnectionManager.getInstance().clearConnection("+ds.getId()+","+vanillaProfil.getDirectoryItemId()+","+ vanillaProfil.getRepositoryId()+");\n");
			
//			buf.append("       " + ds.getId() + ".closeConnections();\n");
			buf.append("    }catch(Exception ex){\n" );
			buf.append("        ex.printStackTrace();\n");
			buf.append("    }\n");
		}
		
		return buf.toString();
	}
	
	public static String prepareQuery(String spacing, String query, IComponentDefinition component) {
		StringBuffer buf = new StringBuffer();
		
		for(ComponentParameter p : component.getParameters()){
			if (p instanceof OutputParameter){
				continue;
			}
			buf.append(spacing + "if (" + p.getName() + "Parameter != null){\n");
			buf.append(spacing + "    " + query +".setString(" + p.getIndice() + ", URLDecoder.decode(" + p.getName() + "Parameter,\"UTF-8\"));\n");
			buf.append("System.out.println(\"" + p.getName() + "Parameter=\"+" + p.getName() + "Parameter);\n");
			buf.append(spacing + "}\n");
		}
		
		return buf.toString();
	}
}
