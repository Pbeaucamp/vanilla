//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2020.01.29 à 03:15:22 PM CET 
//


package bpm.gateway.core.veolia.patrimoine;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour anonymous complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="equipementsSocle">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded">
 *                   &lt;element name="equipementSocle" type="{}typeEquipementSocle"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsAerothermes">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementAerotherme" type="{}typeEquipementAerotherme"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsAgitateurs">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementAgitateur" type="{}typeEquipementAgitateur"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsAppareilsLevage">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementAppareilLevage" type="{}typeEquipementAppareilLevage"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsAspirateursBoues">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementAspirateurBoues" type="{}typeEquipementAspirateurBoues"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsArmoiresElectriques">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementArmoireElectrique" type="{}typeEquipementArmoireElectrique"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsAuscultationsBarrages">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementAuscultationBarrage" type="{}typeEquipementAuscultationBarrage"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsAutomates">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementAutomate" type="{}typeEquipementAutomate"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsBacsRetention">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementBacRetention" type="{}typeEquipementBacRetention"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsBallons">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementBallon" type="{}typeEquipementBallon"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsBennes">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementBenne" type="{}typeEquipementBenne"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsBoitesBoue">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementBoiteBoue" type="{}typeEquipementBoiteBoue"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsBouteillesChlore">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementBouteilleChlore" type="{}typeEquipementBouteilleChlore"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsBuselures">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementBuselure" type="{}typeEquipementBuselure"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsCaillebotis">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementCaillebotis" type="{}typeEquipementCaillebotis"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsCanauxComptage">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementCanalComptage" type="{}typeEquipementCanalComptage"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsCapteurs">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementCapteur" type="{}typeEquipementCapteur"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsCellulesPosteHT">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementCellulePosteHT" type="{}typeEquipementCellulePosteHT"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsCentralesHydrauliques">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementCentraleHydraulique" type="{}typeEquipementCentraleHydraulique"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsCentrifugeuses">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementCentrifugeuse" type="{}typeEquipementCentrifugeuse"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsChariotsElevateurs">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementChariotElevateur" type="{}typeEquipementChariotElevateur"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsChlorometres">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementChlorometre" type="{}typeEquipementChlorometre"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsClapets">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementClapet" type="{}typeEquipementClapet"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsClotures">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementCloture" type="{}typeEquipementCloture"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsCollecteurs">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementCollecteur" type="{}typeEquipementCollecteur"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsColonnesForage">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementColonneForage" type="{}typeEquipementColonneForage"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsColonnesMontantes">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementColonneMontante" type="{}typeEquipementColonneMontante"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsCompteursElectriques">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementCompteurElectrique" type="{}typeEquipementCompteurElectrique"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsComptages">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementComptage" type="{}typeEquipementComptage"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsCondensateurs">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementCondensateur" type="{}typeEquipementCondensateur"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsConvoyeursTransporteurs">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementConvoyeurTransporteur" type="{}typeEquipementConvoyeurTransporteur"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsCrepines">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementCrepine" type="{}typeEquipementCrepine"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsDemarreursElectriques">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementDemarreurElectrique" type="{}typeEquipementDemarreurElectrique"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsDeshumidificateurs">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementDeshumidificateur" type="{}typeEquipementDeshumidificateur"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsDestructeurOzone">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementDestructeurOzone" type="{}typeEquipementDestructeurOzone"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsDisconnecteurs">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementDisconnecteur" type="{}typeEquipementDisconnecteur"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsEchangeursChaleur">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementEchangeurChaleur" type="{}typeEquipementEchangeurChaleur"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsEchelles">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementEchelle" type="{}typeEquipementEchelle"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsElectrodesReference">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementElectrodeReference" type="{}typeEquipementElectrodeReference"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsEnregistreurs">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementEnregistreur" type="{}typeEquipementEnregistreur"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsAntiIntrusions">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementAntiIntrusion" type="{}typeEquipementAntiIntrusion"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsProtectionsIncendie">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementProtectionIncendie" type="{}typeEquipementProtectionIncendie"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsEspacesVert">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementEspaceVert" type="{}typeEquipementEspaceVert"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsExtracteursAir">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementExtracteurAir" type="{}typeEquipementExtracteurAir"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsFenetres">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementFenetre" type="{}typeEquipementFenetre"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsFiltresFermes">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementFiltreFerme" type="{}typeEquipementFiltreFerme"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsFiltresY">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementFiltreY" type="{}typeEquipementFiltreY"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsGardesCorps">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementGardeCorps" type="{}typeEquipementGardeCorps"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsGenerateursUV">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementGenerateurUV" type="{}typeEquipementGenerateurUV"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsGrappins">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementGrappin" type="{}typeEquipementGrappin"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsGroupesAir">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementGroupeAir" type="{}typeEquipementGroupeAir"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsGroupesDosage">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementGroupeDosage" type="{}typeEquipementGroupeDosage"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsGroupesRefroidissement">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementGroupeRefroidissement" type="{}typeEquipementGroupeRefroidissement"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsGroupesElectrogenes">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementGroupeElectrogene" type="{}typeEquipementGroupeElectrogene"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsGroupesMalaxeurs">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementGroupeMalaxeur" type="{}typeEquipementGroupeMalaxeur"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsHerses">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementHerse" type="{}typeEquipementHerse"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsHydrocyclones">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementHydrocyclone" type="{}typeEquipementHydrocyclone"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsHydroejecteurs">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementHydroejecteur" type="{}typeEquipementHydroejecteur"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsLiaisonsElectriques">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementLiaisonElectrique" type="{}typeEquipementLiaisonElectrique"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsMassesAnodiques">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementMasseAnodique" type="{}typeEquipementMasseAnodique"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsMaterielsLaboratoire">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementMaterielLaboratoire" type="{}typeEquipementMaterielLaboratoire"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsMelangeursStatiques">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementMelangeurStatique" type="{}typeEquipementMelangeurStatique"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsModulesLamellaires">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementModuleLamellaire" type="{}typeEquipementModuleLamellaire"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsMembranesUF">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementMembraneUF" type="{}typeEquipementMembraneUF"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsMoteursElectriquesManoeuvres">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementMoteurElectriqueManoeuvre" type="{}typeEquipementMoteurElectriqueManoeuvre"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsOnduleurs">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementOnduleur" type="{}typeEquipementOnduleur"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsOuvragesEvacuationBarrage">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementsOuvrageEvacuationBarrage" type="{}typeEquipementOuvragesEvacuationBarrage"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsOzoneurs">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementOzoneur" type="{}typeEquipementOzoneur"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsPointsprelevement">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementPointPrelevement" type="{}typeEquipementPointPrelevement"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsPompes">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementPompe" type="{}typeEquipementPompe"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsPontsRacleurs">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementPontRacleur" type="{}typeEquipementPontRacleur"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsPortes">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementPorte" type="{}typeEquipementPorte"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsPortails">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementPortail" type="{}typeEquipementPortail"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsPortillons">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementPortillon" type="{}typeEquipementPortillon"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsPortesSectionnelles">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementPorteSectionnelle" type="{}typeEquipementPorteSectionnelle"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsPrefiltres">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementPrefiltre" type="{}typeEquipementPrefiltre"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsPreleveursEchantillonneurs">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementPreleveurEchantillonneur" type="{}typeEquipementPreleveurEchantillonneur"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsPrelocalisateur">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementPrelocalisateur" type="{}typeEquipementPrelocalisateur"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsPressesBoue">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementPresseBoue" type="{}typeEquipementPresseBoue"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsReacteur">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementReacteur" type="{}typeEquipementReacteur"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsRetournementsTerres">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementRetournementTerres" type="{}typeEquipementRetournementTerres"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsSilos">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementSilo" type="{}typeEquipementSilo"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsSecheursAir">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementSecheurAir" type="{}typeEquipementSecheurAir"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsSoupapes">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementSoupape" type="{}typeEquipementSoupape"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsStabilisateursEcoulement">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementStabilisateurEcoulement" type="{}typeEquipementStabilisateurEcoulement"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsStationsTeletransmission">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementStationTeletransmission" type="{}typeEquipementStationTeletransmission"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsSystemeDiffusion">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementSystemeDiffusion" type="{}typeEquipementSystemeDiffusion"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsTransformateursHT">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementTransformateurHT" type="{}typeEquipementTransformateurHT"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsTransformateursRedresseurs">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementTransformateurRedresseur" type="{}typeEquipementTransformateurRedresseur"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsTrappesAcces">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementTrappeAcces" type="{}typeEquipementTrappeAcces"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsTubesCrepine">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementTubeCrepine" type="{}typeEquipementTubeCrepine"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsTurbinesHydroelectriques">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementTurbineHydroelectrique" type="{}typeEquipementTurbineHydroelectrique"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsTuyauteries">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementTuyauterie" type="{}typeEquipementTuyauterie"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsVannes">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementVanne" type="{}typeEquipementVanne"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsVariateursVitesse">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementVariateurVitesse" type="{}typeEquipementVariateurVitesse"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsVidanges">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementVidange" type="{}typeEquipementVidange"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsVentouses">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementVentouse" type="{}typeEquipementVentouse"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsVerins">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementVerin" type="{}typeEquipementVerin"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsVisTransfert">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementVisTransfert" type="{}typeEquipementVisTransfert"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsVoiries">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementVoirie" type="{}typeEquipementVoirie"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="equipementsVannesRegulation">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="equipementVanneRegulation" type="{}typeEquipementVanneRegulation"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="composants">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="composant" type="{}typeComposant"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="interventions">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="intervention" type="{}typeIntervention"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "equipementsSocle",
    "equipementsAerothermes",
    "equipementsAgitateurs",
    "equipementsAppareilsLevage",
    "equipementsAspirateursBoues",
    "equipementsArmoiresElectriques",
    "equipementsAuscultationsBarrages",
    "equipementsAutomates",
    "equipementsBacsRetention",
    "equipementsBallons",
    "equipementsBennes",
    "equipementsBoitesBoue",
    "equipementsBouteillesChlore",
    "equipementsBuselures",
    "equipementsCaillebotis",
    "equipementsCanauxComptage",
    "equipementsCapteurs",
    "equipementsCellulesPosteHT",
    "equipementsCentralesHydrauliques",
    "equipementsCentrifugeuses",
    "equipementsChariotsElevateurs",
    "equipementsChlorometres",
    "equipementsClapets",
    "equipementsClotures",
    "equipementsCollecteurs",
    "equipementsColonnesForage",
    "equipementsColonnesMontantes",
    "equipementsCompteursElectriques",
    "equipementsComptages",
    "equipementsCondensateurs",
    "equipementsConvoyeursTransporteurs",
    "equipementsCrepines",
    "equipementsDemarreursElectriques",
    "equipementsDeshumidificateurs",
    "equipementsDestructeurOzone",
    "equipementsDisconnecteurs",
    "equipementsEchangeursChaleur",
    "equipementsEchelles",
    "equipementsElectrodesReference",
    "equipementsEnregistreurs",
    "equipementsAntiIntrusions",
    "equipementsProtectionsIncendie",
    "equipementsEspacesVert",
    "equipementsExtracteursAir",
    "equipementsFenetres",
    "equipementsFiltresFermes",
    "equipementsFiltresY",
    "equipementsGardesCorps",
    "equipementsGenerateursUV",
    "equipementsGrappins",
    "equipementsGroupesAir",
    "equipementsGroupesDosage",
    "equipementsGroupesRefroidissement",
    "equipementsGroupesElectrogenes",
    "equipementsGroupesMalaxeurs",
    "equipementsHerses",
    "equipementsHydrocyclones",
    "equipementsHydroejecteurs",
    "equipementsLiaisonsElectriques",
    "equipementsMassesAnodiques",
    "equipementsMaterielsLaboratoire",
    "equipementsMelangeursStatiques",
    "equipementsModulesLamellaires",
    "equipementsMembranesUF",
    "equipementsMoteursElectriquesManoeuvres",
    "equipementsOnduleurs",
    "equipementsOuvragesEvacuationBarrage",
    "equipementsOzoneurs",
    "equipementsPointsprelevement",
    "equipementsPompes",
    "equipementsPontsRacleurs",
    "equipementsPortes",
    "equipementsPortails",
    "equipementsPortillons",
    "equipementsPortesSectionnelles",
    "equipementsPrefiltres",
    "equipementsPreleveursEchantillonneurs",
    "equipementsPrelocalisateur",
    "equipementsPressesBoue",
    "equipementsReacteur",
    "equipementsRetournementsTerres",
    "equipementsSilos",
    "equipementsSecheursAir",
    "equipementsSoupapes",
    "equipementsStabilisateursEcoulement",
    "equipementsStationsTeletransmission",
    "equipementsSystemeDiffusion",
    "equipementsTransformateursHT",
    "equipementsTransformateursRedresseurs",
    "equipementsTrappesAcces",
    "equipementsTubesCrepine",
    "equipementsTurbinesHydroelectriques",
    "equipementsTuyauteries",
    "equipementsVannes",
    "equipementsVariateursVitesse",
    "equipementsVidanges",
    "equipementsVentouses",
    "equipementsVerins",
    "equipementsVisTransfert",
    "equipementsVoiries",
    "equipementsVannesRegulation",
    "composants",
    "interventions"
})
@XmlRootElement(name = "patrimoine")
public class Patrimoine {

    @XmlElement(required = true)
    protected Patrimoine.EquipementsSocle equipementsSocle;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsAerothermes equipementsAerothermes;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsAgitateurs equipementsAgitateurs;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsAppareilsLevage equipementsAppareilsLevage;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsAspirateursBoues equipementsAspirateursBoues;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsArmoiresElectriques equipementsArmoiresElectriques;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsAuscultationsBarrages equipementsAuscultationsBarrages;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsAutomates equipementsAutomates;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsBacsRetention equipementsBacsRetention;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsBallons equipementsBallons;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsBennes equipementsBennes;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsBoitesBoue equipementsBoitesBoue;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsBouteillesChlore equipementsBouteillesChlore;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsBuselures equipementsBuselures;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsCaillebotis equipementsCaillebotis;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsCanauxComptage equipementsCanauxComptage;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsCapteurs equipementsCapteurs;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsCellulesPosteHT equipementsCellulesPosteHT;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsCentralesHydrauliques equipementsCentralesHydrauliques;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsCentrifugeuses equipementsCentrifugeuses;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsChariotsElevateurs equipementsChariotsElevateurs;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsChlorometres equipementsChlorometres;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsClapets equipementsClapets;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsClotures equipementsClotures;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsCollecteurs equipementsCollecteurs;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsColonnesForage equipementsColonnesForage;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsColonnesMontantes equipementsColonnesMontantes;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsCompteursElectriques equipementsCompteursElectriques;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsComptages equipementsComptages;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsCondensateurs equipementsCondensateurs;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsConvoyeursTransporteurs equipementsConvoyeursTransporteurs;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsCrepines equipementsCrepines;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsDemarreursElectriques equipementsDemarreursElectriques;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsDeshumidificateurs equipementsDeshumidificateurs;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsDestructeurOzone equipementsDestructeurOzone;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsDisconnecteurs equipementsDisconnecteurs;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsEchangeursChaleur equipementsEchangeursChaleur;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsEchelles equipementsEchelles;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsElectrodesReference equipementsElectrodesReference;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsEnregistreurs equipementsEnregistreurs;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsAntiIntrusions equipementsAntiIntrusions;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsProtectionsIncendie equipementsProtectionsIncendie;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsEspacesVert equipementsEspacesVert;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsExtracteursAir equipementsExtracteursAir;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsFenetres equipementsFenetres;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsFiltresFermes equipementsFiltresFermes;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsFiltresY equipementsFiltresY;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsGardesCorps equipementsGardesCorps;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsGenerateursUV equipementsGenerateursUV;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsGrappins equipementsGrappins;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsGroupesAir equipementsGroupesAir;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsGroupesDosage equipementsGroupesDosage;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsGroupesRefroidissement equipementsGroupesRefroidissement;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsGroupesElectrogenes equipementsGroupesElectrogenes;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsGroupesMalaxeurs equipementsGroupesMalaxeurs;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsHerses equipementsHerses;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsHydrocyclones equipementsHydrocyclones;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsHydroejecteurs equipementsHydroejecteurs;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsLiaisonsElectriques equipementsLiaisonsElectriques;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsMassesAnodiques equipementsMassesAnodiques;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsMaterielsLaboratoire equipementsMaterielsLaboratoire;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsMelangeursStatiques equipementsMelangeursStatiques;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsModulesLamellaires equipementsModulesLamellaires;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsMembranesUF equipementsMembranesUF;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsMoteursElectriquesManoeuvres equipementsMoteursElectriquesManoeuvres;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsOnduleurs equipementsOnduleurs;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsOuvragesEvacuationBarrage equipementsOuvragesEvacuationBarrage;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsOzoneurs equipementsOzoneurs;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsPointsprelevement equipementsPointsprelevement;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsPompes equipementsPompes;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsPontsRacleurs equipementsPontsRacleurs;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsPortes equipementsPortes;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsPortails equipementsPortails;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsPortillons equipementsPortillons;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsPortesSectionnelles equipementsPortesSectionnelles;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsPrefiltres equipementsPrefiltres;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsPreleveursEchantillonneurs equipementsPreleveursEchantillonneurs;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsPrelocalisateur equipementsPrelocalisateur;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsPressesBoue equipementsPressesBoue;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsReacteur equipementsReacteur;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsRetournementsTerres equipementsRetournementsTerres;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsSilos equipementsSilos;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsSecheursAir equipementsSecheursAir;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsSoupapes equipementsSoupapes;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsStabilisateursEcoulement equipementsStabilisateursEcoulement;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsStationsTeletransmission equipementsStationsTeletransmission;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsSystemeDiffusion equipementsSystemeDiffusion;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsTransformateursHT equipementsTransformateursHT;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsTransformateursRedresseurs equipementsTransformateursRedresseurs;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsTrappesAcces equipementsTrappesAcces;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsTubesCrepine equipementsTubesCrepine;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsTurbinesHydroelectriques equipementsTurbinesHydroelectriques;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsTuyauteries equipementsTuyauteries;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsVannes equipementsVannes;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsVariateursVitesse equipementsVariateursVitesse;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsVidanges equipementsVidanges;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsVentouses equipementsVentouses;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsVerins equipementsVerins;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsVisTransfert equipementsVisTransfert;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsVoiries equipementsVoiries;
    @XmlElement(required = true)
    protected Patrimoine.EquipementsVannesRegulation equipementsVannesRegulation;
    @XmlElement(required = true)
    protected Patrimoine.Composants composants;
    @XmlElement(required = true)
    protected Patrimoine.Interventions interventions;

    /**
     * Obtient la valeur de la propriété equipementsSocle.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsSocle }
     *     
     */
    public Patrimoine.EquipementsSocle getEquipementsSocle() {
        return equipementsSocle;
    }

    /**
     * Définit la valeur de la propriété equipementsSocle.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsSocle }
     *     
     */
    public void setEquipementsSocle(Patrimoine.EquipementsSocle value) {
        this.equipementsSocle = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsAerothermes.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsAerothermes }
     *     
     */
    public Patrimoine.EquipementsAerothermes getEquipementsAerothermes() {
        return equipementsAerothermes;
    }

    /**
     * Définit la valeur de la propriété equipementsAerothermes.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsAerothermes }
     *     
     */
    public void setEquipementsAerothermes(Patrimoine.EquipementsAerothermes value) {
        this.equipementsAerothermes = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsAgitateurs.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsAgitateurs }
     *     
     */
    public Patrimoine.EquipementsAgitateurs getEquipementsAgitateurs() {
        return equipementsAgitateurs;
    }

    /**
     * Définit la valeur de la propriété equipementsAgitateurs.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsAgitateurs }
     *     
     */
    public void setEquipementsAgitateurs(Patrimoine.EquipementsAgitateurs value) {
        this.equipementsAgitateurs = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsAppareilsLevage.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsAppareilsLevage }
     *     
     */
    public Patrimoine.EquipementsAppareilsLevage getEquipementsAppareilsLevage() {
        return equipementsAppareilsLevage;
    }

    /**
     * Définit la valeur de la propriété equipementsAppareilsLevage.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsAppareilsLevage }
     *     
     */
    public void setEquipementsAppareilsLevage(Patrimoine.EquipementsAppareilsLevage value) {
        this.equipementsAppareilsLevage = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsAspirateursBoues.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsAspirateursBoues }
     *     
     */
    public Patrimoine.EquipementsAspirateursBoues getEquipementsAspirateursBoues() {
        return equipementsAspirateursBoues;
    }

    /**
     * Définit la valeur de la propriété equipementsAspirateursBoues.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsAspirateursBoues }
     *     
     */
    public void setEquipementsAspirateursBoues(Patrimoine.EquipementsAspirateursBoues value) {
        this.equipementsAspirateursBoues = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsArmoiresElectriques.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsArmoiresElectriques }
     *     
     */
    public Patrimoine.EquipementsArmoiresElectriques getEquipementsArmoiresElectriques() {
        return equipementsArmoiresElectriques;
    }

    /**
     * Définit la valeur de la propriété equipementsArmoiresElectriques.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsArmoiresElectriques }
     *     
     */
    public void setEquipementsArmoiresElectriques(Patrimoine.EquipementsArmoiresElectriques value) {
        this.equipementsArmoiresElectriques = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsAuscultationsBarrages.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsAuscultationsBarrages }
     *     
     */
    public Patrimoine.EquipementsAuscultationsBarrages getEquipementsAuscultationsBarrages() {
        return equipementsAuscultationsBarrages;
    }

    /**
     * Définit la valeur de la propriété equipementsAuscultationsBarrages.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsAuscultationsBarrages }
     *     
     */
    public void setEquipementsAuscultationsBarrages(Patrimoine.EquipementsAuscultationsBarrages value) {
        this.equipementsAuscultationsBarrages = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsAutomates.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsAutomates }
     *     
     */
    public Patrimoine.EquipementsAutomates getEquipementsAutomates() {
        return equipementsAutomates;
    }

    /**
     * Définit la valeur de la propriété equipementsAutomates.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsAutomates }
     *     
     */
    public void setEquipementsAutomates(Patrimoine.EquipementsAutomates value) {
        this.equipementsAutomates = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsBacsRetention.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsBacsRetention }
     *     
     */
    public Patrimoine.EquipementsBacsRetention getEquipementsBacsRetention() {
        return equipementsBacsRetention;
    }

    /**
     * Définit la valeur de la propriété equipementsBacsRetention.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsBacsRetention }
     *     
     */
    public void setEquipementsBacsRetention(Patrimoine.EquipementsBacsRetention value) {
        this.equipementsBacsRetention = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsBallons.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsBallons }
     *     
     */
    public Patrimoine.EquipementsBallons getEquipementsBallons() {
        return equipementsBallons;
    }

    /**
     * Définit la valeur de la propriété equipementsBallons.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsBallons }
     *     
     */
    public void setEquipementsBallons(Patrimoine.EquipementsBallons value) {
        this.equipementsBallons = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsBennes.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsBennes }
     *     
     */
    public Patrimoine.EquipementsBennes getEquipementsBennes() {
        return equipementsBennes;
    }

    /**
     * Définit la valeur de la propriété equipementsBennes.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsBennes }
     *     
     */
    public void setEquipementsBennes(Patrimoine.EquipementsBennes value) {
        this.equipementsBennes = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsBoitesBoue.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsBoitesBoue }
     *     
     */
    public Patrimoine.EquipementsBoitesBoue getEquipementsBoitesBoue() {
        return equipementsBoitesBoue;
    }

    /**
     * Définit la valeur de la propriété equipementsBoitesBoue.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsBoitesBoue }
     *     
     */
    public void setEquipementsBoitesBoue(Patrimoine.EquipementsBoitesBoue value) {
        this.equipementsBoitesBoue = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsBouteillesChlore.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsBouteillesChlore }
     *     
     */
    public Patrimoine.EquipementsBouteillesChlore getEquipementsBouteillesChlore() {
        return equipementsBouteillesChlore;
    }

    /**
     * Définit la valeur de la propriété equipementsBouteillesChlore.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsBouteillesChlore }
     *     
     */
    public void setEquipementsBouteillesChlore(Patrimoine.EquipementsBouteillesChlore value) {
        this.equipementsBouteillesChlore = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsBuselures.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsBuselures }
     *     
     */
    public Patrimoine.EquipementsBuselures getEquipementsBuselures() {
        return equipementsBuselures;
    }

    /**
     * Définit la valeur de la propriété equipementsBuselures.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsBuselures }
     *     
     */
    public void setEquipementsBuselures(Patrimoine.EquipementsBuselures value) {
        this.equipementsBuselures = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsCaillebotis.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsCaillebotis }
     *     
     */
    public Patrimoine.EquipementsCaillebotis getEquipementsCaillebotis() {
        return equipementsCaillebotis;
    }

    /**
     * Définit la valeur de la propriété equipementsCaillebotis.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsCaillebotis }
     *     
     */
    public void setEquipementsCaillebotis(Patrimoine.EquipementsCaillebotis value) {
        this.equipementsCaillebotis = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsCanauxComptage.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsCanauxComptage }
     *     
     */
    public Patrimoine.EquipementsCanauxComptage getEquipementsCanauxComptage() {
        return equipementsCanauxComptage;
    }

    /**
     * Définit la valeur de la propriété equipementsCanauxComptage.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsCanauxComptage }
     *     
     */
    public void setEquipementsCanauxComptage(Patrimoine.EquipementsCanauxComptage value) {
        this.equipementsCanauxComptage = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsCapteurs.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsCapteurs }
     *     
     */
    public Patrimoine.EquipementsCapteurs getEquipementsCapteurs() {
        return equipementsCapteurs;
    }

    /**
     * Définit la valeur de la propriété equipementsCapteurs.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsCapteurs }
     *     
     */
    public void setEquipementsCapteurs(Patrimoine.EquipementsCapteurs value) {
        this.equipementsCapteurs = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsCellulesPosteHT.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsCellulesPosteHT }
     *     
     */
    public Patrimoine.EquipementsCellulesPosteHT getEquipementsCellulesPosteHT() {
        return equipementsCellulesPosteHT;
    }

    /**
     * Définit la valeur de la propriété equipementsCellulesPosteHT.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsCellulesPosteHT }
     *     
     */
    public void setEquipementsCellulesPosteHT(Patrimoine.EquipementsCellulesPosteHT value) {
        this.equipementsCellulesPosteHT = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsCentralesHydrauliques.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsCentralesHydrauliques }
     *     
     */
    public Patrimoine.EquipementsCentralesHydrauliques getEquipementsCentralesHydrauliques() {
        return equipementsCentralesHydrauliques;
    }

    /**
     * Définit la valeur de la propriété equipementsCentralesHydrauliques.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsCentralesHydrauliques }
     *     
     */
    public void setEquipementsCentralesHydrauliques(Patrimoine.EquipementsCentralesHydrauliques value) {
        this.equipementsCentralesHydrauliques = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsCentrifugeuses.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsCentrifugeuses }
     *     
     */
    public Patrimoine.EquipementsCentrifugeuses getEquipementsCentrifugeuses() {
        return equipementsCentrifugeuses;
    }

    /**
     * Définit la valeur de la propriété equipementsCentrifugeuses.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsCentrifugeuses }
     *     
     */
    public void setEquipementsCentrifugeuses(Patrimoine.EquipementsCentrifugeuses value) {
        this.equipementsCentrifugeuses = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsChariotsElevateurs.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsChariotsElevateurs }
     *     
     */
    public Patrimoine.EquipementsChariotsElevateurs getEquipementsChariotsElevateurs() {
        return equipementsChariotsElevateurs;
    }

    /**
     * Définit la valeur de la propriété equipementsChariotsElevateurs.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsChariotsElevateurs }
     *     
     */
    public void setEquipementsChariotsElevateurs(Patrimoine.EquipementsChariotsElevateurs value) {
        this.equipementsChariotsElevateurs = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsChlorometres.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsChlorometres }
     *     
     */
    public Patrimoine.EquipementsChlorometres getEquipementsChlorometres() {
        return equipementsChlorometres;
    }

    /**
     * Définit la valeur de la propriété equipementsChlorometres.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsChlorometres }
     *     
     */
    public void setEquipementsChlorometres(Patrimoine.EquipementsChlorometres value) {
        this.equipementsChlorometres = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsClapets.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsClapets }
     *     
     */
    public Patrimoine.EquipementsClapets getEquipementsClapets() {
        return equipementsClapets;
    }

    /**
     * Définit la valeur de la propriété equipementsClapets.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsClapets }
     *     
     */
    public void setEquipementsClapets(Patrimoine.EquipementsClapets value) {
        this.equipementsClapets = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsClotures.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsClotures }
     *     
     */
    public Patrimoine.EquipementsClotures getEquipementsClotures() {
        return equipementsClotures;
    }

    /**
     * Définit la valeur de la propriété equipementsClotures.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsClotures }
     *     
     */
    public void setEquipementsClotures(Patrimoine.EquipementsClotures value) {
        this.equipementsClotures = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsCollecteurs.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsCollecteurs }
     *     
     */
    public Patrimoine.EquipementsCollecteurs getEquipementsCollecteurs() {
        return equipementsCollecteurs;
    }

    /**
     * Définit la valeur de la propriété equipementsCollecteurs.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsCollecteurs }
     *     
     */
    public void setEquipementsCollecteurs(Patrimoine.EquipementsCollecteurs value) {
        this.equipementsCollecteurs = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsColonnesForage.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsColonnesForage }
     *     
     */
    public Patrimoine.EquipementsColonnesForage getEquipementsColonnesForage() {
        return equipementsColonnesForage;
    }

    /**
     * Définit la valeur de la propriété equipementsColonnesForage.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsColonnesForage }
     *     
     */
    public void setEquipementsColonnesForage(Patrimoine.EquipementsColonnesForage value) {
        this.equipementsColonnesForage = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsColonnesMontantes.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsColonnesMontantes }
     *     
     */
    public Patrimoine.EquipementsColonnesMontantes getEquipementsColonnesMontantes() {
        return equipementsColonnesMontantes;
    }

    /**
     * Définit la valeur de la propriété equipementsColonnesMontantes.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsColonnesMontantes }
     *     
     */
    public void setEquipementsColonnesMontantes(Patrimoine.EquipementsColonnesMontantes value) {
        this.equipementsColonnesMontantes = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsCompteursElectriques.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsCompteursElectriques }
     *     
     */
    public Patrimoine.EquipementsCompteursElectriques getEquipementsCompteursElectriques() {
        return equipementsCompteursElectriques;
    }

    /**
     * Définit la valeur de la propriété equipementsCompteursElectriques.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsCompteursElectriques }
     *     
     */
    public void setEquipementsCompteursElectriques(Patrimoine.EquipementsCompteursElectriques value) {
        this.equipementsCompteursElectriques = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsComptages.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsComptages }
     *     
     */
    public Patrimoine.EquipementsComptages getEquipementsComptages() {
        return equipementsComptages;
    }

    /**
     * Définit la valeur de la propriété equipementsComptages.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsComptages }
     *     
     */
    public void setEquipementsComptages(Patrimoine.EquipementsComptages value) {
        this.equipementsComptages = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsCondensateurs.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsCondensateurs }
     *     
     */
    public Patrimoine.EquipementsCondensateurs getEquipementsCondensateurs() {
        return equipementsCondensateurs;
    }

    /**
     * Définit la valeur de la propriété equipementsCondensateurs.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsCondensateurs }
     *     
     */
    public void setEquipementsCondensateurs(Patrimoine.EquipementsCondensateurs value) {
        this.equipementsCondensateurs = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsConvoyeursTransporteurs.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsConvoyeursTransporteurs }
     *     
     */
    public Patrimoine.EquipementsConvoyeursTransporteurs getEquipementsConvoyeursTransporteurs() {
        return equipementsConvoyeursTransporteurs;
    }

    /**
     * Définit la valeur de la propriété equipementsConvoyeursTransporteurs.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsConvoyeursTransporteurs }
     *     
     */
    public void setEquipementsConvoyeursTransporteurs(Patrimoine.EquipementsConvoyeursTransporteurs value) {
        this.equipementsConvoyeursTransporteurs = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsCrepines.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsCrepines }
     *     
     */
    public Patrimoine.EquipementsCrepines getEquipementsCrepines() {
        return equipementsCrepines;
    }

    /**
     * Définit la valeur de la propriété equipementsCrepines.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsCrepines }
     *     
     */
    public void setEquipementsCrepines(Patrimoine.EquipementsCrepines value) {
        this.equipementsCrepines = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsDemarreursElectriques.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsDemarreursElectriques }
     *     
     */
    public Patrimoine.EquipementsDemarreursElectriques getEquipementsDemarreursElectriques() {
        return equipementsDemarreursElectriques;
    }

    /**
     * Définit la valeur de la propriété equipementsDemarreursElectriques.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsDemarreursElectriques }
     *     
     */
    public void setEquipementsDemarreursElectriques(Patrimoine.EquipementsDemarreursElectriques value) {
        this.equipementsDemarreursElectriques = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsDeshumidificateurs.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsDeshumidificateurs }
     *     
     */
    public Patrimoine.EquipementsDeshumidificateurs getEquipementsDeshumidificateurs() {
        return equipementsDeshumidificateurs;
    }

    /**
     * Définit la valeur de la propriété equipementsDeshumidificateurs.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsDeshumidificateurs }
     *     
     */
    public void setEquipementsDeshumidificateurs(Patrimoine.EquipementsDeshumidificateurs value) {
        this.equipementsDeshumidificateurs = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsDestructeurOzone.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsDestructeurOzone }
     *     
     */
    public Patrimoine.EquipementsDestructeurOzone getEquipementsDestructeurOzone() {
        return equipementsDestructeurOzone;
    }

    /**
     * Définit la valeur de la propriété equipementsDestructeurOzone.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsDestructeurOzone }
     *     
     */
    public void setEquipementsDestructeurOzone(Patrimoine.EquipementsDestructeurOzone value) {
        this.equipementsDestructeurOzone = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsDisconnecteurs.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsDisconnecteurs }
     *     
     */
    public Patrimoine.EquipementsDisconnecteurs getEquipementsDisconnecteurs() {
        return equipementsDisconnecteurs;
    }

    /**
     * Définit la valeur de la propriété equipementsDisconnecteurs.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsDisconnecteurs }
     *     
     */
    public void setEquipementsDisconnecteurs(Patrimoine.EquipementsDisconnecteurs value) {
        this.equipementsDisconnecteurs = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsEchangeursChaleur.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsEchangeursChaleur }
     *     
     */
    public Patrimoine.EquipementsEchangeursChaleur getEquipementsEchangeursChaleur() {
        return equipementsEchangeursChaleur;
    }

    /**
     * Définit la valeur de la propriété equipementsEchangeursChaleur.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsEchangeursChaleur }
     *     
     */
    public void setEquipementsEchangeursChaleur(Patrimoine.EquipementsEchangeursChaleur value) {
        this.equipementsEchangeursChaleur = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsEchelles.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsEchelles }
     *     
     */
    public Patrimoine.EquipementsEchelles getEquipementsEchelles() {
        return equipementsEchelles;
    }

    /**
     * Définit la valeur de la propriété equipementsEchelles.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsEchelles }
     *     
     */
    public void setEquipementsEchelles(Patrimoine.EquipementsEchelles value) {
        this.equipementsEchelles = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsElectrodesReference.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsElectrodesReference }
     *     
     */
    public Patrimoine.EquipementsElectrodesReference getEquipementsElectrodesReference() {
        return equipementsElectrodesReference;
    }

    /**
     * Définit la valeur de la propriété equipementsElectrodesReference.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsElectrodesReference }
     *     
     */
    public void setEquipementsElectrodesReference(Patrimoine.EquipementsElectrodesReference value) {
        this.equipementsElectrodesReference = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsEnregistreurs.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsEnregistreurs }
     *     
     */
    public Patrimoine.EquipementsEnregistreurs getEquipementsEnregistreurs() {
        return equipementsEnregistreurs;
    }

    /**
     * Définit la valeur de la propriété equipementsEnregistreurs.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsEnregistreurs }
     *     
     */
    public void setEquipementsEnregistreurs(Patrimoine.EquipementsEnregistreurs value) {
        this.equipementsEnregistreurs = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsAntiIntrusions.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsAntiIntrusions }
     *     
     */
    public Patrimoine.EquipementsAntiIntrusions getEquipementsAntiIntrusions() {
        return equipementsAntiIntrusions;
    }

    /**
     * Définit la valeur de la propriété equipementsAntiIntrusions.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsAntiIntrusions }
     *     
     */
    public void setEquipementsAntiIntrusions(Patrimoine.EquipementsAntiIntrusions value) {
        this.equipementsAntiIntrusions = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsProtectionsIncendie.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsProtectionsIncendie }
     *     
     */
    public Patrimoine.EquipementsProtectionsIncendie getEquipementsProtectionsIncendie() {
        return equipementsProtectionsIncendie;
    }

    /**
     * Définit la valeur de la propriété equipementsProtectionsIncendie.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsProtectionsIncendie }
     *     
     */
    public void setEquipementsProtectionsIncendie(Patrimoine.EquipementsProtectionsIncendie value) {
        this.equipementsProtectionsIncendie = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsEspacesVert.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsEspacesVert }
     *     
     */
    public Patrimoine.EquipementsEspacesVert getEquipementsEspacesVert() {
        return equipementsEspacesVert;
    }

    /**
     * Définit la valeur de la propriété equipementsEspacesVert.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsEspacesVert }
     *     
     */
    public void setEquipementsEspacesVert(Patrimoine.EquipementsEspacesVert value) {
        this.equipementsEspacesVert = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsExtracteursAir.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsExtracteursAir }
     *     
     */
    public Patrimoine.EquipementsExtracteursAir getEquipementsExtracteursAir() {
        return equipementsExtracteursAir;
    }

    /**
     * Définit la valeur de la propriété equipementsExtracteursAir.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsExtracteursAir }
     *     
     */
    public void setEquipementsExtracteursAir(Patrimoine.EquipementsExtracteursAir value) {
        this.equipementsExtracteursAir = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsFenetres.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsFenetres }
     *     
     */
    public Patrimoine.EquipementsFenetres getEquipementsFenetres() {
        return equipementsFenetres;
    }

    /**
     * Définit la valeur de la propriété equipementsFenetres.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsFenetres }
     *     
     */
    public void setEquipementsFenetres(Patrimoine.EquipementsFenetres value) {
        this.equipementsFenetres = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsFiltresFermes.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsFiltresFermes }
     *     
     */
    public Patrimoine.EquipementsFiltresFermes getEquipementsFiltresFermes() {
        return equipementsFiltresFermes;
    }

    /**
     * Définit la valeur de la propriété equipementsFiltresFermes.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsFiltresFermes }
     *     
     */
    public void setEquipementsFiltresFermes(Patrimoine.EquipementsFiltresFermes value) {
        this.equipementsFiltresFermes = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsFiltresY.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsFiltresY }
     *     
     */
    public Patrimoine.EquipementsFiltresY getEquipementsFiltresY() {
        return equipementsFiltresY;
    }

    /**
     * Définit la valeur de la propriété equipementsFiltresY.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsFiltresY }
     *     
     */
    public void setEquipementsFiltresY(Patrimoine.EquipementsFiltresY value) {
        this.equipementsFiltresY = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsGardesCorps.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsGardesCorps }
     *     
     */
    public Patrimoine.EquipementsGardesCorps getEquipementsGardesCorps() {
        return equipementsGardesCorps;
    }

    /**
     * Définit la valeur de la propriété equipementsGardesCorps.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsGardesCorps }
     *     
     */
    public void setEquipementsGardesCorps(Patrimoine.EquipementsGardesCorps value) {
        this.equipementsGardesCorps = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsGenerateursUV.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsGenerateursUV }
     *     
     */
    public Patrimoine.EquipementsGenerateursUV getEquipementsGenerateursUV() {
        return equipementsGenerateursUV;
    }

    /**
     * Définit la valeur de la propriété equipementsGenerateursUV.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsGenerateursUV }
     *     
     */
    public void setEquipementsGenerateursUV(Patrimoine.EquipementsGenerateursUV value) {
        this.equipementsGenerateursUV = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsGrappins.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsGrappins }
     *     
     */
    public Patrimoine.EquipementsGrappins getEquipementsGrappins() {
        return equipementsGrappins;
    }

    /**
     * Définit la valeur de la propriété equipementsGrappins.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsGrappins }
     *     
     */
    public void setEquipementsGrappins(Patrimoine.EquipementsGrappins value) {
        this.equipementsGrappins = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsGroupesAir.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsGroupesAir }
     *     
     */
    public Patrimoine.EquipementsGroupesAir getEquipementsGroupesAir() {
        return equipementsGroupesAir;
    }

    /**
     * Définit la valeur de la propriété equipementsGroupesAir.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsGroupesAir }
     *     
     */
    public void setEquipementsGroupesAir(Patrimoine.EquipementsGroupesAir value) {
        this.equipementsGroupesAir = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsGroupesDosage.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsGroupesDosage }
     *     
     */
    public Patrimoine.EquipementsGroupesDosage getEquipementsGroupesDosage() {
        return equipementsGroupesDosage;
    }

    /**
     * Définit la valeur de la propriété equipementsGroupesDosage.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsGroupesDosage }
     *     
     */
    public void setEquipementsGroupesDosage(Patrimoine.EquipementsGroupesDosage value) {
        this.equipementsGroupesDosage = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsGroupesRefroidissement.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsGroupesRefroidissement }
     *     
     */
    public Patrimoine.EquipementsGroupesRefroidissement getEquipementsGroupesRefroidissement() {
        return equipementsGroupesRefroidissement;
    }

    /**
     * Définit la valeur de la propriété equipementsGroupesRefroidissement.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsGroupesRefroidissement }
     *     
     */
    public void setEquipementsGroupesRefroidissement(Patrimoine.EquipementsGroupesRefroidissement value) {
        this.equipementsGroupesRefroidissement = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsGroupesElectrogenes.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsGroupesElectrogenes }
     *     
     */
    public Patrimoine.EquipementsGroupesElectrogenes getEquipementsGroupesElectrogenes() {
        return equipementsGroupesElectrogenes;
    }

    /**
     * Définit la valeur de la propriété equipementsGroupesElectrogenes.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsGroupesElectrogenes }
     *     
     */
    public void setEquipementsGroupesElectrogenes(Patrimoine.EquipementsGroupesElectrogenes value) {
        this.equipementsGroupesElectrogenes = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsGroupesMalaxeurs.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsGroupesMalaxeurs }
     *     
     */
    public Patrimoine.EquipementsGroupesMalaxeurs getEquipementsGroupesMalaxeurs() {
        return equipementsGroupesMalaxeurs;
    }

    /**
     * Définit la valeur de la propriété equipementsGroupesMalaxeurs.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsGroupesMalaxeurs }
     *     
     */
    public void setEquipementsGroupesMalaxeurs(Patrimoine.EquipementsGroupesMalaxeurs value) {
        this.equipementsGroupesMalaxeurs = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsHerses.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsHerses }
     *     
     */
    public Patrimoine.EquipementsHerses getEquipementsHerses() {
        return equipementsHerses;
    }

    /**
     * Définit la valeur de la propriété equipementsHerses.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsHerses }
     *     
     */
    public void setEquipementsHerses(Patrimoine.EquipementsHerses value) {
        this.equipementsHerses = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsHydrocyclones.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsHydrocyclones }
     *     
     */
    public Patrimoine.EquipementsHydrocyclones getEquipementsHydrocyclones() {
        return equipementsHydrocyclones;
    }

    /**
     * Définit la valeur de la propriété equipementsHydrocyclones.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsHydrocyclones }
     *     
     */
    public void setEquipementsHydrocyclones(Patrimoine.EquipementsHydrocyclones value) {
        this.equipementsHydrocyclones = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsHydroejecteurs.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsHydroejecteurs }
     *     
     */
    public Patrimoine.EquipementsHydroejecteurs getEquipementsHydroejecteurs() {
        return equipementsHydroejecteurs;
    }

    /**
     * Définit la valeur de la propriété equipementsHydroejecteurs.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsHydroejecteurs }
     *     
     */
    public void setEquipementsHydroejecteurs(Patrimoine.EquipementsHydroejecteurs value) {
        this.equipementsHydroejecteurs = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsLiaisonsElectriques.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsLiaisonsElectriques }
     *     
     */
    public Patrimoine.EquipementsLiaisonsElectriques getEquipementsLiaisonsElectriques() {
        return equipementsLiaisonsElectriques;
    }

    /**
     * Définit la valeur de la propriété equipementsLiaisonsElectriques.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsLiaisonsElectriques }
     *     
     */
    public void setEquipementsLiaisonsElectriques(Patrimoine.EquipementsLiaisonsElectriques value) {
        this.equipementsLiaisonsElectriques = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsMassesAnodiques.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsMassesAnodiques }
     *     
     */
    public Patrimoine.EquipementsMassesAnodiques getEquipementsMassesAnodiques() {
        return equipementsMassesAnodiques;
    }

    /**
     * Définit la valeur de la propriété equipementsMassesAnodiques.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsMassesAnodiques }
     *     
     */
    public void setEquipementsMassesAnodiques(Patrimoine.EquipementsMassesAnodiques value) {
        this.equipementsMassesAnodiques = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsMaterielsLaboratoire.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsMaterielsLaboratoire }
     *     
     */
    public Patrimoine.EquipementsMaterielsLaboratoire getEquipementsMaterielsLaboratoire() {
        return equipementsMaterielsLaboratoire;
    }

    /**
     * Définit la valeur de la propriété equipementsMaterielsLaboratoire.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsMaterielsLaboratoire }
     *     
     */
    public void setEquipementsMaterielsLaboratoire(Patrimoine.EquipementsMaterielsLaboratoire value) {
        this.equipementsMaterielsLaboratoire = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsMelangeursStatiques.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsMelangeursStatiques }
     *     
     */
    public Patrimoine.EquipementsMelangeursStatiques getEquipementsMelangeursStatiques() {
        return equipementsMelangeursStatiques;
    }

    /**
     * Définit la valeur de la propriété equipementsMelangeursStatiques.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsMelangeursStatiques }
     *     
     */
    public void setEquipementsMelangeursStatiques(Patrimoine.EquipementsMelangeursStatiques value) {
        this.equipementsMelangeursStatiques = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsModulesLamellaires.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsModulesLamellaires }
     *     
     */
    public Patrimoine.EquipementsModulesLamellaires getEquipementsModulesLamellaires() {
        return equipementsModulesLamellaires;
    }

    /**
     * Définit la valeur de la propriété equipementsModulesLamellaires.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsModulesLamellaires }
     *     
     */
    public void setEquipementsModulesLamellaires(Patrimoine.EquipementsModulesLamellaires value) {
        this.equipementsModulesLamellaires = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsMembranesUF.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsMembranesUF }
     *     
     */
    public Patrimoine.EquipementsMembranesUF getEquipementsMembranesUF() {
        return equipementsMembranesUF;
    }

    /**
     * Définit la valeur de la propriété equipementsMembranesUF.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsMembranesUF }
     *     
     */
    public void setEquipementsMembranesUF(Patrimoine.EquipementsMembranesUF value) {
        this.equipementsMembranesUF = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsMoteursElectriquesManoeuvres.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsMoteursElectriquesManoeuvres }
     *     
     */
    public Patrimoine.EquipementsMoteursElectriquesManoeuvres getEquipementsMoteursElectriquesManoeuvres() {
        return equipementsMoteursElectriquesManoeuvres;
    }

    /**
     * Définit la valeur de la propriété equipementsMoteursElectriquesManoeuvres.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsMoteursElectriquesManoeuvres }
     *     
     */
    public void setEquipementsMoteursElectriquesManoeuvres(Patrimoine.EquipementsMoteursElectriquesManoeuvres value) {
        this.equipementsMoteursElectriquesManoeuvres = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsOnduleurs.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsOnduleurs }
     *     
     */
    public Patrimoine.EquipementsOnduleurs getEquipementsOnduleurs() {
        return equipementsOnduleurs;
    }

    /**
     * Définit la valeur de la propriété equipementsOnduleurs.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsOnduleurs }
     *     
     */
    public void setEquipementsOnduleurs(Patrimoine.EquipementsOnduleurs value) {
        this.equipementsOnduleurs = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsOuvragesEvacuationBarrage.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsOuvragesEvacuationBarrage }
     *     
     */
    public Patrimoine.EquipementsOuvragesEvacuationBarrage getEquipementsOuvragesEvacuationBarrage() {
        return equipementsOuvragesEvacuationBarrage;
    }

    /**
     * Définit la valeur de la propriété equipementsOuvragesEvacuationBarrage.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsOuvragesEvacuationBarrage }
     *     
     */
    public void setEquipementsOuvragesEvacuationBarrage(Patrimoine.EquipementsOuvragesEvacuationBarrage value) {
        this.equipementsOuvragesEvacuationBarrage = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsOzoneurs.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsOzoneurs }
     *     
     */
    public Patrimoine.EquipementsOzoneurs getEquipementsOzoneurs() {
        return equipementsOzoneurs;
    }

    /**
     * Définit la valeur de la propriété equipementsOzoneurs.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsOzoneurs }
     *     
     */
    public void setEquipementsOzoneurs(Patrimoine.EquipementsOzoneurs value) {
        this.equipementsOzoneurs = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsPointsprelevement.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsPointsprelevement }
     *     
     */
    public Patrimoine.EquipementsPointsprelevement getEquipementsPointsprelevement() {
        return equipementsPointsprelevement;
    }

    /**
     * Définit la valeur de la propriété equipementsPointsprelevement.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsPointsprelevement }
     *     
     */
    public void setEquipementsPointsprelevement(Patrimoine.EquipementsPointsprelevement value) {
        this.equipementsPointsprelevement = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsPompes.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsPompes }
     *     
     */
    public Patrimoine.EquipementsPompes getEquipementsPompes() {
        return equipementsPompes;
    }

    /**
     * Définit la valeur de la propriété equipementsPompes.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsPompes }
     *     
     */
    public void setEquipementsPompes(Patrimoine.EquipementsPompes value) {
        this.equipementsPompes = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsPontsRacleurs.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsPontsRacleurs }
     *     
     */
    public Patrimoine.EquipementsPontsRacleurs getEquipementsPontsRacleurs() {
        return equipementsPontsRacleurs;
    }

    /**
     * Définit la valeur de la propriété equipementsPontsRacleurs.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsPontsRacleurs }
     *     
     */
    public void setEquipementsPontsRacleurs(Patrimoine.EquipementsPontsRacleurs value) {
        this.equipementsPontsRacleurs = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsPortes.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsPortes }
     *     
     */
    public Patrimoine.EquipementsPortes getEquipementsPortes() {
        return equipementsPortes;
    }

    /**
     * Définit la valeur de la propriété equipementsPortes.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsPortes }
     *     
     */
    public void setEquipementsPortes(Patrimoine.EquipementsPortes value) {
        this.equipementsPortes = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsPortails.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsPortails }
     *     
     */
    public Patrimoine.EquipementsPortails getEquipementsPortails() {
        return equipementsPortails;
    }

    /**
     * Définit la valeur de la propriété equipementsPortails.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsPortails }
     *     
     */
    public void setEquipementsPortails(Patrimoine.EquipementsPortails value) {
        this.equipementsPortails = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsPortillons.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsPortillons }
     *     
     */
    public Patrimoine.EquipementsPortillons getEquipementsPortillons() {
        return equipementsPortillons;
    }

    /**
     * Définit la valeur de la propriété equipementsPortillons.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsPortillons }
     *     
     */
    public void setEquipementsPortillons(Patrimoine.EquipementsPortillons value) {
        this.equipementsPortillons = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsPortesSectionnelles.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsPortesSectionnelles }
     *     
     */
    public Patrimoine.EquipementsPortesSectionnelles getEquipementsPortesSectionnelles() {
        return equipementsPortesSectionnelles;
    }

    /**
     * Définit la valeur de la propriété equipementsPortesSectionnelles.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsPortesSectionnelles }
     *     
     */
    public void setEquipementsPortesSectionnelles(Patrimoine.EquipementsPortesSectionnelles value) {
        this.equipementsPortesSectionnelles = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsPrefiltres.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsPrefiltres }
     *     
     */
    public Patrimoine.EquipementsPrefiltres getEquipementsPrefiltres() {
        return equipementsPrefiltres;
    }

    /**
     * Définit la valeur de la propriété equipementsPrefiltres.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsPrefiltres }
     *     
     */
    public void setEquipementsPrefiltres(Patrimoine.EquipementsPrefiltres value) {
        this.equipementsPrefiltres = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsPreleveursEchantillonneurs.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsPreleveursEchantillonneurs }
     *     
     */
    public Patrimoine.EquipementsPreleveursEchantillonneurs getEquipementsPreleveursEchantillonneurs() {
        return equipementsPreleveursEchantillonneurs;
    }

    /**
     * Définit la valeur de la propriété equipementsPreleveursEchantillonneurs.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsPreleveursEchantillonneurs }
     *     
     */
    public void setEquipementsPreleveursEchantillonneurs(Patrimoine.EquipementsPreleveursEchantillonneurs value) {
        this.equipementsPreleveursEchantillonneurs = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsPrelocalisateur.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsPrelocalisateur }
     *     
     */
    public Patrimoine.EquipementsPrelocalisateur getEquipementsPrelocalisateur() {
        return equipementsPrelocalisateur;
    }

    /**
     * Définit la valeur de la propriété equipementsPrelocalisateur.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsPrelocalisateur }
     *     
     */
    public void setEquipementsPrelocalisateur(Patrimoine.EquipementsPrelocalisateur value) {
        this.equipementsPrelocalisateur = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsPressesBoue.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsPressesBoue }
     *     
     */
    public Patrimoine.EquipementsPressesBoue getEquipementsPressesBoue() {
        return equipementsPressesBoue;
    }

    /**
     * Définit la valeur de la propriété equipementsPressesBoue.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsPressesBoue }
     *     
     */
    public void setEquipementsPressesBoue(Patrimoine.EquipementsPressesBoue value) {
        this.equipementsPressesBoue = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsReacteur.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsReacteur }
     *     
     */
    public Patrimoine.EquipementsReacteur getEquipementsReacteur() {
        return equipementsReacteur;
    }

    /**
     * Définit la valeur de la propriété equipementsReacteur.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsReacteur }
     *     
     */
    public void setEquipementsReacteur(Patrimoine.EquipementsReacteur value) {
        this.equipementsReacteur = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsRetournementsTerres.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsRetournementsTerres }
     *     
     */
    public Patrimoine.EquipementsRetournementsTerres getEquipementsRetournementsTerres() {
        return equipementsRetournementsTerres;
    }

    /**
     * Définit la valeur de la propriété equipementsRetournementsTerres.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsRetournementsTerres }
     *     
     */
    public void setEquipementsRetournementsTerres(Patrimoine.EquipementsRetournementsTerres value) {
        this.equipementsRetournementsTerres = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsSilos.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsSilos }
     *     
     */
    public Patrimoine.EquipementsSilos getEquipementsSilos() {
        return equipementsSilos;
    }

    /**
     * Définit la valeur de la propriété equipementsSilos.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsSilos }
     *     
     */
    public void setEquipementsSilos(Patrimoine.EquipementsSilos value) {
        this.equipementsSilos = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsSecheursAir.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsSecheursAir }
     *     
     */
    public Patrimoine.EquipementsSecheursAir getEquipementsSecheursAir() {
        return equipementsSecheursAir;
    }

    /**
     * Définit la valeur de la propriété equipementsSecheursAir.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsSecheursAir }
     *     
     */
    public void setEquipementsSecheursAir(Patrimoine.EquipementsSecheursAir value) {
        this.equipementsSecheursAir = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsSoupapes.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsSoupapes }
     *     
     */
    public Patrimoine.EquipementsSoupapes getEquipementsSoupapes() {
        return equipementsSoupapes;
    }

    /**
     * Définit la valeur de la propriété equipementsSoupapes.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsSoupapes }
     *     
     */
    public void setEquipementsSoupapes(Patrimoine.EquipementsSoupapes value) {
        this.equipementsSoupapes = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsStabilisateursEcoulement.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsStabilisateursEcoulement }
     *     
     */
    public Patrimoine.EquipementsStabilisateursEcoulement getEquipementsStabilisateursEcoulement() {
        return equipementsStabilisateursEcoulement;
    }

    /**
     * Définit la valeur de la propriété equipementsStabilisateursEcoulement.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsStabilisateursEcoulement }
     *     
     */
    public void setEquipementsStabilisateursEcoulement(Patrimoine.EquipementsStabilisateursEcoulement value) {
        this.equipementsStabilisateursEcoulement = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsStationsTeletransmission.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsStationsTeletransmission }
     *     
     */
    public Patrimoine.EquipementsStationsTeletransmission getEquipementsStationsTeletransmission() {
        return equipementsStationsTeletransmission;
    }

    /**
     * Définit la valeur de la propriété equipementsStationsTeletransmission.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsStationsTeletransmission }
     *     
     */
    public void setEquipementsStationsTeletransmission(Patrimoine.EquipementsStationsTeletransmission value) {
        this.equipementsStationsTeletransmission = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsSystemeDiffusion.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsSystemeDiffusion }
     *     
     */
    public Patrimoine.EquipementsSystemeDiffusion getEquipementsSystemeDiffusion() {
        return equipementsSystemeDiffusion;
    }

    /**
     * Définit la valeur de la propriété equipementsSystemeDiffusion.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsSystemeDiffusion }
     *     
     */
    public void setEquipementsSystemeDiffusion(Patrimoine.EquipementsSystemeDiffusion value) {
        this.equipementsSystemeDiffusion = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsTransformateursHT.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsTransformateursHT }
     *     
     */
    public Patrimoine.EquipementsTransformateursHT getEquipementsTransformateursHT() {
        return equipementsTransformateursHT;
    }

    /**
     * Définit la valeur de la propriété equipementsTransformateursHT.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsTransformateursHT }
     *     
     */
    public void setEquipementsTransformateursHT(Patrimoine.EquipementsTransformateursHT value) {
        this.equipementsTransformateursHT = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsTransformateursRedresseurs.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsTransformateursRedresseurs }
     *     
     */
    public Patrimoine.EquipementsTransformateursRedresseurs getEquipementsTransformateursRedresseurs() {
        return equipementsTransformateursRedresseurs;
    }

    /**
     * Définit la valeur de la propriété equipementsTransformateursRedresseurs.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsTransformateursRedresseurs }
     *     
     */
    public void setEquipementsTransformateursRedresseurs(Patrimoine.EquipementsTransformateursRedresseurs value) {
        this.equipementsTransformateursRedresseurs = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsTrappesAcces.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsTrappesAcces }
     *     
     */
    public Patrimoine.EquipementsTrappesAcces getEquipementsTrappesAcces() {
        return equipementsTrappesAcces;
    }

    /**
     * Définit la valeur de la propriété equipementsTrappesAcces.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsTrappesAcces }
     *     
     */
    public void setEquipementsTrappesAcces(Patrimoine.EquipementsTrappesAcces value) {
        this.equipementsTrappesAcces = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsTubesCrepine.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsTubesCrepine }
     *     
     */
    public Patrimoine.EquipementsTubesCrepine getEquipementsTubesCrepine() {
        return equipementsTubesCrepine;
    }

    /**
     * Définit la valeur de la propriété equipementsTubesCrepine.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsTubesCrepine }
     *     
     */
    public void setEquipementsTubesCrepine(Patrimoine.EquipementsTubesCrepine value) {
        this.equipementsTubesCrepine = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsTurbinesHydroelectriques.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsTurbinesHydroelectriques }
     *     
     */
    public Patrimoine.EquipementsTurbinesHydroelectriques getEquipementsTurbinesHydroelectriques() {
        return equipementsTurbinesHydroelectriques;
    }

    /**
     * Définit la valeur de la propriété equipementsTurbinesHydroelectriques.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsTurbinesHydroelectriques }
     *     
     */
    public void setEquipementsTurbinesHydroelectriques(Patrimoine.EquipementsTurbinesHydroelectriques value) {
        this.equipementsTurbinesHydroelectriques = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsTuyauteries.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsTuyauteries }
     *     
     */
    public Patrimoine.EquipementsTuyauteries getEquipementsTuyauteries() {
        return equipementsTuyauteries;
    }

    /**
     * Définit la valeur de la propriété equipementsTuyauteries.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsTuyauteries }
     *     
     */
    public void setEquipementsTuyauteries(Patrimoine.EquipementsTuyauteries value) {
        this.equipementsTuyauteries = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsVannes.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsVannes }
     *     
     */
    public Patrimoine.EquipementsVannes getEquipementsVannes() {
        return equipementsVannes;
    }

    /**
     * Définit la valeur de la propriété equipementsVannes.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsVannes }
     *     
     */
    public void setEquipementsVannes(Patrimoine.EquipementsVannes value) {
        this.equipementsVannes = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsVariateursVitesse.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsVariateursVitesse }
     *     
     */
    public Patrimoine.EquipementsVariateursVitesse getEquipementsVariateursVitesse() {
        return equipementsVariateursVitesse;
    }

    /**
     * Définit la valeur de la propriété equipementsVariateursVitesse.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsVariateursVitesse }
     *     
     */
    public void setEquipementsVariateursVitesse(Patrimoine.EquipementsVariateursVitesse value) {
        this.equipementsVariateursVitesse = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsVidanges.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsVidanges }
     *     
     */
    public Patrimoine.EquipementsVidanges getEquipementsVidanges() {
        return equipementsVidanges;
    }

    /**
     * Définit la valeur de la propriété equipementsVidanges.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsVidanges }
     *     
     */
    public void setEquipementsVidanges(Patrimoine.EquipementsVidanges value) {
        this.equipementsVidanges = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsVentouses.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsVentouses }
     *     
     */
    public Patrimoine.EquipementsVentouses getEquipementsVentouses() {
        return equipementsVentouses;
    }

    /**
     * Définit la valeur de la propriété equipementsVentouses.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsVentouses }
     *     
     */
    public void setEquipementsVentouses(Patrimoine.EquipementsVentouses value) {
        this.equipementsVentouses = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsVerins.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsVerins }
     *     
     */
    public Patrimoine.EquipementsVerins getEquipementsVerins() {
        return equipementsVerins;
    }

    /**
     * Définit la valeur de la propriété equipementsVerins.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsVerins }
     *     
     */
    public void setEquipementsVerins(Patrimoine.EquipementsVerins value) {
        this.equipementsVerins = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsVisTransfert.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsVisTransfert }
     *     
     */
    public Patrimoine.EquipementsVisTransfert getEquipementsVisTransfert() {
        return equipementsVisTransfert;
    }

    /**
     * Définit la valeur de la propriété equipementsVisTransfert.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsVisTransfert }
     *     
     */
    public void setEquipementsVisTransfert(Patrimoine.EquipementsVisTransfert value) {
        this.equipementsVisTransfert = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsVoiries.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsVoiries }
     *     
     */
    public Patrimoine.EquipementsVoiries getEquipementsVoiries() {
        return equipementsVoiries;
    }

    /**
     * Définit la valeur de la propriété equipementsVoiries.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsVoiries }
     *     
     */
    public void setEquipementsVoiries(Patrimoine.EquipementsVoiries value) {
        this.equipementsVoiries = value;
    }

    /**
     * Obtient la valeur de la propriété equipementsVannesRegulation.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.EquipementsVannesRegulation }
     *     
     */
    public Patrimoine.EquipementsVannesRegulation getEquipementsVannesRegulation() {
        return equipementsVannesRegulation;
    }

    /**
     * Définit la valeur de la propriété equipementsVannesRegulation.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.EquipementsVannesRegulation }
     *     
     */
    public void setEquipementsVannesRegulation(Patrimoine.EquipementsVannesRegulation value) {
        this.equipementsVannesRegulation = value;
    }

    /**
     * Obtient la valeur de la propriété composants.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.Composants }
     *     
     */
    public Patrimoine.Composants getComposants() {
        return composants;
    }

    /**
     * Définit la valeur de la propriété composants.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.Composants }
     *     
     */
    public void setComposants(Patrimoine.Composants value) {
        this.composants = value;
    }

    /**
     * Obtient la valeur de la propriété interventions.
     * 
     * @return
     *     possible object is
     *     {@link Patrimoine.Interventions }
     *     
     */
    public Patrimoine.Interventions getInterventions() {
        return interventions;
    }

    /**
     * Définit la valeur de la propriété interventions.
     * 
     * @param value
     *     allowed object is
     *     {@link Patrimoine.Interventions }
     *     
     */
    public void setInterventions(Patrimoine.Interventions value) {
        this.interventions = value;
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="composant" type="{}typeComposant"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "composant"
    })
    public static class Composants {

        protected List<TypeComposant> composant;

        /**
         * Gets the value of the composant property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the composant property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getComposant().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeComposant }
         * 
         * 
         */
        public List<TypeComposant> getComposant() {
            if (composant == null) {
                composant = new ArrayList<TypeComposant>();
            }
            return this.composant;
        }

        public void setComposant(List<TypeComposant> composant) {
			this.composant = composant;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementAerotherme" type="{}typeEquipementAerotherme"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementAerotherme"
    })
    public static class EquipementsAerothermes {

        protected List<TypeEquipementAerotherme> equipementAerotherme;

        /**
         * Gets the value of the equipementAerotherme property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementAerotherme property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementAerotherme().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementAerotherme }
         * 
         * 
         */
        public List<TypeEquipementAerotherme> getEquipementAerotherme() {
            if (equipementAerotherme == null) {
                equipementAerotherme = new ArrayList<TypeEquipementAerotherme>();
            }
            return this.equipementAerotherme;
        }

        public void setEquipementAerotherme(List<TypeEquipementAerotherme> equipementAerotherme) {
			this.equipementAerotherme = equipementAerotherme;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementAgitateur" type="{}typeEquipementAgitateur"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementAgitateur"
    })
    public static class EquipementsAgitateurs {

        protected List<TypeEquipementAgitateur> equipementAgitateur;

        /**
         * Gets the value of the equipementAgitateur property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementAgitateur property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementAgitateur().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementAgitateur }
         * 
         * 
         */
        public List<TypeEquipementAgitateur> getEquipementAgitateur() {
            if (equipementAgitateur == null) {
                equipementAgitateur = new ArrayList<TypeEquipementAgitateur>();
            }
            return this.equipementAgitateur;
        }

        public void setEquipementAgitateur(List<TypeEquipementAgitateur> equipementAgitateur) {
			this.equipementAgitateur = equipementAgitateur;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementAntiIntrusion" type="{}typeEquipementAntiIntrusion"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementAntiIntrusion"
    })
    public static class EquipementsAntiIntrusions {

        protected List<TypeEquipementAntiIntrusion> equipementAntiIntrusion;

        /**
         * Gets the value of the equipementAntiIntrusion property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementAntiIntrusion property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementAntiIntrusion().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementAntiIntrusion }
         * 
         * 
         */
        public List<TypeEquipementAntiIntrusion> getEquipementAntiIntrusion() {
            if (equipementAntiIntrusion == null) {
                equipementAntiIntrusion = new ArrayList<TypeEquipementAntiIntrusion>();
            }
            return this.equipementAntiIntrusion;
        }

        public void setEquipementAntiIntrusion(List<TypeEquipementAntiIntrusion> equipementAntiIntrusion) {
			this.equipementAntiIntrusion = equipementAntiIntrusion;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementAppareilLevage" type="{}typeEquipementAppareilLevage"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementAppareilLevage"
    })
    public static class EquipementsAppareilsLevage {

        protected List<TypeEquipementAppareilLevage> equipementAppareilLevage;

        /**
         * Gets the value of the equipementAppareilLevage property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementAppareilLevage property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementAppareilLevage().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementAppareilLevage }
         * 
         * 
         */
        public List<TypeEquipementAppareilLevage> getEquipementAppareilLevage() {
            if (equipementAppareilLevage == null) {
                equipementAppareilLevage = new ArrayList<TypeEquipementAppareilLevage>();
            }
            return this.equipementAppareilLevage;
        }

        public void setEquipementAppareilLevage(List<TypeEquipementAppareilLevage> equipementAppareilLevage) {
			this.equipementAppareilLevage = equipementAppareilLevage;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementArmoireElectrique" type="{}typeEquipementArmoireElectrique"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementArmoireElectrique"
    })
    public static class EquipementsArmoiresElectriques {

        protected List<TypeEquipementArmoireElectrique> equipementArmoireElectrique;

        /**
         * Gets the value of the equipementArmoireElectrique property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementArmoireElectrique property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementArmoireElectrique().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementArmoireElectrique }
         * 
         * 
         */
        public List<TypeEquipementArmoireElectrique> getEquipementArmoireElectrique() {
            if (equipementArmoireElectrique == null) {
                equipementArmoireElectrique = new ArrayList<TypeEquipementArmoireElectrique>();
            }
            return this.equipementArmoireElectrique;
        }

        public void setEquipementArmoireElectrique(List<TypeEquipementArmoireElectrique> equipementArmoireElectrique) {
			this.equipementArmoireElectrique = equipementArmoireElectrique;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementAspirateurBoues" type="{}typeEquipementAspirateurBoues"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementAspirateurBoues"
    })
    public static class EquipementsAspirateursBoues {

        protected List<TypeEquipementAspirateurBoues> equipementAspirateurBoues;

        /**
         * Gets the value of the equipementAspirateurBoues property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementAspirateurBoues property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementAspirateurBoues().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementAspirateurBoues }
         * 
         * 
         */
        public List<TypeEquipementAspirateurBoues> getEquipementAspirateurBoues() {
            if (equipementAspirateurBoues == null) {
                equipementAspirateurBoues = new ArrayList<TypeEquipementAspirateurBoues>();
            }
            return this.equipementAspirateurBoues;
        }

        public void setEquipementAspirateurBoues(List<TypeEquipementAspirateurBoues> equipementAspirateurBoues) {
			this.equipementAspirateurBoues = equipementAspirateurBoues;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementAuscultationBarrage" type="{}typeEquipementAuscultationBarrage"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementAuscultationBarrage"
    })
    public static class EquipementsAuscultationsBarrages {

        protected List<TypeEquipementAuscultationBarrage> equipementAuscultationBarrage;

        /**
         * Gets the value of the equipementAuscultationBarrage property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementAuscultationBarrage property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementAuscultationBarrage().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementAuscultationBarrage }
         * 
         * 
         */
        public List<TypeEquipementAuscultationBarrage> getEquipementAuscultationBarrage() {
            if (equipementAuscultationBarrage == null) {
                equipementAuscultationBarrage = new ArrayList<TypeEquipementAuscultationBarrage>();
            }
            return this.equipementAuscultationBarrage;
        }

        public void setEquipementAuscultationBarrage(List<TypeEquipementAuscultationBarrage> equipementAuscultationBarrage) {
			this.equipementAuscultationBarrage = equipementAuscultationBarrage;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementAutomate" type="{}typeEquipementAutomate"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementAutomate"
    })
    public static class EquipementsAutomates {

        protected List<TypeEquipementAutomate> equipementAutomate;

        /**
         * Gets the value of the equipementAutomate property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementAutomate property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementAutomate().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementAutomate }
         * 
         * 
         */
        public List<TypeEquipementAutomate> getEquipementAutomate() {
            if (equipementAutomate == null) {
                equipementAutomate = new ArrayList<TypeEquipementAutomate>();
            }
            return this.equipementAutomate;
        }

        public void setEquipementAutomate(List<TypeEquipementAutomate> equipementAutomate) {
			this.equipementAutomate = equipementAutomate;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementBacRetention" type="{}typeEquipementBacRetention"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementBacRetention"
    })
    public static class EquipementsBacsRetention {

        protected List<TypeEquipementBacRetention> equipementBacRetention;

        /**
         * Gets the value of the equipementBacRetention property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementBacRetention property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementBacRetention().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementBacRetention }
         * 
         * 
         */
        public List<TypeEquipementBacRetention> getEquipementBacRetention() {
            if (equipementBacRetention == null) {
                equipementBacRetention = new ArrayList<TypeEquipementBacRetention>();
            }
            return this.equipementBacRetention;
        }

        public void setEquipementBacRetention(List<TypeEquipementBacRetention> equipementBacRetention) {
			this.equipementBacRetention = equipementBacRetention;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementBallon" type="{}typeEquipementBallon"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementBallon"
    })
    public static class EquipementsBallons {

        protected List<TypeEquipementBallon> equipementBallon;

        /**
         * Gets the value of the equipementBallon property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementBallon property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementBallon().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementBallon }
         * 
         * 
         */
        public List<TypeEquipementBallon> getEquipementBallon() {
            if (equipementBallon == null) {
                equipementBallon = new ArrayList<TypeEquipementBallon>();
            }
            return this.equipementBallon;
        }

        public void setEquipementBallon(List<TypeEquipementBallon> equipementBallon) {
			this.equipementBallon = equipementBallon;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementBenne" type="{}typeEquipementBenne"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementBenne"
    })
    public static class EquipementsBennes {

        protected List<TypeEquipementBenne> equipementBenne;

        /**
         * Gets the value of the equipementBenne property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementBenne property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementBenne().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementBenne }
         * 
         * 
         */
        public List<TypeEquipementBenne> getEquipementBenne() {
            if (equipementBenne == null) {
                equipementBenne = new ArrayList<TypeEquipementBenne>();
            }
            return this.equipementBenne;
        }

        public void setEquipementBenne(List<TypeEquipementBenne> equipementBenne) {
			this.equipementBenne = equipementBenne;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementBoiteBoue" type="{}typeEquipementBoiteBoue"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementBoiteBoue"
    })
    public static class EquipementsBoitesBoue {

        protected List<TypeEquipementBoiteBoue> equipementBoiteBoue;

        /**
         * Gets the value of the equipementBoiteBoue property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementBoiteBoue property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementBoiteBoue().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementBoiteBoue }
         * 
         * 
         */
        public List<TypeEquipementBoiteBoue> getEquipementBoiteBoue() {
            if (equipementBoiteBoue == null) {
                equipementBoiteBoue = new ArrayList<TypeEquipementBoiteBoue>();
            }
            return this.equipementBoiteBoue;
        }

        public void setEquipementBoiteBoue(List<TypeEquipementBoiteBoue> equipementBoiteBoue) {
			this.equipementBoiteBoue = equipementBoiteBoue;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementBouteilleChlore" type="{}typeEquipementBouteilleChlore"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementBouteilleChlore"
    })
    public static class EquipementsBouteillesChlore {

        protected List<TypeEquipementBouteilleChlore> equipementBouteilleChlore;

        /**
         * Gets the value of the equipementBouteilleChlore property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementBouteilleChlore property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementBouteilleChlore().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementBouteilleChlore }
         * 
         * 
         */
        public List<TypeEquipementBouteilleChlore> getEquipementBouteilleChlore() {
            if (equipementBouteilleChlore == null) {
                equipementBouteilleChlore = new ArrayList<TypeEquipementBouteilleChlore>();
            }
            return this.equipementBouteilleChlore;
        }

        public void setEquipementBouteilleChlore(List<TypeEquipementBouteilleChlore> equipementBouteilleChlore) {
			this.equipementBouteilleChlore = equipementBouteilleChlore;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementBuselure" type="{}typeEquipementBuselure"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementBuselure"
    })
    public static class EquipementsBuselures {

        protected List<TypeEquipementBuselure> equipementBuselure;

        /**
         * Gets the value of the equipementBuselure property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementBuselure property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementBuselure().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementBuselure }
         * 
         * 
         */
        public List<TypeEquipementBuselure> getEquipementBuselure() {
            if (equipementBuselure == null) {
                equipementBuselure = new ArrayList<TypeEquipementBuselure>();
            }
            return this.equipementBuselure;
        }

        public void setEquipementBuselure(List<TypeEquipementBuselure> equipementBuselure) {
			this.equipementBuselure = equipementBuselure;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementCaillebotis" type="{}typeEquipementCaillebotis"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementCaillebotis"
    })
    public static class EquipementsCaillebotis {

        protected List<TypeEquipementCaillebotis> equipementCaillebotis;

        /**
         * Gets the value of the equipementCaillebotis property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementCaillebotis property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementCaillebotis().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementCaillebotis }
         * 
         * 
         */
        public List<TypeEquipementCaillebotis> getEquipementCaillebotis() {
            if (equipementCaillebotis == null) {
                equipementCaillebotis = new ArrayList<TypeEquipementCaillebotis>();
            }
            return this.equipementCaillebotis;
        }

        public void setEquipementCaillebotis(List<TypeEquipementCaillebotis> equipementCaillebotis) {
			this.equipementCaillebotis = equipementCaillebotis;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementCanalComptage" type="{}typeEquipementCanalComptage"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementCanalComptage"
    })
    public static class EquipementsCanauxComptage {

        protected List<TypeEquipementCanalComptage> equipementCanalComptage;

        /**
         * Gets the value of the equipementCanalComptage property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementCanalComptage property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementCanalComptage().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementCanalComptage }
         * 
         * 
         */
        public List<TypeEquipementCanalComptage> getEquipementCanalComptage() {
            if (equipementCanalComptage == null) {
                equipementCanalComptage = new ArrayList<TypeEquipementCanalComptage>();
            }
            return this.equipementCanalComptage;
        }

        public void setEquipementCanalComptage(List<TypeEquipementCanalComptage> equipementCanalComptage) {
			this.equipementCanalComptage = equipementCanalComptage;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementCapteur" type="{}typeEquipementCapteur"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementCapteur"
    })
    public static class EquipementsCapteurs {

        protected List<TypeEquipementCapteur> equipementCapteur;

        /**
         * Gets the value of the equipementCapteur property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementCapteur property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementCapteur().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementCapteur }
         * 
         * 
         */
        public List<TypeEquipementCapteur> getEquipementCapteur() {
            if (equipementCapteur == null) {
                equipementCapteur = new ArrayList<TypeEquipementCapteur>();
            }
            return this.equipementCapteur;
        }

        public void setEquipementCapteur(List<TypeEquipementCapteur> equipementCapteur) {
			this.equipementCapteur = equipementCapteur;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementCellulePosteHT" type="{}typeEquipementCellulePosteHT"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementCellulePosteHT"
    })
    public static class EquipementsCellulesPosteHT {

        protected List<TypeEquipementCellulePosteHT> equipementCellulePosteHT;

        /**
         * Gets the value of the equipementCellulePosteHT property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementCellulePosteHT property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementCellulePosteHT().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementCellulePosteHT }
         * 
         * 
         */
        public List<TypeEquipementCellulePosteHT> getEquipementCellulePosteHT() {
            if (equipementCellulePosteHT == null) {
                equipementCellulePosteHT = new ArrayList<TypeEquipementCellulePosteHT>();
            }
            return this.equipementCellulePosteHT;
        }

        public void setEquipementCellulePosteHT(List<TypeEquipementCellulePosteHT> equipementCellulePosteHT) {
			this.equipementCellulePosteHT = equipementCellulePosteHT;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementCentraleHydraulique" type="{}typeEquipementCentraleHydraulique"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementCentraleHydraulique"
    })
    public static class EquipementsCentralesHydrauliques {

        protected List<TypeEquipementCentraleHydraulique> equipementCentraleHydraulique;

        /**
         * Gets the value of the equipementCentraleHydraulique property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementCentraleHydraulique property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementCentraleHydraulique().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementCentraleHydraulique }
         * 
         * 
         */
        public List<TypeEquipementCentraleHydraulique> getEquipementCentraleHydraulique() {
            if (equipementCentraleHydraulique == null) {
                equipementCentraleHydraulique = new ArrayList<TypeEquipementCentraleHydraulique>();
            }
            return this.equipementCentraleHydraulique;
        }

        public void setEquipementCentraleHydraulique(List<TypeEquipementCentraleHydraulique> equipementCentraleHydraulique) {
			this.equipementCentraleHydraulique = equipementCentraleHydraulique;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementCentrifugeuse" type="{}typeEquipementCentrifugeuse"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementCentrifugeuse"
    })
    public static class EquipementsCentrifugeuses {

        protected List<TypeEquipementCentrifugeuse> equipementCentrifugeuse;

        /**
         * Gets the value of the equipementCentrifugeuse property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementCentrifugeuse property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementCentrifugeuse().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementCentrifugeuse }
         * 
         * 
         */
        public List<TypeEquipementCentrifugeuse> getEquipementCentrifugeuse() {
            if (equipementCentrifugeuse == null) {
                equipementCentrifugeuse = new ArrayList<TypeEquipementCentrifugeuse>();
            }
            return this.equipementCentrifugeuse;
        }

        public void setEquipementCentrifugeuse(List<TypeEquipementCentrifugeuse> equipementCentrifugeuse) {
			this.equipementCentrifugeuse = equipementCentrifugeuse;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementChariotElevateur" type="{}typeEquipementChariotElevateur"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementChariotElevateur"
    })
    public static class EquipementsChariotsElevateurs {

        protected List<TypeEquipementChariotElevateur> equipementChariotElevateur;

        /**
         * Gets the value of the equipementChariotElevateur property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementChariotElevateur property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementChariotElevateur().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementChariotElevateur }
         * 
         * 
         */
        public List<TypeEquipementChariotElevateur> getEquipementChariotElevateur() {
            if (equipementChariotElevateur == null) {
                equipementChariotElevateur = new ArrayList<TypeEquipementChariotElevateur>();
            }
            return this.equipementChariotElevateur;
        }

        public void setEquipementChariotElevateur(List<TypeEquipementChariotElevateur> equipementChariotElevateur) {
			this.equipementChariotElevateur = equipementChariotElevateur;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementChlorometre" type="{}typeEquipementChlorometre"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementChlorometre"
    })
    public static class EquipementsChlorometres {

        protected List<TypeEquipementChlorometre> equipementChlorometre;

        /**
         * Gets the value of the equipementChlorometre property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementChlorometre property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementChlorometre().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementChlorometre }
         * 
         * 
         */
        public List<TypeEquipementChlorometre> getEquipementChlorometre() {
            if (equipementChlorometre == null) {
                equipementChlorometre = new ArrayList<TypeEquipementChlorometre>();
            }
            return this.equipementChlorometre;
        }

        public void setEquipementChlorometre(List<TypeEquipementChlorometre> equipementChlorometre) {
			this.equipementChlorometre = equipementChlorometre;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementClapet" type="{}typeEquipementClapet"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementClapet"
    })
    public static class EquipementsClapets {

        protected List<TypeEquipementClapet> equipementClapet;

        /**
         * Gets the value of the equipementClapet property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementClapet property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementClapet().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementClapet }
         * 
         * 
         */
        public List<TypeEquipementClapet> getEquipementClapet() {
            if (equipementClapet == null) {
                equipementClapet = new ArrayList<TypeEquipementClapet>();
            }
            return this.equipementClapet;
        }

        public void setEquipementClapet(List<TypeEquipementClapet> equipementClapet) {
			this.equipementClapet = equipementClapet;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementCloture" type="{}typeEquipementCloture"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementCloture"
    })
    public static class EquipementsClotures {

        protected List<TypeEquipementCloture> equipementCloture;

        /**
         * Gets the value of the equipementCloture property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementCloture property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementCloture().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementCloture }
         * 
         * 
         */
        public List<TypeEquipementCloture> getEquipementCloture() {
            if (equipementCloture == null) {
                equipementCloture = new ArrayList<TypeEquipementCloture>();
            }
            return this.equipementCloture;
        }

        public void setEquipementCloture(List<TypeEquipementCloture> equipementCloture) {
			this.equipementCloture = equipementCloture;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementCollecteur" type="{}typeEquipementCollecteur"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementCollecteur"
    })
    public static class EquipementsCollecteurs {

        protected List<TypeEquipementCollecteur> equipementCollecteur;

        /**
         * Gets the value of the equipementCollecteur property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementCollecteur property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementCollecteur().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementCollecteur }
         * 
         * 
         */
        public List<TypeEquipementCollecteur> getEquipementCollecteur() {
            if (equipementCollecteur == null) {
                equipementCollecteur = new ArrayList<TypeEquipementCollecteur>();
            }
            return this.equipementCollecteur;
        }

        public void setEquipementCollecteur(List<TypeEquipementCollecteur> equipementCollecteur) {
			this.equipementCollecteur = equipementCollecteur;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementColonneForage" type="{}typeEquipementColonneForage"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementColonneForage"
    })
    public static class EquipementsColonnesForage {

        protected List<TypeEquipementColonneForage> equipementColonneForage;

        /**
         * Gets the value of the equipementColonneForage property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementColonneForage property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementColonneForage().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementColonneForage }
         * 
         * 
         */
        public List<TypeEquipementColonneForage> getEquipementColonneForage() {
            if (equipementColonneForage == null) {
                equipementColonneForage = new ArrayList<TypeEquipementColonneForage>();
            }
            return this.equipementColonneForage;
        }

        public void setEquipementColonneForage(List<TypeEquipementColonneForage> equipementColonneForage) {
			this.equipementColonneForage = equipementColonneForage;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementColonneMontante" type="{}typeEquipementColonneMontante"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementColonneMontante"
    })
    public static class EquipementsColonnesMontantes {

        protected List<TypeEquipementColonneMontante> equipementColonneMontante;

        /**
         * Gets the value of the equipementColonneMontante property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementColonneMontante property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementColonneMontante().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementColonneMontante }
         * 
         * 
         */
        public List<TypeEquipementColonneMontante> getEquipementColonneMontante() {
            if (equipementColonneMontante == null) {
                equipementColonneMontante = new ArrayList<TypeEquipementColonneMontante>();
            }
            return this.equipementColonneMontante;
        }

        public void setEquipementColonneMontante(List<TypeEquipementColonneMontante> equipementColonneMontante) {
			this.equipementColonneMontante = equipementColonneMontante;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementComptage" type="{}typeEquipementComptage"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementComptage"
    })
    public static class EquipementsComptages {

        protected List<TypeEquipementComptage> equipementComptage;

        /**
         * Gets the value of the equipementComptage property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementComptage property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementComptage().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementComptage }
         * 
         * 
         */
        public List<TypeEquipementComptage> getEquipementComptage() {
            if (equipementComptage == null) {
                equipementComptage = new ArrayList<TypeEquipementComptage>();
            }
            return this.equipementComptage;
        }

        public void setEquipementComptage(List<TypeEquipementComptage> equipementComptage) {
			this.equipementComptage = equipementComptage;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementCompteurElectrique" type="{}typeEquipementCompteurElectrique"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementCompteurElectrique"
    })
    public static class EquipementsCompteursElectriques {

        protected List<TypeEquipementCompteurElectrique> equipementCompteurElectrique;

        /**
         * Gets the value of the equipementCompteurElectrique property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementCompteurElectrique property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementCompteurElectrique().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementCompteurElectrique }
         * 
         * 
         */
        public List<TypeEquipementCompteurElectrique> getEquipementCompteurElectrique() {
            if (equipementCompteurElectrique == null) {
                equipementCompteurElectrique = new ArrayList<TypeEquipementCompteurElectrique>();
            }
            return this.equipementCompteurElectrique;
        }

        public void setEquipementCompteurElectrique(List<TypeEquipementCompteurElectrique> equipementCompteurElectrique) {
			this.equipementCompteurElectrique = equipementCompteurElectrique;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementCondensateur" type="{}typeEquipementCondensateur"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementCondensateur"
    })
    public static class EquipementsCondensateurs {

        protected List<TypeEquipementCondensateur> equipementCondensateur;

        /**
         * Gets the value of the equipementCondensateur property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementCondensateur property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementCondensateur().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementCondensateur }
         * 
         * 
         */
        public List<TypeEquipementCondensateur> getEquipementCondensateur() {
            if (equipementCondensateur == null) {
                equipementCondensateur = new ArrayList<TypeEquipementCondensateur>();
            }
            return this.equipementCondensateur;
        }

        public void setEquipementCondensateur(List<TypeEquipementCondensateur> equipementCondensateur) {
			this.equipementCondensateur = equipementCondensateur;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementConvoyeurTransporteur" type="{}typeEquipementConvoyeurTransporteur"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementConvoyeurTransporteur"
    })
    public static class EquipementsConvoyeursTransporteurs {

        protected List<TypeEquipementConvoyeurTransporteur> equipementConvoyeurTransporteur;

        /**
         * Gets the value of the equipementConvoyeurTransporteur property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementConvoyeurTransporteur property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementConvoyeurTransporteur().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementConvoyeurTransporteur }
         * 
         * 
         */
        public List<TypeEquipementConvoyeurTransporteur> getEquipementConvoyeurTransporteur() {
            if (equipementConvoyeurTransporteur == null) {
                equipementConvoyeurTransporteur = new ArrayList<TypeEquipementConvoyeurTransporteur>();
            }
            return this.equipementConvoyeurTransporteur;
        }

        public void setEquipementConvoyeurTransporteur(List<TypeEquipementConvoyeurTransporteur> equipementConvoyeurTransporteur) {
			this.equipementConvoyeurTransporteur = equipementConvoyeurTransporteur;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementCrepine" type="{}typeEquipementCrepine"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementCrepine"
    })
    public static class EquipementsCrepines {

        protected List<TypeEquipementCrepine> equipementCrepine;

        /**
         * Gets the value of the equipementCrepine property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementCrepine property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementCrepine().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementCrepine }
         * 
         * 
         */
        public List<TypeEquipementCrepine> getEquipementCrepine() {
            if (equipementCrepine == null) {
                equipementCrepine = new ArrayList<TypeEquipementCrepine>();
            }
            return this.equipementCrepine;
        }

        public void setEquipementCrepine(List<TypeEquipementCrepine> equipementCrepine) {
			this.equipementCrepine = equipementCrepine;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementDemarreurElectrique" type="{}typeEquipementDemarreurElectrique"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementDemarreurElectrique"
    })
    public static class EquipementsDemarreursElectriques {

        protected List<TypeEquipementDemarreurElectrique> equipementDemarreurElectrique;

        /**
         * Gets the value of the equipementDemarreurElectrique property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementDemarreurElectrique property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementDemarreurElectrique().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementDemarreurElectrique }
         * 
         * 
         */
        public List<TypeEquipementDemarreurElectrique> getEquipementDemarreurElectrique() {
            if (equipementDemarreurElectrique == null) {
                equipementDemarreurElectrique = new ArrayList<TypeEquipementDemarreurElectrique>();
            }
            return this.equipementDemarreurElectrique;
        }

        public void setEquipementDemarreurElectrique(List<TypeEquipementDemarreurElectrique> equipementDemarreurElectrique) {
			this.equipementDemarreurElectrique = equipementDemarreurElectrique;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementDeshumidificateur" type="{}typeEquipementDeshumidificateur"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementDeshumidificateur"
    })
    public static class EquipementsDeshumidificateurs {

        protected List<TypeEquipementDeshumidificateur> equipementDeshumidificateur;

        /**
         * Gets the value of the equipementDeshumidificateur property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementDeshumidificateur property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementDeshumidificateur().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementDeshumidificateur }
         * 
         * 
         */
        public List<TypeEquipementDeshumidificateur> getEquipementDeshumidificateur() {
            if (equipementDeshumidificateur == null) {
                equipementDeshumidificateur = new ArrayList<TypeEquipementDeshumidificateur>();
            }
            return this.equipementDeshumidificateur;
        }

        public void setEquipementDeshumidificateur(List<TypeEquipementDeshumidificateur> equipementDeshumidificateur) {
			this.equipementDeshumidificateur = equipementDeshumidificateur;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementDestructeurOzone" type="{}typeEquipementDestructeurOzone"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementDestructeurOzone"
    })
    public static class EquipementsDestructeurOzone {

        protected List<TypeEquipementDestructeurOzone> equipementDestructeurOzone;

        /**
         * Gets the value of the equipementDestructeurOzone property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementDestructeurOzone property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementDestructeurOzone().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementDestructeurOzone }
         * 
         * 
         */
        public List<TypeEquipementDestructeurOzone> getEquipementDestructeurOzone() {
            if (equipementDestructeurOzone == null) {
                equipementDestructeurOzone = new ArrayList<TypeEquipementDestructeurOzone>();
            }
            return this.equipementDestructeurOzone;
        }

        public void setEquipementDestructeurOzone(List<TypeEquipementDestructeurOzone> equipementDestructeurOzone) {
			this.equipementDestructeurOzone = equipementDestructeurOzone;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementDisconnecteur" type="{}typeEquipementDisconnecteur"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementDisconnecteur"
    })
    public static class EquipementsDisconnecteurs {

        protected List<TypeEquipementDisconnecteur> equipementDisconnecteur;

        /**
         * Gets the value of the equipementDisconnecteur property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementDisconnecteur property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementDisconnecteur().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementDisconnecteur }
         * 
         * 
         */
        public List<TypeEquipementDisconnecteur> getEquipementDisconnecteur() {
            if (equipementDisconnecteur == null) {
                equipementDisconnecteur = new ArrayList<TypeEquipementDisconnecteur>();
            }
            return this.equipementDisconnecteur;
        }

        public void setEquipementDisconnecteur(List<TypeEquipementDisconnecteur> equipementDisconnecteur) {
			this.equipementDisconnecteur = equipementDisconnecteur;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementEchangeurChaleur" type="{}typeEquipementEchangeurChaleur"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementEchangeurChaleur"
    })
    public static class EquipementsEchangeursChaleur {

        protected List<TypeEquipementEchangeurChaleur> equipementEchangeurChaleur;

        /**
         * Gets the value of the equipementEchangeurChaleur property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementEchangeurChaleur property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementEchangeurChaleur().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementEchangeurChaleur }
         * 
         * 
         */
        public List<TypeEquipementEchangeurChaleur> getEquipementEchangeurChaleur() {
            if (equipementEchangeurChaleur == null) {
                equipementEchangeurChaleur = new ArrayList<TypeEquipementEchangeurChaleur>();
            }
            return this.equipementEchangeurChaleur;
        }

        public void setEquipementEchangeurChaleur(List<TypeEquipementEchangeurChaleur> equipementEchangeurChaleur) {
			this.equipementEchangeurChaleur = equipementEchangeurChaleur;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementEchelle" type="{}typeEquipementEchelle"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementEchelle"
    })
    public static class EquipementsEchelles {

        protected List<TypeEquipementEchelle> equipementEchelle;

        /**
         * Gets the value of the equipementEchelle property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementEchelle property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementEchelle().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementEchelle }
         * 
         * 
         */
        public List<TypeEquipementEchelle> getEquipementEchelle() {
            if (equipementEchelle == null) {
                equipementEchelle = new ArrayList<TypeEquipementEchelle>();
            }
            return this.equipementEchelle;
        }

        public void setEquipementEchelle(List<TypeEquipementEchelle> equipementEchelle) {
			this.equipementEchelle = equipementEchelle;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementElectrodeReference" type="{}typeEquipementElectrodeReference"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementElectrodeReference"
    })
    public static class EquipementsElectrodesReference {

        protected List<TypeEquipementElectrodeReference> equipementElectrodeReference;

        /**
         * Gets the value of the equipementElectrodeReference property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementElectrodeReference property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementElectrodeReference().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementElectrodeReference }
         * 
         * 
         */
        public List<TypeEquipementElectrodeReference> getEquipementElectrodeReference() {
            if (equipementElectrodeReference == null) {
                equipementElectrodeReference = new ArrayList<TypeEquipementElectrodeReference>();
            }
            return this.equipementElectrodeReference;
        }

        public void setEquipementElectrodeReference(List<TypeEquipementElectrodeReference> equipementElectrodeReference) {
			this.equipementElectrodeReference = equipementElectrodeReference;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementEnregistreur" type="{}typeEquipementEnregistreur"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementEnregistreur"
    })
    public static class EquipementsEnregistreurs {

        protected List<TypeEquipementEnregistreur> equipementEnregistreur;

        /**
         * Gets the value of the equipementEnregistreur property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementEnregistreur property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementEnregistreur().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementEnregistreur }
         * 
         * 
         */
        public List<TypeEquipementEnregistreur> getEquipementEnregistreur() {
            if (equipementEnregistreur == null) {
                equipementEnregistreur = new ArrayList<TypeEquipementEnregistreur>();
            }
            return this.equipementEnregistreur;
        }

        public void setEquipementEnregistreur(List<TypeEquipementEnregistreur> equipementEnregistreur) {
			this.equipementEnregistreur = equipementEnregistreur;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementEspaceVert" type="{}typeEquipementEspaceVert"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementEspaceVert"
    })
    public static class EquipementsEspacesVert {

        protected List<TypeEquipementEspaceVert> equipementEspaceVert;

        /**
         * Gets the value of the equipementEspaceVert property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementEspaceVert property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementEspaceVert().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementEspaceVert }
         * 
         * 
         */
        public List<TypeEquipementEspaceVert> getEquipementEspaceVert() {
            if (equipementEspaceVert == null) {
                equipementEspaceVert = new ArrayList<TypeEquipementEspaceVert>();
            }
            return this.equipementEspaceVert;
        }

        public void setEquipementEspaceVert(List<TypeEquipementEspaceVert> equipementEspaceVert) {
			this.equipementEspaceVert = equipementEspaceVert;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementExtracteurAir" type="{}typeEquipementExtracteurAir"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementExtracteurAir"
    })
    public static class EquipementsExtracteursAir {

        protected List<TypeEquipementExtracteurAir> equipementExtracteurAir;

        /**
         * Gets the value of the equipementExtracteurAir property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementExtracteurAir property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementExtracteurAir().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementExtracteurAir }
         * 
         * 
         */
        public List<TypeEquipementExtracteurAir> getEquipementExtracteurAir() {
            if (equipementExtracteurAir == null) {
                equipementExtracteurAir = new ArrayList<TypeEquipementExtracteurAir>();
            }
            return this.equipementExtracteurAir;
        }

        public void setEquipementExtracteurAir(List<TypeEquipementExtracteurAir> equipementExtracteurAir) {
			this.equipementExtracteurAir = equipementExtracteurAir;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementFenetre" type="{}typeEquipementFenetre"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementFenetre"
    })
    public static class EquipementsFenetres {

        protected List<TypeEquipementFenetre> equipementFenetre;

        /**
         * Gets the value of the equipementFenetre property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementFenetre property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementFenetre().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementFenetre }
         * 
         * 
         */
        public List<TypeEquipementFenetre> getEquipementFenetre() {
            if (equipementFenetre == null) {
                equipementFenetre = new ArrayList<TypeEquipementFenetre>();
            }
            return this.equipementFenetre;
        }

        public void setEquipementFenetre(List<TypeEquipementFenetre> equipementFenetre) {
			this.equipementFenetre = equipementFenetre;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementFiltreFerme" type="{}typeEquipementFiltreFerme"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementFiltreFerme"
    })
    public static class EquipementsFiltresFermes {

        protected List<TypeEquipementFiltreFerme> equipementFiltreFerme;

        /**
         * Gets the value of the equipementFiltreFerme property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementFiltreFerme property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementFiltreFerme().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementFiltreFerme }
         * 
         * 
         */
        public List<TypeEquipementFiltreFerme> getEquipementFiltreFerme() {
            if (equipementFiltreFerme == null) {
                equipementFiltreFerme = new ArrayList<TypeEquipementFiltreFerme>();
            }
            return this.equipementFiltreFerme;
        }

        public void setEquipementFiltreFerme(List<TypeEquipementFiltreFerme> equipementFiltreFerme) {
			this.equipementFiltreFerme = equipementFiltreFerme;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementFiltreY" type="{}typeEquipementFiltreY"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementFiltreY"
    })
    public static class EquipementsFiltresY {

        protected List<TypeEquipementFiltreY> equipementFiltreY;

        /**
         * Gets the value of the equipementFiltreY property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementFiltreY property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementFiltreY().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementFiltreY }
         * 
         * 
         */
        public List<TypeEquipementFiltreY> getEquipementFiltreY() {
            if (equipementFiltreY == null) {
                equipementFiltreY = new ArrayList<TypeEquipementFiltreY>();
            }
            return this.equipementFiltreY;
        }

        public void setEquipementFiltreY(List<TypeEquipementFiltreY> equipementFiltreY) {
			this.equipementFiltreY = equipementFiltreY;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementGardeCorps" type="{}typeEquipementGardeCorps"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementGardeCorps"
    })
    public static class EquipementsGardesCorps {

        protected List<TypeEquipementGardeCorps> equipementGardeCorps;

        /**
         * Gets the value of the equipementGardeCorps property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementGardeCorps property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementGardeCorps().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementGardeCorps }
         * 
         * 
         */
        public List<TypeEquipementGardeCorps> getEquipementGardeCorps() {
            if (equipementGardeCorps == null) {
                equipementGardeCorps = new ArrayList<TypeEquipementGardeCorps>();
            }
            return this.equipementGardeCorps;
        }

        public void setEquipementGardeCorps(List<TypeEquipementGardeCorps> equipementGardeCorps) {
			this.equipementGardeCorps = equipementGardeCorps;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementGenerateurUV" type="{}typeEquipementGenerateurUV"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementGenerateurUV"
    })
    public static class EquipementsGenerateursUV {

        protected List<TypeEquipementGenerateurUV> equipementGenerateurUV;

        /**
         * Gets the value of the equipementGenerateurUV property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementGenerateurUV property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementGenerateurUV().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementGenerateurUV }
         * 
         * 
         */
        public List<TypeEquipementGenerateurUV> getEquipementGenerateurUV() {
            if (equipementGenerateurUV == null) {
                equipementGenerateurUV = new ArrayList<TypeEquipementGenerateurUV>();
            }
            return this.equipementGenerateurUV;
        }

        public void setEquipementGenerateurUV(List<TypeEquipementGenerateurUV> equipementGenerateurUV) {
			this.equipementGenerateurUV = equipementGenerateurUV;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementGrappin" type="{}typeEquipementGrappin"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementGrappin"
    })
    public static class EquipementsGrappins {

        protected List<TypeEquipementGrappin> equipementGrappin;

        /**
         * Gets the value of the equipementGrappin property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementGrappin property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementGrappin().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementGrappin }
         * 
         * 
         */
        public List<TypeEquipementGrappin> getEquipementGrappin() {
            if (equipementGrappin == null) {
                equipementGrappin = new ArrayList<TypeEquipementGrappin>();
            }
            return this.equipementGrappin;
        }

        public void setEquipementGrappin(List<TypeEquipementGrappin> equipementGrappin) {
			this.equipementGrappin = equipementGrappin;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementGroupeAir" type="{}typeEquipementGroupeAir"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementGroupeAir"
    })
    public static class EquipementsGroupesAir {

        protected List<TypeEquipementGroupeAir> equipementGroupeAir;

        /**
         * Gets the value of the equipementGroupeAir property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementGroupeAir property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementGroupeAir().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementGroupeAir }
         * 
         * 
         */
        public List<TypeEquipementGroupeAir> getEquipementGroupeAir() {
            if (equipementGroupeAir == null) {
                equipementGroupeAir = new ArrayList<TypeEquipementGroupeAir>();
            }
            return this.equipementGroupeAir;
        }

        public void setEquipementGroupeAir(List<TypeEquipementGroupeAir> equipementGroupeAir) {
			this.equipementGroupeAir = equipementGroupeAir;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementGroupeDosage" type="{}typeEquipementGroupeDosage"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementGroupeDosage"
    })
    public static class EquipementsGroupesDosage {

        protected List<TypeEquipementGroupeDosage> equipementGroupeDosage;

        /**
         * Gets the value of the equipementGroupeDosage property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementGroupeDosage property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementGroupeDosage().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementGroupeDosage }
         * 
         * 
         */
        public List<TypeEquipementGroupeDosage> getEquipementGroupeDosage() {
            if (equipementGroupeDosage == null) {
                equipementGroupeDosage = new ArrayList<TypeEquipementGroupeDosage>();
            }
            return this.equipementGroupeDosage;
        }

        public void setEquipementGroupeDosage(List<TypeEquipementGroupeDosage> equipementGroupeDosage) {
			this.equipementGroupeDosage = equipementGroupeDosage;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementGroupeElectrogene" type="{}typeEquipementGroupeElectrogene"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementGroupeElectrogene"
    })
    public static class EquipementsGroupesElectrogenes {

        protected List<TypeEquipementGroupeElectrogene> equipementGroupeElectrogene;

        /**
         * Gets the value of the equipementGroupeElectrogene property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementGroupeElectrogene property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementGroupeElectrogene().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementGroupeElectrogene }
         * 
         * 
         */
        public List<TypeEquipementGroupeElectrogene> getEquipementGroupeElectrogene() {
            if (equipementGroupeElectrogene == null) {
                equipementGroupeElectrogene = new ArrayList<TypeEquipementGroupeElectrogene>();
            }
            return this.equipementGroupeElectrogene;
        }

        public void setEquipementGroupeElectrogene(List<TypeEquipementGroupeElectrogene> equipementGroupeElectrogene) {
			this.equipementGroupeElectrogene = equipementGroupeElectrogene;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementGroupeMalaxeur" type="{}typeEquipementGroupeMalaxeur"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementGroupeMalaxeur"
    })
    public static class EquipementsGroupesMalaxeurs {

        protected List<TypeEquipementGroupeMalaxeur> equipementGroupeMalaxeur;

        /**
         * Gets the value of the equipementGroupeMalaxeur property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementGroupeMalaxeur property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementGroupeMalaxeur().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementGroupeMalaxeur }
         * 
         * 
         */
        public List<TypeEquipementGroupeMalaxeur> getEquipementGroupeMalaxeur() {
            if (equipementGroupeMalaxeur == null) {
                equipementGroupeMalaxeur = new ArrayList<TypeEquipementGroupeMalaxeur>();
            }
            return this.equipementGroupeMalaxeur;
        }

        public void setEquipementGroupeMalaxeur(List<TypeEquipementGroupeMalaxeur> equipementGroupeMalaxeur) {
			this.equipementGroupeMalaxeur = equipementGroupeMalaxeur;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementGroupeRefroidissement" type="{}typeEquipementGroupeRefroidissement"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementGroupeRefroidissement"
    })
    public static class EquipementsGroupesRefroidissement {

        protected List<TypeEquipementGroupeRefroidissement> equipementGroupeRefroidissement;

        /**
         * Gets the value of the equipementGroupeRefroidissement property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementGroupeRefroidissement property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementGroupeRefroidissement().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementGroupeRefroidissement }
         * 
         * 
         */
        public List<TypeEquipementGroupeRefroidissement> getEquipementGroupeRefroidissement() {
            if (equipementGroupeRefroidissement == null) {
                equipementGroupeRefroidissement = new ArrayList<TypeEquipementGroupeRefroidissement>();
            }
            return this.equipementGroupeRefroidissement;
        }

        public void setEquipementGroupeRefroidissement(List<TypeEquipementGroupeRefroidissement> equipementGroupeRefroidissement) {
			this.equipementGroupeRefroidissement = equipementGroupeRefroidissement;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementHerse" type="{}typeEquipementHerse"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementHerse"
    })
    public static class EquipementsHerses {

        protected List<TypeEquipementHerse> equipementHerse;

        /**
         * Gets the value of the equipementHerse property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementHerse property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementHerse().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementHerse }
         * 
         * 
         */
        public List<TypeEquipementHerse> getEquipementHerse() {
            if (equipementHerse == null) {
                equipementHerse = new ArrayList<TypeEquipementHerse>();
            }
            return this.equipementHerse;
        }

        public void setEquipementHerse(List<TypeEquipementHerse> equipementHerse) {
			this.equipementHerse = equipementHerse;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementHydrocyclone" type="{}typeEquipementHydrocyclone"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementHydrocyclone"
    })
    public static class EquipementsHydrocyclones {

        protected List<TypeEquipementHydrocyclone> equipementHydrocyclone;

        /**
         * Gets the value of the equipementHydrocyclone property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementHydrocyclone property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementHydrocyclone().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementHydrocyclone }
         * 
         * 
         */
        public List<TypeEquipementHydrocyclone> getEquipementHydrocyclone() {
            if (equipementHydrocyclone == null) {
                equipementHydrocyclone = new ArrayList<TypeEquipementHydrocyclone>();
            }
            return this.equipementHydrocyclone;
        }

        public void setEquipementHydrocyclone(List<TypeEquipementHydrocyclone> equipementHydrocyclone) {
			this.equipementHydrocyclone = equipementHydrocyclone;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementHydroejecteur" type="{}typeEquipementHydroejecteur"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementHydroejecteur"
    })
    public static class EquipementsHydroejecteurs {

        protected List<TypeEquipementHydroejecteur> equipementHydroejecteur;

        /**
         * Gets the value of the equipementHydroejecteur property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementHydroejecteur property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementHydroejecteur().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementHydroejecteur }
         * 
         * 
         */
        public List<TypeEquipementHydroejecteur> getEquipementHydroejecteur() {
            if (equipementHydroejecteur == null) {
                equipementHydroejecteur = new ArrayList<TypeEquipementHydroejecteur>();
            }
            return this.equipementHydroejecteur;
        }

        public void setEquipementHydroejecteur(List<TypeEquipementHydroejecteur> equipementHydroejecteur) {
			this.equipementHydroejecteur = equipementHydroejecteur;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementLiaisonElectrique" type="{}typeEquipementLiaisonElectrique"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementLiaisonElectrique"
    })
    public static class EquipementsLiaisonsElectriques {

        protected List<TypeEquipementLiaisonElectrique> equipementLiaisonElectrique;

        /**
         * Gets the value of the equipementLiaisonElectrique property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementLiaisonElectrique property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementLiaisonElectrique().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementLiaisonElectrique }
         * 
         * 
         */
        public List<TypeEquipementLiaisonElectrique> getEquipementLiaisonElectrique() {
            if (equipementLiaisonElectrique == null) {
                equipementLiaisonElectrique = new ArrayList<TypeEquipementLiaisonElectrique>();
            }
            return this.equipementLiaisonElectrique;
        }

        public void setEquipementLiaisonElectrique(List<TypeEquipementLiaisonElectrique> equipementLiaisonElectrique) {
			this.equipementLiaisonElectrique = equipementLiaisonElectrique;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementMasseAnodique" type="{}typeEquipementMasseAnodique"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementMasseAnodique"
    })
    public static class EquipementsMassesAnodiques {

        protected List<TypeEquipementMasseAnodique> equipementMasseAnodique;

        /**
         * Gets the value of the equipementMasseAnodique property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementMasseAnodique property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementMasseAnodique().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementMasseAnodique }
         * 
         * 
         */
        public List<TypeEquipementMasseAnodique> getEquipementMasseAnodique() {
            if (equipementMasseAnodique == null) {
                equipementMasseAnodique = new ArrayList<TypeEquipementMasseAnodique>();
            }
            return this.equipementMasseAnodique;
        }

        public void setEquipementMasseAnodique(List<TypeEquipementMasseAnodique> equipementMasseAnodique) {
			this.equipementMasseAnodique = equipementMasseAnodique;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementMaterielLaboratoire" type="{}typeEquipementMaterielLaboratoire"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementMaterielLaboratoire"
    })
    public static class EquipementsMaterielsLaboratoire {

        protected List<TypeEquipementMaterielLaboratoire> equipementMaterielLaboratoire;

        /**
         * Gets the value of the equipementMaterielLaboratoire property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementMaterielLaboratoire property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementMaterielLaboratoire().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementMaterielLaboratoire }
         * 
         * 
         */
        public List<TypeEquipementMaterielLaboratoire> getEquipementMaterielLaboratoire() {
            if (equipementMaterielLaboratoire == null) {
                equipementMaterielLaboratoire = new ArrayList<TypeEquipementMaterielLaboratoire>();
            }
            return this.equipementMaterielLaboratoire;
        }

        public void setEquipementMaterielLaboratoire(List<TypeEquipementMaterielLaboratoire> equipementMaterielLaboratoire) {
			this.equipementMaterielLaboratoire = equipementMaterielLaboratoire;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementMelangeurStatique" type="{}typeEquipementMelangeurStatique"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementMelangeurStatique"
    })
    public static class EquipementsMelangeursStatiques {

        protected List<TypeEquipementMelangeurStatique> equipementMelangeurStatique;

        /**
         * Gets the value of the equipementMelangeurStatique property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementMelangeurStatique property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementMelangeurStatique().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementMelangeurStatique }
         * 
         * 
         */
        public List<TypeEquipementMelangeurStatique> getEquipementMelangeurStatique() {
            if (equipementMelangeurStatique == null) {
                equipementMelangeurStatique = new ArrayList<TypeEquipementMelangeurStatique>();
            }
            return this.equipementMelangeurStatique;
        }

        public void setEquipementMelangeurStatique(List<TypeEquipementMelangeurStatique> equipementMelangeurStatique) {
			this.equipementMelangeurStatique = equipementMelangeurStatique;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementMembraneUF" type="{}typeEquipementMembraneUF"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementMembraneUF"
    })
    public static class EquipementsMembranesUF {

        protected List<TypeEquipementMembraneUF> equipementMembraneUF;

        /**
         * Gets the value of the equipementMembraneUF property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementMembraneUF property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementMembraneUF().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementMembraneUF }
         * 
         * 
         */
        public List<TypeEquipementMembraneUF> getEquipementMembraneUF() {
            if (equipementMembraneUF == null) {
                equipementMembraneUF = new ArrayList<TypeEquipementMembraneUF>();
            }
            return this.equipementMembraneUF;
        }

        public void setEquipementMembraneUF(List<TypeEquipementMembraneUF> equipementMembraneUF) {
			this.equipementMembraneUF = equipementMembraneUF;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementModuleLamellaire" type="{}typeEquipementModuleLamellaire"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementModuleLamellaire"
    })
    public static class EquipementsModulesLamellaires {

        protected List<TypeEquipementModuleLamellaire> equipementModuleLamellaire;

        /**
         * Gets the value of the equipementModuleLamellaire property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementModuleLamellaire property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementModuleLamellaire().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementModuleLamellaire }
         * 
         * 
         */
        public List<TypeEquipementModuleLamellaire> getEquipementModuleLamellaire() {
            if (equipementModuleLamellaire == null) {
                equipementModuleLamellaire = new ArrayList<TypeEquipementModuleLamellaire>();
            }
            return this.equipementModuleLamellaire;
        }

        public void setEquipementModuleLamellaire(List<TypeEquipementModuleLamellaire> equipementModuleLamellaire) {
			this.equipementModuleLamellaire = equipementModuleLamellaire;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementMoteurElectriqueManoeuvre" type="{}typeEquipementMoteurElectriqueManoeuvre"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementMoteurElectriqueManoeuvre"
    })
    public static class EquipementsMoteursElectriquesManoeuvres {

        protected List<TypeEquipementMoteurElectriqueManoeuvre> equipementMoteurElectriqueManoeuvre;

        /**
         * Gets the value of the equipementMoteurElectriqueManoeuvre property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementMoteurElectriqueManoeuvre property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementMoteurElectriqueManoeuvre().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementMoteurElectriqueManoeuvre }
         * 
         * 
         */
        public List<TypeEquipementMoteurElectriqueManoeuvre> getEquipementMoteurElectriqueManoeuvre() {
            if (equipementMoteurElectriqueManoeuvre == null) {
                equipementMoteurElectriqueManoeuvre = new ArrayList<TypeEquipementMoteurElectriqueManoeuvre>();
            }
            return this.equipementMoteurElectriqueManoeuvre;
        }

        public void setEquipementMoteurElectriqueManoeuvre(List<TypeEquipementMoteurElectriqueManoeuvre> equipementMoteurElectriqueManoeuvre) {
			this.equipementMoteurElectriqueManoeuvre = equipementMoteurElectriqueManoeuvre;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementOnduleur" type="{}typeEquipementOnduleur"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementOnduleur"
    })
    public static class EquipementsOnduleurs {

        protected List<TypeEquipementOnduleur> equipementOnduleur;

        /**
         * Gets the value of the equipementOnduleur property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementOnduleur property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementOnduleur().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementOnduleur }
         * 
         * 
         */
        public List<TypeEquipementOnduleur> getEquipementOnduleur() {
            if (equipementOnduleur == null) {
                equipementOnduleur = new ArrayList<TypeEquipementOnduleur>();
            }
            return this.equipementOnduleur;
        }

        public void setEquipementOnduleur(List<TypeEquipementOnduleur> equipementOnduleur) {
			this.equipementOnduleur = equipementOnduleur;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementsOuvrageEvacuationBarrage" type="{}typeEquipementOuvragesEvacuationBarrage"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementsOuvrageEvacuationBarrage"
    })
    public static class EquipementsOuvragesEvacuationBarrage {

        protected List<TypeEquipementOuvragesEvacuationBarrage> equipementsOuvrageEvacuationBarrage;

        /**
         * Gets the value of the equipementsOuvrageEvacuationBarrage property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementsOuvrageEvacuationBarrage property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementsOuvrageEvacuationBarrage().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementOuvragesEvacuationBarrage }
         * 
         * 
         */
        public List<TypeEquipementOuvragesEvacuationBarrage> getEquipementsOuvrageEvacuationBarrage() {
            if (equipementsOuvrageEvacuationBarrage == null) {
                equipementsOuvrageEvacuationBarrage = new ArrayList<TypeEquipementOuvragesEvacuationBarrage>();
            }
            return this.equipementsOuvrageEvacuationBarrage;
        }

        public void setEquipementsOuvrageEvacuationBarrage(List<TypeEquipementOuvragesEvacuationBarrage> equipementsOuvrageEvacuationBarrage) {
			this.equipementsOuvrageEvacuationBarrage = equipementsOuvrageEvacuationBarrage;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementOzoneur" type="{}typeEquipementOzoneur"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementOzoneur"
    })
    public static class EquipementsOzoneurs {

        protected List<TypeEquipementOzoneur> equipementOzoneur;

        /**
         * Gets the value of the equipementOzoneur property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementOzoneur property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementOzoneur().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementOzoneur }
         * 
         * 
         */
        public List<TypeEquipementOzoneur> getEquipementOzoneur() {
            if (equipementOzoneur == null) {
                equipementOzoneur = new ArrayList<TypeEquipementOzoneur>();
            }
            return this.equipementOzoneur;
        }

        public void setEquipementOzoneur(List<TypeEquipementOzoneur> equipementOzoneur) {
			this.equipementOzoneur = equipementOzoneur;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementPointPrelevement" type="{}typeEquipementPointPrelevement"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementPointPrelevement"
    })
    public static class EquipementsPointsprelevement {

        protected List<TypeEquipementPointPrelevement> equipementPointPrelevement;

        /**
         * Gets the value of the equipementPointPrelevement property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementPointPrelevement property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementPointPrelevement().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementPointPrelevement }
         * 
         * 
         */
        public List<TypeEquipementPointPrelevement> getEquipementPointPrelevement() {
            if (equipementPointPrelevement == null) {
                equipementPointPrelevement = new ArrayList<TypeEquipementPointPrelevement>();
            }
            return this.equipementPointPrelevement;
        }

        public void setEquipementPointPrelevement(List<TypeEquipementPointPrelevement> equipementPointPrelevement) {
			this.equipementPointPrelevement = equipementPointPrelevement;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementPompe" type="{}typeEquipementPompe"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementPompe"
    })
    public static class EquipementsPompes {

        protected List<TypeEquipementPompe> equipementPompe;

        /**
         * Gets the value of the equipementPompe property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementPompe property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementPompe().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementPompe }
         * 
         * 
         */
        public List<TypeEquipementPompe> getEquipementPompe() {
            if (equipementPompe == null) {
                equipementPompe = new ArrayList<TypeEquipementPompe>();
            }
            return this.equipementPompe;
        }

        public void setEquipementPompe(List<TypeEquipementPompe> equipementPompe) {
			this.equipementPompe = equipementPompe;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementPontRacleur" type="{}typeEquipementPontRacleur"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementPontRacleur"
    })
    public static class EquipementsPontsRacleurs {

        protected List<TypeEquipementPontRacleur> equipementPontRacleur;

        /**
         * Gets the value of the equipementPontRacleur property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementPontRacleur property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementPontRacleur().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementPontRacleur }
         * 
         * 
         */
        public List<TypeEquipementPontRacleur> getEquipementPontRacleur() {
            if (equipementPontRacleur == null) {
                equipementPontRacleur = new ArrayList<TypeEquipementPontRacleur>();
            }
            return this.equipementPontRacleur;
        }

        public void setEquipementPontRacleur(List<TypeEquipementPontRacleur> equipementPontRacleur) {
			this.equipementPontRacleur = equipementPontRacleur;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementPortail" type="{}typeEquipementPortail"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementPortail"
    })
    public static class EquipementsPortails {

        protected List<TypeEquipementPortail> equipementPortail;

        /**
         * Gets the value of the equipementPortail property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementPortail property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementPortail().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementPortail }
         * 
         * 
         */
        public List<TypeEquipementPortail> getEquipementPortail() {
            if (equipementPortail == null) {
                equipementPortail = new ArrayList<TypeEquipementPortail>();
            }
            return this.equipementPortail;
        }

        public void setEquipementPortail(List<TypeEquipementPortail> equipementPortail) {
			this.equipementPortail = equipementPortail;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementPorte" type="{}typeEquipementPorte"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementPorte"
    })
    public static class EquipementsPortes {

        protected List<TypeEquipementPorte> equipementPorte;

        /**
         * Gets the value of the equipementPorte property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementPorte property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementPorte().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementPorte }
         * 
         * 
         */
        public List<TypeEquipementPorte> getEquipementPorte() {
            if (equipementPorte == null) {
                equipementPorte = new ArrayList<TypeEquipementPorte>();
            }
            return this.equipementPorte;
        }

        public void setEquipementPorte(List<TypeEquipementPorte> equipementPorte) {
			this.equipementPorte = equipementPorte;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementPorteSectionnelle" type="{}typeEquipementPorteSectionnelle"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementPorteSectionnelle"
    })
    public static class EquipementsPortesSectionnelles {

        protected List<TypeEquipementPorteSectionnelle> equipementPorteSectionnelle;

        /**
         * Gets the value of the equipementPorteSectionnelle property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementPorteSectionnelle property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementPorteSectionnelle().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementPorteSectionnelle }
         * 
         * 
         */
        public List<TypeEquipementPorteSectionnelle> getEquipementPorteSectionnelle() {
            if (equipementPorteSectionnelle == null) {
                equipementPorteSectionnelle = new ArrayList<TypeEquipementPorteSectionnelle>();
            }
            return this.equipementPorteSectionnelle;
        }

        public void setEquipementPorteSectionnelle(List<TypeEquipementPorteSectionnelle> equipementPorteSectionnelle) {
			this.equipementPorteSectionnelle = equipementPorteSectionnelle;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementPortillon" type="{}typeEquipementPortillon"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementPortillon"
    })
    public static class EquipementsPortillons {

        protected List<TypeEquipementPortillon> equipementPortillon;

        /**
         * Gets the value of the equipementPortillon property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementPortillon property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementPortillon().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementPortillon }
         * 
         * 
         */
        public List<TypeEquipementPortillon> getEquipementPortillon() {
            if (equipementPortillon == null) {
                equipementPortillon = new ArrayList<TypeEquipementPortillon>();
            }
            return this.equipementPortillon;
        }

        public void setEquipementPortillon(List<TypeEquipementPortillon> equipementPortillon) {
			this.equipementPortillon = equipementPortillon;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementPrefiltre" type="{}typeEquipementPrefiltre"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementPrefiltre"
    })
    public static class EquipementsPrefiltres {

        protected List<TypeEquipementPrefiltre> equipementPrefiltre;

        /**
         * Gets the value of the equipementPrefiltre property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementPrefiltre property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementPrefiltre().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementPrefiltre }
         * 
         * 
         */
        public List<TypeEquipementPrefiltre> getEquipementPrefiltre() {
            if (equipementPrefiltre == null) {
                equipementPrefiltre = new ArrayList<TypeEquipementPrefiltre>();
            }
            return this.equipementPrefiltre;
        }

        public void setEquipementPrefiltre(List<TypeEquipementPrefiltre> equipementPrefiltre) {
			this.equipementPrefiltre = equipementPrefiltre;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementPreleveurEchantillonneur" type="{}typeEquipementPreleveurEchantillonneur"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementPreleveurEchantillonneur"
    })
    public static class EquipementsPreleveursEchantillonneurs {

        protected List<TypeEquipementPreleveurEchantillonneur> equipementPreleveurEchantillonneur;

        /**
         * Gets the value of the equipementPreleveurEchantillonneur property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementPreleveurEchantillonneur property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementPreleveurEchantillonneur().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementPreleveurEchantillonneur }
         * 
         * 
         */
        public List<TypeEquipementPreleveurEchantillonneur> getEquipementPreleveurEchantillonneur() {
            if (equipementPreleveurEchantillonneur == null) {
                equipementPreleveurEchantillonneur = new ArrayList<TypeEquipementPreleveurEchantillonneur>();
            }
            return this.equipementPreleveurEchantillonneur;
        }

        public void setEquipementPreleveurEchantillonneur(List<TypeEquipementPreleveurEchantillonneur> equipementPreleveurEchantillonneur) {
			this.equipementPreleveurEchantillonneur = equipementPreleveurEchantillonneur;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementPrelocalisateur" type="{}typeEquipementPrelocalisateur"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementPrelocalisateur"
    })
    public static class EquipementsPrelocalisateur {

        protected List<TypeEquipementPrelocalisateur> equipementPrelocalisateur;

        /**
         * Gets the value of the equipementPrelocalisateur property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementPrelocalisateur property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementPrelocalisateur().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementPrelocalisateur }
         * 
         * 
         */
        public List<TypeEquipementPrelocalisateur> getEquipementPrelocalisateur() {
            if (equipementPrelocalisateur == null) {
                equipementPrelocalisateur = new ArrayList<TypeEquipementPrelocalisateur>();
            }
            return this.equipementPrelocalisateur;
        }

        public void setEquipementPrelocalisateur(List<TypeEquipementPrelocalisateur> equipementPrelocalisateur) {
			this.equipementPrelocalisateur = equipementPrelocalisateur;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementPresseBoue" type="{}typeEquipementPresseBoue"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementPresseBoue"
    })
    public static class EquipementsPressesBoue {

        protected List<TypeEquipementPresseBoue> equipementPresseBoue;

        /**
         * Gets the value of the equipementPresseBoue property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementPresseBoue property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementPresseBoue().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementPresseBoue }
         * 
         * 
         */
        public List<TypeEquipementPresseBoue> getEquipementPresseBoue() {
            if (equipementPresseBoue == null) {
                equipementPresseBoue = new ArrayList<TypeEquipementPresseBoue>();
            }
            return this.equipementPresseBoue;
        }

        public void setEquipementPresseBoue(List<TypeEquipementPresseBoue> equipementPresseBoue) {
			this.equipementPresseBoue = equipementPresseBoue;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementProtectionIncendie" type="{}typeEquipementProtectionIncendie"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementProtectionIncendie"
    })
    public static class EquipementsProtectionsIncendie {

        protected List<TypeEquipementProtectionIncendie> equipementProtectionIncendie;

        /**
         * Gets the value of the equipementProtectionIncendie property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementProtectionIncendie property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementProtectionIncendie().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementProtectionIncendie }
         * 
         * 
         */
        public List<TypeEquipementProtectionIncendie> getEquipementProtectionIncendie() {
            if (equipementProtectionIncendie == null) {
                equipementProtectionIncendie = new ArrayList<TypeEquipementProtectionIncendie>();
            }
            return this.equipementProtectionIncendie;
        }

        public void setEquipementProtectionIncendie(List<TypeEquipementProtectionIncendie> equipementProtectionIncendie) {
			this.equipementProtectionIncendie = equipementProtectionIncendie;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementReacteur" type="{}typeEquipementReacteur"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementReacteur"
    })
    public static class EquipementsReacteur {

        protected List<TypeEquipementReacteur> equipementReacteur;

        /**
         * Gets the value of the equipementReacteur property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementReacteur property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementReacteur().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementReacteur }
         * 
         * 
         */
        public List<TypeEquipementReacteur> getEquipementReacteur() {
            if (equipementReacteur == null) {
                equipementReacteur = new ArrayList<TypeEquipementReacteur>();
            }
            return this.equipementReacteur;
        }

        public void setEquipementReacteur(List<TypeEquipementReacteur> equipementReacteur) {
			this.equipementReacteur = equipementReacteur;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementRetournementTerres" type="{}typeEquipementRetournementTerres"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementRetournementTerres"
    })
    public static class EquipementsRetournementsTerres {

        protected List<TypeEquipementRetournementTerres> equipementRetournementTerres;

        /**
         * Gets the value of the equipementRetournementTerres property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementRetournementTerres property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementRetournementTerres().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementRetournementTerres }
         * 
         * 
         */
        public List<TypeEquipementRetournementTerres> getEquipementRetournementTerres() {
            if (equipementRetournementTerres == null) {
                equipementRetournementTerres = new ArrayList<TypeEquipementRetournementTerres>();
            }
            return this.equipementRetournementTerres;
        }

        public void setEquipementRetournementTerres(List<TypeEquipementRetournementTerres> equipementRetournementTerres) {
			this.equipementRetournementTerres = equipementRetournementTerres;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementSecheurAir" type="{}typeEquipementSecheurAir"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementSecheurAir"
    })
    public static class EquipementsSecheursAir {

        protected List<TypeEquipementSecheurAir> equipementSecheurAir;

        /**
         * Gets the value of the equipementSecheurAir property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementSecheurAir property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementSecheurAir().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementSecheurAir }
         * 
         * 
         */
        public List<TypeEquipementSecheurAir> getEquipementSecheurAir() {
            if (equipementSecheurAir == null) {
                equipementSecheurAir = new ArrayList<TypeEquipementSecheurAir>();
            }
            return this.equipementSecheurAir;
        }

        public void setEquipementSecheurAir(List<TypeEquipementSecheurAir> equipementSecheurAir) {
			this.equipementSecheurAir = equipementSecheurAir;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementSilo" type="{}typeEquipementSilo"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementSilo"
    })
    public static class EquipementsSilos {

        protected List<TypeEquipementSilo> equipementSilo;

        /**
         * Gets the value of the equipementSilo property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementSilo property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementSilo().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementSilo }
         * 
         * 
         */
        public List<TypeEquipementSilo> getEquipementSilo() {
            if (equipementSilo == null) {
                equipementSilo = new ArrayList<TypeEquipementSilo>();
            }
            return this.equipementSilo;
        }

        public void setEquipementSilo(List<TypeEquipementSilo> equipementSilo) {
			this.equipementSilo = equipementSilo;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded">
     *         &lt;element name="equipementSocle" type="{}typeEquipementSocle"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementSocle"
    })
    public static class EquipementsSocle {

        @XmlElement(required = true)
        protected List<TypeEquipementSocle> equipementSocle;

        /**
         * Gets the value of the equipementSocle property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementSocle property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementSocle().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementSocle }
         * 
         * 
         */
        public List<TypeEquipementSocle> getEquipementSocle() {
            if (equipementSocle == null) {
                equipementSocle = new ArrayList<TypeEquipementSocle>();
            }
            return this.equipementSocle;
        }

        public void setEquipementSocle(List<TypeEquipementSocle> equipementSocle) {
			this.equipementSocle = equipementSocle;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementSoupape" type="{}typeEquipementSoupape"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementSoupape"
    })
    public static class EquipementsSoupapes {

        protected List<TypeEquipementSoupape> equipementSoupape;

        /**
         * Gets the value of the equipementSoupape property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementSoupape property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementSoupape().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementSoupape }
         * 
         * 
         */
        public List<TypeEquipementSoupape> getEquipementSoupape() {
            if (equipementSoupape == null) {
                equipementSoupape = new ArrayList<TypeEquipementSoupape>();
            }
            return this.equipementSoupape;
        }

        public void setEquipementSoupape(List<TypeEquipementSoupape> equipementSoupape) {
			this.equipementSoupape = equipementSoupape;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementStabilisateurEcoulement" type="{}typeEquipementStabilisateurEcoulement"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementStabilisateurEcoulement"
    })
    public static class EquipementsStabilisateursEcoulement {

        protected List<TypeEquipementStabilisateurEcoulement> equipementStabilisateurEcoulement;

        /**
         * Gets the value of the equipementStabilisateurEcoulement property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementStabilisateurEcoulement property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementStabilisateurEcoulement().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementStabilisateurEcoulement }
         * 
         * 
         */
        public List<TypeEquipementStabilisateurEcoulement> getEquipementStabilisateurEcoulement() {
            if (equipementStabilisateurEcoulement == null) {
                equipementStabilisateurEcoulement = new ArrayList<TypeEquipementStabilisateurEcoulement>();
            }
            return this.equipementStabilisateurEcoulement;
        }

        public void setEquipementStabilisateurEcoulement(List<TypeEquipementStabilisateurEcoulement> equipementStabilisateurEcoulement) {
			this.equipementStabilisateurEcoulement = equipementStabilisateurEcoulement;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementStationTeletransmission" type="{}typeEquipementStationTeletransmission"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementStationTeletransmission"
    })
    public static class EquipementsStationsTeletransmission {

        protected List<TypeEquipementStationTeletransmission> equipementStationTeletransmission;

        /**
         * Gets the value of the equipementStationTeletransmission property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementStationTeletransmission property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementStationTeletransmission().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementStationTeletransmission }
         * 
         * 
         */
        public List<TypeEquipementStationTeletransmission> getEquipementStationTeletransmission() {
            if (equipementStationTeletransmission == null) {
                equipementStationTeletransmission = new ArrayList<TypeEquipementStationTeletransmission>();
            }
            return this.equipementStationTeletransmission;
        }

        public void setEquipementStationTeletransmission(List<TypeEquipementStationTeletransmission> equipementStationTeletransmission) {
			this.equipementStationTeletransmission = equipementStationTeletransmission;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementSystemeDiffusion" type="{}typeEquipementSystemeDiffusion"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementSystemeDiffusion"
    })
    public static class EquipementsSystemeDiffusion {

        protected List<TypeEquipementSystemeDiffusion> equipementSystemeDiffusion;

        /**
         * Gets the value of the equipementSystemeDiffusion property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementSystemeDiffusion property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementSystemeDiffusion().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementSystemeDiffusion }
         * 
         * 
         */
        public List<TypeEquipementSystemeDiffusion> getEquipementSystemeDiffusion() {
            if (equipementSystemeDiffusion == null) {
                equipementSystemeDiffusion = new ArrayList<TypeEquipementSystemeDiffusion>();
            }
            return this.equipementSystemeDiffusion;
        }

        public void setEquipementSystemeDiffusion(List<TypeEquipementSystemeDiffusion> equipementSystemeDiffusion) {
			this.equipementSystemeDiffusion = equipementSystemeDiffusion;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementTransformateurHT" type="{}typeEquipementTransformateurHT"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementTransformateurHT"
    })
    public static class EquipementsTransformateursHT {

        protected List<TypeEquipementTransformateurHT> equipementTransformateurHT;

        /**
         * Gets the value of the equipementTransformateurHT property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementTransformateurHT property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementTransformateurHT().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementTransformateurHT }
         * 
         * 
         */
        public List<TypeEquipementTransformateurHT> getEquipementTransformateurHT() {
            if (equipementTransformateurHT == null) {
                equipementTransformateurHT = new ArrayList<TypeEquipementTransformateurHT>();
            }
            return this.equipementTransformateurHT;
        }

        public void setEquipementTransformateurHT(List<TypeEquipementTransformateurHT> equipementTransformateurHT) {
			this.equipementTransformateurHT = equipementTransformateurHT;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementTransformateurRedresseur" type="{}typeEquipementTransformateurRedresseur"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementTransformateurRedresseur"
    })
    public static class EquipementsTransformateursRedresseurs {

        protected List<TypeEquipementTransformateurRedresseur> equipementTransformateurRedresseur;

        /**
         * Gets the value of the equipementTransformateurRedresseur property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementTransformateurRedresseur property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementTransformateurRedresseur().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementTransformateurRedresseur }
         * 
         * 
         */
        public List<TypeEquipementTransformateurRedresseur> getEquipementTransformateurRedresseur() {
            if (equipementTransformateurRedresseur == null) {
                equipementTransformateurRedresseur = new ArrayList<TypeEquipementTransformateurRedresseur>();
            }
            return this.equipementTransformateurRedresseur;
        }

        public void setEquipementTransformateurRedresseur(List<TypeEquipementTransformateurRedresseur> equipementTransformateurRedresseur) {
			this.equipementTransformateurRedresseur = equipementTransformateurRedresseur;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementTrappeAcces" type="{}typeEquipementTrappeAcces"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementTrappeAcces"
    })
    public static class EquipementsTrappesAcces {

        protected List<TypeEquipementTrappeAcces> equipementTrappeAcces;

        /**
         * Gets the value of the equipementTrappeAcces property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementTrappeAcces property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementTrappeAcces().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementTrappeAcces }
         * 
         * 
         */
        public List<TypeEquipementTrappeAcces> getEquipementTrappeAcces() {
            if (equipementTrappeAcces == null) {
                equipementTrappeAcces = new ArrayList<TypeEquipementTrappeAcces>();
            }
            return this.equipementTrappeAcces;
        }

        public void setEquipementTrappeAcces(List<TypeEquipementTrappeAcces> equipementTrappeAcces) {
			this.equipementTrappeAcces = equipementTrappeAcces;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementTubeCrepine" type="{}typeEquipementTubeCrepine"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementTubeCrepine"
    })
    public static class EquipementsTubesCrepine {

        protected List<TypeEquipementTubeCrepine> equipementTubeCrepine;

        /**
         * Gets the value of the equipementTubeCrepine property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementTubeCrepine property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementTubeCrepine().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementTubeCrepine }
         * 
         * 
         */
        public List<TypeEquipementTubeCrepine> getEquipementTubeCrepine() {
            if (equipementTubeCrepine == null) {
                equipementTubeCrepine = new ArrayList<TypeEquipementTubeCrepine>();
            }
            return this.equipementTubeCrepine;
        }

        public void setEquipementTubeCrepine(List<TypeEquipementTubeCrepine> equipementTubeCrepine) {
			this.equipementTubeCrepine = equipementTubeCrepine;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementTurbineHydroelectrique" type="{}typeEquipementTurbineHydroelectrique"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementTurbineHydroelectrique"
    })
    public static class EquipementsTurbinesHydroelectriques {

        protected List<TypeEquipementTurbineHydroelectrique> equipementTurbineHydroelectrique;

        /**
         * Gets the value of the equipementTurbineHydroelectrique property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementTurbineHydroelectrique property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementTurbineHydroelectrique().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementTurbineHydroelectrique }
         * 
         * 
         */
        public List<TypeEquipementTurbineHydroelectrique> getEquipementTurbineHydroelectrique() {
            if (equipementTurbineHydroelectrique == null) {
                equipementTurbineHydroelectrique = new ArrayList<TypeEquipementTurbineHydroelectrique>();
            }
            return this.equipementTurbineHydroelectrique;
        }

        public void setEquipementTurbineHydroelectrique(List<TypeEquipementTurbineHydroelectrique> equipementTurbineHydroelectrique) {
			this.equipementTurbineHydroelectrique = equipementTurbineHydroelectrique;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementTuyauterie" type="{}typeEquipementTuyauterie"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementTuyauterie"
    })
    public static class EquipementsTuyauteries {

        protected List<TypeEquipementTuyauterie> equipementTuyauterie;

        /**
         * Gets the value of the equipementTuyauterie property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementTuyauterie property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementTuyauterie().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementTuyauterie }
         * 
         * 
         */
        public List<TypeEquipementTuyauterie> getEquipementTuyauterie() {
            if (equipementTuyauterie == null) {
                equipementTuyauterie = new ArrayList<TypeEquipementTuyauterie>();
            }
            return this.equipementTuyauterie;
        }

        public void setEquipementTuyauterie(List<TypeEquipementTuyauterie> equipementTuyauterie) {
			this.equipementTuyauterie = equipementTuyauterie;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementVanne" type="{}typeEquipementVanne"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementVanne"
    })
    public static class EquipementsVannes {

        protected List<TypeEquipementVanne> equipementVanne;

        /**
         * Gets the value of the equipementVanne property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementVanne property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementVanne().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementVanne }
         * 
         * 
         */
        public List<TypeEquipementVanne> getEquipementVanne() {
            if (equipementVanne == null) {
                equipementVanne = new ArrayList<TypeEquipementVanne>();
            }
            return this.equipementVanne;
        }

        public void setEquipementVanne(List<TypeEquipementVanne> equipementVanne) {
			this.equipementVanne = equipementVanne;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementVanneRegulation" type="{}typeEquipementVanneRegulation"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementVanneRegulation"
    })
    public static class EquipementsVannesRegulation {

        protected List<TypeEquipementVanneRegulation> equipementVanneRegulation;

        /**
         * Gets the value of the equipementVanneRegulation property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementVanneRegulation property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementVanneRegulation().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementVanneRegulation }
         * 
         * 
         */
        public List<TypeEquipementVanneRegulation> getEquipementVanneRegulation() {
            if (equipementVanneRegulation == null) {
                equipementVanneRegulation = new ArrayList<TypeEquipementVanneRegulation>();
            }
            return this.equipementVanneRegulation;
        }
        
        public void setEquipementVanneRegulation(List<TypeEquipementVanneRegulation> equipementVanneRegulation) {
			this.equipementVanneRegulation = equipementVanneRegulation;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementVariateurVitesse" type="{}typeEquipementVariateurVitesse"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementVariateurVitesse"
    })
    public static class EquipementsVariateursVitesse {

        protected List<TypeEquipementVariateurVitesse> equipementVariateurVitesse;

        /**
         * Gets the value of the equipementVariateurVitesse property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementVariateurVitesse property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementVariateurVitesse().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementVariateurVitesse }
         * 
         * 
         */
        public List<TypeEquipementVariateurVitesse> getEquipementVariateurVitesse() {
            if (equipementVariateurVitesse == null) {
                equipementVariateurVitesse = new ArrayList<TypeEquipementVariateurVitesse>();
            }
            return this.equipementVariateurVitesse;
        }
        
        public void setEquipementVariateurVitesse(List<TypeEquipementVariateurVitesse> equipementVariateurVitesse) {
			this.equipementVariateurVitesse = equipementVariateurVitesse;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementVentouse" type="{}typeEquipementVentouse"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementVentouse"
    })
    public static class EquipementsVentouses {

        protected List<TypeEquipementVentouse> equipementVentouse;

        /**
         * Gets the value of the equipementVentouse property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementVentouse property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementVentouse().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementVentouse }
         * 
         * 
         */
        public List<TypeEquipementVentouse> getEquipementVentouse() {
            if (equipementVentouse == null) {
                equipementVentouse = new ArrayList<TypeEquipementVentouse>();
            }
            return this.equipementVentouse;
        }
        
        public void setEquipementVentouse(List<TypeEquipementVentouse> equipementVentouse) {
			this.equipementVentouse = equipementVentouse;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementVerin" type="{}typeEquipementVerin"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementVerin"
    })
    public static class EquipementsVerins {

        protected List<TypeEquipementVerin> equipementVerin;

        /**
         * Gets the value of the equipementVerin property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementVerin property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementVerin().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementVerin }
         * 
         * 
         */
        public List<TypeEquipementVerin> getEquipementVerin() {
            if (equipementVerin == null) {
                equipementVerin = new ArrayList<TypeEquipementVerin>();
            }
            return this.equipementVerin;
        }
        
        public void setEquipementVerin(List<TypeEquipementVerin> equipementVerin) {
			this.equipementVerin = equipementVerin;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementVidange" type="{}typeEquipementVidange"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementVidange"
    })
    public static class EquipementsVidanges {

        protected List<TypeEquipementVidange> equipementVidange;

        /**
         * Gets the value of the equipementVidange property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementVidange property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementVidange().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementVidange }
         * 
         * 
         */
        public List<TypeEquipementVidange> getEquipementVidange() {
            if (equipementVidange == null) {
                equipementVidange = new ArrayList<TypeEquipementVidange>();
            }
            return this.equipementVidange;
        }
        
        public void setEquipementVidange(List<TypeEquipementVidange> equipementVidange) {
			this.equipementVidange = equipementVidange;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementVisTransfert" type="{}typeEquipementVisTransfert"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementVisTransfert"
    })
    public static class EquipementsVisTransfert {

        protected List<TypeEquipementVisTransfert> equipementVisTransfert;

        /**
         * Gets the value of the equipementVisTransfert property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementVisTransfert property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementVisTransfert().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementVisTransfert }
         * 
         * 
         */
        public List<TypeEquipementVisTransfert> getEquipementVisTransfert() {
            if (equipementVisTransfert == null) {
                equipementVisTransfert = new ArrayList<TypeEquipementVisTransfert>();
            }
            return this.equipementVisTransfert;
        }

        public void setEquipementVisTransfert(List<TypeEquipementVisTransfert> equipementVisTransfert) {
			this.equipementVisTransfert = equipementVisTransfert;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="equipementVoirie" type="{}typeEquipementVoirie"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "equipementVoirie"
    })
    public static class EquipementsVoiries {

        protected List<TypeEquipementVoirie> equipementVoirie;

        /**
         * Gets the value of the equipementVoirie property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equipementVoirie property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEquipementVoirie().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeEquipementVoirie }
         * 
         * 
         */
        public List<TypeEquipementVoirie> getEquipementVoirie() {
            if (equipementVoirie == null) {
                equipementVoirie = new ArrayList<TypeEquipementVoirie>();
            }
            return this.equipementVoirie;
        }
        
        public void setEquipementVoirie(List<TypeEquipementVoirie> equipementVoirie) {
			this.equipementVoirie = equipementVoirie;
		}
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="intervention" type="{}typeIntervention"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "intervention"
    })
    public static class Interventions {

        protected List<TypeIntervention> intervention;

        /**
         * Gets the value of the intervention property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the intervention property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getIntervention().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TypeIntervention }
         * 
         * 
         */
        public List<TypeIntervention> getIntervention() {
            if (intervention == null) {
                intervention = new ArrayList<TypeIntervention>();
            }
            return this.intervention;
        }
        
		public void setIntervention(List<TypeIntervention> intervention) {
			this.intervention = intervention;
		}
    }

}
