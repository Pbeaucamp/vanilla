package bpm.gwt.aklabox.commons.server.methods;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

import bpm.aklabox.workflow.core.model.IInstanceStatus;
import bpm.aklabox.workflow.core.model.OrbeonWorkflowInformation;
import bpm.aklabox.workflow.core.model.WorkflowLog;
import bpm.aklabox.workflow.core.model.activities.Activity;
import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.Form;
import bpm.document.management.core.model.IObject;
import bpm.document.management.core.model.IObject.ItemTreeType;
import bpm.document.management.core.model.IObject.ItemType;
import bpm.document.management.core.model.OrbeonFormInstance;
import bpm.document.management.core.model.OrbeonFormSection;
import bpm.document.management.core.model.OrganigramElement;
import bpm.document.management.core.model.OrganigramElementSecurity;
import bpm.gwt.aklabox.commons.server.security.CommonSession;
import bpm.gwt.aklabox.commons.server.servlets.OrbeonServlet.OrbeonItem;
import bpm.vanilla.platform.core.config.ConfigurationManager;

public class OrbeonServletAction {

	private CommonSession session;
	private HttpServletRequest req;

	public OrbeonServletAction(CommonSession session, HttpServletRequest req) {
		this.session = session;
		this.req = req;
	}
	
	public String generateOrbeonUrl() {
		String instanceId = req.getParameter("instance");
		String action = instanceId == null || instanceId.isEmpty() ? "new" : "edit";
		instanceId = instanceId == null || instanceId.isEmpty() ? null : instanceId;
		
		String urlToCall = ConfigurationManager.getProperty("bpm.document.management.orbeon.url") + "/fr/" + req.getParameter("app") + "/" + req.getParameter("name") + "/" + action + (instanceId != null ? "/" + instanceId : "");
		
		try {
			int docId = Integer.parseInt(req.getParameter("workflowdoc"));

			OrbeonWorkflowInformation info = session.getAklaflowService().getOrbeonInformation(session.getAklaboxService().getDocInfo(docId));

			List<String> sectionInUrl = new ArrayList<>();
			boolean first = true;
			
			int firstSection = info.getForm().getSections().size();
			
			if (info.getInstance().getInstanceStatus().equals(IInstanceStatus.RUNNING)) {
				List<Activity> acts = session.getAklaflowService().getOrbeonNextActivity(info.getWorkflow(), info.getInstance());
				for (Activity a : acts) {
					if (a.getFormSectionId() != null && !a.getFormSectionId().isEmpty()) {
						boolean authorized = true;
						//rights from organigram
						if(a.getOrgaElements() != null){
							for(OrganigramElement el : a.getOrgaElements()){
								if(el.getId() > 0){
									for (OrganigramElementSecurity sec : el.getSecurities()) {
										if (sec.getUserId() == session.getUser().getUserId() && sec.getFonction().equals(a.getOrgaFunctions().get(a.getOrgaElements().indexOf(el)))) {
											authorized = false;
											firstSection = checkFirstSectionIndex(info.getForm(), a.getFormSectionId(), firstSection);
											break;
										}
									}
								} else {
//									for (OrganigramElementSecurity sec : session.getAklaboxService().getDirectSuperiors(info.getInstance().getActivityLogs().get(info.getInstance().getActivityLogs().size()-1).getExecutor())) {
									for (OrganigramElementSecurity sec : session.getAklaflowService().getDynamicSuperior(a, info.getInstance(), el, info.getInstance().getActivityLogs().get(info.getInstance().getActivityLogs().size()-1).getExecutor())) {
										if (sec.getUserId() == session.getUser().getUserId()) {
											authorized = false;
											firstSection = checkFirstSectionIndex(info.getForm(), a.getFormSectionId(), firstSection);
											break;
										}
									}
								}
							}
							
							//delegation
							if(authorized && info.getInstance().getDelegations() != null) {
								Integer userId = info.getInstance().getDelegations().get(a.getActivityId());
								if(userId != null && session.getUser().getUserId() == userId) {
									authorized = false;
									firstSection = checkFirstSectionIndex(info.getForm(), a.getFormSectionId(), firstSection);
								}
								
							}
						} else {
							authorized = false;
							firstSection = checkFirstSectionIndex(info.getForm(), a.getFormSectionId(), firstSection);
						}

						for(String section : a.getFormSectionIds()) {
						
							if (first) {
								first = false;
								urlToCall += "?" + section + "=" + authorized;
							} else {
								urlToCall += "&" + section + "=" + authorized;
							}
							sectionInUrl.add(section);
						}

					}

				}
			}
			//if no info on the rights, read only
			for (OrbeonFormSection s : info.getForm().getSections()) {
				if (!sectionInUrl.contains(s.getParameterName())) {
					if (first) {
						first = false;
						urlToCall += "?" + s.getParameterName() + "=" + true;
					} else {
						urlToCall += "&" + s.getParameterName() + "=" + true;
					}
				}
			}
			
			urlToCall += "&section=" + ((firstSection) == info.getForm().getSections().size() ? 0 : (firstSection));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return urlToCall;
	}
	
	public void handleFormValidation(OrbeonItem item, String sending) throws Throwable {
		try {

			System.out.println("Getting doc");
			Documents doc = session.getAklaboxService().getDocInfo(Integer.parseInt(item.getWorkflowdoc()));
			
			System.out.println("Getting workflow info");
			
			OrbeonWorkflowInformation info = session.getAklaflowService().getOrbeonInformation(doc);

			SAXReader reader = new SAXReader();
			
			System.out.println("Parsing data");
			String dataXml = IOUtils.toString(req.getInputStream(), "UTF-8");
//			System.out.println(dataXml);
			Document document = reader.read(IOUtils.toInputStream(dataXml, "UTF-8"), "UTF-8");

			XPath xpath = DocumentHelper.createXPath("//*[@filename]");
			List<Element> pjNodes = xpath.selectNodes(document);

			System.out.println("Browsing files");
			
			PJ: for (Element elem : pjNodes) {
				try {
					String filename = elem.attributeValue("filename");
					String path = elem.getText().substring(0, elem.getText().indexOf("?")).replace("file:/", "");
					if(!path.startsWith("/")) {
						path = "/" + path;
					}
					System.out.println("File found " + filename);
					// check if exists
					try {
						List<IObject> obj = session.getAklaboxService().getItems(session.getUser().getUserId(), doc.getParentId(), null, ItemTreeType.ENTERPRISE, ItemType.DOCUMENT, false);

						for (IObject o : obj) {
							if (o.getName().equals(filename)) {
								continue PJ;
							}
						}

						System.out.println("File doesn't exist " + filename);
						
						session.getAklaboxService().uploadfile(new ByteArrayInputStream(FileUtils.readFileToByteArray(new File(path))), doc.getParentId() + "", filename, path, session.getUser(), new Date());
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
				}
			}
			
			System.out.println("Check instance exists");

			if (item.getInstance() == null || item.getInstance().isEmpty()) {
				OrbeonFormInstance instance = session.getAklaboxService().getOrbeonFormInstance(info.getForm(), info.getDocument());
				instance.setOrbeonInstanceId(req.getParameter("document"));
				
				System.out.println("Updating instance");
				
				session.getAklaboxService().updateOrbeonInstance(instance);
			}
			
			List<Activity> acts = session.getAklaflowService().getOrbeonNextActivity(info.getWorkflow(), info.getInstance());
			List<String> sectionId = new ArrayList<>();
			for (Activity a : acts) {
				if (a.getFormSectionId() != null && !a.getFormSectionId().isEmpty() && a.getOrgaElements() != null) {
					for(OrganigramElement el : a.getOrgaElements()){
						if(el.getId() > 0){
							for (OrganigramElementSecurity sec : el.getSecurities()) {
								if (sec.getUserId() == session.getUser().getUserId() && sec.getFonction().equals(a.getOrgaFunctions().get(a.getOrgaElements().indexOf(el)))) {
									sectionId = a.getFormSectionIds();
									break;
								}
							}
						} else {
//							for (OrganigramElementSecurity sec : session.getAklaboxService().getDirectSuperiors(info.getInstance().getActivityLogs().get(info.getInstance().getActivityLogs().size()-1).getExecutor())) {
							for (OrganigramElementSecurity sec : session.getAklaflowService().getDynamicSuperior(a, info.getInstance(), el, info.getInstance().getActivityLogs().get(info.getInstance().getActivityLogs().size()-1).getExecutor())) {
								if (sec.getUserId() == session.getUser().getUserId()/* && sec.getFonction().equals(a.getOrgaFunction())*/) {
									sectionId = a.getFormSectionIds();
									break;
								}
							}
						}
					}
				}
			}
			
			//find if validate comes from radio button
			boolean validate;
			try {
				if(sectionId == null || sectionId.isEmpty()) {
					throw new Exception("Aucune option de validation choisie");
				}
				int index = checkFirstSectionIndex(info.getForm(), sectionId.get(0), 50);
				String sect = info.getForm().getSections().get(index).getLabel();
				
				XPath xpathValidation = DocumentHelper.createXPath( "//" + sect + "//*[contains(name(), 'validation')]");
				List<Element> pjNodeValidations = xpathValidation.selectNodes(document);
				validate = false;
				if(pjNodeValidations != null && !pjNodeValidations.isEmpty()) {
					Element nodeVal = pjNodeValidations.get(0);
					int val = Integer.parseInt(nodeVal.getText());
					if(val == 1) {
						validate = true;
					}
					else if(val == 2){
						validate = false;
					}
					else if(val == 3) {
						throw new Exception("Aucune option de validation choisie");
					}
				}
				else {
					validate = Boolean.parseBoolean(sending);
				}
			} catch (Exception e1) {
				if(e1.getMessage().equals("Aucune option de validation choisie")) {
					throw e1;
				}
				System.out.println("No section id");
				validate = Boolean.parseBoolean(sending);
			}
			
			System.out.println("Update workflow");
			
			//XXX need section names for xml saving
			List<String> sectionNames = new ArrayList<>();
			for(OrbeonFormSection s : info.getForm().getSections()) {
				if(sectionId.contains(s.getParameterName())) {
					sectionNames.add(s.getLabel());
				}
			}
			
			session.getAklaflowService().updateOrbeonWorkflow(new ArrayList<WorkflowLog>(), session.getUser(), info.getWorkflow(), info.getInstance(), validate, sectionNames);
			
			if(validate){ /* si c'est une validation et que le user a un tampon. Le test de la presence du tampon dans le form est fait dans la fonction updateOrbeonStampSection*/
				try {
					if(session.getUser().getStamp() != null && !session.getUser().getStamp().isEmpty()){
						session.getAklaboxService().updateOrbeonStampSection(info.getForm(), sectionId, session.getUser().getStamp(), req.getParameter("document"), dataXml);
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}

		} catch (Throwable e) {
			if (e.getMessage() != null && e.getMessage().equals("Aucune option de validation choisie")) {
				throw e;
			}
			e.printStackTrace();
		}
	}
	
	private int checkFirstSectionIndex(Form form, String formSectionId, int firstSection) {
		for(OrbeonFormSection s : form.getSections()) {
			if(formSectionId.contains(";")) {
				if(formSectionId.contains(s.getParameterName() + ";")) {
					int index = form.getSections().indexOf(s);
					if(index < firstSection) {
						return index;
					}
					return firstSection;
				}
			}
			else {
				if(formSectionId.equals(s.getParameterName())) {
					int index = form.getSections().indexOf(s);
					if(index < firstSection) {
						return index;
					}
					return firstSection;
				}
			}
		}
		
		return 0;
	}
}
