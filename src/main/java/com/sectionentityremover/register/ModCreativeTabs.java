package com.sectionentityremover.register;

import com.sectionentityremover.SectionEntityRemover;
import com.sectionentityremover.item.SectionEntityRemoverItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SectionEntityRemover.MODID);

    public static final RegistryObject<CreativeModeTab> SECTION_ENTITY_REMOVER_TAB = TABS.register("section_entity_remover_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("tabs.section_entity_remover.section_entity_remover_tab"))
                    .icon(() -> ModItems.SECTION_ENTITY_REMOVER.get().getDefaultInstance())
                    .displayItems((pParam, pOutput) -> {
                        pOutput.accept(ModItems.SECTION_ENTITY_REMOVER.get());
                    })
                    .build()
    );

    public static void register(IEventBus bus) {
        TABS.register(bus);
    }
}
