package com.azamudin.personalsecureapp;

import io.realm.RealmObject;

/**
 * Created by anupamchugh on 09/02/16.
 */
public class DataModel{

    String name;
    String type;
    String version_number;
    String feature;

    public String getName() {
        return name;
    }


    public String getType() {
        return type;
    }


    public String getVersion_number() {
        return version_number;
    }


    public String getFeature() {
        return feature;
    }

}

