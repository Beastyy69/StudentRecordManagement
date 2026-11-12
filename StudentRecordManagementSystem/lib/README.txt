Put the JDBC driver .jar file in this folder.

SQLite (default):
  Download sqlite-jdbc-<version>.jar from
  https://github.com/xerial/sqlite-jdbc/releases
  (or Maven Central: org.xerial:sqlite-jdbc)

  Note: sqlite-jdbc 3.42+ also needs slf4j-api-<version>.jar on the classpath
  (Maven Central: org.slf4j:slf4j-api). Put it in this folder too.

MySQL:
  Download mysql-connector-j-<version>.jar from
  https://dev.mysql.com/downloads/connector/j/
  and edit config/app.properties.

If no driver is present, the app still works: it automatically falls back to
file storage (data/students.dat) when storage.type=auto.

The run scripts add every .jar in this folder to the classpath automatically.
