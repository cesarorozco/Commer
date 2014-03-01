package com.cesar.bean;

import java.util.Date;
import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "pago")
public class Pago implements Parcelable{
	public static final String FACTURA_ID = "factura_id";
	
	@DatabaseField(generatedId = true)
	int id;
	@DatabaseField(canBeNull = true)
	Date fecha;
	@DatabaseField(canBeNull = true)
	double valor;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "factura_id")
	Factura factura;
		
	public Pago() {
		
	}
	public Pago(Parcel in){
		this.readFromParcel(in);
		
	}
	public void readFromParcel(Parcel in) {
		 id = in.readInt();
		 fecha = new Date(in.readLong());
		 valor = in.readDouble();
		 factura = in.readParcelable(Factura.class.getClassLoader());
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

	public Factura getFactura() {
		return factura;
	}

	public void setFactura(Factura factura) {
		this.factura = factura;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeLong(fecha.getTime());
		dest.writeDouble(valor);
		dest.writeParcelable(factura, flags);
		
	}
	
	/*
     * Parcelable interface must also have a static field called CREATOR,
     * which is an object implementing the Parcelable.Creator interface.
     * Used to un-marshal or de-serialize object from Parcel.
     */
    public static final Parcelable.Creator<Pago> CREATOR =
            new Parcelable.Creator<Pago>() {
        public Pago createFromParcel(Parcel in) {
            return new Pago(in);
        }
 
        public Pago[] newArray(int size) {
            return new Pago[size];
        }
    };

	

}
