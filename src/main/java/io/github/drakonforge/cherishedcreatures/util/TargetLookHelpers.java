package io.github.drakonforge.cherishedcreatures.util;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector2d;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.modules.collision.CollisionMath;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TargetUtil;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class TargetLookHelpers {
    private TargetLookHelpers() {}

    @Nullable
    public static Ref<EntityStore> getEntityNearestToCrosshair(@Nonnull Ref<EntityStore> originRef, double radius, double maxViewAngle, ComponentAccessor<EntityStore> store) {
        Transform lookTransform = TargetUtil.getLook(originRef, store);
        Vector3d lookPos = lookTransform.getPosition();
        Vector3d lookDir = lookTransform.getDirection().normalize();

        List<Ref<EntityStore>> targetEntities = TargetUtil.getAllEntitiesInSphere(lookPos, radius, store);

        Ref<EntityStore> bestTargetRef = null;
        boolean directlyLookingAt = false;
        double maxDot = 0.0; // Ignore everything past perpendicular by default
        double minDistanceSq = radius * radius;
        for (Ref<EntityStore> targetRef : targetEntities) {
            if (targetRef == null || !targetRef.isValid() || targetRef.equals(originRef)) {
                continue;
            }

            TransformComponent targetTransform = store.getComponent(targetRef, TransformComponent.getComponentType());
            BoundingBox targetBoundingBox = store.getComponent(targetRef, BoundingBox.getComponentType());
            assert targetTransform != null;

            Vector3d targetPos = targetTransform.getPosition().clone();
            if (targetBoundingBox != null) {
                Box boundingBox = targetBoundingBox.getBoundingBox();
                if (isHitByRay(boundingBox, targetPos, lookPos, lookDir)) {
                    double distSq = targetPos.distanceSquaredTo(lookPos);
                    if (distSq < minDistanceSq) {
                        bestTargetRef = targetRef;
                        minDistanceSq = distSq;
                    }
                    directlyLookingAt = true;
                }

                targetPos.add(boundingBox.middleY());
            }

            // If already looking directly at something, we only care about sorting the targets by distance
            // No need to look at nearby targets
            if (directlyLookingAt) {
                continue;
            }

            Vector3d toTarget = targetPos.subtract(lookPos).normalize();
            double dot = toTarget.dot(lookDir);

            if (dot > maxDot) {
                bestTargetRef = targetRef;
                maxDot = dot;
            }
        }

        if (!directlyLookingAt) {
            double angle = Math.acos(maxDot);
            if (angle > maxViewAngle) {
                return null;
            }
        }

        return bestTargetRef;
    }

    private static boolean isHitByRay(@Nonnull Box boundingBox, @Nonnull Vector3d position, @Nonnull Vector3d rayStart, @Nonnull Vector3d rayDir) {
        return CollisionMath.intersectRayAABB(rayStart, rayDir, position.getX(), position.getY(), position.getZ(), boundingBox, new Vector2d());
    }
}
