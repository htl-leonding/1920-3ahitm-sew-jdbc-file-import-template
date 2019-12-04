package at.htl.jdbcfileimport.business;

import at.htl.jdbcfileimport.model.Person;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PersonDatabase {

    static String url = "jdbc:derby://localhost:1527/db";
    static String username = "app";
    static String password = "app";
    static final String FILE_NAME = "got.csv";

    /**
     * import a csv-File of persons in UTF-8
     * all duplicates should be removed
     * <p>
     * name;city;house
     * Mollander;Bhorash;Cockshaw
     * ...
     *
     * @param fileName
     * @return list of Persons
     * @throws IOException
     */
    public List<Person> importCsv(String fileName) throws IOException {
        List<Person> persons = null;


        return persons;
    }

    /**
     * Create the table PERSON
     *
     * @throws SQLException
     */
    public void createTable() throws SQLException {

    }

    /**
     * Es gibt 2 Möglichkeiten
     * <p>
     * Variante 1:
     * Alle Datensätze in den Hauptspeicher laden und dann in Datenbank speichern
     * <p>
     * Variante 2:
     * Zeilenweise einlesen und sofort in der Datenbank speichern
     *
     * @param fileName
     */
    public void insertCsv(String fileName) {

        // 1. Open the stream of the csv-file
        // 2. read a line
        // 3. persist the line



    }

    public void insertCsv() {
        insertCsv(FILE_NAME);
    }

    private void insertPerson(Person person) {

    }


    /**
     * Hier werden Database Metadata Objekte verwendet. Das wurde noch nicht gelernt.
     * Ist daher nur zu benutzen.
     * <p>
     * <p>
     * The ResultSet returned by the getColumns() method contains a list of columns
     * for the given table. The column with index 4 contains the column name,
     * and the column with index 5 contains the column type. The column type is
     * an integer matching one of the type constants found in java.sql.Types
     *
     * @return table names
     */
    static List<String> getTableNamesFromDatabase() {
        String catalog = null;
        String schemaPattern = username.toUpperCase(); // APP
        String tableNamePattern = null;
        String[] types = null;
        List<String> tableNames = new LinkedList<>();

        try (Connection conn = DriverManager.getConnection( // <1>
                url,
                username,
                password)) {

            DatabaseMetaData databaseMetaData = conn.getMetaData();

            ResultSet rs = databaseMetaData.getTables(
                    catalog,
                    schemaPattern,
                    tableNamePattern,
                    types
            );

            while (rs.next()) {
                String dbObjectName = rs.getString(3);
                String typeName = rs.getString(4);

                /**
                 * löschen aller DB-Objekte
                 * Bei Tabellen mit Fremdschlüsseln müssten vorher noch die Constraints
                 * gelöscht werden
                 */
//                String sql = "DROP " + typeName + " " + dbObjectName;
//                System.out.println(sql);
//                try (Statement stmt = conn.createStatement()) {
//                    stmt.execute(sql);
//                }
//                System.out.printf("%s %s deleted\n", rs.getString(3), rs.getString(4));
                tableNames.add(dbObjectName);
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return tableNames;
    }


}
