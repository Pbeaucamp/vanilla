/*
 * Copyright 2009 Fred Sauer
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package bpm.faweb.client.gwt.dnd.client.util.impl;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

/*
 * {@link com.allen_sauer.gwt.dnd.client.util.DOMUtil} default cross-browser implementation.
 */
public class DOMUtilImpl {

  // CHECKSTYLE_JAVADOC_OFF

	  public String adjustTitleForBrowser(String message) {
	    return message.replaceAll("\r\n|\r|\n", " / ");
	  }

	  public native void cancelAllDocumentSelections()
	  /*-{
	    try {
	      $wnd.getSelection().removeAllRanges();
	    } catch(e) { throw new Error("unselect exception:\n" + e); }
	  }-*/;

	  public native int getBorderLeft(Element elem)
	  /*-{
	    try {
	      var computedStyle = $doc.defaultView.getComputedStyle(elem, null);
	      var borderLeftWidth = computedStyle.getPropertyValue("border-left-width");
	      return borderLeftWidth.indexOf("px") == -1 ? 0 : parseInt(borderLeftWidth.substr(0, borderLeftWidth.length - 2));
	    } catch(e) { throw new Error("getBorderLeft exception:\n" + e); }
	  }-*/;

	  public native int getBorderTop(Element elem)
	  /*-{
	    try {
	      var computedStyle = $doc.defaultView.getComputedStyle(elem, null);
	      var borderTopWidth = computedStyle.getPropertyValue("border-top-width");
	      return borderTopWidth.indexOf("px") == -1 ? 0 : parseInt(borderTopWidth.substr(0, borderTopWidth.length - 2));
	    } catch(e) { throw new Error("getBorderTop: " + e); }
	  }-*/;

	  public native int getClientHeight(Element elem)
	  /*-{
	    try {
	      return elem.clientHeight;
	    } catch(e) { throw new Error("getClientHeight exception:\n" + e); }
	  }-*/;

	  public native int getClientWidth(Element elem)
	  /*-{
	    try {
	      return elem.clientWidth;
	    } catch(e) { throw new Error("getClientWidth exception:\n" + e); }
	  }-*/;

  /**
   * From http://code.google.com/p/doctype/wiki/ArticleComputedStyle
   */
  public native String getEffectiveStyle(Element elem, String style) /*-{
    return this.@bpm.faweb.client.gwt.dnd.client.util.impl.DOMUtilImpl::getComputedStyle(Lcom/google/gwt/dom/client/Element;Ljava/lang/String;)(elem,style)
        || (elem.currentStyle ? elem.currentStyle[style] : null)
        || elem.style[style];
  }-*/;

  public final int getHorizontalBorders(Widget widget) {
    return widget.getOffsetWidth() - getClientWidth(widget.getElement());
  }

  public final int getVerticalBorders(Widget widget) {
    return widget.getOffsetHeight() - getClientHeight(widget.getElement());
  }

  @SuppressWarnings("unused")
  private native String getComputedStyle(Element elem, String style) /*-{
    if ($doc.defaultView && $doc.defaultView.getComputedStyle) {
      var styles = $doc.defaultView.getComputedStyle(elem, "");
      if (styles) {
        return styles[style];
      }
    }

    return null;
  }-*/;

}
