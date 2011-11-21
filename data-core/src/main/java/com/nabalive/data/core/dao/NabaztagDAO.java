package com.nabalive.data.core.dao;

import com.google.code.morphia.Morphia;
import com.google.code.morphia.dao.BasicDAO;
import com.mongodb.Mongo;
import com.nabalive.data.core.model.Nabaztag;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/16/11
 */
@Component("nabaztagDAO")
public class NabaztagDAO extends BasicDAO<Nabaztag, ObjectId> {
    @Autowired
    public NabaztagDAO(Morphia morphia, Mongo mongo, @Value("${mongo.database}") String database) {
        super(mongo, morphia, database);
        getDatastore().ensureIndexes();
    }
}
