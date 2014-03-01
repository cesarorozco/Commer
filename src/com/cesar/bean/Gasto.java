package com.cesar.bean;

import java.util.Date;
import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "gasto")
public class Gasto implements Parcelable{
		
	@DatabaseField(generatedId = true)
	int id;
	@DatabaseField(canBeNull = true)
	Date fecha;
	@DatabaseField(canBeNull = true)
	double valor;
	@DatabaseField(canBeNull = true)
	String detalle;
			
	
	public Gasto() {
		
	}
	public Gasto(Parcel in){
		this.readFromParcel(in);
		
	}
	public void readFromParcel(Parcel in) {
		 id = in.readInt();
		 fecha = new Date(in.readLong());
		 valor = in.readDouble();
		 detalle = in.readString();
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
	
	public String getDetalle() {
		return detalle;
	}
	public void setDetalle(String detalle) {
		this.detalle = detalle;
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
		dest.writeString(detalle);
	}
	
	/*
     * Parcelable interface must also have a static field called CREATOR,
     * which is an object implementing the Parcelable.Creator interface.
     * Used to un-marshal or de-serialize object from Parcel.
     */
    public static final Parcelable.Creator<Gasto> CREATOR =
            new Parcelable.Creator<Gasto>() {
        public Gasto createFromParcel(Parcel in) {
            return new Gasto(in);
        }
 
        public Gasto[] newArray(int size) {
            return new Gasto[size];
        }
    };

	

}
