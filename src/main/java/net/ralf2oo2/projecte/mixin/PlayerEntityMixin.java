package net.ralf2oo2.projecte.mixin;

import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.ralf2oo2.projecte.api.capability.AlchemicalBagEntityCapability;
import net.ralf2oo2.projecte.api.capability.KnowledgeEntityCapability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
                capability.readNbt(nbt.getCompound("ProjectE_Knowledge"));
            }
        }
    }
}
