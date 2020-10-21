package com.azamudin.personalsecureapp.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class UserProfile extends RealmObject {

    String userImage;

    String userFullName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @PrimaryKey
    String id;

    String userLocation;
    String userEmail;
    String userPhone;
    String userWorkPlace;
    String userFacebook;

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserWorkPlace() {
        return userWorkPlace;
    }

    public void setUserWorkPlace(String userWorkPlace) {
        this.userWorkPlace = userWorkPlace;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserFacebook() {
        return userFacebook;
    }

    public void setUserFacebook(String userFacebook) {
        this.userFacebook = userFacebook;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }
}
