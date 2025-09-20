package com.sectionentityremover.mixin;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.EntitySectionStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntitySectionStorage.class)
public interface EntitySectionStorageAccessor<T extends EntityAccess> {
    @Accessor
    public Long2ObjectMap<EntitySection<T>> getSections();
}
