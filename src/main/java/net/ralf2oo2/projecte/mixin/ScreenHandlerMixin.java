package net.ralf2oo2.projecte.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.ralf2oo2.projecte.screen.handler.TransmutationScreenHandler;
import net.ralf2oo2.projecte.screen.slot.OutputSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin {
    @Shadow
    public List slots;

    @WrapOperation(method = "onSlotClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxCount()I", ordinal = 3))
    public int projecte_cancleStackTakeWhenNoEmc(ItemStack instance, Operation<Integer> original, int index, int button, boolean shift, PlayerEntity player) {
        if(((ScreenHandler)(Object)this) instanceof TransmutationScreenHandler) {
            if(slots.get(index) instanceof OutputSlot outputSlot) {
                if(outputSlot.canTakeAmount(1)) {
                    outputSlot.consumeEmc(outputSlot.getStack(), 1);
                    return original.call(instance);
                } else {
                    return -1;
                }
            }
        }
        return original.call(instance);
    }

    @WrapOperation(method = "onSlotClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/Slot;setStack(Lnet/minecraft/item/ItemStack;)V", ordinal = 3))
    public void projecte_cancleStackDeletion(Slot instance, ItemStack stack, Operation<Void> original) {
        if(((ScreenHandler)(Object)this) instanceof TransmutationScreenHandler) {
            if(instance instanceof OutputSlot outputSlot) {
                return;
            }
        }
        original.call(instance, stack);
    }
}
