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
import com.cesar.uninorteposition.R;

public class AdapterPagoList extends ArrayAdapter<Pago> {

	private Context context;
	private List<Pago> datos;
	SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
	

	/**
	 * Constructor del Adapter.
	 * 
	 * @param context
	 *            context de la Activity que hace uso del Adapter.
	 * @param listaClientes
	 *            Datos que se desean visualizar en el ListView.
	 */
	public AdapterPagoList(Context context, List<Pago> listaPagos) {
		super(context, R.layout.pago_lista_layout, listaPagos);
		// Guardamos los parámetros en variables de clase.
		this.context = context;
		this.datos = listaPagos;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// En primer lugar "inflamos" una nueva vista, que será la que se
		// mostrará en la celda del ListView.
		View item = LayoutInflater.from(context).inflate(
				R.layout.pago_lista_layout, null);

		
		// Recogemos el TextView para mostrar el nombre y establecemos el
		// nombre.
		TextView nombre = (TextView) item.findViewById(R.id.roll);
		nombre.setText(DecimalFormat.getInstance().format(datos.get(position).getValor()));

		// Recogemos el TextView para mostrar el número de celda y lo
		// establecemos.
		TextView direccion = (TextView) item.findViewById(R.id.textViewDate);
		direccion.setText(sd.format(datos.get(position).getFecha()));

		// Devolvemos la vista para que se muestre en el ListView.
		return item;
	}

	

}
