package com.sectionentityremover;

import com.sectionentityremover.mixin.EntityAccessor;
import com.sectionentityremover.mixin.EntitySectionStorageAccessor;
import com.sectionentityremover.mixin.PersistentEntitySectionManagerAccessor;
import com.sectionentityremover.mixin.ServerLevelAccessor;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.*;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class RemoverUtil {
    public static void removeEntity(@NotNull Entity targetEntity) {
        if (targetEntity instanceof Player && !Config.IS_PLAYER_REMOVABLE.get()) {
            return;
        }
        if (targetEntity instanceof PartEntity<?> partEntity) {
            removeEntity(partEntity.getParent());
            return;
        }
        Level level = targetEntity.level();
        if (level instanceof ServerLevel serverLevel) {
            PersistentEntitySectionManager<Entity> entityManager = ((ServerLevelAccessor) serverLevel).getEntityManager();
            EntitySectionStorage<Entity> sectionStorage = ((PersistentEntitySectionManagerAccessor<Entity>) entityManager).getSectionStorage();
            long index = SectionPos.of(targetEntity.blockPosition()).asLong();
            EntitySection<Entity> tSection = sectionStorage.getSection(index);
            if (Objects.nonNull(tSection)) {
                EntitySection<Entity> newSection = new EntitySection(Entity.class, tSection.getStatus());
                List<Entity> entities = tSection.getEntities()
                        .filter(entity -> targetEntity!=entity)
                        .toList();
                for (Entity entity : entities) {
                    newSection.add(entity);
                }
                serverLevel.getChunkSource().removeEntity(targetEntity);
                ((EntitySectionStorageAccessor<Entity>) sectionStorage).getSections().replace(index, newSection);
            }
            EntityTickList entityTickList = ((ServerLevelAccessor) serverLevel).getEntityTickList();
            entityTickList.remove(targetEntity);
            EntityLookup<Entity> entityLookup = ((PersistentEntitySectionManagerAccessor<Entity>) entityManager).getVisibleEntityStorage();
            entityLookup.remove(targetEntity);
            if(entityLookup.getEntity(targetEntity.getId()) != null) {
                EntityLookup<Entity> newEntityLookup = new EntityLookup<Entity>();
                for (Entity entity : entityLookup.getAllEntities()) {
                    if (entity != targetEntity) {
                        newEntityLookup.add(entity);
                    }
                }
                ((PersistentEntitySectionManagerAccessor<Entity>) entityManager).setVisibleEntityStorage(newEntityLookup);
                LevelEntityGetter<Entity> newEntityGetter = new LevelEntityGetterAdapter<>(newEntityLookup, sectionStorage);
                ((PersistentEntitySectionManagerAccessor<Entity>) entityManager).setEntityGetter(newEntityGetter);
            }
            targetEntity.setRemoved(Entity.RemovalReason.KILLED);
            for(Entity.RemovalReason removalReason : Entity.RemovalReason.values()){
                targetEntity.remove(removalReason);
                targetEntity.setRemoved(removalReason);
            }
            if (!targetEntity.isRemoved()) {
                ((EntityAccessor) targetEntity).setRemovalReason(Entity.RemovalReason.KILLED);
                for(Entity.RemovalReason removalReason : Entity.RemovalReason.values()) {
                    if (targetEntity.isRemoved()) break;
                    ((EntityAccessor) targetEntity).setRemovalReason(removalReason);
                }
            }
            targetEntity.stopRiding();
            targetEntity.onRemovedFromWorld();
            targetEntity.invalidateCaps();
            if (targetEntity.isRemoved()) {
                PacketDistributor.DIMENSION.with(serverLevel::dimension).send(new ClientboundRemoveEntitiesPacket(targetEntity.getId()));
            }
            if (targetEntity instanceof ServerPlayer player) {
                player.connection.player = Objects.requireNonNull(player.getServer()).getPlayerList().respawn(player, false);
            }
        }
    }
}
