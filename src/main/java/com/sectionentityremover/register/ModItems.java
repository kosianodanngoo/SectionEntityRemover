package com.sectionentityremover.register;

import com.sectionentityremover.SectionEntityRemover;
import com.sectionentityremover.item.SectionEntityRemoverItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SectionEntityRemover.MODID);

    public static RegistryObject<Item> SECTION_ENTITY_REMOVER = ITEMS.register("section_entity_remover", SectionEntityRemoverItem::new);

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
