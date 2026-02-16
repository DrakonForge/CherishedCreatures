package io.github.drakonforge.cherishedcreatures.corecomponents.builder;

import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import com.hypixel.hytale.server.npc.instructions.Action;
import io.github.drakonforge.cherishedcreatures.corecomponents.ActionOpenPetMenu;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class BuilderActionOpenPetMenu extends BuilderActionBase {

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
        return new ActionOpenPetMenu(this);
    }

    @NullableDecl
    @Override
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Experimental;
    }
}
