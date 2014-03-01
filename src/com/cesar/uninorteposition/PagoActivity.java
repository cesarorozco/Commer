package com.cesar.uninorteposition;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.cesar.adapter.AdapterPagoList;
import com.cesar.aplicativo.NumberTextWatcher;
import com.cesar.bean.Factura;
import com.cesar.bean.Pago;
import com.cesar.db.DatabaseHelper;
import com.cesar.uninorteposition.ConfiguracionActivity.DatePickerFragment;
import com.j256.ormlite.dao.Dao;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;

public class PagoActivity extends FragmentActivity {
	
	private DatabaseHelper databaseHelper = null;
	private Pago p;
	private TextView fecha;
	private EditText valor;
	private String f;
	SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
	private DecimalFormat df = new DecimalFormat("#,###.##");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pago_layout);
		p = (Pago)getIntent().getParcelableExtra("pago");
		f = getIntent().getStringExtra("flag");
		cargarDatos(p);
		cargarListener();
	}

	private void cargarListener() {
		valor.addTextChangedListener(new NumberTextWatcher(valor));
		
	}

	private void cargarDatos(Pago p2) {
		fecha = (TextView) findViewById(R.id.editTextValor);
		valor = (EditText) findViewById(R.id.editTextDetalle);
		fecha.setText(sd.format(p2.getFecha()));
		valor.setText(DecimalFormat.getInstance().format(p2.getValor()));	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pago, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
                case R.id.menu_save:
                	confirmarabono();
                    break;
                }
        return super.onOptionsItemSelected(item);
    }
	

	public void confirmarabono(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("ABONO "+valor.getText());
		builder.setMessage(p.getFactura().getCliente().getNombres());
		builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   guardarPago();
	                   		        		
		           }
		       });
		builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   Intent data = new Intent();
	                   setResult(RESULT_OK, data);
	                   finish();
		           }
		       });
			//Create the AlertDialog
		builder.create().show();
	}


	private void guardarPago() {
		 try {
			p.setFecha(sd.parse(String.valueOf(fecha.getText())));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		p.setValor(obtenerDouble(valor.getText()));
		try {
			Dao<Pago, Integer> simpleDao = getHelper().getPagoDao();
			if(f.equalsIgnoreCase("crear"))
				simpleDao.create(p);
			else
				simpleDao.update(p);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Intent data = new Intent();
        setResult(RESULT_OK, data);
        finish();
		
	}
	
	public void fecha(View v){
	DialogFragment newFragment = new DatePickerFragment(p.getFecha());
    newFragment.show(getSupportFragmentManager(), "CALENDARIO");

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
				fecha.setText(sd.format(p.getFecha()));
				try {
					p.setFecha(sd.parse(String.valueOf(fecha.getText())));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	      }
	
}
	
	
	private DatabaseHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = DatabaseHelper.getHelper(this);
		}
		return databaseHelper;
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
