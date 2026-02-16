package io.github.drakonforge.cherishedcreatures.data;

import java.util.function.Supplier;

public enum PetActivityType implements Supplier<String> {
    Custom("Never triggered by the base mod"),
    Adventuring("Adventuring with the pet"),
    Petting("Petting your pet");

    private final String description;

    PetActivityType(String description) {
        this.description = description;
    }

    @Override
    public String get() {
        return this.description;
    }
}

