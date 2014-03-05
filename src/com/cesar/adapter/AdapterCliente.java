package com.cesar.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cesar.bean.Cliente;
import com.cesar.uninorteposition.R;

public class AdapterCliente extends ArrayAdapter<Cliente>{

	private Context context;
	private List<Cliente> datos;

	/**
	 * Constructor del Adapter.
	 * 
	 * @param context
	 *            context de la Activity que hace uso del Adapter.
	 * @param datos
	 *            Datos que se desean visualizar en el ListView.
	 */
	public AdapterCliente(Context context, List<Cliente> datos) {
		super(context, R.layout.cliente_adapter, datos);
		// Guardamos los parámetros en variables de clase.
		this.context = context;
		this.datos = datos;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// En primer lugar "inflamos" una nueva vista, que será la que se
		// mostrará en la celda del ListView.
		View item = LayoutInflater.from(context).inflate(
				R.layout.cliente_adapter, null);

		// A partir de la vista, recogeremos los controles que contiene para
		// poder manipularlos.
		// Recogemos el ImageView y le asignamos una foto.
		ImageView imagen = (ImageView) item.findViewById(R.id.imageView1);
		imagen.setImageResource(R.drawable.ic_social_person);

		// Recogemos el TextView para mostrar el nombre y establecemos el
		// nombre.
		TextView nombre = (TextView) item.findViewById(R.id.textViewError);
		nombre.setText(datos.get(position).getNombres()+" "+datos.get(position).getApellidos());

		// Recogemos el TextView para mostrar el número de celda y lo
		// establecemos.
		TextView numCelda = (TextView) item.findViewById(R.id.textDireccion);
		numCelda.setText(datos.get(position).getDireccion());
		
		TextView celular = (TextView) item.findViewById(R.id.textViewUsuario);
		celular.setText(datos.get(position).getCelular()+" "+datos.get(position).getTelefono());

		// Devolvemos la vista para que se muestre en el ListView.
		return item;
	}
}
