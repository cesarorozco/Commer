 package com.cesar.uninorteposition;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.cesar.adapter.AdapterCliente;
import com.cesar.adapter.AdapterClienteList;
import com.cesar.adapter.AdapterPagoList;
import com.cesar.aplicativo.NumberTextWatcher;
import com.cesar.bean.Cliente;
import com.cesar.bean.Configuracion;
import com.cesar.bean.Factura;
import com.cesar.bean.Pago;
import com.cesar.bean.Revision;
import com.cesar.bean.Usuario;
import com.cesar.db.DatabaseHelper;
import com.j256.ormlite.dao.Dao;

public class FacturaActivity extends FragmentActivity {
	
	private DatabaseHelper databaseHelper = null;
	private Factura factura;
	private TextView cliente;
	private TextView fecha;
	private TextView vence;
	private TextView next;
	private EditText monto;
	private EditText total;
	private EditText cuota;
	private TextView interes;
	private TextView formaPago; 
	private String f;
	private int request_code = 1;
	private List<Cliente> listaClientes;
	private List<Cliente> listaClientesFilter;
	private DecimalFormat df = new DecimalFormat("#,###.##");
	SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
	private Revision revision;
	private Usuario u;
	private ProgressDialog pDialog;
	private TareaListarClientes tarea;
	public AdapterClienteList adapter;
	private String e;
	private Configuracion c;
	public EditText edit;
	public ListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.factura_layout);
		u = (Usuario)getIntent().getParcelableExtra("usuario");
		f = getIntent().getStringExtra("flag");
		e = getIntent().getStringExtra("estado");
		c = (Configuracion)getIntent().getParcelableExtra("configuracion");
		if(!f.equalsIgnoreCase("revision")){
			factura = (Factura)getIntent().getParcelableExtra("factura");
			cargarDatos(factura);
		}else{
			revision = (Revision)getIntent().getParcelableExtra("revision");
			factura = revision.getFactura();
			cargarDatos(factura);
		}
		listaClientesFilter = new ArrayList<Cliente>();
	}
	
	private void cargarDatos(Factura factura) {
		cliente = (TextView) findViewById(R.id.editTextCliente);
		fecha = (TextView) findViewById(R.id.editTextFecha);
		vence = (TextView) findViewById(R.id.editTextVence);
		next = (TextView) findViewById(R.id.editTextNext);
		monto = (EditText) findViewById(R.id.editTextMonto);
		total = (EditText) findViewById(R.id.editTextTotal);
		cuota = (EditText) findViewById(R.id.editTextCuota);
		interes = (TextView) findViewById(R.id.editTextInteres);
		formaPago = (TextView) findViewById(R.id.editTextFormaPago);
		if(factura.getCliente()!=null)
		   cliente.setText(factura.getCliente().getNombres()+" "+factura.getCliente().getApellidos());
		fecha.setText(sd.format(factura.getFecha()));
		vence.setText(sd.format(factura.getFechaVencimiento()));
		if(factura.getNext()!=null)
		   next.setText(sd.format(factura.getNext()));
		monto.setText(DecimalFormat.getInstance().format(factura.getMonto()));
		monto.addTextChangedListener(new NumberTextWatcher(monto));
		total.setText(DecimalFormat.getInstance().format(factura.getTotal()));
		total.addTextChangedListener(new NumberTextWatcher(total));
		cuota.setText(DecimalFormat.getInstance().format(factura.getCuota()));
		cuota.addTextChangedListener(new NumberTextWatcher(cuota));
		interes.setText(String.valueOf(factura.getInteres()));
		formaPago.setText(factura.getFormaPago());
		if(f.equalsIgnoreCase("revision"))
		   deshabilitarCampos();
		if(e.equalsIgnoreCase("cancelada"))
		   deshabilitarCampos();
		if(u.getRoll().equalsIgnoreCase("COBRADOR")){
			deshabilitarCampos();
			if(sd.format(factura.getFecha()).equalsIgnoreCase(sd.format(c.getFecha()))){
				cliente.setEnabled(true);
				vence.setEnabled(true);
				monto.setEnabled(true);
				total.setEnabled(true);
			}
			next.setEnabled(true);
			cuota.setEnabled(true);
			interes.setEnabled(true);
			formaPago.setEnabled(true);
		}	
		
	}

	private void deshabilitarCampos() {
		cliente.setEnabled(false);
		fecha.setEnabled(false);
		vence.setEnabled(false);
		next.setEnabled(false);
		monto.setEnabled(false);
		total.setEnabled(false);
		cuota.setEnabled(false);
		interes.setEnabled(false);
		formaPago.setEnabled(false);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		if(f.equalsIgnoreCase("revision")){
			getMenuInflater().inflate(R.menu.revision, menu);
		}else
			if(e.equalsIgnoreCase("activa")) 
			   getMenuInflater().inflate(R.menu.factura, menu);
			else
			   getMenuInflater().inflate(R.menu.cancelada, menu);  
		
		return true;
	}
	
	private DatabaseHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = DatabaseHelper.getHelper(this);
		}
		return databaseHelper;
	}
	
	public void listarClientes(View v){
		
		listarClientes();
		
	}
	public void fecha(View v){
		DialogFragment newFragment = new DatePickerFragment(factura.getFecha());
	    newFragment.show(getSupportFragmentManager(), "CALENDARIO");
		
	}
	public void vence(View v){
		DialogFragment newFragment = new DatePickerFragment(factura.getFechaVencimiento());
	    newFragment.show(getSupportFragmentManager(), "CALENDARIO");

	}
	public void next(View v){
		DialogFragment newFragment = new DatePickerFragment(factura.getNext());
	    newFragment.show(getSupportFragmentManager(), "CALENDARIO");

	}
	
	protected void modificarCliente(int item) {
		factura.setCliente(listaClientesFilter.get(item));
		cliente.setText(factura.getCliente().getNombres()+" "+factura.getCliente().getApellidos());
	}
	
	public void formaPago(View v) {
		 final CharSequence[] items = {
	                "DIARIO", "DIA POR MEDIO", "DOS VECES POR SEMANA","SEMANAL","QUINCENAL","MENSUAL"
	        };

	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setTitle("FORMAS DE PAGO");
	        builder.setItems(items, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int item) {
	                // Do something with the selection
	                factura.setFormaPago(items[item].toString());
	                formaPago.setText(factura.getFormaPago());
	            }
	        });
	        AlertDialog alert = builder.create();
	        alert.show();	
	 }
	
	private void listarClientes() {
		pDialog = new ProgressDialog(FacturaActivity.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Procesando...");
        pDialog.setCancelable(true);
        pDialog.setMax(100);
        tarea = new TareaListarClientes();
        tarea.execute();
	}
	
	public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
		
		private Date fecha;
		
		public DatePickerFragment(Date fecha) {
			this.fecha = fecha;
		}
		
		private boolean fired = false;

	    public void resetFired(){
	        fired = false;
	    }

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int seletedYear, int seletedMonth, int seletedDay) {
			    	fecha.setDate(seletedDay);
					fecha.setMonth(seletedMonth);
					fecha.setYear(seletedYear-1900);
					modificarFecha();
		            Log.i("DatePicker", "Double fire occured.");
		            return;
		}
		
	}

	public void modificarFecha() {
		fecha.setText(sd.format(factura.getFecha()));
		vence.setText(sd.format(factura.getFechaVencimiento()));
		next.setText(sd.format(factura.getNext()));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	        int itemId = item.getItemId();

	        switch (itemId) {
	                case R.id.ver_pagos:
	                	verPagos();
	                    break;
	                case R.id.saldo_factura:
	                	saldo();
	                	break;
	                case R.id.guardar_factura:
	                	guardar();
	                	break;

	    			case R.id.menu_revision:
	    				mostrarRevision();
	    				break;
	    			case R.id.activar_factura:
	    				actualizarfactura();
	    				break;
	          }
	    	return super.onOptionsItemSelected(item);
	    }
	 
	private void verPagos() {
		
		if (f.equalsIgnoreCase("crear")) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("NECESITA GUARDAR");
			builder.setMessage("ESTAS SEGURO DE GUARDAR ESTA FACTURA");
			builder.setPositiveButton("ACEPTAR",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							guardar();
						}
					});
			// Create the AlertDialog
			builder.create().show();
			
		}else{
			Intent i = new Intent(getContext(), PagosActivity.class);
			i .putExtra("factura", factura);
			i.putExtra("configuracion", c);
			i.putExtra("usuario", this.u);
			startActivityForResult(i, request_code);
		}
			
		
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if ((requestCode == request_code) && (resultCode == RESULT_OK)){
        	Intent data1 = new Intent();
	        setResult(2, data1);
	        finish();	
        }else{
           	if((requestCode == request_code) && (resultCode == 2)){
           		Cliente clien = (Cliente)data.getParcelableExtra("cliente");
           		factura.setCliente(clien);
        		cliente.setText(clien.getNombres()+" "+clien.getApellidos());

           	}else
           		if((requestCode == request_code) && (resultCode == 3)){
           			this.finish();
           		}		
        }
        	
    }

	private void saldo() {
		
		List<Pago> pagos=listarPagos();
		double totalAbono=0.0;
		if (pagos.size()!=0) {
			for (Pago p : pagos)
				totalAbono = totalAbono + p.getValor();
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("SALDO");
		builder.setMessage(DecimalFormat.getInstance().format(factura.getTotal()-totalAbono));
		builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	  		        		
		           }
		       });
		// Create the AlertDialog
		builder.create().show();
		
	}
	
	private List<Pago> listarPagos() {
		List<Pago> listaPagos = null;
		try {
			Dao<Pago, Integer> simpleDao = getHelper().getPagoDao();
			listaPagos = simpleDao.queryBuilder().where()
			         .eq(Pago.FACTURA_ID, factura.getId())
			         .query();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listaPagos;
		
	}

	private void mostrarRevision() {
		 final CharSequence[] items = {
	                "IGUAL", "A FAVOR", "EN CONTRA","NO ENCONTRADO"
	        };

	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setTitle("DETALLE DE REVISION");
	        builder.setItems(items, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int item) {
	                // Do something with the selection
	                revision.setDetalle(items[item].toString());
	                
	            }
	        });
	        AlertDialog alert = builder.create();
	        alert.show();	
		
	}

	public void guardar(){
			
			try {
				factura.setFecha(new Date((sd.parse(String.valueOf(fecha.getText()))).getTime()));
				factura.setFechaVencimiento(new Date((sd.parse(String.valueOf(vence.getText()))).getTime()));
				factura.setNext(new Date((sd.parse(String.valueOf(next.getText()))).getTime()));
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			factura.setMonto(obtenerDouble(monto.getText()));
			factura.setTotal(obtenerDouble(total.getText()));
			factura.setCuota(obtenerDouble(cuota.getText()));
			factura.setInteres((Double.parseDouble(String.valueOf(interes.getText()))));
			factura.setFormaPago(((String.valueOf(formaPago.getText()))));
			try {
				Dao<Factura, Integer> facturaDao = getHelper().getFacturaDao();
				Dao<Revision, Integer> revisionDao = getHelper().getRevisionDao();
				if(f.equalsIgnoreCase("crear"))
					facturaDao.create(factura);
				else
				   if(f.equalsIgnoreCase("modificar"))	
					 facturaDao.update(factura);
				   else{
					   revision.setFactura(factura);
					   revisionDao.update(revision);
				   }   
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Intent data = new Intent();
	        setResult(RESULT_OK, data);
	        finish();
			
		}
	
	private class TareaListarClientes extends AsyncTask<Void, Integer, Boolean> {
		 
        private Cliente clienteNuevo;

		@Override
        protected Boolean doInBackground(Void... params) {
 
        	adapter = null;
        	clienteNuevo=new Cliente();
        	clienteNuevo.setNombres("NUEVO CLIENTE");
        	clienteNuevo.setApellidos("");
        	clienteNuevo.setDireccion("");
        	
        	
        	try {
    			Dao<Cliente, Integer> clienteDao = getHelper().getClienteDao();
    			listaClientes = clienteDao.queryBuilder().orderBy("nombres", true).query();
    			listaClientes.add(0, clienteNuevo);
    			listaClientesFilter = new ArrayList<Cliente>(listaClientes);
       			adapter = new AdapterClienteList(getContext(), listaClientes);
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
            	
            	final Dialog dialog = new Dialog(FacturaActivity.this);
            	dialog.setContentView(R.layout.activity_detalle);
            	dialog.setTitle("CLIENTE");
            	listView = (ListView) dialog.findViewById(R.id.listView1);
            	listView.setAdapter(adapter);
            	listView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                	
                	if (listaClientesFilter.get(arg2).getNombres().equalsIgnoreCase("NUEVO CLIENTE")) {
                		nuevoCliente();
					}else
						modificarCliente(arg2); 
						
					dialog.dismiss();
                }
                
            
            });
            	edit = (EditText) dialog.findViewById(R.id.editText1);
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
            	        adapter = new AdapterClienteList(getContext(), listaClientesFilter);
            	        listView.setAdapter(adapter);
            	    }

            	    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            	    public void afterTextChanged(Editable s) {}
            	});
            		
            dialog.show();
            	
            	pDialog.dismiss();
            }
        }
 
        protected void onCancelled() {
            Toast.makeText(FacturaActivity.this, "Tarea cancelada!",
                Toast.LENGTH_SHORT).show();
        }
    }
	
	public void nuevoCliente() {
    	Intent i = new Intent(this, ClienteActivity.class );
		i.putExtra("cliente", new Cliente());
		i.putExtra("flag", "nuevo");
		startActivityForResult(i, request_code);
		
	}
	
	public Context getContext(){
		return this.getApplication();
		
	}
	
	public void actualizarfactura(){
		
		try {
			Dao<Factura, Integer> facturaDao = getHelper().getFacturaDao();
			factura.setEstado("activa");
			facturaDao.update(factura);
			Toast.makeText(FacturaActivity.this, "Factura activada!",
	                Toast.LENGTH_SHORT).show();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private double obtenerDouble(Editable text) {
		
		double n = 0;
		String v = text.toString().replace(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()), "");
	    try {
			n = df.parse(v).doubleValue();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return n;
	}

}
