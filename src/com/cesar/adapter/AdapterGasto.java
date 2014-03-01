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

import com.cesar.bean.Factura;
import com.cesar.bean.Gasto;
import com.cesar.bean.Pago;
import com.cesar.uninorteposition.R;

public class AdapterGasto extends ArrayAdapter<Gasto> {

	private Context context;
	private List<Gasto> datos;
	SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
	
	/**
	 * Constructor del Adapter.
	 * 
	 * @param context
	 *            context de la Activity que hace uso del Adapter.
	 * @param datos
	 *            Datos que se desean visualizar en el ListView.
	 */
	public AdapterGasto(Context context, List<Gasto> datos) {
		super(context, R.layout.gasto_adapter, datos);
		// Guardamos los parámetros en variables de clase.
		this.context = context;
		this.datos = datos;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// En primer lugar "inflamos" una nueva vista, que será la que se
		// mostrará en la celda del ListView.
		View item = LayoutInflater.from(context).inflate(
				R.layout.gasto_adapter, null);

		// A partir de la vista, recogeremos los controles que contiene para
		// poder manipularlos.
		// Recogemos el ImageView y le asignamos una foto.
		ImageView imagen = (ImageView) item.findViewById(R.id.imageView1);
		imagen.setImageResource(R.drawable.ic_action_collections_labels);

		// Recogemos el TextView para mostrar el nombre y establecemos el
		// nombre.
		TextView valor = (TextView) item.findViewById(R.id.login);
		valor.setText(DecimalFormat.getInstance().format(datos.get(position).getValor()));

		// Recogemos el TextView para mostrar el número de celda y lo
		// establecemos.
		TextView detalle = (TextView) item.findViewById(R.id.roll);
		detalle.setText(datos.get(position).getDetalle());
		
		TextView fecha = (TextView) item.findViewById(R.id.fecha);
		fecha.setText(sd.format(datos.get(position).getFecha()));
		
		// Devolvemos la vista para que se muestre en el ListView.
		return item;
	}

	
}
