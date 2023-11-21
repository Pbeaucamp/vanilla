package bpm.fa.api.olap.unitedolap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import bpm.fa.api.olap.Dimension;
import bpm.fa.api.olap.Element;
import bpm.fa.api.olap.Hierarchy;
import bpm.fa.api.olap.Level;
import bpm.fa.api.olap.OLAPMember;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.runtime.IRuntimeContext;

public class UnitedOlapMember extends Element implements OLAPMember, Comparable {
	
	private HashMap<String, OLAPMember> mbs = new LinkedHashMap<String, OLAPMember>();
	
	private Dimension dim;
	private Hierarchy hiera;
	private Level lvl;
	
	private String hieraName;
	
	private boolean sync;
	
	private int depth = 0;
	private String parentUName;
	
	private UnitedOlapMember parent = null;
	
	private String[] propertiesNames;
	private String[] propertiesValues;
	
	public UnitedOlapMember() {
		
	}
	
	public UnitedOlapMember(String uname) {
		super(uname.split("\\]\\.\\[")[uname.split("\\]\\.\\[").length - 1].replace("[", "").replace("]", ""), uname, uname.split("\\]\\.\\[")[uname.split("\\]\\.\\[").length - 1].replace("[", "").replace("]", ""));
	}
	
	public UnitedOlapMember(String name, String uname, String caption) {
		super(name, uname, caption);
	}
	
	public UnitedOlapMember(String name, String uname, String caption, Hierarchy hiera) {
		super(name, uname, caption);
		this.hiera = hiera;
	}
	
	public UnitedOlapMember(String name, String uname, String caption, Hierarchy hiera, Dimension dim, Level lvl) {
		this(name,uname,caption,hiera);
		this.dim = dim;
		this.lvl = lvl;
	}
	
	public UnitedOlapMember(Member utdMember, Hierarchy hiera) {
		super(utdMember.getName(), utdMember.getMemberRelationsUname(), utdMember.getCaption());
		this.hiera = hiera;
		
		
		if(utdMember.getProperties() != null && utdMember.getProperties().size() > 0) {
			propertiesNames = new String[utdMember.getProperties().size()];
			propertiesValues = new String[utdMember.getProperties().size()];
			for(int i = 0 ; i < utdMember.getProperties().size() ; i++) {
				propertiesNames[i] = utdMember.getProperties().get(i).getName();
				propertiesValues[i] = utdMember.getProperties().get(i).getValue();
			}
		}
		
	}
	
	public UnitedOlapMember(String name, String uname, String caption, Dimension dim, Hierarchy hiera, Level lvl) {
		super(name, uname, caption);
		this.hiera = hiera;
		this.dim = dim;
		this.lvl = lvl;
	}
	
	public UnitedOlapMember(String name, String uname, String caption, Dimension dim, Hierarchy hiera, Level lvl, int depth) {
		super(name, uname, caption);
		this.hiera = hiera;
		this.dim = dim;
		this.lvl = lvl;
		this.depth = depth;
	}
	
	@Override
	public void addMember(OLAPMember mb) {
		mbs.put(mb.getUniqueName(), mb);
		((UnitedOlapMember)mb).setParent(this);
	}

	/**
	 * @return the parent
	 */
	public OLAPMember getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	protected void setParent(UnitedOlapMember parent) {
		this.parent = parent;
	}

	@Override
	public OLAPMember findMember(OLAPMember mb) {
		
		return null;
	}
	public OLAPMember findMember(String uname) {
		if (uname.equalsIgnoreCase(this.getUniqueName())) {
			return this;
		}
		else if (uname.startsWith(this.getUniqueName())) {
			//one of my sons
			if (mbs.containsKey(uname)) {
				return mbs.get(uname);
			}
			//one of my descendents
			else {
				Iterator<OLAPMember> it = mbs.values().iterator();
				
				while (it.hasNext()) {
					OLAPMember mb = it.next();
					
					OLAPMember buf = mb.findMember(uname);
					
					if (buf != null) {
						return buf;
					}
				}
			}
		}
		
		return null;
	}

	@Override
	public Dimension getDimension() {
		return dim;
	}

	@Override
	public Hierarchy getHiera() {
		return hiera;
	}

	@Override
	public String getHierarchy() {
		if(hieraName != null) {
			return hieraName;
		}
		if(hiera != null) {
			return hiera.getUniqueName();
		}
		return null;
	}

	@Override
	public Level getLevel() {
		return lvl;
	}

	@Override
	public int getLevelDepth() {
		return depth;
	}

	@Override
	public Collection getMembers() {
		return mbs.values();
	}

	@Override
	public Vector<OLAPMember> getMembersVector() {
		return new Vector<OLAPMember>(mbs.values());
	}

	@Override
	public String[] getPropertiesName() {
		return propertiesNames;
	}

	@Override
	public String[] getPropertiesValue() {
		return propertiesValues;
	}

	@Override
	public boolean hasProperties() {
		if(propertiesNames != null && propertiesNames.length > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isSynchro() {
		return sync;
	}

	@Override
	public void setSynchro(boolean sync) {
		this.sync = sync;
	}

	@Override
	public void setLevel(Level lvl) {
		this.lvl = lvl;
		if (depth <= 0 && getHiera() != null){
			this.depth = getHiera().getLevel().indexOf(lvl);
		}
		
		
	}
	
	public void setHierarchy(Hierarchy h){
		this.hiera = h;
		this.hieraName = h.getUniqueName();
	}

	@Override
	public boolean equals(Object obj) {
		
		return super.equals(obj);
	}
	
	protected OLAPMember findMember(String uname, String utdSchemaId, String cubeName, IRuntimeContext runtimeContext) throws Exception {
		if (uname.equalsIgnoreCase(this.getUniqueName())) {
			return this;
		}
		else if (uname.startsWith(this.getUniqueName())) {
			//load sons if required
			synchronized (this) {
				if (!sync){
					try{
						List<Member> submems = UnitedOlapServiceProvider.getInstance().getModelService().getSubMembers(getUniqueName(), utdSchemaId, cubeName, runtimeContext);
						for(Member subMem : submems) {
							
							UnitedOlapMember sub = new UnitedOlapMember(subMem, getHiera());
							addMember(sub);
						}
						setSynchro(true);
					}catch(Exception ex){
						throw new Exception("Error when loading child for " + getUniqueName() +" - " + ex.getMessage(), ex);				}
					
				}
			}
			
			
			//one of my sons
			if (mbs.containsKey(uname)) {
				return mbs.get(uname);
			}
			//one of my descendents
			else {
				Iterator<OLAPMember> it = mbs.values().iterator();
				
				while (it.hasNext()) {
					OLAPMember mb = it.next();
					
					OLAPMember buf = mb.findMember(uname);
					
					if (buf != null) {
						return buf;
					}
				}
			}
		}
		
		return null;
	}

	@Override
	public int compareTo(Object o) {
		if (o == null){
			throw new RuntimeException(getClass().getName() + " instance cannot be compared with null");
		}
		if (!(o instanceof UnitedOlapMember)){
			throw new RuntimeException(getClass().getName() + " instance cannot be compared with " + o.getClass().getName());
		}
		return getUniqueName().compareTo(((UnitedOlapMember)o).getUniqueName());
	}
}
