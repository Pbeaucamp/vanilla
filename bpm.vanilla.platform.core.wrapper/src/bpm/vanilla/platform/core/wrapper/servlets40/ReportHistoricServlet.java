package bpm.vanilla.platform.core.wrapper.servlets40;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.components.ReportHistoricComponent;
import bpm.vanilla.platform.core.components.historic.HistoricRuntimeConfiguration;
import bpm.vanilla.platform.core.components.historic.HistorizationConfig;
import bpm.vanilla.platform.core.runtime.components.HistoricReportManager;
import bpm.vanilla.platform.core.utils.IOWriter;
import bpm.vanilla.platform.core.wrapper.VanillaCoreWrapper;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;
import bpm.vanilla.platform.logging.IVanillaLogger;

public class ReportHistoricServlet extends AbstractComponentServlet {
	
	public ReportHistoricServlet(VanillaCoreWrapper vanillaCoreWrapper, IVanillaLogger logger) {
		this.logger = logger;
		this.component = vanillaCoreWrapper;
	}
	
	@Override
	public void init() throws ServletException {
		logger.info("Initializing ReportHistoricServlet...");
		super.init();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		try{
			resp.setCharacterEncoding("UTF-8"); 
			XmlAction action = (XmlAction)xstream.fromXML(req.getInputStream());
			
			XmlArgumentsHolder args = action.getArguments();
			Object actionResult = null;
			
			if (action.getActionType() == null){
				throw new Exception("XmlAction has no actionType");
			}
			
			if (!(action.getActionType() instanceof ReportHistoricComponent.ActionType)){
				throw new Exception("ActionType not a ReportHistoricComponent");
			}
			
			ReportHistoricComponent.ActionType type = (ReportHistoricComponent.ActionType)action.getActionType();
			
			log(type, ((HistoricReportManager)component.getReportHistoricComponent()).getComponentName(), req);
			
			try{
				switch (type) {
				case Clear:
					//XXX: never used, don't know what this is supposed to do....
					break;
				
				case Get_Access:
					actionResult = getAccess(args);
					break;
				
				case Delete:
					deleteHistoric(args);
					break;
					
				case Grant_Access:
					grantAccess(args);
					break;
					
				case MassHistorize:
					actionResult = historizeDocument(args);
					break;
					
				case List:
					actionResult = getReportHistoric(args);
					break;
					
				case Load:
					loadHistoricAsStream(args,resp.getOutputStream());
					break;
					
				case Remove_Access:
					removeAccess(args);
					break;
					
				case Historize:
					actionResult = historizeDocument(args);
					break;
				case REMOVE_DOCUMENT_VERSION:
					removeDocumentVersion(args);
					break;
				}
			
			}catch(Exception ex){
				throw new ActionException("Operation " + type.name() + " failed - " + ex.getMessage(), ex);
			}
			
			if (actionResult != null){
				xstream.toXML(actionResult, resp.getWriter());
				resp.getWriter().close();	
			}
			
		}catch(Exception ex){
			logger.error(ex.getMessage(), ex);
			
			resp.getWriter().write("<error>" + ex.getMessage() + "</error>");
			resp.getWriter().close();
		}
		
	}

	private void loadHistoricAsStream(XmlArgumentsHolder args, ServletOutputStream servletOutputStream) throws Exception {
		int docId = (Integer)args.getArguments().get(0);
		InputStream is = component.getReportHistoricComponent().loadHistorizedDocument(docId);
		IOWriter.write(is, servletOutputStream, true, false);
	}

	private Object getReportHistoric(XmlArgumentsHolder args) throws Exception {
		IObjectIdentifier identifier = (IObjectIdentifier) args.getArguments().get(0);
		int groupId = (Integer) args.getArguments().get(1);
		return component.getReportHistoricComponent().getReportHistoric(identifier, groupId);
	}

	private Object historizeDocument(XmlArgumentsHolder args) throws Exception {
		
		IRuntimeConfig conf = (IRuntimeConfig) args.getArguments().get(0);
		byte[] bytes = (byte[]) args.getArguments().get(1);
		
		byte[] decodedBytes = Base64.decodeBase64(bytes);
		InputStream is = new ByteArrayInputStream(decodedBytes);
		
		if(conf instanceof HistorizationConfig) {
			return component.getReportHistoricComponent().historize((HistorizationConfig) conf, is);
		}
		else {
			return component.getReportHistoricComponent().historizeReport((HistoricRuntimeConfiguration) conf, is);
		}
		
	}

	private Object getAccess(XmlArgumentsHolder args) throws Exception {
		IObjectIdentifier identifier = (IObjectIdentifier) args.getArguments().get(0);
		return component.getReportHistoricComponent().getGroupsAuthorizedByItemId(identifier);
	}

	private void removeAccess(XmlArgumentsHolder args) throws Exception {
		int groupId = (Integer)args.getArguments().get(0);
		int itemId = (Integer)args.getArguments().get(1);
		int repId = (Integer)args.getArguments().get(2);
		component.getReportHistoricComponent().removeHistoricAccess(groupId, itemId, repId);
	}

	private void deleteHistoric(XmlArgumentsHolder args) throws Exception {
		List<GedDocument> docs = (List<GedDocument>) args.getArguments().get(0);
		int repId = (Integer)args.getArguments().get(1);
		component.getReportHistoricComponent().deleteHistoricEntry(docs, repId);
	}

	private void grantAccess(XmlArgumentsHolder args) throws Exception {
		int groupId = (Integer)args.getArguments().get(0);
		int itemId = (Integer)args.getArguments().get(1);
		int repId = (Integer)args.getArguments().get(2);
		component.getReportHistoricComponent().grantHistoricAccess(groupId, itemId, repId);
	}

	private void removeDocumentVersion(XmlArgumentsHolder args) throws Exception {
		DocumentVersion version = (DocumentVersion)args.getArguments().get(0);
		component.getReportHistoricComponent().removeDocumentVersion(version);
	}
}
