package com.azamudin.personalsecureapp.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ReceiptItem extends RealmObject {

    @PrimaryKey
    String orderNo;

    String imageString;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getImageString() {
        return imageString;
    }

    public void setImageString(String imageString) {
        this.imageString = imageString;
    }
}
