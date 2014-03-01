package com.cesar.uninorteposition;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.cesar.bean.Configuracion;
import com.cesar.bean.Usuario;
import com.cesar.db.DatabaseHelper;
import com.cesar.uninorteposition.FacturaActivity.DatePickerFragment;
import com.j256.ormlite.dao.Dao;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.DialogFragment;

public class ConfiguracionActivity extends FragmentActivity {
	
	private DatabaseHelper databaseHelper = null;
	private TextView fecha;
	private TextView iva;
	private TextView dias;
	private TextView formaPa;
	private TextView plazo;
	private Configuracion configuracion;
	SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
	String cadena="";
	boolean[] array;
	private String[] bits;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configuracion);
		fecha = (TextView) findViewById(R.id.textDate);
		dias = (TextView) findViewById(R.id.textDias);
		iva = (TextView) findViewById(R.id.textIva);
		formaPa = (TextView) findViewById(R.id.textForma);
		plazo = (TextView) findViewById(R.id.textPlazo);
		this.buscarConfiguracion();
		cargarDatos();
	}

	private void cargarDatos() {
		bits = getResources().getStringArray(R.array.formas_pago);
		fecha.setText(sd.format(configuracion.getFecha()));
		dias.setText(toStringDias());
		iva.setText(String.valueOf(configuracion.getIva()));
		formaPa.setText(bits[configuracion.getFormaPago()]);
		plazo.setText(String.valueOf(configuracion.getPlazo()));
	}

	private CharSequence toStringDias() {
		String texto="";
		String cadena = configuracion.getDias();
		if(cadena.charAt(0)=='1')
			   texto="D ";
		if(cadena.charAt(1)=='1')
			   texto=texto+"L ";
		if(cadena.charAt(2)=='1')
			   texto=texto+"M ";
		if(cadena.charAt(3)=='1')
			   texto=texto+"Mie ";
		if(cadena.charAt(4)=='1')
			   texto=texto+"J ";
		if(cadena.charAt(5)=='1')
			   texto=texto+"V ";
		if(cadena.charAt(6)=='1')
			   texto=texto+"S ";
		
		return texto;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.configuracion, menu);
		return true;
	}
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
                case R.id.guardar_conf:
                	guardarConfiguracion();
                    break;
                }
        return super.onOptionsItemSelected(item);
    }
	
	private void guardarConfiguracion() {
		try {
			configuracion.setIva(Double.parseDouble(String.valueOf(iva.getText())));
			configuracion.setPlazo(Integer.parseInt((String.valueOf(plazo.getText()))));
			Dao<Configuracion, Integer> configuracionDao = getHelper().getConfiguracionDao();
			configuracionDao.update(configuracion);
			Intent data = new Intent();
            setResult(RESULT_OK, data);
            finish();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void recaudo(View v) {
		array =  obtenerDias();
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    // Set the dialog title
	    builder.setTitle("Dias")
	    // Specify the list array, the items to be selected by default (null for none),
	    // and the listener through which to receive callbacks when items are selected
	           .setMultiChoiceItems(R.array.dias_semana, array,
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
	            	   cadena = "";
	            	   for(boolean v:array)
	            		   if(v==true)
	            			  cadena=cadena.concat("1");
	            		   else
	            			  cadena=cadena.concat("0"); 
	            	   	
	                   configuracion.setDias(cadena);
	                   dias.setText(toStringDias());
	               }
	           })
	           .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	             
	               }
	           });

	    builder.create().show();
	}

	private boolean[] obtenerDias() {
      	boolean[] array = new  boolean[ 7 ];
		for(int i=0;i<configuracion.getDias().length();i++){
			if(configuracion.getDias().charAt(i)=='1')
			   array[i]=true;	
		}	
		return array;
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
					fecha.setText(sd.format(configuracion.getFecha()));
					try {
						configuracion.setFecha(new Date(sd.parse(String.valueOf(fecha.getText())).getTime()));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		
	}
	
	public void fecha(View v){
		DialogFragment newFragment = new DatePickerFragment(configuracion.getFecha());
		newFragment.show(getSupportFragmentManager(), "CALENDARIO");
 
	}

	public void mostrarMensaje() {
		Toast.makeText(this, "Formateando", Toast.LENGTH_SHORT).show();
		
	}

	public void formaPago(View v) {
		    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setTitle("Formas de pago");
	        builder.setItems(R.array.formas_pago, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int item) {
	               configuracion.setFormaPago(item);
	               formaPa.setText(bits[configuracion.getFormaPago()]);
	            }
	        });
	        AlertDialog alert = builder.create();
	        alert.show();	
	 }
	
	private Configuracion buscarConfiguracion() {
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
		return configuracion;
	}
	private DatabaseHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = DatabaseHelper.getHelper(this);
		}
		return databaseHelper;
	}
}
