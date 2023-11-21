package bpm.gateway.core.veolia.patrimoine.xls;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jxl.CellType;
import jxl.DateCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import bpm.gateway.core.veolia.LogXML;
import bpm.gateway.core.veolia.patrimoine.PatrimoineDAO;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.Contrats;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.OuvrageBars;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.OuvrageBass;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.OuvrageCaps;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.OuvrageCars;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.OuvrageCpts;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.OuvrageLags;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.OuvrageLogs;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.OuvragePbas;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.OuvragePcas;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.OuvragePres;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.OuvrageRsts;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.OuvrageSocles;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.OuvrageStps;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.OuvrageUsis;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.Sites;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.UniteCars;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.UniteChls;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.UniteClas;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.UniteCpts;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.UniteEles;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.UniteEsvs;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.UniteFils;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.UniteGcis;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.UniteHyds;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.UniteMins;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.UniteOxys;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.UnitePoms;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.UniteRecs;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.UniteRegs;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.UniteSecs;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.UniteSets;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.UniteSocles;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.UniteSpes;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.UniteTlgs;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.UniteTrts;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls.UniteUlts;

public class PatrimoineXlsHelper {
	private static final int FORMAT_TYPE_1 = 1;
	private static final int FORMAT_TYPE_2 = 2;
	private static final int FORMAT_TYPE_3 = 2;

	private static SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat format2 = new SimpleDateFormat("dd-MM-yyyy");
	private static SimpleDateFormat format3 = new SimpleDateFormat("dd/MM/yyyy");

	public static PatrimoineXls buildPatrimoineXls(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, InputStream fis, String fileName) throws Exception {
		PatrimoineXls patrimoineXls = new PatrimoineXls();
		try {
			WorkbookSettings workbookSettings = new WorkbookSettings();
			workbookSettings.setEncoding("Cp1252");

			Workbook workbook = Workbook.getWorkbook(fis, workbookSettings);
			logsRuntime.add(new XlsRuntimeLog(false, " workbook opened"));

			Contrats contrats = buildContrats(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatContrats(contrats);

			Sites patSites = buildSites(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatSites(patSites);

			OuvrageSocles patOuvrageSocles = buildOuvrageSocles(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatOuvrageSocles(patOuvrageSocles);

			OuvrageBars patOuvrageBars = buildOuvrageBars(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatOuvrageBars(patOuvrageBars);

			OuvrageUsis patOuvrageUsis = buildOuvrageUsis(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatOuvrageUsis(patOuvrageUsis);

			OuvrageLogs patOuvrageLogs = buildOuvrageLogs(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatOuvrageLogs(patOuvrageLogs);

			OuvrageCars patOuvrageCars = buildOuvrageCars(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatOuvrageCars(patOuvrageCars);

			OuvrageLags patOuvrageLags = buildOuvrageLags(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatOuvrageLags(patOuvrageLags);

			OuvrageRsts patOuvrageRsts = buildOuvrageRsts(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatOuvrageRsts(patOuvrageRsts);

			OuvrageBass patOuvrageBass = buildOuvrageBass(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatOuvrageBass(patOuvrageBass);

			OuvrageStps patOuvrageStps = buildOuvrageStps(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatOuvrageStps(patOuvrageStps);

			OuvragePcas patOuvragePcas = buildOuvragePcas(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatOuvragePcas(patOuvragePcas);

			OuvrageCaps patOuvrageCaps = buildOuvrageCaps(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatOuvrageCaps(patOuvrageCaps);

			OuvragePbas patOuvragePbas = buildOuvragePbas(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatOuvragePbas(patOuvragePbas);

			OuvrageCpts patOuvrageCpts = buildOuvrageCpts(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatOuvrageCpts(patOuvrageCpts);

			OuvragePres patOuvragePres = buildOuvragePres(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatOuvragePres(patOuvragePres);

			UniteCars patUniteCars = buildUniteCars(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatUniteCars(patUniteCars);

			UniteChls patUniteChls = buildUniteChls(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatUniteChls(patUniteChls);

			UniteClas patUniteClas = buildUniteClas(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatUniteClas(patUniteClas);

			UniteCpts patUniteCpts = buildUniteCpts(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatUniteCpts(patUniteCpts);

			UniteEles patUniteEles = buildUniteEles(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatUniteEles(patUniteEles);

			UniteEsvs patUniteEsvs = buildUniteEsvs(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatUniteEsvs(patUniteEsvs);

			UniteFils patUniteFils = buildUniteFils(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatUniteFils(patUniteFils);

			UniteGcis patUniteGcis = buildUniteGcis(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatUniteGcis(patUniteGcis);

			UniteHyds patUniteHyds = buildUniteHyds(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatUniteHyds(patUniteHyds);

			UniteMins patUniteMins = buildUniteMins(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatUniteMins(patUniteMins);

			UniteOxys patUniteOxys = buildUniteOxys(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatUniteOxys(patUniteOxys);

			UnitePoms patUnitePoms = buildUnitePoms(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatUnitePoms(patUnitePoms);

			UniteRecs patUniteRecs = buildUniteRecs(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatUniteRecs(patUniteRecs);

			UniteRegs patUniteRegs = buildUniteRegs(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatUniteRegs(patUniteRegs);

			UniteSecs patUniteSecs = buildUniteSecs(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatUniteSecs(patUniteSecs);

			UniteSets patUniteSets = buildUniteSets(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatUniteSets(patUniteSets);

			UniteSocles patUniteSocles = buildUniteSocles(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatUniteSocles(patUniteSocles);

			UniteSpes patUniteSpes = buildUniteSpes(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatUniteSpes(patUniteSpes);

			UniteTlgs patUniteTlgs = buildUniteTlgs(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatUniteTlgs(patUniteTlgs);

			UniteTrts patUniteTrts = buildUniteTrts(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatUniteTrts(patUniteTrts);

			UniteUlts patUniteUlts = buildUniteUlts(logsRuntime, logsXml, workbook, fileName);
			patrimoineXls.setPatUniteUlts(patUniteUlts);

			workbook.close();
		} catch (BiffException e) {
			logsRuntime.add(new XlsRuntimeLog(true, " unable to open workbook"));
			throw e;
		} catch (IOException e) {
			logsRuntime.add(new XlsRuntimeLog(true, " unable to open workbook"));
			throw e;
		}
		return patrimoineXls;
	}

	private static Sheet getSheet(List<XlsRuntimeLog> logsRuntime, Workbook workbook, String sheetName) {
		if (sheetName != null && sheetName.contains("patrimoine_")) {
			sheetName = sheetName.split("patrimoine_")[1];
		}
		Sheet sheet = workbook.getSheet(sheetName);

		if (sheet == null) {
			logsRuntime.add(new XlsRuntimeLog(false, " unable to find the sheet " + sheetName));
		}
		logsRuntime.add(new XlsRuntimeLog(false, " sheet " + sheetName + " selected"));
		return sheet;
	}

	private static Contrats buildContrats(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.CONTRAT);

		Contrats contrats = new Contrats();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				Contrat contrat = new Contrat();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}

					try {
						if (i == 0) {
							contrat.setContratVeId(value);
						}
						else if (i == 1) {
							contrat.setContratSecteur(value);
						}
						else if (i == 2) {
							contrat.setContratDenomination(value);
						}
						else if (i == 3) {
							contrat.setContratExploitant(value);
						}
						else if (i == 4) {
							contrat.setContratDatedebut(getDateValue("contrat_datedebut", value, FORMAT_TYPE_1));
						}
						else if (i == 5) {
							contrat.setContratDatefin(getDateValue("contrat_datefin", value, FORMAT_TYPE_1));
						}
						else if (i == 6) {
							contrat.setContratAbrevnom(value);
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.CONTRAT, fileName, contrat.getContratVeId(), e);
					}
				}

				if (!isEmpty) {
					contrats.addContrat(contrat);
				}
			}
		}

		return contrats;
	}

	private static Sites buildSites(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.SITE);

		Sites items = new Sites();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				Site item = new Site();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}

					try {
						if (i == 0) {
							item.setSiteVEID(value);
						}
						else if (i == 1) {
							item.setSiteVeContrat(value);
						}
						else if (i == 2) {
							item.setSiteDenomination(value);
						}
						else if (i == 3) {
							item.setSiteCompetence(value);
						}
						else if (i == 4) {
							item.setSiteProd(value);
						}
						else if (i == 5) {
							item.setSiteSecteur(value);
						}
						else if (i == 6) {
							item.setSiteCommune(value);
						}
						else if (i == 7) {
							item.setSiteInsee(value);
						}
						else if (i == 8) {
							item.setSiteNordwgs84(value);
						}
						else if (i == 9) {
							item.setSiteOuestwgs84(value);
						}
						else if (i == 10) {
							item.setSiteSection(value);
						}
						else if (i == 11) {
							item.setSiteNumsection(getIntegerValue("site_numsection", value));
						}
						else if (i == 12) {
							item.setSiteNomProprietaire(value);
						}
						else if (i == 13) {
							item.setSiteStatut(value);
						}
						else if (i == 14) {
							item.setSiteFonctionnement(value);
						}
						else if (i == 15) {
							item.setSiteInondable(value);
						}
						else if (i == 16) {
							item.setSiteProtection(value);
						}
						else if (i == 17) {
							item.setSitePprdate(getDateValue("site_pprdate", value, FORMAT_TYPE_1));
						}
						else if (i == 18) {
							item.setSiteCommentaire(value);
						}
						else if (i == 19) {
							item.setSitePj1(value);
						}
						else if (i == 20) {
							item.setSitePj2(value);
						}
						else if (i == 21) {
							item.setSitePj3(value);
						}
						else if (i == 22) {
							item.setSitePj4(value);
						}
						else if (i == 23) {
							item.setSitePj6(value);
						}
						else if (i == 24) {
							item.setSiteTiers(value);
						}
						else if (i == 25) {
							item.setSiteConvention(value);
						}
						else if (i == 26) {
							item.setSiteServitudes(value);
						}
						else if (i == 27) {
							item.setSitePjservitudes(value);
						}
						else if (i == 28) {
							item.setSitePj10(value);
						}
						else if (i == 29) {
							item.setSitePj11(value);
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.SITE, fileName, item.getSiteVEID(), e);
					}
				}

				if (!isEmpty) {
					items.addSites(item);
				}
			}
		}

		return items;
	}

	private static OuvrageSocles buildOuvrageSocles(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.OUVRAGE_SOCLE);

		OuvrageSocles items = new OuvrageSocles();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				OuvrageSocle item = new OuvrageSocle();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}

					try {

						if (i == 0) {
							item.setOuvragesocleVeId(value);
						}
						else if (i == 1) {
							item.setOuvragesocleSiteId(value);
						}
						else if (i == 2) {
							item.setOuvragesocleDenomination(value);
						}
						else if (i == 3) {
							item.setOuvragesocleTypeLong(value);
						}
						else if (i == 4) {
							item.setOuvragesocleDescription(value);
						}
						else if (i == 5) {
							item.setOuvragesocleDateconstruction(getIntegerValue("ouvragesocle_dateconstruction", value));
						}
						else if (i == 6) {
							item.setOuvragesocleMiseenservice(getDateValue("ouvragesocle_miseenservice", value, FORMAT_TYPE_1));
						}
						else if (i == 7) {
							item.setOuvragesocleDaterehab(getDateValue("ouvragesocle_daterehab", value, FORMAT_TYPE_1));
						}
						else if (i == 8) {
							item.setOuvragesocleDatefermeture(getDateValue("ouvragesocle_datefermeture", value, FORMAT_TYPE_1));
						}
						else if (i == 9) {
							item.setOuvragesocleCommentaire(value);
						}
						else if (i == 10) {
							item.setOuvragesocleStatut(value);
						}
						else if (i == 11) {
							item.setOuvragesocleCotesol(getDoubleValue("ouvragesocle_cotesol", value));
						}
						else if (i == 12) {
							item.setOuvragesocleXcc47(getDoubleValue("ouvragesocle_xcc47", value));
						}
						else if (i == 13) {
							item.setOuvragesocleYcc47(getDoubleValue("ouvragesocle_ycc47", value));
						}
						else if (i == 14) {
							item.setOuvragesocleXcc47recal(getDoubleValue("ouvragesocle_xcc47recal", value));
						}
						else if (i == 15) {
							item.setOuvragesocleYcc47recal(getDoubleValue("ouvragesocle_ycc47recal", value));
						}
						else if (i == 16) {
							item.setOuvragesoclePj1(value);
						}
						else if (i == 17) {
							item.setOuvragesoclePj2(value);
						}
						else if (i == 18) {
							item.setOuvragesoclePj3(value);
						}
						else if (i == 19) {
							item.setOuvragesoclePj5(value);
						}
						else if (i == 20) {
							item.setOuvragesoclePj6(value);
						}
						else if (i == 21) {
							item.setOuvragesoclePj7(value);
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.OUVRAGE_SOCLE, fileName, item.getOuvragesocleVeId(), e);
					}
				}

				if (!isEmpty) {
					items.addOuvrageSocles(item);
				}
			}
		}

		return items;
	}

	private static OuvrageBars buildOuvrageBars(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.OUVRAGE_BAR);

		OuvrageBars items = new OuvrageBars();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				OuvrageBar item = new OuvrageBar();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}

					try {
						if (i == 0) {
							item.setOuvragebarOuvragesocleId(value);
						}
						else if (i == 1) {
							item.setOuvragebarVolumestocke(getDoubleValue("ouvragebar_volumestocke", value));
						}
						else if (i == 2) {
							item.setOuvragebarEpoqueconstruction(value);
						}
						else if (i == 3) {
							item.setOuvragebarTypebarrage(value);
						}
						else if (i == 4) {
							item.setOuvragebarTypebarragedetail(value);
						}
						else if (i == 5) {
							item.setOuvragebarFruitparement(value);
						}
						else if (i == 6) {
							item.setOuvragebarHauteurtn(getDoubleValue("ouvragebar_hauteurtn", value));
						}
						else if (i == 7) {
							item.setOuvragebarHauteurfondation(getDoubleValue("ouvragebar_hauteurfondation", value));
						}
						else if (i == 8) {
							item.setOuvragebarLongueurcrete(getDoubleValue("ouvragebar_longueurcrete", value));
						}
						else if (i == 9) {
							item.setOuvragebarLargeurcrete(getDoubleValue("ouvragebar_largeurcrete", value));
						}
						else if (i == 10) {
							item.setOuvragebarSurfaceplaneau(getIntegerValue("ouvragebar_surfaceplaneau", value));
						}
						else if (i == 11) {
							item.setOuvragebarCapaciteretenue(getIntegerValue("ouvragebar_capaciteretenue", value));
						}
						else if (i == 12) {
							item.setOuvragebarSuperficiebv(getIntegerValue("ouvragebar_superficiebv", value));
						}
						else if (i == 13) {
							item.setOuvragebarDebitcrucent(getIntegerValue("ouvragebar_debitcrucent", value));
						}
						else if (i == 14) {
							item.setOuvragebarDebitcrudec(getIntegerValue("ouvragebar_debitcrudec", value));
						}
						else if (i == 15) {
							item.setOuvragebarDebitcrumil(getIntegerValue("ouvragebar_debitcrumil", value));
						}
						else if (i == 16) {
							item.setOuvragebarDernierevidange(getIntegerValue("ouvragebar_dernierevidange", value));
						}
						else if (i == 17) {
							item.setOuvragebarIrrigation(value);
						}
						else if (i == 18) {
							item.setOuvragebarIrrigationvolume(getIntegerValue("ouvragebar_irrigationvolume", value));
						}
						else if (i == 19) {
							item.setOuvragebarIrrigationtype(value);
						}
						else if (i == 20) {
							item.setOuvragebarProdelec(value);
						}
						else if (i == 21) {
							item.setOuvragebarSoutienetiage(getDoubleValue("ouvragebar_soutienetiage", value));
						}
						else if (i == 22) {
							item.setOuvragebarCotecrete(getDoubleValue("ouvragebar_cotecrete", value));
						}
						else if (i == 23) {
							item.setOuvragebarCotelegale(getDoubleValue("ouvragebar_cotelegale", value));
						}
						else if (i == 24) {
							item.setOuvragebarCotephe(getDoubleValue("ouvragebar_cotephe", value));
						}
						else if (i == 25) {
							item.setOuvragebarCotehiver(getDoubleValue("ouvragebar_cotehiver", value));
						}
						else if (i == 26) {
							item.setOuvragebarDebitreserve(getDoubleValue("ouvragebar_debitreserve", value));
						}
						else if (i == 27) {
							item.setOuvragebarQmaxevacuation(getDoubleValue("ouvragebar_qmaxevacuation", value));
						}
						else if (i == 28) {
							item.setOuvragebarPrebarrage(value);
						}
						else if (i == 29) {
							item.setOuvragebarEutrophisation(value);
						}
						else if (i == 30) {
							item.setOuvragebarClasse(value);
						}
						else if (i == 31) {
							item.setOuvragebarPj1(value);
						}
						else if (i == 32) {
							item.setOuvragebarPj2(value);
						}
						else if (i == 33) {
							item.setOuvragebarPj3(value);
						}
						else if (i == 34) {
							item.setOuvragebarPj4(value);
						}
						else if (i == 35) {
							item.setOuvragebarPj5(value);
						}
						else if (i == 36) {
							item.setOuvragebarPj6(value);
						}
						else if (i == 37) {
							item.setOuvragebarPj7(value);
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.OUVRAGE_BAR, fileName, item.getOuvragebarOuvragesocleId(), e);
					}
				}

				if (!isEmpty) {
					items.addOuvrageBars(item);
				}
			}
		}

		return items;
	}

	private static OuvrageUsis buildOuvrageUsis(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.OUVRAGE_USI);

		OuvrageUsis items = new OuvrageUsis();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				OuvrageUsi item = new OuvrageUsi();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}

					try {
						if (i == 0) {
							item.setOuvrageusiOuvragesocleId(value);
						}
						else if (i == 1) {
							item.setOuvrageusiAutorisationprelevdate(getDateValue("ouvrageusi_autorisationprelevdate", value, FORMAT_TYPE_1));
						}
						else if (i == 2) {
							item.setOuvrageusiAutorisationprelevcom(value);
						}
						else if (i == 3) {
							item.setOuvrageusiCapacitetraitement(value);
						}
						else if (i == 4) {
							item.setOuvrageusiDebitprodhiver(value);
						}
						else if (i == 5) {
							item.setOuvrageusiDebitprodinter(value);
						}
						else if (i == 6) {
							item.setOuvrageusiDebitprodete(value);
						}
						else if (i == 7) {
							item.setOuvrageusiSuiviqualitatif(value);
						}
						else if (i == 8) {
							item.setOuvrageusiPj1(value);
						}
						else if (i == 9) {
							item.setOuvrageusiPj2(value);
						}
						else if (i == 10) {
							item.setOuvrageusiPj3(value);
						}
						else if (i == 11) {
							item.setOuvrageusiPj4(value);
						}
						else if (i == 12) {
							item.setOuvrageusiPj5(value);
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.OUVRAGE_USI, fileName, item.getOuvrageusiOuvragesocleId(), e);
					}
				}

				if (!isEmpty) {
					items.addOuvrageUsis(item);
				}
			}
		}

		return items;
	}

	private static OuvrageLogs buildOuvrageLogs(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.OUVRAGE_LOG);

		OuvrageLogs items = new OuvrageLogs();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				OuvrageLog item = new OuvrageLog();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}

					try {
						if (i == 0) {
							item.setOuvragelogOuvragesocleId(value);
						}
						else if (i == 1) {
							item.setOuvragelogFonction(value);
						}
						else if (i == 2) {
							item.setOuvragelogTypefondation(value);
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.OUVRAGE_LOG, fileName, item.getOuvragelogOuvragesocleId(), e);
					}
				}

				if (!isEmpty) {
					items.addOuvrageLogs(item);
				}
			}
		}

		return items;
	}

	private static OuvrageCars buildOuvrageCars(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.OUVRAGE_CAR);

		OuvrageCars items = new OuvrageCars();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				OuvrageCar item = new OuvrageCar();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}

					try {
						if (i == 0) {
							item.setOuvragecarOuvragesocleId(value);
						}
						else if (i == 1) {
							item.setOuvragecarSurfaceplaneau(getDoubleValue("ouvragecar_surfaceplaneau", value));
						}
						else if (i == 2) {
							item.setOuvragecarCapaciteretenue(getDoubleValue("ouvragecar_capaciteretenue", value));
						}
						else if (i == 3) {
							item.setOuvragecarSuperficiebv(getDoubleValue("ouvragecar_superficiebv", value));
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.OUVRAGE_CAR, fileName, item.getOuvragecarOuvragesocleId(), e);
					}
				}

				if (!isEmpty) {
					items.addOuvrageCars(item);
				}
			}
		}

		return items;
	}

	private static OuvrageLags buildOuvrageLags(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.OUVRAGE_LAG);

		OuvrageLags items = new OuvrageLags();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				OuvrageLag item = new OuvrageLag();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}

					try {
						if (i == 0) {
							item.setOuvragelagOuvragesocleId(value);
						}
						else if (i == 1) {
							item.setOuvragelagLargeur(getDoubleValue("ouvragelag_largeur", value));
						}
						else if (i == 2) {
							item.setOuvragelagLongueur(getDoubleValue("ouvragelag_longueur", value));
						}
						else if (i == 3) {
							item.setOuvragelagProfondeur(getDoubleValue("ouvragelag_profondeur", value));
						}
						else if (i == 4) {
							item.setOuvragelagSurfaceunitaire(getDoubleValue("ouvragelag_surfaceunitaire", value));
						}
						else if (i == 5) {
							item.setOuvragelagVolumenominal(getDoubleValue("ouvragelag_volumenominal", value));
						}
						else if (i == 6) {
							item.setOuvragelagVolumeStocke(getDoubleValue("ouvragelag_volume_stocke", value));
						}
						else if (i == 7) {
							item.setOuvragelagHauteur(getDoubleValue("ouvragelag_hauteur", value));
						}
						else if (i == 8) {
							item.setOuvragelagMateriauconstruction(value);
						}
						else if (i == 9) {
							item.setOuvragelagMateriauetancheite(value);
						}
						else if (i == 10) {
							item.setOuvragelagMateriauisolation(value);
						}
						else if (i == 11) {
							item.setOuvragelagTpssejour(value);
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.OUVRAGE_LAG, fileName, item.getOuvragelagOuvragesocleId(), e);
					}
				}

				if (!isEmpty) {
					items.addOuvrageLags(item);
				}
			}
		}

		return items;
	}

	private static OuvrageRsts buildOuvrageRsts(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.OUVRAGE_RST);

		OuvrageRsts items = new OuvrageRsts();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				OuvrageRst item = new OuvrageRst();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}

					try {
						if (i == 0) {
							item.setOuvragerstOuvragesocleId(value);
						}
						else if (i == 1) {
							item.setOuvragerstCoteradier(getDoubleValue("ouvragerst_coteradier", value));
						}
						else if (i == 2) {
							item.setOuvragerstCotetropplein(getDoubleValue("ouvragerst_cotetropplein", value));
						}
						else if (i == 3) {
							item.setOuvragerstHauteurtour(getDoubleValue("ouvragerst_hauteurtour", value));
						}
						else if (i == 4) {
							item.setOuvragerstHauteurtotale(getDoubleValue("ouvragerst_hauteurtotale", value));
						}
						else if (i == 5) {
							item.setOuvragerstVolume(getIntegerValue("ouvragerst_volume", value));
						}
						else if (i == 6) {
							item.setOuvragerstVolumeutile(getIntegerValue("ouvragerst_volumeutile", value));
						}
						else if (i == 7) {
							item.setOuvragerstAntenniste(value);
						}
						else if (i == 8) {
							item.setOuvragerstPjvolume(value);
						}
						else if (i == 9) {
							item.setOuvragerstAntennisteliste(value);
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.OUVRAGE_RST, fileName, item.getOuvragerstOuvragesocleId(), e);
					}
				}

				if (!isEmpty) {
					items.addOuvrageRsts(item);
				}
			}
		}

		return items;
	}

	private static OuvrageBass buildOuvrageBass(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.OUVRAGE_BAS);

		OuvrageBass items = new OuvrageBass();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				OuvrageBas item = new OuvrageBas();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}

					try {
						if (i == 0) {
							item.setOuvragebasOuvragesocleId(value);
						}
						else if (i == 1) {
							item.setOuvragebasCoteradier(getDoubleValue("ouvragebas_coteradier", value));
						}
						else if (i == 2) {
							item.setOuvragebasCotetropplein(getDoubleValue("ouvragebas_cotetropplein", value));
						}
						else if (i == 3) {
							item.setOuvragebasHauteurtotale(getDoubleValue("ouvragebas_hauteurtotale", value));
						}
						else if (i == 4) {
							item.setOuvragebasVolume(getDoubleValue("ouvragebas_volume", value));
						}
						else if (i == 5) {
							item.setOuvragebasVolumeutile(getIntegerValue("ouvragebas_volumeutile", value));
						}
						else if (i == 6) {
							item.setOuvragebasPosition(value);
						}
						else if (i == 7) {
							item.setOuvragebasAntenniste(value);
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.OUVRAGE_BAS, fileName, item.getOuvragebasOuvragesocleId(), e);
					}
				}

				if (!isEmpty) {
					items.addOuvrageBass(item);
				}
			}
		}

		return items;
	}

	private static OuvrageStps buildOuvrageStps(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.OUVRAGE_STP);

		OuvrageStps items = new OuvrageStps();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				OuvrageStp item = new OuvrageStp();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}

					try {
						if (i == 0) {
							item.setOuvragestpOuvragesocleId(value);
						}
						else if (i == 1) {
							item.setOuvragestpTypestationpompage(value);
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.OUVRAGE_STP, fileName, item.getOuvragestpOuvragesocleId(), e);
					}
				}

				if (!isEmpty) {
					items.addOuvrageStps(item);
				}
			}
		}

		return items;
	}

	private static OuvragePcas buildOuvragePcas(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.OUVRAGE_PCA);

		OuvragePcas items = new OuvragePcas();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				OuvragePca item = new OuvragePca();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}

					try {
						if (i == 0) {
							item.setOuvragepcaOuvragesocleId(value);
						}
						else if (i == 1) {
							item.setOuvragepcaTypeposte(value);
						}
						else if (i == 2) {
							item.setOuvragepcaCommentaire(value);
						}
						else if (i == 3) {
							item.setOuvragepcaPlanassocie(value);
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.OUVRAGE_PCA, fileName, item.getOuvragepcaOuvragesocleId(), e);
					}
				}

				if (!isEmpty) {
					items.addOuvragePcas(item);
				}
			}
		}

		return items;
	}

	private static OuvrageCaps buildOuvrageCaps(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.OUVRAGE_CAP);

		OuvrageCaps items = new OuvrageCaps();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				OuvrageCap item = new OuvrageCap();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}

					try {
						if (i == 0) {
							item.setOuvragecapOuvragesocleId(value);
						}
						else if (i == 1) {
							item.setOuvragecapCoteniveau(getDoubleValue("ouvragecap_coteniveau", value));
						}
						else if (i == 2) {
							item.setOuvragecapNbss(value);
						}
						else if (i == 3) {
							item.setOuvragecapNsandre(value);
						}
						else if (i == 4) {
							item.setOuvragecapProfondeur(getDoubleValue("ouvragecap_profondeur", value));
						}
						else if (i == 5) {
							item.setOuvragecapAquifere(value);
						}
						else if (i == 6) {
							item.setOuvragecapAutorisationprelevdate(getDateValue("ouvragecap_autorisationprelevdate", value, FORMAT_TYPE_1));
						}
						else if (i == 7) {
							item.setOuvragecapAutorisationprelevcom(value);
						}
						else if (i == 8) {
							item.setOuvragecapSuiviqualificatif(value);
						}
						else if (i == 9) {
							item.setOuvragecapTypeouvrage(value);
						}
						else if (i == 10) {
							item.setOuvragecapTypecaptage(value);
						}
						else if (i == 11) {
							item.setOuvragecapPompage(value);
						}
						else if (i == 12) {
							item.setOuvragecapEtat(value);
						}
						else if (i == 13) {
							item.setOuvragecapPiezometre(value);
						}
						else if (i == 14) {
							item.setOuvragecapPj1(value);
						}
						else if (i == 15) {
							item.setOuvragecapPj2(value);
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.OUVRAGE_CAP, fileName, item.getOuvragecapOuvragesocleId(), e);
					}
				}

				if (!isEmpty) {
					items.addOuvrageCaps(item);
				}
			}
		}

		return items;
	}

	private static OuvragePbas buildOuvragePbas(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.OUVRAGE_PBA);

		OuvragePbas items = new OuvragePbas();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				OuvragePba item = new OuvragePba();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}

					try {
						if (i == 0) {
							item.setOuvragepbaOuvragesocleId(value);
						}
						else if (i == 1) {
							item.setOuvragepbaVolumestocke(getDoubleValue("ouvragepba_volumestocke", value));
						}
						else if (i == 2) {
							item.setOuvragepbaEpoqueconstruction(value);
						}
						else if (i == 3) {
							item.setOuvragepbaDatemes(getIntegerValue("ouvragepba_datemes", value));
						}
						else if (i == 4) {
							item.setOuvragepbaTypeprebarrage(value);
						}
						else if (i == 5) {
							item.setOuvragepbaTypeprebarragedetail(value);
						}
						else if (i == 6) {
							item.setOuvragepbaFruitparement(value);
						}
						else if (i == 7) {
							item.setOuvragepbaHauteurtn(getDoubleValue("ouvragepba_hauteurtn", value));
						}
						else if (i == 8) {
							item.setOuvragepbaHauteurfondation(getDoubleValue("ouvragepba_hauteurfondation", value));
						}
						else if (i == 9) {
							item.setOuvragepbaLongueurcrete(getDoubleValue("ouvragepba_longueurcrete", value));
						}
						else if (i == 10) {
							item.setOuvragepbaLargeurcrete(getDoubleValue("ouvragepba_largeurcrete", value));
						}
						else if (i == 11) {
							item.setOuvragepbaSurfaceplaneau(getDoubleValue("ouvragepba_surfaceplaneau", value));
						}
						else if (i == 12) {
							item.setOuvragepbaCapaciteretenue(getDoubleValue("ouvragepba_capaciteretenue", value));
						}
						else if (i == 13) {
							item.setOuvragepbaSuperficiebv(getDoubleValue("ouvragepba_superficiebv", value));
						}
						else if (i == 14) {
							item.setOuvragepbaDernierevidange(getDateValue("ouvragepba_dernierevidange", value, FORMAT_TYPE_1));
						}
						else if (i == 15) {
							item.setOuvragepbaCotecrete(getDoubleValue("ouvragepba_cotecrete", value));
						}
						else if (i == 16) {
							item.setOuvragepbaCotelegale(getDoubleValue("ouvragepba_cotelegale", value));
						}
						else if (i == 17) {
							item.setOuvragepbaCotephe(getDoubleValue("ouvragepba_cotephe", value));
						}
						else if (i == 18) {
							item.setOuvragepbaCotehiver(getDoubleValue("ouvragepba_cotehiver", value));
						}
						else if (i == 19) {
							item.setOuvragepbaDebitreserve(getDoubleValue("ouvragepba_debitreserve", value));
						}
						else if (i == 20) {
							item.setOuvragepbaQmaxevacuation(getDoubleValue("ouvragepba_qmaxevacuation", value));
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.OUVRAGE_PBA, fileName, item.getOuvragepbaOuvragesocleId(), e);
					}
				}

				if (!isEmpty) {
					items.addOuvragePbas(item);
				}
			}
		}

		return items;
	}

	private static OuvrageCpts buildOuvrageCpts(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.OUVRAGE_CPT);

		OuvrageCpts items = new OuvrageCpts();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				OuvrageCpt item = new OuvrageCpt();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}

					try {
						if (i == 0) {
							item.setOuvragecptOuvragesocleId(value);
						}
						else if (i == 1) {
							item.setOuvragecptCodetype(value);
						}
						else if (i == 2) {
							item.setOuvragecptFonctionnement(value);
						}
						else if (i == 3) {
							item.setOuvragecptSituation(value);
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.OUVRAGE_CPT, fileName, item.getOuvragecptOuvragesocleId(), e);
					}
				}

				if (!isEmpty) {
					items.addOuvrageCpts(item);
				}
			}
		}

		return items;
	}

	private static OuvragePres buildOuvragePres(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.OUVRAGE_PRE);

		OuvragePres items = new OuvragePres();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				OuvragePre item = new OuvragePre();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}

					try {
						if (i == 0) {
							item.setOuvragepreOuvragesocleId(value);
						}
						else if (i == 1) {
							item.setOuvragepreDescriptif(value);
						}
						else if (i == 2) {
							item.setOuvragepreNomcourseau(value);
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.OUVRAGE_PRE, fileName, item.getOuvragepreOuvragesocleId(), e);
					}
				}

				if (!isEmpty) {
					items.addOuvragePres(item);
				}
			}
		}

		return items;
	}

	private static UniteCars buildUniteCars(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.UNITE_CAR);

		UniteCars items = new UniteCars();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				UniteCar item = new UniteCar();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}

					try {
						if (i == 0) {
							item.setUnitecarUnitesocleId(value);
						}
						else if (i == 1) {
							item.setUnitecarProcede(value);
						}
						else if (i == 2) {
							item.setUnitecarMateriaufiltrant(value);
						}
						else if (i == 3) {
							item.setUnitecarGranulo(value);
						}
						else if (i == 4) {
							item.setUnitecarNbfiltre(getDoubleValue("unitecar_nbfiltre", value));
						}
						else if (i == 5) {
							item.setUnitecarSurfacefiltre(getDoubleValue("unitecar_surfacefiltre", value));
						}
						else if (i == 6) {
							item.setUnitecarDimensionfiltre(getDoubleValue("unitecar_dimensionfiltre", value));
						}
						else if (i == 7) {
							item.setUnitecarDebitfiltre(getDoubleValue("unitecar_debitfiltre", value));
						}
						else if (i == 8) {
							item.setUnitecarVolumefiltre(getDoubleValue("unitecar_volumefiltre", value));
						}
						else if (i == 9) {
							item.setUnitecarVitessefiltration(getDoubleValue("unitecar_vitessefiltration", value));
						}
						else if (i == 10) {
							item.setUnitecarBicouche(value);
						}
						else if (i == 11) {
							item.setUnitecarHauteursable(getDoubleValue("unitecar_hauteursable", value));
						}
						else if (i == 12) {
							item.setUnitecarHauteurmatcatalytique(getDoubleValue("unitecar_hauteurmatcatalytique", value));
						}
						else if (i == 13) {
							item.setUnitecarTypematcatalytique(value);
						}
						else if (i == 14) {
							item.setUnitecarNbpompelavageeau(getDoubleValue("unitecar_nbpompelavageeau", value));
						}
						else if (i == 15) {
							item.setUnitecarDebitpompelavageeau(getDoubleValue("unitecar_debitpompelavageeau", value));
						}
						else if (i == 16) {
							item.setUnitecarSecourspompelavageeau(value);
						}
						else if (i == 17) {
							item.setUnitecarNbsurpresseurlavageair(getDoubleValue("unitecar_nbsurpresseurlavageair", value));
						}
						else if (i == 18) {
							item.setUnitecarDebitsurpresseurlavageair(getDoubleValue("unitecar_debitsurpresseurlavageair", value));
						}
						else if (i == 19) {
							item.setUnitecarSecourssurpresseurlavageair(value);
						}
						else if (i == 20) {
							item.setUnitecarNbcrepine(getDoubleValue("unitecar_nbcrepine", value));
						}
						else if (i == 21) {
							item.setUnitecarTypecrepine(value);
						}
						else if (i == 22) {
							item.setUnitecarReserveeaufiltree(value);
						}
						else if (i == 23) {
							item.setUnitecarTpssejour(getDoubleValue("unitecar_tpssejour", value));
						}
						else if (i == 24) {
							item.setUnitecarNbcompartiment(getDoubleValue("unitecar_nbcompartiment", value));
						}
						else if (i == 25) {
							item.setUnitecarTypemateriau(value);
						}
						else if (i == 26) {
							item.setUnitecarSurfacemodule(getDoubleValue("unitecar_surfacemodule", value));
						}
						else if (i == 27) {
							item.setUnitecarTypeextraction(value);
						}
						else if (i == 28) {
							item.setUnitecarContactcap(value);
						}
						else if (i == 29) {
							item.setUnitecarContactcapprocede(value);
						}
						else if (i == 30) {
							item.setUnitecarContactcapdescripouv(value);
						}
						else if (i == 31) {
							item.setUnitecarContactcapassodecan(value);
						}
						else if (i == 32) {
							item.setUnitecarDecandescrip(value);
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.UNITE_CAR, fileName, item.getUnitecarUnitesocleId(), e);
					}
				}

				if (!isEmpty) {
					items.addUniteCars(item);
				}
			}
		}

		return items;
	}

	private static UniteChls buildUniteChls(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.UNITE_CHL);

		UniteChls items = new UniteChls();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				UniteChl item = new UniteChl();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}

					try {
						if (i == 0) {
							item.setUnitechlUnitesocleId(value);
						}
						else if (i == 1) {
							item.setUnitechlTypedesinfectantstocke(value);
						}
						else if (i == 2) {
							item.setUnitechlFonction(value);
						}
						else if (i == 3) {
							item.setUnitechlPositionptinjection(value);
						}
						else if (i == 4) {
							item.setUnitechlModeregulation(value);
						}
						else if (i == 5) {
							item.setUnitechlPeriodefonctionnement(value);
						}
						else if (i == 6) {
							item.setUnitechlTelegestconcentrationamont(value);
						}
						else if (i == 7) {
							item.setUnitechlTelegestconcentrationaval(value);
						}
						else if (i == 8) {
							item.setUnitechlTypecuve(value);
						}
						else if (i == 9) {
							item.setUnitechlNbpompedoseuse(getDoubleValue("unitechl_nbpompedoseuse", value));
						}
						else if (i == 10) {
							item.setUnitechlVolumecuve(getDoubleValue("unitechl_volumecuve", value));
						}
						else if (i == 11) {
							item.setUnitechlVolumeretention(getDoubleValue("unitechl_volumeretention", value));
						}
						else if (i == 12) {
							item.setUnitechlAiredepotage(value);
						}
						else if (i == 13) {
							item.setUnitechlLocal(value);
						}
						else if (i == 14) {
							item.setUnitechlConformitereglementaire(value);
						}
						else if (i == 15) {
							item.setUnitechlNbbouteille(getDoubleValue("unitechl_nbbouteille", value));
						}
						else if (i == 16) {
							item.setUnitechlPoidsbouteille(getDoubleValue("unitechl_poidsbouteille", value));
						}
						else if (i == 17) {
							item.setUnitechlTypeinversion(value);
						}
						else if (i == 18) {
							item.setUnitechlPlandetaille(value);
						}
						else if (i == 19) {
							item.setUnitechlCommentaire1(value);
						}
						else if (i == 20) {
							item.setUnitechlCommentaire2(value);
						}
						else if (i == 21) {
							item.setUnitechlPresencemasque(value);
						}
						else if (i == 22) {
							item.setUnitechlPositionbouteille(value);
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.UNITE_CHL, fileName, item.getUnitechlUnitesocleId(), e);
					}
				}

				if (!isEmpty) {
					items.addUniteChls(item);
				}
			}
		}

		return items;
	}

	private static UniteClas buildUniteClas(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.UNITE_CLA);

		UniteClas items = new UniteClas();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				UniteCla item = new UniteCla();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}

					try {
						if (i == 0) {
							item.setUniteclaUnitesocleId(value);
						}
						else if (i == 1) {
							item.setUniteclaCoaginjectionreactif(value);
						}
						else if (i == 2) {
							item.setUniteclaCoagreglageph(value);
						}
						else if (i == 3) {
							item.setUniteclaFlocinjectionreactif(value);
						}
						else if (i == 4) {
							item.setUniteclaCoagflocnbagitateur(getDoubleValue("unitecla_coagflocnbagitateur", value));
						}
						else if (i == 5) {
							item.setUniteclaCoagflocnbagitateurfile(getDoubleValue("unitecla_coagflocnbagitateurfile", value));
						}
						else if (i == 6) {
							item.setUniteclaFlotdebitpressurisation(getDoubleValue("unitecla_flotdebitpressurisation", value));
						}
						else if (i == 7) {
							item.setUniteclaFlotfiltrationeaupress(value);
						}
						else if (i == 8) {
							item.setUniteclaFlotdebitair(getDoubleValue("unitecla_flotdebitair", value));
						}
						else if (i == 9) {
							item.setUniteclaFlotvitesse(getDoubleValue("unitecla_flotvitesse", value));
						}
						else if (i == 10) {
							item.setUniteclaFlottempssejour(getDoubleValue("unitecla_flottempssejour", value));
						}
						else if (i == 11) {
							item.setUniteclaTypedecantation(value);
						}
						else if (i == 12) {
							item.setUniteclaVitessemiroir(value);
						}
						else if (i == 13) {
							item.setUniteclaVitessehazen(value);
						}
						else if (i == 14) {
							item.setUniteclaReserveeaupress(value);
						}
						else if (i == 15) {
							item.setUniteclaBallonpression(value);
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.UNITE_CLA, fileName, item.getUniteclaUnitesocleId(), e);
					}
				}

				if (!isEmpty) {
					items.addUniteClas(item);
				}
			}
		}

		return items;
	}

	private static UniteCpts buildUniteCpts(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.UNITE_CPT);

		UniteCpts items = new UniteCpts();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				UniteCpt item = new UniteCpt();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}

					try {
						if (i == 0) {
							item.setUnitecptUnitesocleId(value);
						}
						else if (i == 1) {
							item.setUnitecptTelegestiondebit(value);
						}
						else if (i == 2) {
							item.setUnitecptTelegestionpressionamont(value);
						}
						else if (i == 3) {
							item.setUnitecptTelegestionpressionaval(value);
						}
						else if (i == 4) {
							item.setUnitecptOrganeassocie(value);
						}
						else if (i == 5) {
							item.setUnitecptNomsecteurcompte(value);
						}
						else if (i == 6) {
							item.setUnitecptCommentaire1(value);
						}
						else if (i == 7) {
							item.setUnitecptCommentaire2(value);
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.UNITE_CPT, fileName, item.getUnitecptUnitesocleId(), e);
					}
				}

				if (!isEmpty) {
					items.addUniteCpts(item);
				}
			}
		}

		return items;
	}

	private static UniteEles buildUniteEles(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.UNITE_ELE);

		UniteEles items = new UniteEles();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				UniteEle item = new UniteEle();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}

					try {
						if (i == 0) {
							item.setUniteeleUnitesocleId(value);
						}
						else if (i == 1) {
							item.setUniteeleTypecontrat(value);
						}
						else if (i == 2) {
							item.setUniteeleLocalisationtransfo(value);
						}
						else if (i == 3) {
							item.setUniteeleLocalisationtransfosec(value);
						}
						else if (i == 4) {
							item.setUniteelePuissancesouscrite(getDoubleValue("uniteele_puissancesouscrite", value));
						}
						else if (i == 5) {
							item.setUniteeleNumcontrat(value);
						}
						else if (i == 6) {
							item.setUniteelePostesource1(value);
						}
						else if (i == 7) {
							item.setUniteelePostesource2(value);
						}
						else if (i == 8) {
							item.setUniteelePuisouscritep(getDoubleValue("uniteele_puisouscritep", value));
						}
						else if (i == 9) {
							item.setUniteelePuisouscritehph(getDoubleValue("uniteele_puisouscritehph", value));
						}
						else if (i == 10) {
							item.setUniteelePuisouscritehch(getDoubleValue("uniteele_puisouscritehch", value));
						}
						else if (i == 11) {
							item.setUniteelePuisouscritehpdemisaison(getDoubleValue("uniteele_puisouscritehpdemisaison", value));
						}
						else if (i == 12) {
							item.setUniteelePuisouscritehcdemisaison(getDoubleValue("uniteele_puisouscritehcdemisaison", value));
						}
						else if (i == 13) {
							item.setUniteelePuisouscritehpete(getDoubleValue("uniteele_puisouscritehpete", value));
						}
						else if (i == 14) {
							item.setUniteelePuisouscritehcete(getDoubleValue("uniteele_puisouscritehcete", value));
						}
						else if (i == 15) {
							item.setUniteelePuisouscriteja(getDoubleValue("uniteele_puisouscriteja", value));
						}
						else if (i == 16) {
							item.setUniteeleLocalisationarmoirecommande(value);
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.UNITE_ELE, fileName, item.getUniteeleUnitesocleId(), e);
					}
				}

				if (!isEmpty) {
					items.addUniteEles(item);
				}
			}
		}

		return items;
	}

	private static UniteEsvs buildUniteEsvs(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.UNITE_ESV);

		UniteEsvs items = new UniteEsvs();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				UniteEsv item = new UniteEsv();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}

					try {
						if (i == 0) {
							item.setUniteesvUnitesocleId(value);
						}
						else if (i == 1) {
							item.setUniteesvResponsabiliteentretien(value);
						}
						else if (i == 2) {
							item.setUniteesvFrequenceentretien(value);
						}
						else if (i == 3) {
							item.setUniteesvDispositionsspecifiques1(value);
						}
						else if (i == 4) {
							item.setUniteesvDispositionsspecifiques2(value);
						}
						else if (i == 5) {
							item.setUniteesvCommentaires1(value);
						}
						else if (i == 6) {
							item.setUniteesvCommentaires2(value);
						}
						else if (i == 7) {
							item.setUniteesvSuperficie(getDoubleValue("uniteesv_superficie", value));
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.UNITE_ESV, fileName, item.getUniteesvUnitesocleId(), e);
					}
				}

				if (!isEmpty) {
					items.addUniteEsvs(item);
				}
			}
		}

		return items;
	}

	private static UniteFils buildUniteFils(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.UNITE_FIL);

		UniteFils items = new UniteFils();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				UniteFil item = new UniteFil();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}

					try {
						if (i == 0) {
							item.setUnitefilUnitesocleId(value);
						}
						else if (i == 1) {
							item.setUnitefilMateriaufiltrant(value);
						}
						else if (i == 2) {
							item.setUnitefilGranulo(value);
						}
						else if (i == 3) {
							item.setUnitefilNbfiltre(getDoubleValue("unitefil_nbfiltre", value));
						}
						else if (i == 4) {
							item.setUnitefilSurfacefiltre(getDoubleValue("unitefil_surfacefiltre", value));
						}
						else if (i == 5) {
							item.setUnitefilDimensionfiltre(value);
						}
						else if (i == 6) {
							item.setUnitefilDebitfiltre(getDoubleValue("unitefil_debitfiltre", value));
						}
						else if (i == 7) {
							item.setUnitefilVolumefiltre(getDoubleValue("unitefil_volumefiltre", value));
						}
						else if (i == 8) {
							item.setUnitefilVitessefiltration(getDoubleValue("unitefil_vitessefiltration", value));
						}
						else if (i == 9) {
							item.setUnitefilBicouche(value);
						}
						else if (i == 10) {
							item.setUnitefilHauteursable(getDoubleValue("unitefil_hauteursable", value));
						}
						else if (i == 11) {
							item.setUnitefilHauteurmatcatalytique(getDoubleValue("unitefil_hauteurmatcatalytique", value));
						}
						else if (i == 12) {
							item.setUnitefilNbpompelavageeau(getDoubleValue("unitefil_nbpompelavageeau", value));
						}
						else if (i == 13) {
							item.setUnitefilDebitpompelavageeau(getDoubleValue("unitefil_debitpompelavageeau", value));
						}
						else if (i == 14) {
							item.setUnitefilSecourspompelavageeau(value);
						}
						else if (i == 15) {
							item.setUnitefilNnbsurpresseurlavageair(getDoubleValue("unitefil_nbsurpresseurlavageair", value));
						}
						else if (i == 16) {
							item.setUnitefilDebitsurpresseurlavageair(getDoubleValue("unitefil_debitsurpresseurlavageair", value));
						}
						else if (i == 17) {
							item.setUnitefilSecourssurpresseurlavageair(value);
						}
						else if (i == 18) {
							item.setUnitefilNbcrepine(getDoubleValue("unitefil_nbcrepine", value));
						}
						else if (i == 19) {
							item.setUnitefil_typecrepine(value);
						}
						else if (i == 20) {
							item.setUnitefilReserveeaufiltree(value);
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.UNITE_FIL, fileName, item.getUnitefilUnitesocleId(), e);
					}
				}

				if (!isEmpty) {
					items.addUniteFils(item);
				}
			}
		}

		return items;
	}

	private static UniteGcis buildUniteGcis(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.UNITE_GCI);

		UniteGcis items = new UniteGcis();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				UniteGci item = new UniteGci();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}

					try {
						if (i == 0) {
							item.setUnitegciUnitesocleId(value);
						}
						else if (i == 1) {
							item.setUnitegciAppreciationetat(value);
						}
						else if (i == 2) {
							item.setUnitegciEtancheite(value);
						}
						else if (i == 3) {
							item.setUnitegciVolume(getDoubleValue("unitegci_volume", value));
						}
						else if (i == 4) {
							item.setUnitegciHauteur(getDoubleValue("unitegci_hauteur", value));
						}
						else if (i == 5) {
							item.setUnitegciLongueur(getDoubleValue("unitegci_longueur", value));
						}
						else if (i == 6) {
							item.setUnitegciLargeur(getDoubleValue("unitegci_largeur", value));
						}
						else if (i == 7) {
							item.setUnitegciSurface(getDoubleValue("unitegci_surface", value));
						}
						else if (i == 8) {
							item.setUnitegciMateriauconstruction(value);
						}
						else if (i == 9) {
							item.setUnitegciMateriauisolationthermique(value);
						}
						else if (i == 10) {
							item.setUnitegciMateriauisolationphonique(value);
						}
						else if (i == 11) {
							item.setUnitegciMateriauetancheite(value);
						}
						else if (i == 12) {
							item.setUnitegciRezchaussee(value);
						}
						else if (i == 13) {
							item.setUnitegciNiveau1(value);
						}
						else if (i == 14) {
							item.setUnitegciNiveau2(value);
						}
						else if (i == 15) {
							item.setUnitegciSoussol(value);
						}
						else if (i == 16) {
							item.setUnitegciTypetoiture(value);
						}
						else if (i == 17) {
							item.setUnitegciVidecave(value);
						}
						else if (i == 18) {
							item.setUnitegciDiametreint(getDoubleValue("unitegci_diametreint", value));
						}
						else if (i == 19) {
							item.setUnitegciHauteureau(getDoubleValue("unitegci_hauteureau", value));
						}
						else if (i == 20) {
							item.setUnitegciConcentrationmax(getDoubleValue("unitegci_concentrationmax", value));
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.UNITE_GCI, fileName, item.getUnitegciUnitesocleId(), e);
					}
				}

				if (!isEmpty) {
					items.addUniteGcis(item);
				}
			}
		}

		return items;
	}

	private static UniteHyds buildUniteHyds(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.UNITE_HYD);

		UniteHyds items = new UniteHyds();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				UniteHyd item = new UniteHyd();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}

					try {
						if (i == 0) {
							item.setUnitehydUnitesocleId(value);
						}
						else if (i == 1) {
							item.setUnitehydDescriptif(value);
						}
						else if (i == 2) {
							item.setUnitehydEtat(value);
						}
						else if (i == 3) {
							item.setUnitehydDate(getDateValue("unitehyd_date", value, FORMAT_TYPE_1));
						}
						else if (i == 4) {
							item.setUnitehydCommentaire(value);
						}
						else if (i == 5) {
							item.setUnitehydEntretienparticulier(value);
						}
						else if (i == 6) {
							item.setUnitehydConformitesecurite(value);
						}
						else if (i == 7) {
							item.setUnitehydDescriptif2(value);
						}
						else if (i == 8) {
							item.setUnitehydEtat2(value);
						}
						else if (i == 9) {
							item.setUnitehydDate2(getDateValue("unitehyd_date2", value, FORMAT_TYPE_1));
						}
						else if (i == 10) {
							item.setUnitehydCommentaire2(value);
						}
						else if (i == 11) {
							item.setUnitehydEntretienparticulier2(value);
						}
						else if (i == 12) {
							item.setUnitehydConformitesecurite2(value);
						}
						else if (i == 13) {
							item.setUnitehydDescriptif3(value);
						}
						else if (i == 14) {
							item.setUnitehydEtat3(value);
						}
						else if (i == 15) {
							item.setUnitehydDate3(getDateValue("unitehyd_date3", value, FORMAT_TYPE_1));
						}
						else if (i == 16) {
							item.setUnitehydCommentaire3(value);
						}
						else if (i == 17) {
							item.setUnitehydEntretienparticulier3(value);
						}
						else if (i == 18) {
							item.setUnitehydConformitesecurite3(value);
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.UNITE_HYD, fileName, item.getUnitehydUnitesocleId(), e);
					}
				}

				if (!isEmpty) {
					items.addUniteHyds(item);
				}
			}
		}

		return items;
	}

	private static UniteMins buildUniteMins(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.UNITE_MIN);

		UniteMins items = new UniteMins();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				UniteMin item = new UniteMin();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}
					
					try {
						if (i == 0) {
							item.setUniteminUnitesocleId(value);
						}
						else if (i == 1) {
							item.setUniteminNeuralisation(value);
						}
						else if (i == 2) {
							item.setUniteminRemineralisation(value);
						}
						else if (i == 3) {
							item.setUniteminDecarbonatation(value);
						}
						else if (i == 4) {
							item.setUniteminPreremin(value);
						}
						else if (i == 5) {
							item.setUniteminPreremincommentaire(value);
						}
						else if (i == 6) {
							item.setUniteminPrereminreactif(value);
						}
						else if (i == 7) {
							item.setUniteminInterremin(value);
						}
						else if (i == 8) {
							item.setUniteminInterremincommentaire(value);
						}
						else if (i == 9) {
							item.setUniteminInterreminreactif(value);
						}
						else if (i == 10) {
							item.setUniteminPostremin(value);
						}
						else if (i == 11) {
							item.setUniteminPostremincomentaire(value);
						}
						else if (i == 12) {
							item.setUniteminPostreminreactif(value);
						}
						else if (i == 13) {
							item.setUniteminNeutralisationreactif(value);
						}
						else if (i == 14) {
							item.setUniteminTypedecarbonatation(value);
						}
						else if (i == 15) {
							item.setUniteminDecarbonatationreactif(value);
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.UNITE_MIN, fileName, item.getUniteminUnitesocleId(), e);
					}
				}

				if (!isEmpty) {
					items.addUniteMins(item);
				}
			}
		}

		return items;
	}

	private static UniteOxys buildUniteOxys(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.UNITE_OXY);

		UniteOxys items = new UniteOxys();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				UniteOxy item = new UniteOxy();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}

					try {
						if (i == 0) {
							item.setUniteoxyUnitesocle_id(value);
						}
						else if (i == 1) {
							item.setUniteoxyPrezonation(value);
						}
						else if (i == 2) {
							item.setUniteoxyPrezonationtpscontact(value);
						}
						else if (i == 3) {
							item.setUniteoxyPrezonationnbporeux(getDoubleValue("uniteoxy_prezonationnbporeux", value));
						}
						else if (i == 4) {
							item.setUniteoxyPrezonationcompart(value);
						}
						else if (i == 5) {
							item.setUniteoxyInterzonation(value);
						}
						else if (i == 6) {
							item.setUniteoxyInterzonationnbporeux(getDoubleValue("uniteoxy_interzonationnbporeux", value));
						}
						else if (i == 7) {
							item.setUniteoxyInterzonationtpscontact(getDoubleValue("uniteoxy_interzonationtpscontact", value));
						}
						else if (i == 8) {
							item.setUniteoxyPostzonation(value);
						}
						else if (i == 9) {
							item.setUniteoxyPostzonationnbporeux(getDoubleValue("uniteoxy_postzonationnbporeux", value));
						}
						else if (i == 10) {
							item.setUniteoxyPostzonationtpscontact(getDoubleValue("uniteoxy_postzonationtpscontact", value));
						}
						else if (i == 11) {
							item.setUniteoxyOzoneur(value);
						}
						else if (i == 12) {
							item.setUniteoxyOzoneurcapaciteunit(value);
						}
						else if (i == 13) {
							item.setUniteoxyOzoneurnb(getDoubleValue("uniteoxy_ozoneurnb", value));
						}
						else if (i == 14) {
							item.setUniteoxyOzoneursecours(value);
						}
						else if (i == 15) {
							item.setUniteoxyDestructeurozoneur(value);
						}
						else if (i == 16) {
							item.setUniteoxyDestructeurozonecapacunit(value);
						}
						else if (i == 17) {
							item.setUniteoxyDestructeurozonetype(value);
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.UNITE_OXY, fileName, item.getUniteoxyUnitesocle_id(), e);
					}
				}

				if (!isEmpty) {
					items.addUniteOxys(item);
				}
			}
		}

		return items;
	}

	private static UnitePoms buildUnitePoms(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.UNITE_POM);

		UnitePoms items = new UnitePoms();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				UnitePom item = new UnitePom();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}

					try {
						if (i == 0) {
							item.setUnitepomUnitesocleId(value);
						}
						else if (i == 1) {
							item.setUnitepomNbpompes(getDoubleValue("unitepom_nbpompes", value));
						}
						else if (i == 2) {
							item.setUnitepomNbpmpesmaxfonctionnement(getDoubleValue("unitepom_nbpmpesmaxfonctionnement", value));
						}
						else if (i == 3) {
							item.setUnitepomTyperegul(value);
						}
						else if (i == 4) {
							item.setUnitepomOrganecontrole(value);
						}
						else if (i == 5) {
							item.setUnitepomDebitotal(value);
						}
						else if (i == 6) {
							item.setUnitepomHmt(getDoubleValue("unitepom_hmt", value));
						}
						else if (i == 7) {
							item.setUnitepomDebitmin(getDoubleValue("unitepom_debitmin", value));
						}
						else if (i == 8) {
							item.setUnitepomHmtmin(getDoubleValue("unitepom_hmtmin", value));
						}
						else if (i == 9) {
							item.setUnitepomDebitmax(getDoubleValue("unitepom_debitmax", value));
						}
						else if (i == 10) {
							item.setUnitepomHmtmax(getDoubleValue("unitepom_hmtmax", value));
						}
						else if (i == 11) {
							item.setUnitepomRdt(getDoubleValue("unitepom_rdt", value));
						}
						else if (i == 12) {
							item.setUnitepomCourbefonctionnementpompage(value);
						}
						else if (i == 13) {
							item.setUnitepomCotesol(getDoubleValue("unitepom_cotesol", value));
						}
						else if (i == 14) {
							item.setUnitepomSynoptiquehydraulique(value);
						}
						else if (i == 15) {
							item.setUnitepomPhoto(value);
						}
						else if (i == 16) {
							item.setUnitepomPeriodefonctionnement(value);
						}
						else if (i == 17) {
							item.setUnitepomGrpeelectrogene(value);
						}
						else if (i == 18) {
							item.setUnitepomEdfpuissancesouscrite(getIntegerValue("unitepom_edfpuissancesouscrite", value));
						}
						else if (i == 19) {
							item.setUnitepomPuissanceabsorbeetransformateur(getIntegerValue("unitepom_puissanceabsorbeetransformateur", value));
						}
						else if (i == 20) {
							item.setUnitepomTypecontrat(value);
						}
						else if (i == 21) {
							item.setUnitepomPlateforme(value);
						}
						else if (i == 22) {
							item.setUnitepomPuissance(getIntegerValue("unitepom_puissance", value));
						}
						else if (i == 23) {
							item.setUnitepomAntibelier(value);
						}
						else if (i == 24) {
							item.setUnitepomNbantibelier(getDoubleValue("unitepom_nbantibelier", value));
						}
						else if (i == 25) {
							item.setUnitepomUsageantibelier(value);
						}
						else if (i == 26) {
							item.setUnitepomCommentaire1(value);
						}
						else if (i == 27) {
							item.setUnitepomCommentaire2(value);
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.UNITE_POM, fileName, item.getUnitepomUnitesocleId(), e);
					}
				}

				if (!isEmpty) {
					items.addUnitePoms(item);
				}
			}
		}

		return items;
	}

	private static UniteRecs buildUniteRecs(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.UNITE_REC);

		UniteRecs items = new UniteRecs();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				UniteRec item = new UniteRec();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}

					try {
						if (i == 0) {
							item.setUniterecUnitesocleId(value);
						}
						else if (i == 1) {
							item.setUniterecPresrecirculation1(value);
						}
						else if (i == 2) {
							item.setUniterecEtaperecirculation1(value);
						}
						else if (i == 3) {
							item.setUniterecRecirculationreactif1(value);
						}
						else if (i == 4) {
							item.setUniterecRecirculationdebit1(getDoubleValue("uniterec_recirculationdebit1", value));
						}
						else if (i == 5) {
							item.setUniterecRecirculationpompage1(value);
						}
						else if (i == 6) {
							item.setUniterecPresrecirculation2(value);
						}
						else if (i == 7) {
							item.setUniterecEtaperecirculation2(value);
						}
						else if (i == 8) {
							item.setUniterecRecirculationreactif2(value);
						}
						else if (i == 9) {
							item.setUniterecRecirculationdebit2(getDoubleValue("uniterec_recirculationdebit2", value));
						}
						else if (i == 10) {
							item.setUniterecRecirculationpompage2(value);
						}
						else if (i == 11) {
							item.setUniterecPresrecirculation3(value);
						}
						else if (i == 12) {
							item.setUniterecEtaperecirculation3(value);
						}
						else if (i == 13) {
							item.setUniterecRecirculationreactif3(value);
						}
						else if (i == 14) {
							item.setUniterecRecirculationdebit3(getDoubleValue("uniterec_recirculationdebit3", value));
						}
						else if (i == 15) {
							item.setUniterecRecirculationpompage3(value);
						}
						else if (i == 16) {
							item.setUniterecPresrecirculation4(value);
						}
						else if (i == 17) {
							item.setUniterecEtaperecirculation4(value);
						}
						else if (i == 18) {
							item.setUniterecRecirculationreactif4(value);
						}
						else if (i == 19) {
							item.setUniterecRecirculationdebit4(getDoubleValue("uniterec_recirculationdebit4", value));
						}
						else if (i == 20) {
							item.setUniterecRecirculationpompage4(value);
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.UNITE_REC, fileName, item.getUniterecUnitesocleId(), e);
					}
				}

				if (!isEmpty) {
					items.addUniteRecs(item);
				}
			}
		}

		return items;
	}

	private static UniteRegs buildUniteRegs(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.UNITE_REG);

		UniteRegs items = new UniteRegs();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				UniteReg item = new UniteReg();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}

					try {
						if (i == 0) {
							item.setUniteregUnitesocleId(value);
						}
						else if (i == 1) {
							item.setUniteregType(value);
						}
						else if (i == 2) {
							item.setUniteregTelegestiondebit(value);
						}
						else if (i == 3) {
							item.setUniteregTelegestionpressionamont(value);
						}
						else if (i == 4) {
							item.setUniteregTelegestionpressionaval(value);
						}
						else if (i == 5) {
							item.setUniteregOrganeassocie(value);
						}
						else if (i == 6) {
							item.setUniteregNomsecteurregule(value);
						}
						else if (i == 7) {
							item.setUniteregPlanassocie(value);
						}
						else if (i == 8) {
							item.setUniteregCommentaire1(value);
						}
						else if (i == 9) {
							item.setUniteregCommentaire2(value);
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.UNITE_REG, fileName, item.getUniteregUnitesocleId(), e);
					}
				}

				if (!isEmpty) {
					items.addUniteRegs(item);
				}
			}
		}

		return items;
	}

	private static UniteSecs buildUniteSecs(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.UNITE_SEC);

		UniteSecs items = new UniteSecs();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				UniteSec item = new UniteSec();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}

					try {
						if (i == 0) {
							item.setUnitesecUnitesocleId(value);
						}
						else if (i == 1) {
							item.setUnitesecCommentaire1(value);
						}
						else if (i == 2) {
							item.setUnitesecCommentaire2(value);
						}
						else if (i == 3) {
							item.setUnitesecConformitecloture(value);
						}
						else if (i == 4) {
							item.setUnitesecDateconformitecloture(getDateValue("unitesec_dateconformitecloture", value, FORMAT_TYPE_1));
						}
						else if (i == 5) {
							item.setUnitesecConformiteportail(value);
						}
						else if (i == 6) {
							item.setUnitesecDateconformiteportail(getDateValue("unitesec_dateconformiteportail", value, FORMAT_TYPE_1));
						}
						else if (i == 7) {
							item.setUnitesecControleacces(value);
						}
						else if (i == 8) {
							item.setUnitesecAntiintrusionrdc(value);
						}
						else if (i == 9) {
							item.setUnitesecAntiintrusionrdctype(value);
						}
						else if (i == 10) {
							item.setUnitesecAlarmedesactivation(value);
						}
						else if (i == 11) {
							item.setUnitesecLiaisonalarme(value);
						}
						else if (i == 12) {
							item.setUnitesecAntiintrusionfut(value);
						}
						else if (i == 13) {
							item.setUnitesecAntiintrusionfuttype(value);
						}
						else if (i == 14) {
							item.setUnitesecFutalarmedesactivation(value);
						}
						else if (i == 15) {
							item.setUnitesecFutliaisonalarme(value);
						}
						else if (i == 16) {
							item.setUnitesecAntiintrusionsouscuve(value);
						}
						else if (i == 17) {
							item.setUnitesecAntiintrusionsouscuvetype(value);
						}
						else if (i == 18) {
							item.setUnitesecSouscuvealarmedesactivation(value);
						}
						else if (i == 19) {
							item.setUnitesecSouscuveliaisonalarme(value);
						}
						else if (i == 20) {
							item.setUnitesecAntiintrusiontype(value);
						}
						else if (i == 21) {
							item.setUnitesecAccestiersmodeop(value);
						}
						else if (i == 22) {
							item.setUnitesecAntiintrusionechelleext(value);
						}
						else if (i == 23) {
							item.setUnitesecAntiintrusioncuve1(value);
						}
						else if (i == 24) {
							item.setUnitesecSurvvideo(value);
						}
						else if (i == 25) {
							item.setUnitesecNbcamera(getDoubleValue("unitesec_nbcamera", value));
						}
						else if (i == 26) {
							item.setUnitesecPositioncamera(value);
						}
						else if (i == 27) {
							item.setUnitesecCentraleacquisition(value);
						}
						else if (i == 28) {
							item.setUnitesecEnregistrement(value);
						}
						else if (i == 29) {
							item.setUnitesecRapportsecurite(value);
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.UNITE_SEC, fileName, item.getUnitesecUnitesocleId(), e);
					}
				}

				if (!isEmpty) {
					items.addUniteSecs(item);
				}
			}
		}

		return items;
	}

	private static UniteSets buildUniteSets(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.UNITE_SET);

		UniteSets items = new UniteSets();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				UniteSet item = new UniteSet();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}

					try {
						if (i == 0) {
							item.setUnitesetUnitesocleId(value);
						}
						else if (i == 1) {
							item.setUnitesetModesechage(value);
						}
						else if (i == 2) {
							item.setUnitesetDescriptif(value);
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.UNITE_SET, fileName, item.getUnitesetUnitesocleId(), e);
					}
				}

				if (!isEmpty) {
					items.addUniteSets(item);
				}
			}
		}

		return items;
	}

	private static UniteSocles buildUniteSocles(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.UNITE_SOCLE);

		UniteSocles items = new UniteSocles();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				UniteSocle item = new UniteSocle();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}

					try {
						if (i == 0) {
							item.setUnitesocleVeId(value);
						}
						else if (i == 1) {
							item.setUnitesocleOuvragesocleId(value);
						}
						else if (i == 2) {
							item.setUnitesocleDenomination(value);
						}
						else if (i == 3) {
							item.setUnitesocleType(value);
						}
						else if (i == 4) {
							item.setUnitesocleCaracteristique(value);
						}
						else if (i == 5) {
							item.setUnitesocleConsignes(value);
						}
						else if (i == 6) {
							item.setUnitesocleCommentaire(value);
						}
						else if (i == 7) {
							item.setUnitesocleStatut(value);
						}
						else if (i == 8) {
							item.setUnitesocleMiseenservice(getDateValue("unitesocle_miseenservice", value, FORMAT_TYPE_1));
						}
						else if (i == 9) {
							item.setUnitesocleDatefermeture(getDateValue("unitesocle_datefermeture", value, FORMAT_TYPE_1));
						}
						else if (i == 10) {
							item.setUnitesocleEtat(value);
						}
						else if (i == 11) {
							item.setUnitesocleDaterehabilitation1(getDateValue("unitesocle_daterehabilitation1", value, FORMAT_TYPE_1));
						}
						else if (i == 12) {
							item.setUnitesocleContenurehabilitation1(value);
						}
						else if (i == 13) {
							item.setUnitesocleDaterehabilitation2(getDateValue("unitesocle_daterehabilitation2", value, FORMAT_TYPE_1));
						}
						else if (i == 14) {
							item.setUnitesocleContenurehabilitation2(value);
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.UNITE_SOCLE, fileName, item.getUnitesocleVeId(), e);
					}
				}

				if (!isEmpty) {
					items.addUniteSocles(item);
				}
			}
		}

		return items;
	}

	private static UniteSpes buildUniteSpes(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.UNITE_SPE);

		UniteSpes items = new UniteSpes();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				UniteSpe item = new UniteSpe();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}

					try {
						if (i == 0) {
							item.setUnitespeUnitesocleId(value);
						}
						else if (i == 1) {
							item.setUnitespeNbeqptproduction(getDoubleValue("unitespe_nbeqptproduction", value));
						}
						else if (i == 2) {
							item.setUnitespeTypeeqpt(value);
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.UNITE_SPE, fileName, item.getUnitespeUnitesocleId(), e);
					}
				}

				if (!isEmpty) {
					items.addUniteSpes(item);
				}
			}
		}

		return items;
	}

	private static UniteTlgs buildUniteTlgs(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.UNITE_TLG);

		UniteTlgs items = new UniteTlgs();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				UniteTlg item = new UniteTlg();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}

					try {
						if (i == 0) {
							item.setUnitetlgUnitesocleId(value);
						}
						else if (i == 1) {
							item.setUnitetlgModealimentation(value);
						}
						else if (i == 2) {
							item.setUnitetlgPositionnement(value);
						}
						else if (i == 3) {
							item.setUnitetlgAntennedeportee(value);
						}
						else if (i == 4) {
							item.setUnitetlgListingdonneestele(value);
						}
						else if (i == 5) {
							item.setUnitetlgTarif(value);
						}
						else if (i == 6) {
							item.setUnitetlgRefcontratabt(value);
						}
						else if (i == 7) {
							item.setUnitetlgNbpf(getDoubleValue("unitetlg_nbpf", value));
						}
						else if (i == 8) {
							item.setUnitetlgRefpf1(value);
						}
						else if (i == 9) {
							item.setUnitetlgRefpf2(value);
						}
						else if (i == 10) {
							item.setUnitetlgRefpf3(value);
						}
						else if (i == 11) {
							item.setUnitetlgRefpf4(value);
						}
						else if (i == 12) {
							item.setUnitetlgRefpf5(value);
						}
						else if (i == 13) {
							item.setUnitetlgPhotochambre(value);
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.UNITE_TLG, fileName, item.getUnitetlgUnitesocleId(), e);
					}
				}

				if (!isEmpty) {
					items.addUniteTlgs(item);
				}
			}
		}

		return items;
	}

	private static UniteTrts buildUniteTrts(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.UNITE_TRT);

		UniteTrts items = new UniteTrts();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				UniteTrt item = new UniteTrt();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}

					try {
						if (i == 0) {
							item.setUnitetrtUnitesocleId(value);
						}
						else if (i == 1) {
							item.setUnitetrtProcede(value);
						}
						else if (i == 2) {
							item.setUnitetrtCentrinb(getDoubleValue("unitetrt_centrinb", value));
						}
						else if (i == 3) {
							item.setUnitetrtPressenb(getDoubleValue("unitetrt_pressenb", value));
						}
						else if (i == 4) {
							item.setUnitetrtInjectionpolymere(value);
						}
						else if (i == 5) {
							item.setUnitetrtNompolymere(value);
						}
						else if (i == 6) {
							item.setUnitetrt_presencechaulage(value);
						}
						else if (i == 7) {
							item.setUnitetrtChaulageetape(value);
						}
						else if (i == 8) {
							item.setUnitetrtTypechaulage(value);
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.UNITE_TRT, fileName, item.getUnitetrtUnitesocleId(), e);
					}
				}

				if (!isEmpty) {
					items.addUniteTrts(item);
				}
			}
		}

		return items;
	}

	private static UniteUlts buildUniteUlts(List<XlsRuntimeLog> logsRuntime, List<LogXML> logsXml, Workbook workbook, String fileName) {
		Sheet sheet = getSheet(logsRuntime, workbook, PatrimoineDAO.UNITE_ULT);

		UniteUlts items = new UniteUlts();
		if (sheet != null) {
			for (int row = 1; row < sheet.getRows(); row++) {
				UniteUlt item = new UniteUlt();

				boolean isEmpty = true;
				for (int i = 0; i < sheet.getColumns(); i++) {
					String value = getValue(sheet, i, row);
					if (value != null && !value.isEmpty()) {
						isEmpty = false;
					}

					try {
						if (i == 0) {
							item.setUniteultUnitesocleId(value);
						}
						else if (i == 1) {
							item.setUniteultTypemembrane(value);
						}
						else if (i == 2) {
							item.setUniteultNbmodule(getDoubleValue("uniteult_nbmodule", value));
						}
						else if (i == 3) {
							item.setUniteultNbprefiltre(getDoubleValue("uniteult_nbprefiltre", value));
						}
						else if (i == 4) {
							item.setUniteultNbblocultra(getDoubleValue("uniteult_nbblocultra", value));
						}
						else if (i == 5) {
							item.setUniteultModefiltration(value);
						}
						else if (i == 6) {
							item.setUniteultFluxfiltration(getDoubleValue("uniteult_fluxfiltration", value));
						}
						else if (i == 7) {
							item.setUniteultPressiontransmembranaire(getDoubleValue("uniteult_pressiontransmembranaire", value));
						}
						else if (i == 8) {
							item.setUniteultNbpompegavage(getDoubleValue("uniteult_nbpompegavage", value));
						}
						else if (i == 9) {
							item.setUniteultTypecontroleintegrite(value);
						}
						else if (i == 10) {
							item.setUniteultSurfacemembtot(getDoubleValue("uniteult_surfacemembtot", value));
						}
						else if (i == 11) {
							item.setUniteultPrefiltre(value);
						}
						else if (i == 12) {
							item.setUniteultSeuilcoupure(value);
						}
						else if (i == 13) {
							item.setUniteultNettoyageprefiltre(value);
						}
					} catch (Exception e) {
						manageException(logsXml, PatrimoineDAO.UNITE_ULT, fileName, item.getUniteultUnitesocleId(), e);
					}
				}

				if (!isEmpty) {
					items.addUniteUlts(item);
				}
			}
		}

		return items;
	}

	private static void manageException(List<LogXML> logsXml, String tableName, String fileName, String lineId, Exception e) {
		String message = "";
		if (e.getCause() != null && e.getCause().getCause() != null && e.getCause().getCause() instanceof IllegalArgumentException) {
			IllegalArgumentException ex = (IllegalArgumentException) e.getCause().getCause();
			e = ex;

			message = e.getMessage();
		}
		else if (e.getCause() != null) {
			message = e.getCause().getMessage();
		}
		else {
			message = e.getMessage();
		}

		e.printStackTrace();
		logsXml.add(new LogXML(tableName, fileName, lineId, message, null));
	}

	private static Double getDoubleValue(String field, String value) throws Exception {
		if (value == null || value.isEmpty()) {
			return null;
		}

		try {
			value = value.replace(",", ".");
			return Double.parseDouble(value);
		} catch (Exception e) {
			throw new Exception("Conversion de '" + value + "' en valeur numerique impossible pour le champ '" + field + "'");
		}
	}

	private static Integer getIntegerValue(String field, String value) throws Exception {
		if (value == null || value.isEmpty()) {
			return null;
		}

		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			throw new Exception("Conversion de '" + value + "' en valeur numerique impossible pour le champ '" + field + "'");
		}
	}

	private static Date getDateValue(String field, String value, int typeFormat) throws Exception {
		if (value == null || value.isEmpty()) {
			return null;
		}

		try {
			if (typeFormat == FORMAT_TYPE_1) {
				return format1.parse(value);
			}
			else if (typeFormat == FORMAT_TYPE_2) {
				return format2.parse(value);
			}
			else if (typeFormat == FORMAT_TYPE_3) {
				return format3.parse(value);
			}
			else {
				return format1.parse(value);
			}
		} catch (Exception e) {
			throw new Exception("Conversion de '" + value + "' en date impossible pour le champ '" + field + "'");
		}
	}

	private static String getValue(Sheet sheet, int col, int row) {
		jxl.Cell c = sheet.getCell(col, row);
		if (c.getType() == CellType.DATE) {
			Date myDate = ((DateCell) c).getDate();
			return format1.format(myDate);
		}
		return c.getContents();

		// Object value = null;
		// if (c.getType() == CellType.BOOLEAN) {
		// value = ((BooleanCell) c).getValue();
		// }
		// else if (c.getType() == CellType.BOOLEAN_FORMULA) {
		// value = ((BooleanFormulaCell) c).getValue();
		// }
		// else if (c.getType() == CellType.DATE) {
		// value = ((DateCell) c).getDate();
		// }
		// else if (c.getType() == CellType.DATE_FORMULA) {
		// value = ((DateFormulaCell) c).getDate();
		// }
		// else if (c.getType() == CellType.EMPTY) {
		// value = null;
		// }
		// else if (c.getType() == CellType.LABEL) {
		// value = ((LabelCell) c).getString();
		// }
		// else if (c.getType() == CellType.STRING_FORMULA) {
		// value = ((StringFormulaCell) c).getString();
		// }
		// else if (c.getType() == CellType.NUMBER) {
		// value = ((NumberCell) c).getValue();
		// }
		// else if (c.getType() == CellType.NUMBER_FORMULA) {
		// value = ((NumberFormulaCell) c).getValue();
		// }
		// else {
		// value = c.getContents();
		// }
		//
		// return value;
	}
}
