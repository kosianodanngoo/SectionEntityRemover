package com.sectionentityremover;

import com.mojang.logging.LogUtils;
import com.sectionentityremover.register.ModCreativeTabs;
import com.sectionentityremover.register.ModItems;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.Collection;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SectionEntityRemover.MODID)
public class SectionEntityRemover {

    public static final String MODID = "section_entity_remover";

    public SectionEntityRemover() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);

        ModCreativeTabs.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @SubscribeEvent
    public void onRegisterCommand(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("section_entity_remover")
            .then(Commands.literal("remove")
                .requires(source -> source.hasPermission(3))
                .then(Commands.argument("targets", EntityArgument.entities())
                    .executes(context -> {
                        Collection<? extends Entity> entities = EntityArgument.getEntities(context, "targets");
                        for (Entity entity : entities) {
                            RemoverUtil.removeEntity(entity);
                        }
                        if (entities.size() == 1) {
                            context.getSource().sendSuccess(() -> Component.translatable("commands.section_entity_remover.remove.success.single", entities.iterator().next().getDisplayName()), true);
                        } else {
                            context.getSource().sendSuccess(() -> Component.translatable("commands.section_entity_remover.remove.success.multiple", entities.size()), true);
                        }
                        return entities.size();
                    })
                )
            )
        );
    }
}
