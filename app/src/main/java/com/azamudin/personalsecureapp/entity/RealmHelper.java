package com.azamudin.personalsecureapp.entity;

import java.util.ArrayList;
import io.realm.Realm;
import io.realm.RealmResults;

public class RealmHelper {
    Realm realm;

    public RealmHelper(Realm realm) {
        this.realm = realm;
    }

    //WRITE
    public void save(final Category category) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Category s = realm.copyToRealm(category);
            }
        });
    }

    //READ
    public ArrayList<String> retrieve() {
        ArrayList<String> categoryName = new ArrayList<>();
        RealmResults<Category> spacecrafts = realm.where(Category.class).findAll();
        for (Category s : spacecrafts) {
            categoryName.add(s.getName());
        }
        return categoryName;
    }
}
