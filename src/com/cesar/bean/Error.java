package com.cesar.bean;

import java.util.Date;
import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "error")
public class Error implements Parcelable{
	public static final String USUARIO_ID = "usuario_id";
	
	@DatabaseField(generatedId = true)
	int id;
	@DatabaseField(canBeNull = true)
	Date fecha;
	@DatabaseField(canBeNull = true)
	double base;
	@DatabaseField(canBeNull = true)
	double recaudo;
	@DatabaseField(canBeNull = true)
	double ventas;
	@DatabaseField(canBeNull = true)
	double gastos;
	@DatabaseField(canBeNull = true)
	double efectivo;
	@DatabaseField(canBeNull = true)
	double error;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "usuario_id")
	Usuario usuario;
		
	public Error() {
		
	}
	public Error(Parcel in){
		this.readFromParcel(in);
		
	}
	public void readFromParcel(Parcel in) {
		 id = in.readInt();
		 fecha = new Date(in.readLong());
		 base = in.readDouble();
		 recaudo = in.readDouble();
		 ventas = in.readDouble();
		 gastos = in.readDouble();
		 efectivo = in.readDouble();
		 error = in.readDouble();
		 usuario = in.readParcelable(Factura.class.getClassLoader());
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

	

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public double getBase() {
		return base;
	}
	public void setBase(double base) {
		this.base = base;
	}
	public double getRecaudo() {
		return recaudo;
	}
	public void setRecaudo(double recaudo) {
		this.recaudo = recaudo;
	}
	public double getVentas() {
		return ventas;
	}
	public void setVentas(double ventas) {
		this.ventas = ventas;
	}
	public double getGastos() {
		return gastos;
	}
	public void setGastos(double gastos) {
		this.gastos = gastos;
	}
	public double getEfectivo() {
		return efectivo;
	}
	public void setEfectivo(double efectivo) {
		this.efectivo = efectivo;
	}
	public double getError() {
		return error;
	}
	public void setError(double error) {
		this.error = error;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeLong(fecha.getTime());
		dest.writeDouble(base);
		dest.writeDouble(recaudo);
		dest.writeDouble(ventas);
		dest.writeDouble(gastos);
		dest.writeDouble(efectivo);
		dest.writeDouble(error);
		dest.writeParcelable(usuario, flags);
		
	}
	
	/*
     * Parcelable interface must also have a static field called CREATOR,
     * which is an object implementing the Parcelable.Creator interface.
     * Used to un-marshal or de-serialize object from Parcel.
     */
    public static final Parcelable.Creator<Error> CREATOR =
            new Parcelable.Creator<Error>() {
        public Error createFromParcel(Parcel in) {
            return new Error(in);
        }
 
        public Error[] newArray(int size) {
            return new Error[size];
        }
    };

	

}
