package com.cesar.bean;

import java.util.Date;
import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "revision")
public class Revision implements Parcelable{
		
	@DatabaseField(generatedId = true)
	int id;
	@DatabaseField(canBeNull = true)
	Date fecha;
	@DatabaseField(canBeNull = true)
	String detalle;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "factura_id")
	Factura factura;		
	
	public Revision() {
		
	}
	public Revision(Parcel in){
		this.readFromParcel(in);
		
	}
	public void readFromParcel(Parcel in) {
		 id = in.readInt();
		 fecha = new Date(in.readLong());
		 detalle = in.readString();
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
	public Factura getFactura() {
		return factura;
	}

	public void setFactura(Factura factura) {
		this.factura = factura;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeLong(fecha.getTime());
		dest.writeString(detalle);
		dest.writeParcelable(factura, flags);
	}
	
	/*
     * Parcelable interface must also have a static field called CREATOR,
     * which is an object implementing the Parcelable.Creator interface.
     * Used to un-marshal or de-serialize object from Parcel.
     */
    public static final Parcelable.Creator<Revision> CREATOR =
            new Parcelable.Creator<Revision>() {
        public Revision createFromParcel(Parcel in) {
            return new Revision(in);
        }
 
        public Revision[] newArray(int size) {
            return new Revision[size];
        }
    };

	

}
