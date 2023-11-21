package org.fasd.security;

import java.util.ArrayList;
import java.util.List;

import org.fasd.olap.OLAPElement;
import org.fasd.olap.OLAPHierarchy;


public class View extends OLAPElement {
	private static int counter = 0;
	
	private String desc = "";
	private String securityGroupId = "";
	private boolean allowAccess = false;
	private boolean allowFullAccess = false;
	private List<String> restrictItems = new ArrayList<String>();
	
	private SecurityGroup group;
	private SecurityDim parent;
	private OLAPHierarchy hiera;
	private String hierarchyId;
	
	public View(OLAPHierarchy hiera){
		super("");
		counter++;
		setId("z" + String.valueOf(counter));
		this.hiera = hiera;
	}
	
	public View(){
		super("");
		counter++;
		setId("z" + String.valueOf(counter));
	}
	
	public void setId(String id) {
		this.id = id;
		int i = Integer.parseInt(id.substring(1));
		if (i > counter){
			counter = i + 1;
		}
	}
	
	public void setHierarchyId(String id){
		hierarchyId = id;
	}
	
	public String getHierarchyId(){
		return hierarchyId;
	}
	
	public void setHierarchy(OLAPHierarchy h){
		this.hiera = h;
	}
	
	public View(String name){
		super(name);
		counter++;
		setId("z" + String.valueOf(counter));
	}
	
	public SecurityDim getParent(){
		return parent;
	}
	
	public void setParent(SecurityDim sd){
		parent = sd;
	}

	public boolean isAllowAccess() {
		return allowAccess;
	}

	public void setAllowAccess(boolean allowAccess) {
		this.allowAccess = allowAccess;
	}
	public void setAllowAccess(String allowAccess) {
		this.allowAccess = Boolean.parseBoolean(allowAccess);
	}

	public boolean isAllowFullAccess() {
		return allowFullAccess;
	}

	public void setAllowFullAccess(boolean allowFullAccess) {
		this.allowFullAccess = allowFullAccess;
	}
	public void setAllowFullAccess(String allowFullAccess) {
		this.allowFullAccess = Boolean.parseBoolean(allowFullAccess);
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getSecurityGroupId() {
		return securityGroupId;
	}

	public void setSecurityGroupId(String securityGroupId) {
		this.securityGroupId = securityGroupId;
	}

		
	public SecurityGroup getGroup(){
		return group;
	}
	
	public void setGroup(SecurityGroup group){
		if (group != null){
			group.removeView(this);
		}
		if (group != null){
			setSecurityGroupId(group.getId());
			this.group = group;
		}
		else{
			setSecurityGroupId("");
			group = null;
		}
		if (group != null)
			group.addView(this);
		
	}
	
	public String getFAXML(){
		StringBuffer buf = new StringBuffer();
		buf.append("        <groupdefinition-item>\n");
		buf.append("            <id>" + getId() + "</id>\n");
		buf.append("            <name>" + getName() + "</name>\n");
		buf.append("            <description>" + getDesc() + "</description>\n");
		buf.append("            <securitygroup-id>" + securityGroupId + "</securitygroup-id>\n");
		buf.append("            <hierarchy-id>" + hiera.getId() + "</hierarchy-id>\n");
		buf.append("            <AllowAcces>" + allowAccess + "</AllowAcces>\n");
		buf.append("            <AllowFullAcces>" + allowFullAccess + "</AllowFullAcces>\n");
		
		for(String i : restrictItems)
			buf.append("            <RestrictionMemberList>" + i + "</RestrictionMemberList>\n");
		
		buf.append("        </groupdefinition-item>\n");
		return buf.toString();
	}

	public void addMember(String member) {
		restrictItems.add(member);
	}
	
	public List<String> getMembers(){
		return restrictItems;
	}

	public void removeMember(String s) {
		for(String m : restrictItems){
			if (m.equals(s)){
				restrictItems.remove(m);
				break;
			}
		}
	}
	
	
	public String getXML(){
		StringBuffer buf = new StringBuffer();
		
		if (allowFullAccess == true){
			if (restrictItems.size() > 0){
				if (!hiera.getName().trim().equals(""))
					buf.append("                 <HierarchyGrant hierarchy=\"[" + hiera.getParent().getName() + "].[" + hiera.getName() + "]\" access=\"custom\" topLevel=\"" + hiera.getLevels().get(0).getName() + "\">\n");
				else
					buf.append("                 <HierarchyGrant hierarchy=\"" + hiera.getParent().getName() + "\" access=\"custom\" topLevel=\"" + hiera.getLevels().get(0).getName() + "\" bottomLevel=\"" + hiera.getLevels().get(hiera.getLevels().size() - 1).getName() +"\">\n");
				for(String s : restrictItems){
					buf.append("                     <MemberGrant member=\"" + s + "\" access=\"none\"/>\n");
				}
				buf.append("                 </HierarchyGrant>\n");
			}
			else{
				if (!hiera.getName().trim().equals(""))
					buf.append("                 <HierarchyGrant hierarchy=\"[" + hiera.getParent().getName() + "].[" + hiera.getName() + "]\" access=\"all\"/>\n");
				else
					buf.append("                 <HierarchyGrant hierarchy=\"" + hiera.getParent().getName() + "\" access=\"all\"/>\n");
			}
		}
		else{
			if (restrictItems.size() > 0){
				if (!hiera.getName().trim().equals(""))
					buf.append("                 <HierarchyGrant hierarchy=\"[" + hiera.getParent().getName() + "].[" + hiera.getName() + "]\" access=\"custom\">\n");
				else
					buf.append("                 <HierarchyGrant hierarchy=\"" + hiera.getParent().getName()  + "\" access=\"custom\">\n");
				for(String s : restrictItems){
					buf.append("                     <MemberGrant member=\"" + s + "\" access=\"all\"/>\n");
				}
				buf.append("                 </HierarchyGrant>\n");

			}
			else{
				if (!hiera.getName().trim().equals(""))
					buf.append("                 <HierarchyGrant hierarchy=\"[" + hiera.getParent().getName() + "].[" + hiera.getName() + "]\" access=\"none\"/>\n");
				else
					buf.append("                 <HierarchyGrant hierarchy=\"" + hiera.getParent().getName() + "\" access=\"none\"/>\n");
			}
		}
		
		
		
		return buf.toString();
	}

	public OLAPHierarchy getHierarchy() {
		return hiera;
	}
}
