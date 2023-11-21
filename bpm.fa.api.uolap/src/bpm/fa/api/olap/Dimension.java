package bpm.fa.api.olap;

import java.util.Collection;
import java.util.HashMap;

import bpm.fa.api.utils.log.Log;

/**
 * OLAP Dimension
 * @author ereynard
 *
 */
public class Dimension extends Element {
	private HashMap<String, Hierarchy> hiera = new HashMap<String, Hierarchy>();
	private boolean isGeolocalisable;
	private boolean isDate;
	
	/**
	 * Default Constructor
	 * @param name
	 * @param uname
	 * @param caption
	 */
	public Dimension(String name, String uname, String caption) {
		super(name, uname, caption);
		
		Log.info("Loaded " + this.getClass().getName() + " uname = " + getUniqueName());
	}
	
	/**
	 * Add specified Hierarchy
	 * @param h {@link Hierarchy}
	 */
	public void addHierarchy(Hierarchy h) {
		hiera.put(h.getUniqueName(), h);
	}
	
	/**
	 * Get Hierarchy specified by it's unique name
	 * @param uname Unique Name of the Hierarchy
	 * @return {@link Hierarchy}
	 */
	public Hierarchy getHierarchy(String uname) {
		return hiera.get(uname);
	}
	
	/**
	 * Get All Hierarchies
	 * @return {@link Collection} and {@link Hierarchy}
	 */
	public Collection<Hierarchy> getHierarchies() {
		return hiera.values();
	}

//	@Override
//	public boolean equals(Object o) {
//		Dimension d = (Dimension) o;
//		
//		return this.getUniqueName().equalsIgnoreCase(d.getUniqueName());
//	}
	
	/**
	 * Public access to find a dimension Member
	 * @param mb {@link MondrianMember} query Member to find
	 * @return {@link MondrianMember} or null
	 */
	public OLAPMember findMember(OLAPMember mb) {
		return hiera.get(mb.getHierarchy()).findMember(mb);
	}

	public void setGeolocalisable(boolean isGeolocalisable) {
		this.isGeolocalisable = isGeolocalisable;
	}

	public boolean isGeolocalisable() {
		return isGeolocalisable;
	}

	
	public Dimension clone(){
		Dimension d = new Dimension(getName(), getUniqueName(), getCaption());
		d.setGeolocalisable(isGeolocalisable());
		d.setDate(isDate());
		for(Hierarchy h : getHierarchies()){
			d.addHierarchy(h.clone());
		}
		
		return d;
	}

	public void setDate(boolean isDate) {
		this.isDate = isDate;
	}

	public boolean isDate() {
		return isDate;
	}
	
}
