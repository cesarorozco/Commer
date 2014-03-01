package com.cesar.db;

import java.util.Date;
import java.sql.SQLException;
//import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cesar.bean.Cliente;
import com.cesar.bean.Configuracion;
import com.cesar.bean.Factura;
import com.cesar.bean.Gasto;
import com.cesar.bean.Pago;
import com.cesar.bean.Revision;
import com.cesar.bean.Usuario;
import com.cesar.bean.Error;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	// name of the database file for your application -- change to something appropriate for your app
	private static final String DATABASE_NAME = "commer.db";
	// any time you make changes to your database objects, you may have to increase the database version
	private static final int DATABASE_VERSION = 2;

	// the DAO object we use to access the SimpleData table
	private Dao<Factura, Integer> facturaDao = null;
	private Dao<Cliente, Integer> clienteDao = null;
	private Dao<Pago, Integer> pagoDao = null;
	private Dao<Gasto, Integer> gastoDao = null;
	private Dao<Usuario, Integer> usuarioDao = null;
	private Dao<Error, Integer> errorDao = null;
	private Dao<Configuracion, Integer> configuracionDao = null;
	private Dao<Revision, Integer> revisionDao = null;

	private static final AtomicInteger usageCounter = new AtomicInteger(0);

	// we do this so there is only one helper
	private static DatabaseHelper helper = null;

	private DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * Get the helper, possibly constructing it if necessary. For each call to this method, there should be 1 and only 1
	 * call to {@link #close()}.
	 */
	public static synchronized DatabaseHelper getHelper(Context context) {
		if (helper == null) {
			helper = new DatabaseHelper(context);
		}
		usageCounter.incrementAndGet();
		return helper;
	}

	/**
	 * This is called when the database is first created. Usually you should call createTable statements here to create
	 * the tables that will store your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTable(connectionSource, Cliente.class);
			TableUtils.createTable(connectionSource, Factura.class);
			TableUtils.createTable(connectionSource, Pago.class);
			TableUtils.createTable(connectionSource, Gasto.class);
			TableUtils.createTable(connectionSource, Usuario.class);
			TableUtils.createTable(connectionSource, Error.class);
			TableUtils.createTable(connectionSource, Configuracion.class);
			TableUtils.createTable(connectionSource, Revision.class);
			// here we try inserting data in the on-create as a test
			Dao<Factura, Integer> dao = getFacturaDao();
			Dao<Cliente, Integer> cli = getClienteDao();
			Dao<Pago, Integer> pag = getPagoDao();
			Dao<Usuario, Integer> usu = getUsuarioDao();
			Dao<Configuracion, Integer> conf = getConfiguracionDao();
			
			long millis = System.currentTimeMillis();
			// create some entries in the onCreate
			Usuario u = new Usuario();
			u.setLogin("admin");
			u.setPassword("123456");
			u.setRoll("Administrador");
			usu.create(u);
			
			Configuracion co = new Configuracion();
			co.setFecha(new Date());
			co.setDias("0101010");
			co.setFormaPago(3);
			co.setIva(20);
			co.setPlazo(60);
			conf.create(co);
			
			Cliente c = new Cliente();
			c.setNombres("Cesar orozco");
			cli.create(c);
			
			Factura simple = new Factura();
			simple.setEstado("activa");
			simple.setCliente(c);
			simple.setFecha(new Date());
			simple.setNext(new Date());
			simple.setFechaVencimiento(new Date());
			dao.create(simple);
			
			Pago pago = new Pago();
			pago.setValor(60000);
			pago.setFecha(new Date());
			pago.setFactura(simple);
			pag.create(pago);
			
			Pago pago2 = new Pago();
			pago2.setValor(60000);
			pago2.setFecha(new Date());
			pago2.setFactura(simple);
			pag.create(pago2);
			
			// create some entries in the onCreate
			Cliente c1 = new Cliente();
			c1.setNombres("Maria marin");
			cli.create(c1);
						
			Factura simple1 = new Factura();
			simple1.setEstado("activa");
			simple1.setCliente(c1);
			simple1.setFecha(new Date());
			simple1.setNext(new Date());
			simple1.setFechaVencimiento(new Date());
			dao.create(simple1);
			
			Pago pago3 = new Pago();
			pago3.setValor(60000);
			pago3.setFecha(new Date());
			pago3.setFactura(simple1);
			pag.create(pago3);
			
			Pago pago4 = new Pago();
			pago4.setValor(60000);
			pago4.setFecha(new Date());
			pago4.setFactura(simple1);
			pag.create(pago4);
			
			
			Log.i(DatabaseHelper.class.getName(), "created new entries in onCreate: " + millis);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
	 * the various data to match the new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, Pago.class, true);
			TableUtils.dropTable(connectionSource, Factura.class, true);
			TableUtils.dropTable(connectionSource, Cliente.class, true);
			TableUtils.dropTable(connectionSource, Gasto.class, true);
			TableUtils.dropTable(connectionSource, Usuario.class, true);
			TableUtils.dropTable(connectionSource, Error.class, true);
			TableUtils.dropTable(connectionSource, Configuracion.class, true);
			TableUtils.dropTable(connectionSource, Revision.class, true);
			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the Database Access Object (DAO) for our SimpleData class. It will create it or just give the cached
	 * value.
	 */
	public Dao<Factura, Integer> getFacturaDao() throws SQLException {
		if (facturaDao == null) {
			facturaDao = getDao(Factura.class);
		}
		return facturaDao;
	}
	
	public Dao<Cliente, Integer> getClienteDao() throws SQLException {
		if (clienteDao == null) {
			clienteDao = getDao(Cliente.class);
		}
		return clienteDao;
	}
	
	public Dao<Pago, Integer> getPagoDao() throws SQLException {
		if (pagoDao == null) {
			pagoDao = getDao(Pago.class);
		}
		return pagoDao;
	}
	
	public Dao<Gasto, Integer> getGastoDao() throws SQLException {
		if (gastoDao == null) {
			gastoDao = getDao(Gasto.class);
		}
		return gastoDao;
	}

	public Dao<Usuario, Integer> getUsuarioDao() throws SQLException {
		if (usuarioDao == null) {
			usuarioDao = getDao(Usuario.class);
		}
		return usuarioDao;
	}
	public Dao<Error, Integer> getErrorDao() throws SQLException {
		if (errorDao == null) {
			errorDao = getDao(Error.class);
		}
		return errorDao;
	}
	public Dao<Configuracion, Integer> getConfiguracionDao() throws SQLException {
		if (configuracionDao == null) {
			configuracionDao = getDao(Configuracion.class);
		}
		return configuracionDao;
	}
	public Dao<Revision, Integer> getRevisionDao() throws SQLException {
		if (revisionDao == null) {
			revisionDao = getDao(Revision.class);
		}
		return revisionDao;
	}

	/**
	 * Close the database connections and clear any cached DAOs. For each call to {@link #getHelper(Context)}, there
	 * should be 1 and only 1 call to this method. If there were 3 calls to {@link #getHelper(Context)} then on the 3rd
	 * call to this method, the helper and the underlying database connections will be closed.
	 */
	@Override
	public void close() {
		if (usageCounter.decrementAndGet() == 0) {
			super.close();
			facturaDao = null;
			helper = null;
		}
	}
}
