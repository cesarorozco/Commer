package com.cesar.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = "usuario")
public class Usuario implements Parcelable{
	
	public static final String USUARIO_LOGIN = "login";
	
	@DatabaseField(generatedId = true)
	int id;
	@DatabaseField(canBeNull = true)
	String login;
	@DatabaseField(canBeNull = true)
	String password;
	@DatabaseField(canBeNull = true)
	String roll;

	
	public Usuario() {
		
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRoll() {
		return roll;
	}

	public void setRoll(String roll) {
		this.roll = roll;
	}

	public Usuario(Parcel in) {
		this.readFromParcel(in);
	}


	private void readFromParcel(Parcel in) {
		id = in.readInt();
		login = in.readString();
		password = in.readString();
		roll = in.readString();
	}
	
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(login);
		dest.writeString(password);
		dest.writeString(roll);		
			}
	
	/*
     * Parcelable interface must also have a static field called CREATOR,
     * which is an object implementing the Parcelable.Creator interface.
     * Used to un-marshal or de-serialize object from Parcel.
     */
    public static final Parcelable.Creator<Usuario> CREATOR =
            new Parcelable.Creator<Usuario>() {
        public Usuario createFromParcel(Parcel in) {
            return new Usuario(in);
        }
 
        public Usuario[] newArray(int size) {
            return new Usuario[size];
        }
    };

}
