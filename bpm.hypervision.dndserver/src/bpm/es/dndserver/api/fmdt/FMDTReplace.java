package bpm.es.dndserver.api.fmdt;

import bpm.es.dndserver.api.fmdt.replacers.IFMDTReplacer;
import bpm.es.dndserver.api.fmdt.replacers.impl.BirtFmdtReplacer;
import bpm.es.dndserver.api.fmdt.replacers.impl.FdDicoFmdtReplacer;
import bpm.es.dndserver.api.fmdt.replacers.impl.FwrFmdtReplacer;
import bpm.es.dndserver.api.fmdt.replacers.impl.GtwFmdtReplacer;
import bpm.es.dndserver.api.repository.AxisDirectoryItemWrapper;
import bpm.vanilla.platform.core.IRepositoryApi;

public class FMDTReplace {

	public static IFMDTReplacer getReplacer(AxisDirectoryItemWrapper item/*, String modelXml, FMDTDataSource orig, FMDTDataSource toreplace*/) throws Exception {
		
//		Document doc = DocumentHelper.parseText(modelXml);
//		//.element("report")
//		//.elements("property").get(0)
//		//doc.getRootElement().element("data-sources").element("oda-data-source").elements("property").get(0)
//		List<Element> list = doc.getRootElement().element("data-sources").elements("oda-data-source");
//		
//		for (Element ele : list) {
//			parseAndReplaceBIRTProps(ele, orig, toreplace);
//			
//			System.out.println();
//		}
//		
//		return doc.asXML();
		IFMDTReplacer replacer = null;
		switch(item.getAxisItem().getType()){
		case IRepositoryApi.FD_DICO_TYPE:
			replacer = new FdDicoFmdtReplacer();
			break;
//			return replacer.replace(modelXml, orig, toreplace);
		case IRepositoryApi.GTW_TYPE:
			replacer = new GtwFmdtReplacer();
			break;
		case IRepositoryApi.FWR_TYPE:
			replacer = new FwrFmdtReplacer();
			break;
		case IRepositoryApi.CUST_TYPE:
			if (item.getAxisItem().getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE){
				replacer = new BirtFmdtReplacer();
				break;
//				return replacer.replace(modelXml, orig, toreplace);
			}
		}
		return replacer;
		
	}
//	S
}
