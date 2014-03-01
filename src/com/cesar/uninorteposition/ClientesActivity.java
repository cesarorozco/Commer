package com.cesar.uninorteposition;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.cesar.adapter.AdapterCliente;
import com.cesar.adapter.AdapterClienteList;
import com.cesar.adapter.AdapterFactura;
import com.cesar.adapter.AdapterFacturaSimple;
import com.cesar.bean.Cliente;
import com.cesar.bean.Factura;
import com.cesar.bean.Pago;
import com.cesar.db.DatabaseHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

public class ClientesActivity extends ListActivity {
	
	private DatabaseHelper databaseHelper = null;
	private List<Cliente> listaClientes;
	private int request_code = 1;
	private Cliente clienteSeleccionado;
	private ProgressDialog pDialog;
	private TareaListarClientes tarea;
	public  AdapterCliente adapter;
	private ListarClientes tarea2;
	public ArrayList<Cliente> listaClientesFilter;
	public AdapterClienteList adapterCliente;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.listarClientes();
		registerForContextMenu(this.getListView());
		listaClientesFilter = new ArrayList<Cliente>();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.clientes, menu);
		return true;
	}
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
                case R.id.nuevo_cliente:
                	nuevoCliente();
                    break;
                case R.id.menu_buscar:
                	buscarClientes();
                    break;    
                }
        return super.onOptionsItemSelected(item);
    }
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
	  super.onCreateContextMenu(menu, v, menuInfo);
	  getMenuInflater().inflate(R.menu.menu_context_clientes,menu);
	  menu.setHeaderTitle("MENU");
	  
	}
	
	@Override
    public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		int itemId = item.getItemId();
        switch (itemId) {
                case R.id.nuevo_cliente:
                	nuevoCliente();
                	break;
                case R.id.borrar_cliente:
                	clienteSeleccionado = listaClientes.get(info.position);
                	if(consultarFacturas()==0)
                		mostrarMensajeEliminar();
                	else
                		mostrarMensajeNoEliminar();
                    break;

                }
        return super.onOptionsItemSelected(item);
    }
	
	private void mostrarMensajeNoEliminar() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Error al Eliminar");
		builder.setMessage(clienteSeleccionado.getNombres()+" Posee facturas");
		builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	  	        		
		           }
		       });
		
			// Create the AlertDialog
		builder.create().show();
		
	}

	private int consultarFacturas() {
		int resp = 0;
		Dao<Factura, Integer> facturaDao;
		try {
			facturaDao = getHelper().getFacturaDao();
			resp = facturaDao.queryBuilder().where()
	         .eq("cliente_id", clienteSeleccionado.getId())
	         .query().size();
			 
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return resp;
	}

	private void nuevoCliente(){
		Intent i = new Intent(this, ClienteActivity.class );
		i.putExtra("cliente", new Cliente());
		i.putExtra("flag", "crear");
		startActivityForResult(i, request_code);
	}
	
	
	private void mostrarMensajeEliminar() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Esta seguro de eliminar a ?");
		builder.setMessage(clienteSeleccionado.getNombres());
		builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        			eliminarCliente();
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

	private void eliminarCliente(){
			Dao<Cliente, Integer> clienteDao;
			try {
				clienteDao = getHelper().getClienteDao();
				clienteDao.delete(clienteSeleccionado);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			listarClientes();
	}

	private void listarClientes() {
		pDialog = new ProgressDialog(ClientesActivity.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Procesando...");
        pDialog.setCancelable(true);
        pDialog.setMax(100);
        tarea = new TareaListarClientes();
        tarea.execute();
	}
	
	private DatabaseHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = DatabaseHelper.getHelper(this);
		}
		return databaseHelper;
	}
	
	public void onListItemClick(ListView parent,View v,int position, long id){
		Intent i = new Intent(this, ClienteActivity.class );
		i.putExtra("cliente", listaClientes.get(position));
		i.putExtra("flag", "modificar");
		startActivityForResult(i, request_code);
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        // TODO Auto-generated method stub
	        if ((requestCode == request_code) && (resultCode == RESULT_OK)){
	        	listarClientes();
	        }
	    }

	private class TareaListarClientes extends AsyncTask<Void, Integer, Boolean> {
		 
        @Override
        protected Boolean doInBackground(Void... params) {
 
        	adapter = null;
    		try {
    			Dao<Cliente, Integer> clienteDao = getHelper().getClienteDao();
    			listaClientes = clienteDao.queryBuilder().orderBy("nombres", true).query();
    			listaClientesFilter = new ArrayList<Cliente>(listaClientes);
    			adapter = new AdapterCliente(getContext(), listaClientes);
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
            Toast.makeText(ClientesActivity.this, "Tarea cancelada!",
                Toast.LENGTH_SHORT).show();
        }
    }
	
	public Context getContext(){
		return this.getApplication();
		
	}
	
	private void buscarClientes() {
		pDialog = new ProgressDialog(ClientesActivity.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(true);
        pDialog.setMax(100);
        tarea2 = new ListarClientes();
        tarea2.execute();
		
		
	}
	
	private class ListarClientes extends AsyncTask<Void, Integer, Boolean> {
		 
        private ListView listView;

		@Override
        protected Boolean doInBackground(Void... params) {
           	listaClientesFilter = new ArrayList<Cliente >(listaClientes);
           	adapterCliente = new AdapterClienteList(getContext(), listaClientesFilter );
		
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
            	
            	final Dialog dialog = new Dialog(ClientesActivity.this);
            	dialog.setContentView(R.layout.activity_detalle);
            	dialog.setTitle("CLIENTE");
            	listView = (ListView) dialog.findViewById(R.id.listView1);
            	listView.setAdapter(adapterCliente);
            	listView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                	getListView().setSelection(buscarPosicion(listaClientesFilter.get(arg2)));
                	dialog.dismiss();
                }
                
            
            });
            	EditText edit = (EditText) dialog.findViewById(R.id.editText1);
            	edit.addTextChangedListener(new TextWatcher() {
            	    public void onTextChanged(CharSequence s, int start, int before, int count) {
            	    	listaClientesFilter.clear();
            	        for(Cliente c : listaClientes){
            	        	if(c.getNombres()==null)
            	        		c.setNombres("");
            	        	if(c.getApellidos()==null)
            	        		c.setApellidos("");
            	        	if(c.getDireccion()==null)
            	        		c.setDireccion("");
            	        	 if(c.getNombres().toLowerCase().indexOf(String.valueOf(s).toLowerCase())!=-1||c.getApellidos().toLowerCase().indexOf(String.valueOf(s).toLowerCase())!=-1||c.getDireccion().toLowerCase().indexOf(String.valueOf(s).toLowerCase())!=-1)
            	        		 listaClientesFilter.add(c);
            	        }	
            	        adapterCliente = new AdapterClienteList(getContext(), listaClientesFilter );
            	        listView.setAdapter(adapterCliente);
            	    }

            	    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            	    public void afterTextChanged(Editable s) {}
            	});
            		
            	dialog.show();
            	
            	pDialog.dismiss();
            }
        }
 
        public int buscarPosicion(Cliente cliente) {
			int i = 0;
			for(int u =0;u<listaClientes.size();u++){
				if(listaClientes.get(u).getId()==cliente.getId()){
					i=u;
					break;
				}	
			}	
			return i;
		}

		@Override
        protected void onCancelled() {
            Toast.makeText(ClientesActivity.this, "Tarea cancelada!",
                Toast.LENGTH_SHORT).show();
        }
    }
	

}
