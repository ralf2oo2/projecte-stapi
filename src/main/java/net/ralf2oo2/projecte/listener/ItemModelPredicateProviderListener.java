package net.ralf2oo2.projecte.listener;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.BlockView;
import net.modificationstation.stationapi.api.client.event.render.model.ItemModelPredicateProviderRegistryEvent;
import net.modificationstation.stationapi.api.client.model.item.ItemModelPredicateProvider;
import net.ralf2oo2.projecte.ProjectE;
import net.ralf2oo2.projecte.item.ProjectEItem;
import org.jetbrains.annotations.Nullable;

public class ItemModelPredicateProviderListener {
    @EventListener
    public void registerItemModelPredicateProviders(ItemModelPredicateProviderRegistryEvent event) {
        event.registry.register(ItemListener.swrg, ProjectE.NAMESPACE.id("mode"), new ModePredicate());
    }

    public static class ModePredicate implements ItemModelPredicateProvider {
        @Override
        public float call(ItemStack stack, @Nullable BlockView world, @Nullable LivingEntity entity, int seed) {
            return stack.getStationNbt().getInt(ProjectEItem.TAG_MODE);
        }
    }
}
