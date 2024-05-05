package lol.athena.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import lol.athena.Athena;
import lol.athena.plugin.Plugin;
import lol.athena.plugin.events.Listener;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.events.Event;

@AllArgsConstructor
public class EventRegistration {

    @Getter private Plugin plugin;
    @Getter private Listener listenerClass;
    @Getter private Method invokingMethod;

    public void call(Event event) {
        if (!plugin.isEnabled()) {
            return;
        }

        try {
            invokingMethod.invoke(listenerClass, event);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Athena.getInstance().getLogger().severe("An unhandled exception occurred during event execution for event " + listenerClass.getClass().getSimpleName());
            ex.printStackTrace();
        }
    }
}
