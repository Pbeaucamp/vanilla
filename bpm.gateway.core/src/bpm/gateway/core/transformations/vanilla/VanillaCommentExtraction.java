package bpm.gateway.core.transformations.vanilla;

import java.util.Date;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.vanilla.RunVanillaCommentExtraction;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.repository.Comment;

public class VanillaCommentExtraction extends AbstractTransformation {

	private Integer itemId;
	private Integer type = Comment.ITEM;

	private DefaultStreamDescriptor descriptor;

	public VanillaCommentExtraction() {
		addPropertyChangeListener(this);
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("vanillacommentextraction");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);

		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);

		e.addElement("type").setText(type + "");
		e.addElement("itemId").setText(itemId + "");

		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		return e;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunVanillaCommentExtraction(repositoryCtx, this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {

		descriptor = new DefaultStreamDescriptor();

		StreamElement element = new StreamElement();
		element.name = "Comment";
		element.originTransfo = this.getName();
		element.className = String.class.getName();
		element.transfoName = getName();
		descriptor.addColumn(element);

		element = new StreamElement();
		element.name = "Date";
		element.originTransfo = this.getName();
		element.className = Date.class.getName();
		element.transfoName = getName();
		descriptor.addColumn(element);

		element = new StreamElement();
		element.name = "UserId";
		element.originTransfo = this.getName();
		element.className = Integer.class.getName();
		element.transfoName = getName();
		descriptor.addColumn(element);

		element = new StreamElement();
		element.name = "ParentId";
		element.originTransfo = this.getName();
		element.className = Integer.class.getName();
		element.transfoName = getName();
		descriptor.addColumn(element);

		element = new StreamElement();
		element.name = "ObjectId";
		element.originTransfo = this.getName();
		element.className = Integer.class.getName();
		element.transfoName = getName();
		descriptor.addColumn(element);

		for (Transformation t : getOutputs()) {
			t.refreshDescriptor();
		}
	}

	@Override
	public Transformation copy() {
		VanillaCommentExtraction extr = new VanillaCommentExtraction();

		extr.setDescription(description);
		extr.setName("copy of " + name);
		extr.setType(type);
		extr.setItemId(itemId);

		return extr;
	}

	@Override
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("Comment type : " + type + "\n");
		buf.append("Item id : " + itemId + "\n");
		return buf.toString();
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public void setType(String type) {
		this.type = Integer.parseInt(type);
	}

	public Integer getType() {
		return type;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = Integer.parseInt(itemId);
	}

	public Integer getItemId() {
		return itemId;
	}

}
