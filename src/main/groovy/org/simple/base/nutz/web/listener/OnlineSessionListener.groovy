package org.simple.base.nutz.web.listener

import org.nutz.log.Log
import org.nutz.log.Logs
import org.simple.base.model.Function
import org.simple.base.nutz.model.Pair

import javax.servlet.http.HttpSession
import javax.servlet.http.HttpSessionEvent
import javax.servlet.http.HttpSessionListener
import java.util.concurrent.ConcurrentHashMap

public class OnlineSessionListener implements HttpSessionListener {

    private static final Log log = Logs.get()

    public static List<HttpSession> online = new ArrayList<HttpSession>()

    private static Map<String, Function<Pair<HttpSession, Action>, Object>> events =
            new ConcurrentHashMap<String, Function<Pair<HttpSession, Action>, Object>>()

    public static enum Action {
        CREATED, DESTROYED
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        online.add(se.getSession())

        for (Function<Pair<HttpSession, Action>, Object> func : events.values()) {
            try {
                func.run(new Pair(se.getSession(), Action.CREATED))
            } catch (Exception e) {
                log.error(e.getMessage(), e)
            }
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        online.remove(se.getSession())

        for (Function<Pair<HttpSession, Action>, Object> func : events.values()) {
            try {
                func.run(new Pair(se.getSession(), Action.DESTROYED))
            } catch (Exception e) {
                log.error(e.getMessage(), e)
            }
        }
    }

    public static void addEvent(String key, Function<Pair<HttpSession, Action>, Object> func) {
        events.put(key, func)
    }

    public static void removeEvent(String key) {
        events.remove(key)
    }
}

