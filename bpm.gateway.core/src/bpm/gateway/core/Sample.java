package bpm.gateway.core;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.parser.XSOMParser;

public class Sample {
	static final String s = "toto";
	static List<String> l;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		// XSImplementation impl = new XSImplementationImpl();
		// XSLoader schemaLoader = impl.createXSLoader(null);
		// XSModel model =
		// schemaLoader.loadURI("C:/BPM/Test/ORU/FluxXSD_RPU.xsd");

		XSOMParser parser = new XSOMParser();
		// parser.setErrorHandler(...);
		// parser.setEntityResolver(...);

		parser.parse(new File("C:/BPM/Test/XML/ORU/FluxXSD_SAU.xml"));
		// parser.parseSchema( new File("XHTML.xsd"));

		XSSchemaSet sset = parser.getResult();

		Iterator<XSComplexType> ctiter = sset.iterateComplexTypes();
		while (ctiter.hasNext()) {
			XSComplexType ct = (XSComplexType) ctiter.next();
			String typeName = ct.getName();
			// these are extensions so look at the base type to see what it is
			String baseTypeName = ct.getBaseType().getName();
			System.out.println(typeName + " is a " + baseTypeName);
		}

		// =========================================================
		// global namespace
		XSSchema globalSchema = sset.getSchema("");
		// local definitions of enums are in complex types
		ctiter = globalSchema.iterateComplexTypes();
		while (ctiter.hasNext()) {
			XSComplexType ct = (XSComplexType) ctiter.next();
			String typeName = ct.getName();
			String baseTypeName = ct.getBaseType().getName();
			System.out.println(typeName + " is a " + baseTypeName);
		}

		// =========================================================
		// the main entity of this file is in the Elements
		// there should only be one!
		if (globalSchema.getElementDecls().size() != 1) {
			throw new Exception("Should be only 1 element type per file.");
		}

		XSElementDecl ed = globalSchema.getElementDecls().values().toArray(new XSElementDecl[0])[0];
		String entityType = ed.getName();
		XSContentType xsContentType = ed.getType().asComplexType().getContentType();
		XSParticle particle = xsContentType.asParticle();
		if (particle != null) {

			XSTerm term = particle.getTerm();
			if (term.isModelGroup()) {
				XSModelGroup xsModelGroup = term.asModelGroup();
				term.asElementDecl();
				XSParticle[] particles = xsModelGroup.getChildren();
				String propertyName = null;
				String propertyType = null;
				XSParticle pp = particles[0];
				for (XSParticle p : particles) {
					XSTerm pterm = p.getTerm();
					if (pterm.isElementDecl()) {
						propertyName = pterm.asElementDecl().getName();
						if (pterm.asElementDecl().getType().getName() == null) {
							propertyType = pterm.asElementDecl().getType().getBaseType().getName();
						}
						else {
							propertyType = pterm.asElementDecl().getType().getName();
						}

						List<String> list = new ArrayList<String>();
						XSComplexType xsComplexType = pterm.asElementDecl().getType().asComplexType();
						if (xsComplexType != null) {
							XSContentType content = xsComplexType.getContentType();
							XSParticle partic = content.asParticle();
							getOptionalElements(list, partic);
						}

						System.out.println(propertyName + " is a " + propertyType);
					}
				}
			}
		}
		return;

	}

	private static void getOptionalElements(List<String> list, XSParticle xsParticle) {

		if (xsParticle != null) {

			XSTerm pterm = xsParticle.getTerm();

			if (pterm.isElementDecl()) {

				if (xsParticle.getMinOccurs().equals(new BigInteger("0"))) {
					list.add(pterm.getSourceDocument().getTargetNamespace() + ":" + pterm.asElementDecl().getName());
				}

				System.out.println("Min Occurs : " + xsParticle.getMinOccurs());
				System.out.println("Element Type : " + pterm.asElementDecl().getType());
				System.out.println("Element Name : " + pterm.asElementDecl().getName());

				XSComplexType xsComplexType = (pterm.asElementDecl()).getType().asComplexType();

				if (xsComplexType != null && !(pterm.asElementDecl().getType()).toString().contains("Enumeration")) {

					XSContentType xsContentType = xsComplexType.getContentType();

					XSParticle xsParticleInside = xsContentType.asParticle();
					getOptionalElements(list, xsParticleInside);
				}

			}
			else if (pterm.isModelGroup()) {

				XSModelGroup xsModelGroup2 = pterm.asModelGroup();
				XSParticle[] xsParticleArray = xsModelGroup2.getChildren();
				for (XSParticle xsParticleTemp : xsParticleArray) {
					getOptionalElements(list, xsParticleTemp);
				}
			}
		}
	}
}
