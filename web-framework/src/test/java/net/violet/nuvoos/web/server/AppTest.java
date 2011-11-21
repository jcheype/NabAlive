package net.violet.nuvoos.web.server;

import com.nabalive.framework.web.Route;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Map;

/**
 * Unit test for simple App.
 */
public class AppTest {
    @Test
    public void testRouteMatcher() {
        final Route route = new Route("/api/:name/:id");
        final Map<String, String> rez = route.parse("/api/julien/123", null);
        System.out.println(rez);
        assertEquals("should contain julien", rez.get("name"), "julien");
        assertEquals("should contain 123", rez.get("id"), "123");
    }

    @Test
    public void testRouteMatcherFalse() {
        final Route route = new Route("/api/:name/:id");
        final Map<String, String> rez = route.parse("/api/julien/toto/123", null);
        assertNull("should contain julien", rez);
    }
}
