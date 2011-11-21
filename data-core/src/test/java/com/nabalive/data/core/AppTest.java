package com.nabalive.data.core;


import com.nabalive.data.core.dao.NabaztagDAO;
import com.nabalive.data.core.dao.UserDAO;
import com.nabalive.data.core.model.Nabaztag;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Unit test for simple App.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/com/nabalive/data/core/bean.xml"})
public class AppTest {
    @Autowired
    UserDAO userDAO;

    @Autowired
    NabaztagDAO nabaztagDAO;

    @Test
    public void testUserDAOInjection() {
        assertNotNull(userDAO);
    }

    @Test
    public void testInserNabaztag() {
        Nabaztag n1 = new Nabaztag();
        Nabaztag n2 = new Nabaztag();

        n1.setName("n1");
        n2.setName("n2");

        n1.setMacAddress(UUID.randomUUID().toString());
        n2.setMacAddress(UUID.randomUUID().toString());
        
        nabaztagDAO.save(n1);
        nabaztagDAO.save(n2);
    }

}
