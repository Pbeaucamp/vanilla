package bpm.vanilla.portal.client.utils;

import java.util.List;

import bpm.gwt.commons.client.images.CommonImages;
import bpm.gwt.commons.client.utils.FileFormat;
import bpm.gwt.commons.shared.repository.PortailRepositoryDirectory;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.portal.client.I18N.LabelsConstants;
import bpm.vanilla.portal.client.images.PortalImage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Belgarde
 * 
 */
public class ToolsGWT implements IsSerializable {

	public static LabelsConstants lblCnst = (LabelsConstants) GWT.create(LabelsConstants.class);

	/**
	 * This method aim to open a new page with the given url as target
	 * 
	 * @param url
	 *            the target
	 */
	public static native void doRedirect(String url)/*-{
		$wnd.open(url);
	}-*/;

	/**
	 * 
	 * This method allows to get the ImageResource from a Repository Object
	 * 
	 * @param object
	 *            the item to display
	 * @return the ImageResource associated to the object
	 */
	public static ImageResource getImageForObject(IRepositoryObject object, boolean big) {
		if (object instanceof PortailRepositoryDirectory) {
			if (big) {
				return PortalImage.INSTANCE.repository_folder();
			}
			else {
				return PortalImage.INSTANCE.folder();
			}
		}
		else if (object instanceof PortailRepositoryItem) {
			int type = ((PortailRepositoryItem) object).getType();
			int subType = ((PortailRepositoryItem) object).getSubType();

			if (type == IRepositoryApi.FASD_TYPE) {
				if (big) {
					return PortalImage.INSTANCE.repository_fasd();
				}
				else {
					return PortalImage.INSTANCE.fasd();
				}
			}
			else if (type == IRepositoryApi.FA_CUBE_TYPE) {
				return PortalImage.INSTANCE.Analysis();
			}
			else if (type == IRepositoryApi.FAV_TYPE) {
				return PortalImage.INSTANCE.view();
			}
			else if (type == IRepositoryApi.FD_TYPE) {
				if (big) {
					return PortalImage.INSTANCE.repository_dashboard();
				}
				else {
					return PortalImage.INSTANCE.fd();
				}
			}
			else if (type == IRepositoryApi.FWR_TYPE) {
				if (big) {
					return PortalImage.INSTANCE.repository_fwr();
				}
				else {
					return PortalImage.INSTANCE.Reports();
				}
			}
			else if (type == IRepositoryApi.MAP_TYPE) {
				if (big) {
					return PortalImage.INSTANCE.map_128();
				}
				else {
					return PortalImage.INSTANCE.md();
				}
			}
			else if (type == IRepositoryApi.FMDT_TYPE) {
				if (big) {
					return PortalImage.INSTANCE.metadata_big();
				}
				else {
					return PortalImage.INSTANCE.fmdt();
				}
			}
			else if (type == IRepositoryApi.FD_DICO_TYPE) {
				return PortalImage.INSTANCE.fd_Dico();
			}
			else if (type == IRepositoryApi.FAV_TYPE) {
				return PortalImage.INSTANCE.view();
			}
			else if (type == IRepositoryApi.BIW_TYPE) {
				if (big) {
					return PortalImage.INSTANCE.repository_wkf();
				}
				else {
					return PortalImage.INSTANCE.biw();
				}
			}
			else if (type == IRepositoryApi.GTW_TYPE) {
				if (big) {
					return PortalImage.INSTANCE.repository_big();
				}
				else {
					return PortalImage.INSTANCE.gateway_16();
				}
			}
			else if (type == IRepositoryApi.EXTERNAL_DOCUMENT) {
				String modelType = ((PortailRepositoryItem) object).getItem().getModelType();
				String format = "";
				if (modelType != null && modelType.contains(".")) {
					format = modelType.substring(modelType.lastIndexOf(".") + 1);
				}
				else if (modelType != null) {
					format = ((PortailRepositoryItem) object).getItem().getModelType();
				}
				return findImageForExtDoc(format, big);
			}
			else if (type == IRepositoryApi.URL) {
				if (big) {
					return PortalImage.INSTANCE.repository_ext_link();
				}
				else {
					return PortalImage.INSTANCE.link();
				}
			}
			else if (type == IRepositoryApi.GED_TYPE) {
				if (big) {
					return PortalImage.INSTANCE.repository_ged();
				}
				else {
					return PortalImage.INSTANCE.ged_temporary();
				}
			}
			else if (type == IRepositoryApi.GED_ENTRY) {
				if (big) {
					return PortalImage.INSTANCE.repository_ged();
				}
				else {
					return PortalImage.INSTANCE.repository_ged_small();
				}
			}
			else if (type == IRepositoryApi.CUST_TYPE) {
				// can be null, in that case it is not a valid object to show
				if (subType < 0) {
					// do nothing
				}
				else if (subType == IRepositoryApi.XACTION_SUBTYPE) {
					return PortalImage.INSTANCE.xaction();
				}
				else if (subType == IRepositoryApi.JASPER_REPORT_SUBTYPE) {
					if (big) {
						return PortalImage.INSTANCE.repository_jasper();
					}
					else {
						return PortalImage.INSTANCE.jrxml();
					}
				}
				else if (subType == IRepositoryApi.BIRT_REPORT_SUBTYPE) {
					if (big) {
						return PortalImage.INSTANCE.repository_birt();
					}
					else {
						return PortalImage.INSTANCE.birt16();
					}

				}
				else if (subType == IRepositoryApi.ORBEON_XFORMS) {
					if (big) {
						return PortalImage.INSTANCE.repository_orbeon();
					}
					else {
						return PortalImage.INSTANCE.orbeon_16();
					}
				}
			}
			else if (type == IRepositoryApi.REPORTS_GROUP) {
				if (big) {
					return CommonImages.INSTANCE.reports_group_128();
				}
				else {
					return CommonImages.INSTANCE.reports_group_16();
				}
			}
			else if (type == IRepositoryApi.R_MARKDOWN_TYPE) {
				if(subType == IRepositoryApi.MARKDOWN_SUBTYPE){
					if (big) {
						return PortalImage.INSTANCE.repository_markdown();
					}
					else {
						return PortalImage.INSTANCE.repository_markdown_small();
					}
				} else if(subType == IRepositoryApi.R_SUBTYPE){
					if (big) {
						return PortalImage.INSTANCE.repository_r();
					}
					else {
						return PortalImage.INSTANCE.repository_r_small();
					}
				}
				
			}
			else if (type == IRepositoryApi.KPI_THEME) {
				if (big) {
					return CommonImages.INSTANCE.kpi_theme_128();
				}
				else {
					return CommonImages.INSTANCE.kpi_theme_16();
				}
			}
			else if (type == IRepositoryApi.KPI_MAP) {
				if (big) {
					return CommonImages.INSTANCE.kpi_theme_128();
				}
				else {
					return CommonImages.INSTANCE.ic_map_black_18dp();
				}
			}
			else if (type == IRepositoryApi.FORM) {
				if (big) {
					return CommonImages.INSTANCE.portrait();
				}
				else {
					return CommonImages.INSTANCE.lk_wb_report_16();
				}
			}
			else if (type == IRepositoryApi.FMDT_CHART_TYPE) {
				return CommonImages.INSTANCE.chart_item();
			}
		}

		return PortalImage.INSTANCE.object();
	}

	private static ImageResource findImageForExtDoc(String format, boolean big) {
		if (big) {
			if (format.equalsIgnoreCase(FileFormat.PDF)) {
				return PortalImage.INSTANCE.format_pdf_128();
			}
			else if (format.equalsIgnoreCase(FileFormat.XLS) || format.equalsIgnoreCase(FileFormat.XLSX)) {
				return PortalImage.INSTANCE.format_xls_128();
			}
			else if (format.equalsIgnoreCase(FileFormat.DOC) || format.equalsIgnoreCase(FileFormat.DOCX)) {
				return PortalImage.INSTANCE.format_doc_128();
			}
			else if (format.equalsIgnoreCase(FileFormat.PPT) || format.equalsIgnoreCase(FileFormat.PPTX)) {
				return PortalImage.INSTANCE.format_ppt_128();
			}
			else if (format.equalsIgnoreCase(FileFormat.XML)) {
				return PortalImage.INSTANCE.format_xml_128();
			}
			else if (format.equalsIgnoreCase(FileFormat.TXT)) {
				return PortalImage.INSTANCE.format_txt_128();
			}
			else if (format.equalsIgnoreCase(FileFormat.HTML)) {
				return PortalImage.INSTANCE.format_html_128();
			}
			else if (format.equalsIgnoreCase(FileFormat.ODT)) {
				return PortalImage.INSTANCE.format_odt_128();
			}
			else if (format.equalsIgnoreCase(FileFormat.ODS)) {
				return PortalImage.INSTANCE.format_ods_128();
			}
			else if (format.equalsIgnoreCase(FileFormat.ZIP) || format.equalsIgnoreCase(FileFormat.TAR_GZ) || format.equalsIgnoreCase(FileFormat.TAR) || format.equalsIgnoreCase(FileFormat.GZ) || format.equalsIgnoreCase(FileFormat.JAR) || format.equalsIgnoreCase(FileFormat.RAR)) {
				return PortalImage.INSTANCE.format_zip_128();
			}
			else if (format.equalsIgnoreCase(FileFormat.JPG) || format.equalsIgnoreCase(FileFormat.JPEG) || format.equalsIgnoreCase(FileFormat.GIF) || format.equalsIgnoreCase(FileFormat.PNG) || format.equalsIgnoreCase(FileFormat.SVG) || format.equalsIgnoreCase(FileFormat.BMP)) {
				return PortalImage.INSTANCE.format_img_128();
			}
			else if (format.equalsIgnoreCase(FileFormat.MPG) || format.equalsIgnoreCase(FileFormat.MPEG) || format.equalsIgnoreCase(FileFormat.AVI) || format.equalsIgnoreCase(FileFormat.MP4) || format.equalsIgnoreCase(FileFormat.WMW) || format.equalsIgnoreCase(FileFormat.MOV) || format.equalsIgnoreCase(FileFormat.MKV) || format.equalsIgnoreCase(FileFormat.MLV) || format.equalsIgnoreCase(FileFormat.OGG)) {
				return PortalImage.INSTANCE.format_video_128();
			}
			else if (format.equalsIgnoreCase(FileFormat.MP3) || format.equalsIgnoreCase(FileFormat.WAV) || format.equalsIgnoreCase(FileFormat.FLAC) || format.equalsIgnoreCase(FileFormat.WMA) || format.equalsIgnoreCase(FileFormat.M4A)) {
				return PortalImage.INSTANCE.format_music_128();
			}

			return PortalImage.INSTANCE.repository_ext_document();
		}
		else {
			if (format.equalsIgnoreCase(FileFormat.PDF)) {
				return PortalImage.INSTANCE.format_pdf_16();
			}
			else if (format.equalsIgnoreCase(FileFormat.XLS) || format.equalsIgnoreCase(FileFormat.XLSX)) {
				return PortalImage.INSTANCE.format_xls_16();
			}
			else if (format.equalsIgnoreCase(FileFormat.DOC) || format.equalsIgnoreCase(FileFormat.DOCX)) {
				return PortalImage.INSTANCE.format_doc_16();
			}
			else if (format.equalsIgnoreCase(FileFormat.PPT) || format.equalsIgnoreCase(FileFormat.PPTX)) {
				return PortalImage.INSTANCE.format_ppt_16();
			}
			else if (format.equalsIgnoreCase(FileFormat.XML)) {
				return PortalImage.INSTANCE.format_xml_16();
			}
			else if (format.equalsIgnoreCase(FileFormat.TXT)) {
				return PortalImage.INSTANCE.format_txt_16();
			}
			else if (format.equalsIgnoreCase(FileFormat.HTML)) {
				return PortalImage.INSTANCE.format_html_16();
			}
			else if (format.equalsIgnoreCase(FileFormat.ODT)) {
				return PortalImage.INSTANCE.format_odt_16();
			}
			else if (format.equalsIgnoreCase(FileFormat.ODS)) {
				return PortalImage.INSTANCE.format_ods_16();
			}
			else if (format.equalsIgnoreCase(FileFormat.ZIP) || format.equalsIgnoreCase(FileFormat.TAR_GZ) || format.equalsIgnoreCase(FileFormat.TAR) || format.equalsIgnoreCase(FileFormat.GZ) || format.equalsIgnoreCase(FileFormat.JAR) || format.equalsIgnoreCase(FileFormat.RAR)) {
				return PortalImage.INSTANCE.format_zip_16();
			}
			else if (format.equalsIgnoreCase(FileFormat.JPG) || format.equalsIgnoreCase(FileFormat.JPEG) || format.equalsIgnoreCase(FileFormat.GIF) || format.equalsIgnoreCase(FileFormat.PNG) || format.equalsIgnoreCase(FileFormat.SVG) || format.equalsIgnoreCase(FileFormat.BMP)) {
				return PortalImage.INSTANCE.format_img_16();
			}
			else if (format.equalsIgnoreCase(FileFormat.MPG) || format.equalsIgnoreCase(FileFormat.MPEG) || format.equalsIgnoreCase(FileFormat.AVI) || format.equalsIgnoreCase(FileFormat.MP4) || format.equalsIgnoreCase(FileFormat.WMW) || format.equalsIgnoreCase(FileFormat.MOV) || format.equalsIgnoreCase(FileFormat.MKV) || format.equalsIgnoreCase(FileFormat.MLV)) {
				return PortalImage.INSTANCE.format_video_16();
			}
			else if (format.equalsIgnoreCase(FileFormat.MP3) || format.equalsIgnoreCase(FileFormat.WAV) || format.equalsIgnoreCase(FileFormat.FLAC) || format.equalsIgnoreCase(FileFormat.WMA) || format.equalsIgnoreCase(FileFormat.M4A)) {
				return PortalImage.INSTANCE.format_music_16();
			}

			return PortalImage.INSTANCE.doc();
		}
	}

	public static boolean isBirt(PortailRepositoryItem item) {
		return item.getType() == IRepositoryApi.CUST_TYPE && item.getSubType() == IRepositoryApi.BIRT_REPORT_SUBTYPE;
	}

	public static boolean isCube(PortailRepositoryItem item) {
		return item.getType() == IRepositoryApi.FASD_TYPE || item.getType() == IRepositoryApi.FA_CUBE_TYPE || item.getType() == IRepositoryApi.FAV_TYPE;
	}

	public static boolean isRunnable(PortailRepositoryItem item) {
		return item.isReport() || item.getType() == IRepositoryApi.FD_TYPE || item.getType() == IRepositoryApi.FASD_TYPE || item.getType() == IRepositoryApi.FA_CUBE_TYPE 
				|| item.getType() == IRepositoryApi.FAV_TYPE || item.getType() == IRepositoryApi.GTW_TYPE || item.getType() == IRepositoryApi.GED_ENTRY 
				|| item.getType() == IRepositoryApi.URL || item.getType() == IRepositoryApi.EXTERNAL_DOCUMENT || item.getType() == IRepositoryApi.FMDT_TYPE 
				|| item.getType() == IRepositoryApi.FMDT_DRILLER_TYPE || item.getType() == IRepositoryApi.FMDT_CHART_TYPE || item.getType() == IRepositoryApi.BIW_TYPE || item.getType() == IRepositoryApi.REPORTS_GROUP || item.getType() == IRepositoryApi.KPI_THEME
				|| item.getType() == IRepositoryApi.R_MARKDOWN_TYPE|| item.getType() == IRepositoryApi.KPI_MAP|| item.getType() == IRepositoryApi.FORM|| item.getType() == IRepositoryApi.MAP_TYPE;
	}

	public static boolean isPublicUrl(PortailRepositoryItem item) {
		return item.isReport() || item.getType() == IRepositoryApi.FD_TYPE || item.getType() == IRepositoryApi.FAV_TYPE || item.getType() == IRepositoryApi.GTW_TYPE
				|| item.getType() == IRepositoryApi.BIW_TYPE;
	}

	public static boolean checkItemToPack(PortailRepositoryItem item) {
		if ((item.getType() == IRepositoryApi.CUST_TYPE && item.getSubType() == IRepositoryApi.BIRT_REPORT_SUBTYPE) || item.getType() == IRepositoryApi.FD_TYPE || item.getType() == IRepositoryApi.FD_DICO_TYPE || item.getType() == IRepositoryApi.FASD_TYPE) {
			return true;
		}
		return false;
	}

	public static boolean checkItemsToPack(List<IRepositoryObject> items) {
		if (items != null) {
			for (IRepositoryObject obj : items) {
				if (obj instanceof PortailRepositoryItem) {
					PortailRepositoryItem item = (PortailRepositoryItem) obj;
					if ((item.getType() == IRepositoryApi.CUST_TYPE && item.getSubType() == IRepositoryApi.BIRT_REPORT_SUBTYPE) || item.getType() == IRepositoryApi.FD_TYPE || item.getType() == IRepositoryApi.FD_DICO_TYPE || item.getType() == IRepositoryApi.FASD_TYPE) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
