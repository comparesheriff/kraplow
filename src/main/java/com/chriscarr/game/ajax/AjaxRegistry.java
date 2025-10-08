package com.chriscarr.game.ajax;

import java.util.EnumMap;
import java.util.Map;

public final class AjaxRegistry {
    private final Map<MessageType, AjaxAction> actions = new EnumMap<>(MessageType.class);

    public AjaxRegistry register(MessageType type, AjaxAction action) {
        actions.put(type, action);
        return this;
    }

    public AjaxAction get(MessageType type) {
        return actions.get(type);
    }
}
