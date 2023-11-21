package bpm.vanilla.platform.core.beans.ged;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import bpm.vanilla.platform.core.beans.ged.constant.SecurityConstants;

public class SecurityProperties {
	private Properties simples = new Properties();
	private HashMap<String, List<String>> multiples = new HashMap<String, List<String>>();
	
	public void setUserId(String userId) throws Exception {
		setProperty(SecurityConstants.USERID.getName(), userId);
	}
	
	public void addGroup(String groupId) throws Exception {
		setProperty(SecurityConstants.GROUPID.getName(), groupId);
	}
	
	public void setRepositoryId(String repositoryId) throws Exception {
		setProperty(SecurityConstants.REPOSITORYID.getName(), repositoryId);
	}
	
	public String getUserId() {
		if (simples.getProperty(SecurityConstants.USERID.getName()) != null) {
			return simples.getProperty(SecurityConstants.USERID.getName());
		}
		else {
			return null;
		}
	}
	
	public List<String> getGroupId() {
		List<String> l =  multiples.get(SecurityConstants.GROUPID.getName());
		if (l == null){
			return new ArrayList<String>();
		}
		return l;
	}
	
	public String getRepositoryId() {
		if (simples.getProperty(SecurityConstants.REPOSITORYID.getName()) != null) {
			return simples.getProperty(SecurityConstants.REPOSITORYID.getName());
		}
		else {
			return null;
		}
	}
	
	private void setProperty(String pname, String value) throws Exception {
		if (pname.equals(SecurityConstants.GROUPID.getName())) {
			if (multiples.get(pname) == null) {
				multiples.put(pname, new ArrayList<String>());
			}
			multiples.get(pname).add(value);
		}
		else if (pname.equals(SecurityConstants.REPOSITORYID.getName()) && value != null){
			simples.setProperty(pname, value);
		}
		else if (pname.equals(SecurityConstants.USERID.getName()) && value != null){
			simples.setProperty(pname, value);
		}
	}

}
