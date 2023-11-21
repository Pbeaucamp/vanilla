package bpm.vanilla.platform.core;

import java.io.InputStream;
import java.util.List;

import bpm.vanilla.platform.core.beans.VanillaImage;
import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

public interface IImageManager {

	public static enum ActionType implements IXmlActionType {
		UPLOAD_IMAGE(Level.INFO), DOWNLOAD_IMAGE(Level.DEBUG), GET_IMAGE(Level.DEBUG), GET_IMAGE_LIST(Level.DEBUG), GET_IMAGE_BY_NAME(Level.DEBUG);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}
	
	/**
	 * Update an image to the vanilla image folder
	 * @param image
	 * @return The image url
	 * @throws Exception
	 */
	public VanillaImage uploadImage(InputStream image, String name) throws Exception;
	
	public InputStream downloadImage(VanillaImage image) throws Exception;
	
	public VanillaImage getImage(int id) throws Exception;
	
	/**
	 * Get the image list
	 * @param filter : A String to filter images, can be null
	 * @return
	 * @throws Exception
	 */
	public List<VanillaImage> getImageList(String filter) throws Exception;
	
	public VanillaImage getImageByName(String name) throws Exception;
	
}
