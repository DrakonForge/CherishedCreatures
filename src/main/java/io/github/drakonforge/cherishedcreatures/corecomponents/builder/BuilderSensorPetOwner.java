package io.github.drakonforge.cherishedcreatures.corecomponents.builder;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.Feature;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderSensorBase;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import io.github.drakonforge.cherishedcreatures.corecomponents.SensorPetOwner;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class BuilderSensorPetOwner extends BuilderSensorBase {

    @NullableDecl
    @Override
    public String getShortDescription() {
        return "TODO";
    }

    @NullableDecl
    @Override
    public String getLongDescription() {
        return "TODO";
    }

    @NullableDecl
    @Override
    public Sensor build(BuilderSupport builderSupport) {
        return new SensorPetOwner(this);
    }

    @NullableDecl
    @Override
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Experimental;
    }

    @Override
    public Builder<Sensor> readConfig(JsonElement data) {
        this.provideFeature(Feature.Player);
        return super.readConfig(data);
    }
}
