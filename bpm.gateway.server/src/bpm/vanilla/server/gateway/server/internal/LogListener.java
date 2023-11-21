package bpm.vanilla.server.gateway.server.internal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.TransformationLog;
import bpm.vanilla.server.commons.server.tasks.ITask;

public class LogListener implements PropertyChangeListener{
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final String OBJECT_TYPE = "BIG";
	private long taskId;
	private String repository;
	private int directoryItemId;
	private String stepName;
	private String stepClassName;
	private String serverUrl;
	
	private List<Element> batch = new ArrayList<Element>(1000);
	private String vanillaUrl;
	
	public LogListener(ITask task, String repository, int directoryItemId, RuntimeStep step, String serverUrl, String vanillaUrl){
		taskId = task.getId();
		this.repository = repository;
		this.directoryItemId = directoryItemId;
		this.stepName = step.getTransformation().getName();
		this.stepClassName = step.getClass().getName();
		this.serverUrl = serverUrl;
		this.vanillaUrl = vanillaUrl;
	}
	
	
	public void propertyChange(PropertyChangeEvent evt) {
		if (!RuntimeStep.EVENT_LOG.equals(evt.getPropertyName())){
			return;
		}
		Date d = Calendar.getInstance().getTime();
		TransformationLog l = (TransformationLog)evt.getNewValue();
		
		Element e = DocumentHelper.createElement("add");
		e.addElement("date").setText(sdf.format(d));
		e.addElement("directoryItemId").setText(directoryItemId + "");
		switch (l.priority) {
		case Level.INFO_INT:
			e.addElement("levelInfo").setText("INFO");
			break;

		case Level.DEBUG_INT:
			e.addElement("levelInfo").setText("DEBUG");
			break;
		case Level.WARN_INT:
			e.addElement("levelInfo").setText("WARN");
			break;
		case Level.ERROR_INT:
			e.addElement("levelInfo").setText("ERROR");
			break;
		}
		
		e.addElement("message").setText(l.message);
		e.addElement("objectType").setText(OBJECT_TYPE);
		e.addElement("repository").setText(repository);
		e.addElement("instanceId").setText(taskId + "");
		e.addElement("serverUrl").setText(serverUrl);
		e.addElement("stepClassName").setText(stepClassName);
		e.addElement("stepName").setText(stepName);
		
		batch.add(e);
		
		if (batch.size() == 1000){
			try {
				flushBatch();
			} catch (Exception e1) {
				
				e1.printStackTrace();
			}
		}
	}
	
	public void releaseResources(){
		if (!batch.isEmpty()){
			try {
				flushBatch();
			} catch (Exception e) {
				
//				e.printStackTrace();
			}
		}
	}
	private void flushBatch() throws Exception{
		URL url = new URL(vanillaUrl+ "/LoggingServlet");
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();
		sock.setDoInput(true);
		sock.setDoOutput(true);
		sock.setRequestMethod("POST");
		sock.setRequestProperty("Content-type", "text/xml;charset=UTF-8");
			
		//send datas
			
		Element e = DocumentHelper.createElement("root");
		for(Element _e : batch){
			e.add(_e);
		}
		XMLWriter pw = new XMLWriter(new OutputStreamWriter(sock.getOutputStream(), "UTF-8"));
		pw.write(e);
		pw.close();
		sock.connect();
		String response = IOUtils.toString(sock.getInputStream(), "UTF-8");
		
		sock.disconnect();
		batch.clear();
		if (response.contains("<error>")){
			throw new Exception(response);
		}
	}
}
