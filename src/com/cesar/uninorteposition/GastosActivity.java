package com.cesar.uninorteposition;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.cesar.adapter.AdapterCliente;
import com.cesar.adapter.AdapterGasto;
import com.cesar.bean.Cliente;
import com.cesar.bean.Configuracion;
import com.cesar.bean.Gasto;
import com.cesar.bean.Pago;
import com.cesar.bean.Usuario;
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
  
public class GastosActivity extends ListActivity {
	
	private DatabaseHelper databaseHelper = null;
	private List<Gasto> listaGastos;
	private int request_code = 1;
	private Gasto gastoSeleccionado;
	private Usuario u;
	private Configuracion c;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		u = (Usuario)getIntent().getParcelableExtra("usuario");
		c = (Configuracion)getIntent().getParcelableExtra("configuracion");
		setListAdapter(listarGastos());
		registerForContextMenu(this.getListView());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gastos, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
                case R.id.nuevo:
                	Gasto g = new Gasto();
                	g.setFecha(c.getFecha());
                	g.setDetalle("");
                	verGasto(g,"crear");
                	break;
                }
        return super.onOptionsItemSelected(item);
    }
	
	private void verGasto(Gasto g,String f){
		Intent i = new Intent(this, GastoActivity.class );
		i.putExtra("gasto", g);
		i.putExtra("flag", f);
		startActivityForResult(i, request_code);
	}
	
	private ListAdapter listarGastos() {
		AdapterGasto adapter = null;
		try {
			Dao<Gasto, Integer> gastoDao = getHelper().getGastoDao();
			listaGastos = gastoDao.queryForAll();
			adapter = new AdapterGasto(this, listaGastos);
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
	
	public void onListItemClick(ListView parent,View v,int position, long id){
		verGasto(listaGastos.get(position),"modificar");		
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if ((requestCode == request_code) && (resultCode == RESULT_OK)){
        	setListAdapter(listarGastos());
        }
    }
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
	  super.onCreateContextMenu(menu, v, menuInfo);
	  getMenuInflater().inflate(R.menu.menu_context_gasto,menu);
	  menu.setHeaderTitle("Menu");
	  
	}
	
	@Override
    public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		int itemId = item.getItemId();
        switch (itemId) {
                case R.id.nuevo_gasto:
                	Gasto g = new Gasto();
                	g.setFecha(new Date());
                	g.setDetalle("");
                	verGasto(g,"crear");
                	break;
                case R.id.borrar_gasto:
                	gastoSeleccionado = listaGastos.get(info.position);
                	mostrarMensajeEliminar();
                    break;

                }
        return super.onOptionsItemSelected(item);
    }
	
	private void mostrarMensajeEliminar() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Eliminar");
		builder.setMessage("Esta seguro de eliminar este gasto ?");
		builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   eliminarGasto();		        		
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
	
	private void eliminarGasto() {
		Dao<Gasto, Integer> gastoDao;
		try {
			gastoDao = getHelper().getGastoDao();
			gastoDao.delete(gastoSeleccionado);
			setListAdapter(listarGastos());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
