package io.github.drakonforge.cherishedcreatures.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.Object2FloatMapCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.CherishedCreaturesPlugin;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import java.util.Collection;
import org.jetbrains.annotations.Nullable;

// Simple persistent data holder for pet attributes.
public class PetAttributes implements Component<EntityStore> {

    public static final String HEALTH = "Health";
    public static final String STAMINA = "Stamina";
    public static final String MOUNT_BASE_SPEED = "MountBaseSpeed";
    public static final String MOUNT_GAIT_ACCELERATION = "MountGaitAcceleration";

    public static final PetAttributes EMPTY = new PetAttributes();
    public static final BuilderCodec<PetAttributes> CODEC = BuilderCodec.builder(
            PetAttributes.class, PetAttributes::new)
            .append(new KeyedCodec<>("BaseAttributes", new Object2FloatMapCodec<>(Codec.STRING, Object2FloatOpenHashMap::new, false), true), (petAttributes, attributeMap) -> petAttributes.attributes = attributeMap, petAttributes -> petAttributes.attributes)
            .documentation("TODO")
            .add()
            .append(new KeyedCodec<>("AttributeModifiers", new Object2FloatMapCodec<>(Codec.STRING, Object2FloatOpenHashMap::new, false)), (petAttributes, attributeMap) -> petAttributes.modifiers = attributeMap, petAttributes -> petAttributes.modifiers)
            .documentation("TODO")
            .add()
            .documentation("TODO")
            .build();

    public static ComponentType<EntityStore, PetAttributes> getComponentType() {
        return CherishedCreaturesPlugin.get().getPetAttributesComponentType();
    }

    // TODO: Definitely a better way to do this but it works lmao
    private Object2FloatMap<String> attributes = new Object2FloatOpenHashMap<>();
    private Object2FloatMap<String> modifiers = new Object2FloatOpenHashMap<>();

    public void putBaseAttribute(String key, float value) {
        attributes.put(key, value);
    }

    public void putAttributeModifier(String key, float value) {
        if (attributes.containsKey(key)) {
            modifiers.put(key, value);
        }
    }

    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }

    public float get(String key) {
        return attributes.getFloat(key) + modifiers.getOrDefault(key, 0.0f);
    }

    public float getBase(String key) {
        return attributes.getFloat(key);
    }

    public float getOrDefault(String key, float defaultValue) {
        if (attributes.containsKey(key)) {
            return get(key);
        }
        return defaultValue;
    }

    public Collection<String> getKeys() {
        return attributes.keySet();
    }

    @Nullable
    @Override
    public Component<EntityStore> clone() {
        PetAttributes clone = new PetAttributes();
        clone.attributes = new Object2FloatOpenHashMap<>(attributes);
        return clone;
    }
}
