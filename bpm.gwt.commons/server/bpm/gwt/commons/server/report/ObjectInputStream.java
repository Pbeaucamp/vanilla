package bpm.gwt.commons.server.report;

import java.io.ByteArrayInputStream;
import java.util.HashMap;

public class ObjectInputStream {

	private HashMap<String, ByteArrayInputStream> streams;

	public ObjectInputStream() {
	}

	public void addStream(String format, ByteArrayInputStream stream) {
		if (streams == null) {
			streams = new HashMap<String, ByteArrayInputStream>();
		}

		streams.put(format, stream);
	}

	public ByteArrayInputStream getStream(String format) {
		return streams.get(format);
	}
}
