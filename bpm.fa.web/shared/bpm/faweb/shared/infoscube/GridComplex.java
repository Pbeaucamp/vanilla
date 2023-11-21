package bpm.faweb.shared.infoscube;

import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GridComplex extends GridCube implements IsSerializable {
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
	  
	  private Integer[] firstNotNull;

	  /**
	   * Default Constructor. The Default Constructor's explicit declaration
	   * is required for a serializable class.
	   */  
	  public GridComplex(){		  
		  super();
	  }

	public Integer[] getFirstNotNull() {
		return firstNotNull;
	}

	public void setFirstNotNull(Integer[] firstNotNull) {
		this.firstNotNull = firstNotNull;
	}
	  

		

	  

}
