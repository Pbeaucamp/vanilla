package bpm.smart.runtime.workflow.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import org.apache.commons.io.IOUtils;

import bpm.smart.runtime.workflow.WorkflowRunInstance;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.workflow.commons.beans.ActivityOutput;
import bpm.workflow.commons.beans.ActivityOutput.OutputType;

public class OutputFileManager {

	public static ActivityOutput writeFile(WorkflowRunInstance instance, byte[] file, OutputType type, String name) throws Exception {
		Date date = new Date();
		String fileName = (date.getYear() + 1900) + "" + (date.getMonth() + 1) + "" + date.getDay() + "_" + date.getHours() + "" + date.getMinutes() + date.getSeconds() + "_" + name + getExtension(type);
		
		String basePath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.vanillafiles.path");
		String path = "";
		if(instance.getWorkflow() != null && instance.getProgress() != null){
			path = basePath + "air_files/" + getFolderName(instance) + "/" + fileName;
		} else {
			path = basePath + "air_files/" + "workflow_orphans" + "/" + fileName;
		}
		
		
		File fileToCreate = new File(path);
		fileToCreate.getParentFile().mkdirs();
		
		FileOutputStream output = new FileOutputStream(fileToCreate);
		
		IOUtils.write(file, output);
		
		ActivityOutput outs = new ActivityOutput();
		outs.setPath(path);
		outs.setType(type);
		outs.setName(name);
		
		return outs;
	}
	
	private static String getExtension(OutputType type) throws Exception {
		switch (type) {
		case CHART:
			return ".svg";
		case CSV:
			return ".csv";
		default:
			throw new Exception("Type not supported : " + type);
		}
	}

	private static String getFolderName(WorkflowRunInstance instance) {
		return instance.getWorkflow().getId() + "_" + instance.getProgress().getUuid();
	}
	
}
