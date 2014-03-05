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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.cesar.bean.Factura;
import com.cesar.bean.Pago;
import com.cesar.uninorteposition.R;

public class AdapterFactura extends ArrayAdapter<Factura> {

	private Context context;
	private List<Factura> datos;
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
	public AdapterFactura(Context context, List<Factura> datos, Date fecha) {
		super(context, R.layout.factura_adapter, datos);
		// Guardamos los parámetros en variables de clase.
		this.context = context;
		this.datos = datos;
		cal.setTime(fecha);
		
	}
	
	@Override  
    public int getCount() {  
        // TODO Auto-generated method stub  
        return datos.size();  
    }  
  
    @Override  
    public Factura getItem(int position) {  
        // TODO Auto-generated method stub  
        return null;  
    }  
  
    @Override  
    public long getItemId(int position) {  
        // TODO Auto-generated method stub  
        return 0;  
    }
	
	@Override
	public View getView(  final int position, View convertView, ViewGroup parent) {
		// En primer lugar "inflamos" una nueva vista, que será la que se
		// mostrará en la celda del ListView.
		  View item = LayoutInflater.from(context).inflate(R.layout.factura_adapter, null);
		  
		  
		  cal2.setTime(datos.get(position).getFechaVencimiento());
		  ImageView imagen = (ImageView) item.findViewById(R.id.imageView1);
		  
		  if (cal2.before(cal)) {
			imagen.setImageResource(R.drawable.ic_action_collections_view_as_list_red);
		  }else
			  if(sd.format(datos.get(position).getNext()).equalsIgnoreCase(sd.format(cal.getTime())))
				  imagen.setImageResource(R.drawable.ic_action_collections_view_as_list_green);
			  else
				  imagen.setImageResource(R.drawable.ic_action_collections_view_as_list);
				  
		
			
		// A partir de la vista, recogeremos los controles que contiene para
		// poder manipularlos.
		// Recogemos el ImageView y le asignamos una foto.
		

		// Recogemos el TextView para mostrar el nombre y establecemos el
		// nombre.
		TextView nombre = (TextView) item.findViewById(R.id.textViewError);
		nombre.setText(datos.get(position).getCliente().getNombres()+" "+datos.get(position).getCliente().getApellidos());

		// Recogemos el TextView para mostrar el número de celda y lo
		// establecemos.
		TextView numCelda = (TextView) item.findViewById(R.id.textDireccion);
		numCelda.setText(datos.get(position).getCliente().getDireccion());
		
		TextView saldo = (TextView) item.findViewById(R.id.textViewUsuario);
		saldo.setText(DecimalFormat.getInstance().format(datos.get(position).getTotal())+" "+DecimalFormat.getInstance().format(calcularSaldo(datos.get(position))));
		
		TextView next = (TextView) item.findViewById(R.id.textViewFecha);
		next.setText(sd.format(datos.get(position).getNext()));
	
		// Devolvemos la vista para que se muestre en el ListView.
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
