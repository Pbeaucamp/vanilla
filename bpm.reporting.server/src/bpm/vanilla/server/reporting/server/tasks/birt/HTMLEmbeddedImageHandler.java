package bpm.vanilla.server.reporting.server.tasks.birt;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.birt.report.engine.api.HTMLCompleteImageHandler;
import org.eclipse.birt.report.engine.api.IImage;


public class HTMLEmbeddedImageHandler extends HTMLCompleteImageHandler{

	/* (non-Javadoc)
	 * @see org.eclipse.birt.report.engine.api.HTMLCompleteImageHandler#handleImage(org.eclipse.birt.report.engine.api.IImage, java.lang.Object, java.lang.String, boolean)
	 */
	@Override
	protected String handleImage(IImage image, Object context, String prefix,
			boolean needMap) {
		Base64 base64  = new Base64();
		if(image.getImageData() != null){
			byte[] encoded = base64.encode(image.getImageData());
	
			image.getRenderOption().setOutputFormat("PNG");
			/* Remove the point in the image name */
		    String extension = image.getExtension() != null ? image.getExtension().substring(1, image.getExtension().length()) : "png";
		    StringBuffer sb = new StringBuffer("data:image/" + extension + ";base64,");
		    String stringEncoded = new String(encoded);
		    sb.append(stringEncoded);
		    return sb.toString();
		}
		else {
			return "";
		}
	}

}
