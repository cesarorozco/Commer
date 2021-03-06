package com.cesar.adapter;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
import com.cesar.bean.Pago;
import com.cesar.bean.Error;
import com.cesar.uninorteposition.R;

public class AdapterError extends ArrayAdapter<Error> {

	private Context context;
	private List<Error> datos;
	SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
	

	/**
	 * Constructor del Adapter.
	 * 
	 * @param context
	 *            context de la Activity que hace uso del Adapter.
	 * @param listaClientes
	 *            Datos que se desean visualizar en el ListView.
	 */
	public AdapterError(Context context, List<Error> listaErrores) {
		super(context, R.layout.error_adapter, listaErrores);
		// Guardamos los par�metros en variables de clase.
		this.context = context;
		this.datos = listaErrores;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// En primer lugar "inflamos" una nueva vista, que ser� la que se
		// mostrar� en la celda del ListView.
		View item = LayoutInflater.from(context).inflate(
				R.layout.error_adapter, null);
		ImageView imagen = (ImageView) item.findViewById(R.id.imageView1);
		  
		  
		
		// Recogemos el TextView para mostrar el nombre y establecemos el
		// nombre.
		TextView error = (TextView) item.findViewById(R.id.textViewError);
		error.setText(DecimalFormat.getInstance().format(datos.get(position).getError()));

		// Recogemos el TextView para mostrar el n�mero de celda y lo
		// establecemos.
		TextView fecha = (TextView) item.findViewById(R.id.textViewFecha);
		fecha.setText(sd.format(datos.get(position).getFecha()));
		
		TextView responsable = (TextView) item.findViewById(R.id.textViewUsuario);
		responsable.setText(datos.get(position).getUsuario().getLogin());
		
		if (datos.get(position).getError()<0) {
			imagen.setImageResource(R.drawable.ic_action_collections_view_as_list_red);
		}else
			imagen.setImageResource(R.drawable.ic_action_collections_view_as_list_green);


		// Devolvemos la vista para que se muestre en el ListView.
		return item;
	}

	

}
