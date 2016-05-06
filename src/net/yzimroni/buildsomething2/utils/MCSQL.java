/** TO DO 
 * AUTO ID
 * getNextID
 */

package net.yzimroni.buildsomething2.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MCSQL {
	private MySQL ms = null;

	public MCSQL(String host, String port, String database, String username, String password) {
		ms = new MySQL(host, port, database, username, password);
		openConnecting();
		ms.REPASS();
		password = "123";
	}

	public void disable() {
		closeConnecting();
		this.ms = null;
	}

	public boolean hasConnecting() {
		return ms.checkConnection();
	}

	public boolean closeConnecting() {
		ms.closeConnection(ms.getConn());
		return !hasConnecting();
	}

	public boolean openConnecting() {
		try {
			ms.open();
		} catch (Exception e) {
			return false;
		}
		return hasConnecting();
	}

	public PreparedStatement getPrepare(String s) {
		try {
			return ms.getConn().prepareStatement(s);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int getPrepareReturnId(String s) {
		try {
			PreparedStatement p = getPrepareAutoKeys(s);
			p.executeUpdate();
			return getIdFromPrepared(p);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public int getIdFromPrepared(PreparedStatement p) {
		try {
			ResultSet rs = p.getGeneratedKeys();
			rs.next();
			return rs.getInt(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public PreparedStatement getPrepare(String s, int autoGeneratedKeys) {
		try {
			return ms.getConn().prepareStatement(s, autoGeneratedKeys);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public PreparedStatement getPrepareAutoKeys(String s) {
		return getPrepare(s, Statement.RETURN_GENERATED_KEYS);
	}

	public ResultSet get(String q) {
		try {
			ResultSet a = ms.getConn().createStatement().executeQuery(q);
			return a;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public int set(String q) {
		try {
			int a = ms.getConn().createStatement().executeUpdate(q);
			return a;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

}

class MySQL extends Database {
	String user = "";
	String database = "";
	String password = "";
	String port = "";
	String hostname = "";
	Connection c = null;

	public MySQL(String hostname, String portnmbr, String database, String username, String password) {
		this.hostname = hostname;
		this.port = portnmbr;
		this.database = database;
		this.user = username;
		this.password = password;
	}

	public Connection open() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			this.c = DriverManager.getConnection("jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database, this.user, this.password);
			this.password = "123";
			return c;

		} catch (SQLException e) {
			System.out.println("Could not connect to MySQL server! because: " + e.getMessage());
		} catch (ClassNotFoundException e) {
			System.out.println("JDBC Driver not found!");
		}
		return this.c;
	}

	public void REPASS() {
		password = "123";
	}

	public boolean checkConnection() {
		if (this.c != null) {
			return true;
		}
		return false;
	}

	public Connection getConn() {
		return this.c;
	}

	public void closeConnection(Connection c) {
		try {
			c.close();
		} catch (SQLException e) {
		}
		c = null;
	}
}

class Database {
	protected boolean connected;
	protected Connection connection;
	public int lastUpdate;

	public Database() {
		this.connected = false;
		this.connection = null;
	}

	protected Statements getStatement(String query) {
		String trimmedQuery = query.trim();
		if (trimmedQuery.substring(0, 6).equalsIgnoreCase("SELECT"))
			return Statements.SELECT;
		if (trimmedQuery.substring(0, 6).equalsIgnoreCase("INSERT"))
			return Statements.INSERT;
		if (trimmedQuery.substring(0, 6).equalsIgnoreCase("UPDATE"))
			return Statements.UPDATE;
		if (trimmedQuery.substring(0, 6).equalsIgnoreCase("DELETE"))
			return Statements.DELETE;
		if (trimmedQuery.substring(0, 6).equalsIgnoreCase("CREATE"))
			return Statements.CREATE;
		if (trimmedQuery.substring(0, 5).equalsIgnoreCase("ALTER"))
			return Statements.ALTER;
		if (trimmedQuery.substring(0, 4).equalsIgnoreCase("DROP"))
			return Statements.DROP;
		if (trimmedQuery.substring(0, 8).equalsIgnoreCase("TRUNCATE"))
			return Statements.TRUNCATE;
		if (trimmedQuery.substring(0, 6).equalsIgnoreCase("RENAME"))
			return Statements.RENAME;
		if (trimmedQuery.substring(0, 2).equalsIgnoreCase("DO"))
			return Statements.DO;
		if (trimmedQuery.substring(0, 7).equalsIgnoreCase("REPLACE"))
			return Statements.REPLACE;
		if (trimmedQuery.substring(0, 4).equalsIgnoreCase("LOAD"))
			return Statements.LOAD;
		if (trimmedQuery.substring(0, 7).equalsIgnoreCase("HANDLER"))
			return Statements.HANDLER;
		if (trimmedQuery.substring(0, 4).equalsIgnoreCase("CALL")) {
			return Statements.CALL;
		}
		return Statements.SELECT;
	}

	protected static enum Statements {
		SELECT, INSERT, UPDATE, DELETE, DO, REPLACE, LOAD, HANDLER, CALL, CREATE, ALTER, DROP, TRUNCATE, RENAME, START, COMMIT, ROLLBACK, SAVEPOINT, LOCK, UNLOCK, PREPARE, EXECUTE, DEALLOCATE, SET, SHOW, DESCRIBE, EXPLAIN, HELP, USE, ANALYZE, ATTACH, BEGIN, DETACH, END, INDEXED, ON, PRAGMA, REINDEX, RELEASE, VACUUM;
	}
}
