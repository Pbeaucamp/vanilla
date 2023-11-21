package bpm.vanillahub.runtime.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;

import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanillahub.runtime.i18N.Labels;

import com.thoughtworks.xstream.XStream;

public abstract class ResourceManager<T extends Resource> {

	private String resourceFilePath;
	private String typeResource;

	public ResourceManager(String filePath, String resourceFileName, String typeResource) {
		this.resourceFilePath = filePath.endsWith("/") ? filePath + resourceFileName : filePath + "/" + resourceFileName;
		this.typeResource = typeResource;
	}

	public T addResource(Locale locale, T resource) throws Exception {
		List<T> resources = getResources(locale);
		if ((resource.getId() > 0 && getResource(resources, resource.getId()) == null) || getResource(resources, resource.getName()) == null) {
			int newResourceId = buildId(resources);
			resource.setId(newResourceId);
			
			manageResourceForAdd(resource);

			resources.add(resource);
			writeResources(locale, resources);

			return resource;
		}
		else {
			throw new Exception("'" + resource.getName() + "' " + Labels.getLabel(locale, Labels.AlreadyExist));
		}
	}

	private int buildId(List<T> resources) {
		if (!resources.isEmpty()) {
			int resourceId = 1;
			for (T resource : resources) {
				if (resource.getId() > resourceId) {
					resourceId = resource.getId();
				}
			}

			return resourceId + 1;
		}
		return 1;
	}

	public void removeResource(Locale locale, T resource) throws Exception {
		List<T> resources = getResources(locale);
		T resourceTmp = getResource(resources, resource.getId());
		if (resourceTmp != null) {
			resources.remove(resourceTmp);

			writeResources(locale, resources);
		}
		else {
			throw new Exception("'" + resource.getName() + "' " + Labels.getLabel(locale, Labels.NotExistCannotBeDelete));
		}
	}

	public T modifyResource(Locale locale, T resource) throws Exception {
		List<T> resources = getResources(locale);
		T resourceTmp = getResource(resources, resource.getId());
		if (resourceTmp != null) {
			manageResourceForModification(resourceTmp, resource);
			
			writeResources(locale, resources);

			return resourceTmp;
		}
		else {
			throw new Exception("'" + resource.getName() + "' " + Labels.getLabel(locale, Labels.NotExist));
		}
	}

	private T getResource(List<T> resources, int resourceId) {
		for (T resource : resources) {
			if (resource.getId() == resourceId) {
				return resource;
			}
		}

		return null;
	}

	private T getResource(List<T> resources, String name) {
		for (T resource : resources) {
			if (resource.getName().equals(name)) {
				return resource;
			}
		}

		return null;
	}
	
	public T getResource(Locale locale, int resourceId) throws Exception {
		List<T> resources = getResources(locale);
		return getResource(resources, resourceId);
	}

	@SuppressWarnings("unchecked")
	public synchronized List<T> getResources(Locale locale) throws Exception {
		File file = new File(resourceFilePath);
		if (!file.exists()) {
			try {
				if (!file.createNewFile()) {
					throw new Exception("'" + typeResource + "' " + Labels.getLabel(locale, Labels.CannotBeCreated));
				}
				else {
					writeResources(locale, new ArrayList<T>());
					return new ArrayList<T>();
				}
			} catch (IOException e) {
				e.printStackTrace();
				throw new Exception("'" + typeResource + "' " + Labels.getLabel(locale, Labels.CannotBeCreated) + " : " + e.getMessage());
			}
		}

		try (FileInputStream fis = new FileInputStream(new File(resourceFilePath))) {
			String resourceXml = IOUtils.toString(fis, "UTF-8");
			if (resourceXml.isEmpty()) {
				writeResources(locale, new ArrayList<T>());
			}

			fis.close();

			XStream xStream = new XStream();
			return (List<T>) xStream.fromXML(resourceXml);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(Labels.getLabel(locale, Labels.ErrorDuringFileReading) + " '" + typeResource + "' : " + e.getMessage());
		}
	}

	private synchronized void writeResources(Locale locale, List<T> resources) throws Exception {
		File file = new File(resourceFilePath);
		if (!file.exists()) {
			try {
				if (!file.createNewFile()) {
					throw new Exception("'" + typeResource + "' " + Labels.getLabel(locale, Labels.CannotBeCreated));
				}
			} catch (IOException e) {
				e.printStackTrace();
				throw new Exception("'" + typeResource + "' " + Labels.getLabel(locale, Labels.CannotBeCreated) + " : " + e.getMessage());
			}
		}

		XStream xStream = new XStream();
		try (FileOutputStream out = new FileOutputStream(new File(resourceFilePath)); OutputStreamWriter writer = new OutputStreamWriter(out, Charset.forName("UTF-8"));) {
			xStream.toXML(resources, writer);

			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(Labels.getLabel(locale, Labels.ErrorDuringFileWriting) + " '" + typeResource + "' : " + e.getMessage());
		}
	}
	
	protected abstract void manageResourceForAdd(T resource);
	
	protected abstract void manageResourceForModification(T newResource, T oldResource);
}
