package bpm.faweb.shared.infoscube;

import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ItemView implements IsSerializable {
	private String name;
	private String key;
	private int id;
	private String imagePath;
	private String leftImagePath;
	private String rightImagePath;
	private String xml;
	private boolean isSnapshot;
	private int directoryItemId;

	/**
	 * This field is a Set that must always contain Strings.
	 * 
	 * @gwt.typeArgs <java.lang.String>
	 */
	public Set setOfStrings;

	/**
	 * This field is a Map that must always contain Strings as its keys and
	 * values.
	 * 
	 * @gwt.typeArgs <java.lang.String,java.lang.String>
	 */
	public Map mapOfStringToString;

	/**
	 * Default Constructor. The Default Constructor's explicit declaration is
	 * required for a serializable class.
	 */
	public ItemView() {
	}

	public ItemView(String name, String k, int id, String imagePath, String leftImagePath, String rightImagePath) {
		this.name = name;
		this.key = k;
		this.setId(id);
		this.imagePath = imagePath;
		this.leftImagePath = leftImagePath;
		this.rightImagePath = rightImagePath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String k) {
		this.key = k;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getLeftImagePath() {
		return leftImagePath;
	}

	public void setLeftImagePath(String leftImagePath) {
		this.leftImagePath = leftImagePath;
	}

	public String getRightImagePath() {
		return rightImagePath;
	}

	public void setRightImagePath(String rightImagePath) {
		this.rightImagePath = rightImagePath;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getXml() {
		return xml;
	}

	public void setSnapshot(boolean isSnapshot) {
		this.isSnapshot = isSnapshot;
	}

	public boolean isSnapshot() {
		return isSnapshot;
	}

	public void setDirectoryItemId(int directoryItemId) {
		this.directoryItemId = directoryItemId;
	}

	public int getDirectoryItemId() {
		return directoryItemId;
	}

	@Override
	public String toString() {
		return name != null && !name.isEmpty() ? name : "Unknown";
	}
}
