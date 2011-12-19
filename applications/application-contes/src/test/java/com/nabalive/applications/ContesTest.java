package com.nabalive.applications;

import com.nabalive.applications.contes.ContesApplication;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 12/19/11
 */
public class ContesTest {

    @Test
    public void conte() throws IOException {
        ContesApplication contesApplication = new ContesApplication();
        String rand = contesApplication.getRand();
        System.out.println(rand);
        assertTrue(rand.startsWith("http://"));
    }
}
