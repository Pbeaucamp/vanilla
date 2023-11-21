package groupviewer.generator;

import groupviewer.GroupDataLoader;
import groupviewer.Messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fexgenerator.generator.FlexGenerator;
import org.flexgenerator.elements.FlexProperty;
import org.flexgenerator.elements.Node;

import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;

public class GroupFlexGenerator extends FlexGenerator{
	
	private GroupDataLoader groupDataLoader;
	private Map<Integer, Node> indGroupList;
	private Map<Integer, Node> indUserList;

	public GroupFlexGenerator(GroupDataLoader groupDataLoader){
		super();
		this.groupDataLoader = groupDataLoader;
		generateGroupElements();
		generateUserElements();
		setGroupEdge();
		setUserEdge();
	}
		
	private void generateGroupElements() {
		List<Group>groupList = groupDataLoader.getGroupList();
		indGroupList = new HashMap<Integer, Node>();
		for(Group group : groupList){
			Node node = new Node(group.getName(),group.getComment(),"group", "35"); //$NON-NLS-1$ //$NON-NLS-2$
			node.setData(group);
			indGroupList.put(group.getId(), node);
			addNode(node);
		}	
	}
	

	private void generateUserElements() {
		List<User>userList = groupDataLoader.getUserList();
		indUserList = new HashMap<Integer, Node>();
		for(User user : userList){
			Node node = new Node(user.getName(),user.getFunction(), "user", "25"); //$NON-NLS-1$ //$NON-NLS-2$
			node.setData(user);
			indUserList.put(user.getId(),node);
			addNode(node);
		}
	}
	
	private void setGroupEdge(){
		List<Group>groupList = groupDataLoader.getGroupList();
		for(Group group : groupList){
			if (group.getParentId() != null){
				Node parent = indGroupList.get(group.getParentId());
				Node child = indGroupList.get(group.getId());
				child.addParentNode(parent);
			}
		}
	}
	
	private void setUserEdge(){
		List<Group>groupList = groupDataLoader.getGroupList();
		for(Group group : groupList){
			Node parent = indGroupList.get(group.getId());
			List<User>userList = groupDataLoader.getUsersInGroup(group);
			for (User user : userList){
				Node child = indUserList.get(user.getId());
				child.addParentNode(parent);
			}
		}
	}

	@Override
	public Map<Class<?>, List<FlexProperty>> createExtraAtrributes() {
		Map<Class<?>, List<FlexProperty>> list = new HashMap<Class<?>, List<FlexProperty>>();
		ArrayList<FlexProperty> usersProp = new ArrayList<FlexProperty>();
		usersProp.add(new FlexProperty("bMail", "getBusinessMail")); //$NON-NLS-1$ //$NON-NLS-2$
		usersProp.add(new FlexProperty("cellular", "getCellular")); //$NON-NLS-1$ //$NON-NLS-2$
		usersProp.add(new FlexProperty("fonction", "getFunction")); //$NON-NLS-1$ //$NON-NLS-2$
		usersProp.add(new FlexProperty("login", "getLogin")); //$NON-NLS-1$ //$NON-NLS-2$
		usersProp.add(new FlexProperty("pMail", "getPrivateMail")); //$NON-NLS-1$ //$NON-NLS-2$
		usersProp.add(new FlexProperty("sName", "getSkypeName")); //$NON-NLS-1$ //$NON-NLS-2$
		usersProp.add(new FlexProperty("sNumber", "sNumber")); //$NON-NLS-1$ //$NON-NLS-2$
		usersProp.add(new FlexProperty("surname", "getSurname")); //$NON-NLS-1$ //$NON-NLS-2$
		usersProp.add(new FlexProperty("phone", "getTelephone")); //$NON-NLS-1$ //$NON-NLS-2$
		list.put(User.class, usersProp);
		return list;
	}
	
	
}
