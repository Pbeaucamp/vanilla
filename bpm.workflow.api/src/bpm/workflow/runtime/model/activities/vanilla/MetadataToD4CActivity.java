package bpm.workflow.runtime.model.activities.vanilla;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.IOUtils;
import org.dom4j.Element;

import bpm.metadata.MetaDataReader;
import bpm.metadata.layer.business.BusinessPackage;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.sql.SQLDataSource;
import bpm.metadata.misc.AggregateFormula;
import bpm.metadata.query.EffectiveQuery;
import bpm.metadata.query.Formula;
import bpm.metadata.query.QuerySql;
import bpm.metadata.query.SavedQuery;
import bpm.metadata.query.SqlQueryGenerator;
import bpm.metadata.resource.Prompt;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.CkanResource;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.utils.CkanHelper;
import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.ActivityVariables;
import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.IComment;
import bpm.workflow.runtime.model.IConditionnable;
import bpm.workflow.runtime.model.IRepositoryItem;
import bpm.workflow.runtime.resources.BiRepositoryObject;

public class MetadataToD4CActivity extends AbstractActivity implements IRepositoryItem, IComment, IConditionnable {

	private BiRepositoryObject metadata;
	private String queryName;

	private CkanPackage ckanPackage;
	private String resourceId;
	private String resourceName;
	
	private String comment;
	private int metadataId;
	private String metadataName;
	

	public MetadataToD4CActivity() {
		super();
	}

	public MetadataToD4CActivity(String string) {
		setName(string);
	}

	public BiRepositoryObject getMetadata() {
		return metadata;
	}

	public void setMetadata(BiRepositoryObject metadata) {
		this.metadata = metadata;
	}

	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

	public CkanPackage getCkanPackage() {
		return ckanPackage;
	}

	public void setCkanPackage(CkanPackage ckanPackage) {
		this.ckanPackage = ckanPackage;
	}
	
	public String getResourceId() {
		return resourceId;
	}

//	public String getResourceName() {
//		return resourceName;
//	}

	public void setResource(String resourceId, String resourceName) {
		this.resourceId = resourceId;
		this.resourceName = resourceName;
	}
	
	public void setPackage(String id, String name, String title) {
		ckanPackage = new CkanPackage();
		if(id == null || id.equalsIgnoreCase("null")) {
			ckanPackage.setId(null);
		}
		else {
			ckanPackage.setId(id);
		}
		ckanPackage.setName(name);
		ckanPackage.setTitle(title);
	}
	
	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("metadataD4CActivity");
		if (comment != null) {
			e.addElement("comment").setText(comment);
		}
		e.addElement("metadataid").setText(metadata.getId() + "");
		e.addElement("metadataName").setText(metadataName);
		e.addElement("query").setText(queryName);
		e.addElement("resource").setText(resourceName);
		e.addElement("resourceId").setText(resourceId);
		if(ckanPackage.getId() != null) {
			Element pack = e.addElement("package");
			pack.addElement("id").setText(ckanPackage.getId());
			pack.addElement("name").setText(ckanPackage.getName());
			pack.addElement("title").setText(ckanPackage.getTitle());
		}

		return e;
	}
	
	public void setMetadataId(String metadataId) {
		this.metadataId = Integer.parseInt(metadataId);
	}

	@Override
	public IActivity copy() {
		return null;
	}

	@Override
	public String getProblems() {
		return null;
	}

	@Override
	public void execute() throws Exception {
		if(super.checkPreviousActivities()) {
			
			try {
				IRepositoryContext repCtx = workflowInstance.getRepositoryApi().getContext();
				
				RepositoryItem it = workflowInstance.getRepositoryApi().getRepositoryService().getDirectoryItem(metadataId);
				String result = workflowInstance.getRepositoryApi().getRepositoryService().loadModel(it);
				List<IBusinessModel> bModels = null;
				try {
					bModels = MetaDataReader.read(repCtx.getGroup().getName(), IOUtils.toInputStream(result, "UTF-8"), workflowInstance.getRepositoryApi(), false);
				} catch (Exception e) {
					e.printStackTrace();
					return ;
				}

				SavedQuery q = null;
				IBusinessPackage packk = null;
				
				if (bModels != null) {
					LOOP:for (IBusinessModel bmod : bModels) {

						List<IBusinessPackage> packages = bmod.getBusinessPackages(repCtx.getGroup().getName());
						for (IBusinessPackage pack : packages) {
							if (pack.isExplorable()) {
								for(SavedQuery query: pack.getSavedQueries()){
									if(query.getName().equals(queryName)) {
										q = query;
										packk = pack;
										break LOOP;
									}
								}
							}
						}				
					}
				}
				
				QuerySql query = q.loadQuery(repCtx.getGroup().getName(), packk);
				String conName = ((SQLDataSource) ((BusinessPackage) packk).getDataSources(repCtx.getGroup().getName()).get(0)).getConnections().get(0).getName();
				
//				List<List<String>> queryResult = packk.executeQuery(null, workflowInstance.getRepositoryApi().getContext().getVanillaContext(), "Default", sql, new ArrayList<List<String>>());
				
				EffectiveQuery sqlQuery = SqlQueryGenerator.getQuery(repCtx != null ? repCtx.getGroup().getMaxSupportedWeightFmdt() : null, repCtx != null ? repCtx.getVanillaContext() : null, (BusinessPackage) packk, query, repCtx.getGroup().getName(), false, new HashMap<Prompt, List<String>>());
				String valueQuery = sqlQuery.getGeneratedQuery();
				List<List<String>> values = ((BusinessPackage) packk).executeQuery(0, conName, valueQuery);
				
				
				List<String> colName = new ArrayList<>();
				for (IDataStreamElement col : query.getSelect()) {
					colName.add(col.getName());
				}
				for(AggregateFormula agg : query.getAggs()) {
					colName.add(agg.getOutputName());
				}
				for(Formula agg : query.getFormulas()) {
					colName.add(agg.getOutputName());
				}

				values.add(0, colName);
				
				ByteArrayInputStream is = buildCsv(values, ";");
				
				resourceName = resourceName.replace(".csv", "");
				
				ckanPackage.setSelectedResource(new CkanResource(resourceId, resourceName, "csv", ""));

				CkanHelper helper = new CkanHelper();
				helper.uploadCkanFile(resourceName, ckanPackage, is);
				activityResult = true;
			} catch (Exception e) {
				e.printStackTrace();
				activityResult = false;
				activityState = ActivityState.FAILED;
				throw e;
			}
			
			
			
			super.finishActivity();
		}
	}
	
	private ByteArrayInputStream buildCsv(List<List<String>> values, String separator) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
//			FileOutputStream os = new FileOutputStream("D:/DATA/Test/0717/test_output.csv");

			OutputStreamWriter writer = new OutputStreamWriter(os);
			CSVPrinter printer = null;
			
			printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withDelimiter(separator.charAt(0)));
			
			for (int i = 0; i < values.size(); i++) {

				List line = new ArrayList();

				List<String> row = values.get(i);
				for (int j = 0; j < row.size(); j++) {
					String val = row.get(j);
					line.add(val);
				}

				try {
					printer.printRecord(line);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			writer.flush();
			writer.close();
			printer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
		ByteArrayInputStream byteArrayIs = new ByteArrayInputStream(os.toByteArray());
		
		try {
			
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return byteArrayIs;
	}

	@Override
	public List<ActivityVariables> getVariables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSuccessVariableSuffix() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String text) {
		this.comment = text;
	}

	@Override
	public void setItem(BiRepositoryObject obj) {
		this.metadata = obj;
		this.setMetadataId(metadata.getId());
		this.metadataName = obj.getItemName();
	}

	@Override
	public BiRepositoryObject getItem() {
		return metadata;
	}
	
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public int getMetadataId() {
		return metadataId;
	}

	public void setMetadataId(int metadataId) {
		this.metadataId = metadataId;
		if(metadataName != null) {
			createDummyMetadata();
		}
	}

	private void createDummyMetadata() {
		metadata = new BiRepositoryObject();
		metadata.setId(metadataId + "");
		metadata.setItemName(metadataName);
		
	}

	public String getMetadataName() {
		return metadataName;
	}

	public void setMetadataName(String metadataName) {
		this.metadataName = metadataName;
		if(metadataId > 0) {
			createDummyMetadata();
		}
	}

}
