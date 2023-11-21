package bpm.vanilla.portal.server.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

public class InputStreamDatasource implements DataSource{

	private InputStream stream;
	private String format;
	
	public InputStreamDatasource(InputStream stream, String format) {
		this.stream = stream;
		this.format = format;
	}
	
	@Override
	public String getContentType() {
		return format;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return stream;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return null;
	}

}
