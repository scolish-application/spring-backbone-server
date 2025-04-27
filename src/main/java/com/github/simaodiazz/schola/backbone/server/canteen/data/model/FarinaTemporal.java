package com.github.simaodiazz.schola.backbone.server.canteen.data.model;

public enum FarinaTemporal {

    BREAKFAST("Pequeno Almoço"),
    LUNCH("Almoço"),
    DINNER("Jantar"),
    SNACK("Lanche");

    private final String description;

    FarinaTemporal(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
