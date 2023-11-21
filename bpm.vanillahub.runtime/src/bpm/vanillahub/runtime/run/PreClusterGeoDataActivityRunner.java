package bpm.vanillahub.runtime.run;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.utils.CkanHelper;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanillahub.core.beans.activities.CibleActivity;
import bpm.vanillahub.core.beans.activities.PreClusterGeoDataActivity;
import bpm.vanillahub.runtime.WorkflowProgress;
import bpm.vanillahub.runtime.i18N.Labels;
import bpm.vanillahub.runtime.utils.CibleHelper;
import bpm.workflow.commons.beans.Result;
import bpm.workflow.commons.resources.Cible;

public class PreClusterGeoDataActivityRunner extends ActivityRunner<PreClusterGeoDataActivity> {

	private List<Cible> cibles;

	public PreClusterGeoDataActivityRunner(WorkflowProgress workflowProgress, IVanillaLogger logger, String workflowName, String launcher, PreClusterGeoDataActivity activity, List<Parameter> parameters, List<Variable> variables, boolean isLoop, List<Cible> cibles) {
		super(workflowProgress, logger, workflowName, launcher, activity, parameters, variables, isLoop);
		this.cibles = cibles;
	}

	@Override
	protected void run(Locale locale) {
		String urlServerNode = (String) activity.getUrlServerNode(parameters, variables);
		if (!urlServerNode.endsWith("/")) {
			urlServerNode += "/";
		}
		urlServerNode += "precluster";
		Cible d4c = (Cible) activity.getResource(cibles);

		try {
//			boolean isAvailable = CkanHelper.pingUrl(urlServerNode);
//			
//			if (isAvailable) {
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				nvps.add(new BasicNameValuePair("id", d4c.getCkanPackage().getId()));
	
				HttpPost post = new HttpPost(urlServerNode);
				post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
	
				final SSLConnectionSocketFactory sslsf;
				try {
					sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault(), NoopHostnameVerifier.INSTANCE);
				} catch (NoSuchAlgorithmException e) {
					throw new RuntimeException(e);
				}
	
				final Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create().register("http", new PlainConnectionSocketFactory()).register("https", sslsf).build();
	
				final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
				cm.setMaxTotal(100);
				HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).setConnectionManager(cm).build();
	
				// HttpClient client = HttpClientBuilder.create().build();
				HttpResponse response = httpclient.execute(post);
				HttpEntity entity = response.getEntity();
	
				BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
	
				String line = null;
				StringBuffer sb = new StringBuffer();
				while ((line = reader.readLine()) != null) {
					sb.append(line);
					sb.append("\n");
				}
				reader.close();
				if (response.getStatusLine().getStatusCode() == 500) {
					addError(Labels.getLabel(locale, Labels.Reason) + " : " + sb.toString());
					setResult(Result.ERROR);
				}
				else {
					addInfo(sb.toString());
					setResult(Result.SUCCESS);
				}
//			}
//			else {
//				addError(Labels.getLabel(locale, Labels.Reason) + " : " + Labels.getLabel(locale, Labels.TheNodeUrlIsNotAvailable));
//				setResult(Result.ERROR);
//			}
		} catch (Exception e) {
			e.printStackTrace();

			addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

			setResult(Result.ERROR);

			return;
		}

	}

	@Override
	public List<Variable> getVariables() {
		return activity.getVariables(cibles);
	}

	@Override
	public List<Parameter> getParameters() {
		return activity.getParameters(cibles);
	}

	@Override
	protected void clearResources() {
	}
}
