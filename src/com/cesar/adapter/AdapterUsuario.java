package com.cesar.adapter;


import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.cesar.bean.Usuario;
import com.cesar.uninorteposition.R;

public class AdapterUsuario extends ArrayAdapter<Usuario> {

	private Context context;
	private List<Usuario> datos;
	/**
	 * Constructor del Adapter.
	 * 
	 * @param context
	 *            context de la Activity que hace uso del Adapter.
	 * @param datos
	 *            Datos que se desean visualizar en el ListView.
	 */
	public AdapterUsuario(Context context, List<Usuario> datos) {
		super(context, R.layout.usuario_adapter, datos);
		// Guardamos los parámetros en variables de clase.
		this.context = context;
		this.datos = datos;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// En primer lugar "inflamos" una nueva vista, que será la que se
		// mostrará en la celda del ListView.
		View item = LayoutInflater.from(context).inflate(
				R.layout.usuario_adapter, null);

		// A partir de la vista, recogeremos los controles que contiene para
		// poder manipularlos.
		// Recogemos el ImageView y le asignamos una foto.
		ImageView imagen = (ImageView) item.findViewById(R.id.imageView1);
		imagen.setImageResource(R.drawable.ic_action_collections_labels);

		// Recogemos el TextView para mostrar el nombre y establecemos el
		// nombre.
		TextView login = (TextView) item.findViewById(R.id.login);
		login.setText(datos.get(position).getLogin());

		// Recogemos el TextView para mostrar el número de celda y lo
		// establecemos.
		TextView roll = (TextView) item.findViewById(R.id.roll);
		roll.setText(datos.get(position).getRoll());
				// Devolvemos la vista para que se muestre en el ListView.
		return item;
	}

	
}
