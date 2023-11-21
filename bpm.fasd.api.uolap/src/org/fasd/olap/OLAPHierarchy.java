package org.fasd.olap;

import java.util.ArrayList;
import java.util.List;

import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;
import org.fasd.datasource.DataView;
import org.fasd.inport.mondrian.beans.InlineBean;
import org.fasd.olap.exceptions.HierarchyException;
import org.fasd.utils.ActionGetPath;
import org.fasd.utils.Path;



public class OLAPHierarchy extends OLAPElement {
	private static int counter = 0;
	public static void resetCounter(){
		counter =0;
	}
	private int nbLevel = 0;
	
//	private boolean hasAll = true;
	private String allMember = "All";
	private String table = "";
	private String desc = "";
	
	private String primaryKey = "";
	private String primaryKeyTable = "";
	private String tableName = "";
//	private String defaultMember = "";
	private DataObjectItem primaryKeyTableCol ;
	private OLAPDimension parent;
	private String caption = "";
	private String allMemberCaption = "";
	private String memberReaderClass = "";
	private InlineBean inlineBean;
	private DataView view;
	
	private List<Joint> joints = new ArrayList<Joint>();
	
	private ArrayList<OLAPLevel> lvls = new ArrayList<OLAPLevel>();
	private List<Parameter> params = new ArrayList<Parameter>();

	public OLAPHierarchy(String name) {
		super(name);
		//this.name = name;
		this.table = name;
		counter++;
		id = "n" + String.valueOf(counter);
	}
	
	public OLAPHierarchy() {
		super("");
		//this.name = "";
		this.table = "";
		counter++;
		id = "n" + String.valueOf(counter);
	}
	
	public void setId(String id) {
		this.id = id;
		int i = Integer.parseInt(id.substring(1));
		if (i > counter){
			counter = i + 1;
		}
	}
	
	public void setName(String name) {
		super.setName(name);
	}
	
//	public String getHierarchyName() {
//		return name;
//	}
	
	public void addLevel(OLAPLevel lvl) throws HierarchyException {
		if ((lvls.size() > 0 && lvls.get(0).getClosureTable() != null) ||
			(lvls.size() > 0 && lvl.getClosureChildCol() != null)){
			throw new HierarchyException("Parent Child hierarchy can only hav one level");
		}
		
		
		lvls.add(lvl);
		if (lvl.getNb() == -1)
			lvl.setNb(nbLevel);
		lvl.setParent(this);
		nbLevel ++;
	}
	
	public void addLevel(int i, OLAPLevel lvl) throws HierarchyException {
		if ((lvls.size() > 0 && lvls.get(0).getClosureChildCol() != null) ||
				(lvls.size() > 0 && lvl.getClosureParentCol() != null)){
				throw new HierarchyException("Parent Child hierarchy can only hav one level");
			}
		if (lvl.getNb() == -1)
			lvl.setNb(nbLevel);
		lvl.setParent(this);
		nbLevel ++;
		lvls.add(i, lvl);
		for(int j = i+1; j<lvls.size(); j++){
			lvls.get(j).setNb(lvls.get(j).getNb() + 1);
		}
		
	}
	
	public ArrayList<OLAPLevel> getLevels() {
		return lvls;
	}

	public String getAllMember() {
		if(allMember != null && (allMember.isEmpty() || allMember.equals("All"))) {
			allMember = "All " + parent.getName();
		}
		return allMember;
	}

	public void setAllMember(String allMember) {
		this.allMember = "" + allMember;
	}

//	public boolean isHasAll() {
//		return hasAll;
//	}

//	public void setHasAll(boolean hasAll) {
//		this.hasAll = hasAll;
//	}
	
//	public void setHasAll(String s){
//		this.hasAll = Boolean.parseBoolean(s);
//	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = "" + table;
	}

	
	public void removeLevel(OLAPLevel level){
		for(int i=level.getNb()+1; i<lvls.size(); i++){
			OLAPLevel curentL = lvls.get(i);
			curentL.setNb(curentL.getNb() - 1);
		}
		
		nbLevel--;
		lvls.remove(level);
	}
	
	public String getFAXML() {
		String buf = "        <Hierarchy>\n";
		buf += "            <id>" + super.getId() + "</id>\n";
		buf += "            <name>" + getName() + "</name>\n";
		buf += "            <caption>" + caption + "</caption>\n";
		buf += "            <memberReaderClass>" + memberReaderClass + "</memberReaderClass>\n";
		buf += "            <allMemberCaption>" + allMemberCaption + "</allMemberCaption>\n";
		buf += "            <desc>" + desc + "</desc>\n";
		buf += "            <dimension-id>";
		if (parent != null)
			buf += parent.getId();
		buf += "</dimension-id>\n";
		
//		buf += "            <hasAll>" + hasAll + "</hasAll>\n";
		buf += "            <allMember>" + allMember + "</allMember>\n";
//		buf += "            <defaultMember>" + defaultMember + "</defaultMember>\n";
		
		for (int i=0; i < lvls.size(); i++) {
			buf += lvls.get(i).getFAXML();
		}
		for(Parameter p :params){
			buf += p.getFAXML();
		}
		buf += "        </Hierarchy>\n";
		
		return buf;
	}
	
	public String buildJoins(Path path, int i){
		StringBuffer buf = new StringBuffer();
		if (i>= path.size())
			return "";
		
		if (i == path.size() - 1){
			buf.append("<Join leftKey=\"" + path.getRelationship(i).getLeftObjectItem().getName() + "\" ");
			buf.append("rightKey=\"" + path.getRelationship(i).getRightObjectItem().getName() + "\">\n");
			
//			if (path.getRelationship(i).getLeftObject().isInline()){
//				buf.append(path.getRelationship(i).getRightObject().getDatas().getXML());
//			}
//			else{
			
			
//				buf.append("    <Table name=\"" + path.getRelationship(i).getLeftObject().getShortName() + "\"/>\n");
			
			
			String tableName =  path.getRelationship(0).getLeftObject().getPhysicalName();
			if (tableName.contains(".")){
				buf.append("                <Table name=\"" + tableName.substring( tableName.indexOf(".") + 1) +"\"");
				buf.append(" schema=\"" +tableName.substring(0, tableName.indexOf("."))  + "\" ");
			}
			else{
				buf.append("                <Table name=\"" + tableName +"\"");
				
			}
			
			
			
//			buf.append("                <Table name=\"" + path.getRelationship(0).getLeftObject().getShortName() +"\"");
//			if (path.getRelationship(i).getLeftObject().getDataSource().getDriver().getSchemaName() != null && !"".equals(path.getRelationship(i).getLeftObject().getDataSource().getDriver().getSchemaName())){
//				buf.append(" schema=\"" + path.getRelationship(i).getLeftObject().getDataSource().getDriver().getSchemaName() + "\" ");
//			}
			buf.append("/>");
		//	}
			
//			if (path.getRelationship(i).getRightObject().isInline()){
//				buf.append(path.getRelationship(i).getRightObject().getDatas().getXML());
//			}
//			else{
			
//				buf.append("    <Table name=\"" + path.getRelationship(i).getRightObject().getShortName() + "\"/>\n");
			
			tableName =  path.getRelationship(0).getRightObject().getPhysicalName();
			if (tableName.contains(".")){
				buf.append("                <Table name=\"" + tableName.substring( tableName.indexOf(".") + 1) +"\"");
				buf.append(" schema=\"" +tableName.substring(0, tableName.indexOf("."))  + "\" ");
			}
			else{
				buf.append("                <Table name=\"" + tableName +"\"");
				
			}
			
//			buf.append("                <Table name=\"" + path.getRelationship(0).getRightObject().getShortName() +"\"");
//			if (path.getRelationship(i).getRightObject().getDataSource().getDriver().getSchemaName() != null && !"".equals(path.getRelationship(i).getRightObject().getDataSource().getDriver().getSchemaName())){
//				buf.append(" schema=\"" + path.getRelationship(i).getRightObject().getDataSource().getDriver().getSchemaName() + "\" ");
//			}
			buf.append("/>");
			
//			}
			
			
			buf.append("</Join>\n");
			
			return buf.toString();
		}
		
		buf.append("<Join leftKey=\"" + path.getRelationship(i).getLeftObjectItem().getName() + "\" ");
		buf.append("rightKey=\"" + path.getRelationship(i).getRightObjectItem().getName() + "\" ");
		buf.append("rightAlias=\"" + path.getRelationship(i).getRightObject().getName() + "\">\n");
//		buf.append("    <Table name=\"" + path.getRelationship(i).getLeftObject().getShortName() + "\"/>\n");
		
		String tableName =  path.getRelationship(0).getLeftObject().getPhysicalName();
		if (tableName.contains(".")){
			buf.append("                <Table name=\"" + tableName.substring( tableName.indexOf(".") + 1) +"\"");
			buf.append(" schema=\"" +tableName.substring(0, tableName.indexOf("."))  + "\" ");
		}
		else{
			buf.append("                <Table name=\"" + tableName +"\"");
			
		}
		
//		buf.append("                <Table name=\"" + path.getRelationship(0).getLeftObject().getShortName() +"\"");
//		if (path.getRelationship(i).getLeftObject().getDataSource().getDriver().getSchemaName() != null && !"".equals(path.getRelationship(i).getLeftObject().getDataSource().getDriver().getSchemaName())){
//			buf.append(" schema=\"" + path.getRelationship(i).getLeftObject().getDataSource().getDriver().getSchemaName() + "\" ");
//		}
		
		buf.append("/>");
		buf.append(buildJoins(path, i+1));
		buf.append("</Join>\n");
	
		return buf.toString();
	}
	
	protected DataObjectItem getForeignKey(DataObject factObject){
		List<DataObject> list = new ArrayList<DataObject>();
		list.add(factObject);
		for(OLAPLevel l : lvls){
			if (l.getItem() != null)
				if (!list.contains(l.getItem().getParent()))
					list.add(l.getItem().getParent());
		}
		if (list.size()>0){
			ActionGetPath a = new ActionGetPath(getParent().getParent().getParent(), list.toArray(new DataObject[list.size()]));
			a.run();
			Path path = a.getPath();
			if (path.size()> 0 )
				return path.getRelationship(0).getLeftObjectItem();
		}
		
		return null;
		
	}
	
	
	
	public String getXML(DataObject factObject){
		List<DataObject> list = new ArrayList<DataObject>();
		list.add(factObject);
		for(OLAPLevel l : lvls){
			if (l.getItem() != null && l.getItem().getParent() != null && !list.contains(l.getItem().getParent()))
				list.add(l.getItem().getParent());
		}
		ActionGetPath a = new ActionGetPath(getParent().getParent().getParent(), list.toArray(new DataObject[list.size()]));
		a.run();
		Path path = null;
		if (list.size() > 1){
			path = a.getPath();
		}
			
		
		String buf ="";
		
		buf += "            <Hierarchy";
		
		
		
//		if (parent.getHierarchies().size()>1 && parent.getHierarchies().get(0) != this){
//			buf += " name=\"" + getName() + "\"";
//		}
		if (!getName().trim().equals(""))
			buf += " name=\"" + getName() + "\"";
		if (!caption.trim().equals("")){
			buf += " caption=\"" + caption + "\"";
		}
		
		if (!allMemberCaption.trim().equals("")){
			buf += " allMemberCaption=\"" + allMemberCaption + "\"";
		}
		
		if (!memberReaderClass.trim().equals("")){
			buf += " memberReaderClass=\"" + memberReaderClass + "\"";
		}
		
//		if (hasAll){
////			if (getLevels().size() == 1 && (getLevels().get(0).getParentItem() != null  || getLevels().get(0).getParentColumnId() != null)){
////				buf+=" hasAll=\"false\"";
////			}
////			else{
//				buf+=" hasAll=\""+ hasAll +"\" allMemberName=\"" + allMember +"\"";
////			}
//			
//		}
//		else if (!defaultMember.equals(""))
//			buf+=" defaultMember=\""+ defaultMember +"\""; 
		
		if (path != null && path.size()>0 && path.getRelationship(0).getRightObjectItem() != null)
			buf += " primaryKey=\"" + path.getRelationship(0).getRightObjectItem().getOrigin() + "\"";
		else if (!primaryKey.equals(""))
			buf += " primaryKey=\"" + primaryKey + "\"";
		
		if (path != null && path.size()>1){
			buf += " primaryKeyTable=\"" ;
				if (path.getRelationship(0).getRightObjectItem() != null)
					buf += path.getRelationship(0).getRightObject().getShortName();
			
			
			buf += "\">\n";
			
			
			buf += buildJoins(path,1);
		}
		else{
			buf += ">\n";

			//if ()
			
			if (path != null && path.size()>0 && path.getRelationship(0).getRightObject() != null){
				if (path.getRelationship(0).getRightObject() == this.getLevels().get(0).getItem().getParent()){
					if (!path.getRelationship(0).getRightObject().isInline()){
						if (path.getRelationship(0).getRightObject() instanceof DataView)
							buf += ((DataView)path.getRelationship(0).getRightObject()).getXML();
						else{
							
							String tableName =  path.getRelationship(0).getRightObject().getPhysicalName();
							if (tableName.contains(".")){
								buf += "                <Table name=\"" + tableName.substring( tableName.indexOf(".") + 1) +"\"";
								buf += " schema=\"" +tableName.substring(0, tableName.indexOf("."))  + "\" ";
							}
							else{
								buf += "                <Table name=\"" + tableName +"\"";
								
							}
//							buf += "                <Table name=\"" + path.getRelationship(0).getRightObject().getShortName() +"\"/>";
							
							buf += "/>";
						}
							
					}
						
					else
						buf += path.getRelationship(0).getRightObject().getDatas().getXML();
				}
				else{
					if (!path.getRelationship(0).getLeftObject().isInline()){
						if (path.getRelationship(0).getLeftObject() instanceof DataView)
							buf += ((DataView)path.getRelationship(0).getLeftObject()).getXML();
						else{
							String tableName = path.getRelationship(0).getLeftObject().getPhysicalName();
							
							if (tableName.contains(".")){
								buf += "                <Table name=\"" + tableName.substring( tableName.indexOf(".") + 1) +"\"";
								buf += " schema=\"" + tableName.substring(0, tableName.indexOf(".")) + "\" ";
								
							}
							else{
								buf += "                <Table name=\"" + tableName +"\"";
								
							}
							
							
							buf += "/>";
						}
							
						
					}
						
					else
						buf += path.getRelationship(0).getLeftObject().getDatas().getXML();
				}
			}
				
			else if(!tableName.equals(""))
				buf += "                <Table name=\"" +tableName + "\"/>";
			//???
			else if (parent.isDegenerated(factObject)){
//				buf+= "                <Table name=\"" +factObject.getShortName() + "\"/>";
				String tname = factObject.getPhysicalName();
				
				if (tname.contains(".")){
					buf += "                <Table name=\"" + tname.substring(tname.indexOf(".") + 1) +"\"";
					buf += " schema=\"" + factObject.getPhysicalName().substring(0, factObject.getPhysicalName().indexOf(".")) + "\" ";

				}
				else{
					buf += "                <Table name=\"" + tname +"\"";
				}
								
				buf += "/>";
			}
			buf += "\n";
		}
		

		for (int i=0; i < lvls.size(); i++) {
			buf += lvls.get(i).getXML();
		}
		for(Parameter p :params){
			buf += p.getXML();
		}
		
		buf += "            </Hierarchy>\n";

		return buf;
	}

	
	public OLAPDimension getParent() {
		return parent;
	}

	public void setParent(OLAPDimension parent) {
		this.parent = parent;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<Joint> getJoints() {
		return joints;
	}
	
	public void addJoint(Joint j){
		joints.add(j);
		System.out.println("hiera >> addJoint");
	}

	public String getPrimaryKeyTable() {
		return primaryKeyTable;
	}

	public void setPrimaryKeyTable(String primaryKeyTable) {
		this.primaryKeyTable = primaryKeyTable;
	}

	public DataObjectItem getPrimaryKeyTableCol() {
		return primaryKeyTableCol;
	}

	public void setPrimaryKeyTableCol(DataObjectItem primaryKeyTableCol) {
		this.primaryKeyTableCol = primaryKeyTableCol;
	}
	
	public boolean isSnowFlakes(){
		DataObject table = null;
		for(OLAPLevel l : lvls){
			if (table == null)
				table = l.getItem().getParent();
			if (l.getItem() == null)
				return false;
			if (l.getItem().getParent() != table)
				return true;
		}
		return false;
	}
//
//	public String getDefaultMember() {
//		return defaultMember;
//	}
//
//	public void setDefaultMember(String defaultMember) {
//		this.defaultMember = defaultMember;
//	}

	public InlineBean getInlineBean() {
		return inlineBean;
	}

	public void setInlineBean(InlineBean inlineBean) {
		this.inlineBean = inlineBean;
	}
	
	public void setView(DataView view){
		this.view = view;
	}
	
	public DataView getView(){
		return view;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getAllMemberCaption() {
		return allMemberCaption;
	}

	public void setAllMemberCaption(String allMemberCaption) {
		this.allMemberCaption = allMemberCaption;
	}

//	public String getMemberReaderClass() {
//		return memberReaderClass;
//	}
//
//	public void setMemberReaderClass(String memberReaderClass) {
//		this.memberReaderClass = memberReaderClass;
//	}
	
	public List<Parameter> getParams(){
		return params;
	}
	
	public void addParameter(Parameter p){
		params.add(p);
	}
	
	/**
	 * return all reflexive relation on the hierarchy
	 * @return
	 */
	public List<OLAPRelation> getPCRelations(){
		List<OLAPRelation> rel = new ArrayList<OLAPRelation>();
		
		FAModel model = getParent().getParent().getParent();
		
		for(OLAPLevel l : lvls){
			for (OLAPRelation r : model.getRelations()){
				if (r.isReflexive() && r.isUsingItem(l.getItem())){

					boolean inside = false;
					for (OLAPLevel ll : lvls){
						if (r.getLeftObjectItem() == l.getItem()){
							if (ll.getItem() == r.getRightObjectItem()){
								inside = true;
								break;
							}
						}
						else{
							if (ll.getItem() == r.getLeftObjectItem()){
								inside = true;
								break;
							}
						}
					}

					if (inside)
						rel.add(r);
				}
			}
			
			
		}
		
		return rel;
	}
	
	public void swapLevels(OLAPLevel l1, OLAPLevel l2){
		if(lvls.contains(l1) && lvls.contains(l2)){
			
			int i1 = lvls.indexOf(l1);
			int i2 = lvls.indexOf(l2);
			OLAPLevel tmp = lvls.get(i1);
			
			lvls.set(i1, l2);
			lvls.set(i2, tmp);
			
		}
	}
	
	public String getUniqueName() {
		return "["+parent.getName()+"."+getName()+"]";
	}
}
