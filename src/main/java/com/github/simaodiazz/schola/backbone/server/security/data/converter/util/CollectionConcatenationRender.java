package com.github.simaodiazz.schola.backbone.server.security.data.converter.util;

import java.util.Collection;

public final class CollectionConcatenationRender {

    private char separator;
    private Collection<String> collection;
    private String standard;

    private CollectionConcatenationRender() {
    }

    public static CollectionConcatenationRender options() {
        return new CollectionConcatenationRender();
    }

    public CollectionConcatenationRender separator(final char separator) {
        this.separator = separator;
        return this;
    }

    public CollectionConcatenationRender collection(final Collection<String> collection) {
        this.collection = collection;
        return this;
    }

    public CollectionConcatenationRender standard(final String standard) {
        this.standard = standard;
        return this;
    }

    public String render() {
        return collection
                .stream()
                .map(Object::toString)
                .reduce((a, b) -> a + separator + b)
                .orElse(standard);
    }
}
