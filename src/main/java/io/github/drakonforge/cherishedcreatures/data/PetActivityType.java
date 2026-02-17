package io.github.drakonforge.cherishedcreatures.data;

import java.util.function.Supplier;

public enum PetActivityType implements Supplier<String> {
    Custom("Never triggered by the base mod"),
    Exploring("Traveling far distances with the pet by your side"),
    Feeding("Giving a pet food"),
    FeedingFavorite("Giving a pet its favorite food"),
    Combat("Performing a combat action with your pet by your side"),
    Looting("Finding precious items with your pet"),
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

