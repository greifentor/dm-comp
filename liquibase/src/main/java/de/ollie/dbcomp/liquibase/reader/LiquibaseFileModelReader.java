package de.ollie.dbcomp.liquibase.reader;

import java.io.File;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.ollie.dbcomp.liquibase.reader.actions.AddAutoIncrementChangeModelChangeAction;
import de.ollie.dbcomp.liquibase.reader.actions.AddColumnChangeModelChangeAction;
import de.ollie.dbcomp.liquibase.reader.actions.AddPrimaryKeyChangeModelChangeAction;
import de.ollie.dbcomp.liquibase.reader.actions.CreateTableChangeModelChangeAction;
import de.ollie.dbcomp.liquibase.reader.actions.DropColumnChangeModelChangeAction;
import de.ollie.dbcomp.liquibase.reader.actions.DropPrimaryKeyChangeModelChangeAction;
import de.ollie.dbcomp.liquibase.reader.actions.DropTableChangeModelChangeAction;
import de.ollie.dbcomp.liquibase.reader.actions.ModelChangeAction;
import de.ollie.dbcomp.model.ColumnCMO;
import de.ollie.dbcomp.model.DataModelCMO;
import de.ollie.dbcomp.model.ReaderResult;
import de.ollie.dbcomp.model.SchemaCMO;
import de.ollie.dbcomp.model.TableCMO;
import de.ollie.dbcomp.model.TypeCMO;
import de.ollie.dbcomp.report.ImportReport;
import de.ollie.dbcomp.report.ImportReportMessage;
import de.ollie.dbcomp.report.ImportReportMessageLevel;
import de.ollie.dbcomp.util.TypeConverter;
import liquibase.change.Change;
import liquibase.change.ColumnConfig;
import liquibase.changelog.ChangeLogParameters;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.datatype.DataTypeFactory;
import liquibase.datatype.LiquibaseDataType;
import liquibase.parser.ChangeLogParserFactory;
import liquibase.resource.FileSystemResourceAccessor;
import liquibase.resource.ResourceAccessor;

/**
 * A Liquibase model reader, which is able to process Liquibase XML file from the file system.
 *
 * @author Oliver.Lieshoff
 *
 */
public class LiquibaseFileModelReader {

	private static final Logger LOG = LogManager.getLogger(LiquibaseFileModelReader.class);

	private TypeConverter typeConverter;
	private File baseDirectory;
	private File rootFile;

	/**
	 * Creates a new model reader with the passed parameters.
	 *
	 * @param typeConverter A converter for the types.
	 * @param baseDirectory The directory which contains the base XML file of the model.
	 * @param rootFile      The root file of the Liquibase model.
	 * @throws IllegalArgumentException Passing null value.
	 */
	public LiquibaseFileModelReader(TypeConverter typeConverter, File baseDirectory, File rootFile) {
		super();
		this.baseDirectory = baseDirectory;
		this.rootFile = rootFile;
		this.typeConverter = typeConverter;
	}

	public ReaderResult read() throws Exception {
		ImportReport importReport = new ImportReport();
		DatabaseChangeLog changeLog = getDatabaseChangeLog(importReport);
		return new ReaderResult() //
				.setDataModel(createDataModel(changeLog, importReport)) //
				.setImportReport(importReport);
	}

	private DatabaseChangeLog getDatabaseChangeLog(ImportReport importReport) throws Exception {
		ResourceAccessor resourceAccessor = new FileSystemResourceAccessor(baseDirectory.getAbsolutePath());
		ChangeLogParameters changeLogParameters = new ChangeLogParameters();
		return ChangeLogParserFactory.getInstance().getParser(rootFile.getName(), resourceAccessor)
				.parse(rootFile.getName(), changeLogParameters, resourceAccessor);
	}

	private DataModelCMO createDataModel(DatabaseChangeLog changeLog, ImportReport importReport) {
		List<ModelChangeAction> actions = Arrays.asList( //
				new AddColumnChangeModelChangeAction(), //
				new AddAutoIncrementChangeModelChangeAction(), //
				new AddPrimaryKeyChangeModelChangeAction(), //
				new CreateTableChangeModelChangeAction(), //
				new DropColumnChangeModelChangeAction(), //
				new DropPrimaryKeyChangeModelChangeAction(), //
				new DropTableChangeModelChangeAction() //
		);
		DataModelCMO dataModel = DataModelCMO.of();
		for (ChangeSet changeSet : changeLog.getChangeSets()) {
			for (Change change : changeSet.getChanges()) {
				LOG.info("processing: {} ({})", change.getChangeSet().getId(), change.getClass());
				actions //
						.stream() //
						.filter(action -> action.isMatchingForChange(change)) //
						.findFirst() //
						.ifPresentOrElse( //
								action -> action.processOnDataModel(change, dataModel, importReport), //
								() -> {
									LOG.warn("ignored: {} - {}", change.getClass().getSimpleName(), change);
									importReport.addMessages( //
											new ImportReportMessage() //
													.setLevel(ImportReportMessageLevel.WARN) //
													.setMessage(String.format("ignored: %s - %s",
															change.getClass().getSimpleName(), change)));
								} //
						) //
				;
			}
		}
		return dataModel;

	}

	public static SchemaCMO getSchema(DataModelCMO dataModel, String schemaName) {
		schemaName = isEmptyOrNull(schemaName) ? "" : schemaName;
		if (dataModel.getSchemaByName(schemaName).isEmpty()) {
			dataModel.addSchemata(SchemaCMO.of(schemaName));
			LOG.info("added schema '{}' to data model.", schemaName);
		}
		return dataModel.getSchemaByName(schemaName).get();
	}

	public static boolean isEmptyOrNull(String s) {
		return (s == null) || s.isEmpty();
	}

	public static Optional<TableCMO> createOrGetTable(SchemaCMO schema, String tableName) {
		if (schema.getTableByName(tableName).isEmpty()) {
			schema.addTables(TableCMO.of(tableName));
			LOG.info("added table '{}' to schema: {}", tableName, schema);
		}
		return schema.getTableByName(tableName);
	}

	public static Optional<TableCMO> getTable(SchemaCMO schema, String tableName, ImportReport importReport) {
		Optional<TableCMO> result = schema.getTableByName(tableName);
		if (result.isEmpty()) {
			LOG.warn("table '{}' not found in schema: {}", tableName, getSchemaName(schema));
			importReport.addMessages( //
					new ImportReportMessage() //
							.setLevel(ImportReportMessageLevel.ERROR) //
							.setMessage(String.format("table '%s' not found in schema: %s", tableName,
									getSchemaName(schema))) //
			);
		}
		return result;
	}

	private static String getSchemaName(SchemaCMO schema) {
		return (schema != null) && !schema.getName().equals("") //
				? schema.getName() //
				: "n/a";
	}

	public static ColumnCMO getColumn(TableCMO table, ColumnConfig columnConfig, ImportReport importReport) {
		return ColumnCMO.of( //
				columnConfig.getName(), //
				getType(columnConfig, importReport), //
				columnConfig.isAutoIncrement()//
		);
	}

	// TODO: Types should be managed by special classes (independent from the
	// Types or other classes and frameworks).
	public static TypeCMO getType(ColumnConfig cc, ImportReport importReport) {
		LiquibaseDataType lbdType = DataTypeFactory.getInstance().fromDescription(cc.getType(), null);
		TypeCMO type = TypeCMO.of(Types.OTHER, null, null);
		if ("bigint".equalsIgnoreCase(lbdType.getName())) {
			type.setSqlType(Types.BIGINT);
		} else if ("blob".equalsIgnoreCase(lbdType.getName())) {
			type.setSqlType(Types.BLOB);
		} else if ("bool".equalsIgnoreCase(lbdType.getName()) || "boolean".equalsIgnoreCase(lbdType.getName())) {
			type.setSqlType(Types.BOOLEAN);
		} else if ("currency".equalsIgnoreCase(lbdType.getName())) {
			type.setDecimalPlace(2);
			type.setLength(15);
			type.setSqlType(Types.NUMERIC);
		} else if ("date".equalsIgnoreCase(lbdType.getName())) {
			type.setSqlType(Types.DATE);
		} else if ("datetime".equalsIgnoreCase(lbdType.getName())) {
			type.setSqlType(Types.TIMESTAMP);
		} else if ("decimal".equalsIgnoreCase(lbdType.getName())) {
			type.setDecimalPlace(Integer.valueOf(lbdType.getParameters()[1].toString()));
			type.setLength(Integer.valueOf(lbdType.getParameters()[0].toString()));
			type.setSqlType(Types.DECIMAL);
		} else if ("int".equalsIgnoreCase(lbdType.getName()) || "integer".equalsIgnoreCase(lbdType.getName())) {
			type.setSqlType(Types.INTEGER);
		} else if ("number".equalsIgnoreCase(lbdType.getName()) || "numeric".equalsIgnoreCase(lbdType.getName())) {
			type.setDecimalPlace(Integer.valueOf(lbdType.getParameters()[1].toString()));
			type.setLength(Integer.valueOf(lbdType.getParameters()[0].toString()));
			type.setSqlType(Types.NUMERIC);
		} else if ("timestamp".equalsIgnoreCase(lbdType.getName())) {
			type.setSqlType(Types.TIMESTAMP);
		} else if ("varchar".equalsIgnoreCase(lbdType.getName())) {
			type.setLength(Integer.valueOf(lbdType.getParameters()[0].toString()));
			type.setSqlType(Types.VARCHAR);
		} else {
			LOG.warn("ignored type: {}", lbdType.getName());
			importReport.addMessages( //
					new ImportReportMessage() //
							.setLevel(ImportReportMessageLevel.WARN) //
							.setMessage(String.format("type ignored: %s", lbdType.getName())) //
			);
		}
		return type;
	}

}