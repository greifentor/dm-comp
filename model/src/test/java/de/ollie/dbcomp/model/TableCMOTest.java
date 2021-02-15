package de.ollie.dbcomp.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Types;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TableCMOTest {

	@InjectMocks
	private TableCMO unitUnderTest;

	@Nested
	class TestsOfMethod_hasForeignKey_ForeignKeyCMO {

		@Test
		void passANullPointer_ReturnsFalse() {
			// Ausführung & Prüfung
			assertFalse(unitUnderTest.hasForeignKey(null));
		}

		@Test
		void passAPresentForeignKey_ReturnsFalse() {
			// Vorbereitung
			unitUnderTest = TableCMO.of("BASE_TABLE");
			ForeignKeyCMO fk =
					ForeignKeyCMO
							.of(
									"bla",
									ForeignKeyMemberCMO
											.of(
													unitUnderTest,
													ColumnCMO
															.of(
																	"BASE_COLUMN",
																	TypeCMO.of(Types.BIGINT, null, null),
																	false,
																	false),
													TableCMO.of("REF_TABLE"),
													ColumnCMO
															.of(
																	"REF_COLUMN",
																	TypeCMO.of(Types.BIGINT, null, null),
																	false,
																	false)));
			unitUnderTest.addForeignKeys(fk);
			// Ausführung & Prüfung
			assertTrue(unitUnderTest.hasForeignKey(fk));
		}

	}

}