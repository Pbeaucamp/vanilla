//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.08.18 at 03:51:39 PM CEST 
//


package bpm.gateway.core.tsbn.atih;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the generated package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SYRIUS }
     * 
     */
    public SYRIUS createSYRIUS() {
        return new SYRIUS();
    }

    /**
     * Create an instance of {@link ETABLISSEMENT }
     * 
     */
    public ETABLISSEMENT createETABLISSEMENT() {
        return new ETABLISSEMENT();
    }

    /**
     * Create an instance of {@link PASSAGES }
     * 
     */
    public PASSAGES createPASSAGES() {
        return new PASSAGES();
    }

    /**
     * Create an instance of {@link PATIENT }
     * 
     */
    public PATIENT createPATIENT() {
        return new PATIENT();
    }

    /**
     * Create an instance of {@link LISTEDA }
     * 
     */
    public LISTEDA createLISTEDA() {
        return new LISTEDA();
    }

    /**
     * Create an instance of {@link LISTEACTES }
     * 
     */
    public LISTEACTES createLISTEACTES() {
        return new LISTEACTES();
    }

}
