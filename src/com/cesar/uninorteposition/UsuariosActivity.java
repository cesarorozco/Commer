package com.cesar.uninorteposition;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.cesar.adapter.AdapterCliente;
import com.cesar.adapter.AdapterFactura;
import com.cesar.adapter.AdapterUsuario;
import com.cesar.bean.Cliente;
import com.cesar.bean.Factura;
import com.cesar.bean.Pago;
import com.cesar.bean.Usuario;
import com.cesar.db.DatabaseHelper;
import com.j256.ormlite.dao.Dao;

public class UsuariosActivity extends ListActivity {
	
	private DatabaseHelper databaseHelper = null;
	private List<Usuario> listaUsuarios;
	private int request_code = 1;
	private Usuario usuarioSeleccionado;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(listarUsuarios());
		registerForContextMenu(this.getListView());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.usuarios, menu);
		return true;
	}
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
                case R.id.nuevo_usuario:
                	nuevoUsuario();
                    break;
                }
        return super.onOptionsItemSelected(item);
    }
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
	  super.onCreateContextMenu(menu, v, menuInfo);
	  getMenuInflater().inflate(R.menu.menu_context_usuarios,menu);
	  menu.setHeaderTitle("MENU");
	  
	}
	
	@Override
    public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		int itemId = item.getItemId();
        switch (itemId) {
                case R.id.nuevo_usuario:
                	nuevoUsuario();
                	break;
                case R.id.borrar_usuario:
                	usuarioSeleccionado = listaUsuarios.get(info.position);
                	/*if(consultarFacturas()==0)
                		mostrarMensajeEliminar();
                	else
                		mostrarMensajeNoEliminar();*/
                    break;

                }
        return super.onOptionsItemSelected(item);
    }
	
	private void mostrarMensajeNoEliminar() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Error al Eliminar");
		builder.setMessage(usuarioSeleccionado.getLogin()+" Posee facturas");
		builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	  	        		
		           }
		       });
		
			// Create the AlertDialog
		builder.create().show();
		
	}

	/*private int consultarFacturas() {
		int resp = 0;
		Dao<Factura, Integer> facturaDao;
		try {
			facturaDao = getHelper().getFacturaDao();
			resp = facturaDao.queryBuilder().where()
	         .eq("cliente_id", usuarioSeleccionado.getId())
	         .query().size();
			 
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return resp;
	}*/

	private void nuevoUsuario(){
		Intent i = new Intent(this, UsuarioActivity.class );
		i.putExtra("usuario", new Usuario());
		i.putExtra("flag", "crear");
		startActivityForResult(i, request_code);
	}
	
	
	private void mostrarMensajeEliminar() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Esta seguro de eliminar a ?");
		builder.setMessage(usuarioSeleccionado.getLogin());
		builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        			eliminarUsuario();
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

	private void eliminarUsuario(){
			Dao<Usuario, Integer> usuarioDao;
			try {
				usuarioDao = getHelper().getUsuarioDao();
				usuarioDao.delete(usuarioSeleccionado);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			refrescarLista(); 
	}

	private ListAdapter listarUsuarios() {
		AdapterUsuario adapter = null;
		try {
			Dao<Usuario, Integer> usuarioDao = getHelper().getUsuarioDao();
			listaUsuarios = usuarioDao.queryForAll();
			adapter = new AdapterUsuario(this, listaUsuarios);
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
		Intent i = new Intent(this, UsuarioActivity.class );
		i.putExtra("usuario", listaUsuarios.get(position));
		i.putExtra("flag", "modificar");
		startActivityForResult(i, request_code);
		
	}
	
	 @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        // TODO Auto-generated method stub
	        if ((requestCode == request_code) && (resultCode == RESULT_OK)){
	           refrescarLista(); 
	        }
	    }

	private void refrescarLista() {
		setListAdapter(listarUsuarios());
		
	}

}
