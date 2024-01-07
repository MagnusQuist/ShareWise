package com.sdu.sharewise.data.model

import android.os.Parcel
import android.os.Parcelable

data class Group(
    val groupUid: String?,
    val name: String?,
    val desc: String?,
    val color: String?,
    val ownerUid: String?,
    val members: MutableList<String?> = mutableListOf(),
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        TODO("members")
    ) {
    }

    // Secondary constructor with no-argument default values
    constructor() : this("", "", "", "", "", mutableListOf())

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        TODO("Not yet implemented")
    }

    companion object CREATOR : Parcelable.Creator<Group> {
        override fun createFromParcel(parcel: Parcel): Group {
            return Group(parcel)
        }

        override fun newArray(size: Int): Array<Group?> {
            return arrayOfNulls(size)
        }
    }
}