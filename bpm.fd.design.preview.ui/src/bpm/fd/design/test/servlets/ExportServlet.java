package bpm.fd.design.test.servlets;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.ConnectException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;

import bpm.fd.runtime.model.DashInstance;
import bpm.fd.runtime.model.controler.Controler;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;

public class ExportServlet extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String osName = System.getProperty("os.name");
		
		String destFile = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_FILES) + "/temp";
		File f = new File(destFile);
		f.mkdir();
		
		String fileName = "ExportDashboard" + new Object().hashCode() + ".pdf";
		
		destFile = destFile + "/" + fileName;
		
		if(osName.toLowerCase().contains("windows")) {
//			Runtime rt = Runtime.getRuntime() ;
//			String resourcesFolder = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.vanilla.platform.resourcesFolder");
//			String wkhtmltopdf = resourcesFolder + "/wkhtmltopdf/" + "wkhtmltopdf.exe";
//			File wkhtmltopdfFile = new File(wkhtmltopdf);
//			
			String uuid = req.getParameter("uuid");
			DashInstance instance = null;
			try {
				instance = Controler.getInstance().getInstance(uuid);
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			
			
			String url = req.getScheme() + "://" +
            req.getServerName() + 
            ("http".equals(req.getScheme()) && req.getServerPort() == 80 || "https".equals(req.getScheme()) && req.getServerPort() == 443 ? "" : ":" + req.getServerPort() ) +
            "/generation/" + instance.getRelativeUrl();
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			getFileStream(url, out, 2048);
			
			String html = out.toString("UTF-8");
			
			FileOutputStream fos = new FileOutputStream(destFile);
			
			try {
				Document doc = new Document(PageSize.A4);
				PdfWriter.getInstance(doc, fos);
				doc.open();
				HTMLWorker hw = new HTMLWorker(doc);
				hw.parse(new StringReader(html));
				doc.close();
			} catch(DocumentException e) {
				e.printStackTrace();
			}
			
//			String execCommand = wkhtmltopdfFile.getAbsolutePath() + " " + url + "&_isExport_=true " + destFile;
////			System.out.println(execCommand);
//			
//			Process p = rt.exec(execCommand);
//			
//			String errors = IOUtils.toString(p.getErrorStream(), "UTF-8");
//			System.out.println(errors);
			
		}
		else {
			//TODO do it for linux
		}
		
		FileInputStream is = new FileInputStream(destFile);
		
		resp.setHeader("Content-disposition", "attachment; filename=" + fileName);
		
		ServletOutputStream out = resp.getOutputStream();

		byte buffer[] = new byte[512 * 1024];
		int nbLecture;
		while ((nbLecture = is.read(buffer)) != -1) {
			out.write(buffer, 0, nbLecture);
		}
		is.close();
		out.close();
		
	}
	
	/**
	 * Retrieves the file specified by <code>fileUrl</code> and writes it to 
	 * <code>out</code>.
	 * <p>
	 * Does not close <code>out</code>, but does flush.
	 * @param fileUrl The URL of the file.
	 * @param out An output stream to capture the contents of the file
	 * @param batchWriteSize The number of bytes to write to <code>out</code>
	 *                       at once (larger files than this will be written
	 *                       in several batches)
	 * @throws IOException If call to web server fails
	 * @throws FileNotFoundException If the call to the web server does not
	 *                               return status code 200. 
	 */
	public static void getFileStream(String fileURL, OutputStream out, int batchWriteSize)
								throws IOException{
		GetMethod get = new GetMethod(fileURL);
		HttpClient client = new HttpClient();
		HttpClientParams params = client.getParams();
		params.setSoTimeout(2000);
		client.setParams(params);
		try {
			client.executeMethod(get);
		} catch(ConnectException e){
			// Add some context to the exception and rethrow
			throw new IOException("ConnectionException trying to GET " + 
					fileURL,e);
		}

		if(get.getStatusCode()!=200){
			throw new FileNotFoundException(
					"Server returned " + get.getStatusCode());
		}

		// Get the input stream
		BufferedInputStream bis = 
			new BufferedInputStream(get.getResponseBodyAsStream());

		// Read the file and stream it out
		byte[] b = new byte[batchWriteSize];
		int bytesRead = bis.read(b,0,batchWriteSize);
		long bytesTotal = 0;
		while(bytesRead!=-1) {
			bytesTotal += bytesRead;
			out.write(b, 0, bytesRead);
			bytesRead = bis.read(b,0,batchWriteSize);;
		} 
		bis.close(); // Release the input stream.
		out.flush(); 		
	}
}
