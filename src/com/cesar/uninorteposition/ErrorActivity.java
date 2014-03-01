package com.cesar.uninorteposition;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.cesar.adapter.AdapterClienteList;
import com.cesar.adapter.AdapterError;
import com.cesar.adapter.AdapterFacturaSimple;
import com.cesar.adapter.AdapterGastos;
import com.cesar.adapter.AdapterRecaudo;
import com.cesar.adapter.AdapterVentas;
import com.cesar.aplicativo.NumberTextWatcher;
import com.cesar.bean.Cliente;
import com.cesar.bean.Error;
import com.cesar.bean.Factura;
import com.cesar.bean.Gasto;
import com.cesar.bean.Pago;
import com.cesar.bean.Usuario;
import com.cesar.db.DatabaseHelper;
import com.cesar.uninorteposition.PagoActivity.DatePickerFragment;
import com.j256.ormlite.dao.Dao;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ErrorActivity extends FragmentActivity {
	
	SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
	DecimalFormat format =  new  DecimalFormat ( "#,###.###" );
	private DatabaseHelper databaseHelper = null;
	private Usuario u;
	private Error e; 
	private EditText fecha;
	private EditText base;
	private EditText recaudo;
	private EditText ventas;
	private EditText gastos;
	private EditText efectivo;
	private EditText error;
	private String f;
	private ProgressDialog pDialog;
	private TareaListarPagos tarea;
	public AdapterRecaudo adapterRecaudo;
	public AdapterVentas adapterVentas;
	private TareaListarVentas tarea2;
	public AdapterGastos adapterGasto;
	private TareaListarGastos tarea3;
	private double value = 0;
	private DecimalFormat df = new DecimalFormat("#,###.##");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_error);
		e = (Error)getIntent().getParcelableExtra("error");
		u = (Usuario)getIntent().getParcelableExtra("usuario");
		f = getIntent().getStringExtra("flag");
		format.setDecimalSeparatorAlwaysShown(true);
		cargarDatos(e);
		
	}

	private void cargarDatos(Error e) {
		fecha = (EditText) findViewById(R.id.textFecha);
		base = (EditText) findViewById(R.id.textBase);
		recaudo = (EditText) findViewById(R.id.textRecaudo);
		ventas = (EditText) findViewById(R.id.textVentas);
		gastos = (EditText) findViewById(R.id.textGastos); 
		efectivo = (EditText) findViewById(R.id.textEfectivo);
		error = (EditText) findViewById(R.id.textError);
		fecha.setText(sd.format(e.getFecha()));
		base.setText(DecimalFormat.getInstance().format(e.getBase()));
		recaudo.setText(DecimalFormat.getInstance().format(e.getRecaudo()));
		ventas.setText(DecimalFormat.getInstance().format(e.getVentas()));
		gastos.setText(DecimalFormat.getInstance().format(e.getGastos()));
		efectivo.setText(DecimalFormat.getInstance().format(e.getEfectivo()));
		error.setText(DecimalFormat.getInstance().format(e.getError()));
				
		if(!u.getRoll().equalsIgnoreCase("Administrador"))
			desabilitarText();
		cargarListener();
	}

	private void cargarListener() {
		efectivo.addTextChangedListener(new NumberTextWatcher(efectivo) );
		base.addTextChangedListener(new NumberTextWatcher(base));
				
	}
	
	
	public void calcularError(){
		double b;
		double ef;
		double er;
		if (TextUtils.isEmpty(base.getText().toString().trim()))
    	    base.setText("0");
    	if (TextUtils.isEmpty(efectivo.getText().toString().trim()))
    	    efectivo.setText("0");	
    		
    		b = obtenerDouble(base.getText());
    		ef = obtenerDouble(efectivo.getText());
    		er = (ef+e.getGastos()+e.getVentas())-(b+e.getRecaudo());
    		error.setText(DecimalFormat.getInstance().format(er));
			e.setError(er);
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

	private void desabilitarText() {
		fecha.setEnabled(false);
		base.setEnabled(false);
		recaudo.setEnabled(false);
		ventas.setEnabled(false);
		gastos.setEnabled(false);
		//efectivo.setEnabled(false);
		error.setEnabled(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.error, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
                case R.id.menu_guardar:
                	guardarError();
                	break;
                }
        return super.onOptionsItemSelected(item);
    }

	private void guardarError() {
		try {
			e.setFecha(sd.parse(String.valueOf(fecha.getText())));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		e.setBase(obtenerDouble(base.getText()));
		e.setRecaudo(obtenerDouble(recaudo.getText()));
		e.setVentas(obtenerDouble(ventas.getText()));
		e.setGastos(obtenerDouble(gastos.getText()));
		e.setEfectivo(obtenerDouble(efectivo.getText()));
		e.setError(obtenerDouble(error.getText()));
		try {
			Dao<Error, Integer> simpleDao = getHelper().getErrorDao();
			if(f.equalsIgnoreCase("crear"))
				simpleDao.create(e);
			else
				simpleDao.update(e);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Intent data = new Intent();
        setResult(RESULT_OK, data);
        finish();
		
	}
	
	private DatabaseHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = DatabaseHelper.getHelper(this);
		}
		return databaseHelper;
	}
	
	public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
		
		private Date f;
		
		public DatePickerFragment(Date fecha) {
			this.f = fecha;
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
			
				 	f.setDate(seletedDay);
					f.setMonth(seletedMonth);
					f.setYear(seletedYear-1900);
					fecha.setText(sd.format(e.getFecha()));
					try {
						e.setFecha(sd.parse(String.valueOf(fecha.getText())));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		     }
		
	}
	
	public void fecha(View v){
		DialogFragment newFragment = new DatePickerFragment(e.getFecha());
		newFragment.show(getSupportFragmentManager(), "CALENDARIO");

	}
	
	public void gastos(View v){
		pDialog = new ProgressDialog(ErrorActivity.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Gastos...");
        pDialog.setCancelable(true);
        pDialog.setMax(100);
        tarea3 = new TareaListarGastos();
        tarea3.execute();
		
	}
	
	public void ventas(View v){
		pDialog = new ProgressDialog(ErrorActivity.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Ventas...");
        pDialog.setCancelable(true);
        pDialog.setMax(100);
        tarea2 = new TareaListarVentas();
        tarea2.execute();
		
	}
	
	public void recaudo(View v){
		pDialog = new ProgressDialog(ErrorActivity.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Pagos...");
        pDialog.setCancelable(true);
        pDialog.setMax(100);
        tarea = new TareaListarPagos();
        tarea.execute();
		
	}
	
	private class TareaListarGastos extends AsyncTask<Void, Integer, Boolean> {
		 
        @Override
        protected Boolean doInBackground(Void... params) {
        	
    		try {
    			Dao<Gasto, Integer> simpleDao = getHelper().getGastoDao();
    			try {
    				List<Gasto> listaGastos = simpleDao.queryBuilder().where().eq("fecha", sd.parse(sd.format(e.getFecha()))).query();
    				adapterGasto = new AdapterGastos(getContext(), listaGastos);
    			} catch (ParseException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			
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
            	
            	final Dialog dialog = new Dialog(ErrorActivity.this);
            	dialog.setContentView(R.layout.list_layout);
            	dialog.setTitle("GASTOS");
            	ListView listView = (ListView) dialog.findViewById(R.id.listView1);
            	listView.setAdapter(adapterGasto);
            	dialog.show();
            	
            	pDialog.dismiss();
            }
        }
 
        @Override
        protected void onCancelled() {
            Toast.makeText(ErrorActivity.this, "Tarea cancelada!",
                Toast.LENGTH_SHORT).show();
        }
    }
	
	private class TareaListarVentas extends AsyncTask<Void, Integer, Boolean> {
		 
        @Override
        protected Boolean doInBackground(Void... params) {
        	List<Factura> listaFacturas = null;
    		try {
    			Dao<Factura, Integer> simpleDao = getHelper().getFacturaDao();
    			try {
    				listaFacturas = simpleDao.queryBuilder().where().eq("fecha", sd.parse(sd.format(e.getFecha()))).query();
    				adapterVentas = new AdapterVentas(getContext(), listaFacturas);
    			} catch (ParseException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			
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
            	
            	final Dialog dialog = new Dialog(ErrorActivity.this);
            	dialog.setContentView(R.layout.list_layout);
            	dialog.setTitle("VENTAS");
            	ListView listView = (ListView) dialog.findViewById(R.id.listView1);
            	listView.setAdapter(adapterVentas);
            	dialog.show();
            	
            	pDialog.dismiss();
            }
        }
 
        @Override
        protected void onCancelled() {
            Toast.makeText(ErrorActivity.this, "Tarea cancelada!",
                Toast.LENGTH_SHORT).show();
        }
    }
	
	private class TareaListarPagos extends AsyncTask<Void, Integer, Boolean> {
		 
        @Override
        protected Boolean doInBackground(Void... params) {
        	try {
    			Dao<Pago, Integer> simpleDao = getHelper().getPagoDao();
    			try {
    				List<Pago> listaPago = simpleDao.queryBuilder().where().eq("fecha", sd.parse(sd.format(e.getFecha()))).query();
    				adapterRecaudo = new AdapterRecaudo(getContext(), listaPago);
    			} catch (ParseException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			
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
            	
            	final Dialog dialog = new Dialog(ErrorActivity.this);
            	dialog.setContentView(R.layout.list_layout);
            	dialog.setTitle("PAGOS");
            	ListView listView = (ListView) dialog.findViewById(R.id.listView1);
            	listView.setAdapter(adapterRecaudo);
            	dialog.show();
            	
            	pDialog.dismiss();
            }
        }
 
        @Override
        protected void onCancelled() {
            Toast.makeText(ErrorActivity.this, "Tarea cancelada!",
                Toast.LENGTH_SHORT).show();
        }
    }
	
	public Context getContext(){
		return this.getApplication();
		
	}
	
	public class NumberTextWatcher implements TextWatcher {
	    
	    private DecimalFormat df;
	    private DecimalFormat dfnd;
	    private boolean hasFractionalPart;
	    
	    private EditText et;
	    
	    public NumberTextWatcher(EditText et)
	    {
	    	df = new DecimalFormat("#,###.##");
	    	df.setDecimalSeparatorAlwaysShown(true);
	    	dfnd = new DecimalFormat("#,###");
	    	this.et = et;
	    	hasFractionalPart = false;
	    }
	    
	    @SuppressWarnings("unused")
		private static final String TAG = "NumberTextWatcher";

	    @Override
		public void afterTextChanged(Editable s)
	    {
		et.removeTextChangedListener(this);

		try {
		    int inilen, endlen;
		    inilen = et.getText().length();

		    String v = s.toString().replace(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()), "");
		    Number n = df.parse(v);
		    int cp = et.getSelectionStart();
		    if (hasFractionalPart) {
			et.setText(df.format(n));
		    } else {
			et.setText(dfnd.format(n));
		    }
		    endlen = et.getText().length();
		    int sel = (cp + (endlen - inilen));
		    if (sel > 0 && sel <= et.getText().length()) {
			et.setSelection(sel);
		    } else {
			// place cursor at the end?
			et. setSelection(et.getText().length() - 1);
		    }
		    
		} catch (NumberFormatException nfe) {
		    // do nothing?
		} catch (ParseException e) {
		    // do nothing?
		}

		et.addTextChangedListener(this);
	    }

	    @Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after)
	    {
	    }

	    @Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
	    {
	    	calcularError();
		if (s.toString().contains(String.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator())))
		    {
			hasFractionalPart = true;
		    } else {
		    hasFractionalPart = false;
		}
	    }

	}
	
	

}
