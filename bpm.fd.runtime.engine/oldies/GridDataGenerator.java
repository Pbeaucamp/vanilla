package bpm.fd.runtime.engine.components;

import java.util.HashMap;
import java.util.List;

import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.datagrid.ComponentDataGrid;
import bpm.fd.api.core.model.components.definition.datagrid.DataGridData;
import bpm.fd.api.core.model.components.definition.datagrid.DataGridOptions;
import bpm.fd.api.core.model.components.definition.datagrid.DataGridLayout.DataGridCellType;
import bpm.fd.api.core.model.datas.ColumnDescriptor;
import bpm.fd.runtime.engine.VanillaProfil;
import bpm.fd.runtime.engine.datas.JSPDataGenerator;

public class GridDataGenerator {
	
	
	public static String generateJspBlock(int offset, ComponentDataGrid dataGrid, String outputParameterName, HashMap<IComponentDefinition, String> pOuts, List<ComponentConfig> configs, VanillaProfil profil)throws Exception{
		StringBuffer buf = new StringBuffer();
		String spacing = "";
		String resultSet = dataGrid.getId() + "ResultSet";
		String queryName = dataGrid.getDatas().getDataSet().getId() + "Query";
		
		buf.append(spacing + " <%\n");
		buf.append(spacing + "     List<List<Object>> " + resultSet + "Values = null;\n");
		buf.append(spacing + "     IResultSet " + resultSet + " = null;\n");
		buf.append(spacing + "     try{\n");
		
		buf.append(JSPDataGenerator.prepareQuery(spacing, queryName, dataGrid));
		
		buf.append(spacing + "        " + resultSet + " = " + queryName + ".executeQuery();\n");
		Integer orderPos = ((DataGridData)dataGrid.getDatas()).getOrderFieldPosition();
		if (((DataGridData)dataGrid.getDatas()).isOrderDatas() &&  orderPos != null && orderPos != null && orderPos >= 0){
			buf.append(spacing + "        " + resultSet + "Values = Sorter.sort(" + resultSet + ", " + (orderPos + 1) +");\n");
		}
		else{
			buf.append(spacing + "        " + resultSet + "Values = Sorter.sort(" + resultSet + ", " + (-1) +");\n");
		}
	
		
		
		buf.append(spacing + "     }catch(Exception e){\n");
		buf.append(spacing + "         e.printStackTrace();\n");
//		buf.append(spacing + "         e.printStackTrace(errorWriter);\n");
		buf.append(spacing + "     }\n");
		buf.append(spacing + "%>\n\n");
		buf.append(" <%\n");
		
		String css = "";
		if (dataGrid.getCssClass() != null && !"".equals(dataGrid.getCssClass())){
			css = " class=\\\"" + dataGrid.getCssClass() + "\\\" ";
		}
		
		String cellspacing = "";//" cellspacing=\\\"0\\\" ";
		
//		<table>
//		<tr>
//		 <td colspan="2">
		buf.append("    out.write(\"<table>\\n\");\n");
		buf.append("    out.write(\"<tr>\\n\");\n");
		buf.append("    out.write(\"<td colspan=\\\"2\\\">\\n\");\n");
		buf.append("    out.write(\"<table " + cellspacing + " id=\\\"" + dataGrid.getId() + "\\\" " + css + ">\\n\");\n");
		
		
		/*
		 * write headers
		 */
		DataGridOptions opt = (DataGridOptions)dataGrid.getOptions(DataGridOptions.class);
		
		if (opt.isHeadersVisible()){
			buf.append("    out.write(\"    <tr>\\n\");\n");
			for(ColumnDescriptor c : dataGrid.getDatas().getDataSet().getDataSetDescriptor().getColumnsDescriptors()){
				DataGridCellType type = dataGrid.getLayout().getType(c.getColumnIndex() - 1);
				
				if (dataGrid.getLayout().getHeaderTitle(c.getColumnIndex()) != null){
					if (type != DataGridCellType.Hidden){
						
						buf.append("    out.write(\"<th>" + dataGrid.getLayout().getHeaderTitle(c.getColumnIndex()) + "</th>\\n\");\n");
					}
					else{
						buf.append("    out.write(\"<th  style='display:none;'  >&nbsp;</th>\\n\");\n");
					}
				}
				else{
					if (type != DataGridCellType.Hidden){
						
						buf.append("    out.write(\"<th>" + c.getColumnLabel() + "</th>\\n\");\n");
					}
					else{
						buf.append("    out.write(\"<th  style='display:none;' >&nbsp;</th>\\n\");\n");
					}
				}
				
				
				
			}
			
//			
			
			
			
			if (dataGrid.isDrillable()){
				String s = "i18nReader.getLabel(clientLocale, \"" + dataGrid.getId() + "." + opt.getInternationalizationKeys()[DataGridOptions.KEY_DRILL_HEADER] + "\", _parameterMap)";
				buf.append("    out.write(\"<th>\"+" + s+ "+\"</th>\\n\");\n");
			}
			buf.append("    out.write(\"    </tr>\\n\");\n");
		}
		
		
		
		/*
		 * drill
		 */
		HashMap<IComponentDefinition, ComponentConfig> targetPageConfig = dataGrid.getDrillInfo().getModelPage() == null ? new HashMap<IComponentDefinition, ComponentConfig>() : dataGrid.getDrillInfo().getModelPage().getComponents();
		StringBuffer s = new StringBuffer();
		if (dataGrid.getDrillInfo().getModelPage() != null){
			s.append("request.getRequestURI() + \"/../" + dataGrid.getDrillInfo().getModelPage().getId() + ".jsp\" +\"?\" + (request.getQueryString() != null ? request.getQueryString() : \"\") + \"");
			
		}
		
		for(IComponentDefinition def : targetPageConfig.keySet()){
			for(ComponentParameter p : targetPageConfig.get(def).getParameters()){
				if (dataGrid.getName().equals(targetPageConfig.get(def).getComponentNameFor(p))){
					
					Integer fieldPosition = null;
					for(ComponentParameter _p : dataGrid.getOutputParameters()){
						//fieldPosition = dataGrid.getDrillInfo().getColumnPositionForParameter(_p);
						break;
					}
					
					
//					s.append("&" + p.getId() + "=\"+" + resultSet + ".getString(" + (fieldPosition + 1) + ")+\"" );
					s.append("&" + p.getId() + "=\"+row.get(" + fieldPosition + ")+\"" );
//					break;
				}
				else{
					s.append("&" + targetPageConfig.get(def).getComponentNameFor(p) + "='+ parameters['"+ targetPageConfig.get(def).getComponentNameFor(p)  + "']+'" );
				}
			}
		}
//		System.out.println(s);
		
		/*
		 * write DataSetValues
		 */
		buf.append("        int _" + dataGrid.getId() + "RowNumber=0;\n");
		
		buf.append("        Float[] _" + dataGrid.getId() + "Totals = new Float[" + dataGrid.getDatas().getDataSet().getDataSetDescriptor().getColumnsDescriptors().size()+ "];");

		/*
		 * the colorTab
		 */
		if (dataGrid.getLayout().isUseColor()){
			buf.append("        String[] _" + dataGrid.getId() + "Colors = new String[]{");
			boolean f = true;
			for(String _s : dataGrid.getLayout().getColors()){
				if (f){
					f = !f;
				}
				else{
					buf.append(",");
				}
				buf.append("\"" + _s + "\"");
			}
			buf.append("};\n");
		}
		buf.append(spacing + "            Iterator<List<Object>> " + resultSet + "Iter = " + resultSet + "Values.iterator();\n");
		buf.append(spacing + "            while(" + resultSet + "Iter != null && " + resultSet + "Iter.hasNext()){\n");
		buf.append(spacing + "               List<Object> row = " + resultSet + "Iter.next();\n");

		
//		buf.append("    while(" + resultSet + " != null && " + resultSet + ".next()){\n");
		buf.append("        try{\n");
		buf.append("            out.write(\"    <tr>\\n\");\n");
		for(int i = 0; i < dataGrid.getDatas().getDataSet().getDataSetDescriptor().getColumnsDescriptors().size(); i++){
			
			
			boolean aggregate = opt.isIncludeTotal();
			
			String layout = "";
			DataGridCellType type = dataGrid.getLayout().getType(i);
			switch(type){
			case Editable:
				break;
			case Hidden:
				aggregate = false;
				layout = " type=\\\"hidden\\\" ";
				break;
			default:
				layout = " disabled=\\\"true\\\" ";
			}
			
			if (aggregate){
				buf.append("        try{\n");
				buf.append("         if (_" + dataGrid.getId() + "Totals[" + (i)+"] == null){\n");
//				buf.append("             _" + dataGrid.getId() + "Totals[" + (i)+"] = Float.parseFloat(" + resultSet + ".getString(" + (i+1) + "));\n");
				buf.append("             _" + dataGrid.getId() + "Totals[" + (i)+"] = Float.parseFloat(row.get(" + (i) + ").toString());\n");
				buf.append("         }\n");
				buf.append("         else{\n");
//				buf.append("             _" + dataGrid.getId() + "Totals[" + (i)+"] += Float.parseFloat(" + resultSet + ".getString(" + (i+1) + "));\n");
				buf.append("             _" + dataGrid.getId() + "Totals[" + (i)+"] += Float.parseFloat(row.get(" + (i) + ").toString());\n");
				buf.append("         }\n");
				
				buf.append("        }catch(Exception _ex){\n");
				buf.append("        _ex.printStackTrace();");
				buf.append("        }\n");
				   
			}
//			
			String tdStyle = "";
			if (type == DataGridCellType.Hidden){
				tdStyle = " style='display:none;' ";
			}
			if  (dataGrid.getLayout().isUseColor() && i == dataGrid.getLayout().getColorFieldIndex()){
				
				String colBuf = " <span style='background-color:\" + (_" + dataGrid.getId() + "RowNumber < _" + dataGrid.getId() + "Colors.length ? _" + dataGrid.getId() + "Colors[_" + dataGrid.getId() + "RowNumber] : \"000000\")+ \";width:50px;'>&nbsp;&nbsp;</span>";
//				buf.append("            out.write(\"<td><input id=\\\"" + dataGrid.getId() + "_col" + dataGrid.getId() + "RowNumber_row" +i + "\\\" " + layout + " value=\\\"\"+" + resultSet + ".getString(" + (i+1) + ")+" + "\"\\\" />" + colBuf + "</td>\\n\");\n");
				buf.append("            out.write(\"<td" + tdStyle + "><input id=\\\"" + dataGrid.getId() + "_col" + dataGrid.getId() + "RowNumber_row" +i + "\\\" " + layout + " value=\\\"\"+row.get(" + i + ")+" + "\"\\\" />" + colBuf + "</td>\\n\");\n");
			}
			else{
				
//				buf.append("            out.write(\"<td><input id=\\\"" + dataGrid.getId() + "_col" + dataGrid.getId() + "RowNumber_row" +i + "\\\" " + layout + " value=\\\"\"+" + resultSet + ".getString(" + (i+1) + ")+" + "\"\\\" /></td>\\n\");\n");
				buf.append("            out.write(\"<td " + tdStyle + "><input id=\\\"" + dataGrid.getId() + "_col" + dataGrid.getId() + "RowNumber_row" +i + "\\\" " + layout + " value=\\\"\"+row.get(" + i + ")+" + "\"\\\" /></td>\\n\");\n");
			}
			
			
		}
		buf.append("        _" + dataGrid.getId() + "RowNumber++;\n");
		if (dataGrid.isDrillable()){
			String _s = "i18nReader.getLabel(clientLocale, \"" + dataGrid.getId() + "." + opt.getInternationalizationKeys()[DataGridOptions.KEY_DRILL_TEXT] + "\", _parameterMap)";
			buf.append("    out.write(\"<td><a href=\\\"#\\\" onclick=\\\"javascript:window.open('\"+" + s.toString() + "\" + \"', 'zz', 'menubar=0,height=" +
						dataGrid.getDrillInfo().getPopupHeight() + ",width=" +
						dataGrid.getDrillInfo().getPopupWidth()
						+ "');\\\">\"+" + _s + "+\"</a></td>\\n\");\n");
		}
		buf.append("            out.write(\"    </tr>\\n\");\n");
		
		
		
		
		
		buf.append("        }catch(Exception ex){\n");
		buf.append("            ex.printStackTrace();\n");
		buf.append("        }\n");
		buf.append("    }\n");
		
		if (opt.isIncludeTotal()){
			buf.append("            out.write(\"    <tr>\\n\");\n");
			buf.append("            out.write(\"    <td>Total</td>\\n\");\n");
			
			buf.append("            for(int i = 1; i < " + dataGrid.getDatas().getDataSet().getDataSetDescriptor().getColumnsDescriptors().size() + "; i++){\n");
			buf.append("                out.write(\"    <td>\"+(_" + dataGrid.getId() + "Totals[i] != null ?_" + dataGrid.getId() + "Totals[i] : \"&nbsp;\")+\"</td>\\n\");\n");
			buf.append("            }\n");
			buf.append("            out.write(\"    </tr>\\n\");\n");
		}
		
		buf.append("    out.write(\"</table>\\n\");\n");

		buf.append("    out.write(\"</td>\\n\");\n");
		buf.append("    out.write(\"</tr>\\n\");\n");
		if (((DataGridOptions)dataGrid.getOptions(DataGridOptions.class)).isRowsCanBeAdded()){
			buf.append("    out.write(\"<tr>\\n\");\n");
			buf.append("    out.write(\"<td>\\n\");\n");
			buf.append("    out.write(\"<table>\\n\");\n");
			buf.append("    out.write(\"<tr>\\n\");\n");
			buf.append("    out.write(\"<td style=\\\"width:32px\\\">\\n\");\n");
			buf.append("    out.write(\"<a href=\\\"javascript:showGridMenu('" + dataGrid.getId() + "_menu')\\\" ><img src=\\\"../../datagrid/2downarrow.png\\\" title=\\\"Open DataGridToolBar\\\" class=\\\"_datagridMenuButton\\\"/></a>\\n\");\n");
			buf.append("    out.write(\"</td>\\n\");\n");
			buf.append("    out.write(\"<td>\\n\");\n");
			buf.append("    out.write(\" <div id='" + dataGrid.getId() + "_menu' class=\\\"_datagridMenu\\\">\\n\");\n");
			buf.append("    out.write(\"<a href=\\\"javascript:gridData_addRow('" + dataGrid.getId() + "')\\\" class=\\\"_datagrid_add\\\"><img src=\\\"../../datagrid/add.png\\\" title=\\\"Add an empty Row\\\" /></a>\\n\");\n");
			buf.append("    out.write(\"<a href=\\\"javascript:gridData_delRow('" + dataGrid.getId() + "')\\\" class=\\\"_datagrid_del\\\"><img src=\\\"../../datagrid/close.png\\\" title=\\\"Remove the last Row\\\" /></a>\\n\");\n");
			buf.append("    out.write(\"</div>\\n\");\n");
			buf.append("    out.write(\"</td>\\n\");\n");
			buf.append("    out.write(\"</tr>\\n\");\n");
			buf.append("    out.write(\"</table>\\n\");\n");
			buf.append("    out.write(\"</td>\\n\");\n");
			buf.append("    out.write(\"</tr>\\n\");\n");
		}

		buf.append("    out.write(\"</td>\\n\");\n");
		buf.append("    out.write(\"</tr>\\n\");\n");
		buf.append("    out.write(\"</table>\\n\");\n");
		buf.append(" %>\n");
		
		return buf.toString();
	}
}
