package bpm.gateway.core.transformations.mdm;

import java.sql.Types;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;

import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.transformations.outputs.FileOutputCSV;
import bpm.mdm.model.Attribute;
import bpm.mdm.model.Entity;
import bpm.mdm.model.Synchronizer;
import bpm.mdm.model.api.IMdmProvider;
import bpm.mdm.model.helper.DataSetDesignConverter;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.Supplier;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.beans.data.OdaInput;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.beans.meta.Meta;
import bpm.vanilla.platform.core.beans.meta.Meta.TypeMeta;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGedComponent;

public class MdmHelper {
	
	private IMdmProvider provider;
	private IRepositoryContext repositoryContext;
	
	private IGedComponent gedComponent;
	
	public MdmHelper(IMdmProvider provider, IRepositoryContext repCtx){
		this.provider = provider;
		this.repositoryContext = repCtx;
	}

	public IMdmProvider getMdmApi() {
		return provider;
	}
	
	public StreamDescriptor getDescriptor(IMdm tr) throws Exception{
		
		Entity entity = null;
		if (tr.getEntityName() == null){
			Logger.getLogger(getClass()).warn("MdmInput has no entityName set, cannot create its descriptor");
			return null;
		}
		for(Entity e : provider.getModel().getEntities()){
			if (e.getName().equals(tr.getEntityName())){
				entity = e;
				break;
			}
		}
		
		if (entity == null){
			throw new Exception("Could not find entity named " + tr.getEntityName() + " within Mdm");
		}
		
		DefaultStreamDescriptor desc = new DefaultStreamDescriptor();
		for(Attribute a : entity.getAttributes()){
			StreamElement col = new StreamElement();
			col.name = new String(a.getName());
			col.defaultValue = a.getDefaultValue() != null ? a.getDefaultValue().toString() : null;
			col.transfoName = new String(((Transformation)tr).getName());
//			col.type = ;
			col.typeName = new String(a.getDataType().getName());
			col.originTransfo = ((Transformation)tr).getName();
			col.tableName = entity.getName();
			col.className = a.getDataType().getJavaClassName();
			desc.addColumn(col);
		}
		
		return desc;
	}



	/**
	 * 
	 * @param synchronizerName
	 * @return
	 * @throws Exception
	 */
	public DocumentGateway createGatewayModel(String synchronizerName) throws Exception{
		Synchronizer sync = null;
		for(Synchronizer s : provider.getModel().getSynchronizers()){
			if (s.getName().equals(synchronizerName)){
				sync = s;
				break;
			}
		}
		
		if (sync == null){
			throw new Exception("No Synchronizer named " + synchronizerName + " within Mdm");
		}
		
		DataSetDesign dsd = provider.getModel().getDataSource(sync.getDataSourceName());
		
		OdaInput odaInput = DataSetDesignConverter.convert(dsd);
		
		DocumentGateway doc = new DocumentGateway();
		
		/*
		 * input
		 */
		bpm.gateway.core.transformations.inputs.OdaInput in = new bpm.gateway.core.transformations.inputs.OdaInput();
		
		in.setName("Extract Datas");
		in.setTemporaryFilename("{$ENV_GATEWAY_TEMP}" + in.getName());
		in.setDatasetPrivateProperties(odaInput.getDatasetPrivateProperties());
		in.setDatasetPublicProperties(odaInput.getDatasetPublicProperties());
		in.setDatasourcePublicProperties(odaInput.getDatasourcePublicProperties());
		in.setDatasourcePrivateProperties(odaInput.getDatasourcePrivateProperties());
		in.setOdaExtensionDataSourceId(odaInput.getOdaExtensionDataSourceId());
		in.setOdaExtensionId(odaInput.getOdaExtensionId());
		in.setQueryText(odaInput.getQueryText());
		
		in.setPositionX(20);
		in.setPositionY(20);
		
		doc.addTransformation(in);
		
		
		
		/*
		 * output
		 */
		MdmOutput out = new MdmOutput();
		out.setEntityName(sync.getEntity().getName());
		out.setName("Mdm Storage");
		out.setTemporaryFilename("{$ENV_GATEWAY_TEMP}" + out.getName());
		out.setPositionX(200);
		out.setPositionY(20);
		doc.addTransformation(out);
		
		
		/*
		 * errorsCsv 
		 */
		FileOutputCSV err = new FileOutputCSV();
		err.setName("Un-registered Rows");
		err.setTemporaryFilename("{$ENV_GATEWAY_TEMP}" + err.getName());
		err.setDefinition(sync.getEntity().getName() + "_error.csv");
		err.setPositionX(300);
		err.setPositionY(20);
		doc.addTransformation(err);
		
		out.setTrashTransformation(err.getName());
		
		out.addInput(in);
		in.addOutput(out);
		
		err.addInput(out);
		out.addOutput(err);
		
		in.initDescriptor();
		out.initDescriptor();
		err.initDescriptor();
		
		for(int i = 0; i < sync.getEntity().getAttributes().size(); i++){
			int pos = sync.getDataSourceField(sync.getEntity().getAttributes().get(i));
			if (pos >= 0){
				out.createMapping(in, pos, i);
			}
			
		}

		return doc;
	}
	
	public List<Supplier> getMdmSuppliers() {
		try {
			return provider.getSuppliersByGroupId(repositoryContext.getGroup().getId());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public GedDocument getGedDocument(Contract contract) throws Exception {
		if(gedComponent == null) {
			gedComponent = new RemoteGedComponent(repositoryContext.getVanillaContext());
		}
		return gedComponent.getDocumentDefinitionById(contract.getDocId());
	}

	public RemoteRepositoryApi getRepositoryApi() throws Exception {
		return new RemoteRepositoryApi(repositoryContext);
	}
	
	public static StreamElement buildColumn(String transfoName, Meta meta) {
		int type = meta.getType() == TypeMeta.DATE ? Types.DATE : Types.VARCHAR;
		String classname = meta.getType() == TypeMeta.DATE ? "java.util.Date" : "java.lang.String";
		String typeName = meta.getType() == TypeMeta.DATE ? "DATE" : "VARCHAR";

		StreamElement element = new StreamElement();
		element.className = classname;
		element.name = meta.getLabel();
		element.tableName = "meta";
		element.transfoName = transfoName;
		element.type = type;
		element.originTransfo = transfoName;
		element.isNullable = true;
		element.defaultValue = null;
		element.typeName = typeName;
		element.isPrimaryKey = false;
		return element;
	}
}
