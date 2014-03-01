package com.cesar.bean;


import java.util.Collection;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "factura")
public class Factura implements Parcelable{
	
	@DatabaseField(generatedId = true)
	int id;
	@DatabaseField(canBeNull = true)
	Date fecha;
	@DatabaseField(canBeNull = true)
	Date fechaVencimiento;
	@DatabaseField(canBeNull = true)
	Date next;
	@DatabaseField(canBeNull = true)
	double monto;
	@DatabaseField(canBeNull = true)
	double total;
	@DatabaseField(canBeNull = true)
	double cuota;
	@DatabaseField(canBeNull = true)
	double interes;
	@DatabaseField(canBeNull = true)
	String formaPago;
	@DatabaseField(canBeNull = true)
	int indice;
	@DatabaseField(canBeNull = true)
	String estado;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "cliente_id")
	Cliente cliente;
	@ForeignCollectionField
	private Collection<Pago> pagos;
		
	public Factura() {
		
	}
	
	public Factura(Parcel in) {
		this.readFromParcel(in);
	}
	
	public void readFromParcel(Parcel in) {
		id = in.readInt();
		fecha = new Date(in.readLong());
		fechaVencimiento = new Date(in.readLong());
		next = new Date(in.readLong());
		monto = in.readDouble();
		total = in.readDouble();
		cuota = in.readDouble();
		interes = in.readDouble();
		formaPago = in.readString();
		indice = in.readInt();
		estado = in.readString();
		cliente = in.readParcelable(Cliente.class.getClassLoader());
	}
	
	public Collection<Pago> getPagos() {
		return pagos;
	}

	public void setPagos(Collection<Pago> pagos) {
		this.pagos = pagos;
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

	public Date getNext() {
		return next;
	}

	public void setNext(Date next) {
		this.next = next;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public int getIndice() {
		return indice;
	}

	public void setIndice(int indice) {
		this.indice = indice;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		Log.i("estudiante","write to parcel factura Inicio");
		dest.writeInt(id);
		dest.writeLong(fecha.getTime());
		dest.writeLong(fechaVencimiento.getTime());
		dest.writeLong(next.getTime());
		dest.writeDouble(monto);
		dest.writeDouble(total);
		dest.writeDouble(cuota);
		dest.writeDouble(interes);
		dest.writeString(formaPago);
		dest.writeInt(indice);
		dest.writeString(estado);
		dest.writeParcelable(cliente, flags);
		Log.i("estudiante","write to parcel factura Fin");
		
	}
	
	public Date getFechaVencimiento() {
		return fechaVencimiento;
	}

	public void setFechaVencimiento(Date fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}

	public double getMonto() {
		return monto;
	}

	public void setMonto(double monto) {
		this.monto = monto;
	}

	public double getCuota() {
		return cuota;
	}

	public void setCuota(double cuota) {
		this.cuota = cuota;
	}

	public double getInteres() {
		return interes;
	}

	public void setInteres(double interes) {
		this.interes = interes;
	}

	public String getFormaPago() {
		return formaPago;
	}

	public void setFormaPago(String formaPago) {
		this.formaPago = formaPago;
	}
	
	public Factura clonar(){
		Factura f = new Factura();
		f.setCliente(this.getCliente());
		f.setCuota(this.getCuota());
		f.setEstado(this.getEstado());
		f.setFecha(this.getFecha());
		f.setFechaVencimiento(this.getFechaVencimiento());
		f.setFormaPago(this.getFormaPago());
		f.setIndice(this.getIndice());
		f.setInteres(this.getInteres());
		f.setMonto(this.getMonto());
		f.setNext(this.getNext());
		f.setTotal(this.getTotal());
		return f;
	}

	/*
     * Parcelable interface must also have a static field called CREATOR,
     * which is an object implementing the Parcelable.Creator interface.
     * Used to un-marshal or de-serialize object from Parcel.
     */
    public static final Parcelable.Creator<Factura> CREATOR =
            new Parcelable.Creator<Factura>() {
        public Factura createFromParcel(Parcel in) {
            return new Factura(in);
        }
 
        public Factura[] newArray(int size) {
            return new Factura[size];
        }
    };

	
}
