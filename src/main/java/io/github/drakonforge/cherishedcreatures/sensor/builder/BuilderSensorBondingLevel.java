package io.github.drakonforge.cherishedcreatures.sensor.builder;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.NumberArrayHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.IntSequenceValidator;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderSensorBase;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import io.github.drakonforge.cherishedcreatures.sensor.SensorBondingLevel;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class BuilderSensorBondingLevel extends BuilderSensorBase {
    protected final NumberArrayHolder levelRange = new NumberArrayHolder();

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
        return new SensorBondingLevel(this, builderSupport);
    }

    @NullableDecl
    @Override
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Experimental;
    }

    @Override
    public Builder<Sensor> readConfig(JsonElement data) {
        this.requireIntRange(data, "LevelRange", this.levelRange, IntSequenceValidator.between(
                1, 5), BuilderDescriptorState.Stable, "The bonding level range", "TODO");
        return this;
    }

    public int[] getLevelRange(@Nonnull BuilderSupport support) {
        return this.levelRange.getIntArray(support.getExecutionContext());
    }
}
