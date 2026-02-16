package io.github.drakonforge.cherishedcreatures.corecomponents.builder;

import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.IEntityFilter;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderEntityFilterBase;
import io.github.drakonforge.cherishedcreatures.corecomponents.EntityFilterPetOwner;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class BuilderEntityFilterPetOwner extends BuilderEntityFilterBase {

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
    public IEntityFilter build(BuilderSupport builderSupport) {
        return new EntityFilterPetOwner(this);
    }

    @NullableDecl
    @Override
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Experimental;
    }
}
