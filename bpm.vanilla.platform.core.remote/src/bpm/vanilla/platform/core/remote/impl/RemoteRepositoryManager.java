package bpm.vanilla.platform.core.remote.impl;

import java.util.List;

import bpm.vanilla.platform.core.IRepositoryManager;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.UserRep;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteRepositoryManager implements IRepositoryManager{
	private HttpCommunicator httpCommunicator;
	private static XStream xstream;
	static{
		xstream = new XStream();
	}
	public RemoteRepositoryManager(HttpCommunicator httpCommunicator){
		this.httpCommunicator = httpCommunicator;
	}

	
	private XmlArgumentsHolder createArguments(Object...  arguments){
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		for(int i = 0; i < arguments.length; i++){
			args.addArgument(arguments[i]);
		}
		return args;
	}
	
	
	@Override
	public void addRepository(Repository repository) throws Exception {
		XmlAction op = new XmlAction(createArguments(repository), IRepositoryManager.ActionType.ADD_REP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		
	}
	@Override
	public void addUserRep(UserRep userRep) throws Exception {
		XmlAction op = new XmlAction(createArguments(userRep), IRepositoryManager.ActionType.ADD_USER_ACCESS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		
	}
	@Override
	public void delUserRep(UserRep userRep) throws Exception {
		XmlAction op = new XmlAction(createArguments(userRep), IRepositoryManager.ActionType.DEL_USER_ACCESS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		
	}
	@Override
	public void deleteRepository(Repository repository) throws Exception {
		XmlAction op = new XmlAction(createArguments(repository), IRepositoryManager.ActionType.DEL_REP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		
	}
	@Override
	public List<Repository> getRepositories() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IRepositoryManager.ActionType.LIST_REP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		Object o = xstream.fromXML(xml);
		return (List)o;
	}
	@Override
	public Repository getRepositoryById(int repId) throws Exception {
		XmlAction op = new XmlAction(createArguments(repId), IRepositoryManager.ActionType.FIND_REP_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		return (Repository)xstream.fromXML(xml);
	}
	@Override
	public Repository getRepositoryByName(String repName) throws Exception {
		XmlAction op = new XmlAction(createArguments(repName), IRepositoryManager.ActionType.FIND_REP_NAME);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		return (Repository)xstream.fromXML(xml);
	}
	@Override
	public UserRep getUserRepById(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IRepositoryManager.ActionType.FIND_USER_ACCESS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		return (UserRep)xstream.fromXML(xml);
	}
	@Override
	public List<UserRep> getUserRepByRepositoryId(int repositoryId)	throws Exception {
		XmlAction op = new XmlAction(createArguments(repositoryId), IRepositoryManager.ActionType.FIND_USER_ACCESS_4_REP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		return (List)xstream.fromXML(xml);
	}
	@Override
	public List<UserRep> getUserRepByUserId(int userId) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId), IRepositoryManager.ActionType.FIND_USER_ACCESS_4_USER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		return (List)xstream.fromXML(xml);
	}
	@Override
	public List<Repository> getUserRepositories(String userName)throws Exception {
		XmlAction op = new XmlAction(createArguments(userName), IRepositoryManager.ActionType.FIND_USER_ACCESS_4_USER_NAME);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		return (List)xstream.fromXML(xml);
	}
	@Override
	public boolean hasUserHaveAccess(int userId, int repositoryId)throws Exception {
		XmlAction op = new XmlAction(createArguments(userId, repositoryId), IRepositoryManager.ActionType.HAS_ACCESS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Boolean)xstream.fromXML(xml);
	}
	@Override
	public void updateRepository(Repository repository) throws Exception {
		XmlAction op = new XmlAction(createArguments(repository), IRepositoryManager.ActionType.UPDATE_REP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

	}
	@Override
	public Repository getRepositoryFromUrl(String repositoryUrl) throws Exception{
		XmlAction op = new XmlAction(createArguments(repositoryUrl), IRepositoryManager.ActionType.FIND_REP_URL);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		return (Repository)xstream.fromXML(xml);
	}

	
}
