package io.github.drakonforge.cherishedcreatures.sensor.builder;

import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.EnumHolder;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderSensorBase;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import io.github.drakonforge.cherishedcreatures.data.PetFollowMode;
import io.github.drakonforge.cherishedcreatures.sensor.SensorPetFollowMode;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class BuilderSensorPetFollowMode extends BuilderSensorBase {

    protected final EnumHolder<PetFollowMode> petFollowMode = new EnumHolder<>();

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
        return new SensorPetFollowMode(this, builderSupport);
    }

    @NullableDecl
    @Override
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Experimental;
    }

    public PetFollowMode getPetFollowMode(@Nonnull BuilderSupport support) {
        return petFollowMode.get(support.getExecutionContext());
    }
}
