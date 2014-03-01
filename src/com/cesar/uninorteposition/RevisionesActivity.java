package com.cesar.uninorteposition;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import com.cesar.adapter.AdapterError;
import com.cesar.adapter.AdapterFactura;
import com.cesar.adapter.AdapterGasto;
import com.cesar.adapter.AdapterRevision;
import com.cesar.bean.Configuracion;
import com.cesar.bean.Error;
import com.cesar.bean.Factura;
import com.cesar.bean.Gasto;
import com.cesar.bean.Pago;
import com.cesar.bean.Revision;
import com.cesar.bean.Usuario;
import com.cesar.db.DatabaseHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class RevisionesActivity extends ListActivity {
	
	private DatabaseHelper databaseHelper = null;
	private List<Revision> listaRevisiones;
	private int request_code = 1;
	private Gasto gastoSeleccionado;
	private AdapterRevision adapter;
	private Usuario u;
	private Configuracion c;
	public  ProgressDialog pDialog;
	private TareaListarRevisiones tarea;
	public List<Factura> listaFacturas;
	public double totalCartera;
	private TareaListarFacturas tarea2;
	public BigDecimal b;
	public int clientes;
	private TareaGenerarRevisiones tarea3;
	private TareaListarFacturas tarea4;
	DecimalFormat format =  new  DecimalFormat ( "##" );
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		c = this.buscarConfiguracion();
		u = (Usuario)getIntent().getParcelableExtra("usuario");
		listarRevisiones();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (u.getRoll().equals("Administrador")) {
			getMenuInflater().inflate(R.menu.revisiones, menu);
	      }
		
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        
        switch (itemId) {
                case R.id.generar_revisiones:
                	generar();
                    break;
                }
    	return super.onOptionsItemSelected(item);
    }
	
	private void generar() {
		
			pDialog = new ProgressDialog(RevisionesActivity.this);
	        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        pDialog.setMessage("Listando Facturas...");
	        pDialog.setCancelable(true);
	        pDialog.setMax(100);
	        tarea4 = new TareaListarFacturas();
	        tarea4.execute();
		
	}

	private void guardarRevisiones(List<Revision> lista) {
		try {
			Dao<Revision, Integer> simpleDao = getHelper().getRevisionDao();
			for(Revision r:lista)
				simpleDao.create(r);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private List<Revision> generarRevisiones(List<Factura> subList) {
		List<Revision> lista = new LinkedList<Revision>();
		Revision r;
		
		for(Factura f :subList){
			r = new Revision();
			r.setFecha(c.getFecha());
			r.setFactura(f);
			r.setDetalle("NO ENCONTRADO");
			lista.add(r);
		}
			
				
		return lista;
	}

	private List<Error> listarErrores() {
		List<Error> listaErrores = null;
		try {
			Dao<Error, Integer> simpleDao = getHelper().getErrorDao();
			listaErrores = simpleDao.queryForAll();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listaErrores;
		
	}
	
	private double calcularSaldo(Factura factura) {
		double totalAbono=0.0;
		if (factura.getPagos()!=null) {
			for (Pago p : factura.getPagos())
				totalAbono = totalAbono + p.getValor();
		}
		return factura.getTotal()-totalAbono;
	}
	
	public int calcularDias(){
		int resp = 30;
		double total = calcularError();
		Toast.makeText(this, format.format(totalCartera), Toast.LENGTH_SHORT).show();
		if(totalCartera*(10/100)<total&&total<totalCartera*(15/100))
			resp=15;
		else
			if(totalCartera*(15/100)<=total)
				resp = 7;
		
		return resp;
	}
	
	private double calcularError() {
		double total = 0;
		for(Error e : listarErrores())
			total = total + e.getError();
		return total;
	}

	private void listarRevisiones() {
		pDialog = new ProgressDialog(RevisionesActivity.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Listando Revisiones...");
        pDialog.setCancelable(true);
        pDialog.setMax(100);
        tarea = new TareaListarRevisiones();
        tarea.execute();
	}
	
	private void listarFacturas() {
		pDialog = new ProgressDialog(RevisionesActivity.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Calculando...");
        pDialog.setCancelable(true);
        pDialog.setMax(100);
        tarea2 = new TareaListarFacturas();
        tarea2.execute();
	}
	
	private DatabaseHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = DatabaseHelper.getHelper(this);
		}
		return databaseHelper;
	}
	
	public void onListItemClick(ListView parent,View v,int position, long id){
		verRevision(listaRevisiones.get(position),"revision");		
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if ((requestCode == request_code) && (resultCode == RESULT_OK)){
        	listarRevisiones();
        }
    }
	
	private void verRevision(Revision r,String f){
		Intent i = new Intent(this, FacturaActivity.class );
		i.putExtra("revision", r);
		i.putExtra("flag", f);
		i.putExtra("usuario", this.u);
		startActivityForResult(i, request_code);
	}
	
	private Configuracion buscarConfiguracion() {
		Configuracion configuracion = null;
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
		return configuracion;
	}
	
	private class TareaListarRevisiones extends AsyncTask<Void, Integer, Boolean> {
		 
        @Override
        protected Boolean doInBackground(Void... params) {
        	
        	adapter = null;
 
        	try {
    			Dao<Revision, Integer> gastoDao = getHelper().getRevisionDao();
    			listaRevisiones = gastoDao.queryBuilder().orderBy("fecha", true).query();
    			adapter = new AdapterRevision(getContext(), listaRevisiones, c.getFecha());
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
            	setListAdapter(adapter);
                pDialog.dismiss();
            }
        }
 
        @Override
        protected void onCancelled() {
            Toast.makeText(RevisionesActivity.this, "Tarea cancelada!",
                Toast.LENGTH_SHORT).show();
        }
    }
	
	public Context getContext(){
		return this.getApplication();
		
	}

	private class TareaListarFacturas extends AsyncTask<Void, Integer, Boolean> {
		 
        @Override
        protected Boolean doInBackground(Void... params) {
 
        	try { 
    			Dao<Factura, Integer> simpleDao = getHelper().getFacturaDao();
    			QueryBuilder<Factura, Integer> qb = simpleDao.queryBuilder();
    			qb.where().eq("estado", "activa");
    		    listaFacturas = qb.orderBy("indice", true).query();
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
            	generarRevisiones();
            }
        }
 
        @Override
        protected void onCancelled() {
            Toast.makeText(RevisionesActivity.this, "Tarea cancelada!",
                Toast.LENGTH_SHORT).show();
        }
    }
	
	private class TareaGenerarRevisiones extends AsyncTask<Void, Integer, Boolean> {
		 
        @Override
        protected Boolean doInBackground(Void... params) {
 
        	for (Factura f : listaFacturas)
    			totalCartera=totalCartera+calcularSaldo(f);
 
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
            	int nm_dias=calcularDias();
            	Toast.makeText(getContext(), listaFacturas.size()+" "+nm_dias, Toast.LENGTH_SHORT).show();
            	b=new BigDecimal(listaFacturas.size()/nm_dias);
            	if(nm_dias<=listaFacturas.size())
            		clientes = b.setScale(0, RoundingMode.UP).intValue();
            	else
            		clientes=1;
            	Toast.makeText(getContext(), " "+clientes, Toast.LENGTH_SHORT).show();
            	int indice = (int) (Math.random()*(listaFacturas.size()-clientes));
            	Toast.makeText(getContext(), clientes+" "+indice, Toast.LENGTH_SHORT).show();
            	List<Revision> facturasAleatoreas = generarRevisiones(listaFacturas.subList(indice, indice+clientes));
            	listaRevisiones = facturasAleatoreas;
            	guardarRevisiones(listaRevisiones);
    			listarRevisiones();
            }
        }
 
        @Override
        protected void onCancelled() {
            Toast.makeText(RevisionesActivity.this, "Tarea cancelada!",
                Toast.LENGTH_SHORT).show();
        }
    }

	public void generarRevisiones() {
		pDialog = new ProgressDialog(RevisionesActivity.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Calculando...");
        pDialog.setCancelable(true);
        pDialog.setMax(100);
        tarea3 = new TareaGenerarRevisiones();
        tarea3.execute();
		
	}
}
