package bpm.faweb.server.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XLSWriter {
	public static void main(String[] args) 
    {
		List<String> header = new ArrayList<String>();
		header.add("Customer");
		header.add("Country");
		header.add("City");
		
		List<List<String>> cell = new ArrayList<List<String>>();
		cell.add(header);
		cell.add(header);
		cell.add(header);
		cell.add(header);
		cell.add(header);
		cell.add(header);
		
		new XLSWriter("C:/Development/Free Analysis/poi-bin-3.10-FINAL-20140208/dsads.xlsx","Sample","Sample", "Sample","Sampledsaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", header, cell);
    }

	private String title;
	private Object description;
	private Object origin;
	private Object filters;
	private List<String> headerList;
	private List<List<String>> cellList;
	private String url;
	
	public XLSWriter(String url,String title, Object description, Object origin, Object filters, List<String> headerList, List<List<String>> cellList){
		this.url = url;
		this.title = title;
		this.description = description;
		this.origin = origin;
		this.filters = filters;
		this.headerList = headerList;
		this.cellList = cellList;
		writeXLS();
	}
	
	private void writeXLS(){
		//Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook(); 
         
        //Create a blank sheet
        XSSFSheet sheet = workbook.createSheet(title);
       
        
        //This data needs to be written (Object[])
        Map<Integer, Object[]> data = new TreeMap<Integer, Object[]>();
        data.put(1, new Object[] {"Title", title});
        data.put(2, new Object[] {"Description", description});
        data.put(3, new Object[] {"Origin", origin});
        data.put(4, new Object[] {"Filters", filters});
        data.put(5, new Object[] {"", "", ""});
        
        
        //Building the Header
        Object[] headerObj = new Object[headerList.size()];
        int x = 0;
        for(String s : headerList){
        	headerObj[x] = s.toUpperCase();
        	x++;
        }
        data.put(6, headerObj);
        
        //Build the Cells
        
        int c = 7;
        for(List<String> cell : cellList){
        	Object[] cellObj = new Object[cell.size()];
            int y = 0;
            for(String s : cell){
            	cellObj[y] = s;
            	y++;
            }
            
            data.put(c, cellObj);
            c++;
        }
       
        //Iterate over data and write to sheet
        Set<Integer> keyset =  data.keySet();
        int rownum = 0;
        for (Integer key : keyset)
        {
        	XSSFRow row = sheet.createRow(rownum++);
            Object [] objArr = data.get(key);
            int cellnum = 0;
            for (Object obj : objArr)
            {
            	XSSFCell cell = row.createCell(cellnum++);
            	if(key == 6){
            	   setCellColorAndFontColor(cell, IndexedColors.BLACK, IndexedColors.WHITE);
            	}
               if(obj instanceof String)
                    cell.setCellValue((String)obj);
                else if(obj instanceof Integer)
                    cell.setCellValue((Integer)obj);
            }
        }
        try
        {
        	
            //Write the workbook in file system
            FileOutputStream out = new FileOutputStream(new File(url));
            workbook.write(out);
            out.close();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
	}
	
	public static void setCellColorAndFontColor(XSSFCell cell, IndexedColors FGcolor, IndexedColors FontColor ){
	    XSSFWorkbook wb = cell.getRow().getSheet().getWorkbook();
	    
	    CellStyle style = wb.createCellStyle();
	    XSSFFont font = wb.createFont();
	    font.setBold(true);
	    font.setColor(FontColor.getIndex());
	    style.setFont(font);
	    style.setFillForegroundColor(FGcolor.getIndex());
	    style.setFillPattern(CellStyle.SOLID_FOREGROUND);
	    cell.setCellStyle(style);
	}


}
