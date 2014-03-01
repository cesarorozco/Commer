package com.cesar.adapter;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.cesar.bean.Revision;
import com.cesar.uninorteposition.R;

public class AdapterRevision extends ArrayAdapter<Revision> {

	private Context context;
	private List<Revision> datos;
	SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
	Calendar cal = Calendar.getInstance();
	Calendar cal2 = Calendar.getInstance();
	/**
	 * Constructor del Adapter.
	 * 
	 * @param context
	 *            context de la Activity que hace uso del Adapter.
	 * @param datos
	 *            Datos que se desean visualizar en el ListView.
	 */
	public AdapterRevision(Context context, List<Revision> datos, Date fecha) {
		super(context, R.layout.revision_adapter, datos);
		// Guardamos los parámetros en variables de clase.
		this.context = context;
		this.datos = datos;
		cal.setTime(fecha);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// En primer lugar "inflamos" una nueva vista, que será la que se
		// mostrará en la celda del ListView.
		  View item = LayoutInflater.from(context).inflate(R.layout.revision_adapter, null);
		  ImageView imagen = (ImageView) item.findViewById(R.id.imageView1);
		  if (cal2.before(cal)) {
				imagen.setImageResource(R.drawable.ic_action_collections_view_as_list_red);
			  }else
				  if(sd.format(datos.get(position).getFactura().getNext()).equalsIgnoreCase(sd.format(cal.getTime())))
					  imagen.setImageResource(R.drawable.ic_action_collections_view_as_list_green);
				  else
					  imagen.setImageResource(R.drawable.ic_action_collections_view_as_list);
			
		// A partir de la vista, recogeremos los controles que contiene para
		// poder manipularlos.
		// Recogemos el ImageView y le asignamos una foto.
		

		// Recogemos el TextView para mostrar el nombre y establecemos el
		// nombre.
		TextView nombre = (TextView) item.findViewById(R.id.textViewCliente);
		nombre.setText(datos.get(position).getFactura().getCliente().getNombres()+" "+datos.get(position).getFactura().getCliente().getApellidos());

		// Recogemos el TextView para mostrar el número de celda y lo
		// establecemos.
		TextView numCelda = (TextView) item.findViewById(R.id.textDireccion);
		numCelda.setText(datos.get(position).getFactura().getCliente().getDireccion());
		
		TextView saldo = (TextView) item.findViewById(R.id.textTelefono);
		saldo.setText(DecimalFormat.getInstance().format(datos.get(position).getFactura().getTotal())+" "+DecimalFormat.getInstance().format(calcularSaldo(datos.get(position).getFactura())));
		
		TextView fecha = (TextView) item.findViewById(R.id.textViewFecha2);
		fecha.setText(sd.format(datos.get(position).getFecha()));
		
		TextView next = (TextView) item.findViewById(R.id.revision_fecha);
		next.setText(datos.get(position).getDetalle());
		
		return item;
	}
	
	private double calcularSaldo(Factura factura) {
		double totalAbono=0.0;
		if (factura.getPagos()!=null) {
			for (Pago p : factura.getPagos())
				totalAbono = totalAbono + p.getValor();
		}
		return factura.getTotal()-totalAbono;
	}

	
}
