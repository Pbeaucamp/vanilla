package bpm.united.olap.api.result.impl;

public class EmptyResultCell extends ResultCellImpl {

	@Override
	public String getHtml() {
		return "<td class=\"gridItem gridItemElement\">&nbsp;</td>\n";
	}

	@Override
	public String getType() {
		return "empty";
	}

}
