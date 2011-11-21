package com.nabalive.data.core.dao;

import com.google.code.morphia.Morphia;
import com.google.code.morphia.dao.BasicDAO;
import com.mongodb.Mongo;
import com.nabalive.data.core.model.Application;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/16/11
 */
@Component("applicationDAO")
public class ApplicationDAO extends BasicDAO<Application, ObjectId> {
    @Autowired
    public ApplicationDAO(Morphia morphia, Mongo mongo, @Value("${mongo.database}") String database) {
        super(mongo, morphia, database);
        getDatastore().ensureIndexes();
    }
}
