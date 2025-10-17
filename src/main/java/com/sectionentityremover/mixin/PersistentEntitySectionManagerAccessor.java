package com.sectionentityremover.mixin;

import net.minecraft.world.level.entity.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PersistentEntitySectionManager.class)
public interface PersistentEntitySectionManagerAccessor<T extends EntityAccess> {
    @Accessor
    public EntitySectionStorage<T> getSectionStorage();

    @Accessor
    public EntityLookup<T> getVisibleEntityStorage();

    @Accessor("visibleEntityStorage")
    public void setVisibleEntityStorage(EntityLookup<T> entityLookup);

    @Mutable
    @Accessor("entityGetter")
    public void setEntityGetter(LevelEntityGetter<T> entityGetter);
}
