package org.fasd.olap.virtual;

import java.util.ArrayList;
import java.util.List;

import org.fasd.datasource.DataSource;
import org.fasd.datasource.IConnection;
import org.fasd.olap.Drill;
import org.fasd.olap.ICube;
import org.fasd.olap.ICubeView;
import org.fasd.olap.NamedSet;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPElement;
import org.fasd.olap.OLAPMeasure;
import org.fasd.olap.list.ListSecurityGroup;
import org.fasd.security.SecurityGroup;
import org.fasd.xmla.ISchema;

public class VirtualCube extends OLAPElement implements ICube{
	private List<VirtualDimension> virtualDims = new ArrayList<VirtualDimension>();
	private List<VirtualMeasure> virtualMes = new ArrayList<VirtualMeasure>();
	private List<OLAPMeasure> calcMes = new ArrayList<OLAPMeasure>();
	
	private ListSecurityGroup securityGroups = new ListSecurityGroup();
	private List<String> secuId = new ArrayList<String>();
	
	private String dataSourceId = "";
	private String description = "";
	private DataSource dataSource ;
	private List<NamedSet> namedSet = new ArrayList<NamedSet>();
	private List<String> namedSetId = new ArrayList<String>();
	
	private String caption ="";
	private boolean enable = true;
	private boolean cache = true;
	
	public boolean isCache() {
		return cache;
	}

	public void setCache(boolean cache) {
		this.cache = cache;
	}
	
	public void setCache(String cache) {
		this.cache = Boolean.parseBoolean(cache);
	}
	public void setEnable(String enable) {
		this.enable = Boolean.parseBoolean(enable);
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public VirtualCube(){
		
	}
	public List<VirtualDimension> getVirtualDimensions(){
		return virtualDims;
	}
	public void addVirtualDimension(VirtualDimension dim){
		virtualDims.add(dim);
	}
	
	public void removeVirtualDimension(VirtualDimension dim){
		virtualDims.remove(dim);
	}
	
	public List<VirtualMeasure> getVirtualMeasure(){
		return virtualMes;
	}
	public void addVirtualMeasure(VirtualMeasure mes){
		virtualMes.add(mes);
	}
	
	public void removeVirtualMeasure(VirtualMeasure mes){
		virtualMes.remove(mes);
	}
	
	public List<OLAPMeasure> getCalcMeasure(){
		return calcMes;
	}
	public void addCalcMeasure(OLAPMeasure mes){
		if (mes.getType().equals("calculated"))
			calcMes.add(mes);
	}
	
	public void removeCalcMeasure(OLAPMeasure mes){
		calcMes.remove(mes);
	}
	
	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	public String getDataSourceId() {
		return dataSourceId;
	}
	public void setDataSourceId(String dataSourceId) {
		this.dataSourceId = dataSourceId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	
	public String getFAXML(){
		StringBuffer buf = new StringBuffer();
		
		buf.append("         <VirtualCube-item>\n");
		buf.append("             <id>" + getId() + "</id>\n");
		buf.append("             <name>" + getName() + "</name>\n");
		buf.append("             <enabled>" + enable + "</enabled>\n");
		buf.append("             <cached>" + cache + "</cached>\n");
		buf.append("             <caption>" + caption + "</caption>\n");
		buf.append("             <description>" + description + "</description>\n");
		buf.append("             <datasource-id>" + dataSource.getId() + "</datasource-id>\n");
		for(VirtualDimension v : virtualDims){
			buf.append(v.getFAXML());
		}
		
		for(VirtualMeasure m : virtualMes){
			buf.append(m.getFAXML());
		}
		
		for(SecurityGroup g : securityGroups.getList()){
			buf.append("        <security-group-item-id>" + g.getId() + "</security-group-item-id>\n");
		}
			
		for(NamedSet ns : namedSet){
			buf.append("        <namedSet-id>" + ns.getId() + "</namedSet-id>\n");
		}
		
		buf.append("        </VirtualCube-item>\n");
		
		return buf.toString();
	}
	
	public String getXML(){
		StringBuffer buf = new StringBuffer();
		buf.append("        <VirtualCube name=\"" + getName());
		if (!caption.trim().equals(""))
			buf.append( "\" caption=\"" + caption);
		buf.append( "\">\n");
		
		for(VirtualDimension vd : virtualDims)
			buf.append(vd.getXML());
		
		for(VirtualMeasure vm : virtualMes)
			buf.append(vm.getXML());
		
		for(OLAPMeasure m : calcMes)
			buf.append(m.getXML(null));
		
		for(NamedSet ns : namedSet){
			buf.append(ns.getXML());
			
		}
		buf.append("        </VirtualCube>\n");
		
		return buf.toString();
	}
	
	
	public void addSecurityGroup(SecurityGroup secu){
		securityGroups.add(secu);
	}
	
	public void removeSecurityGroup(SecurityGroup secu){
		securityGroups.remove(secu);
	}
	
	public List<SecurityGroup> getSecurityGroups(){
		return securityGroups.getList();
	}
	
	public void addSecurityId(String id){
		secuId.add(id);
	}
	
	public List<String> getSecurityGroupsId(){
		return secuId;
	}
	
	public void addNamedSetid(String id){
		namedSetId.add(id);
	}
	
	public void addNamedSet(NamedSet ns){
		namedSet.add(ns);
	}

	public List<String> getNamedSetsId() {
		return namedSetId;
	}

	public List<NamedSet> getNamedSets() {
		return namedSet;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public IConnection getConnection() {
		if (getDataSource() != null)
			return getDataSource().getDriver();
		else
			return null;
	}
	
	/**
	 * check if the dimension d is already in the VirtualCube instance
	 * @param d
	 * @return
	 */
	public boolean containsDimension(OLAPDimension d){
		for(VirtualDimension vd: virtualDims){
			if (vd.getDim() == d)
				return true;
		}
		return false;
	}

	public boolean containsMeasure(OLAPMeasure m) {
		for(VirtualMeasure vm: virtualMes){
			if (vm.getMes() == m)
				return true;
		}
		return false;
	}

	public List<ICubeView> getCubeViews() {
		
		return null;
	}

	public String getFAProvider() {
		return "mondrian";
	}

	public String getFAType() {
		return "mondrian";
	}

	public List<Drill> getDrills() {
		return new ArrayList<Drill>();
	}

	@Override
	public ISchema getParent() {
		
		return null;
	}
}
