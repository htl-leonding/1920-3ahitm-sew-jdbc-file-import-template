package at.htl.jdbcfileimport.business;

import at.htl.jdbcfileimport.model.Person;
import org.assertj.db.type.Source;
import org.assertj.db.type.Table;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.sql.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.db.output.Outputs.output;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class JdbcTest {

    /**
     * Löschen aller Tabellen (hier PERSON)
     */
    @BeforeAll
    static void init() {
        List<String> tables = PersonDatabase.getTableNamesFromDatabase();

        if (tables.isEmpty()) {
            System.out.println("no database tables to delete");
            return;
        }

        try (Connection conn = DriverManager.getConnection(
                PersonDatabase.url, PersonDatabase.username, PersonDatabase.password)) {
            for (String table : tables) {
                String sql = "DROP TABLE " + table;
                System.out.println(sql);
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(sql);
                }
            }
        } catch (SQLException e) {
            org.assertj.core.api.Assertions.fail(e.getMessage());
        }
    }


    @Test
    void test010_simpleFileImport() {
        try {
            PersonDatabase personDatabase = new PersonDatabase();
            List<Person> persons = personDatabase.importCsv(PersonDatabase.FILE_NAME);
            assertThat(persons).hasSize(200);
        } catch (IOException e) {
            org.assertj.core.api.Assertions.fail(e.getMessage());
        }
    }

    /**
     * Check, if the import does eliminate duplicates in the file
     */
    @Test
    void test020_simpleFileImport_double_Entries() {
        try {
            PersonDatabase personDatabase = new PersonDatabase();
            List<Person> persons = personDatabase.importCsv("got2.csv");
            assertThat(persons).hasSize(6);
        } catch (IOException e) {
            org.assertj.core.api.Assertions.fail("You have to remove the duplicates in your method" + " / " + e.getMessage());
        }
    }

    /**
     * Tabelle PERSON darf noch nicht erstellt sein
     */
    @Test
    void test030_check_if_table_existing() {

        Source source = new Source(
                PersonDatabase.url, PersonDatabase.username, PersonDatabase.password);
        Table table = new Table(source, "PERSON");
        assertThatThrownBy(() -> {
            output(table).toConsole();
        })
                .isInstanceOf(org.assertj.db.exception.AssertJDBException.class)
                .hasCauseInstanceOf(SQLSyntaxErrorException.class)
                .hasMessageContaining("Table/View 'PERSON' does not exist");
    }

    @Test
    void test040_createTable() {
        PersonDatabase personDatabase = new PersonDatabase();
        try {
            personDatabase.createTable();
        } catch (SQLException e) {
            org.assertj.core.api.Assertions.fail(e.getMessage());
        }
    }

    @Test
    void test050_createTable() {
        Source source = new Source(
                PersonDatabase.url, PersonDatabase.username, PersonDatabase.password);
        Table table = new Table(source, "PERSON");
        output(table).toConsole();
        org.assertj.db.api.Assertions.assertThat(table).hasNumberOfRows(0);
    }

    @Test
    void test060_insertCsvIntoDatabase() {

        PersonDatabase personDatabase = new PersonDatabase();
        personDatabase.insertCsv();

        try (Connection conn = DriverManager.getConnection(
                PersonDatabase.url, PersonDatabase.username, PersonDatabase.password
        )) {
            try (Statement stmt = conn.createStatement()) {
                String sql = "SELECT COUNT(*) as total FROM APP.PERSON";
                ResultSet rs = stmt.executeQuery(sql);
                rs.next();
                int total = rs.getInt("total");
                assertThat(total).isEqualTo(200);

                Source source = new Source(
                        PersonDatabase.url, PersonDatabase.username, PersonDatabase.password
                );
                Table personTable = new Table(source, "PERSON");
                org.assertj.db.api.Assertions.assertThat(personTable)
                        .row(0)
                        .hasValues(1,
                                "Mollander",
                                "Bhorash",
                                "Cockshaw"
                        );
                output(personTable).toConsole();
            }
        } catch (SQLException e) {
            org.assertj.core.api.Assertions.fail(e.getMessage());
        }
    }


}

