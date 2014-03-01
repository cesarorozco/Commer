package com.cesar.uninorteposition;


import java.sql.SQLException;

import com.cesar.adapter.AdapterFactura;
import com.cesar.bean.Cliente;
import com.cesar.bean.Factura;
import com.cesar.db.DatabaseHelper;
import com.j256.ormlite.dao.Dao;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ClienteActivity extends Activity {
	
	private DatabaseHelper databaseHelper = null;
	private Cliente cliente;
	private TextView cedula;
	private TextView nombres;
	private TextView apellidos;
	private TextView direccion;
	private TextView telefono;
	private TextView celular;
	private int request_code = 1;
	private String f;
	private int parent=1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cliente_layout);
		cliente = (Cliente)getIntent().getParcelableExtra("cliente");
		f = getIntent().getStringExtra("flag");
		cargarDatos(cliente);
		if(f.equalsIgnoreCase("nuevo")){
			parent=0;
			f="crear";
		}	
	}

	private void cargarDatos(Cliente cliente) {
		cedula = (TextView) findViewById(R.id.editTextCedula);
		nombres = (TextView) findViewById(R.id.editTextNombres);
		apellidos = (TextView) findViewById(R.id.editTextApellidos);
		direccion = (TextView) findViewById(R.id.editTextDireccion);
		telefono = (TextView) findViewById(R.id.editTextTelefono);
		celular = (TextView) findViewById(R.id.editTextCelular);
		cedula.setText(cliente.getCedula());
		nombres.setText(cliente.getNombres());
		apellidos.setText(cliente.getApellidos());
		direccion.setText(cliente.getDireccion());
		telefono.setText(cliente.getTelefono());
		celular.setText(cliente.getCelular());
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cliente, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
                case R.id.guardar_cliente:
                	if(camposValidos())
                	   guardar();
                	else{
                	   mostrarMensaje();
                	}
                	break;
                case R.id.registrar_posicion:
                	posicion();
                    break;

                }
        return super.onOptionsItemSelected(item);
    }
	
	private void mostrarMensaje() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("CAMPOS VACIOS");
		builder.setMessage("DEBE RELLENAR TODOS LOS DATOS");
		builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	    		
		           }
		       });
		//Create the AlertDialog
		builder.create().show();
		
	}

	private boolean camposValidos() {
		// TODO Auto-generated method stub
		if(this.cedula.getText().toString().equalsIgnoreCase("")||
			this.nombres.getText().toString().equalsIgnoreCase("")||
			this.apellidos.getText().toString().equalsIgnoreCase("")||
			this.direccion.getText().toString().equalsIgnoreCase("")||
			this.telefono.getText().toString().equalsIgnoreCase("")||
			this.celular.getText().toString().equalsIgnoreCase("")){
			return false;
		}
		return true;
	}

	public void guardar(){
		cliente.setCedula(this.cedula.getText().toString());
		cliente.setNombres(this.nombres.getText().toString());
		cliente.setApellidos(this.apellidos.getText().toString());
		cliente.setDireccion(this.direccion.getText().toString());
		cliente.setTelefono(this.telefono.getText().toString());
		cliente.setCelular(this.celular.getText().toString());
		try {
			Dao<Cliente, Integer> clienteDao = getHelper().getClienteDao();
			if(f.equalsIgnoreCase("crear"))
				clienteDao.create(cliente);
			else
				clienteDao.update(cliente);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Intent data = new Intent();
		if(parent==1){
           setResult(RESULT_OK, data);
		}else{
			data.putExtra("cliente", cliente);
			setResult(2, data);
		}	
		finish();
		
	}
	
	public void posicion(){
		Intent i = new Intent(this, Map.class );
		i.putExtra("cliente", cliente);
		startActivityForResult(i, request_code);
		
	}
	
	private DatabaseHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = DatabaseHelper.getHelper(this);
		}
		return databaseHelper;
	}
	
	 @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        // TODO Auto-generated method stub
	        if ((requestCode == request_code) && (resultCode == RESULT_OK)){
	        	cliente = (Cliente)data.getParcelableExtra("cliente");
	        	guardar();
	        }
	    }
}
