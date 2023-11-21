package bpm.vanilla.server.client.commandline.profiing;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class XlsWriter {

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
	
	private WritableWorkbook workbook;
	
	public XlsWriter(File outputFile) throws Exception{
		workbook = Workbook.createWorkbook(outputFile);
	}
	
	public void writeSheet(String sheetName, List<TaskRunner> taskProfilers) throws Exception{
		WritableSheet sheet = workbook.createSheet(sheetName, workbook.getNumberOfSheets() + 1);
		
		try{
			sheet.addCell(new Label(0, 0, "TaskNumber"));
			sheet.addCell(new Label(1, 0, "TaskId"));
			sheet.addCell(new Label(2, 0, "Created"));
			sheet.addCell(new Label(3, 0, "Started"));
			sheet.addCell(new Label(4, 0, "Stopped"));
			sheet.addCell(new Label(5, 0, "SubmissionTime"));
			sheet.addCell(new Label(6, 0, "RunningTime"));
			sheet.addCell(new Label(7, 0, "WaitingTime"));
			sheet.addCell(new Label(8, 0, "ProfilingStatus"));
			sheet.addCell(new Label(9, 0, "Status"));
			
			/*
			 * column width
			 */
			sheet.setColumnView(0, 10);
			sheet.setColumnView(1, 6);
			sheet.setColumnView(2, 24);
			sheet.setColumnView(3, 24);
			sheet.setColumnView(4, 24);
			sheet.setColumnView(5, 14);
			sheet.setColumnView(6, 11);
			sheet.setColumnView(7, 11);
			sheet.setColumnView(8, 15);
			sheet.setColumnView(9, 7);
			
			int row = 1;
			
			for(TaskRunner t : taskProfilers){
				sheet.addCell( new Label(0, row, t.getNumber()));
				sheet.addCell( new Label(1, row, t.getTaskId()));
				sheet.addCell( new Label(2, row, sdf.format(t.getTaskInfo().getCreationDate())));
				sheet.addCell( new Label(3, row, sdf.format(t.getTaskInfo().getStartedDate())));
				sheet.addCell( new Label(4, row, sdf.format(t.getTaskInfo().getStoppedDate())));
				sheet.addCell( new jxl.write.Number(5, row, t.getSubmissionTime()));
				

				try{
					long l = t.getTaskInfo().getStoppedDate().getTime() - t.getTaskInfo().getStartedDate().getTime();
					sheet.addCell( new jxl.write.Number(6, row, l));
				}catch(Exception ex){
					sheet.addCell( new jxl.write.Number(6, row, 0));
				}
				
				try{
					long l = t.getTaskInfo().getStartedDate().getTime() - t.getTaskInfo().getCreationDate().getTime();
					sheet.addCell( new jxl.write.Number(7, row, l));
				}catch(Exception ex){
					sheet.addCell( new jxl.write.Number(7, row, 0));
				}
				sheet.addCell( new Label(8, row, (t.hasProfilingSucceed() ? "succeed" : "failed")));
				
				
				try{
					sheet.addCell( new jxl.write.Number(9, row, t.getTaskInfo().getResult()));
				}catch(Exception ex){
					sheet.addCell( new Label(9, row, ex.getMessage()));
				}
				
				row ++;
			}
			
			
		}catch(Exception ex){
			throw ex;
		}
		
		
	}
	
	public void writeResume(List<ProfilingResult> results){
		
		
		
		
		try{
			
			
			WritableSheet sheet = workbook.createSheet("Resume", workbook.getNumberOfSheets() + 1);
			
			/*
			 * write Labels
			 */
			
			sheet.addCell(new Label(0, 0, "Configured TaskNumber"));
			sheet.addCell(new Label(1, 0, "Configured RepositoryConnectionNumber"));
			sheet.addCell(new Label(2, 0, "Average Publishing Time"));
			sheet.addCell(new Label(3, 0, "Average Running Time"));
			sheet.addCell(new Label(4, 0, "Average Waiting Time"));
			
			
			/*
			 * column width
			 */
			sheet.setColumnView(0, 21);
			sheet.setColumnView(1, 37);
			sheet.setColumnView(2, 23);
			sheet.setColumnView(3, 20);
			sheet.setColumnView(4, 20);
			
			/*
			 * write Datas
			 */
			int row = 1;
			for(ProfilingResult r : results){
				
				sheet.addCell( new jxl.write.Number(0, row, (r.taskNumber == null ? -1 : r.taskNumber)));
				sheet.addCell( new jxl.write.Number(1, row, (r.repositoryConnectionNumber == null ? -1 : r.repositoryConnectionNumber)));
				sheet.addCell( new jxl.write.Number(2, row, r.avgPublishingTime));
				sheet.addCell( new jxl.write.Number(3, row, r.avgRunningTime));
				sheet.addCell( new jxl.write.Number(4, row, r.avgWaitingTime));
				
				
				row++;
			}
		}catch(Exception ex){
			System.err.println("Error writing Resume XLS Sheet");
			ex.printStackTrace();
			System.exit(-1);
		}
		
		
		
		
	}

	public void close() throws Exception{
		workbook.write();
		workbook.close();
		
	}
}
