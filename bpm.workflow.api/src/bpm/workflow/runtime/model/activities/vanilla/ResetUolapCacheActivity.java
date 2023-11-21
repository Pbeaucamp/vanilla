package bpm.workflow.runtime.model.activities.vanilla;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.fasd.inport.DigesterFasd;
import org.fasd.olap.FAModel;

import bpm.fa.api.olap.unitedolap.UnitedOlapLoader;
import bpm.fa.api.olap.unitedolap.UnitedOlapServiceProvider;
import bpm.fa.api.repository.FaApiHelper;
import bpm.united.olap.api.preload.IPreloadConfig;
import bpm.united.olap.api.preload.PreloadConfig;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.impl.RuntimeContext;
import bpm.united.olap.remote.services.RemoteServiceProvider;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.beans.UOlapPreloadBean;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.IComment;
import bpm.workflow.runtime.model.IRepositoryItem;
import bpm.workflow.runtime.resources.BiRepositoryObject;

public class ResetUolapCacheActivity extends AbstractActivity implements IRepositoryItem, IComment {

	private String comment;
	private BiRepositoryObject repositoryItem;
	private boolean reloadCache;
	private static int number = 0;
	private String cubeName;
	
	public ResetUolapCacheActivity() {number++;}
	
	public ResetUolapCacheActivity(String name) {
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;
	}

	@Override
	public BiRepositoryObject getItem() {
		return repositoryItem;
	}

	@Override
	public void setItem(BiRepositoryObject obj) {
		this.repositoryItem = obj;
	}

	@Override
	public String getComment() {
		return comment;
	}

	@Override
	public void setComment(String text) {
		this.comment = text;
	}

	@Override
	public IActivity copy() {
		ResetUolapCacheActivity copy = new ResetUolapCacheActivity();
		
		copy.setComment(comment);
		copy.setDescription(description);
		copy.setItem(repositoryItem);
		copy.setName(name);
		copy.setParent(parent);
		copy.setPositionHeight(height);
		copy.setPositionWidth(width);
		copy.setPositionX(xPos);
		copy.setPositionY(yPos);
		copy.setRelativePositionY(yRel);

		copy.setItem(repositoryItem);
		copy.setReloadCache(reloadCache);
		
		return copy;
	}
	
	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("resetuolapcacheactivity");
		if(comment != null) {
			e.addElement("comment").setText(comment);
		}
		if(repositoryItem != null) {
			e.add(repositoryItem.getXmlNode());
			e.addElement("cubename").setText(cubeName);
		}
		
		e.addElement("reload").setText(reloadCache + "");
		return e;
	}
	
	@Override
	public String getProblems() {
		String problems = "";
		if(repositoryItem == null) {
			problems += "the repository item is not selected.";
		}
		return problems;
	}

	public void setReloadCache(boolean reload) {
		this.reloadCache = reload;
	}
	
	public void setReloadCache(String reload) {
		this.reloadCache = Boolean.parseBoolean(reload);
	}
	
	public boolean isReloadCache() {
		return this.reloadCache;
	}
	
	public void setCubeName(String cubeName) {
		this.cubeName = cubeName;
	}
	
	public String getCubeName() {
		return this.cubeName;
	}

	@Override
	public void execute() throws Exception {
		if(super.checkPreviousActivities()) {
			
			try {
				boolean isReload = reloadCache;

				RepositoryItem item = workflowInstance.getRepositoryApi().getRepositoryService().getDirectoryItem(repositoryItem.getId());
				
				String xml = workflowInstance.getRepositoryApi().getRepositoryService().loadModel(item);
				
				DigesterFasd dig = new DigesterFasd(IOUtils.toInputStream(xml, "UTF-8"));
				
				FAModel mondrianModel = dig.getFAModel();
				
				//clear cache
				RemoteServiceProvider remoteServiceProvider = new RemoteServiceProvider();
				remoteServiceProvider.configure(workflowInstance.getRepositoryApi().getContext().getVanillaContext());
				
				UnitedOlapServiceProvider.getInstance().init(remoteServiceProvider.getRuntimeProvider(), remoteServiceProvider.getModelProvider());
				
				FaApiHelper helper = new FaApiHelper(workflowInstance.getRepositoryApi().getContext().getVanillaContext().getVanillaUrl(), new UnitedOlapLoader());
				
				IRuntimeContext runtimeCtx = new RuntimeContext(workflowInstance.getRepositoryApi().getContext().getVanillaContext().getLogin(), 
						workflowInstance.getRepositoryApi().getContext().getVanillaContext().getPassword(), workflowInstance.getRepositoryApi().getContext().getGroup().getName(), workflowInstance.getRepositoryApi().getContext().getGroup().getId());
				
				IObjectIdentifier identifier = new ObjectIdentifier(workflowInstance.getRepositoryApi().getContext().getRepository().getId(), item.getId());
				String schemaId = helper.getCube(identifier, runtimeCtx, cubeName).getSchemaId();
				
				remoteServiceProvider.getModelProvider().removeCache(schemaId, cubeName, runtimeCtx, true, true);
				
				
				//Reload if needed
				if(isReload) {
					List<UOlapPreloadBean> beans = workflowInstance.getVanillaApi().getUnitedOlapPreloadManager().getPreloadForIdentifier(identifier);
					
					List<String> mdxQueries = new ArrayList<String>();
					for(UOlapPreloadBean bean : beans)  {
						mdxQueries.add(bean.getMdxQuery());
					}
					
					IPreloadConfig config = new PreloadConfig();
					if(mondrianModel.getPreloadConfig() !=  null) {
						HashMap<String, Integer> levels = mondrianModel.getPreloadConfig().getLevelNumbers();
						for(String s : levels.keySet()) {
							config.setHierarchyLevel(s, levels.get(s));
						}
					}
					
					remoteServiceProvider.getModelProvider().restoreReloadCache(schemaId, cubeName, config, runtimeCtx, mdxQueries);
				}
				
				activityResult = true;
			} catch(Exception e) {
				activityResult = false;
				activityState = ActivityState.FAILED;
				failureCause = e.getMessage();
				e.printStackTrace();
			}
			
			super.finishActivity();
		}
	}
}
