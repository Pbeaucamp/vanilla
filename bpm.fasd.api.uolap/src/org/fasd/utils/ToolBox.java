/**
 * 
 */
package org.fasd.utils;

/**This class contains some usefull and global methods
 * @author Belgarde
 *
 */
public class ToolBox {

	/**This method allow to check if a given string is not null and not empty.
	 * @param p_strString2Test string to test value
	 * @return true if a given string is not null and not empty or false otherwise.
	 */
	public static boolean isValidString(String p_strString2Test){
		boolean succes = false;
		if(p_strString2Test != null && !p_strString2Test.trim().equalsIgnoreCase("")){
			succes = true;
		}
		return succes;
	}

	
//	public static void main(String[] args){
//		System.out.println(isValidString("fqz"));
//	}
	
}
