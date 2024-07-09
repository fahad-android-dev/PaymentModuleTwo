package com.orbits.paymentmoduletwo.interfaces

interface CommonInterfaceClickEvent {
    fun onItemClick(type: String, position: Int) {}
    fun onChildItemClick(type: String, parentPosition : Int, position: Int) {}
    fun onResultLocation(isLatLngSaved: Boolean) {}
    fun onToolBarListener(type: String) {}
    fun onFailure() {}
    fun onEmptyLayoutClick(clickFrom:String){}
}