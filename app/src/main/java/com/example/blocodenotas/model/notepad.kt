package com.example.blocodenotas.model

import android.os.Parcel
import android.os.Parcelable

data class notepad(val id:Int, var title:String, var image:String, var note:String, var date:String):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(image)
        parcel.writeString(note)
        parcel.writeString(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<notepad> {
        override fun createFromParcel(parcel: Parcel): notepad {
            return notepad(parcel)
        }

        override fun newArray(size: Int): Array<notepad?> {
            return arrayOfNulls(size)
        }
    }


}
