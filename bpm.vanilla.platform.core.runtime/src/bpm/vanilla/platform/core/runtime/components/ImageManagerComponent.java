package bpm.vanilla.platform.core.runtime.components;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.osgi.service.component.ComponentContext;

import bpm.vanilla.platform.core.IImageManager;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.VanillaImage;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.utils.IOWriter;

public class ImageManagerComponent extends AbstractVanillaManager implements IImageManager {

	private IVanillaContext rootVanillaCtx;
	private String imageFolder;

	public void activate(ComponentContext ctx) {
		try {
			super.activate(ctx);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void init() throws Exception {
		this.rootVanillaCtx = getRootVanillaContext();
		this.imageFolder = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_IMAGE_FOLDER);
		if(imageFolder == null) {
			this.imageFolder = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_FILES) + "vanilla_images/";
		}
		if(!imageFolder.endsWith("/")) {
			imageFolder += "/";
		}
		File imageFile = new File(imageFolder);
		if(!imageFile.exists()) {
			imageFile.mkdirs();
		}
	}

	@Override
	public String getComponentName() {
		return getClass().getName();
	}
	
	private IVanillaAPI getRootVanillaApi() {
		return new RemoteVanillaPlatform(rootVanillaCtx);
	}

	private IRepositoryApi getRootRepositoryApi(int repId) throws Exception {
		return new RemoteRepositoryApi(getRootRepositoryContext(repId, null));
	}

	private IRepositoryContext getRootRepositoryContext(int repId, Integer groupId) {
		Group dummyGroup = new Group();
		dummyGroup.setId(groupId != null ? groupId : -1);

		Repository rep = new Repository();
		rep.setId(repId);

		return new BaseRepositoryContext(rootVanillaCtx, dummyGroup, rep);
	}

	private IVanillaContext getRootVanillaContext() {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String vanillaUrl = config.getVanillaServerUrl();
		String login = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String password = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);

		return new BaseVanillaContext(vanillaUrl, login, password);
	}

	@Override
	public VanillaImage uploadImage(InputStream image, String name) throws Exception {
		
		File imageFile = new File(imageFolder);
		int id = imageFile.listFiles().length;
		
		File resultFile = new File(imageFolder + id + "_" + name);
		FileOutputStream fileOutputStream = new FileOutputStream(resultFile);
		IOWriter.write(image, fileOutputStream, true, true);
		return createImage(resultFile);
	}

	@Override
	public InputStream downloadImage(VanillaImage image) throws Exception {
		String filePath = imageFolder + image.getFileName();
		File imageFile = new File(filePath);
		return new ByteArrayInputStream(IOUtils.toByteArray(new FileInputStream(imageFile)));
	}

	@Override
	public VanillaImage getImage(final int id) throws Exception {
		File imageFile = new File(imageFolder);
		for(File file : imageFile.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith(id + "_");
			}
		})) {
			return createImage(file);
		}
		throw new Exception("No VanillaImage with id = " + id);
	}

	@Override
	public List<VanillaImage> getImageList(String filter) throws Exception {
		List<VanillaImage> images = new ArrayList<VanillaImage>();
		File imageFile = new File(imageFolder);
		for(File file : imageFile.listFiles()) {
			if(filter == null || filter.isEmpty() || file.getName().contains(filter)) {
				images.add(createImage(file));
			}
		}
		return images;
	}

	private VanillaImage createImage(File file) {
		VanillaImage image = new VanillaImage();
		
		String filename = file.getName();
		
		int id = Integer.parseInt(filename.substring(0, filename.indexOf("_")));
		String name = filename.substring(filename.indexOf("_") + 1, filename.lastIndexOf("."));
		String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
		
		image.setExtension(extension);
		image.setId(id);
		image.setName(name);
		image.setSize(0);
		image.setUrl(imageFolder + filename);
		image.setFileName(filename);
		
		return image;
	}

	@Override
	public VanillaImage getImageByName(final String fileName) throws Exception {
		File imageFile = new File(imageFolder);
		for(File file : imageFile.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				
				if(name.equals(fileName) || name.substring(name.indexOf("_"), name.length()).startsWith(fileName + ".")) {
					return true;
				}
				return false;
			}
		})) {
			return createImage(file);
		}
		throw new Exception("No VanillaImage with name = " + fileName);
	}

}
