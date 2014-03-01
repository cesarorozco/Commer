package com.cesar.uninorteposition;

import java.sql.SQLException;
import java.util.List;

import com.cesar.adapter.AdapterUsuario;
import com.cesar.bean.Usuario;
import com.cesar.db.DatabaseHelper;
import com.j256.ormlite.dao.Dao;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.TextView;

public class LoginActivity extends Activity {

	private DatabaseHelper databaseHelper = null;
	private TextView login;
	private TextView passwd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		login = (TextView) findViewById(R.id.editTextUsuario);
		passwd = (TextView) findViewById(R.id.editTextPasswd);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
		
	}
	
	public void login(View v) {
		Usuario u = buscarUsuario(String.valueOf(login.getText()));
		if(u!=null){
			if(u.getPassword().equals(String.valueOf(passwd.getText()))){
				Intent i = new Intent(this, MainActivity.class );
				i.putExtra("usuario", u);
				startActivity(i);
			}else{
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Contrasena Invalida");
				builder.setMessage(login.getText()+"");
				builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				        	  	        		
				           }
				       });
				
					// Create the AlertDialog
				builder.create().show();
			}
		}else{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Usuario no encontrado");
			builder.setMessage(login.getText()+"");
			builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	  	        		
			           }
			       });
			
				// Create the AlertDialog
			builder.create().show();
			
		}
	}
	
	private Usuario buscarUsuario(String cod) {
		Usuario usuario = null;
		List<Usuario> listUsuarios = null;
		try {
			Dao<Usuario, Integer> usuarioDao = getHelper().getUsuarioDao();
			listUsuarios = usuarioDao.queryBuilder().where()
			         .eq(Usuario.USUARIO_LOGIN, cod)
			         .query();
			//listUsuarios = usuarioDao.queryForAll();
			if(!listUsuarios.isEmpty())
				usuario=listUsuarios.get(0);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return usuario;
	}
	
	private DatabaseHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = DatabaseHelper.getHelper(this);
		}
		return databaseHelper;
	}

}
