
package powerschool.idp.sample.module;

import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.naming.directory.Attributes;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import edu.vt.middleware.ldap.auth.Authenticator;
import edu.vt.middleware.ldap.jaas.AbstractLoginModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Properties;

public class SimpleLoginModule extends AbstractLoginModule implements LoginModule {

    private static final Logger log = LoggerFactory.getLogger(SimpleLoginModule.class);
    
    private String driver = "org.hsqldb.jdbcDriver";
    private String protocol = "jdbc:hsqldb:mem:";
    private Connection conn = null;
    private ArrayList statements = new ArrayList(); // list of Statements, PreparedStatements
    private PreparedStatement psInsert = null;
    private PreparedStatement psUpdate = null;
    private Statement s = null;
    private ResultSet rs = null;
    private String dbName = "IdPdb";

	// contains key/value pairs for the valid users. Valid keys: admin, teacher, student, guardian. Values in format: "username;passwd"
	private Map<String, Set<String>> validUsersMap; 

	// configurable option
	private boolean debug = false;

	// username and password
	private String username;
	private String password;
	
	private String host;

	private Principal userPrincipal;

	public SimpleLoginModule() {
		log.debug("[SimpleLoginModule] created");
	}
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) { 

		this.subject = subject;
		this.callbackHandler = callbackHandler;
		this.sharedState = sharedState;
		validUsersMap = new HashMap<String, Set<String>>();

		final Iterator<String> i = options.keySet().iterator();
		while (i.hasNext()) {
			String key = i.next();
			if (!key.equals("debug") && !key.equals("host")) {
				Set<String> logins = new HashSet<String>();
				validUsersMap.put(key, logins);
				String []users = ((String)options.get(key)).split("\\|");
				for (String user: users) {
					logins.add(user);
				}
			}
		}
		log.warn("[SimpleLoginModule] validUsersMap:"+validUsersMap);

		if (options.get("debug") != null) {
			debug = "true".equalsIgnoreCase((String)options.get("debug"));
		}		
		
		host = "https://localhost";
		if (options.get("host") != null) {
			host = (String)options.get("host");
		}
		
		database.loadUsers(host, validUsersMap);
		
		log.debug("[SimpleLoginModule] initialized");

	}

	/**
	 * Authenticate the user by prompting for a user name and password.
	 *
	 * @return true in all cases since this <code>LoginModule</code> should not be ignored.
	 *
	 * @exception v if the authentication fails. <p>
	 *
	 * @exception c if this <code>LoginModule</code> is unable to perform the authentication.
	 */
	public boolean login() throws LoginException {		
		
		final NameCallback nameCb = new NameCallback("Enter user: ");
		final PasswordCallback passCb = new PasswordCallback("Enter user password: ", false);
		getCredentials(nameCb, passCb, true);

		username = nameCb.getName();
		password = (passCb.getPassword() != null) ? new String(passCb.getPassword()) : "";

		log.debug("[SimpleLoginModule] user entered user name: {}", username);
		log.debug("[SimpleLoginModule] user entered password: {}", password);

		
		if (debug) {
			log.debug("[SimpleLoginModule] user entered user name: {}", username);
			log.debug("[SimpleLoginModule] user entered password: {}", password);
		}

		loginSuccess = database.authenticate(username, password);

		if (debug) {
			log.debug("[SimpleLoginModule] Authentication {}", ((loginSuccess) ? "succeeded" : "failed"));
		}

		if (!loginSuccess) {
			username = null;
			password = null;
			throw new LoginException("Authentication failed.");
		}
		return true;
	}

	public boolean commit() throws LoginException {
		if (!loginSuccess) {
			return false;
			
		} else {
			// Add a Principal (authenticated identity) to the Subject
			userPrincipal = new SimplePrincipal(username);
			if (!subject.getPrincipals().contains(userPrincipal)) {
				subject.getPrincipals().add(userPrincipal);
			}

			if (debug) {
				log.debug("[SimpleLoginModule] added Principal to Subject");
			}

			username = null;
			password = null;

			commitSuccess = true;
			return true;
		}
	}

	public boolean abort() throws LoginException {
		if (!loginSuccess) {
			return false;
			
		} else if (loginSuccess && !commitSuccess) {
			// login succeeded but overall authentication failed
			loginSuccess = false;
			username = null;
			password = null;
			userPrincipal = null;
		} else {
			// overall authentication succeeded and commit succeeded, but someone else's commit failed
			logout();
		}
		return true;
	}

	public boolean logout() throws LoginException {
		subject.getPrincipals().remove(userPrincipal);
		loginSuccess = false;
		loginSuccess = commitSuccess;
		username = null;
		password = null;
		userPrincipal = null;
		return true;
	}


	private static Database database = new Database();
	
	private static class Database {
	
		private String driver = "org.hsqldb.jdbcDriver";
		private String protocol = "jdbc:hsqldb:mem:";
		private Connection conn = null;
		private ArrayList statements = new ArrayList(); // list of Statements, PreparedStatements
		private PreparedStatement psInsert = null;
		private PreparedStatement psUpdate = null;
		private Statement s = null;
		private ResultSet rs = null;
		private String dbName = "IdPdb";
		
		int numUsers = 0;
		
		public Database() {
			initDB();
		}
		
		private void initDB() {
				
			loadDriver();

			try {
				conn = DriverManager.getConnection(protocol + dbName, "sa", "");

				conn.setAutoCommit(false);

				s = conn.createStatement();
				statements.add(s);

				s.execute("create table idpusers(username varchar(128), password varchar(25), context varchar(128), oid varchar(100))");
				conn.commit();

			} catch (SQLException sqle) {
				printSQLException(sqle);
				releaseDB();
			}
		}
		
		private void loadDriver() {

			try {
				Class.forName(driver ).newInstance();
				
			} catch (ClassNotFoundException cnfe) {
				log.debug("[SimpleLoginModule] Unable to load the JDBC driver {}", driver);

			} catch (InstantiationException ie) {
				log.debug("[SimpleLoginModule] Unable to instantiate the JDBC driver {}", driver);

			} catch (IllegalAccessException iae) {
				log.debug("[SimpleLoginModule] Not allowed to access the JDBC driver {}", driver);

			}
		}
		
		private void printSQLException(SQLException e) {
			while (e != null) {
				log.debug("\n----- SQLException -----");
				log.debug("  SQL State:  {}", e.getSQLState());
				log.debug("  Error Code: {}", e.getErrorCode());
				log.debug("  Message:    {}", e.getMessage());
				e.printStackTrace(System.err);
				e = e.getNextException();
			}
		}
		
		private void releaseDB() {
			// ResultSet
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (SQLException sqle) {
			}

			// Statements and PreparedStatements
			int i = 0;
			while (!statements.isEmpty()) {
				// PreparedStatement extend Statement
				Statement st = (Statement)statements.remove(i);
				try {
					if (st != null) {
						st.close();
						st = null;
					}
				} catch (SQLException sqle) {
					printSQLException(sqle);
				}
			}

			//Connection
			try {
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (SQLException sqle) {
				printSQLException(sqle);
			}

		}
		
		private void loadUsers(String host, Map<String, Set<String>> validUsersMap) {
			if (numUsers > 0) return;
			try {

				psInsert = conn.prepareStatement("insert into idpusers values (?, ?, ?, ?)");
				statements.add(psInsert);
				
				final Iterator<String> i = validUsersMap.keySet().iterator();
				while (i.hasNext()) {
					String key = i.next();

					for (String credpair : validUsersMap.get(key)) {
						String[] creds = credpair.split(";");
						psInsert.setString(1, creds[0]);
						psInsert.setString(2, creds[1]);
						psInsert.setString(3, key);
						String cred = null;
						
						if ("state-id".equals(key)) {
							cred = creds[0];
						} else if ("psguid".equals(key)) {
							cred = creds[0];
						} else {
							cred = host + "/oid/" + key + "/" + creds[0];
						}
					
						psInsert.setString(4, cred);
						psInsert.executeUpdate();
						
						log.debug("[SimpleLoginModule] inserted user:"+creds[0]+", pw: "+creds[1]+", cred: "+cred);
						numUsers++;
					}
				}
				
				conn.commit();

			} catch (SQLException sqle) {
				printSQLException(sqle);
				releaseDB();
			}
		}
		
		private boolean authenticate(String username, String password) {
			boolean success = false;
			try {
				
				rs = s.executeQuery("SELECT username, password FROM idpusers");
				while(rs.next()) {
					if (rs.getString(1).equals(username) && rs.getString(2).equals(password)) {
						success = true;
						break;
					}
				}			
			
			} catch (SQLException sqle) {
				printSQLException(sqle);
				
			} finally {
				return success;
			}
		}
	}
}
