package com.cesar.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = "cliente")
public class Cliente implements Parcelable{
	
	@DatabaseField(generatedId = true)
	int id;
	@DatabaseField(canBeNull = true)
	String cedula;
	@DatabaseField(canBeNull = true)
	String nombres;
	@DatabaseField(canBeNull = true)
	String apellidos;
	@DatabaseField(canBeNull = true)
	String direccion;
	@DatabaseField(canBeNull = true)
	String telefono;
	@DatabaseField(canBeNull = true)
	String celular;
	@DatabaseField(canBeNull = true)
	double longitud;
	@DatabaseField(canBeNull = true)
	double latitud;
	
	
	public Cliente() {
		
	}
	public String getCedula() {
		return cedula;
	}


	public void setCedula(String cedula) {
		this.cedula = cedula;
	}
		
	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}

	public double getLongitud() {
		return longitud;
	}

	public void setLongitud(double longitud) {
		this.longitud = longitud;
	}

	public double getLatitud() {
		return latitud;
	}

	public void setLatitud(double latitud) {
		this.latitud = latitud;
	}

	
	public Cliente(Parcel in) {
		this.readFromParcel(in);
	}


	private void readFromParcel(Parcel in) {
		id = in.readInt();
		cedula = in.readString();
		nombres = in.readString();
		apellidos = in.readString();
		direccion = in.readString();
		telefono = in.readString();
		celular = in.readString();
		longitud = in.readDouble();
		latitud = in.readDouble();
				
	}
	
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getNombres() {
		return nombres;
	}


	public void setNombres(String nombres) {
		this.nombres = nombres;
	}


	public String getDireccion() {
		return direccion;
	}


	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}


	public String getTelefono() {
		return telefono;
	}


	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}


	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(cedula);
		dest.writeString(nombres);
		dest.writeString(apellidos);		
		dest.writeString(direccion);
		dest.writeString(telefono);
		dest.writeString(celular);
		dest.writeDouble(longitud);
		dest.writeDouble(latitud);
	}
	
	/*
     * Parcelable interface must also have a static field called CREATOR,
     * which is an object implementing the Parcelable.Creator interface.
     * Used to un-marshal or de-serialize object from Parcel.
     */
    public static final Parcelable.Creator<Cliente> CREATOR =
            new Parcelable.Creator<Cliente>() {
        public Cliente createFromParcel(Parcel in) {
            return new Cliente(in);
        }
 
        public Cliente[] newArray(int size) {
            return new Cliente[size];
        }
    };

}
