package com.cesar.bean;

import java.util.Date;
import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "configuracion")
public class Configuracion implements Parcelable{
		
	@DatabaseField(generatedId = true)
	int id;
	@DatabaseField(canBeNull = true)
	Date fecha;
	@DatabaseField(canBeNull = true)
	String  dias;
	@DatabaseField(canBeNull = true)
	double iva;
	@DatabaseField(canBeNull = true)
	int formaPago;
	@DatabaseField(canBeNull = true)
	int plazo;
	
	
		
	public Configuracion() {
		
	}
	public Configuracion(Parcel in){
		this.readFromParcel(in);
		
	}
	public void readFromParcel(Parcel in) {
		 id = in.readInt();
		 fecha = new Date(in.readLong());
		 dias = in.readString();
		 iva = in.readDouble();
		 formaPago = in.readInt();
		 plazo   = in.readInt();
		 
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public  Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	public String getDias() {
		return dias;
	}
	public void setDias(String dias) {
		this.dias = dias;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeLong(fecha.getTime());
		dest.writeString(dias);
		dest.writeDouble(iva);
		dest.writeInt(formaPago);
		dest.writeInt(plazo);
	}
	
	public double getIva() {
		return iva;
	}
	public void setIva(double iva) {
		this.iva = iva;
	}
	public int getFormaPago() {
		return formaPago;
	}
	public void setFormaPago(int formaPago) {
		this.formaPago = formaPago;
	}
	public int getPlazo() {
		return plazo;
	}
	public void setPlazo(int plazo) {
		this.plazo = plazo;
	}

	/*
     * Parcelable interface must also have a static field called CREATOR,
     * which is an object implementing the Parcelable.Creator interface.
     * Used to un-marshal or de-serialize object from Parcel.
     */
    public static final Parcelable.Creator<Configuracion> CREATOR =
            new Parcelable.Creator<Configuracion>() {
        public Configuracion createFromParcel(Parcel in) {
            return new Configuracion(in);
        }
 
        public Configuracion[] newArray(int size) {
            return new Configuracion[size];
        }
    };

	

}
