package com.nabalive.data.core.dao;

import com.google.code.morphia.Morphia;
import com.google.code.morphia.dao.BasicDAO;
import com.mongodb.Mongo;
import com.nabalive.data.core.model.TmpData;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 12/7/11
 */

@Component("tmpDataDAO")
public class TmpDataDAO extends BasicDAO<TmpData, ObjectId> {
    @Autowired
    public TmpDataDAO(Morphia morphia, Mongo mongo, @Value("${mongo.database}") String database) {
        super(mongo, morphia, database);
        getDatastore().ensureIndexes();
        getDatastore().ensureCaps();
    }
}
