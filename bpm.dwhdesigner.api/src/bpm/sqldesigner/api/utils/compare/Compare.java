package bpm.sqldesigner.api.utils.compare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import bpm.sqldesigner.api.model.Catalog;
import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.api.model.Table;
import bpm.sqldesigner.api.model.procedure.SQLProcedure;
import bpm.sqldesigner.api.model.view.SQLView;
import bpm.sqldesigner.api.utils.compare.report.CompareCatalogsReport;
import bpm.sqldesigner.api.utils.compare.report.CompareClustersReport;
import bpm.sqldesigner.api.utils.compare.report.CompareColumnsReport;
import bpm.sqldesigner.api.utils.compare.report.CompareProceduresReport;
import bpm.sqldesigner.api.utils.compare.report.CompareSchemasReport;
import bpm.sqldesigner.api.utils.compare.report.CompareTablesReport;
import bpm.sqldesigner.api.utils.compare.report.CompareViewsReport;
import bpm.sqldesigner.api.utils.compare.report.Report;
import bpm.sqldesigner.api.utils.compare.report.single.SingleCatalogReport;
import bpm.sqldesigner.api.utils.compare.report.single.SingleColumnReport;
import bpm.sqldesigner.api.utils.compare.report.single.SingleProcedureReport;
import bpm.sqldesigner.api.utils.compare.report.single.SingleSchemaReport;
import bpm.sqldesigner.api.utils.compare.report.single.SingleTableReport;
import bpm.sqldesigner.api.utils.compare.report.single.SingleViewReport;

public class Compare {

	public static Report compareNodes(Node firstNode, Node secondNode) {
		Report report = null;

		if (firstNode instanceof Catalog)
			report = compareCatalogs((Catalog) firstNode, (Catalog) secondNode);
		if (firstNode instanceof DatabaseCluster)
			report = compareClusters((DatabaseCluster) firstNode,
					(DatabaseCluster) secondNode);
		if (firstNode instanceof Column)
			report = compareColumns((Column) firstNode, (Column) secondNode);
		if (firstNode instanceof SQLProcedure)
			report = compareProcedures((SQLProcedure) firstNode,
					(SQLProcedure) secondNode);
		if (firstNode instanceof Schema)
			report = compareSchemas((Schema) firstNode, (Schema) secondNode);
		if (firstNode instanceof Table)
			report = compareTables((Table) firstNode, (Table) secondNode);
		if (firstNode instanceof SQLView)
			report = compareViews((SQLView) firstNode, (SQLView) secondNode);

		return report;
	}

	public static CompareClustersReport compareClusters(
			DatabaseCluster catalogsListA, DatabaseCluster catalogsListB) {
		CompareClustersReport report = new CompareClustersReport(catalogsListA,
				catalogsListB);

		report.setNamesMatch(true);

		TreeMap<String, Catalog> hmCatalogsA = catalogsListA.getCatalogs();
		TreeMap<String, Catalog> hmCatalogsB = catalogsListB.getCatalogs();

		Set<String> keySetA = hmCatalogsA.keySet();
		Set<String> keySetB = hmCatalogsB.keySet();

		List<String> listA = new ArrayList<String>(keySetA);
		List<String> listB = new ArrayList<String>(keySetB);

		for (String catalogNameA : keySetA) {
			Catalog catalogA = hmCatalogsA.get(catalogNameA);
			Catalog catalogB = hmCatalogsB.get(catalogNameA);

			if (catalogB != null) {
				report.addCatalogsReport(compareCatalogs(catalogA, catalogB));
				listB.remove(catalogNameA);
				listA.remove(catalogNameA);
			}
		}

		if (listB.size() != 0) {
			for (String catalogNameB : listB) {
				Catalog catalogB = hmCatalogsB.get(catalogNameB);
				SingleCatalogReport singleReport = new SingleCatalogReport(
						catalogB);
				singleReport.setOtherCluster(catalogsListA);
				report.addCatalogsReport(singleReport);
			}
		}
		if (listA.size() != 0) {
			for (String catalogNameA : listA) {
				Catalog catalogA = hmCatalogsA.get(catalogNameA);
				SingleCatalogReport singleReport = new SingleCatalogReport(
						catalogA);
				singleReport.setOtherCluster(catalogsListB);
				report.addCatalogsReport(singleReport);
			}
		}

		return report;
	}

	public static CompareCatalogsReport compareCatalogs(Catalog catalogA,
			Catalog catalogB) {
		CompareCatalogsReport report = new CompareCatalogsReport(catalogA,
				catalogB);

		report.setNamesMatch(catalogA.getName().equals(catalogB.getName()));

		TreeMap<String, Schema> hmSchemasA = catalogA.getSchemas();
		TreeMap<String, Schema> hmSchemasB = catalogB.getSchemas();

		Set<String> keySetA = hmSchemasA.keySet();
		Set<String> keySetB = hmSchemasB.keySet();

		List<String> listA = new ArrayList<String>(keySetA);
		List<String> listB = new ArrayList<String>(keySetB);

		for (String schemaNameA : keySetA) {
			Schema schemaA = hmSchemasA.get(schemaNameA);
			Schema schemaB = hmSchemasB.get(schemaNameA);

			if (schemaB != null) {
				report.addSchemasReport(compareSchemas(schemaA, schemaB));
				listB.remove(schemaNameA);
				listA.remove(schemaNameA);
			}
		}

		if (listB.size() != 0) {
			for (String schemaNameB : listB) {
				Schema schemaB = hmSchemasB.get(schemaNameB);
				SingleSchemaReport singleReport = new SingleSchemaReport(
						schemaB);
				singleReport.setOtherCatalog(catalogA);
				report.addSchemasReport(singleReport);
			}
		}
		if (listA.size() != 0) {
			for (String schemaNameA : listA) {
				Schema schemaA = hmSchemasA.get(schemaNameA);
				SingleSchemaReport singleReport = new SingleSchemaReport(
						schemaA);
				singleReport.setOtherCatalog(catalogB);
				report.addSchemasReport(singleReport);
			}
		}
		return report;

	}

	public static CompareSchemasReport compareSchemas(Schema schemaA,
			Schema schemaB) {
		CompareSchemasReport report = new CompareSchemasReport(schemaA, schemaB);

		report.setNamesMatch(schemaA.getName().equals(schemaB.getName()));

		/***********************************************************************
		 * TABLES
		 **********************************************************************/
		TreeMap<String, Table> hmTablesA = schemaA.getTables();
		TreeMap<String, Table> hmTablesB = schemaB.getTables();

		Set<String> keySetA = hmTablesA.keySet();
		Set<String> keySetB = hmTablesB.keySet();

		List<String> listA = new ArrayList<String>(keySetA);
		List<String> listB = new ArrayList<String>(keySetB);

		for (String tableNameA : keySetA) {
			Table tableA = hmTablesA.get(tableNameA);
			Table tableB = hmTablesB.get(tableNameA);

			if (tableB != null) {
				report.addTablesReport(compareTables(tableA, tableB));
				listB.remove(tableNameA);
				listA.remove(tableNameA);
			}
		}

		if (listB.size() != 0) {
			for (String tableNameB : listB) {
				Table tableB = hmTablesB.get(tableNameB);
				SingleTableReport singleReport = new SingleTableReport(tableB);
				singleReport.setOtherSchema(schemaA);
				report.addTablesReport(singleReport);
			}
		}
		if (listA.size() != 0) {
			for (String tableNameA : listA) {
				Table tableA = hmTablesA.get(tableNameA);
				SingleTableReport singleReport = new SingleTableReport(tableA);
				singleReport.setOtherSchema(schemaB);
				report.addTablesReport(singleReport);
			}
		}

		/***********************************************************************
		 * VIEWS
		 **********************************************************************/
		HashMap<String, SQLView> hmViewsA = schemaA.getViews();
		HashMap<String, SQLView> hmViewsB = schemaB.getViews();

		keySetA = hmViewsA.keySet();
		keySetB = hmViewsB.keySet();

		listA = new ArrayList<String>(keySetA);
		listB = new ArrayList<String>(keySetB);

		for (String viewNameA : keySetA) {
			SQLView viewA = hmViewsA.get(viewNameA);
			SQLView viewB = hmViewsB.get(viewNameA);

			if (viewB != null) {
				if (viewA.getValue() != null && viewB.getValue() != null)
					report.addViewsReport(compareViews(viewA, viewB));
				listB.remove(viewNameA);
				listA.remove(viewNameA);
			}
		}

		if (listB.size() != 0) {
			for (String viewNameB : listB) {
				SQLView viewB = hmViewsB.get(viewNameB);
				if (viewB.getValue() != null) {
					SingleViewReport singleReport = new SingleViewReport(viewB);
					singleReport.setOtherSchema(schemaA);
					report.addViewsReport(singleReport);
				}
			}
		}
		if (listA.size() != 0) {
			for (String tableNameA : listA) {
				SQLView viewA = hmViewsA.get(tableNameA);
				if (viewA.getValue() != null) {
					SingleViewReport singleReport = new SingleViewReport(viewA);
					singleReport.setOtherSchema(schemaB);
					report.addViewsReport(singleReport);
				}
			}
		}

		/***********************************************************************
		 * PROCEDURES
		 **********************************************************************/
		HashMap<String, SQLProcedure> hmProceduresA = schemaA.getProcedures();
		HashMap<String, SQLProcedure> hmProceduresB = schemaB.getProcedures();

		keySetA = hmProceduresA.keySet();
		keySetB = hmProceduresB.keySet();

		listA = new ArrayList<String>(keySetA);
		listB = new ArrayList<String>(keySetB);

		for (String procedureNameA : keySetA) {
			SQLProcedure procedureA = hmProceduresA.get(procedureNameA);
			SQLProcedure procedureB = hmProceduresB.get(procedureNameA);

			if (procedureB != null) {
				if (procedureB.getValue() != null
						&& procedureA.getValue() != null)
					report.addProceduresReport(compareProcedures(procedureA,
							procedureB));
				listB.remove(procedureNameA);
				listA.remove(procedureNameA);
			}
		}

		if (listB.size() != 0) {
			for (String procedureNameB : listB) {
				SQLProcedure procedureB = hmProceduresB.get(procedureNameB);
				if (procedureB.getValue() != null)
					report.addProceduresReport(new SingleProcedureReport(
							procedureB));
			}
		}
		if (listA.size() != 0) {
			for (String procedureNameA : listA) {
				SQLProcedure procedureA = hmProceduresA.get(procedureNameA);
				if (procedureA.getValue() != null)
					report.addProceduresReport(new SingleProcedureReport(
							procedureA));
			}
		}

		return report;
	}

	public static CompareProceduresReport compareProcedures(
			SQLProcedure procedureA, SQLProcedure procedureB) {
		CompareProceduresReport report = new CompareProceduresReport(
				procedureA, procedureB);

		report.setNamesMatch(procedureA.getName().equals(procedureB.getName()));

		report.setValuesMatch(procedureA.getValue().equals(
				procedureB.getValue()));

		return report;
	}

	public static CompareViewsReport compareViews(SQLView viewA, SQLView viewB) {
		CompareViewsReport report = new CompareViewsReport(viewA, viewB);

		report.setNamesMatch(viewA.getName().equals(viewB.getName()));

		report.setValuesMatch(viewA.getValue().equals(viewB.getValue()));

		return report;
	}

	public static CompareTablesReport compareTables(Table tableA, Table tableB) {
		CompareTablesReport report = new CompareTablesReport(tableA, tableB);

		report.setNamesMatch(tableA.getName().equals(tableB.getName()));

		HashMap<String, Column> hmColumnsA = tableA.getColumns();
		HashMap<String, Column> hmColumnsB = tableB.getColumns();

		Set<String> keySetA = hmColumnsA.keySet();
		Set<String> keySetB = hmColumnsB.keySet();

		List<String> listA = new ArrayList<String>(keySetA);
		List<String> listB = new ArrayList<String>(keySetB);

		for (String columnNameA : keySetA) {
			Column columnA = hmColumnsA.get(columnNameA);
			Column columnB = hmColumnsB.get(columnNameA);

			if (columnB != null) {
				report.addColumnsReport(compareColumns(columnA, columnB));
				listB.remove(columnNameA);
				listA.remove(columnNameA);
			}
		}

		// HashMap<Column, ListPair> hmListColumn = new HashMap<Column,
		// ListPair>();
		// for (String columnNameA : listA) {
		// Column columnA = hmColumnsA.get(columnNameA);
		//
		// boolean listIsNull = true;
		// for (String columnNameB : listB) {
		// Column columnB = hmColumnsB.get(columnNameB);
		// int score = compareColumns(columnA, columnB).evaluateMatches();
		//
		// if (listIsNull) {
		// hmListColumn.put(columnA, new ListPair());
		// listIsNull = false;
		// }
		// hmListColumn.get(columnA).addPair(new Pair(columnB, score));
		// }
		// }
		//
		//		
		// // In this part we try to find the columns couples which best
		// correspond one with each other
		// Iterator<Column> it = hmListColumn.keySet().iterator();
		// while (it.hasNext()) {
		// Column columnA = it.next();
		// ListPair listPair = hmListColumn.get(columnA);
		//
		// if (listPair.size() != 0) {
		//
		// Pair pair = listPair.findMaxScore();
		//
		// Pair maxPair = pair;
		// Column maxColumn = columnA;
		// ListPair maxListPair = listPair;
		//
		// for (Column columnA2 : hmListColumn.keySet()) {
		// ListPair LP = hmListColumn.get(columnA2);
		// if (LP.size() != 0) {
		//
		// Pair pairA2 = LP.getPair(pair.column.getName());
		// if (pairA2 == null) {
		// System.out.println();
		// }
		// if (pairA2.score >= maxPair.score) {
		// boolean isMax = true;
		// if (pairA2.score == maxPair.score) {
		// isMax = false;
		// int compare = pair.column.getName().compareTo(
		// pairA2.column.getName());
		// int compare2 = pair.column.getName().compareTo(
		// maxPair.column.getName());
		//
		// if (compare * compare < compare2 * compare2) {
		// isMax = true;
		// }
		// } else if (isMax) {
		// maxColumn = columnA2;
		// maxPair = pairA2;
		// maxListPair = LP;
		// }
		// }
		// }
		// }
		// report.addColumnsReport(compareColumns(maxPair.column,
		// maxColumn));
		// listB.remove(maxPair.column.getName());
		// listA.remove(maxColumn.getName());
		// maxListPair.clear();
		//
		// for (Column columnA2 : hmListColumn.keySet()) {
		// if (!columnA2.getName().equals(maxColumn.getName())) {
		// hmListColumn.get(columnA2).remove(maxPair.column);
		// }
		// }
		//
		// if (!maxColumn.getName().equals(columnA.getName())) {
		// it = hmListColumn.keySet().iterator();
		// }
		// }
		// }

		// This part is when keySetA.size != keySetB.size
		if (listB.size() != 0) {
			for (String columnNameB : listB) {
				Column columnB = hmColumnsB.get(columnNameB);
				SingleColumnReport singleReport = new SingleColumnReport(
						columnB);
				singleReport.setOtherTable(tableA);
				report.addColumnsReport(singleReport);
			}
		}
		if (listA.size() != 0) {
			for (String columnNameA : listA) {
				Column columnA = hmColumnsA.get(columnNameA);
				SingleColumnReport singleReport = new SingleColumnReport(
						columnA);
				singleReport.setOtherTable(tableB);
				report.addColumnsReport(singleReport);
			}
		}
		return report;
	}

	public static CompareColumnsReport compareColumns(Column columnA,
			Column columnB) {
		CompareColumnsReport report = new CompareColumnsReport(columnA, columnB);

		report.setNamesMatch(columnA.getName().equals(columnB.getName()));

		if (columnA.getDefaultValue() != null)
			report.setDefaultValuesMatch(columnA.getDefaultValue().equals(
					columnB.getDefaultValue()));
		else if (columnB.getDefaultValue() == null)
			report.setDefaultValuesMatch(true);

		report.setSizesMatch(columnA.getSize() == columnB.getSize());

		report.setTypesMatch(columnA.getType().equals(columnB.getType()));

		report.setPKMatch(columnA.isPrimaryKey() == columnB.isPrimaryKey());

		report.setUnsignedMatch(columnA.isUnsigned() == columnB.isUnsigned());

		report.setNullablesMatch(columnA.isNullable() == columnB.isNullable());

		if (columnA.isForeignKey()) {
			if (columnB.isForeignKey()) {
				Column columnTA = columnA.getTargetColumnPrimaryKey();
				Column columnTB = columnB.getTargetColumnPrimaryKey();
				report.setTargetPKMatch(columnTA.getName().equals(
						columnTB.getName())
						&& columnTA.getTable().getName().equals(
								columnTB.getTable().getName()));
			} else report.setTargetPKMatch(false);
		} else if (!columnB.isForeignKey())
			report.setTargetPKMatch(true);

		return report;
	}

	private static class Pair {
		public Column column;
		public int score;

		public Pair(Column column, int score) {
			this.column = column;
			this.score = score;
		}
	}

	private static class ListPair {
		private List<Pair> list = new ArrayList<Pair>();

		public void addPair(Pair pair) {
			list.add(pair);
		}

		public void remove(Column column) {
			remove(getPair(column.getName()));
		}

		public int size() {
			return list.size();
		}

		public void clear() {
			list.clear();
		}

		public void remove(Pair pair) {
			list.remove(pair);
		}

		public Pair findMaxScore() {
			Pair maxPair = new Pair(null, -1);
			for (Pair pair : list) {
				if (pair.score > maxPair.score)
					maxPair = pair;
			}

			return maxPair;
		}

		public Pair getPair(String columnName) {
			Iterator<Pair> it = list.iterator();
			boolean found = false;
			Pair pair = null;
			while (it.hasNext() && !found) {
				pair = it.next();
				found = pair.column.getName().equals(columnName);
			}

			if (found)
				return pair;
			return null;
		}
	}
}
