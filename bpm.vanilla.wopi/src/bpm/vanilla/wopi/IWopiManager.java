package bpm.vanilla.wopi;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.ws.rs.core.StreamingOutput;

public interface IWopiManager {

	public WopiFileInfo getFileInfo(String fileId);

	public StreamingOutput getFile(String fileId);

	public void putFile(String fileId, InputStream is);
	
	public ByteArrayOutputStream getFullFile(String fileId);
}
