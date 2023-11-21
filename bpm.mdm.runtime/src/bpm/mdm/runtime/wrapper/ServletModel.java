package bpm.mdm.runtime.wrapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import bpm.mdm.model.Model;
import bpm.mdm.model.api.IMdmProvider;
import bpm.mdm.model.api.IMdmProvider.ActionType;
import bpm.mdm.model.serialisation.EMFSerializer;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.DocumentItem;
import bpm.mdm.model.supplier.MdmDirectory;
import bpm.mdm.model.supplier.Supplier;
import bpm.mdm.runtime.MdmRuntime;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.beans.DataSort;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.beans.resources.ContractIntegrationInformations;
import bpm.vanilla.platform.core.beans.resources.ContractType;
import bpm.vanilla.platform.core.beans.resources.DocumentSchema;
import bpm.vanilla.platform.core.utils.IOWriter;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class ServletModel extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7562572113412996741L;
	private XStream xstream;
	private IMdmProvider component;
	
	public ServletModel(IMdmProvider component) {
		this.component = component ;
		
		xstream = new XStream();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			resp.setCharacterEncoding("UTF-8"); 
			XmlAction action = (XmlAction)xstream.fromXML(req.getInputStream());
			
			XmlArgumentsHolder args = action.getArguments();
			
			Object actionResult = null;
			
			ActionType type = (ActionType)action.getActionType();
			switch(type) {
				case GET_MODEL:
					actionResult = this.component.getModel();
					break;
				case UPDATE_MODEL:
					actionResult = updateModel(args);
					break;
				case ADD_CONTRACT:
					this.component.addContract((Contract) args.getArguments().get(0));
					break;
				case ADD_SECURITY:
					this.component.addSecuredSupplier((Integer)args.getArguments().get(0), (Integer)args.getArguments().get(1));
					break;
				case ADD_SUPPLIER:
					if(args.getArguments().size() == 1) {
						actionResult = this.component.addSupplier((Supplier) args.getArguments().get(0));
					}
					else {
						actionResult = this.component.addSupplier((Supplier) args.getArguments().get(0), (List<Group>) args.getArguments().get(1));
					}
					break;
				case GET_SUPPLIERS:
					if(args.getArguments().size() > 0) {
						actionResult = this.component.getSuppliersByGroupId((Integer)args.getArguments().get(0));
					}
					else {
						actionResult = this.component.getSuppliers();
					}
					break;
				case REMOVE_CONTRACT:
					this.component.removeContract((Contract) args.getArguments().get(0));
					break;
				case REMOVE_SUPPLIER:
					this.component.removeSupplier((Supplier) args.getArguments().get(0));
					break;
				case SAVE_SUPPLIERS:
					actionResult = this.component.saveSuppliers((List<Supplier>) args.getArguments().get(0));
					break;
				case GET_SUPPLIER_SECURITY:
					actionResult = this.component.getSupplierSecurity((Integer) args.getArguments().get(0));
					break;
				case ADD_DOCUMENT_ITEM:
					component.saveOrUpdateDocumentItem((DocumentItem) args.getArguments().get(0));
					break;
				case REMOVE_DOCUMENT_ITEM:
					component.removeDocumentItem((DocumentItem) args.getArguments().get(0));
					break;
				case GED_DOCUMENT_ITEMS:
					actionResult = component.getDocumentItems((Integer) args.getArguments().get(0));
					break;
				case GET_CONTRACT:
					actionResult = component.getContract((Integer) args.getArguments().get(0));
					break;
				case LAUNCH_ASSOCIATED_ITEMS:
					this.component.launchAssociatedItems((Integer) args.getArguments().get(0), (IRepositoryContext) args.getArguments().get(1), (User) args.getArguments().get(2));
					break;
				case GET_CONTRACTS_BY_DIRECTORY:
					actionResult = component.getContracts((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1), (String) args.getArguments().get(2), (Integer) args.getArguments().get(3), (Integer) args.getArguments().get(4), (DataSort) args.getArguments().get(5));
					break;
				case SAVE_DIRECTORY:
					actionResult = component.saveOrUpdateDirectory((MdmDirectory) args.getArguments().get(0));
					break;
				case REMOVE_DIRECTORY:
					component.removeDirectory((MdmDirectory) args.getArguments().get(0));
					break;
				case GET_DIRECTORIES:
					actionResult = component.getDirectories((Integer) args.getArguments().get(0), (Boolean) args.getArguments().get(1));
					break;
				case GET_INTEGRATION_INFOS_BY_CONTRACT:
					actionResult = component.getIntegrationInfosByContract((Integer) args.getArguments().get(0));
					break;
				case GET_INTEGRATION_INFOS_BY_LIMESURVEY:
					actionResult = component.getIntegrationInfosByLimesurvey((String) args.getArguments().get(0));
					break;
				case GET_KPI_INFOS_BY_DATASET_ID:
					actionResult = component.getKpiInfosByDatasetId((String) args.getArguments().get(0));
					break;
				case MANAGE_INTEGRATION_INFOS:
					actionResult = component.saveOrUpdateIntegrationInfos((ContractIntegrationInformations) args.getArguments().get(0));
					break;
				case REMOVE_INTEGRATION_INFOS:
					component.removeIntegrationInfos((ContractIntegrationInformations) args.getArguments().get(0));
					break;
				case ADD_DOCUMENT_SCHEMA:
					component.saveOrUpdateDocumentSchema((DocumentSchema) args.getArguments().get(0));
					break;
				case REMOVE_DOCUMENT_SCHEMA:
					component.removeDocumentSchema((DocumentSchema) args.getArguments().get(0));
					break;
				case GED_DOCUMENT_SCHEMAS:
					actionResult = component.getDocumentSchemas((Integer) args.getArguments().get(0));
					break;
				case GET_INTEGRATION_INFOS_BY_ORGANISATION:
					actionResult = component.getIntegrationInfosByOrganisation((String) args.getArguments().get(0), (ContractType) args.getArguments().get(1));
					break;
			}
			
			if (actionResult != null){
				if (actionResult instanceof Model){
					EMFSerializer s = new EMFSerializer(((MdmRuntime)component).getConfig());
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					s.save(bos, component.getModel());
					IOWriter.write(new ByteArrayInputStream(bos.toByteArray()), resp.getOutputStream(), true, true);

				}
				else{
					xstream.toXML(actionResult, resp.getWriter());
				}
								
			}
			
		} catch(Throwable ex) {
			ex.printStackTrace();
		}
	}

	private Object updateModel(XmlArgumentsHolder args) {
		ByteArrayInputStream bis = new ByteArrayInputStream(new Base64().decode((byte[])args.getArguments().get(0)));
		Model model = null;
		try{
			EMFSerializer s = new EMFSerializer(((MdmRuntime)component).getConfig());
			model = s.loadModel(bis);
		}catch(Exception ex){
			Logger.getLogger(getClass()).error(ex.getMessage(), ex);
			return new Exception("Could not parse incoming model - " + ex.getMessage(), ex);
		}

		
		try{
			component.persistModel(model);
		}catch(Exception ex){
			Logger.getLogger(getClass()).error("Failed to persist Mdm - " + ex.getMessage(), ex);
			return ex;
		}
		return Boolean.TRUE;
	}
}
