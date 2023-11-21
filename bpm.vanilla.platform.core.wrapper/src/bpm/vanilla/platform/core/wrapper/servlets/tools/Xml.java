package bpm.vanilla.platform.core.wrapper.servlets.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.vanilla.platform.core.beans.FmdtUrl;
import bpm.vanilla.platform.core.beans.LogBean;
import bpm.vanilla.platform.core.beans.PublicParameter;
import bpm.vanilla.platform.core.beans.PublicUrl;
import bpm.vanilla.platform.core.beans.VanillaSession;
import bpm.vanilla.platform.core.beans.Variable;

public class Xml {
	
	synchronized public static String readXML(InputStream strIn) throws IOException {
        BufferedReader s = new BufferedReader(new InputStreamReader(strIn, "UTF-8"));
        String line = s.readLine();
        String doc="";

        while (line != null) {
            doc+=line +"\n";
            line = s.readLine();
        }
        s.close();
        
        return doc;
	}
	
	public static Element toElement(PublicParameter pp){
		Element publicparameter = DocumentHelper.createElement("publicparameter");
		publicparameter.addElement("id").setText(pp.getId() + "");
		publicparameter.addElement("publicurlid").setText(pp.getPublicUrlId() + "");
		publicparameter.addElement("parametername").setText(pp.getParameterName());
		publicparameter.addElement("parametervalue").setText(pp.getParameterValue());
		
		return publicparameter;
	}
	
	public static Element toElement(PublicUrl pu){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Element publicurl = DocumentHelper.createElement("publicurl");
		publicurl.addElement("id").setText(pu.getId() + "");
		publicurl.addElement("publickey").setText(pu.getPublicKey());
		publicurl.addElement("groupid").setText(pu.getGroupId() + "");
		publicurl.addElement("userid").setText(pu.getUserId() + "");
		publicurl.addElement("itemId").setText(pu.getItemId() + "");
		publicurl.addElement("repId").setText(pu.getRepositoryId() + "");
		publicurl.addElement("creationdate").setText(sdf.format(pu.getCreationDate()));
		
		if (pu.getEndDate() != null) {
			publicurl.addElement("enddate").setText(sdf.format(pu.getEndDate()));
		}
		
		publicurl.addElement("active").setText(pu.getActive()+ "");
		publicurl.addElement("deleted").setText(pu.getDeleted() + "");
		publicurl.addElement("outputformat").setText(pu.getOutputFormat());
		
		if (pu.getDatasourceId() != null)
			publicurl.addElement("datasourceid").setText(pu.getDatasourceId() + "");
		
		return publicurl;
	}

	public static Element toElement(Variable v) {
		Element e = DocumentHelper.createElement("variable");
		if (v.getId() != null)
			e.addElement("id").setText(v.getId()+"");
		if (v.getName() != null)
			e.addElement("name").setText(v.getName());
		if (v.getValue() != null)
			e.addElement("value").setText(v.getValue());
		if (v.getType() != null)
			e.addElement("type").setText(v.getType() + "");
		if (v.getSource() != null)
			e.addElement("source").setText(v.getSource());
		if (v.getScope() != null)
			e.addElement("scope").setText(v.getScope() + "");
		return e;
	}

	public static Element toElement(FmdtUrl fu) {
		Element root = DocumentHelper.createElement("fmdturl");
		root.addElement("id").setText(fu.getId()+"");
		root.addElement("itemid").setText(fu.getItemId()+"");
		root.addElement("groupname").setText(fu.getGroupName());
		root.addElement("modelname").setText(fu.getModelName());
		
		if (fu.getPackageName() != null){
			root.addElement("packagename").setText(fu.getPackageName());
		}
			
		
		if (fu.getDescription() != null){
			root.addElement("description").setText(fu.getDescription());
		}
			
		
		if (fu.getUser() != null){
			root.addElement("user").setText(fu.getUser());
		}
			
		
		if (fu.getPassword() != null){
			root.addElement("password").setText(fu.getPassword());
		}
			
		if (fu.getRepositoryId() != null){
			root.addElement("repositoryid").setText(fu.getRepositoryId() + "");
		}
		
		root.addElement("name").setText(fu.getName());
		
		
		return root;
	}

	public static Element toElement(LogBean bean) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Element e = DocumentHelper.createElement("log");
		e.addElement("id").setText(bean.getId() + "");
		e.addElement("date").setText(sdf.format(bean.getDate()));
		e.addElement("directoryItemId").setText(bean.getDirectoryItemId() + "");
		e.addElement("levelInfo").setText(bean.getLevelInfo());
		e.addElement("message").setText(bean.getMessage());
		e.addElement("type").setText(bean.getObjectType());
		e.addElement("repositoryUrl").setText(bean.getRepositoryUrl());
		e.addElement("instanceId").setText(bean.getRunningInstanceId() + "");
		e.addElement("stepName").setText(bean.getStepName());
		e.addElement("stepClassName").setText(bean.getStepClassName());
		e.addElement("runtimeServerUrl").setText(bean.getRuntimeInstanceUrl());
		return e;
	}

	public static Element toElement(VanillaSession s) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Element session = DocumentHelper.createElement("session");
//		session.addElement("id").setText(s.getId()+"");
		session.addElement("userid").setText(s.getUser().getId() + "");
//		session.addElement("repositoryid").setText(s.getRepositoryId()+"");
//		session.addElement("ticket").setText(s.getTicket());
		session.addElement("begindate").setText(sdf.format(s.getCreationDate()));
//		session.addElement("group").setText(s.getGroup());
//		session.addElement("refreshdate").setText(sdf.format(s.getRefreshDate()));
		
		return session;
	}

}
