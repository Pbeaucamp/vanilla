package bpm.vanillahub.core.beans.managers;

import java.io.ByteArrayInputStream;

public interface TransformManager {
	
	public ByteArrayInputStream buildFile(ByteArrayInputStream parentStream) throws Exception;
}
