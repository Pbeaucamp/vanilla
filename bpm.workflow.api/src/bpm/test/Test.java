package bpm.test;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpm.vanilla.platform.core.utils.MD5Helper;

public class Test {

	public static void main(String[] args) throws Exception {

		// String[] s = {"50B5E1",
		// "91DAFA",
		// "CEEBFD",
		// "E8FE49",
		// "A2C728",
		// "EA46B2",
		// "00AEEF",
		// "70B1DB",
		// "A1B80E",
		// "88B115",
		// "DBBA0B",
		// "B90109",
		// "015EAC",
		// "C42D7A",
		// "00A6DA",
		// "C8870F",
		// "C69200",
		// "D7AE14",
		// "0193CE",
		// "BE2671",
		// "C99100",
		// "7D4B95",
		// "9F65B5",
		// "C8E6E6",
		// "D6ECFF",};
		//
		// StringBuilder buf = new StringBuilder();
		//
		// for(String a : s) {
		// buf.append("<color>"+a+"</color>\n");
		// }
		//
		// System.out.println(buf.toString());

		// File file = new File("C:\\BPM\\testbuildgwt\\bpm.workflow.api\\src\\bpm\\test\\build.properties");
		// FileInputStream fis = null;
		// BufferedInputStream bis = null;
		// DataInputStream dis = null;
		//
		//
		// fis = new FileInputStream(file);
		//
		// // Here BufferedInputStream is added for fast reading.
		// bis = new BufferedInputStream(fis);
		// dis = new DataInputStream(bis);
		//
		// // dis.available() returns 0 if the file does not have more lines.
		// while (dis.available() != 0) {
		//
		// // this statement reads the line from the file and print it to
		// // the console.
		// String l = dis.readLine();
		//
		// String[] ll = l.split("=");
		//
		// if(ll.length == 2) {
		//
		// System.out.println("<property name=\"" + ll[0] + "\" value=\"" + ll[1] + "\"/>");
		// }
		// else {
		// System.out.println("<property name=\"" + ll[0] + "\" value=\"\"/>");
		// }
		//
		//
		// }
		//
		// // dispose all the resources after using them.
		// fis.close();
		// bis.close();
		// dis.close();
		//
		// re();

//		List<String> key = Arrays.asList("demrru", "ist", "ehllo", "aemnsy", "no");
//		List<String> list1 = Arrays.asList("MURDER!","It's", "Hello","Yes-Man", "ON");
//		List<Integer> list2 = Arrays.asList(2, 4, 3, 5, 1);            // Also use Integer type 
//		List<Double>  list3 = Arrays.asList(0.1, 0.4, 0.3, 0.2, 0.5);  // or Double type
//
//		// Sort all lists (excluding the key)
//		keySort(key, key, list1, list2, list3);
//
//		// Sort all lists (including the key)
//		keySort(key, key, list1, list2, list3);
		
//		new FileInputStream("file:///C:/BPM/test/config/thermolabo.properties".replace("file:///", ""));
		System.out.println(MD5Helper.encode("no password"));
//		int i = 1000 * 60 * 60 * 24 * 35;
//		long j = (long)1000 * (long)60 * (long)60 * (long)24 * 35L;
//		
//		long t = new Date().getTime();
//		
//		System.out.println(i);
//		System.out.println(j);
//		System.out.println(t);
//		System.out.println(t - j);
	}

	public static <T extends Comparable<T>> void keySort(final List<T> key, List<?>... lists) {
		// Create a List of indices
		List<Integer> indices = new ArrayList<Integer>();
		for(int i = 0; i < key.size(); i++)
			indices.add(i);

		// Sort the indices list based on the key
		Collections.sort(indices, new Comparator<Integer>() {
			@Override
			public int compare(Integer i, Integer j) {
				return key.get(i).compareTo(key.get(j));
			}
		});

		// Create a mapping that allows sorting of the List by N swaps.
		Map<Integer, Integer> swapMap = new HashMap<Integer, Integer>(indices.size());

		// Only swaps can be used b/c we cannot create a new List of type <?>
		for(int i = 0; i < indices.size(); i++) {
			int k = indices.get(i);
			while(swapMap.containsKey(k))
				k = swapMap.get(k);

			swapMap.put(i, k);
		}

		// for each list, swap elements to sort according to key list
		for(Map.Entry<Integer, Integer> e : swapMap.entrySet())
			for(List<?> list : lists)
				Collections.swap(list, e.getKey(), e.getValue());
	}

	private static void re() throws Exception {
		File file = new File("C:\\BPM\\testbuildgwt\\bpm.workflow.api\\src\\bpm\\test\\build.properties");
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;

		fis = new FileInputStream(file);

		// Here BufferedInputStream is added for fast reading.
		bis = new BufferedInputStream(fis);
		dis = new DataInputStream(bis);

		// dis.available() returns 0 if the file does not have more lines.
		while(dis.available() != 0) {

			// this statement reads the line from the file and print it to
			// the console.
			String l = dis.readLine();

			String[] ll = l.split("=");

			if(ll.length == 2) {

				System.out.println("<echo file=\"build.properties\" append=\"true\">" + ll[0] + "=" + ll[1] + "\\n</echo>");
			}
			else {
				System.out.println("<echo file=\"build.properties\" append=\"true\">" + ll[0] + "=\\n</echo>");
			}

		}

		// dispose all the resources after using them.
		fis.close();
		bis.close();
		dis.close();
	}
}