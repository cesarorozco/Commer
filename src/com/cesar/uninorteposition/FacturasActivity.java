package com.cesar.uninorteposition;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.cesar.adapter.AdapterClienteList;
import com.cesar.adapter.AdapterFactura;
import com.cesar.adapter.AdapterFacturaSimple;
import com.cesar.bean.Cliente;
import com.cesar.bean.Configuracion;
import com.cesar.bean.Error;
import com.cesar.bean.Factura;
import com.cesar.bean.Usuario;
import com.cesar.db.DatabaseHelper;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

public class FacturasActivity extends ListActivity{
	
	private DatabaseHelper databaseHelper = null;
	private List<Factura> listaFacturas; 
	private Factura facturaSeleccionada;
	private int request_code = 1;
	private int posicion = -1;
	private boolean moviendo = false;
	private Factura facturaMover;
	private AdapterFactura adapter;
	private boolean[] array;
	private List<Factura> facturasUpdate;
	private List<Factura> facturasToInsert;
	private List<Factura> facturasToNotInsert;
	private String[] bits;
	private Usuario u;
	private Parcelable state = null;
	private ProgressDialog pDialog;
	private TareaListarFacturas tarea;
	private TareaActualizarPosiciones tarea2;
	private Configuracion c;
	private String e;
	private ListView listView;
	private EditText edit;
	protected List<Cliente> listaClientesFilter;
	private TareaListarClientes tarea4;
	public List<Factura> listaFacturasFilter;
	public List<Cliente> listaClientes;
	public AdapterFacturaSimple adapterFactura;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		u = (Usuario)getIntent().getParcelableExtra("usuario");
		e = getIntent().getStringExtra("estado");
		c = (Configuracion)getIntent().getParcelableExtra("configuracion");
		listarFacturas();
		registerForContextMenu(this.getListView());
		bits = getResources().getStringArray(R.array.formas_pago);
		state = this.getListView().onSaveInstanceState();
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.facturas, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId)  {
                case R.id.nueva_fact:
                	posicion = 0;
                	state = this.getListView().onSaveInstanceState();
                	Calendar cal = Calendar.getInstance();
                	facturaSeleccionada = new Factura(); 
                	facturaSeleccionada.setFecha(c.getFecha());
                	cal.setTime(facturaSeleccionada.getFecha());
                	cal.add(Calendar.DATE, c.getPlazo());
                	facturaSeleccionada.setFechaVencimiento(cal.getTime());
                	facturaSeleccionada.setInteres(c.getIva());
                	facturaSeleccionada.setFormaPago(bits[c.getFormaPago()]);
                	facturaSeleccionada.setNext(calcularNext(facturaSeleccionada));
                	facturaSeleccionada.setEstado("activa");
                	if(listaFacturas!=null)
                		facturaSeleccionada.setIndice(listaFacturas.size());
                	else
                		facturaSeleccionada.setIndice(0);
                	verFactura(facturaSeleccionada,"crear");
                    break;
                case R.id.menu_refrescar:
                	listarFacturas();
                	break;
                case R.id.menu_buscar:
                	buscarFacturas();
                	break;
                }
        return super.onOptionsItemSelected(item);
    }
	
	private void buscarFacturas() {
		pDialog = new ProgressDialog(FacturasActivity.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(true);
        pDialog.setMax(100);
        tarea4 = new TareaListarClientes();
        tarea4.execute();
		
		
	}
	
	private class TareaListarClientes extends AsyncTask<Void, Integer, Boolean> {
		 
        @Override
        protected Boolean doInBackground(Void... params) {
           	listaFacturasFilter = new ArrayList<Factura >(listaFacturas);
           	adapterFactura = new AdapterFacturaSimple(getContext(), listaFacturasFilter );
		
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
            	
            	final Dialog dialog = new Dialog(FacturasActivity.this);
            	dialog.setContentView(R.layout.activity_detalle);
            	dialog.setTitle("CLIENTE");
            	listView = (ListView) dialog.findViewById(R.id.listView1);
            	listView.setAdapter(adapterFactura);
            	listView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                	getListView().setSelection(listaFacturasFilter.get(arg2).getIndice()-1);
                	dialog.dismiss();
                }
                
            
            });
            	edit = (EditText) dialog.findViewById(R.id.editText1);
            	edit.addTextChangedListener(new TextWatcher() {
            	    public void onTextChanged(CharSequence s, int start, int before, int count) {
            	    	listaFacturasFilter.clear();
            	        for(Factura f : listaFacturas){
            	        	if(f.getCliente().getNombres()==null)
            	        		f.getCliente().setNombres("");
            	        	if(f.getCliente().getApellidos()==null)
            	        		f.getCliente().setApellidos("");
            	        	if(f.getCliente().getDireccion()==null)
            	        		f.getCliente().setDireccion("");
            	        	 if(f.getCliente().getNombres().toLowerCase().indexOf(String.valueOf(s).toLowerCase())!=-1||f.getCliente().getApellidos().toLowerCase().indexOf(String.valueOf(s).toLowerCase())!=-1||f.getCliente().getDireccion().toLowerCase().indexOf(String.valueOf(s).toLowerCase())!=-1)
            	        		 listaFacturasFilter.add(f);
            	        }	
            	        adapterFactura = new AdapterFacturaSimple(getContext(), listaFacturasFilter );
            	        listView.setAdapter(adapterFactura);
            	    }

            	    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            	    public void afterTextChanged(Editable s) {}
            	});
            		
            	dialog.show();
            	
            	pDialog.dismiss();
            }
        }
 
        @Override
        protected void onCancelled() {
            Toast.makeText(FacturasActivity.this, "Tarea cancelada!",
                Toast.LENGTH_SHORT).show();
        }
    }

	private Date calcularNext(Factura f) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(c.getFecha());
		if(f.getFormaPago().equalsIgnoreCase("DIARIO"))
    	  cal.add(Calendar.DATE, 1);
		if(f.getFormaPago().equalsIgnoreCase("DIA POR MEDIO"))
	    	  cal.add(Calendar.DATE, 2);
		if(f.getFormaPago().equalsIgnoreCase("DOS VECES POR SEMANA"))
	    	  cal.add(Calendar.DATE, 3);
		if(f.getFormaPago().equalsIgnoreCase("SEMANAL"))
	    	  cal.add(Calendar.DATE, 7);
		if(f.getFormaPago().equalsIgnoreCase("QUINCENAL"))
	    	  cal.add(Calendar.DATE, 15);
		if(f.getFormaPago().equalsIgnoreCase("MENSUAL"))
	    	  cal.add(Calendar.DATE, 30);
		
		for(int u=0 ; u<7; u++)
		   if(c.getDias().charAt(u)=='1')	
			if(cal.get(Calendar.DAY_OF_WEEK)==u+1)
			   break;	
			else
			   cal.add(Calendar.DATE, 1);

		return cal.getTime();
	}

	private void listarFacturas() {
		
		pDialog = new ProgressDialog(FacturasActivity.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(true);
        pDialog.setMax(100);
 
        tarea = new TareaListarFacturas();
        tarea.execute();
        		
	}
	
	private DatabaseHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = DatabaseHelper.getHelper(this);
		}
		return databaseHelper;
	}
	
	public void onListItemClick(ListView parent,View v,int position, long id){
		facturaSeleccionada = listaFacturas.get(position);
		verFactura(facturaSeleccionada,"modificar");
		posicion = -1;
		state = this.getListView().onSaveInstanceState();
	}
	
	public void verFactura(Factura f, String modo){
		Intent i = new Intent(this, FacturaActivity.class );
		i.putExtra("factura", f);
		i.putExtra("flag", modo);
		i.putExtra("estado", e);
		i.putExtra("usuario", this.u);
		i.putExtra("configuracion", this.c);
		startActivityForResult(i, request_code);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
	  super.onCreateContextMenu(menu, v, menuInfo);
	  if (moviendo) {
		getMenuInflater().inflate(R.menu.menu_context_factura2, menu);
	  }else
		getMenuInflater().inflate(R.menu.menu_context_factura, menu);
	
	  menu.setHeaderTitle("Menu");
	  
	}
	
	@Override
    public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		posicion = info.position;
		int itemId = item.getItemId();
        switch (itemId) {
                case R.id.nueva_factura:
                	facturaSeleccionada = listaFacturas.get(info.position).clonar();
                	facturaSeleccionada.setIndice(info.position+1);
                	nuevaFactura();
                   	break;
                case R.id.borrar_factura:
                	state = this.getListView().onSaveInstanceState();
                	facturaSeleccionada = listaFacturas.get(info.position);
                	if(facturaSeleccionada.getPagos().isEmpty())
                		mostrarMensajeEliminar();
                	else
                		mostrarMensajeNoEliminar();
                    break;
                case R.id.mover_factura:
                	state = this.getListView().onSaveInstanceState();
                	verDialog();
                   	break;    

    			case R.id.soltar_factura:
    				state = this.getListView().onSaveInstanceState();
    				moviendo = false;
    				insertarFacturas(info.position);
    				break;    

		}
        return super.onOptionsItemSelected(item);
    }
	
	private void nuevaFactura() {
		state = this.getListView().onSaveInstanceState();
       	Calendar cal = Calendar.getInstance();
    	facturaSeleccionada.setFecha(c.getFecha());
    	cal.setTime(facturaSeleccionada.getFecha());
    	cal.add(Calendar.DATE, c.getPlazo());
    	facturaSeleccionada.setFechaVencimiento(cal.getTime());
    	facturaSeleccionada.setNext(calcularNext(facturaSeleccionada));
       	facturaSeleccionada.setEstado("activa");
       	verFactura(facturaSeleccionada,"crear");
		
	}

	private void insertarFacturas(int position) {
		int indice = position+1;
		for(Factura f : facturasToInsert){
			facturasUpdate.add(indice,f);
		}	
		listaFacturas = new LinkedList(facturasUpdate);
	    adapter = new AdapterFactura(getActivity(), listaFacturas, c.getFecha());
	    setListAdapter(adapter);
	    if(state!=null)
			this.getListView().onRestoreInstanceState(state);
	    actualizarposiciones(0);
	}
	
	private void verDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String[] nombres = obtenerNombres();
		array = new boolean[listaFacturas.size()];
		array[posicion] = true;
	    builder.setTitle("FACTURAS")
	    // Specify the list array, the items to be selected by default (null for none),
	    // and the listener through which to receive callbacks when items are selected
	           .setMultiChoiceItems(nombres, array,
	                      new DialogInterface.OnMultiChoiceClickListener() {
	     	@Override
	               public void onClick(DialogInterface dialog, int which,
	                       boolean isChecked) {
	                   if (isChecked) {
	                       // If the user checked the item, add it to the selected items
	                       array[which]=true;
	                   } else{
	                       // Else, if the item is already in the array, remove it 
	                	   array[which]=false;
	                   }
	               }
	           })
	    // Set the action buttons
	           .setPositiveButton("ok", new DialogInterface.OnClickListener() {
	               
				@Override
	               public void onClick(DialogInterface dialog, int id) {
						moviendo = true;
	            	    facturasUpdate = new LinkedList(listaFacturas);
	            	    facturasToInsert = new LinkedList();
	            	    facturasToNotInsert = new LinkedList();
	            	    for(int y = 0;y<array.length;y++){
	            		   if(array[y]==true){
	            			   facturasToInsert.add(facturasUpdate.get(y));
	            			   
	            		   }else
	            			   facturasToNotInsert.add(facturasUpdate.get(y)) ;
	            	    }
	            	    facturasUpdate = facturasToNotInsert;
	            	    listaFacturas = new LinkedList(facturasUpdate);
	            	    adapter = new AdapterFactura(getActivity(), listaFacturas, c.getFecha());
	            	    setListAdapter(adapter);
	               }

				      })
	           .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	             
	               }
	           });

	    builder.create().show();
		
	}

	private Context getActivity() {
		
		return this;
	}

	private String[] obtenerNombres() {
		String[] nom = new String[listaFacturas.size()];
		for(int u = 0;u<listaFacturas.size();u++){
			nom[u]=listaFacturas.get(u).getCliente().getNombres()+" "+
					listaFacturas.get(u).getCliente().getApellidos();
		}	
		return nom;
	}

	private void mostrarMensajeEliminar() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("ESTA SEGURO DE ELIMINAR ESTA FACTURA ?");
		builder.setMessage(facturaSeleccionada.getCliente().getNombres());
		builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	  eliminarFactura();		        		
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
	
	private void mostrarMensajeNoEliminar() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("ERROR AL ELIMINAR");
		builder.setMessage("ESTA FACTURA POSEE PAGOS");
		builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	  	        		
		           }
		       });
		
			// Create the AlertDialog
		builder.create().show();
		
	}
	
	private void eliminarFactura() {
		Dao<Factura, Integer> facturaDao;
		try {
			facturaDao = getHelper().getFacturaDao();
			facturaDao.delete(facturaSeleccionada);
			listarFacturas();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
        // TODO Auto-generated method stub
        if ((requestCode == request_code) && (resultCode == RESULT_OK)){
        	if (posicion!=-1){
        		actualizarposiciones(posicion+1);
        	}else
        		listarFacturas();
        }else{
           	if((requestCode == request_code) && (resultCode == 2))
        		nuevaFactura();
        }
        	
    }

	private void actualizarposiciones(int p) {
		pDialog = new ProgressDialog(FacturasActivity.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Actualizando...");
        pDialog.setCancelable(true);
        pDialog.setMax(100);
        tarea2 = new TareaActualizarPosiciones();
        tarea2.setInicio(p);
        tarea2.execute();
		
	}
		
	private class TareaListarFacturas extends AsyncTask<Void, Integer, Boolean> {
		 
        @Override
        protected Boolean doInBackground(Void... params) {
 
        	try { 
    			Dao<Factura, Integer> simpleDao = getHelper().getFacturaDao();
    			QueryBuilder<Factura, Integer> qb = simpleDao.queryBuilder();
    			qb.where().eq("estado", e);
    		    listaFacturas = qb.orderBy("indice", true).query();
    		   	adapter = new AdapterFactura(getContext(), listaFacturas , c.getFecha());
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
            	if(state!=null)
           		   getListView().onRestoreInstanceState(state);
                pDialog.dismiss();
            }
        }
 
        @Override
        protected void onCancelled() {
            Toast.makeText(FacturasActivity.this, "Tarea cancelada!",
                Toast.LENGTH_SHORT).show();
        }
    }
	
	public class TareaActualizarPosiciones extends AsyncTask<Void, Integer, Boolean> {
		 
        private int inicio = 0;
        
        public void setInicio(int i){
        	inicio = i;
        }

		@Override
        protected Boolean doInBackground(Void... params) {
 
        	Dao<Factura, Integer> facturaDao;
    		try {
    			facturaDao = getHelper().getFacturaDao();
    			for(int i=inicio;i<listaFacturas.size();i++){
    				Factura fac = listaFacturas.get(i);
    				fac.setIndice(i+1);
    				facturaDao.update(fac);
    			}
    		} catch (SQLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		};
 
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
              listarFacturas();
            }
        }
 
        @Override
        protected void onCancelled() {
            Toast.makeText(FacturasActivity.this, "Tarea cancelada!",
                Toast.LENGTH_SHORT).show();
        }
    }
	
	public Context getContext(){
		return this.getApplication();
		
	}
	
}
