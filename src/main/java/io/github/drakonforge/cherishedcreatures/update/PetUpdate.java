package io.github.drakonforge.cherishedcreatures.update;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import java.util.UUID;

public class PetUpdate {

    public static final String PET_ADD = "PetAdd";
    public static final String PET_TRANSFER_SEND = "PetTransferSend";
    public static final String PET_TRANSFER_RECEIVE = "PetTransferReceive";
    public static final String PET_DECEASED = "PetDeceased";
    public static final String PET_ABANDON = "PetAbandon";

    public static final BuilderCodec<PetUpdate> CODEC = BuilderCodec.builder(PetUpdate.class,
                    PetUpdate::new)
            .append(new KeyedCodec<>("OwnerUUID", Codec.UUID_STRING, true),
                    (data, uuid) -> data.ownerUuid = uuid, data -> data.ownerUuid)
            .add()
            .append(new KeyedCodec<>("PetUUID", Codec.UUID_STRING),
                    (data, uuid) -> data.petUuid = uuid, data -> data.petUuid)
            .add()
            .build();
    // TODO: Codec

    private UUID ownerUuid;
    private UUID petUuid;
    private String operation;
    private boolean deliveredToOwner = false;
    private boolean deliveredToPet = false;

    public PetUpdate() {}

    public PetUpdate(UUID ownerUuid, UUID petUuid, String operation) {
        this(ownerUuid, petUuid, operation, false);
    }

    public PetUpdate(UUID ownerUuid, UUID petUuid, String operation, boolean skipPetDelivery) {
        this.ownerUuid = ownerUuid;
        this.petUuid = petUuid;
        this.operation = operation;
        this.deliveredToPet = skipPetDelivery;
    }

    public void markDeliveredToOwner() {
        deliveredToOwner = true;
    }

    public void markDeliveredToPet() {
        deliveredToPet = true;
    }

    public boolean completed() {
        return deliveredToOwner && deliveredToPet;
    }

    public boolean isDeliveredToOwner() {
        return deliveredToOwner;
    }

    public boolean isDeliveredToPet() {
        return deliveredToPet;
    }

    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    public UUID getPetUuid() {
        return petUuid;
    }
}
