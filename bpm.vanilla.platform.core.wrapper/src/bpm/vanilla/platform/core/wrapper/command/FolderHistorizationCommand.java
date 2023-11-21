package bpm.vanilla.platform.core.wrapper.command;

import java.io.File;
import java.io.FileInputStream;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.components.ReportHistoricComponent;
import bpm.vanilla.platform.core.components.historic.HistorizationConfig;
import bpm.vanilla.platform.core.components.historic.HistorizationConfig.HistorizationTarget;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;

public class FolderHistorizationCommand extends Thread{
	private static class FileDescription{
		private String entryName;
		private String entryFormat;
		private int repositoryId;
		private int reportItemId;
		private int vanillaUserId;
		private File file;
		public FileDescription(File file) throws Exception{
			
			String[] majorBlock = file.getName().split("\\$");
			//majorBlock[0]=entryName
			//majorBlock[1]=remaining
			entryName = majorBlock[0];
			
			int extensionIndex = majorBlock[1].lastIndexOf(".");
			entryFormat = majorBlock[1].substring(extensionIndex + 1);
			
			String[] identifiers = majorBlock[1].substring(0, extensionIndex).split("_");
			//identifiers[0]=repositoryId
			//identifiers[1]=reportDirectoryItemId
			//identifiers[2]=userId
			
			repositoryId = Integer.parseInt(identifiers[0]);
			reportItemId = Integer.parseInt(identifiers[1]);
			vanillaUserId = Integer.parseInt(identifiers[2]);
			this.file = file;
		}
		

		/**
		 * @return the file
		 */
		public File getFile() {
			return file;
		}


		/**
		 * @return the entryName
		 */
		public String getEntryName() {
			return entryName;
		}

		/**
		 * @return the entryFormat
		 */
		public String getEntryFormat() {
			return entryFormat;
		}

		/**
		 * @return the repositoryId
		 */
		public int getRepositoryId() {
			return repositoryId;
		}

		/**
		 * @return the reportItemId
		 */
		public int getReportItemId() {
			return reportItemId;
		}

		/**
		 * @return the vanillaUserId
		 */
		public int getVanillaUserId() {
			return vanillaUserId;
		}
	}
	
	
	private File folder;
	private IVanillaComponentProvider api;
	private ReportHistoricComponent remote;
	
	
	
	public FolderHistorizationCommand(File folder, IVanillaComponentProvider api, ReportHistoricComponent remote){
		this.folder = folder;
		this.api = api;
		this.remote = remote;
		setDaemon(true);
	}
	
	
	
	public void run(){
		for(String fName : folder.list()){
			File file = new File(folder, fName);
			if (!file.exists()){
				continue;
			}
			if (!file.isFile()){
				continue;
			}
			try{
				historize( new FileDescription(file));
				
			}catch(Exception ex){
				Logger.getLogger(getClass()).warn("File " + file.getAbsolutePath() + " historization failed : " + ex.getMessage());
			}
		}
		
	}
	
	private void historize(FileDescription desc) throws Exception{
		Repository rep = api.getRepositoryManager().getRepositoryById(desc.getRepositoryId());
		if (rep == null){
			throw new Exception("The Repository with id = " + desc.getRepositoryId() + " does not exist");
		}
		
		User userTarget = api.getSecurityManager().getUserById(desc.getVanillaUserId());
		if (userTarget == null){
			throw new Exception("The User with id = " + desc.getVanillaUserId() + " does not exist");
		}
		
		
		HistorizationConfig conf = new HistorizationConfig(
				HistorizationTarget.User, 
				new ObjectIdentifier(desc.getRepositoryId(), desc.getReportItemId()), 
				0, 
				desc.getEntryName(), 
				desc.getEntryFormat());
		
		conf.addTargetId(userTarget.getId());
		
		GedDocument doc = remote.historize(conf, new FileInputStream(desc.getFile()));
		if (doc != null){
			if (desc.getFile().delete()){
				Logger.getLogger(getClass()).info(desc.getFile().getAbsolutePath() + " deleted after historization");
			}
			else{
				
				Logger.getLogger(getClass()).info(desc.getFile().getAbsolutePath() + " failed to delete");
			}
		}
	}
	
}
