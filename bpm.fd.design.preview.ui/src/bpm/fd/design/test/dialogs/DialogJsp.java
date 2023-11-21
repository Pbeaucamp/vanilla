package bpm.fd.design.test.dialogs;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.vanilla.platform.core.exceptions.VanillaSessionExpiredException;

public class DialogJsp extends Dialog{
	

	private Browser browser;
	private String url;
	private boolean loadHtml;
	
	public DialogJsp(Shell parentShell, String url, boolean loadHtml) {
		super(parentShell);
		this.url = url;
		this.loadHtml = loadHtml;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		browser = new Browser(parent, SWT.NONE);
		browser.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		if (loadHtml){
			try{
				
//				c.init("http://localhost:9191/generation/_System_ppppiipo/model.jsp", "system", "system");
				
				
				
				URL url =new URL("http://localhost:9191/generation/3_39_System_ppppiipo/model.jsp"); //$NON-NLS-1$
				HttpURLConnection sock = (HttpURLConnection) url.openConnection();
				String authentication = new String(Base64.encodeBase64(new String("system:54b53072540eeeb8f8e9343e71f28176").getBytes())); //$NON-NLS-1$
				sock.setRequestProperty( "Authorization", "Basic " + authentication ); //$NON-NLS-1$ //$NON-NLS-2$
//				writeAdditionalHttpHeader(sock);
				sock.setRequestProperty("Content-type", "text/xml;charset=UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$
				sock.setDoInput(true);
				sock.setDoOutput(true);
				sock.setRequestMethod("GET"); //$NON-NLS-1$
					
				
				String result = null;
				
				InputStream is = sock.getInputStream();
			
				result = IOUtils.toString(is, "UTF-8"); //$NON-NLS-1$
				is.close();
				sock.disconnect();
				
//				extractSessionId(sock);
				//error catching
				if (result.contains("<error>")){ //$NON-NLS-1$
					
					if (result.contains("<error><session>")){ //$NON-NLS-1$
						throw new VanillaSessionExpiredException(result.substring(result.indexOf("<error><session>") + 16, result.indexOf("</session></error>"))); //$NON-NLS-1$ //$NON-NLS-2$
					}
					else{
						throw new Exception(result.substring(result.indexOf("<error>") + 7, result.indexOf("</error>"))); //$NON-NLS-1$ //$NON-NLS-2$
					}
					
					
				}
				
				browser.setText(result);
			}catch(Exception ex){
				browser.setUrl(url);
			}
		}
		else{
			browser.setUrl(url);
		}
		
		return browser;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#initializeBounds()
	 */
	@Override
	protected void initializeBounds() {
		getShell().setSize(800, 600);
	}

	
	
}
