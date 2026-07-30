package net.ralf2oo2.projecte.listener;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.item.Item;
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent;
import net.ralf2oo2.projecte.ProjectE;
import net.ralf2oo2.projecte.item.TransmutationTabletItem;

public class ItemListener {
    public static Item philosStone;
    public static Item alchBag;
    public static Item repairTalisman;
    public static Item kleinStars;
    public static Item fuels;
    public static Item covalence;
    public static Item matter;

    public static Item dmPick;
    public static Item dmAxe;
    public static Item dmShovel;
    public static Item dmSword;
    public static Item dmHoe;
    public static Item dmShears;
    public static Item dmHammer;

    public static Item rmPick;
    public static Item rmAxe;
    public static Item rmShovel;
    public static Item rmSword;
    public static Item rmHoe;
    public static Item rmShears;
    public static Item rmHammer;
    public static Item rmKatar;
    public static Item rmStar;

    public static Item dmHelmet;
    public static Item dmChest;
    public static Item dmLegs;
    public static Item dmFeet;

    public static Item rmHelmet;
    public static Item rmChest;
    public static Item rmLegs;
    public static Item rmFeet;

    public static Item gemHelmet;
    public static Item gemChest;
    public static Item gemLegs;
    public static Item gemFeet;

    public static Item ironBand;
    public static Item blackHole;
    public static Item angelSmite;
    public static Item harvestGod;
    public static Item ignition;
    public static Item zero;
    public static Item swrg;
    public static Item timeWatch;
    public static Item everTide;
    public static Item volcanite;
    public static Item eternalDensity;
    public static Item dRod1;
    public static Item dRod2;
    public static Item dRod3;
    public static Item mercEye;
    public static Item voidRing;
    public static Item arcana;

    public static Item dCatalyst;
    public static Item hyperLens;
    public static Item cataliticLens;

    public static Item bodyStone;
    public static Item soulStone;
    public static Item mindStone;
    public static Item lifeStone;

    public static Item tome;

    public static Item waterOrb;
    public static Item lavaOrb;
    public static Item mobRandomizer;
    public static Item lensExplosive;
    public static Item fireProjectile;
    public static Item windProjectile;
    public static Item transmutationTablet;
    public static Item manual;

    @EventListener
    public void registerItems(ItemRegistryEvent event) {
        transmutationTablet = new TransmutationTabletItem(ProjectE.NAMESPACE.id("transmutation_tablet"));
    }
}
