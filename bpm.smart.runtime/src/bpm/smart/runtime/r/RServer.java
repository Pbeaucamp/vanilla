package bpm.smart.runtime.r;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPInteger;
import org.rosuda.REngine.REXPLogical;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REXPRaw;
import org.rosuda.REngine.REXPString;
import org.rosuda.REngine.REXPVector;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import bpm.smart.core.model.RScriptModel;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

public class RServer {

	private String host;
	private int port = 0;
	private RConnection connexion;

	private List<User> users = new ArrayList<User>();

	// private String userEnv;

	String[] nonSupported = { "q()", "ggobi(", "ihist(", "ibar(", "iplot(", "ibox(", "ipcp(", "imosaic(", "plot3d(", "scatter3d(" };
	String[] plots = {};/*
						 * = {"plot(", "qplot(", "ggplot(", "barchart(",
						 * "bwplot(", "cloud(", "contourplot(",
						 * "densityplot(","dotplot(", "histogram(","levelplot(",
						 * "splom(", "stripplot(", "xyplot(", "wireframe(",
						 * "mosaic(", "assoc(", "corrgram(", "grid.arrange(",
						 * "clusplot("};
						 */
	Map<String, String> varEnv = new HashMap<String, String>();

	private Queue<String> tempNewLineTable = new CircularFifoQueue<String>(5);;

	// private boolean socialConnected = false;

	public RServer() {
		getGraphicsHandlers();
	}

	public RServer(int port) {
		this.port = port;
		getGraphicsHandlers();
	}

	public RServer(String url) {
		String[] parts = url.split(":");
		if (parts.length == 2) {
			host = parts[0];
			port = Integer.parseInt(parts[1]);
		}
		else {
			try {
				port = Integer.parseInt(parts[0]);
			} catch (NumberFormatException e) {
				host = parts[0];
			}
		}
		getGraphicsHandlers();
		// connect("");
	}

	public boolean connect(String userEnv) {
		try {
			if (host != null && port != 0) {
				connexion = new RConnection(host, port);
			}
			else if (host != null) {
				connexion = new RConnection(host);
			}
			else if (port != 0) {
				connexion = new RConnection("localhost", port);
			}
			else {
				connexion = new RConnection();
			}

			this.tempNewLineTable = new CircularFifoQueue<String>(5);
			/* ajouté par kevin pour env */
			if (!connexion.equals(null)) { // mise a jour des variables de
											// l'environnement, execute un
											// script, donc potentiellement
											// fermeture connexion ... :( donc
											// retest de connexion derriere
				// this.userEnv = userEnv;
				RScriptModel model = new RScriptModel();
				String script = "if(!exists('" + userEnv + "')){\n";
				script += userEnv + "<- new.env()\n";
				script += "}\n";
				// script+= "manual_result <- ls()";
				// script +=
				// "f<-function(x){paste(x,class(get(x)), sep=';')}\n";
				// script += "environment(f) <- "+userEnv +"\n";
				// script += "manual_result <- sapply(ls(),f)\n";
				// script += "rm(f)";
				model.setScript(script);
				model.setOutputs("no_return".split(" "));
				model.setUserREnv("");
				model = scriptEval(model);
				// if(model.getOutputVarstoString().size() < 1 ||
				// (model.getOutputVarstoString().get(0)).length() < 1){
				// varEnv = new HashMap<String, String>();
				// } else {
				// varEnv = new HashMap<String, String>();
				// for(String var :
				// model.getOutputVarstoString().get(0).split("\t")){
				// varEnv.put(var.split(";")[0].trim(),
				// var.split(";")[1].trim());
				// }
				// if(varEnv.containsKey(userEnv.toString())){ //protection
				// varEnv.remove(userEnv);
				// }
				// }

			}

			// if(!socialConnected){
			socialNetworkInit();
			// }

			this.tempNewLineTable = new CircularFifoQueue<String>(5);

			return true;
		} catch (RserveException e1) {
			System.out.println(ExceptionUtils.getStackTrace(e1).replace("&nbsp;", " "));
			return false;

		}
	}

	public void deconnect() {
		connexion.close();
	}

	public boolean testconnect() {
		return !(connexion == null || !connexion.isConnected());
	}

	public RScriptModel scriptEval(RScriptModel box) {
		String userEnv = box.getUserREnv();
		if ((connexion == null || !connexion.isConnected()) && !userEnv.equals("")) {
			connect(userEnv);
		}

		if (box.getOutputs() == null) {
			box.setOutputs("no_return".split(" "));
		}

		boolean error = false;
		List<Boolean> wait = new ArrayList<Boolean>();
		wait.add(false);
		String waitstring = "";
		String log = "";
		Serializable output_var = null;
		String output_var_to_string = "";
		List<Serializable> output_vars = new ArrayList<Serializable>();
		List<String> output_vars_to_string = new ArrayList<String>();
		String[] lines = box.getScript().replace("\r", "").split("\n");
		int i = 0;
		try {
			if (box.getInputVars() != null) {
				for (String var : box.getInputVars().keySet()) { // insertion
																	// des
																	// variables
																	// d'entrée
					connexion.assign(userEnv + "$" + var, (REXP) box.getInputVars().get(var));
					// connexion.assign(var, (REXP)box.getInputVars().get(var));
				}
			}// TOTEST

			for (String line : lines) { // execution du script
				//log += "<p>> " + line + "</p>";
				i++; // ligne actuelle = lines[i-1], ligne suivante = lines[i]
				if (wait.get(wait.size() - 1)) { // si une accolade est ouverte
					if (line.contains("}")) { // si une accolade se referme
						if (line.contains("else")) {
							waitstring += line.replace("\n", "");
							continue;//
						}
						else if (wait.size() > 1) { // si ce n'est pas la
													// derniere ouverte
							waitstring += line.replace("\n", "") + ";";
							wait.remove(wait.size() - 1);

							continue;
						}
						else {
							waitstring += line;
							if (i < lines.length && lines[i].trim().startsWith("else")) { // au
																							// cas
																							// ou
																							// il
																							// faille
																							// rattacher
																							// un
																							// else/else
																							// if
																							// à
																							// son
																							// if
																							// précedent
								wait.set(0, false);
								continue;
							}
						}

					}
					else {
						if (line.contains("{") && !line.contains("}")) { // si
																			// une
																			// nouvelle
																			// accolade
																			// s'ouvre
																			// TODO
																			// si
																			// ligne
																			// suivante
																			// débute
																			// par
																			// une
																			// accolade
							if (line.contains("else")) {
								waitstring = waitstring.substring(0, waitstring.length() - 1);
							}
							wait.add(true);
							waitstring += line.replace("\n", "");
							continue;
						}
						else { // on est encore dans une boucle ou if
							waitstring += line.replace("\n", "") + ";";
							continue;
						}

					}
				}
				else if (line.contains("{") && !line.contains("}")) { // pas
																		// encore
																		// d'accolade
																		// ouverte
																		// TODO
																		// si
																		// ligne
																		// suivante
																		// débute
																		// par
																		// une
																		// accolade
					wait.clear();
					wait.add(true);
					waitstring += line.replace("\n", "");
					continue;
				}

				String result;
				if (wait.size() < 2 && wait.get(0)) {
					result = lineEval(waitstring, userEnv);
					wait.set(0, false);
					waitstring = "";
				}
				else {
					result = lineEval(line, userEnv);
					waitstring = "";
				}
				// result = allScriptEval(box.getScript(), userEnv);

				String[] tab = result.split("&_&");
				if (tab[0].equals("fatal")) {
					log += "<p style='color:red'>> " + tab[1] + "</p>";
					error = true;
					// break;
				}
				else if (tab[0].equals("error")) {
					log += "<p style='color:red'>> " + tab[1] + "</p>";
					error = true;
				}
				else if (tab[0].equals("success-file")) {
					if (tab.length > 1) {
						log += "<p style='color:green'>" + tab[1] + "</p>";

						if (box.getOutputFiles() == null) {
							// byte[][] bytes = {tab[2].getBytes()};
							String[] first = { tab[2] };
							box.setOutputFiles(first);
						}
						else {
							box.setOutputFiles((String[]) ArrayUtils.add(box.getOutputFiles(), box.getOutputFiles().length, tab[2]));
						}

					}

				}
				else if (tab[0].equals("unsupported")) {
					log += "<p style='color:red'>> A command is forbidden or unsupported</p>";
					error = true;
				}
				else {
					if (tab.length > 1) {
						log += "<p style='color:green'>" + tab[1] + "</p>";
					}

				}

			}

			if (error || box.getOutputs()[0].equals("no_return")) {
				// on sort
			}
			else { // récuperation des variables de sortie
				for (String output : box.getOutputs()) {
					// connexion.assign(".tmp.", output);
					String key = new Date().getTime() + "";
					connexion.assign(".tmpframe.", output.getBytes());
					connexion.assign(".tmp.", "writeBin(object=.tmpframe., con='script" + key + ".R');source('script" + key + ".R', echo=FALSE, local=" + ((userEnv.equals("")) ? "FALSE" : userEnv) + ",encoding = 'UTF-8')");
					// connexion.assign(".tmp.", ((userEnv.equals(""))? "" :
					// userEnv+"$" ) + output);
					// connexion.assign(".tmp.", output);
					REXP res = connexion.parseAndEval("try(eval(parse(text=.tmp.)),silent=TRUE)");
					connexion.eval("file.remove('script" + key + ".R' )");
					if (res.inherits("try-error")) {
						System.err.println("Error: " + res.asString());
						log += "<p style='color:red'>> " + res.asString() + "</p>";
					}
					else {
						output_var = getOutputVar(res);
						try {
							output_var_to_string = getOutputStream(res, userEnv + "$" + output);
							// output_var_to_string = getOutputStream(res,
							// output);
						} catch (Exception e) {
							System.out.println(ExceptionUtils.getStackTrace(e).replace("&nbsp;", " "));
						}
						if (!res.isRaw()) // ne pas afficher tous les caracteres
											// d'un fichier
							log += "<p style='color:green'>" + output_var_to_string + "</p>";
						output_vars.add(output_var);
						output_vars_to_string.add(output_var_to_string);
					}
				}

			}
		} catch (Exception e) {

			System.out.println(ExceptionUtils.getStackTrace(e).replace("&nbsp;", " "));
			connexion.close();
		} finally {
			// connexion.close();
		}

		box.setScriptError(error);
		box.setOutputLog(log);
		box.setOutputVars(output_vars);
		box.setOutputVarstoString(output_vars_to_string);

		return box;
	}

	public String lineEval(String line, String userEnv) throws Exception {

		if ((connexion == null || !connexion.isConnected()) && !userEnv.equals("")) {
			connect(userEnv);
		}

		String error = "";
		String oldLine = line;
		line = adaptLine(line, userEnv);

		String result = "";
		REXP res = null;
		String key = new Date().getTime() + "";
		if (line.equals("manual_result")) { // sortie manuelle
			// connexion.assign(".tmp.", line);
			connexion.assign(".tmpframe.", line.getBytes(StandardCharsets.UTF_8));
			connexion.assign(".tmp.", "writeBin(object=.tmpframe., con='script" + key + ".R');source('script" + key + ".R', echo=TRUE, local=" + ((userEnv.equals("")) ? "FALSE" : userEnv) + ",encoding = 'UTF-8')");
			res = connexion.parseAndEval("try(eval(parse(text=.tmp.)),silent=TRUE)");
			// REXP res = connexion.eval(".tmp.");
			connexion.eval("file.remove('script" + key + ".R' )");
			result = getOutputStream(res, line);
		}
		else { // sortie console
				// String newline =
				// "paste(capture.output("+((line.startsWith("(")
				// && line.endsWith(")")? "print"+line :
				// line))+"), sep=\"\t\", collapse='<br>')";

			try {
				connexion.assign(".tmpframe.", line.getBytes(StandardCharsets.UTF_8));
				connexion.assign(".tmp.", "writeBin(object=.tmpframe., con='script" + key + ".R');paste(capture.output(source('script" + key + ".R', echo=TRUE, local=" + ((userEnv.equals("")) ? "FALSE" : userEnv) + ",encoding = 'UTF-8')), sep=\"\t\", collapse='<br>')");
				res = connexion.parseAndEval("try(eval(parse(text=.tmp.)),silent=TRUE)");
				// connexion.assign(".tmp.", newline);
				connexion.eval("file.remove('script" + key + ".R' )");
			} catch (Exception e) {
				connexion.close();
				e.printStackTrace();
				throw new Exception("Operation failed - " + e.getMessage(), e);
			}
			result = getOutputStream(res, line);
		}

		if (result.contains("Error")) {
			System.err.println("Error: " + result.replace("&nbsp;", " "));
			error = "error";
			if (line.contains("help(") || line.contains("?")) {
				result = "Error: This library or this function has no help HTML file !";
			}
			if (result.contains(("objet '" + userEnv + "' introuvable").replace(" ", "&nbsp;")) || result.contains(("objet '" + userEnv + "' introuvable"))) {
				result = "Error: The connection was interrupted, run the script again";
				connexion.close();
			}
		}
		else {
			boolean bfind = false;
			for (String match : nonSupported) {
				if (oldLine.contains(match)) {
					error = "unsupported";
					bfind = true;
				}
			}
			// for(String match : plots){
			// if(oldLine.contains(match)){
			if (line.contains("varGraphManual<-readBin")) {
				String tempStream = "";
				connexion.assign(".tmp.", userEnv + "$" + "varGraphManual");
				// connexion.assign(".tmp.", "varGraphManual");
				res = connexion.parseAndEval("try(eval(parse(text=.tmp.)),silent=TRUE)");
				// tempStream = "data:image/svg+xml;base64,"+
				// Base64.encodeBase64String(res.asBytes());
				tempStream = new String(res.asBytes());
				tempStream = tempStream.substring(tempStream.indexOf("<svg"));
				error = "success-file";
				result += "&_&" + tempStream;
				bfind = true;
				// }
			}
			if (!bfind) {
				if (line.contains("library(help")) {
					String tempStream = "";
					connexion.assign(".tmp.", userEnv + "$" + "r");
					// connexion.assign(".tmp.", "r");
					res = connexion.parseAndEval("try(eval(parse(text=.tmp.)),silent=TRUE)");
					try {
						tempStream = "txt;" + IOUtils.toString(res.asBytes(), "UTF-8");
					} catch (IOException e) {
						System.out.println(ExceptionUtils.getStackTrace(e).replace("&nbsp;", " "));
					}
					error = "success-file";
					result += "&_&" + tempStream;
				}
				else if (line.contains("help(") || line.contains("?")) {
					String tempStream = "";
					connexion.assign(".tmp.", userEnv + "$" + "r");
					// connexion.assign(".tmp.", "r");
					res = connexion.parseAndEval("try(eval(parse(text=.tmp.)),silent=TRUE)");
					try {
						tempStream = "html;" + IOUtils.toString(Base64.encodeBase64(res.asBytes()), "UTF-8");
					} catch (IOException e) {
						System.out.println(ExceptionUtils.getStackTrace(e).replace("&nbsp;", " "));
					}
					error = "success-file";
					result += "&_&" + tempStream;
				}
				else {
					error = "success";
				}
			}

		}

		result = error + "&_&" + result;

		return result;
	}

	// public String allScriptEval(String script, String userEnv) throws
	// Exception {
	//
	// if((connexion == null || !connexion.isConnected()) &&
	// !userEnv.equals("")){
	// connect(userEnv);
	// }
	//
	// String oldScript = new String(script);
	// script = adaptScript(script, userEnv);
	//
	// String error = "";
	// String result = "";
	// REXP res = null;
	// String key = new Date().getTime() +"";
	//
	// try {
	// connexion.assign(".tmpframe.", script.getBytes());
	// connexion.assign(".tmp.",
	// "writeBin(object=.tmpframe., con='script"+key+".R');paste(capture.output(source('script"+key+".R', echo=TRUE, local="+
	// ((userEnv.equals(""))? "FALSE" : userEnv)
	// +",encoding = 'UTF-8')), sep=\"\t\", collapse='<br>')" );
	// res = connexion.parseAndEval("try(eval(parse(text=.tmp.)),silent=TRUE)");
	// // connexion.assign(".tmp.", newline);
	// connexion.eval("file.remove('script"+key+".R' )");
	// } catch (Exception e) {
	// connexion.close();
	// e.printStackTrace();
	// throw new Exception("Operation failed - " + e.getMessage(), e);
	// }
	// result = getOutputStream(res, script);
	//
	//
	// if (result.contains("Error")){
	// System.err.println("Error: "+result.replace("&nbsp;", " "));
	// error = "error";
	//
	// } else {
	// boolean bfind = false;
	// for(String match : nonSupported){
	// if(oldScript.contains(match)){
	// error = "unsupported";
	// bfind = true;
	// }
	// }
	// // for(String match : plots){
	// // if(oldLine.contains(match)){
	// if(script.contains("varGraphManual<-readBin")){
	// String tempStream = "";
	// connexion.assign(".tmp.", userEnv+"$"+"varGraphManual");
	// // connexion.assign(".tmp.", "varGraphManual");
	// res = connexion.parseAndEval("try(eval(parse(text=.tmp.)),silent=TRUE)");
	// //tempStream = "data:image/svg+xml;base64,"+
	// Base64.encodeBase64String(res.asBytes());
	// tempStream = new String(res.asBytes());
	// tempStream = tempStream.substring(tempStream.indexOf("<svg"));
	// error = "success-file";
	// result += "&_&"+ tempStream;
	// bfind = true;
	// // }
	// }
	// if(!bfind){
	// if(script.contains("library(help")){
	// String tempStream = "";
	// connexion.assign(".tmp.", userEnv+"$"+"r");
	// // connexion.assign(".tmp.", "r");
	// res = connexion.parseAndEval("try(eval(parse(text=.tmp.)),silent=TRUE)");
	// try {
	// tempStream = "txt;"+ IOUtils.toString(res.asBytes(), "UTF-8");
	// } catch (IOException e) {
	// System.out.println(ExceptionUtils.getStackTrace(e).replace("&nbsp;",
	// " "));
	// }
	// error = "success-file";
	// result += "&_&"+ tempStream;
	// } else if(script.contains("help(") || script.contains("?")){
	// String tempStream = "";
	// connexion.assign(".tmp.", userEnv+"$"+"r");
	// // connexion.assign(".tmp.", "r");
	// res = connexion.parseAndEval("try(eval(parse(text=.tmp.)),silent=TRUE)");
	// try {
	// tempStream = "html;"+
	// IOUtils.toString(Base64.encodeBase64(res.asBytes()), "UTF-8");
	// } catch (IOException e) {
	// System.out.println(ExceptionUtils.getStackTrace(e).replace("&nbsp;",
	// " "));
	// }
	// error = "success-file";
	// result += "&_&"+ tempStream;
	// }
	// else {
	// error = "success";
	// }
	// }
	//
	// }
	//
	// result = error +"&_&" + result;
	//
	//
	// return result;
	// }

	private Serializable getOutputVar(REXP res) throws REXPMismatchException {
		Serializable output = null;
		if (res.isComplex()) {
			// nothing complex not treated by rserve
		}
		else if (res.isEnvironment()) {
			// nothing rocognize as unknown
		}
		else if (res.isExpression()) {
			output = res.asList();
		}
		else if (res.isFactor()) {
			output = res.asFactor().asStrings();
		}
		else if (res.isInteger()) {
			output = res.asIntegers();
		}
		else if (res.isLanguage()) {
			output = res.asList();
		}
		else if (res.isList()) {
			List list = new ArrayList();

			RList truc = res.asList();
			try {
				return (Serializable) getList(truc);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (res.isLogical()) {
			output = res.asIntegers();
		}
		else if (res.isNull()) {
			output = null;
		}
		else if (res.isNumeric()) {
			output = res.asDoubles();
		}
		else if (res.isPairList()) {
			output = (Serializable) res.asNativeJavaObject();
		}
		else if (res.isRaw()) {
			output = res.asBytes();
		}
		else if (res.isRecursive()) {
			output = (Serializable) res.asNativeJavaObject();
		}
		else if (res.isReference()) {
			output = (Serializable) res.asNativeJavaObject();
		}
		else if (res.isString()) {
			output = res.asStrings();
		}
		else if (res.isSymbol()) {
			output = (Serializable) res.asNativeJavaObject();
		}
		else if (res.isVector()) {
			output = (Serializable) res.asNativeJavaObject();
		}
		else { // unknown
			output = (Serializable) res.asNativeJavaObject();
		}
		return output;
	}

	private List<Object> getList(RList list) throws Exception {
		List<Object> listJava = new ArrayList<Object>();

		for (int i = 0; i < list.size(); i++) {
			Object o = list.get(i);
			addItem(listJava, o);
		}
		return listJava;
	}
	
	@SuppressWarnings("unchecked")
	private void addItem(List<Object> items, Object o) throws REXPMismatchException {
		if (o instanceof REXPString) {
			items.addAll(Arrays.asList(((REXPString) o).asStrings()));
		}
		else if (o instanceof REXPDouble) {
			try {
				double[][] matrix = ((REXPDouble) o).asDoubleMatrix();
				for (int a = 0; a < matrix.length; a++) {
					items.add(new ArrayList<String>());
					for (int b = 0; b < matrix[a].length; b++) {
						((List<String>) items.get(a)).add(matrix[a][b] + "");
					}
				}
			} catch (Exception e) {

				try {
					double[] matrix = ((REXPDouble) o).asDoubles();
					for (int a = 0; a < matrix.length; a++) {
						items.add(matrix[a] + "");
					}
				} catch (Exception e1) {
					items.add(((REXPDouble) o).asDouble());
				}
			}
		}
		else if (o instanceof REXPInteger) {
			try {
				double[][] matrix = ((REXPInteger) o).asDoubleMatrix();
				for (int a = 0; a < matrix.length; a++) {
					items.add(new ArrayList<String>());
					for (int b = 0; b < matrix[a].length; b++) {
						((List<String>) items.get(a)).add(matrix[a][b] + "");
					}
				}
			} catch (Exception e) {
				try {
					int[] matrix = ((REXPInteger) o).asIntegers();
					for (int a = 0; a < matrix.length; a++) {
						items.add(matrix[a] + "");
					}
				} catch (Exception e1) {
					items.add(((REXPInteger) o).asInteger());
				}

			}
		}
		else if (o instanceof REXPLogical) {

		}
		else if (o instanceof REXPRaw) {
			items.add(((REXPRaw) o).asBytes());
		}
		else if (o instanceof REXPVector) {
			
			RList subList = ((REXPVector) o).asList();
			for (int index = 0; index < subList.size(); index++) {
				List<Object> subItems = new ArrayList<Object>();
				
				Object item = subList.get(index);
				addItem(subItems, item);
				
				items.add(subItems);
			}
		}
	}

	private String getOutputStream(REXP res, String lineRequest) throws Exception {
		String result = " ";
		if (lineRequest.contains("manual_") && res != null) { // temporaire ...
			if (res.isComplex()) {
				// nothing complex not treated by rserve
			}
			else if (res.isEnvironment()) {
				// nothing rocognize as unknown
			}
			else if (res.isExpression()) {
				result = lineEval("eval(" + lineRequest + ")", "").split(";")[1]; // TOTEST
			}
			else if (res.isFactor()) {
				String[] tab = res.asFactor().asStrings();
				for (String s : tab) {
					result += s + "\t";
				}
			}
			else if (res.isInteger()) {
				int[] tab = res.asIntegers();
				for (int i : tab) {
					result += i + "\t";
				}
			}
			else if (res.isLanguage()) {
				RList list = res.asList();
				for (int i = 0; i < list.size(); i++) {
					result += list.get(i).toString();
				}
			}
			else if (res.isList()) {
				RList list = res.asList();
				for (int i = 0; i < list.size(); i++) {
					result += getOutputStream((REXP) list.get(i), "manual_result");
				}
			}
			else if (res.isNull()) {
				result = "null";
			}
			else if (res.isNumeric()) {
				double[] tab = res.asDoubles();
				for (double s : tab) {
					result += s + "\t";
				}
			}
			else if (res.isLogical()) {
				int[] tab = res.asIntegers();
				for (int i : tab) {
					result += i + "\t";
				}
			}
			else if (res.isPairList()) {

			}
			else if (res.isRaw()) {

			}
			else if (res.isRecursive()) {

			}
			else if (res.isRecursive()) {

			}
			else if (res.isReference()) {

			}
			else if (res.isString()) {
				String[] tab = res.asStrings();
				for (String s : tab) {
					result += s.trim() + "\t";
				}
			}
			else if (res.isSymbol()) {

			}
			else if (res.isVector()) {

			}
		}
		else {
			if (res.isString()) {
				// if(res.asString().length() > 10000){
				// result = new String(res.asString()).substring(0, 10000) +
				// " [...] ";
				// } else {
				result = new String(res.asString());
				// }

			}
			else if (res.isRaw()) {
				if (res.asBytes().length > 10000) {
					result = new String(res.asBytes()).substring(0, 10000) + " [...] ";
				}
				else {
					result = new String(res.asBytes());
				}
				// result = new String(res.asBytes());
			}
			else if (res.isVector()) {
				if(((REXP) res.asList().get(0)).isString()){
					if (((REXP) res.asList().get(0)).asString().length() > 10000) {
						result = new String(((REXP) res.asList().get(0)).asString().substring(0, 10000) + " [...] ");
					}
					else {
						result = new String(((REXP) res.asList().get(0)).asString());
					}
				}
			}

			result = result.replace(" ", "&nbsp;");
		}

		return result;
	}

	public String adaptLine(String line, String userEnv) {
		String newLine = line;
		boolean bfind = false;

		// traitement , ou ; pour le capture.output()
		String sep = ";";
		// if(line.contains("{") || line.contains("}")){
		// sep = ";";
		// } else {
		// sep = ",";
		// }

		// /////////////////////// UNSUPPORTED
		// /////////////////////////////////////
		// si la ligne contient une fonction non suportée --> on skip
		for (String match : nonSupported) {
			if (line.contains(match)) {
				newLine = "";
				return newLine;
			}
		}
		// ////////////////////////////////////////////////////////////////////////

		// /////////////////////// Comments
		// /////////////////////////////////////
		String regcom = "(#.*" + sep + ")|(#.*)"; // Variable Name 1
		Pattern pcom = Pattern.compile(regcom);
		Matcher mcom = pcom.matcher(newLine);
		StringBuffer bufcom = new StringBuffer();
		while (mcom.find()) {
			// String var1=mcom.group(1);
			if (!(mcom.start() != 0 && (newLine.charAt(mcom.start() - 1) == '\"' || newLine.charAt(mcom.start() - 1) == '\''))) {
				mcom.appendReplacement(bufcom, "");
			}

		}
		mcom.appendTail(bufcom);
		newLine = bufcom.toString();

		// ////////////////////////////////////////////////////////////////////////

		// ///////////////////////// PLOT AND GRAPHS
		// /////////////////////////////////////
		// on capte les fonctions graphiques pour recuperer les images

		Set<String> plotConstructiontoReplace = new HashSet<String>();
		Set<String> plotConstructiontoAppend = new HashSet<String>();
		int deb = 0, fin = 0;
		for (String match : plots) {
			if (line.contains(match)) {
				String strPlot = match.replace("(", "\\(");
				strPlot = strPlot.replace(".", "\\.");
				// String reg = "(\\b" + strPlot + "([^)]+)\\))";
				String reg = "(\\b" + strPlot + "((\\((\\(.*?\\)|.)*?\\)|.)*?)\\))"; // (\bclusplot\(((\((\(.*?\)|.)*?\)|.)*?\)))
				Pattern p = Pattern.compile(reg);
				Matcher m = p.matcher(newLine);
				StringBuffer buf = new StringBuffer();

				while (m.find()) {

					String var1 = m.group(1);
					String var2 = m.group(2);
					if ((m.start() != 0 && (newLine.substring(m.start() - 1, m.start())).equals(".")) || (m.end() != newLine.length() && (newLine.substring(m.end(), m.end() + 1)).equals("."))) { // ce
																																																	// n'est
																																																	// pas
																																																	// la
																																																	// bonne
																																																	// variable
						continue;
					}
					if (!(m.start() != 0 && (newLine.substring(0, m.start()).trim()).endsWith("<-"))) { // si
																										// le
																										// plot
																										// n'est
																										// pas
																										// stocké
																										// dans
																										// une
																										// variable
						if (!(m.start() != 0 && ((newLine.substring(0, m.start()).trim()).endsWith("+") || (newLine.substring(m.end()).trim()).startsWith("+")))) { // si
																																									// c'est
																																									// un
																																									// plot
																																									// direct
							String groupReplacement = "", whichparameter = "";
							String elem = var2;
							if (elem.contains(",") && !elem.contains("("))
								elem = elem.substring(0, elem.indexOf(","));
							if (strPlot.equals("plot\\(") && var1.contains("lm(")) {
								if (!var2.contains("which"))
									whichparameter = ", which=1:6"; // TODO algo
																	// pour
																	// recuperer
																	// la value
																	// du param
																	// which
																	// pour en
																	// sortir un
																	// layout
																	// adapté
								var1 = "layout(matrix(c(1,2,3,4,5,6),2,3))" + sep + var1.substring(0, var1.indexOf("lm(")) + var2.substring(var2.indexOf("lm(")) + whichparameter + ", ask=FALSE)";
							}
							if (strPlot.equals("ggplot\\(")) {
								// var1 = var1.substring(0,
								// var1.indexOf(strPlot)+ strPlot.length()) +
								// var2 + ", environment=" + userEnv +
								// var1.substring(var1.indexOf(var2) +
								// var2.length());
								if (var2.equals("")) {
									var1 = var1.substring(0, var1.indexOf(strPlot) + strPlot.length()) + "environment=" + userEnv + ")";
								}
								else {
									var1 = var1.substring(0, var1.indexOf(strPlot) + strPlot.length()) + var2 + ", environment=" + userEnv + var1.substring(var1.indexOf(var2) + var2.length());
								}
							}
							// if(strPlot.equals("plot\\(") &&
							// (varEnv.containsKey(elem) &&
							// varEnv.get(elem).contains("lm"))){
							// if(!var2.contains("which")) whichparameter =
							// ", which=1:6";
							// var1 = "layout(matrix(c(1,2,3,4,5,6),2,3))" + sep
							// + var1.substring(0,var1.indexOf(elem)) +
							// var2.substring(var2.indexOf(elem)) +
							// whichparameter +", ask=FALSE)";
							// }
							groupReplacement = generateLinePlot(var1, sep);

							m.appendReplacement(buf, groupReplacement.replace("$", "\\$"));
						}
						else {// si c'est une construction de plot
								// recup part of line with '+'
							String partline = "";
							String firstpart = newLine.substring(0, m.start());
							String reg3 = "(\\(.*(" + sep + ").*\\)|\\[.*(" + sep + ").*\\])";
							Pattern p3 = Pattern.compile(reg3);
							Matcher m3 = p3.matcher(firstpart);
							while (m3.find()) {
								String varice = m3.group(1);// craquage
								String varane = varice.replaceAll(sep, "!!VirGulE!!");
								firstpart = firstpart.replace(varice, varane);
							}
							if (firstpart.contains(sep)) {
								partline = firstpart.substring(firstpart.lastIndexOf(sep));
							}
							else {
								partline = firstpart;
							}
							partline = partline.replaceAll("!!VirGulE!!", sep);
							partline += newLine.substring(m.start(), m.end());
							String lastpart = newLine.substring(m.end());
							if (lastpart.contains(sep)) {
								partline += lastpart.substring(0, firstpart.indexOf(sep));
							}
							else {
								partline += lastpart;
							}
							plotConstructiontoReplace.add(partline);
							String[] tab = partline.split("\\+");
							List<String> plotvars = new ArrayList<String>();
							partline = "";
							// for (Entry<String, String> entry :
							// varEnv.entrySet()) {
							// if
							// (Arrays.asList(plots).contains(entry.getValue()+"("))
							// {
							// plotvars.add(entry.getKey());
							// }
							// }
							tabloop: for (String part : tab) {
								for (String plot : plots) {
									if (line.contains(plot)) {
										partline += (partline.length() > 0) ? " + " + part : part;
										continue tabloop;
									}
								}
								for (String plot : plotvars) {
									if (line.contains(plot)) {
										partline += (partline.length() > 0) ? " + " + part : part;
										continue tabloop;
									}
								}
							}

							String groupReplacement = generateLinePlot(partline, sep);
							// m.appendReplacement(buf, groupReplacement);
							plotConstructiontoAppend.add(groupReplacement);

						}
					}
					else { // si le plot est placé dans une variable
						String reg2 = "\\b(\\w+)\\s*<-";
						Pattern p2 = Pattern.compile(reg2);
						Matcher m2 = p2.matcher(newLine.substring(0, m.start()));
						String var = "";
						while (m2.find()) {
							var = m2.group(1);
						}
						varEnv.put(var, match.replace("(", "")); // XXX on
																	// laisse
																	// juste ce
																	// system
																	// d'enregistrement
																	// de
																	// variable
																	// plour le
																	// plot
																	// multi
																	// lignes

						if (strPlot.equals("ggplot\\(")) {
							String groupReplacement = "";
							if (var2.equals("")) {
								groupReplacement = var1.substring(0, var1.indexOf(strPlot) + strPlot.length()) + "environment=" + userEnv + ")";
							}
							else {
								groupReplacement = var1.substring(0, var1.indexOf(strPlot) + strPlot.length()) + var2 + ", environment=" + userEnv + var1.substring(var1.indexOf(var2) + var2.length());
							}
							m.appendReplacement(buf, groupReplacement.replace("$", "\\$"));
						}

					}
				}
				m.appendTail(buf);
				newLine = buf.toString();
				bfind = true;

			}
		}
		for (int i = 0; i < plotConstructiontoReplace.size(); i++) {
			newLine = newLine.replace((String) plotConstructiontoReplace.toArray()[i], (String) plotConstructiontoAppend.toArray()[i]);
		}
		// ////////////////////////////////////////////////////////////////////////

		// /////////////////////////// HELP + LS + objects
		// //////////////////////////////
		if (!bfind) {
			// on capte les help
			if (line.contains("library(help")) {
				String tempFile = new Date().getTime() + ".txt";
				// r <- library(help = datasets)
				// txt <- c(paste("\t\tInformation sur le package", r$name),
				// "\nDescription :\n", r$info[[1]], "\nIndex:\n", r$info[[2]])
				// write(txt, "temp/datasets.txt", sep="\t")
				newLine = "r <- " + line + ";";
				newLine += "txt <- c(paste(\"\t\tInformation sur le package\", r$name), \"\nDescription :\n\", r$info[[1]], \"\nIndex:\n\", r$info[[2]]);";
				newLine += "write(txt, '" + tempFile + "', sep=\"\t\"); r<-readBin('" + tempFile + "','raw',1024*1024); file.remove('" + tempFile + "')";
			}
			else if (line.contains("help(") || line.contains("?")) {

				String elem;
				if (line.contains("help(")) {
					elem = line.substring(line.indexOf("help(") + 5, (line.indexOf(")", line.indexOf("help(") + 5)));
					if (elem.contains(","))
						elem = elem.substring(0, elem.indexOf(","));
				}
				else {
					elem = line.substring(line.indexOf("?") + 1, line.length());
				}
				// //help(<nom fonction>, help_type="pdf")
				// newLine = "help("+ elem +", help_type='pdf'),";
				newLine = "library(tools); r<-utils:::.getHelpFile(help('" + elem + "'));";
				newLine += "Rd2HTML(r,'" + elem + ".html');";
				newLine += "r<-readBin('" + elem + ".html','raw',1024*1024); file.remove('" + elem + ".html')";

			}
			else if (line.split("\\bls\\(").length > 1) {
				// else if(line.contains("ls(")){
				String elem;
				elem = newLine.substring(newLine.indexOf("ls(") + 3, (newLine.indexOf(")", newLine.indexOf("ls(") + 3)));
				if (elem.equals("")) {
					elem = "envir=" + userEnv;
				}
				else {
					elem = "envir=" + userEnv + "," + elem;
				}
				newLine = newLine.substring(0, newLine.indexOf("ls(") + 3) + elem + newLine.substring(newLine.indexOf(")", newLine.indexOf("ls(") + 3));

			}
			else if (line.contains("objects(")) {
				String elem;
				elem = newLine.substring(newLine.indexOf("objects(") + 8, (newLine.indexOf(")", newLine.indexOf("objects(") + 8)));
				if (elem.equals("")) {
					elem = "envir=" + userEnv;
				}
				else {
					elem = "envir=" + userEnv + "," + elem;
				}
				newLine = newLine.substring(0, newLine.indexOf("objects(") + 8) + elem + newLine.substring(newLine.indexOf(")", newLine.indexOf("objects(") + 8));

			}
		}
		// ////////////////////////////////////////////////////////////////////////

		// ///////////////////////////// ENVIRONNEMENT
		// /////////////////////////////////
		// //traitement variables vers environnement
		// //String res = newLine.replaceAll("(\\w+)\\s*<-",
		// userEnv+"$1"+" <-");
		// String reg = "\\b([\\w|.]+\\s*<-)";
		// Pattern p = Pattern.compile(reg);
		// Matcher m = p.matcher(newLine);
		// StringBuffer buf = new StringBuffer();
		// while (m.find())
		// {
		// String var1=m.group(1);
		// if(!var1.contains(userEnv.toString()) && !(m.start()!=0 &&
		// ((newLine.substring(m.start()-1, m.start())).equals("$") ||
		// (newLine.substring(m.start()-1, m.start())).equals(".")) )){
		// //newLine = newLine.replace(var1, userEnv+"$"+var1);
		// m.appendReplacement(buf, userEnv+ m.quoteReplacement("$") +var1);
		// if(!varEnv.containsKey(var1.split("<-")[0].trim())){
		// varEnv.put(var1.split("<-")[0].trim(),"");
		// }
		//
		// }
		//
		// }
		// m.appendTail(buf);
		// newLine = buf.toString();
		//
		for (String var : varEnv.keySet()) {
			// //String re1="\\b(\\w*[\\.]?" + var + ")\\b"; // Variable Name 1
			String re1 = "\\b(" + var + ")\\b"; // Variable Name 1
			int s;
			Pattern p = Pattern.compile(re1);
			Matcher m = p.matcher(newLine);
			StringBuffer buf = new StringBuffer();
			while (m.find()) {
				String var1 = m.group(1);
				if ((m.start() != 0 && (newLine.substring(m.start() - 1, m.start())).equals(".")) || (m.end() != newLine.length() && (newLine.substring(m.end(), m.end() + 1)).equals("."))) { // ce
																																																// n'est
																																																// pas
																																																// la
																																																// bonne
																																																// variable
					continue;
				}
				if ((Arrays.asList(plots).contains(varEnv.get(var) + "(")) && ((m.end() != newLine.length() && (newLine.substring(m.end(), m.end() + 1)).equals(sep)) || var1.equals(newLine))) {// traitement
																																																	// des
																																																	// variables
																																																	// (plots
																																																	// ou
																																																	// lm)
																																																	// si
																																																	// ....var1;
																																																	// ||
																																																	// var1
																																																	// ==
																																																	// line
					if (!newLine.contains("grid.arrange(")) { // provisoire je
																// pense // un
																// provisoire
																// qui dure...si
																// on voit
																// grid.arrange(
																// on passe a la
																// ligne
																// suivante
																// j'imagine

						String groupReplacement = generateLinePlot(var1, sep);
						m.appendReplacement(buf, groupReplacement);
					}
					else {
						// m.appendReplacement(buf, userEnv+
						// m.quoteReplacement("$") +var1);
						// m.appendReplacement(buf, var1);
					}

				}
				// else if(!( // triatement de la variable en env
				// (m.start()!=0 && (newLine.substring(m.start()-1,
				// m.start())).equals("$"))
				// || (m.end()!=newLine.length() && (newLine.substring(m.end(),
				// m.end()+1)).equals("("))
				// || (m.end()!=newLine.length() && (newLine.substring(m.end(),
				// m.end()+1)).equals("."))
				// || (m.start()!=0 && (newLine.substring(m.start()-1,
				// m.start())).equals("."))
				// )){
				// //newLine = newLine.replace(var1, userEnv+"$"+var1);
				// m.appendReplacement(buf, userEnv+ m.quoteReplacement("$")
				// +var1);
				// //System.out.print("("+var1.toString()+")"+"\n");
				// }
				//
				//
				//
			}
			m.appendTail(buf);
			newLine = buf.toString();
		}
		// //////////////////////////////////////////////////////////////////////////

		// /////////////////////////// RM ou REMOVE
		// /////////////////////////////////
		// if(line.contains("rm(")){
		// String elem;
		// elem = newLine.substring(newLine.indexOf("rm(")+3,
		// (newLine.indexOf(")", newLine.indexOf("rm(")+3)));
		// elem = elem.replace(userEnv.toString()+"$", "") + ", envir=" +
		// userEnv;
		// newLine = newLine.substring(0, newLine.indexOf("rm(")+3) + elem +
		// newLine.substring(newLine.indexOf(")", newLine.indexOf("rm(")+3));
		// }
		// else if(line.contains("remove(")){
		// String elem;
		// elem = newLine.substring(newLine.indexOf("remove(")+7,
		// (newLine.indexOf(")", newLine.indexOf("remove(")+7)));
		// elem = elem.replace(userEnv.toString()+"$", "") + ", envir=" +
		// userEnv;
		// newLine = newLine.substring(0, newLine.indexOf("remove(")+7) + elem +
		// newLine.substring(newLine.indexOf(")",
		// newLine.indexOf("remove(")+7));
		// }

		// XXX
		// ////////////////////////////////////////////////////////////////////////
		tempNewLineTable.add(newLine);

		// String funcdeb = ".f <- function(){";
		// String funcend = sep +
		// " for(.n in ls(all.names=TRUE)){assign(.n, get(.n), envir="+ userEnv
		// +")}}, environment(.f) <- "+ userEnv +", .f()" ;
		//
		// newLine = funcdeb + newLine + funcend;

		return newLine;
	}

	// private String adaptScript(String script, String userEnv) {
	// String newScript = script;
	// boolean bfind = false;
	//
	// String sep= ";";
	//
	// ///////////////////////// UNSUPPORTED
	// /////////////////////////////////////
	// // si la ligne contient une fonction non suportée --> on skip
	// for(String match : nonSupported){
	// if(script.contains(match)){
	// newScript = "";
	// return newScript;
	// }
	// }
	// //////////////////////////////////////////////////////////////////////////
	//
	// /////////////////////////// PLOT AND GRAPHS
	// /////////////////////////////////////
	// //on capte les fonctions graphiques pour recuperer les images
	//
	// Set<String> plotConstructiontoReplace = new HashSet<String>();
	// Set<String> plotConstructiontoAppend = new HashSet<String>();
	// int deb=0, fin=0;
	// for(String match : plots){
	// if(script.contains(match)){
	// String strPlot = match.replace("(", "\\(");
	// strPlot = strPlot.replace(".", "\\.");
	// // String reg = "(\\b" + strPlot + "([^)]+)\\))";
	// String reg = "(\\b" + strPlot + "((\\((\\(.*?\\)|.)*?\\)|.)*?)\\))";
	// //(\bclusplot\(((\((\(.*?\)|.)*?\)|.)*?\)))
	// Pattern p = Pattern.compile(reg);
	// Matcher m = p.matcher(newScript);
	// StringBuffer buf = new StringBuffer();
	//
	// while (m.find())
	// {
	//
	// String var1=m.group(1);
	// String var2=m.group(2);
	// if((m.start()!=0 && (newScript.substring(m.start()-1,
	// m.start())).equals(".")) || (m.end()!=newScript.length() &&
	// (newScript.substring(m.end(), m.end()+1)).equals("."))){ //ce n'est pas
	// la bonne variable
	// continue;
	// }
	// if(!(m.start()!=0 && (newScript.substring(0,
	// m.start()).trim()).endsWith("<-"))){ //si le plot n'est pas stocké dans
	// une variable
	// if(!(m.start()!=0 && ((newScript.substring(0,
	// m.start()).trim()).endsWith("+") ||
	// (newScript.substring(m.end()).trim()).startsWith("+")) )){ //si c'est un
	// plot direct
	// String groupReplacement = "", whichparameter = "";
	// String elem = var2;
	// if(elem.contains(",") && ! elem.contains("(")) elem = elem.substring(0,
	// elem.indexOf(","));
	// if(strPlot.equals("plot\\(") && var1.contains("lm(")){
	// if(!var2.contains("which")) whichparameter = ", which=1:6"; //TODO algo
	// pour recuperer la value du param which pour en sortir un layout adapté
	// var1 = "layout(matrix(c(1,2,3,4,5,6),2,3))" + sep +
	// var1.substring(0,var1.indexOf("lm(")) +
	// var2.substring(var2.indexOf("lm(")) + whichparameter +", ask=FALSE)";
	// }
	// if(strPlot.equals("ggplot\\(")){
	// //var1 = var1.substring(0, var1.indexOf(strPlot)+ strPlot.length()) +
	// var2 + ", environment=" + userEnv + var1.substring(var1.indexOf(var2) +
	// var2.length());
	// if(var2.equals("")){
	// var1 = var1.substring(0, var1.indexOf(strPlot)+ strPlot.length()) +
	// "environment=" + userEnv + ")";
	// } else {
	// var1 = var1.substring(0, var1.indexOf(strPlot)+ strPlot.length()) + var2
	// + ", environment=" + userEnv + var1.substring(var1.indexOf(var2) +
	// var2.length());
	// }
	// }
	// // if(strPlot.equals("plot\\(") && (varEnv.containsKey(elem) &&
	// varEnv.get(elem).contains("lm"))){
	// // if(!var2.contains("which")) whichparameter = ", which=1:6";
	// // var1 = "layout(matrix(c(1,2,3,4,5,6),2,3))" + sep +
	// var1.substring(0,var1.indexOf(elem)) + var2.substring(var2.indexOf(elem))
	// + whichparameter +", ask=FALSE)";
	// // }
	// groupReplacement = generateLinePlot(var1, sep);
	//
	// m.appendReplacement(buf, groupReplacement.replace("$", "\\$"));
	// } else {//si c'est une construction de plot
	// //recup part of line with '+'
	// String partline = "";
	// String firstpart = newScript.substring(0, m.start());
	// String reg3 = "(\\(.*("+sep+").*\\)|\\[.*("+sep+").*\\])";
	// Pattern p3 = Pattern.compile(reg3);
	// Matcher m3 = p3.matcher(firstpart);
	// while(m3.find()){
	// String varice = m3.group(1);//craquage
	// String varane =varice.replaceAll(sep, "!!VirGulE!!");
	// firstpart = firstpart.replace(varice, varane);
	// }
	// if(firstpart.contains(sep)){
	// partline = firstpart.substring(firstpart.lastIndexOf(sep));
	// } else {
	// partline = firstpart;
	// }
	// partline = partline.replaceAll("!!VirGulE!!", sep);
	// partline += newScript.substring(m.start(), m.end());
	// String lastpart = newScript.substring(m.end());
	// if(lastpart.contains(sep)){
	// partline += lastpart.substring(0, firstpart.indexOf(sep));
	// } else {
	// partline += lastpart;
	// }
	// plotConstructiontoReplace.add(partline);
	// String[] tab = partline.split("\\+");
	// List<String> plotvars = new ArrayList<String>();
	// partline = "";
	// // for (Entry<String, String> entry : varEnv.entrySet()) {
	// // if (Arrays.asList(plots).contains(entry.getValue()+"(")) {
	// // plotvars.add(entry.getKey());
	// // }
	// // }
	// tabloop:for(String part : tab){
	// for(String plot : plots){
	// if(script.contains(plot)){
	// partline += (partline.length()>0) ? " + " + part : part;
	// continue tabloop;
	// }
	// }
	// for(String plot : plotvars){
	// if(script.contains(plot)){
	// partline += (partline.length()>0) ? " + " + part : part;
	// continue tabloop;
	// }
	// }
	// }
	//
	// String groupReplacement = generateLinePlot(partline, sep);
	// //m.appendReplacement(buf, groupReplacement);
	// plotConstructiontoAppend.add(groupReplacement);
	//
	//
	// }
	// } else { //si le plot est placé dans une variable
	// String reg2 = "\\b(\\w+)\\s*<-";
	// Pattern p2 = Pattern.compile(reg2);
	// Matcher m2 = p2.matcher(newScript.substring(0, m.start()));
	// String var = "";
	// while (m2.find())
	// {
	// var = m2.group(1);
	// }
	// varEnv.put(var, match.replace("(", "")); //XXX on laisse juste ce system
	// d'enregistrement de variable plour le plot multi lignes
	//
	// if(strPlot.equals("ggplot\\(")){
	// String groupReplacement = "";
	// if(var2.equals("")){
	// groupReplacement = var1.substring(0, var1.indexOf(strPlot)+
	// strPlot.length()) + "environment=" + userEnv + ")";
	// } else {
	// groupReplacement = var1.substring(0, var1.indexOf(strPlot)+
	// strPlot.length()) + var2 + ", environment=" + userEnv +
	// var1.substring(var1.indexOf(var2) + var2.length());
	// }
	// m.appendReplacement(buf, groupReplacement.replace("$", "\\$"));
	// }
	//
	// }
	// }
	// m.appendTail(buf);
	// newScript = buf.toString();
	// bfind = true;
	//
	//
	// }
	// }
	// for(int i=0; i<plotConstructiontoReplace.size(); i++){
	// newScript =
	// newScript.replace((String)plotConstructiontoReplace.toArray()[i],
	// (String)plotConstructiontoAppend.toArray()[i]);
	// }
	// //////////////////////////////////////////////////////////////////////////
	//
	//
	// ///////////////////////////// HELP + LS + objects
	// //////////////////////////////
	// if(!bfind){
	// // on capte les help
	// if(script.contains("library(help")){
	// String tempFile = new Date().getTime() +".txt";
	// // r <- library(help = datasets)
	// // txt <- c(paste("\t\tInformation sur le package", r$name),
	// "\nDescription :\n", r$info[[1]], "\nIndex:\n", r$info[[2]])
	// // write(txt, "temp/datasets.txt", sep="\t")
	// newScript = "r <- "+ script +";";
	// newScript +=
	// "txt <- c(paste(\"\t\tInformation sur le package\", r$name), \"\nDescription :\n\", r$info[[1]], \"\nIndex:\n\", r$info[[2]]);"
	// ;
	// newScript += "write(txt, '"+ tempFile + "', sep=\"\t\"); r<-readBin('"+
	// tempFile +"','raw',1024*1024); file.remove('"+ tempFile +"')";
	// }
	// else if(script.contains("help(") || script.contains("?")){
	//
	// String elem;
	// if(script.contains("help(")){
	// elem = script.substring(script.indexOf("help(")+5, (script.indexOf(")",
	// script.indexOf("help(")+5)));
	// if(elem.contains(",")) elem = elem.substring(0, elem.indexOf(","));
	// } else {
	// elem = script.substring(script.indexOf("?") + 1, script.length());
	// }
	// // //help(<nom fonction>, help_type="pdf")
	// // newLine = "help("+ elem +", help_type='pdf'),";
	// newScript = "library(tools); r<-utils:::.getHelpFile(help('"+elem+"'));";
	// newScript += "Rd2HTML(r,'"+ elem +".html');";
	// newScript += "r<-readBin('"+ elem
	// +".html','raw',1024*1024); file.remove('"+ elem +".html')";
	//
	//
	// }
	// else if(script.split("\\bls\\(").length > 1){
	// //else if(line.contains("ls(")){
	// String elem;
	// elem = newScript.substring(newScript.indexOf("ls(")+3,
	// (newScript.indexOf(")", newScript.indexOf("ls(")+3)));
	// if(elem.equals("")){
	// elem = "envir=" + userEnv;
	// } else {
	// elem = "envir=" + userEnv + "," + elem;
	// }
	// newScript = newScript.substring(0, newScript.indexOf("ls(")+3) + elem +
	// newScript.substring(newScript.indexOf(")", newScript.indexOf("ls(")+3));
	//
	//
	// }
	// else if(script.contains("objects(")){
	// String elem;
	// elem = newScript.substring(newScript.indexOf("objects(")+8,
	// (newScript.indexOf(")", newScript.indexOf("objects(")+8)));
	// if(elem.equals("")){
	// elem = "envir=" + userEnv;
	// } else {
	// elem = "envir=" + userEnv + "," + elem;
	// }
	// newScript = newScript.substring(0, newScript.indexOf("objects(")+8) +
	// elem + newScript.substring(newScript.indexOf(")",
	// newScript.indexOf("objects(")+8));
	//
	//
	// }
	// }
	// //////////////////////////////////////////////////////////////////////////
	//
	// for(String var : varEnv.keySet()){
	// // //String re1="\\b(\\w*[\\.]?" + var + ")\\b"; // Variable Name 1
	// String re1="\\b(" + var + ")\\b"; // Variable Name 1
	// int s;
	// Pattern p = Pattern.compile(re1);
	// Matcher m = p.matcher(newScript);
	// StringBuffer buf = new StringBuffer();
	// while (m.find())
	// {
	// String var1=m.group(1);
	// if((m.start()!=0 && (newScript.substring(m.start()-1,
	// m.start())).equals(".")) || (m.end()!=newScript.length() &&
	// (newScript.substring(m.end(), m.end()+1)).equals("."))){ //ce n'est pas
	// la bonne variable
	// continue;
	// }
	// if((Arrays.asList(plots).contains(varEnv.get(var)+"("))
	// && ((m.end()!=newScript.length() && (newScript.substring(m.end(),
	// m.end()+1)).equals(sep)) || var1.equals(newScript))){// traitement des
	// variables (plots ou lm) si ....var1; || var1 == line
	// if(!newScript.contains("grid.arrange(")){ //provisoire je pense // un
	// provisoire qui dure...si on voit grid.arrange( on passe a la ligne
	// suivante j'imagine
	//
	// String groupReplacement = generateLinePlot(var1, sep);
	// m.appendReplacement(buf, groupReplacement);
	// } else {
	// // m.appendReplacement(buf, userEnv+ m.quoteReplacement("$") +var1);
	// // m.appendReplacement(buf, var1);
	// }
	//
	// }
	//
	// }
	// m.appendTail(buf);
	// newScript = buf.toString();
	// }
	//
	//
	// //XXX
	// //////////////////////////////////////////////////////////////////////////
	// tempNewLineTable.add(newScript);
	//
	// return newScript;
	// }

	public RScriptModel addDatasetTemp(byte[] resultStream, boolean[] aredigits, String name, String userREnv) {
		String basePath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_FILES) + "air_files/datasets/";
		if(basePath.startsWith("webapps")){
			basePath = "../../"+basePath;
		}
		RScriptModel model = new RScriptModel();
		try {
			String colClasses = "";
			for (int i = 0; i < aredigits.length; i++) {
				colClasses += (aredigits[i]) ? "'double'," : "'factor',";
			}
			colClasses = colClasses.substring(0, colClasses.length() - 1);
			connexion.assign("tempstream", resultStream);
			String script = "filetemp <- tempfile(); writeBin(object=tempstream, con=filetemp)\n";
			script += name + " <- read.csv(filetemp, sep=';', colClasses=c(" + colClasses + "), quote='\"')\n";
			script += "write.table("+ name +", '"+ basePath + name +".airdata', sep='\t')\n";
			script += name;
			model.setUserREnv(userREnv);
			model.setScript(script);
			model = scriptEval(model);

		} catch (REngineException e) {
			System.out.println(ExceptionUtils.getStackTrace(e).replace("&nbsp;", " "));
		}
		return model;

	}

	public RScriptModel addDatasetTempFile(String query, String name, String tempfile, String userREnv) {
		String basePath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_FILES) + "air_files/datasets/";
		if(basePath.startsWith("webapps")){
			basePath = "../../"+basePath;
		}
		RScriptModel model = new RScriptModel();
		String script = name + " <- " + tempfile + "[c(" + query + ")]\n";
		script += "write.table("+ name +", '"+ basePath + name +".airdata', sep='\t')\n";
		script += name;
		model.setUserREnv(userREnv);
		model.setScript(script);
		model = scriptEval(model);
		return model;

	}

	public RScriptModel loadDataset(String name, String request, String userREnv) {
		String basePath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_FILES) + "air_files/datasets/";
		if(basePath.startsWith("webapps")){
			basePath = "../../"+basePath;
		}
		RScriptModel model = new RScriptModel();
		// String script = "library("+ rPackage +")\n";
		// script += "data("+ name +")\n";
		String script = request + "\n";
		script += "write.table("+ name +", '"+ basePath + name +".airdata', sep='\t')\n";
		script += name;
		model.setUserREnv(userREnv);
		model.setScript(script);
		model = scriptEval(model);
		return model;
	}
	
	public RScriptModel loadDatasetTemp(String name, String dataPath, String userREnv) {
		if(dataPath.startsWith("webapps")){
			dataPath = "../../"+dataPath;
		}
		RScriptModel model = new RScriptModel();
		String script = name + " <- read.table('"+ dataPath +"', header=TRUE, sep='\t')";
		model.setUserREnv(userREnv);
		model.setScript(script);
		model = scriptEval(model);
		return model;
	}

	public RScriptModel renderMarkdown(byte[] resultStream, String name, String userREnv, List<String> outputs) {
		RScriptModel model = new RScriptModel();
		List<String> outputVars = new ArrayList<String>();

		model.setUserREnv(userREnv);
		try {
			connexion.assign("tempstream", resultStream);
			String script = "library(rmarkdown)\n";
			script += "writeBin(object=tempstream, con=\"" + name + ".Rmd\")\n";

			if (outputs.isEmpty()) {
				script += "urlhtml <- rmarkdown::render(\"" + name + ".Rmd\", encoding = getOption(\"encoding\"), envir=" + userREnv + ")\n";
				script += "temphtml<-readBin(urlhtml,'raw',2048*2048); file.remove(urlhtml)\n";
				outputVars.add("temphtml");
			}
			
			if (outputs.contains("html")) {
				script += "urlhtml <- rmarkdown::render(\"" + name + ".Rmd\", output_format = 'html_document',encoding = getOption(\"encoding\"), envir=" + userREnv + ")\n";
				script += "temphtml<-readBin(urlhtml,'raw',2048*2048); file.remove(urlhtml)\n";
				outputVars.add("temphtml");
			}

			if (outputs.contains("pdf")) {
				script += "urlpdf <- rmarkdown::render(\"" + name + ".Rmd\", output_format=pdf_document(latex_engine='pdflatex'), envir=" + userREnv + ")\n";
				script += "temppdf<-readBin(urlpdf,'raw',2048*2048); file.remove(urlpdf)\n";
				outputVars.add("temppdf");
			}

			if (outputs.contains("docx")) {
				script += "urldoc <- rmarkdown::render(\"" + name + ".Rmd\", output_format='word_document', envir=" + userREnv + ")\n";
				script += "tempdoc<-readBin(urldoc,'raw',2048*2048); file.remove(\"" + name + ".Rmd\"); file.remove(urldoc)";
				outputVars.add("tempdoc");
			}
			model.setOutputs(outputVars.toArray(new String[outputVars.size()]));// new
																				// String[]{"temphtml","temppdf","tempdoc"});

			model.setScript(script);
			model = scriptEval(model);

		} catch (REngineException e) {
			System.out.println(ExceptionUtils.getStackTrace(e).replace("&nbsp;", " "));
		}
		return model;
	}

	public String loadCsvFile(byte[] resultStream, boolean hasHeader, String sep, String userREnv) {
		RScriptModel model = new RScriptModel();
		ArrayList<DataColumn> columns = new ArrayList<DataColumn>();
		try {

			connexion.assign("tempdata", resultStream);
			String script = "filetemp <- tempfile(); writeBin(object=tempdata, con=filetemp)\n";
			script += "rm(tempframe)\n";
			script += "tempframe <- read.csv(filetemp, sep='" + sep + "', header=" + (hasHeader ? "TRUE" : "FALSE") + " , encoding='UTF-8')";
			model.setUserREnv(userREnv);
			model.setScript(script);
			model = scriptEval(model);

		} catch (REngineException e) {
			System.out.println(ExceptionUtils.getStackTrace(e).replace("&nbsp;", " "));
		}
		return "tempframe";
	}

	public String loadExcelFile(byte[] resultStream, boolean hasHeader, boolean fast, List<DataColumn> cols, String userREnv) {
		RScriptModel model = new RScriptModel();

		try {

			connexion.assign("tempdata", resultStream);
			String script = "filetemp <- tempfile(); writeBin(object=tempdata, con=filetemp)\n";
			script += "library(xlsx)\n";
			script += "rm(tempframe)\n";
			if (!fast) {
				script += "tempframe <- read.xlsx(filetemp, sheetIndex=1)";
			}
			else {
				String colClasses = "";
				for (DataColumn col : cols) {
					colClasses += "'" + col.getColumnName() + "'='" + ((col.getColumnTypeName().equals("factor")) ? "character'," : col.getColumnTypeName() + "',");
				}
				colClasses = colClasses.substring(0, colClasses.length() - 1);
				script += "tempframe <- read.xlsx2(filetemp, sheetIndex=1, colClasses=c(" + colClasses + "))";
			}
			model.setUserREnv(userREnv);
			model.setScript(script);
			model = scriptEval(model);

		} catch (REngineException e) {
			System.out.println(ExceptionUtils.getStackTrace(e).replace("&nbsp;", " "));
		}

		return "tempframe";

	}

	public List<DataColumn> getTempFileMetaData(String var, String userREnv) {
		RScriptModel model = new RScriptModel();
		ArrayList<DataColumn> columns = new ArrayList<DataColumn>();
		String script = "res<-character()\n";
		script += "data(" + var + ")\n";
		script += "for(name in colnames(" + var + ")) {\n";
		script += "colclass<-sapply(" + var + "[name], class)\n";
		script += "res<-rbind(res,c(paste(name, colclass)))\n";
		script += "}\n";
		script += "manual_result <- res";
		model.setUserREnv(userREnv);
		model.setScript(script);
		model.setOutputs("manual_result".split(" "));
		model = scriptEval(model);

		List<String> it = new ArrayList<>();
		try {
			 it = (List<String>) model.getOutputVars().get(0);
		} catch (Exception e) {
			it = Arrays.asList((String[])model.getOutputVars().get(0));
		}
		
		DataColumn column;
		for (String rsM : it) {
			String name = rsM.split(" ")[0];
			String RType = rsM.split(" ")[1];
			int SQLType = 0;
			if (RType.equals("numeric") || RType.equals("integer")) {
				SQLType = java.sql.Types.DOUBLE;
			}
			else if (RType.equals("character") || RType.equals("factor")) {
				SQLType = java.sql.Types.VARCHAR;
			}
			else if (RType.equals("logical")) {
				SQLType = java.sql.Types.BOOLEAN;
			}
			else {
				SQLType = java.sql.Types.OTHER;
			}

			column = new DataColumn(name, RType, SQLType, name);

			columns.add(column);
		}
		return columns;
	}

	private void getGraphicsHandlers() {
		String path = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_FILES) + "air_files/air_config/plot_functions.xml";
		try {
			FileInputStream fis = new FileInputStream(new File(path));
			String xml = IOUtils.toString(fis, "UTF-8");
			Document doc = DocumentHelper.parseText(xml);
			Element root = doc.getRootElement();
			List<Element> list = (List<Element>) doc.selectNodes("//doc/function");
			plots = new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				plots[i] = list.get(i).getText() + "(";
			}
		} catch (IOException | DocumentException e) {
			System.out.println(ExceptionUtils.getStackTrace(e).replace("&nbsp;", " "));
		}

	}

	// private void refreshVarsEnv() {
	// RScriptModel model = new RScriptModel();
	// String script = "manual_result <- ls("+userEnv+")";
	// model.setScript(script);
	// model.setOutputs("manual_result".split(" "));
	// model = scriptEval(model);
	// varEnv = new
	// HashSet<String>(Arrays.asList((String[])model.getOutputVars().get(0)));
	// }

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public void addUser(User user) {
		this.users.add(user);
		String userEnv = user.getLogin() + user.getId();
		if ((connexion == null || !connexion.isConnected()) && !userEnv.equals("")) {
			connect(userEnv);
		}
		else {
			RScriptModel model = new RScriptModel();
			String script = "if(!exists('" + userEnv + "')){\n";
			script += userEnv + "<- new.env()\n";
			script += "}";
			model.setUserREnv("");
			model.setScript(script);
			model.setOutputs("no_return".split(" "));
			model = scriptEval(model);
		}

	}

	public void removeUser(User user) {
		this.users.remove(user);
	}

	@SuppressWarnings("static-access")
	public String adaptRMarkdownLine(String line, String userEnv) {
		String newLine = line;

		String sep = ";";

		// /////////////////////// UNSUPPORTED
		// /////////////////////////////////////
		// si la ligne contient une fonction non suportée --> on skip
		for (String match : nonSupported) {
			if (line.contains(match)) {
				newLine = "";
				return newLine;
			}
		}
		// ////////////////////////////////////////////////////////////////////////

		// /////////////////////////// HELP + LS + objects
		// //////////////////////////////
		if (line.split("\\bls\\(").length > 1) {
			// else if(line.contains("ls(")){
			String elem;
			elem = newLine.substring(newLine.indexOf("ls(") + 3, (newLine.indexOf(")", newLine.indexOf("ls(") + 3)));
			if (elem.equals("")) {
				elem = "envir=" + userEnv;
			}
			else {
				elem = "envir=" + userEnv + "," + elem;
			}
			newLine = newLine.substring(0, newLine.indexOf("ls(") + 3) + elem + newLine.substring(newLine.indexOf(")", newLine.indexOf("ls(") + 3));

		}
		else if (line.contains("objects(")) {
			String elem;
			elem = newLine.substring(newLine.indexOf("objects(") + 8, (newLine.indexOf(")", newLine.indexOf("objects(") + 8)));
			if (elem.equals("")) {
				elem = "envir=" + userEnv;
			}
			else {
				elem = "envir=" + userEnv + "," + elem;
			}
			newLine = newLine.substring(0, newLine.indexOf("objects(") + 8) + elem + newLine.substring(newLine.indexOf(")", newLine.indexOf("objects(") + 8));

		}

		// ////////////////////////////////////////////////////////////////////////

		// ///////////////////////////// ENVIRONNEMENT
		// /////////////////////////////////
		// //traitement variables vers environnement
		// //String res = newLine.replaceAll("(\\w+)\\s*<-",
		// userEnv+"$1"+" <-");
		// String reg = "\\b([\\w|.]+\\s*<-)";
		// Pattern p = Pattern.compile(reg);
		// Matcher m = p.matcher(newLine);
		// StringBuffer buf = new StringBuffer();
		// while (m.find())
		// {
		// String var1=m.group(1);
		// if(!var1.contains(userEnv.toString()) && !(m.start()!=0 &&
		// ((newLine.substring(m.start()-1, m.start())).equals("$") ||
		// (newLine.substring(m.start()-1, m.start())).equals(".")) )){
		// //newLine = newLine.replace(var1, userEnv+"$"+var1);
		// m.appendReplacement(buf, userEnv+ m.quoteReplacement("$") +var1);
		// if(!varEnv.containsKey(var1.split("<-")[0].trim())){
		// varEnv.put(var1.split("<-")[0].trim(),"");
		// }
		//
		// }
		//
		// }
		// m.appendTail(buf);
		// newLine = buf.toString();
		//
		// for(String var : varEnv.keySet()){
		//
		// //String re1="\\b(\\w*[\\.]?" + var + ")\\b"; // Variable Name 1
		// String re1="\\b(" + var + ")\\b"; // Variable Name 1
		// p = Pattern.compile(re1);
		// m = p.matcher(newLine);
		// buf = new StringBuffer();
		// while (m.find())
		// {
		// String var1=m.group(1);
		// if((m.start()!=0 && (newLine.substring(m.start()-1,
		// m.start())).equals(".")) || (m.end()!=newLine.length() &&
		// (newLine.substring(m.end(), m.end()+1)).equals("."))){ //ce n'est pas
		// la bonne variable
		// continue;
		// }
		// if((m.start()!=0 && var1.equals("r") &&
		// (newLine.substring(m.start()-1, m.start())).equals("{"))){ //c'est la
		// balise markdown r
		// continue;
		// }
		// if(!( // triatement de la variable en env
		// (m.start()!=0 && (newLine.substring(m.start()-1,
		// m.start())).equals("$"))
		// || (m.end()!=newLine.length() && (newLine.substring(m.end(),
		// m.end()+1)).equals("("))
		// || (m.end()!=newLine.length() && (newLine.substring(m.end(),
		// m.end()+1)).equals("."))
		// || (m.start()!=0 && (newLine.substring(m.start()-1,
		// m.start())).equals("."))
		// )){
		// //newLine = newLine.replace(var1, userEnv+"$"+var1);
		// m.appendReplacement(buf, userEnv+ m.quoteReplacement("$") +var1);
		// //System.out.print("("+var1.toString()+")"+"\n");
		// }
		//
		//
		//
		// }
		// m.appendTail(buf);
		// newLine = buf.toString();
		// }
		// //////////////////////////////////////////////////////////////////////////
		return newLine;
	}

	private String generateLinePlot(String var, String sep) {

		// traitement par, layout etc
		String addPart = "";
		if (tempNewLineTable.size() > 0) {
			Object[] table = tempNewLineTable.toArray();
			int size = table.length;
			for (int i = 1; i <= size; i++) {
				String line = (String) table[size - i];
				if (line.contains("par(")) {
					addPart = line.substring(line.indexOf("par(")) + sep;
					int j;
					for (j = i - 1; j > 0; j--) {
						addPart += (String) table[size - j] + sep;
					}
					break;
				}
				if (line.contains("layout(matrix(")) {
					addPart = line.substring(line.indexOf("layout(matrix(")) + sep;
					int j;
					for (j = i - 1; j > 0; j--) {
						addPart += (String) table[size - j] + sep;
					}
					break;
				}
			}
		}
		// affichage standard

		String tempFile = new Date().getTime() + ".svg";
		String groupReplacement = "svg('" + tempFile + "')" + sep;
		groupReplacement += addPart;
		// groupReplacement += "print("+ ((addUserEnv)? userEnv+ "\\$" : "" ) +
		// var +")"+sep;
		// groupReplacement += "graphics.off()"+sep+ ((addUserEnv)? userEnv+
		// "\\$" : "" )+ "varGraphManual<-readBin('"+ tempFile
		// +"','raw',2048*2048)"+sep+" file.remove('"+ tempFile +"')";
		groupReplacement += "print(" + var + ")" + sep;
		groupReplacement += "graphics.off()" + sep + "varGraphManual<-readBin('" + tempFile + "','raw',2048*2048)" + sep ;//+ " file.remove('" + tempFile + "')";
		return groupReplacement;
	}

	private void socialNetworkInit() {
		String connectionTwitter = "library(twitteR)\n" + "consumer_key    <- 'vIBQsoRkFxLaKIO7FeWa8SNsZ'\n" + "consumer_secret <- '5JCwWavmw7S6XjTJYzyCAqahEgzGxgLhzaO6qRyN4EmZob4jX0'\n" + "access_token    <- '449516774-pNNEKCn3MvNUAXkk0SRLgW7hIVIpVsaTzzoRc52M'\n" + "access_secret   <- 'TJh0ZsJTf3ZMWsuAVok3Vi0DO6r55NHe8gVJOWJoT53aZ'\n" + "options(httr_oauth_cache=T)\n" + "setup_twitter_oauth(consumer_key, consumer_secret, access_token, access_secret)\n";

		String connectionFacebook = "library(Rfacebook)\n" + "app_id <- '956647941108685'\n" + "app_secret<- 'c82f681004ddf4f46ade028eca9c46b4'\n" + "fb_oauth <- fbOAuth(app_id, app_secret, extended_permissions = FALSE, legacy_permissions = FALSE)\n";

		RScriptModel model = new RScriptModel();
		model.setScript(connectionTwitter);
		model.setOutputs("no_return".split(" "));
		model.setUserREnv("");
		model = scriptEval(model);
		// if(!model.isScriptError()){
		// socialConnected = true;
		// }
	}
}
