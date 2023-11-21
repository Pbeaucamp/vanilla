package org.fasd.olap;

import java.util.ArrayList;
import java.util.List;

import org.fasd.inport.mondrian.beans.RoleGrantBean;
import org.fasd.olap.aggregate.AggregateTable;
import org.fasd.olap.list.ListCube;
import org.fasd.olap.list.ListDimGroup;
import org.fasd.olap.list.ListDimension;
import org.fasd.olap.list.ListMeasure;
import org.fasd.olap.list.ListMesGroup;
import org.fasd.olap.list.ListVirtualCube;
import org.fasd.olap.virtual.VirtualCube;
import org.fasd.security.SecurityDim;
import org.fasd.security.SecurityGroup;
import org.fasd.security.View;
import org.fasd.xmla.ISchema;




public class OLAPSchema extends OLAPElement implements ISchema{
	private String name = "MySchema";
	
	//private ArrayList<OLAPCube> cubes = new ArrayList<OLAPCube>();
	private ListCube cubes = new ListCube();
	
	//private ArrayList<OLAPDimension> dims = new ArrayList<OLAPDimension>();
	private ListDimension dims = new ListDimension();
	
	//private ArrayList<OLAPMeasure> mes = new ArrayList<OLAPMeasure>();
	private ListMeasure mes = new ListMeasure();
	
	private ArrayList<SecurityDim> dimView = new ArrayList<SecurityDim>();
	
	//private List<OLAPDimensionGroup> dimGrps = new ArrayList<OLAPDimensionGroup>();
	private ListDimGroup dimGrps = new ListDimGroup();
	
	//private List<OLAPMeasureGroup> mesGrps = new ArrayList<OLAPMeasureGroup>();
	private ListMesGroup mesGrps = new ListMesGroup();
	
	private List<SecurityGroup> secGrps = new ArrayList<SecurityGroup>();
	
	private List<AggregateTable> aggs = new ArrayList<AggregateTable>();
	
	
	//private List<VirtualCube> virtualCubes = new ArrayList<VirtualCube>();
	private ListVirtualCube virtualCubes = new ListVirtualCube();
	
	private List<NamedSet> namedSets = new ArrayList<NamedSet>();
	
	private List<UserDefinedFunction> userFunctions = new ArrayList<UserDefinedFunction>();
	
	private List<RoleGrantBean> mondrianRole = new ArrayList<RoleGrantBean>();
	
	private String measureCaption = "";
	private String defaultRole = "";
	
	private FAModel parent;
	
	
	public OLAPSchema() {
	}
	
	public void addCube(OLAPCube cube) {
		cubes.add(cube);
		cube.setParent(this);
	}
	
	public FAModel getParent(){
		return parent;
	}
	
	public void setParent(FAModel parent){
		this.parent = parent;
	}
	
	public void addVirtualCube(VirtualCube vCube){
		virtualCubes.add(vCube);
	}
	
	public void removeVirtualCube(VirtualCube vCube){
		virtualCubes.remove(vCube);
	}
	
	public List<VirtualCube> getVirtualCubes(){
		return virtualCubes.getList();
	}
	public void addDimensionGroup(OLAPDimensionGroup g){
		dimGrps.add(g);
	}
	
	public void removeDimensionGroup(OLAPDimensionGroup element){
		dimGrps.remove(element);
		if (element.getParent() != null){
			element.getParent().removeChild(element);
		}
		for(OLAPDimension d : element.getDimensions()){
			d.setGroup(null);
		}
		
		for(OLAPGroup gg : element.getChilds()){
			removeDimensionGroup((OLAPDimensionGroup)gg);
		}
	}
	
	public List<OLAPDimensionGroup> getDimensionGroups(){
		return dimGrps.getList();
	}
	
	public List<OLAPMeasureGroup> getMeasureGroups(){
		return mesGrps.getList();
	}
	
	public List<OLAPCube> getCubes() {
		return cubes.getList();
	}
	
	public void addDimension(OLAPDimension dim) {
		dims.add(dim);
		dim.setParent(this);
		boolean exists = false;
//		for(SecurityDim s : dimView){
//			if (s.getDim() == dim){
//				exists = true;
//			}
//		}
//		
//		if (!exists){
//			SecurityDim sd = new SecurityDim();
//			sd.setDim(dim);
//			sd.setName(dim.getName()+ " view");
//			addDimView(sd);
//		}

		
		
	}
	
	public List<OLAPDimension> getDimensions() {
		return dims.getList();
	}
	



	
	public void removeDimension(String name) {
		for (int i=0; i < dims.getList().size(); i++) {
			if (dims.getList().get(i).getName().equals(name)) {
				System.out.println("removing dimension in OLAPSchema = " + dims.getList().get(i).getName());
				dims.getList().remove(i);
				break;
			}
		}
	}
	
	public void removeDimension(OLAPDimension dim){
		dims.remove(dim);
	}
	public void removeCube(String name) {
		for (int i=0; i < cubes.getList().size(); i++) {
			if (cubes.getList().get(i).getName().equals(name)) {
				System.out.println("removing cube in OLAPSchema = " + cubes.getList().get(i).getName());
				cubes.getList().remove(i);
				break;
			}
		}
	}
	
	public void removeCube(OLAPCube cube){
		cubes.remove(cube);
	}
	

	

	
	public String getXML() {
		String buf = "";
				//"failed, check io?";
		
		buf += "<Schema name=\"" + name + "\"";
		if (!defaultRole.trim().equals(""))
			buf += "defaultRole=\"" + defaultRole + "\"";
		if (!measureCaption.trim().equals(""))
			buf += "measureCaption=\"" + measureCaption + "\"";
		
		buf += ">\n\n";
		
		//userdefinedfunction
		for(UserDefinedFunction f : userFunctions)
			buf += f.getXML();
		
		for(OLAPDimension d : dims.getList()){
			for(OLAPCube c : cubes.getList()){
				if (c.getDims().contains(d)){
					buf += d.getXML(c.getFactDataObject());
					break;
				}
			}
			
		}
		
		for (int i=0; i < cubes.getList().size(); i++) {
			buf += cubes.getList().get(i).getXML();
		}
		
		for(VirtualCube vCube : virtualCubes.getList())
			buf += vCube.getXML();

		//buf += "    <Roles>\n";
		
		
//		for (SecurityGroup s :  secGrps){
//			boolean needed = false;
//			for(OLAPCube c : cubes.getList()){
//				if (c.getSecurityGroups().contains(s)){
//					needed = true;
//					break;
//				}
//			}
//			if (needed)
//				buf += s.getXML();
//		}
		//buf += "    </Roles>\n";
		
		
		for(NamedSet ns : namedSets){
			if (ns.isGlobal())
				buf += ns.getXML();
		}
		
		buf += "</Schema>\n";
		
		return buf;
	}
	
	public String getFAXML() {
		String tmp = "<olap>\n\n";
		
		tmp += "    <defaultRoleName>" + defaultRole + "</defaultRoleName>\n";
		tmp += "    <measureCaption>" + measureCaption + "</measureCaption>\n";
		
		tmp += "    <UserDefinedFunctions>\n";
		for(UserDefinedFunction f : userFunctions)
			tmp += f.getFAXML();
		tmp += "    </UserDefinedFunctions>\n";
		
		tmp += "    <Dimension-group>\n";
		List<OLAPGroup> lst = new ArrayList<OLAPGroup>();
		
		for(OLAPDimensionGroup g : dimGrps.getList()){
			tmp += g.getFAXML();
			lst.addAll(g.getChilds());
		}
		for(OLAPGroup g : lst){
			tmp += g.getFAXML();
			lst.addAll(g.getChilds());
		}
		
		tmp += "    </Dimension-group>\n";
		
		tmp += "    <Measure-group>\n";
		List<OLAPGroup> l = new ArrayList<OLAPGroup>();
		
		for(OLAPMeasureGroup g : mesGrps.getList()){
			tmp += g.getFAXML();
			l.addAll(g.getChilds());
		}
		for(OLAPGroup g : getMeasureGroups()){
			tmp += g.getFAXML();
			l.addAll(g.getChilds());
		}
		
		tmp += "    </Measure-group>\n";
		
		tmp += "    <Security-group>\n";
		List<OLAPGroup> sl = new ArrayList<OLAPGroup>();
		
		for(SecurityGroup g : secGrps){
			tmp += g.getFAXML();
			l.addAll(g.getChilds());
		}
		for(OLAPGroup g : sl){
			tmp += g.getFAXML();
			l.addAll(g.getChilds());
		}
		
		tmp += "    </Security-group>\n";
		
		
		tmp +="    <Aggregate>\n";
		for(AggregateTable a : aggs){
			tmp += a.getFAXML();
		}
		tmp +="    </Aggregate>\n";
		
		tmp += "    <Dimension>\n";
		for (int i=0; i < dims.getList().size(); i++) {
			tmp += dims.getList().get(i).getFAXML();
		}
		tmp += "    </Dimension>\n";

		tmp += "    <Measure>\n";
		for (int i=0; i < mes.getList().size(); i++){
			tmp += mes.getList().get(i).getFAXML();
		}
		tmp += "    </Measure>\n";

		tmp += "    <Cube>\n";
		for (int i=0; i < cubes.getList().size(); i++) {
			tmp += cubes.getList().get(i).getFAXML();
		}
		tmp += "    </Cube>\n";
		
		tmp += "    <VirtualCube>\n";
		for (int i=0; i < virtualCubes.getList().size(); i++) {
			tmp += virtualCubes.getList().get(i).getFAXML();
		}
		tmp += "    </VirtualCube>\n";
		

		tmp += "    <NamedSets>\n";
		for(NamedSet n : namedSets){
			//if (n.isGlobal())
				tmp += n.getFAXML();
		}
		tmp += "    </NamedSets>\n";
		
//		tmp += "    <DimensionViews>\n";
//		for(SecurityDim d : dimView){
//			tmp += d.getFAXML();
//			
//		}
//		tmp += "    </DimensionViews>\n";
		
		
		tmp += "</olap>\n\n";
		
		
		
		return tmp;
	}


	private boolean inAllCube(NamedSet n){
		for(OLAPCube c : cubes.getList()){
			if (c.getNamedSets().contains(n)){
				return false;
				
			}
		}
		return true;
	}


	public List<OLAPMeasure> getMeasures() {
		return mes.getList();
	}

	public void addMeasure(OLAPMeasure measure) {
		mes.add(measure);
	}

	public void removeMeasure(OLAPMeasure mes) {
		this.mes.remove(mes);
	}

	public void addMeasureGroup(OLAPMeasureGroup element) {
		mesGrps.add(element);
	}
	
	public void addSecurityGroup(SecurityGroup g){
		boolean contains = false;
		for(SecurityGroup sg : secGrps){
			if (sg.getName().equals(g.getName())){
				contains = true;
				break;
			}
		}
		if (!contains){
			secGrps.add(g);
		}
		
	}
	
	public void removeSecurityGroup(SecurityGroup g){
		secGrps.remove(g);
		if (g.getParent() != null){
			g.getParent().removeChild(g);
		}
		for(View m : g.getViews())
				m.setGroup(null);

		
		for(OLAPGroup gg : g.getChilds()){
			removeSecurityGroup((SecurityGroup)gg);
		}
	}

	public void removeMeasureGroup(OLAPMeasureGroup element) {
		mesGrps.remove(element);
		if (element.getParent() != null){
			element.getParent().removeChild(element);
		}
		for(OLAPMeasure m : element.getMeasures()){
			m.setGroup(null);
		}
		element.getMeasures().clear();
		
		for(OLAPGroup gg : element.getChilds()){
			removeMeasureGroup((OLAPMeasureGroup)gg);
		}
		
		
	}

	public OLAPDimension findDimension(String s) {
		for(OLAPDimension d : dims.getList()){
			if (d.getId().equals(s))
				return d;
		}
		return null;
	}

	public OLAPMeasure findMeasure(String s) {
		for(OLAPMeasure m : mes.getList()){
			if (m.getId().equals(s))
				return m;
		}
		return null;
	}

	public OLAPMeasureGroup findMeasureGroup(String s) {
		for(OLAPMeasureGroup m : mesGrps.getList()){
			if (m.getId().equals(s))
				return m;
		}
		return null;
	}

	public OLAPDimensionGroup findDimensionGroup(String s) {
		for(OLAPDimensionGroup m : dimGrps.getList()){
			if (m.getId().equals(s))
				return m;
		}
		return null;
	}
	
	public List<SecurityDim> getDimViews(){
		return dimView;
	}
	
	public void addDimView(SecurityDim dim){
		dimView.add(dim);
	}
	
	public void removeDimView(SecurityDim dim){
		dimView.remove(dim);
	}
	
	public List<SecurityGroup> getSecurityGroups(){
		return secGrps;
	}

	public SecurityDim findDimensionView(String dimId) {
		for(SecurityDim d : dimView){
			if (d.getId().equals(dimId))
				return d;
		}
		return null;
	}

	public SecurityGroup findSecurityGroup(String parentId) {
		for(SecurityGroup g : secGrps){
			if (g.getId().equals(parentId)){
				return g;
			}
		}
		return null;
	}

	public View findView(String string) {
		for(SecurityDim d : dimView){
			for(View v : d.getViews()){
				if (v.getId().equals(string))
					return v;
			}
		}
		return null;
	}

	public OLAPDimension findDimensionByName(String s) {
		for(OLAPDimension d : dims.getList()){
			if (d.getName().equals(s))
				return d;
		}
		return null;
	}
	
	/**
	 * return the cube with teh given id or null
	 * @param id
	 * @return
	 */
	public OLAPCube findCube(String id){
		for(OLAPCube c : cubes.getList()){
			if (c.getId().equals(id))
				return c;
		}
		
		return null;
	}

	public OLAPCube findCubeNamed(String cubeName) {
		for(OLAPCube c : cubes.getList()){
			if (c.getName().equals(cubeName))
				return c;
		}
		
		return null;
	}

	public ListCube getListCube() {
		return cubes;
	}

	public ListMeasure getListMeasures() {
		return mes;
	}
	
	public ListMesGroup getListMesGroup(){
		return mesGrps;
	}

	public ListDimension getListDimensions() {
		return dims;
	}

	public ListDimGroup getListDimGroup() {
		return dimGrps;
	}

	public ListVirtualCube getListVirtualCube() {
		return virtualCubes;
	}

	public SecurityDim findSecurityDim(OLAPDimension dimension) {
		for(SecurityDim sd : dimView){
			if (sd.getDim() == dimension)
				return sd;
		}
		//if were there, the securitydim is missing so we create it
		//create SecurDim associated to this dimension
		SecurityDim sd = new SecurityDim();
		sd.setDim(dimension);
		sd.setDimId(dimension.getId());
		sd.setName(dimension.getName() + " View");
		this.addDimView(sd);
		
		
		return sd;
	}
	
	public List<AggregateTable> getAggregates(){
		return aggs;
	}
	public void addAggregate(AggregateTable agg){
		if (!aggs.contains(agg)){
			aggs.add(agg);
		}
		
	}
	
	public void removeAggregate(AggregateTable agg){
		aggs.remove(agg);
	}

	public AggregateTable findAggTableById(String s) {
		for(AggregateTable a: aggs){
			if (a.getId().equals(s))
				return a;
		}
		
		return null;
	}

	public OLAPLevel findLevel(String levelId) {
		for(OLAPDimension d : dims.getList()){
			for(OLAPHierarchy h : d.getHierarchies()){
				for(OLAPLevel l : h.getLevels()){
					if (l.getId().equals(levelId)){
						return l;
					}
				}
			}
		}
		return null;
	}

	public OLAPLevel findLevelNamed(String levelName) {
		String[] buf = levelName.split("]");
		OLAPDimension d = null;
		OLAPHierarchy h = null;
	
		
		for(OLAPDimension dim : dims.getList()){
			if (dim.getName().equals(buf[0].substring(1, buf[0].length()))){
				d = dim;
				
				if (buf.length == 2){
					for(OLAPHierarchy hiera : d.getHierarchies()){
						if (hiera.getName().equals("")){
							h = hiera;
							for(OLAPLevel lvl : h.getLevels()){
								if(lvl.getName().equals(buf[1].substring(2, buf[1].length()))){
									return lvl;
								}
							}
							break;
						}
					}
					break;
				}
				else{
					for(OLAPHierarchy hiera : d.getHierarchies()){
						if (hiera.getName().equals(buf[1].substring(2, buf[1].length()))){
							h = hiera;
							for(OLAPLevel lvl : h.getLevels()){
								if(lvl.getName().equals(buf[2].substring(2, buf[2].length()))){
									return lvl;
								}
							}
							break;
						}
					}
					break;
				}
				
			}
		}
		return null;
		
	}

	public OLAPMeasure findMeasureNamed(String mesName) throws Exception {
		String[] buf = mesName.split("]");
		String s = ""; 
		try{
			if (mesName.contains("["))
				s = buf[1].substring(2);
			else
				s = mesName;
		}
		catch (Exception e){
			throw new Exception();
		}
		for(OLAPMeasure m : mes.getList()){
			if (m.getName().equals(s))
				return m;
		}
		return null;
	}
	
	/**
	 * return the Mondrian XML for the specified cube
	 * return null if the ube is not present in the schema
	 */
	
	public String getXML(OLAPCube cube){
		if (!cubes.contains(cube))
			return null;
		
		StringBuffer buf = new StringBuffer();
		
		buf.append("<Schema name=\"" + name + "\">\n\n");
		
		for(OLAPDimension d : cube.getDims()){
			buf.append(d.getXML(cube.getFactDataObject()));
		}
		
		buf.append(cube.getXML());
		
//		for (SecurityGroup s :  cube.getSecurityGroups()){
//			buf.append(s.getXML());
//		}
		
		buf.append("</Schema>\n");
		
		return buf.toString();
	}
	
	
	/**
	 * return the cubes for the specified SecurityGroup(ie Role)
	 * the cubes can be virtual cubes
	 * @param secu
	 * @return
	 */
	public List<ICube> getCubes(SecurityGroup secu){
		List<ICube> result = new ArrayList<ICube>();
		
//		for(OLAPCube c : cubes.getList()){
//			if (c.getSecurityGroups().contains(secu))
//				result.add(c);
//		}
		
		for(VirtualCube c : virtualCubes.getList()){
			if (c.getSecurityGroups().contains(secu))
				result.add(c);
		}
		
		return result;
	}
	
	public List<ICube> getICubes(){
		List<ICube> result = new ArrayList<ICube>();
		
		for(OLAPCube c : cubes.getList()){
				result.add(c);
		}
		
		for(VirtualCube c : virtualCubes.getList()){
				result.add(c);
		}
		
		return result;
	}
	
	public void addNamedSet(NamedSet set){
		namedSets.add(set);
	}

	public List<NamedSet> getNamedSets() {
		return namedSets;
	}
	
	public NamedSet findNamedSetById(String id){
		for( NamedSet ns : namedSets){
			if (ns.getId().equals(id)){
				return ns;
			}
		}
		return null;
	}
	
	public void addUserDefinedFunction(UserDefinedFunction func){
		userFunctions.add(func);
	}

	public String getDefaultRole() {
		return defaultRole;
	}

	public void setDefaultRole(String defaultRole) {
		this.defaultRole = defaultRole;
	}

	public String getMeasureCaption() {
		return measureCaption;
	}

	public void setMeasureCaption(String measureCaption) {
		this.measureCaption = measureCaption;
	}
	
	public void addMondrianRole(RoleGrantBean bean){
		mondrianRole.add(bean);
	}
	
	public List<RoleGrantBean> getMondrianRoles(){
		return mondrianRole;
	}

	public SecurityGroup findSecurityGroupNamed(String name2) {
		for(SecurityGroup g : secGrps){
			if (g.getName().equals(name2))
				return g;
		}
		return null;
	}

	@Override
	public SchemaType getSchemaType() {
		for(ICube c : getICubes()){
			if (c.getFAType().equals("mondrian")){
				return SchemaType.MONDRIAN;
			}
		}
		
		return SchemaType.UNITED_OLAP;
	}


}
