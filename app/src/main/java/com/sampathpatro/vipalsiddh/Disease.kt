package com.sampathpatro.vipalsiddh

import android.os.Parcel
import android.os.Parcelable

data class Disease(val name: String, val desc: String, val diseaseSymptoms: ArrayList<String>,
                   val seriousness: Int, val spread: Int, var match: Int): Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readArrayList(String::class.java.classLoader) as ArrayList<String>,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(desc)
        parcel.writeArray(arrayOf(diseaseSymptoms))
        parcel.writeInt(seriousness)
        parcel.writeInt(spread)
        parcel.writeInt(match)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Disease> {
        override fun createFromParcel(parcel: Parcel): Disease {
            return Disease(parcel)
        }

        override fun newArray(size: Int): Array<Disease?> {
            return arrayOfNulls(size)
        }
    }

}