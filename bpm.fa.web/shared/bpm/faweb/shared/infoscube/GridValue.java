package bpm.faweb.shared.infoscube;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GridValue extends GridCube implements IsSerializable {
	/**
	   * This field is a Set that must always contain Strings.
	   * 
	   * @gwt.typeArgs <java.lang.String>
	   */
	  public Set setOfStrings;

	  /**
	   * This field is a Map that must always contain Strings as its keys and
	   * values.
	   * 
	   * @gwt.typeArgs <java.lang.String,java.lang.String>
	   */
	  public Map mapOfStringToString;
	  
	  private  List items;

	  /**
	   * Default Constructor. The Default Constructor's explicit declaration
	   * is required for a serializable class.
	   */  
	  public GridValue(){		  
		  super();
	  }
	  
	  public void addItem(int i, int j, double d){
			Object o = new Double(d);
			((List) items.get(i)).add(j, o);
		}
	  
	  public double getItem(int i, int j){
		return Double.parseDouble(((ItemCube) this.getLigne(i).get(j)).getLabel());
	  }
	  
}
