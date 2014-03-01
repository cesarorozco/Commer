package com.cesar.uninorteposition;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import com.cesar.adapter.AdapterError;
import com.cesar.adapter.AdapterFactura;
import com.cesar.bean.Error;
import com.cesar.bean.Factura;
import com.cesar.bean.Usuario;
import com.cesar.db.DatabaseHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ErroresActivity extends ListActivity {
	
	private DatabaseHelper databaseHelper = null;
	private List<Error> listaErrores;
	private Usuario u;
	private int request_code = 1;
	private Error errorSeleccionado;
	private ProgressDialog pDialog;
	private TareaListarErrores tarea;
	public AdapterError adapter;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		u = (Usuario)getIntent().getParcelableExtra("usuario");
		listarErrores();
		registerForContextMenu(this.getListView());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pagos, menu);
		return true;
	}
	
	private class TareaListarErrores extends AsyncTask<Void, Integer, Boolean> {
		 
        @Override
        protected Boolean doInBackground(Void... params) {
 
        	
    		try {
    			Dao<Error, Integer> simpleDao = getHelper().getErrorDao();
    			if (!u.getRoll().equals("Administrador")) {
    				listaErrores = simpleDao.queryBuilder().where()
    						.eq(Error.USUARIO_ID, u.getId()).query();
    			}else{
    				listaErrores = simpleDao.queryForAll();
    			}
    			
    			adapter = new AdapterError(getContext(), listaErrores);
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
            Toast.makeText(ErroresActivity.this, "Tarea cancelada!",
                Toast.LENGTH_SHORT).show();
        }
    }
	
	public void listarErrores() {
		pDialog = new ProgressDialog(ErroresActivity.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(true);
        pDialog.setMax(100);
 
        tarea = new TareaListarErrores();
        tarea.execute();
		
	}
	private DatabaseHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = DatabaseHelper.getHelper(this);
		}
		return databaseHelper;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
                case R.id.menu_add:
                	Error error = new Error();
                	error.setFecha(new Date());
                	error.setUsuario(u);
                   	verError(error,"crear");
                    break;
                }
        return super.onOptionsItemSelected(item);
    }
	
	public void verError(Error e,String f){
		Intent i = new Intent(this, ErrorActivity.class );
		i.putExtra("error", e);
		i.putExtra("flag", f);
		i.putExtra("usuario", this.u);
		startActivityForResult(i, request_code);
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if ((requestCode == request_code) && (resultCode == RESULT_OK)){
        	listarErrores();
        }
    }
		
	public void onListItemClick(ListView parent,View v,int position, long id){
		Error e = listaErrores.get(position);
		verError(e,"modificar");
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
	  super.onCreateContextMenu(menu, v, menuInfo);
	  getMenuInflater().inflate(R.menu.menu_context_errores,menu);
	  menu.setHeaderTitle("Menu");
	  
	}
	
	@Override
    public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		int itemId = item.getItemId();
        switch (itemId) {
                case R.id.nuevo_error:
                	Error error = new Error();
                	error.setFecha(new Date());
                	error.setUsuario(u);
                	verError(error,"crear");;
                	break;
                case R.id.borrar_error:
                	errorSeleccionado = listaErrores.get(info.position);
                	mostrarMensajeEliminar();
                    break;

                }
        return super.onOptionsItemSelected(item);
    }
	
	private void mostrarMensajeEliminar() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Eliminar");
		builder.setMessage("Esta seguro de eliminar este error ?");
		builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   eliminarError();		        		
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
	
	private void eliminarError() {
		Dao<Error, Integer> errorDao;
		try {
			errorDao = getHelper().getErrorDao();
			errorDao.delete(errorSeleccionado);
			listarErrores();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Context getContext(){
		return this.getApplication();
		
	}

}
