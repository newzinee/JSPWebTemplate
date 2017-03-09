package jdbc;

import java.io.IOException;
import java.io.StringReader;
import java.sql.DriverManager;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDriver;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

@WebListener
public class DBCPInitListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		String poolConfig = sce.getServletContext().getInitParameter("poolConfig");
		Properties prop = new Properties();
		try {
			prop.load(new StringReader(poolConfig));
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		loadJDBCDriver(prop);
		initConnectionPool(prop);
	}

	private void initConnectionPool(Properties prop) {
		String driverClass = prop.getProperty("jdbcdriver");
		try {
			Class.forName(driverClass);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("JDBC load 실패" + e);
		}
	}

	private void loadJDBCDriver(Properties prop) {
		try {
			String jdbcUrl = prop.getProperty("jdbcUrl");
			String dbUser = prop.getProperty("dbUser");
			String dbPass = prop.getProperty("dbPass");
			
			ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(jdbcUrl, dbUser, dbPass);
			
			PoolableConnectionFactory poolableConnFactory = new PoolableConnectionFactory(connectionFactory, null);
			poolableConnFactory.setValidationQuery("select 1");
			
			GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
			poolConfig.setTimeBetweenEvictionRunsMillis(1000L * 60L * 5L);
			poolConfig.setTestWhileIdle(true);
			poolConfig.setMinIdle(4);
			poolConfig.setMaxTotal(50);
			
			GenericObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(poolableConnFactory, poolConfig);
			poolableConnFactory.setPool(connectionPool);
			
			Class.forName("org.apache.commons.dbcp2.PoolingDriver");
			PoolingDriver driver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");
			String poolName = prop.getProperty("poolName");
			driver.registerPool(poolName, connectionPool);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
	}
}
