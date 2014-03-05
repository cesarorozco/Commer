package com.cesar.uninorteposition;

import java.sql.SQLException;
import java.text.ParseException;

import com.cesar.bean.Pago;
import com.cesar.bean.Usuario;
import com.cesar.db.DatabaseHelper;
import com.cesar.uninorteposition.FacturaActivity.DatePickerFragment;
import com.j256.ormlite.dao.Dao;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class UsuarioActivity extends Activity {
	
	private String f;
	private Usuario u;
	private TextView login;
	private TextView password;
	private TextView roll;
	private DatabaseHelper databaseHelper = null;
	private String[] bits=null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_usuario);
		bits = getResources().getStringArray(R.array.rolls);
		u = (Usuario)getIntent().getParcelableExtra("usuario");
		f = getIntent().getStringExtra("flag");
		cargarDatos(u);
	}
	
	private void cargarDatos(Usuario u) {
		login = (TextView) findViewById(R.id.editTextLogin);
		password = (TextView) findViewById(R.id.editTextPassword);
		roll = (TextView) findViewById(R.id.editTextRoll);
		login.setText(u.getLogin());
		password.setText(u.getPassword());	
		roll.setText(u.getRoll());
		roll.setKeyListener(null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.usuario, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
                case R.id.guardar_usuario:
                	guardarUsuario();
                	Intent data = new Intent();
                    setResult(RESULT_OK, data);
                    finish();
                    break;
                /*case R.id.menu_settings:
                        //Configuracion de la aplicacion
                        break;*/

                }
        return super.onOptionsItemSelected(item);
    }

	private void guardarUsuario() {
		
		u.setLogin(this.login.getText().toString());
		u.setPassword(this.password.getText().toString());
		u.setRoll(this.roll.getText().toString());

		try {
			Dao<Usuario, Integer> simpleDao = getHelper().getUsuarioDao();
			if(f.equalsIgnoreCase("crear"))
				simpleDao.create(u);
			else
				simpleDao.update(u);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	private DatabaseHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = DatabaseHelper.getHelper(this);
		}
		return databaseHelper;
	}
	
	public void roll(View v){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ROLLES");
        builder.setItems(R.array.rolls, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
               u.setRoll(bits[item]);
               roll.setText(bits[item]);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();	

	}

}
