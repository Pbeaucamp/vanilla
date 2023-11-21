package bpm.gateway.core.veolia.abonnes;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.NamespaceSupport;
import org.xml.sax.helpers.XMLFilterImpl;

import bpm.gateway.core.veolia.IXmlManager;
import bpm.gateway.core.veolia.LogInsertXML;
import bpm.gateway.core.veolia.LogXML;
import bpm.gateway.core.veolia.abonnes.AbonnesDAO.ClassAbonnee;
import bpm.vanilla.platform.core.beans.resources.ClassDefinition;

/**
 * This object implements XMLFilter and monitors the incoming SAX events. Once
 * it hits a purchaseOrder element, it creates a new unmarshaller and unmarshals
 * one purchase order.
 * 
 * <p>
 * Once finished unmarshalling it, we will process it, then move on to the next
 * purchase order.
 */
public class AbonnesSplitter extends XMLFilterImpl {

	private IXmlManager manager;
	private JAXBContext context;
	
	private ClassDefinition mainClass;
	private String fileName;
	private Integer idChg;
	
	private ClassAbonnee currentClass;
	private ClassDefinition currentClassDef;
	private LogInsertXML currentLog;
	private List<LogXML> currentLogs;

	/**
	 * Remembers the depth of the elements as we forward SAX events to a JAXB
	 * unmarshaller.
	 */
	private int depth;

	/**
	 * Reference to the unmarshaller which is unmarshalling an object.
	 */
	private UnmarshallerHandler unmarshallerHandler;

	/**
	 * Keeps a reference to the locator object so that we can later pass it to a
	 * JAXB unmarshaller.
	 */
	private Locator locator;

	/**
	 * Used to keep track of in-scope namespace bindings.
	 * 
	 * For JAXB unmarshaller to correctly unmarshal documents, it needs to know
	 * all the effective namespace declarations.
	 */
	private NamespaceSupport namespaces = new NamespaceSupport();
	
	public AbonnesSplitter(IXmlManager manager, ClassDefinition mainClass, String fileName, Integer idChg) {
		try {
			this.manager = manager;
			this.mainClass = mainClass;
			this.fileName = fileName;
			this.idChg = idChg;
			context = JAXBContext.newInstance(TypeContratAbt.class, TypeContratAep.class, TypeContratAc.class, TypeAbonne.class, TypeFacture.class,
					TypeLigneFacture.class, TypeBranchement.class, TypePointFourniture.class, TypeCompteur.class, TypeReleve.class, TypeIntervention.class);
		} catch (JAXBException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {

		if (depth != 0) {
			// we are in the middle of forwarding events.
			// continue to do so.
			depth++;
			super.startElement(namespaceURI, localName, qName, atts);
			return;
		}

		if (namespaceURI.equals("")) {
			ClassAbonnee element = getClassAbonne(localName);
			if (element == null) {
				return;
			}
			if (currentClass != element) {
				if (currentLog != null) {
					manager.insertLog(fileName, idChg, currentLog, currentLogs);
				}
				
				this.currentLog = manager.createLog(element, fileName, idChg);
				this.currentLogs = new ArrayList<>();
				
				this.currentClassDef = manager.getClassDefinition(element, mainClass);
			}
			this.currentClass = element;
			
			// start a new unmarshaller
			Unmarshaller unmarshaller;
			try {
				unmarshaller = context.createUnmarshaller();
			} catch (JAXBException e) {
				// there's no way to recover from this error.
				// we will abort the processing.
				throw new SAXException(e);
			}
			unmarshallerHandler = unmarshaller.getUnmarshallerHandler();

			// set it as the content handler so that it will receive
			// SAX events from now on.
			setContentHandler(unmarshallerHandler);

			// fire SAX events to emulate the start of a new document.
			unmarshallerHandler.startDocument();
			unmarshallerHandler.setDocumentLocator(locator);

			Enumeration e = namespaces.getPrefixes();
			while (e.hasMoreElements()) {
				String prefix = (String) e.nextElement();
				String uri = namespaces.getURI(prefix);

				unmarshallerHandler.startPrefixMapping(prefix, uri);
			}
			String defaultURI = namespaces.getURI("");
			if (defaultURI != null)
				unmarshallerHandler.startPrefixMapping("", defaultURI);

			super.startElement(namespaceURI, localName, qName, atts);

			// count the depth of elements and we will know when to stop.
			depth = 1;
		}
	}

	private ClassAbonnee getClassAbonne(String localName) {
		for (ClassAbonnee cl : ClassAbonnee.values()) {
			if (cl.getRoot().equals(localName)) {
				return cl;
			}
		}
		
		return null;
	}

	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		// forward this event
		super.endElement(namespaceURI, localName, qName);

		if (depth != 0) {
			depth--;
			if (depth == 0) {
				// just finished sending one chunk.

				// emulate the end of a document.
				Enumeration e = namespaces.getPrefixes();
				while (e.hasMoreElements()) {
					String prefix = (String) e.nextElement();
					unmarshallerHandler.endPrefixMapping(prefix);
				}
				String defaultURI = namespaces.getURI("");
				if (defaultURI != null)
					unmarshallerHandler.endPrefixMapping("");
				unmarshallerHandler.endDocument();

				// stop forwarding events by setting a dummy handler.
				// XMLFilter doesn't accept null, so we have to give it
				// something,
				// hence a DefaultHandler, which does nothing.
				setContentHandler(new DefaultHandler());

				// then retrieve the fully unmarshalled object
				try {
					process(currentClass, unmarshallerHandler.getResult());
				} catch (JAXBException je) {
					// error was found during the unmarshalling.
					// you can either abort the processing by throwing a
					// SAXException,
					// or you can continue processing by returning from this
					// method.
					System.err.println("unable to process an order at line " + locator.getLineNumber());
					return;
				}

				unmarshallerHandler = null;
			}
		}
	}

	private void process(ClassAbonnee currentClass, Object item) {
		if (currentClass == null) {
			System.err.println("unable to process an order at line " + locator.getLineNumber());
			return;
		}
		
		manager.processItem(currentClass, currentClassDef, item, idChg, currentLog, currentLogs);
	}

	@Override
	public void setDocumentLocator(Locator locator) {
		super.setDocumentLocator(locator);
		this.locator = locator;
	}

	@Override
	public void startPrefixMapping(String prefix, String uri) throws SAXException {
		namespaces.pushContext();
		namespaces.declarePrefix(prefix, uri);

		super.startPrefixMapping(prefix, uri);
	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
		namespaces.popContext();

		super.endPrefixMapping(prefix);
	}
}
