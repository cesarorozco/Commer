package com.cesar.uninorteposition;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.cesar.adapter.AdapterError;
import com.cesar.adapter.AdapterFactura;
import com.cesar.bean.Configuracion;
import com.cesar.bean.Error;
import com.cesar.bean.Factura;
import com.cesar.bean.Gasto;
import com.cesar.bean.Pago;
import com.cesar.bean.Usuario;
import com.cesar.db.DatabaseHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Usuario u;
	private Configuracion configuracion;
	private DatabaseHelper databaseHelper = null;
	SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
	private ProgressDialog pDialog;
	private TareaListarFacturas tarea;
	private TareaListarErrores tarea2;
	public double total = 0;
	public long error = 0;
	
	private int request_code = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		u = (Usuario)getIntent().getParcelableExtra("usuario");
		cargarConfiguracion();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		if(u.getRoll().equalsIgnoreCase("Administrador"))
		   getMenuInflater().inflate(R.menu.main_admin, menu);
		else
			if(u.getRoll().equalsIgnoreCase("Cobrador"))
				getMenuInflater().inflate(R.menu.main, menu);
			else
				getMenuInflater().inflate(R.menu.menu_sup, menu);
		
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        Intent i;
        switch (itemId) {
                case R.id.menu_clientes:
                	 i = new Intent(this, ClientesActivity.class );
            		 startActivity(i);
                	 break;
                case R.id.menu_facturas:
                	 i = new Intent(this, FacturasActivity.class );
                	 i.putExtra("usuario", this.u);
                	 i.putExtra("estado", "activa");
                	 i.putExtra("configuracion", this.configuracion);
            		 startActivity(i);
               	 	 break;
                case R.id.menu_gastos:
                	 i = new Intent(this, GastosActivity.class );
                	 i.putExtra("usuario", this.u);
              	 	 i.putExtra("configuracion", this.configuracion);
            		 startActivity(i);
               	 	 break;
                case R.id.menu_resumen:
                	Error error = null;
                	if(this.listarErrores(configuracion.getFecha()).size()!=0){
                		error = this.listarErrores(configuracion.getFecha()).get(0);
                		error.setRecaudo(this.calcularPagos());
                		error.setVentas(this.calcularVentas());
                		error.setGastos(this.calcularGastos());
                		verError(error,"modificar");
                	}else{
                		error = new Error();
                		error.setFecha(configuracion.getFecha());
                  	 	error.setUsuario(u);
                  	 	error.setBase(0);
                  	 	error.setRecaudo(this.calcularPagos());
                  	 	error.setVentas(calcularVentas());
                  	 	error.setGastos(this.calcularGastos());
                  	 	error.setEfectivo(0);
                  	 	error.setError(0);
                     	verError(error,"crear");
                	}
                	 break;
                case R.id.menu_usuarios:
                	 i = new Intent(this, UsuariosActivity.class );
            		 startActivity(i);
               	 	 break;
                case R.id.menu_errores:
                	 i = new Intent(this, ErroresActivity.class );
                	 i.putExtra("usuario", this.u);
                	 i.putExtra("configuracion", this.configuracion);
           		 	 startActivity(i);
                   	 break;
                case R.id.menu_conf:
                	 i = new Intent(this, ConfiguracionActivity.class );
                	 startActivityForResult(i, request_code);
               	 	 break; 
                case R.id.menu_exportar:
              	 	 exportar();
              	 	 break; 
               case R.id.menu_importar:
            	     mostrarMensajeImportar();
              	 	 break; 
               case R.id.menu_revisiones:
            	     i = new Intent(this, RevisionesActivity.class );
            	     i.putExtra("usuario", this.u);
         		     startActivity(i);
               	 	 break;
               case R.id.menu_canceladas:
            	   	 i = new Intent(this, FacturasActivity.class );
              	     i.putExtra("usuario", this.u);
              	 	 i.putExtra("estado", "cancelada");
              	 	 i.putExtra("configuracion", this.configuracion);
              	 	 startActivity(i);
              	 	 break;
               case R.id.menu_estado:
          	   	 	 calcularEstado();
            	 	 break;
                }
        
        
        return super.onOptionsItemSelected(item);
    }
	
	private void mostrarMensajeImportar() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("PELIGRO");
		builder.setMessage("LA BASE DE DATOS SERA REEMPLAZADA");
		builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   importar(); 		
		           }
		       });
		builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	  
	           }
	       });
		//Create the AlertDialog
		builder.create().show();
		
	}

	private void calcularEstado() {
		total = 0;
		pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Sumando Saldos...");
        pDialog.setCancelable(true);
        pDialog.setMax(100);
 
        tarea = new TareaListarFacturas();
        tarea.execute();
		
		
	}

	private void importar() {
		try {
            File sd = Environment.getExternalStorageDirectory();
            File data  = Environment.getDataDirectory();
      
            if (sd.canWrite()) {
            	String  currentDBPath= "//data//" + "com.cesar.uninorteposition"
                        + "//databases//" + "commer.db";
                String backupDBPath  = "/import/commer.db";
                File  backupDB= new File(data, currentDBPath);
                File currentDB  = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                backupDB.delete();
                Toast.makeText(getBaseContext(), "ARCHIVO IMPORTADO EXITOSAMENTE",
                        Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {

            Toast.makeText(getBaseContext(), "ERROR AL IMPORTAR", Toast.LENGTH_LONG)
                    .show();

        }		
	}

	private void exportar() {
		 try {
             File sd = Environment.getExternalStorageDirectory();
             File data = Environment.getDataDirectory();

             if (sd.canWrite()) {
                 String  currentDBPath= "//data//" + "com.cesar.uninorteposition"
                         + "//databases//" + "commer.db";
                 String backupDBPath  = "/export/commer.db";
                 File currentDB = new File(data, currentDBPath);
                 File backupDB = new File(sd, backupDBPath);

                 FileChannel src = new FileInputStream(currentDB).getChannel();
                 FileChannel dst = new FileOutputStream(backupDB).getChannel();
                 dst.transferFrom(src, 0, src.size());
                 src.close();
                 dst.close();
                 Toast.makeText(getBaseContext(), "ARCHIVO EXPORTADO EXITOSAMENTE",
                         Toast.LENGTH_LONG).show();

             }
         } catch (Exception e) {

             Toast.makeText(getBaseContext(), "ERROR AL EXPORTAR", Toast.LENGTH_LONG)
                     .show();

         }
     }
		
	public void verError(Error e,String f){
		Intent i = new Intent(this, ErrorActivity.class );
		i.putExtra("error", e);
		i.putExtra("usuario", this.u);
		i.putExtra("flag", f);
		i.putExtra("configuracion", this.configuracion);
		startActivity(i);
	}
	
	private double calcularGastos() {
		double total = 0.0;
		for(Gasto g:listarGastos())
			total=total+g.getValor();
		return total;
	}
	
	private List<Gasto> listarGastos() {
		List<Gasto> listaGasto = null;
		try {
			Dao<Gasto, Integer> simpleDao = getHelper().getGastoDao();
			try {
				listaGasto = simpleDao.queryBuilder().where().eq("fecha", sd.parse(sd.format(configuracion.getFecha()))).query();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listaGasto;
	}

	private double calcularPagos() {
		double total = 0.0;
		for(Pago p:listarPagos())
			total=total+p.getValor();
		return total;
	}

	private List<Pago> listarPagos() {
		List<Pago> listaPago = null;
		try {
			Dao<Pago, Integer> simpleDao = getHelper().getPagoDao();
			try {
				listaPago = simpleDao.queryBuilder().where().eq("fecha", sd.parse(sd.format(configuracion.getFecha()))).query();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listaPago;
	}

	private double calcularVentas() {
		double total = 0.0;
		for(Factura f:listarFacturas())
			total=total+f.getMonto();
		return total;
	}

	private List<Factura> listarFacturas() {
		
		List<Factura> listaFacturas = null;
		try {
			Dao<Factura, Integer> simpleDao = getHelper().getFacturaDao();
			try {
				listaFacturas = simpleDao.queryBuilder().where().eq("fecha", sd.parse(sd.format(configuracion.getFecha()))).query();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listaFacturas;
		
	}
	
	private void cargarConfiguracion() {
		configuracion = null;
		List<Configuracion> listConfiguracion = null;
		try {
			Dao<Configuracion, Integer> configuracionDao = getHelper().getConfiguracionDao();
			listConfiguracion = configuracionDao.queryForAll();
			//listUsuarios = usuarioDao.queryForAll();
			if(!listConfiguracion.isEmpty())
				configuracion=listConfiguracion.get(0);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private DatabaseHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = DatabaseHelper.getHelper(this);
		}
		return databaseHelper;
	}

	private class TareaListarFacturas extends AsyncTask<Void, Integer, Boolean> {
		 
        private List<Factura> listaFacturas;
        

		@Override
        protected Boolean doInBackground(Void... params) {
 
        	try { 
    			Dao<Factura, Integer> simpleDao = getHelper().getFacturaDao();
    			QueryBuilder<Factura, Integer> qb = simpleDao.queryBuilder();
    			qb.where().eq("estado", "activa");
    		    listaFacturas = qb.query();
    		    for(Factura f:listaFacturas)
    		    	total=total+calcularSaldo(f);
    		   
    		} catch (SQLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
 
            return true;
        }
 
       	@Override
        protected void onProgressUpdate(Integer... values) {
            int progreso = values[0].intValue();
            pDialog.setProgress(progreso);
        }
 
        @Override
        protected void onPreExecute() {
            pDialog.setProgress(0);
            pDialog.show();
        }
 
        @Override
        protected void onPostExecute(Boolean result) {
            if(result)
            {
            	pDialog.dismiss();
            	listarErrores();
            	
            	
            }
        }
 
        @Override
        protected void onCancelled() {
            Toast.makeText(MainActivity.this, "Tarea cancelada!",
                Toast.LENGTH_SHORT).show();
        }
        
        private double calcularSaldo(Factura factura) {
    		double totalAbono=0.0;
    		if (factura.getPagos()!=null) {
    			for (Pago p : factura.getPagos())
    				totalAbono = totalAbono + p.getValor();
    		}
    		return factura.getTotal()-totalAbono;
    	}
    }

	private class TareaListarErrores extends AsyncTask<Void, Integer, Boolean> {
		 
       

		@Override
        protected Boolean doInBackground(Void... params) {
 
        	
    		try {
    			Dao<Error, Integer> simpleDao = getHelper().getErrorDao();
    			error = simpleDao.queryRawValue("select sum(error) as total from error");
    		} catch (SQLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
 
            return true;
        }
 
       	@Override
        protected void onProgressUpdate(Integer... values) {
            int progreso = values[0].intValue();
            pDialog.setProgress(progreso);
        }
 
        @Override
        protected void onPreExecute() {
            pDialog.setProgress(0);
            pDialog.show();
        }
 
        @Override
        protected void onPostExecute(Boolean result) {
            if(result)
            	
            {
            	mostrarMensaje();
            	pDialog.dismiss();
            }
        }
 
        @Override
        protected void onCancelled() {
            Toast.makeText(MainActivity.this, "Tarea cancelada!",
                Toast.LENGTH_SHORT).show();
        }
    }
	
	public void listarErrores() {
		pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Errores...");
        pDialog.setCancelable(true);
        pDialog.setMax(100);
 
        tarea2 = new TareaListarErrores();
        tarea2.execute();
		
	}

	public void mostrarMensaje() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("ESTADO TOTAL");
		builder.setMessage("TOTAL: "+DecimalFormat.getInstance().format(total)+"\nERROR: "+DecimalFormat.getInstance().format(error));
		builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	    		
		           }
		       });
		//Create the AlertDialog
		builder.create().show();
		
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
        // TODO Auto-generated method stub
        if ((requestCode == request_code) && (resultCode == RESULT_OK)){
        	cargarConfiguracion();
        }
        	
    }
	
	private List<Error> listarErrores(Date fecha) {
		List<Error> listaErrores = null;
		try {
			Dao<Error, Integer> simpleDao = getHelper().getErrorDao();
			try {
				listaErrores  = simpleDao.queryBuilder().where()
					.eq("fecha", sd.parse(sd.format(fecha))).and().eq(Error.USUARIO_ID, u.getId()).query();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listaErrores;
	}



}
