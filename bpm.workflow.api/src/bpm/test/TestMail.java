package bpm.test;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.URLName;
import javax.mail.internet.MimeBodyPart;

import com.sun.mail.pop3.POP3SSLStore;

public class TestMail {

	public static String receiving_attachments = "C:\\bpm\\test";

	private static void dumpEnvelope(Message m) throws Exception {
		String body = "";
		String path = "";
		int size = 0;
		Object content = m.getContent();
		if(content instanceof String) {
			body = (String) content;
		}
		else if(content instanceof Multipart) {
			Multipart mp = (Multipart) content;
			for(int j = 0; j < mp.getCount(); j++) {
				Part part = mp.getBodyPart(j);
				String disposition = part.getDisposition();
				// System.out.println("test disposition---->>"+disposition);
				if(disposition == null) {
					// Check if plain
					MimeBodyPart mbp = (MimeBodyPart) part;
					if(mbp.isMimeType("text/plain")) {
						body += mbp.getContent().toString();
					}
					else if(mbp.isMimeType("TEXT/HTML")) {
						body += mbp.getContent().toString();
					}
					else {
						// unknown
					}
				}
				else if((disposition != null) && (disposition.equals(Part.ATTACHMENT) || disposition.equals(Part.INLINE) || disposition.equals("ATTACHMENT") || disposition.equals("INLINE"))) {
					// Check if plain
					MimeBodyPart mbp = (MimeBodyPart) part;
					if(mbp.isMimeType("text/plain")) {
						body += (String) mbp.getContent();
					}
					else if(mbp.isMimeType("TEXT/HTML")) {
						body += mbp.getContent().toString();
					}
					else {
						File savedir = new File(receiving_attachments);
						savedir.mkdirs();
						File savefile = new File(savedir + "\\" + part.getFileName());
						path = savefile.getAbsolutePath();
						size = saveFile(savefile, part);

					}
				}
			}
			System.out.println(body);
		}

	}

	public static int saveFile(File saveFile, Part part) throws Exception {

		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(saveFile));

		byte[] buff = new byte[2048];
		InputStream is = part.getInputStream();
		int ret = 0, count = 0;
		while((ret = is.read(buff)) > 0) {
			bos.write(buff, 0, ret);
			count += ret;
		}
		bos.close();
		is.close();
		return count;
	}

	public static void main(String args[]) throws Exception {

		String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

		Properties pop3Props = new Properties();

		pop3Props.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
		pop3Props.setProperty("mail.pop3.socketFactory.fallback", "false");
		pop3Props.setProperty("mail.pop3.port", "995");
		pop3Props.setProperty("mail.pop3.socketFactory.port", "995");

		URLName url = new URLName("pop3", "pop.gmail.com", 995, "", "lanquetin.marc", "2904marco");

		Session session = Session.getInstance(pop3Props, null);
		POP3SSLStore store = new POP3SSLStore(session, url);
		store.connect();

		Folder folder = store.getFolder("INBOX");
		folder.open(Folder.READ_ONLY);

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		// Get directory
		Message message[] = folder.getMessages();
		for(int i = 0, n = message.length; i < n; i++) {
			System.out.println(i + ": " + message[i].getFrom()[0] + "\t" + message[i].getSubject());

			System.out.println("Read message? [YES to read/QUIT to end]");
			String line = reader.readLine();
			if("YES".equalsIgnoreCase(line)) {

				dumpEnvelope(message[i]);

			}
			else if("QUIT".equalsIgnoreCase(line)) {
				break;
			}
		}

		// Close connection
		folder.close(false);
		store.close();

		// String host = "pop.gmail.com";
		// String username = "lanquetin.marc";
		// String password = "2904marco";
		//
		// // Create empty properties
		// Properties props = new Properties();
		//
		// // Get session
		// Session session = Session.getDefaultInstance(props, null);
		//
		// // Get the store
		// Store store = session.getStore("pop3");
		// store.connect(host, username, password);
		//
		// // Get folder
		// Folder folder = store.getFolder("INBOX");
		// folder.open(Folder.READ_ONLY);
		//
		// BufferedReader reader = new BufferedReader(new InputStreamReader(
		// System.in));
		//
		// // Get directory
		// Message message[] = folder.getMessages();
		// for (int i = 0, n = message.length; i < n; i++) {
		// System.out.println(i + ": " + message[i].getFrom()[0] + "\t"
		// + message[i].getSubject());
		//
		// System.out.println("Read message? [YES to read/QUIT to end]");
		// String line = reader.readLine();
		// if ("YES".equalsIgnoreCase(line)) {
		// System.out.println(message[i].getContent());
		// } else if ("QUIT".equalsIgnoreCase(line)) {
		// break;
		// }
		// }
		//
		// // Close connection
		// folder.close(false);
		// store.close();
	}

	// /**
	// * @param args
	// */
	// public static void main(String[] args) throws Exception {
	// Properties props = new Properties();
	// props.setProperty("mail.smtp.host", "smtp.orange.fr");
	//
	// StringBuffer p = new StringBuffer();
	// p.append("mail.smtp.host=" + props.getProperty("mail.smtp.host"));
	//
	// if (props.getProperty("mail.smtp.port") != null) {
	// p.append("&mail.smtp.port=" + props.getProperty("mail.smtp.port"));
	// }
	// if (props.getProperty("mail.user") != null) {
	// p.append("&mail.user=" + props.getProperty("mail.user"));
	// }
	//
	// if (props.getProperty("mail.password") != null) {
	// p.append("&mail.password=" + props.getProperty("mail.password"));
	// }
	//
	// p
	// .append("&fileName=C:/developpement/export/3.4_RC3/vanilla-tomcat-mysql-3.4.0_RC3/webapps/vanilla_files/external_documents/doc_9459322iii.pdf");
	// p.append("&from=ludovic.camus@bpm-conseil.com");
	// p.append("&to=ludovic.camus@gmail.com");
	// p.append("&object=yop");
	// p.append("&message=youyou");
	//
	// String servletUrl = "http://localhost:8080/vanilla/WkfMailSender?"
	// + p.toString();
	//
	// URL url = new URL(servletUrl);
	// HttpURLConnection sock = (HttpURLConnection) url.openConnection();
	//
	// sock.setDoInput(true);
	// sock.setDoOutput(true);
	// sock.setRequestMethod("GET");
	//
	// InputStream is = sock.getInputStream();
	// sock.connect();
	// sock.disconnect();
	//
	// // Session session = Session.getInstance(props, null);
	// //
	// // MimeMessage msg = new MimeMessage(session);
	// //
	// // msg.setFrom(new InternetAddress("ludovic.camus@bpm-conseil.com"));
	// // msg.setRecipient(Message.RecipientType.TO, new
	// // InternetAddress("ludovic.camus@gmail.com"));
	// //
	// // msg.setSubject("yop");
	// //
	// // msg.setSentDate(Calendar.getInstance().getTime());
	// //
	// // MimeMultipart mp = new MimeMultipart();
	// //
	// //
	// // MimeBodyPart mbp1 = new MimeBodyPart();
	// // mbp1.setText("youyou");
	// // mp.addBodyPart(mbp1);
	// //
	// //
	// //
	// // MimeBodyPart mbp2 = new MimeBodyPart();
	// //
	// //
	// // try{
	// // File f = new
	// //
	// File("C:/developpement/export/3.4_RC3/vanilla-tomcat-mysql-3.4.0_RC3/webapps/vanilla_files/external_documents/doc_9459322iii.pdf");
	// // // FileInputStream fis = new FileInputStream(f);
	// // // ByteArrayOutputStream bos = new ByteArrayOutputStream();
	// // //
	// // // int sz = 0;
	// // // byte[] buf = new byte[1024];
	// // //
	// // // while((sz = fis.read(buf)) != -1){
	// // // bos.write(buf, 0, sz);
	// // // }
	// // // fis.close();
	// // // bos.flush();
	// //
	// //
	// // mbp2.setFileName(f.getName());
	// // // mbp2.setContent(bos.toByteArray(), "application/pdf");
	// // ByteArrayDataSource ba = new ByteArrayDataSource(new
	// // FileInputStream(f), "application/pdf");
	// // mbp2.setDataHandler(new DataHandler(ba));
	// //
	// //
	// //
	// // mp.addBodyPart(mbp2);
	// //
	// //
	// // }catch(Exception ex){
	// // ex.printStackTrace();
	// // }
	// //
	// // msg.setContent(mp);
	// //
	// //
	// // Transport.send(msg);
	//
	// }

}
