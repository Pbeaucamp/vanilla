package bpm.test;


public class RemoteTest {
	
	public static void main(String[] args) {
		System.out.println(System.getProperty("user.dir"));
		try { 
			String[] a = { "cmd.exe", "/C", "test.bat d:\\Licence.txt" };
			Runtime r = Runtime.getRuntime(); 
			Process p = r.exec(a);
			

		}catch(Exception e) { 
			e.printStackTrace();
		} 
	}
	
}