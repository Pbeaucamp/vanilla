package bpm.gwt.commons.client.tree;

import bpm.gwt.commons.client.images.CommonImages;
import bpm.gwt.commons.client.utils.FileFormat;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class RepositoryObjectWidget extends Composite implements DragStartHandler {

	private static RepositoryObjectWidgetUiBinder uiBinder = GWT.create(RepositoryObjectWidgetUiBinder.class);

	interface RepositoryObjectWidgetUiBinder extends UiBinder<Widget, RepositoryObjectWidget> {
	}

	interface MyStyle extends CssResource {
		String imgObject();
	}

	@UiField
	FocusPanel dragPanel;

	@UiField
	Image imgObject;

	@UiField
	Label lblObject;

	@UiField
	MyStyle style;

	private IRepositoryObject repositoryObject;

	public RepositoryObjectWidget(IRepositoryObject repositoryObject, boolean dragAndDrop) {
		initWidget(uiBinder.createAndBindUi(this));
		this.repositoryObject = repositoryObject;

		imgObject.setResource(findResource(repositoryObject));
		imgObject.addStyleName(style.imgObject());

		lblObject.setText(repositoryObject.getName());

		if (repositoryObject instanceof RepositoryItem && dragAndDrop) {
			getElement().setDraggable(Element.DRAGGABLE_TRUE);
			dragPanel.addDragStartHandler(this);
		}
	}

	public IRepositoryObject getRepositoryObject() {
		return repositoryObject;
	}

	private ImageResource findResource(IRepositoryObject repositoryObject) {
		if (repositoryObject instanceof RepositoryDirectory) {
			return CommonImages.INSTANCE.folder();
		}
		else if (repositoryObject instanceof RepositoryItem) {
			RepositoryItem item = (RepositoryItem) repositoryObject;

			int type = item.getType();
			int subType = item.getSubtype();

			if (type == IRepositoryApi.FASD_TYPE) {
				return CommonImages.INSTANCE.fasd();
			}
			else if (type == IRepositoryApi.FAV_TYPE) {
				return CommonImages.INSTANCE.view();
			}
			else if (type == IRepositoryApi.FD_TYPE) {
				return CommonImages.INSTANCE.fd();
			}
			else if (type == IRepositoryApi.FWR_TYPE) {
				return CommonImages.INSTANCE.Reports();
			}
			else if (type == IRepositoryApi.MAP_TYPE) {
				return CommonImages.INSTANCE.md();
			}
			else if (type == IRepositoryApi.FMDT_TYPE) {
				return CommonImages.INSTANCE.fmdt();
			}
			else if (type == IRepositoryApi.FD_DICO_TYPE) {
				return CommonImages.INSTANCE.fd_Dico();
			}
			else if (type == IRepositoryApi.BIW_TYPE) {
				return CommonImages.INSTANCE.biw();
			}
			else if (type == IRepositoryApi.GTW_TYPE) {
				return CommonImages.INSTANCE.gateway_16();
			}
			else if (type == IRepositoryApi.EXTERNAL_DOCUMENT) {
				String modelType = item.getModelType();
				String format = "";
				if (modelType != null && modelType.contains(".")) {
					format = modelType.substring(modelType.lastIndexOf(".") + 1);
				}
				else if (modelType != null) {
					format = item.getModelType();
				}
				return findImageForExtDoc(format);
			}
			else if (type == IRepositoryApi.URL) {
				return CommonImages.INSTANCE.link();
			}
			else if (type == IRepositoryApi.GED_TYPE) {
				return CommonImages.INSTANCE.ged_temporary();
			}
			else if (type == IRepositoryApi.GED_ENTRY) {
				return CommonImages.INSTANCE.repository_ged_small();
			}
			else if (type == IRepositoryApi.CUST_TYPE) {
				if (subType == IRepositoryApi.XACTION_SUBTYPE) {
					return CommonImages.INSTANCE.xaction();
				}
				else if (subType == IRepositoryApi.JASPER_REPORT_SUBTYPE) {
					return CommonImages.INSTANCE.jrxml();
				}
				else if (subType == IRepositoryApi.BIRT_REPORT_SUBTYPE) {
					return CommonImages.INSTANCE.birt16();

				}
				else if (subType == IRepositoryApi.ORBEON_XFORMS) {
					return CommonImages.INSTANCE.orbeon_16();
				}
			}
			else if (type == IRepositoryApi.REPORTS_GROUP) {
				return CommonImages.INSTANCE.reports_group_16();
			}
			else if (type == IRepositoryApi.KPI_THEME) {
				return CommonImages.INSTANCE.kpi_theme_16();
			}
			else if (type == IRepositoryApi.KPI_MAP) {
				return CommonImages.INSTANCE.ic_map_black_18dp();
			}
			else if (type == IRepositoryApi.FORM) {
				return CommonImages.INSTANCE.lk_wb_report_64();
			}
		}

		return CommonImages.INSTANCE.object();
	}

	private ImageResource findImageForExtDoc(String format) {
		if (format.equalsIgnoreCase(FileFormat.PDF)) {
			return CommonImages.INSTANCE.format_pdf_16();
		}
		else if (format.equalsIgnoreCase(FileFormat.XLS) || format.equalsIgnoreCase(FileFormat.XLSX)) {
			return CommonImages.INSTANCE.format_xls_16();
		}
		else if (format.equalsIgnoreCase(FileFormat.DOC) || format.equalsIgnoreCase(FileFormat.DOCX)) {
			return CommonImages.INSTANCE.format_doc_16();
		}
		else if (format.equalsIgnoreCase(FileFormat.PPT) || format.equalsIgnoreCase(FileFormat.PPTX)) {
			return CommonImages.INSTANCE.format_ppt_16();
		}
		else if (format.equalsIgnoreCase(FileFormat.XML)) {
			return CommonImages.INSTANCE.format_xml_16();
		}
		else if (format.equalsIgnoreCase(FileFormat.TXT)) {
			return CommonImages.INSTANCE.format_txt_16();
		}
		else if (format.equalsIgnoreCase(FileFormat.HTML)) {
			return CommonImages.INSTANCE.format_html_16();
		}
		else if (format.equalsIgnoreCase(FileFormat.ODT)) {
			return CommonImages.INSTANCE.format_odt_16();
		}
		else if (format.equalsIgnoreCase(FileFormat.ODS)) {
			return CommonImages.INSTANCE.format_ods_16();
		}
		else if (format.equalsIgnoreCase(FileFormat.ZIP) || format.equalsIgnoreCase(FileFormat.TAR_GZ) || format.equalsIgnoreCase(FileFormat.TAR) || format.equalsIgnoreCase(FileFormat.GZ) || format.equalsIgnoreCase(FileFormat.JAR) || format.equalsIgnoreCase(FileFormat.RAR)) {
			return CommonImages.INSTANCE.format_zip_16();
		}
		else if (format.equalsIgnoreCase(FileFormat.JPG) || format.equalsIgnoreCase(FileFormat.JPEG) || format.equalsIgnoreCase(FileFormat.GIF) || format.equalsIgnoreCase(FileFormat.PNG) || format.equalsIgnoreCase(FileFormat.SVG) || format.equalsIgnoreCase(FileFormat.BMP)) {
			return CommonImages.INSTANCE.format_img_16();
		}
		else if (format.equalsIgnoreCase(FileFormat.MPG) || format.equalsIgnoreCase(FileFormat.MPEG) || format.equalsIgnoreCase(FileFormat.AVI) || format.equalsIgnoreCase(FileFormat.MP4) || format.equalsIgnoreCase(FileFormat.WMW) || format.equalsIgnoreCase(FileFormat.MOV) || format.equalsIgnoreCase(FileFormat.MKV) || format.equalsIgnoreCase(FileFormat.MLV)) {
			return CommonImages.INSTANCE.format_video_16();
		}
		else if (format.equalsIgnoreCase(FileFormat.MP3) || format.equalsIgnoreCase(FileFormat.WAV) || format.equalsIgnoreCase(FileFormat.FLAC) || format.equalsIgnoreCase(FileFormat.WMA) || format.equalsIgnoreCase(FileFormat.M4A)) {
			return CommonImages.INSTANCE.format_music_16();
		}

		return CommonImages.INSTANCE.doc();
	}

	@Override
	public void onDragStart(DragStartEvent event) {
		RepositoryItem item = (RepositoryItem) repositoryObject;

		event.setData("id", String.valueOf(item.getId()));
		event.setData("name", item.getName());
		event.getDataTransfer().setDragImage(getElement(), 10, 10);
	}

}
