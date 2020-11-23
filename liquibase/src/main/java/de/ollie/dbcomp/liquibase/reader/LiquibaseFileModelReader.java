package de.ollie.dbcomp.liquibase.reader;

import java.io.File;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.ollie.dbcomp.liquibase.reader.actions.AddColumnChangeModelChangeAction;
import de.ollie.dbcomp.liquibase.reader.actions.CreateTableChangeModelChangeAction;
import de.ollie.dbcomp.liquibase.reader.actions.DropColumnChangeModelChangeAction;
import de.ollie.dbcomp.liquibase.reader.actions.ModelChangeAction;
import de.ollie.dbcomp.model.ColumnCMO;
import de.ollie.dbcomp.model.DatamodelCMO;
import de.ollie.dbcomp.model.SchemaCMO;
import de.ollie.dbcomp.model.TableCMO;
import de.ollie.dbcomp.model.TypeCMO;
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

	public DatamodelCMO readModel() throws Exception {
		DatabaseChangeLog changeLog = getDatabaseChangeLog();
		return createDatamodel(changeLog);
	}

	private DatabaseChangeLog getDatabaseChangeLog() throws Exception {
		ResourceAccessor resourceAccessor = new FileSystemResourceAccessor(baseDirectory.getAbsolutePath());
		ChangeLogParameters changeLogParameters = new ChangeLogParameters();
		return ChangeLogParserFactory.getInstance().getParser(rootFile.getName(), resourceAccessor)
				.parse(rootFile.getName(), changeLogParameters, resourceAccessor);
	}

	private DatamodelCMO createDatamodel(DatabaseChangeLog changeLog) {
		List<ModelChangeAction> actions = Arrays.asList( //
				new AddColumnChangeModelChangeAction(), //
				new CreateTableChangeModelChangeAction(), //
				new DropColumnChangeModelChangeAction() //
		);
		DatamodelCMO datamodel = DatamodelCMO.of();
		for (ChangeSet changeSet : changeLog.getChangeSets()) {
			for (Change change : changeSet.getChanges()) {
				LOG.info("processing: {} ({})", change.getChangeSet().getId(), change.getClass());
				actions //
						.stream() //
						.filter(action -> action.isMatchingForChange(change)) //
						.findFirst() //
						.ifPresentOrElse( //
								action -> action.processOnDataModel(change, datamodel), //
								() -> LOG.warn("ignored: {} - {}", change.getClass().getSimpleName(), change) //
						) //
				;
			}
		}
		return datamodel;
	}

	public static SchemaCMO getSchema(DatamodelCMO datamodel, String schemaName) {
		schemaName = isEmptyOrNull(schemaName) ? "" : schemaName;
		if (datamodel.getSchemaByName(schemaName).isEmpty()) {
			datamodel.addSchemata(SchemaCMO.of(schemaName));
			LOG.info("added schema '{}' to data model.", schemaName);
		}
		return datamodel.getSchemaByName(schemaName).get();
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

	public static Optional<TableCMO> getTable(SchemaCMO schema, String tableName) {
		return schema.getTableByName(tableName);
	}

	public static ColumnCMO getColumn(TableCMO table, ColumnConfig columnConfig) {
		return ColumnCMO.of( //
				columnConfig.getName(), //
				getType(columnConfig), //
				columnConfig.isAutoIncrement()//
		);
	}

	// TODO: Types should be managed by special classes (independent from the
	// Types or other classes and frameworks).
	public static TypeCMO getType(ColumnConfig cc) {
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
		}
		return type;
	}

}