package net.ralf2oo2.projecte.mixin;

import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.item.Items;
import net.modificationstation.stationapi.api.vanillafix.util.DyeColor;
import net.ralf2oo2.projecte.api.capability.AlchemicalBagEntityCapability;
import net.ralf2oo2.projecte.api.capability.KnowledgeEntityCapability;
import net.ralf2oo2.projecte.capability.InternalAbilitiesEntityCapability;
import net.ralf2oo2.projecte.capability.InternalTimersEntityCapability;
import net.ralf2oo2.projecte.item.AlchemicalBagItem;
import net.ralf2oo2.projecte.listener.ItemListener;
import net.ralf2oo2.projecte.screen.handler.AlchemicalBagScreenHandler;
import net.ralf2oo2.projecte.util.StackUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;
import java.util.Set;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "writeNbt", at = @At("TAIL"))
    public void projecte_writeKnowledgeNbt(NbtCompound nbt, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        KnowledgeEntityCapability capability = CapabilityHelper.getCapability(player, KnowledgeEntityCapability.class);

        if(capability != null) {
            nbt.put("ProjectE_Knowledge", capability.writeNbt());
        }
    }

    @Inject(method = "readNbt", at = @At("TAIL"))
    public void projecte_readKnowledgeNbt(NbtCompound nbt, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        if(nbt.contains("ProjectE_Knowledge")) {
            KnowledgeEntityCapability capability = CapabilityHelper.getCapability(player, KnowledgeEntityCapability.class);

            if(capability != null) {
                capability.readNbt(nbt.getCompound("ProjectE_Knowledge"));
            }
        }
    }

    @Inject(method = "writeNbt", at = @At("TAIL"))
    public void projecte_writeAlchemicalBagNbt(NbtCompound nbt, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        AlchemicalBagEntityCapability capability = CapabilityHelper.getCapability(player, AlchemicalBagEntityCapability.class);

        if(capability != null) {
            nbt.put("ProjectE_Alchemical", capability.writeNbt());
        }
    }

    @Inject(method = "readNbt", at = @At("TAIL"))
    public void projecte_readAlchemicalBagNbt(NbtCompound nbt, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        if(nbt.contains("ProjectE_Alchemical")) {
            AlchemicalBagEntityCapability capability = CapabilityHelper.getCapability(player, AlchemicalBagEntityCapability.class);

            if(capability != null) {
                capability.readNbt(nbt.getCompound("ProjectE_Alchemical"));
            }
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void projecte_tick(CallbackInfo ci) {
        InternalAbilitiesEntityCapability abilities = CapabilityHelper.getCapability((PlayerEntity) (Object) this, InternalAbilitiesEntityCapability.class);
        InternalTimersEntityCapability timers = CapabilityHelper.getCapability((PlayerEntity) (Object) this, InternalTimersEntityCapability.class);
        AlchemicalBagEntityCapability bags = CapabilityHelper.getCapability((PlayerEntity) (Object) this, AlchemicalBagEntityCapability.class);

        if(abilities != null && !((PlayerEntity) (Object) this).world.isRemote) {
            abilities.tick();
        }

        if(timers != null && !((PlayerEntity) (Object) this).world.isRemote) {
            timers.tick();
        }

        if(bags != null) {
            Set<DyeColor> colorsChanged = EnumSet.noneOf(DyeColor.class);

            for (DyeColor color : getBagColorsPresent((PlayerEntity) (Object) this))
            {
                Inventory inv = bags.getBag(color);
                for (int i = 0; i < inv.size(); i++)
                {
                    ItemStack current = inv.getStack(i);
                    if (!StackUtil.isEmpty(current) && current.getItem() instanceof net.ralf2oo2.projecte.api.item.AlchemicalBagItem bagItem
                                && bagItem.updateInAlchBag(inv, (PlayerEntity) (Object) this, current))
                    {
                        colorsChanged.add(color);
                    }
                }
            }

            if (!((PlayerEntity) (Object) this).world.isRemote)
            {
                for (DyeColor e : colorsChanged)
                {
                    if (((PlayerEntity) (Object) this).currentScreenHandler instanceof AlchemicalBagScreenHandler && ((PlayerEntity) (Object) this).getHand() != null && ((PlayerEntity) (Object) this).getHand().getItem() instanceof AlchemicalBagItem bagItem && bagItem.color == e) {
                        // Do not sync if this color is open, the container system does it for us
                        // and we'll stay out of its way.
                        continue;
                    } else {
                        bags.sync(e, (PlayerEntity) (Object) this);
                    }
                }
            }
        }
    }

    @Unique
    private static Set<DyeColor> getBagColorsPresent(PlayerEntity player)
    {
        Set<DyeColor> bagsPresent = EnumSet.noneOf(DyeColor.class);

        Inventory inv = player.inventory;
        for (int i = 0; i < inv.size(); i++)
        {
            ItemStack stack = inv.getStack(i);
            if (!StackUtil.isEmpty(stack) && stack.getItem() instanceof AlchemicalBagItem alchemicalBagItem)
            {
                bagsPresent.add(alchemicalBagItem.color);
            }
        }

        return bagsPresent;
    }
}
