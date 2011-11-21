package com.nabalive.data.core.factory;

import com.google.code.morphia.Morphia;
import com.google.code.morphia.validation.MorphiaValidation;

/**
 * Created by IntelliJ IDEA.
 * UserDAO: Julien Cheype
 * Date: 6/1/11
 */
public class MorphiaFactoryBean {
    private String packageName;

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Morphia createInstance(){
        Morphia morphia = new Morphia();
        MorphiaValidation morphiaValidation = new MorphiaValidation();
        morphiaValidation.applyTo(morphia);
        return morphia.mapPackage(packageName);
    }
}
