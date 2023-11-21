package bpm.vanilla.platform.core.remote.impl;

import java.net.URL;
import java.util.List;

import bpm.vanilla.platform.core.ISchedulerManager;
import bpm.vanilla.platform.core.beans.scheduler.Job;
import bpm.vanilla.platform.core.beans.scheduler.JobInstance;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteSchedulerManager implements ISchedulerManager{
	
	private HttpCommunicator httpCommunicator = new HttpCommunicator();
	private XStream xstream = new XStream();
	public URL url;
	
	public RemoteSchedulerManager(HttpCommunicator con) {
		httpCommunicator = con;				
	}

	private XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		if (args == null){ return new XmlArgumentsHolder();}
		return args;
	}

	@Override
	public void addJob(Job job) throws Exception {
		XmlAction op = new XmlAction(createArguments(job), ISchedulerManager.ActionType.ADD_JOB);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}
	
	@Override
	public void delJob(Job job) throws Exception {
		XmlAction op = new XmlAction(createArguments(job), ISchedulerManager.ActionType.DELETE_JOB);

		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void editJob(Job job) throws Exception {
		XmlAction op = new XmlAction(createArguments(job), ISchedulerManager.ActionType.EDIT_JOB);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);	
	}
	
	@Override
	public void runNowJob(Job itm) throws Exception {
		XmlAction op = new XmlAction(createArguments(itm), ISchedulerManager.ActionType.RUN_NOW_JOB);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);	
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<JobInstance> getJobHistoric(Job job) throws Exception {
		XmlAction op = new XmlAction(createArguments(job), ISchedulerManager.ActionType.GET_JOB_HISTORIC);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<JobInstance>) xstream.fromXML(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Job> getJobs(Integer repositoryId) throws Exception {
		XmlAction op = new XmlAction(createArguments(repositoryId), ISchedulerManager.ActionType.GET_JOBS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<Job>) xstream.fromXML(xml);
	}

}