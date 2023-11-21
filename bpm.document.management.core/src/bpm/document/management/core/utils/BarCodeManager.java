package bpm.document.management.core.utils;

public class BarCodeManager {
	public static enum FORMAT{
		NONE(""),
		QR_CODE("QR_CODE"),
		DATA_MATRIX("DATA_MATRIX");
		  
		private String name = "";
		   
		  //Constructeur
		FORMAT(String name){
			this.name = name;
		}
		   
		public String toString(){
			return name;
		}
		
		  public static FORMAT fromString(String text) {
			    if (text != null) {
			      for (FORMAT f : FORMAT.values()) {
			        if (text.equalsIgnoreCase(f.name)) {
			          return f;
			        }
			      }
			    }
			    return null;
			  }
	};
}
