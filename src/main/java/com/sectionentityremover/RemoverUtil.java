package com.sectionentityremover;

import com.mojang.logging.LogUtils;
import com.sectionentityremover.mixin.EntitySectionStorageAccessor;
import com.sectionentityremover.mixin.PersistentEntitySectionManagerAccessor;
import com.sectionentityremover.mixin.ServerLevelAccessor;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class RemoverUtil {
    public static void removeEntity(@NotNull Entity targetEntity) {
        LogUtils.getLogger().info("[Section Entity Remover] remove: {}, Class:{}", targetEntity, targetEntity.getClass());
        if (targetEntity instanceof Player && !Config.IS_PLAYER_REMOVABLE.get()) {
            return;
        }
        if (targetEntity instanceof PartEntity<?> partEntity) {
            removeEntity(partEntity.getParent());
            return;
        }
        Level level = targetEntity.level();
        LogUtils.getLogger().info("{}",level);
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
            ((PersistentEntitySectionManagerAccessor<Entity>) entityManager).getVisibleEntityStorage().remove(targetEntity);
            for(Entity.RemovalReason removalReason : Entity.RemovalReason.values()){
                targetEntity.setRemoved(removalReason);
            }
            targetEntity.onRemovedFromWorld();
        }
    }
}
