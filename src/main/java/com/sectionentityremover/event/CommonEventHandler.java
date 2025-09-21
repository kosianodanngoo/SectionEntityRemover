package com.sectionentityremover.event;

import com.sectionentityremover.RemoverUtil;
import com.sectionentityremover.SectionEntityRemover;
import com.sectionentityremover.register.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SectionEntityRemover.MODID)
public class CommonEventHandler {
    @SubscribeEvent(receiveCanceled = true)
    public static void hurt(LivingHurtEvent event) {
        if (event.getSource().getDirectEntity() instanceof LivingEntity attacker && attacker.level() instanceof ServerLevel level) {
            if (attacker.getMainHandItem().is(ModItems.SECTION_ENTITY_REMOVER.get())) {
                RemoverUtil.removeEntity(event.getEntity());
            }
        }
    }
}
