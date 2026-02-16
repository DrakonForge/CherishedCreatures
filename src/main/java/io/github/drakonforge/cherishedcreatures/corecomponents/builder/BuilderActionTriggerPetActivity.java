package io.github.drakonforge.cherishedcreatures.corecomponents.builder;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.EnumHolder;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import com.hypixel.hytale.server.npc.instructions.Action;
import io.github.drakonforge.cherishedcreatures.corecomponents.ActionTriggerPetActivity;
import io.github.drakonforge.cherishedcreatures.data.PetActivityType;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class BuilderActionTriggerPetActivity extends BuilderActionBase {

    protected final EnumHolder<PetActivityType> activityType = new EnumHolder<>();

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
    public Action build(BuilderSupport builderSupport) {
        return new ActionTriggerPetActivity(this, builderSupport);
    }

    @Override
    public Builder<Action> readConfig(@Nonnull JsonElement data) {
        this.requireEnum(data, "ActivityType", this.activityType, PetActivityType.class, BuilderDescriptorState.Experimental, "The pet activity type to trigger", null);
        return this;
    }

    @NullableDecl
    @Override
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Experimental;
    }

    public PetActivityType getBondingActivityType(@Nonnull BuilderSupport support) {
        return this.activityType.get(support.getExecutionContext());
    }
}
