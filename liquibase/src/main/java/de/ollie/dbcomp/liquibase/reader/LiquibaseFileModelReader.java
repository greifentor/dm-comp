package de.ollie.dbcomp.liquibase.reader;

import java.io.File;
import java.sql.Types;

import de.ollie.dbcomp.model.DatamodelCMO;
import de.ollie.dbcomp.model.SchemeCMO;
import de.ollie.dbcomp.util.DBTypeConverter;
import liquibase.change.Change;
import liquibase.change.ColumnConfig;
import liquibase.change.core.AddColumnChange;
import liquibase.change.core.CreateTableChange;
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

	private DBTypeConverter typesConverter;
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
	public LiquibaseFileModelReader(DBTypeConverter typesConverter, File baseDirectory, File rootFile) {
		super();
		this.baseDirectory = baseDirectory;
		this.rootFile = rootFile;
		this.typesConverter = typesConverter;
	}

	public DatamodelCMO readModel() throws Exception {
		DatabaseChangeLog changeLog = getDatabaseChangeLog();
		return createDatamodel(changeLog);
	}

	private DatabaseChangeLog getDatabaseChangeLog() throws Exception {
		ResourceAccessor resourceAccessor = new FileSystemResourceAccessor(this.baseDirectory.getAbsolutePath());
		ChangeLogParameters changeLogParameters = new ChangeLogParameters();
		return ChangeLogParserFactory.getInstance().getParser(this.rootFile.getName(), resourceAccessor)
				.parse(this.rootFile.getName(), changeLogParameters, resourceAccessor);
	}

	private DatamodelCMO createDatamodel(DatabaseChangeLog changeLog) {
		DatamodelCMO datamodel = DatamodelCMO.of(new SchemeCMO[0]);
		for (ChangeSet changeSet : changeLog.getChangeSets()) {
			for (Change change : changeSet.getChanges()) {
				if (change instanceof AddColumnChange) {
					processAddColumnChange((AddColumnChange) change, datamodel);
				} else if (change instanceof CreateTableChange) {
					processCreateTableChange((CreateTableChange) change, datamodel);
				} else {
					System.out.println("ignored: " + change.getClass().getSimpleName() + " - " + change);
				}
			}
		}
		return datamodel;
	}

	private void processAddColumnChange(AddColumnChange change, DatamodelCMO datamodel) {
	}

	private void processCreateTableChange(CreateTableChange change, DatamodelCMO datamodel) {
	}

	// TODO: Types should be managed by special classes (independent from the
	// Types or other classes and frameworks).
	private TypeInfo getDataType(ColumnConfig cc) {
		LiquibaseDataType type = DataTypeFactory.getInstance().fromDescription(cc.getType(), null);
		TypeInfo ti = new TypeInfo().setName(type.getName().toUpperCase());
		if ("bigint".equalsIgnoreCase(type.getName())) {
			ti.setName("BIGINT");
			ti.setDataType(Types.BIGINT);
		} else if ("blob".equalsIgnoreCase(type.getName())) {
			ti.setName("BLOB");
			ti.setDataType(Types.BLOB);
		} else if ("bool".equalsIgnoreCase(type.getName()) || "boolean".equalsIgnoreCase(type.getName())) {
			ti.setName("BOOLEAN");
			ti.setDataType(Types.BOOLEAN);
		} else if ("currency".equalsIgnoreCase(type.getName())) {
			ti.setName("NUMERIC");
			ti.setDataType(Types.NUMERIC);
			ti.setDecimalDigits(2);
			ti.setColumnSize(15);
		} else if ("date".equalsIgnoreCase(type.getName())) {
			ti.setName("DATE");
			ti.setDataType(Types.DATE);
		} else if ("datetime".equalsIgnoreCase(type.getName())) {
			ti.setName("TIMESTAMP");
			ti.setDataType(Types.TIMESTAMP);
		} else if ("decimal".equalsIgnoreCase(type.getName())) {
			ti.setName("DECIMAL");
			ti.setDataType(Types.DECIMAL);
			ti.setColumnSize(Integer.valueOf(type.getParameters()[0].toString()));
			ti.setDecimalDigits(Integer.valueOf(type.getParameters()[1].toString()));
		} else if ("int".equalsIgnoreCase(type.getName()) || "integer".equalsIgnoreCase(type.getName())) {
			ti.setName("INTEGER");
			ti.setDataType(Types.INTEGER);
		} else if ("number".equalsIgnoreCase(type.getName()) || "numeric".equalsIgnoreCase(type.getName())) {
			ti.setName("NUMERIC");
			ti.setDataType(Types.NUMERIC);
			ti.setColumnSize(Integer.valueOf(type.getParameters()[0].toString()));
			ti.setDecimalDigits(Integer.valueOf(type.getParameters()[1].toString()));
		} else if ("timestamp".equalsIgnoreCase(type.getName())) {
			ti.setName("TIMESTAMP");
			ti.setDataType(Types.TIMESTAMP);
		} else if ("varchar".equalsIgnoreCase(type.getName())) {
			ti.setDataType(Types.VARCHAR);
			ti.setColumnSize(Integer.valueOf(type.getParameters()[0].toString()));
		} else {
			System.out.println("ignored type: " + type.getName());
		}
		return ti;
	}

}