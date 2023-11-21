package bpm.update.manager.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import com.thoughtworks.xstream.XStream;

import bpm.update.manager.api.beans.Update;
import bpm.update.manager.api.utils.Constants;

public class MainDescriptorCreationHelper {

	private static final String FILE_PATH = "C:/BPM/BUILD/Patch/";

	public static void main(String[] args) {
		Update update = new Update(buildDescription(), buildProperties(), buildScripts());

		File f = new File(FILE_PATH + Constants.DESCRIPTOR);
		try (OutputStreamWriter fos = new OutputStreamWriter(new FileOutputStream(f), "UTF-8")) {
			new XStream().toXML(update, fos);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String buildDescription() {
		StringBuffer buf = new StringBuffer();
		buf.append("This update fix the problem for Vanilla Metadata with Pg and H2\n");
		return buf.toString();
	}

	private static String buildProperties() {
		StringBuffer buf = new StringBuffer();
//		buf.append("(Add)\n");
//		buf.append("bpm.test.platform.core=yoyo\n");
//		buf.append("(Remove)\n");
//		buf.append("bpm.test.platform.core2=yoyo");
		return buf.toString();
	}

	private static String buildScripts() {
		StringBuffer buf = new StringBuffer();
//		buf.append("INSERT blabla INTO blabla\n");
//		buf.append("INSERT blabla2 INTO blabla2\n");
		return buf.toString();
	}
}
