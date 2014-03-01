package com.cesar.uninorteposition;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.cesar.adapter.AdapterFactura;
import com.cesar.adapter.AdapterPagoList;
import com.cesar.bean.Cliente;
import com.cesar.bean.Configuracion;
import com.cesar.bean.Factura;
import com.cesar.bean.Pago;
import com.cesar.bean.Revision;
import com.cesar.db.DatabaseHelper;
import com.j256.ormlite.dao.Dao;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class PagosActivity extends ListActivity {
	
	private DatabaseHelper databaseHelper = null;
	private List<Pago> listaPagos;
	private Factura f;
	private int request_code = 1;
	private Pago pagoSeleccionado;
	private Configuracion c;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		f = (Factura)getIntent().getParcelableExtra("factura");
		c = (Configuracion)getIntent().getParcelableExtra("configuracion");
		setListAdapter(listarPagos());
		registerForContextMenu(this.getListView());
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pagos, menu);
		return true;
	}
	
	private ListAdapter listarPagos() {
		AdapterPagoList adapter = null;
		try {
			Dao<Pago, Integer> simpleDao = getHelper().getPagoDao();
			listaPagos = simpleDao.queryBuilder().where()
			         .eq(Pago.FACTURA_ID, f.getId())
			         .query();
			adapter = new AdapterPagoList(this, listaPagos);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return adapter;
		
	}
	private DatabaseHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = DatabaseHelper.getHelper(this);
		}
		return databaseHelper;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
                case R.id.menu_add:
                	Pago pago = new Pago();
                	pago.setFactura(f);
                	pago.setFecha(c.getFecha());
                	pago.setValor(f.getCuota());
                	verPago(pago,"crear");
                    break;
                }
        return super.onOptionsItemSelected(item);
    }
	
	public void verPago(Pago p,String f){
		Intent i = new Intent(this, PagoActivity.class );
		i.putExtra("pago", p);
		i.putExtra("flag", f);
		startActivityForResult(i, request_code);
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if ((requestCode == request_code) && (resultCode == RESULT_OK)){
        	setListAdapter(listarPagos());
        	verificarSaldo();
        }
    }
		
	private void verificarSaldo() {
		double totalPagos = 0;
		for(Pago p:listaPagos){
			totalPagos=totalPagos+p.getValor();
		}
		if(totalPagos>=f.getTotal()){
			  actualizarfactura();	
			  nuevoPrestamo();
		}else{
			Intent data = new Intent();
     	    setResult(3, data);
            finish();
		}
			
		
	}

	protected void nuevoPrestamo() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("NUEVA FACTURA");
		builder.setMessage("DESEA CREAR UNA NUEVA FACTURA ?");
		builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   Intent data = new Intent();
		        	   setResult(RESULT_OK, data);
		               finish();
		           }
		       });
		builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   Intent data = new Intent();
		        	   setResult(3, data);
		               finish();
		           }
		       });
			// Create the AlertDialog
		builder.create().show(); 
		
	}

	public void onListItemClick(ListView parent,View v,int position, long id){
		Pago p = listaPagos.get(position);
		verPago(p,"modificar");
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
	  super.onCreateContextMenu(menu, v, menuInfo);
	  getMenuInflater().inflate(R.menu.menu_context_pagos,menu);
	  menu.setHeaderTitle("MENU");
	  
	}
	
	@Override
    public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		int itemId = item.getItemId();
        switch (itemId) {
                case R.id.nuevo_pago:
                	Pago pago = new Pago();
                	pago.setFactura(f);
                	pago.setFecha(new Date());
                	pago.setValor(f.getCuota());
                	verPago(pago,"crear");
                	break;
                case R.id.borrar_pago:
                	pagoSeleccionado = listaPagos.get(info.position);
                	mostrarMensajeEliminar();
                    break;

                }
        return super.onOptionsItemSelected(item);
    }
	
	private void mostrarMensajeEliminar() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("ELIMINAR");
		builder.setMessage("ESTA SEGURO DE ELIMINAR ESTE PAGO ?");
		builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   eliminarPago();		        		
		           }
		       });
		builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               // User cancelled the dialog
		           }
		       });
			// Create the AlertDialog
		builder.create().show();
		
	}
	
	private void eliminarPago() {
		Dao<Pago, Integer> pagoDao;
		try {
			pagoDao = getHelper().getPagoDao();
			pagoDao.delete(pagoSeleccionado);
			setListAdapter(listarPagos());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void actualizarfactura(){
		
		try {
			Dao<Factura, Integer> facturaDao = getHelper().getFacturaDao();
			f.setEstado("cancelada");
			facturaDao.update(f);
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
