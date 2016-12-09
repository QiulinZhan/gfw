package cn.call110.utils;

import java.util.function.Consumer;

import io.realm.Realm;

/**
 * Created by zhan on 2016/11/23.
 */

public class RealmUtils {

    public static void execute(Consumer<Realm> consumer){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(r -> {
            consumer.accept(r);
        });
        realm.close();
    }

    public static void query(Consumer<Realm> consumer) {
        Realm realm = Realm.getDefaultInstance();
        consumer.accept(realm);
        realm.close();
    }
}
