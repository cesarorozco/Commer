package com.cesar.adapter;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cesar.bean.Cliente;
import com.cesar.bean.Usuario;
import com.cesar.uninorteposition.R;

public class AdapterUsuarioList extends ArrayAdapter<Usuario> {

	private Context context;
	private List<Usuario> datos;

	/**
	 * Constructor del Adapter.
	 * 
	 * @param context
	 *            context de la Activity que hace uso del Adapter.
	 * @param listaClientes
	 *            Datos que se desean visualizar en el ListView.
	 */
	public AdapterUsuarioList(Context context, List<Usuario> listaUsuario) {
		super(context, R.layout.cliente_lista_layout, listaUsuario);
		// Guardamos los parámetros en variables de clase.
		this.context = context;
		this.datos = listaUsuario;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// En primer lugar "inflamos" una nueva vista, que será la que se
		// mostrará en la celda del ListView.
		View item = LayoutInflater.from(context).inflate(
				R.layout.cliente_lista_layout, null);

		
		// Recogemos el TextView para mostrar el nombre y establecemos el
		// nombre.
		TextView nombre = (TextView) item.findViewById(R.id.roll);
		nombre.setText(datos.get(position).getLogin());

		// Recogemos el TextView para mostrar el número de celda y lo
		// establecemos.
		TextView direccion = (TextView) item.findViewById(R.id.textViewDate);
		direccion.setText(datos.get(position).getRoll());

		// Devolvemos la vista para que se muestre en el ListView.
		return item;
	}

	

}
