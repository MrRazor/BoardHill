package cz.uhk.boardhill.service;

import com.vaadin.flow.shared.Registration;

import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class MessageSentEventBroadcaster {

    static final Executor executor = Executors.newSingleThreadExecutor();
    static final LinkedList<Consumer<MessageSentEvent>> listeners = new LinkedList<>();

    private MessageSentEventBroadcaster() {
    }

    public static synchronized Registration register(Consumer<MessageSentEvent> listener) {
        listeners.add(listener);

        return () -> {
            synchronized (MessageSentEventBroadcaster.class) {
                listeners.remove(listener);
            }
        };
    }

    public static synchronized void broadcast(MessageSentEvent messageSentEvent) {
        for (Consumer<MessageSentEvent> listener : listeners) {
            executor.execute(() -> listener.accept(messageSentEvent));
        }
    }
}
