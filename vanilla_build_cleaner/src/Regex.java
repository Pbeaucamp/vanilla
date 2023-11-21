
public class Regex {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String s = "org.apache.commons.el_1.0.0.v200806031608.jar@start,com.springsource.org.apache.commons.logging-1.1.1.jar@start";
		
		
		String regex = "_.*jar@start,";
		
		for(String k : s.split(regex)){
			System.out.println(k);
		}
		

	}

}
