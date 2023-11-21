package bpm.workflow.runtime.model.activities;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.RepositoryPluginType;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.RepositoriesMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryMeta;

import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.ActivityVariables;
import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.IComment;
import bpm.workflow.runtime.model.IConditionnable;

public class KettleActivity extends AbstractActivity implements IComment,IConditionnable {

	private static int number = 0;
	
	public List<ActivityVariables> listeVar = new ArrayList<ActivityVariables>();
	public ActivityVariables varSucceed;
	
	private String comment;
	
	private String repositoryName;
	private String repositoryUser;
	private String repositoryPassword;
	private String jobName;
	
	public KettleActivity() {
		varSucceed = new ActivityVariables();

		varSucceed.setNomInterne("_succeed");
		varSucceed.setType(0);
		
		number++;
	}
	
	public KettleActivity(String name){

		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;
		
		varSucceed = new ActivityVariables();
		varSucceed.setNomInterne("_succeed");
		varSucceed.setType(0);
	

	}
	
	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("kettleActivity");
		
		if(comment != null){
			e.addElement("comment").setText(comment);
		}
		if(repositoryName != null){
			e.addElement("repositoryName").setText(repositoryName);
		}
		if(repositoryUser != null){
			e.addElement("repositoryUser").setText(repositoryUser);
		}
		if(repositoryPassword != null){
			e.addElement("repositoryPassword").setText(repositoryPassword);
		}
		if(jobName != null){
			e.addElement("jobName").setText(jobName);
		}
		
		return e;
	}
	
	@Override
	public IActivity copy() {
		KettleActivity act = new KettleActivity();
		act.setName("copy of " + name);
		act.setComment(comment);
		act.setJobName(jobName);
		act.setRepositoryName(repositoryName);
		act.setRepositoryUser(repositoryUser);
		act.setRepositoryPassword(repositoryPassword);
		
		return act;
	}

	@Override
	public String getProblems() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute() throws Exception {
		if(super.checkPreviousActivities()) {
			try {
				KettleEnvironment.init();

				RepositoriesMeta repositoriesMeta = new RepositoriesMeta();
				repositoriesMeta.readData();
				RepositoryMeta repositoryMeta = repositoriesMeta.findRepository(repositoryName);

				PluginRegistry registry = PluginRegistry.getInstance();
				Repository repository = registry.loadClass(RepositoryPluginType.class, repositoryMeta, Repository.class);
				repository.init(repositoryMeta);
				repository.connect(repositoryUser, repositoryPassword);

				RepositoryDirectoryInterface directory = repository.loadRepositoryDirectoryTree();
				directory = directory.findDirectory("ShirusKettleDirectory");

				JobMeta jobMeta = new JobMeta();
				jobMeta = repository.loadJob(jobName, directory, null, null);
				Job job = new Job(repository, jobMeta);
				job.start();

				job.waitUntilFinished();
				
				activityResult = true;
				
				
			} catch(Exception e) {
				activityResult = false;
				e.printStackTrace();
			} catch(Error e) {
				activityResult = false;
				e.printStackTrace();
			}
			super.finishActivity();
		}
	}

	@Override
	public List<ActivityVariables> getVariables() {
		return listeVar;
	}

	@Override
	public String getSuccessVariableSuffix() {
		return "_succeed";
	}

	@Override
	public String getComment() {
		return comment;
	}

	@Override
	public void setComment(String text) {
		this.comment = text;
	}

	public String getRepositoryName() {
		return repositoryName;
	}

	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}

	public String getRepositoryUser() {
		return repositoryUser;
	}

	public void setRepositoryUser(String repositoryUser) {
		this.repositoryUser = repositoryUser;
	}

	public String getRepositoryPassword() {
		return repositoryPassword;
	}

	public void setRepositoryPassword(String repositoryPassword) {
		this.repositoryPassword = repositoryPassword;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
}
