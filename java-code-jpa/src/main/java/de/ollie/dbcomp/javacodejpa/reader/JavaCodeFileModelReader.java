package de.ollie.dbcomp.javacodejpa.reader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.ollie.blueprints.codereader.java.JavaCodeConverter;
import de.ollie.blueprints.codereader.java.model.Annotation;
import de.ollie.blueprints.codereader.java.model.ClassDeclaration;
import de.ollie.blueprints.codereader.java.model.CompilationUnit;
import de.ollie.dbcomp.javacodejpa.reader.converter.FieldDeclarationToColumnCMOConverter;
import de.ollie.dbcomp.model.ColumnCMO;
import de.ollie.dbcomp.model.DataModelCMO;
import de.ollie.dbcomp.model.ReaderResult;
import de.ollie.dbcomp.model.SchemaCMO;
import de.ollie.dbcomp.model.TableCMO;
import de.ollie.dbcomp.report.ImportReport;

/**
 * A Liquibase model reader, which is able to process Liquibase XML file from the file system.
 *
 * @author Oliver.Lieshoff
 *
 */
public class JavaCodeFileModelReader {

	private static final Logger LOG = LogManager.getLogger(JavaCodeFileModelReader.class);

	private final FieldDeclarationToColumnCMOConverter fieldDeclarationToColumnCMOConverter;

	private static String cutQuotes(String s) {
		if (s.startsWith("\"")) {
			s = s.substring(1);
		}
		if (s.endsWith("\"")) {
			s = s.substring(0, s.length() - 1);
		}
		return s;
	}

	public JavaCodeFileModelReader(FieldDeclarationToColumnCMOConverter fieldDeclarationToColumnCMOConverter) {
		super();
		this.fieldDeclarationToColumnCMOConverter = fieldDeclarationToColumnCMOConverter;
	}

	public ReaderResult read(String pathName) throws Exception {
		ImportReport importReport = new ImportReport();
		return new ReaderResult() //
				.setDataModel(createDataModel(
						getCompilationUnits(new ArrayList<>(), new File(pathName), new JavaCodeConverter()),
						importReport)) //
				.setImportReport(importReport);
	}

	private List<CompilationUnit> getCompilationUnits(List<CompilationUnit> l, File file,
			JavaCodeConverter codeConverter) throws IOException {
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				getCompilationUnits(l, f, codeConverter);
			}
			return l;
		}
		if (file.getCanonicalPath().endsWith(".java")) {
			LOG.info("added file: {}", file.getCanonicalPath());
			l.add(codeConverter.convert(Files.readString(Path.of(file.getCanonicalPath()))));
		}
		return l;
	}

	private DataModelCMO createDataModel(List<CompilationUnit> compilationUnits, ImportReport importReport) {
		return DataModelCMO.of( //
				SchemaCMO.of( //
						"", //
						getTables(compilationUnits, importReport) //
				) //
		);
	}

	private TableCMO[] getTables(List<CompilationUnit> compilationUnits, ImportReport importReport) {
		return compilationUnits //
				.stream() //
				.flatMap(compilationUnit -> compilationUnit.getTypeDeclarations().stream()) //
				.filter(typeDeclaration -> typeDeclaration instanceof ClassDeclaration) //
				.map(typeDeclaration -> (ClassDeclaration) typeDeclaration) //
				.filter(classDeclaration -> getAnnotationWithName("Entity", classDeclaration).isPresent()) //
				.map(classDeclaration -> TableCMO.of(getTableName(classDeclaration), getColumns(classDeclaration))) //
				.collect(Collectors.toList()) //
				.toArray(new TableCMO[0]) //
		;
	}

	private Optional<Annotation> getAnnotationWithName(String name, ClassDeclaration classDeclaration) {
		return classDeclaration.getAnnotations() //
				.stream() //
				.filter(annotation -> annotation.getName().equals(name)) //
				.findFirst() //
		;
	}

	private String getTableName(ClassDeclaration classDeclaration) {
		return getAnnotationWithName("Table", classDeclaration) //
				.map(annotation -> cutQuotes(annotation.getValue())) //
				.orElse(classDeclaration.getName()) //
		;
	}

	private ColumnCMO[] getColumns(ClassDeclaration classDeclaration) {
		return classDeclaration.getFields() //
				.stream() //
				.map(fieldDeclarationToColumnCMOConverter::convert) //
				.collect(Collectors.toList()) //
				.toArray(new ColumnCMO[classDeclaration.getFields().size()]) //
		;
	}

}