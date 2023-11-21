package bpm.gwt.commons.client.viewer.widget;

import com.google.gwt.user.client.Element;

public interface ICanExpand {

	public boolean isExpend();

	public void setImgExpendLeft(int left);

	public void setExpend(boolean expand);

	public Element getElement();
}
