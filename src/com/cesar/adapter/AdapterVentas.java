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

public class AdapterVentas extends ArrayAdapter<Factura> {

	private Context context;
	private List<Factura> datos;
		
		
	/**
	 * Constructor del Adapter.
	 * 
	 * @param context
	 *            context de la Activity que hace uso del Adapter.
	 * @param datos
	 *            Datos que se desean visualizar en el ListView.
	 */
	public AdapterVentas(Context context, List<Factura> datos) {
		super(context, R.layout.cliente_lista_layout, datos);
		// Guardamos los parámetros en variables de clase.
		this.context = context;
		this.datos = datos;
	
		
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
				View item = LayoutInflater.from(context).inflate(
						R.layout.cliente_lista_layout, null);

				
				// Recogemos el TextView para mostrar el nombre y establecemos el
				// nombre.
				TextView nombre = (TextView) item.findViewById(R.id.roll);
				nombre.setText(datos.get(position).getCliente().getNombres()+" "+datos.get(position).getCliente().getApellidos());

				// Recogemos el TextView para mostrar el número de celda y lo
				// establecemos.
				TextView direccion = (TextView) item.findViewById(R.id.textViewDate);
				direccion.setText(DecimalFormat.getInstance().format(datos.get(position).getMonto()));
				
				// Devolvemos la vista para que se muestre en el ListView.
				return item;
	}

	

}
