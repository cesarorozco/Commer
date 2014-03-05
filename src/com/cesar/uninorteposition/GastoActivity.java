package com.cesar.uninorteposition;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.cesar.aplicativo.NumberTextWatcher;
import com.cesar.bean.Configuracion;
import com.cesar.bean.Gasto;
import com.cesar.bean.Usuario;
import com.cesar.db.DatabaseHelper;
import com.cesar.uninorteposition.PagoActivity.DatePickerFragment;
import com.j256.ormlite.dao.Dao;

public class GastoActivity extends FragmentActivity {

	private Gasto gasto;
	private String f;
	private EditText valor;
	private TextView detalle;
	private TextView fecha;
	SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
	private DecimalFormat df = new DecimalFormat("#,###.##");
	private DatabaseHelper databaseHelper = null;
	private Configuracion c;
	private Usuario u;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gasto);
		gasto = (Gasto)getIntent().getParcelableExtra("gasto");
		f = getIntent().getStringExtra("flag");
		c = (Configuracion)getIntent().getParcelableExtra("configuracion");
		u = (Usuario)getIntent().getParcelableExtra("usuario");
		cargarDatos(gasto);
		cargarListener();
	}

	private void cargarListener() {
		valor.addTextChangedListener(new NumberTextWatcher(valor));
		
	}

	private void cargarDatos(Gasto gasto) {
		valor = (EditText) findViewById(R.id.editTextValor);
		detalle = (TextView) findViewById(R.id.editTextDetalle);
		fecha = (TextView) findViewById(R.id.editTextDate);
		valor.setText(DecimalFormat.getInstance().format(gasto.getValor()));
		detalle.setText(gasto.getDetalle());
		fecha.setText(sd.format(gasto.getFecha()));
		if (u.getRoll().equalsIgnoreCase("COBRADOR")){
		    if(!sd.format(gasto.getFecha()).equalsIgnoreCase(sd.format(c.getFecha()))){
		    	fecha.setEnabled(false);
				valor.setEnabled(false);	
				detalle.setEnabled(false);
		    }else{
		    	valor.setEnabled(true);
		    	fecha.setEnabled(false);
		    }	
		}else{
			if(u.getRoll().equalsIgnoreCase("SUPERVISOR")){
				fecha.setEnabled(false);
				valor.setEnabled(false);
				detalle.setEnabled(false);
			}
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gasto, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
                case R.id.guardar:
                	guardarGasto();
                	Intent data = new Intent();
                    setResult(RESULT_OK, data);
                    finish();
                    break;
                }
        return super.onOptionsItemSelected(item);
    }
	
	private void guardarGasto() {
		try {
			gasto.setFecha(sd.parse(String.valueOf(fecha.getText())));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		gasto.setValor(obtenerDouble(valor.getText()));
		gasto.setDetalle(String.valueOf(detalle.getText()));
		try {
			Dao<Gasto, Integer> simpleDao = getHelper().getGastoDao();
			if(f.equalsIgnoreCase("crear"))
				simpleDao.create(gasto);
			else
				simpleDao.update(gasto);
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
				fecha.setText(sd.format(gasto.getFecha()));
				try {
					gasto.setFecha(sd.parse(String.valueOf(fecha.getText())));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		
	}
	
}

	public void fecha(View v){
	DialogFragment newFragment = new DatePickerFragment(gasto.getFecha());
    newFragment.show(getSupportFragmentManager(), "CALENDARIO");

}

}