package com.sectionentityremover.item;

import com.sectionentityremover.RemoverUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SectionEntityRemoverItem extends Item {
    public SectionEntityRemoverItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        RemoverUtil.removeEntity(entity);
        return true;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity victim, LivingEntity attacker) {
        RemoverUtil.removeEntity(victim);
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        double reach = Math.max(player.getBlockReach(), player.getEntityReach());
        Vec3 view = player.getViewVector(1);
        Vec3 vector = view.scale(reach);
        Vec3 eye = player.getEyePosition();
        Entity target = null;
        for (Entity entity : player.level().getEntities(player, player.getBoundingBox().expandTowards(vector).inflate(1))) {
            AABB aabb = entity.getBoundingBox().inflate(entity.getPickRadius()).inflate(0.3F);
            Optional<Vec3> optional = aabb.clip(eye, eye.add(vector));
            if (optional.isPresent()) {
                if (!Objects.nonNull(target) || entity.distanceToSqr(player) < target.distanceToSqr(player)) {
                    target = entity;
                }
            }
        }
        if (Objects.nonNull(target)) {
            RemoverUtil.removeEntity(target);
            return InteractionResultHolder.success(player.getItemInHand(interactionHand));
        }
        return InteractionResultHolder.pass(player.getItemInHand(interactionHand));
    }
}
