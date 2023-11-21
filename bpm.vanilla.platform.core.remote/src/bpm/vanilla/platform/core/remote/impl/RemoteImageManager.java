package bpm.vanilla.platform.core.remote.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import bpm.vanilla.platform.core.IImageManager;
import bpm.vanilla.platform.core.beans.VanillaImage;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.utils.IOWriter;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteImageManager implements IImageManager {

	private HttpCommunicator httpCommunicator;
	private static XStream xstream;

	public RemoteImageManager(HttpCommunicator httpCommunicator) {
		this.httpCommunicator = httpCommunicator;
	}

	static {
		xstream = new XStream();
	}

	private XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}

	@Override
	public VanillaImage uploadImage(InputStream image, String name) throws Exception {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		IOWriter.write(image, bos, true, true);

		byte[] rawBytes = Base64.encodeBase64(bos.toByteArray());

		XmlAction op = new XmlAction(createArguments(rawBytes, name), IImageManager.ActionType.UPLOAD_IMAGE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (VanillaImage) xstream.fromXML(xml);
	}

	@Override
	public InputStream downloadImage(VanillaImage image) throws Exception {
		XmlAction op = new XmlAction(createArguments(image), IImageManager.ActionType.UPLOAD_IMAGE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		Object result = xml;
		return (ByteArrayInputStream) result;
	}

	@Override
	public VanillaImage getImage(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IImageManager.ActionType.GET_IMAGE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (VanillaImage) xstream.fromXML(xml);
	}

	@Override
	public List<VanillaImage> getImageList(String filter) throws Exception {
		XmlAction op = new XmlAction(createArguments(filter), IImageManager.ActionType.GET_IMAGE_LIST);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<VanillaImage>) xstream.fromXML(xml);
	}

	@Override
	public VanillaImage getImageByName(String name) throws Exception {
		XmlAction op = new XmlAction(createArguments(name), IImageManager.ActionType.GET_IMAGE_BY_NAME);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (VanillaImage) xstream.fromXML(xml);
	}
}
