package net.ralf2oo2.projecte.listener;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.item.Item;
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.vanillafix.util.DyeColor;
import net.ralf2oo2.projecte.ProjectE;
import net.ralf2oo2.projecte.item.*;
import net.ralf2oo2.projecte.util.Constants;

public class ItemListener {
    public static Item philosStone;
    public static Item[] alchBag;
    public static Item repairTalisman;
    public static Item[] kleinStars;
    public static Item alchemicalCoal;
    public static Item mobiusFuel;
    public static Item aeternalisFuel;
    public static Item lowCovalenceDust;
    public static Item mediumCovalenceDust;
    public static Item highCovalenceDust;
    public static Item redMatter;
    public static Item darkMatter;

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

        tome = new TomeItem(ProjectE.NAMESPACE.id("tome"));

        alchemicalCoal = new FuelItem(ProjectE.NAMESPACE.id("alchemical_coal"), Constants.ALCH_BURN_TIME);
        mobiusFuel = new FuelItem(ProjectE.NAMESPACE.id("mobius_fuel"), Constants.MOBIUS_BURN_TIME);
        aeternalisFuel = new FuelItem(ProjectE.NAMESPACE.id("aeternalis_fuel"), Constants.AETERNALIS_BURN_TIME);

        alchBag =  new Item[DyeColor.values().length];
        for(int i = 0; i < DyeColor.values().length; i++) {
            DyeColor color = DyeColor.values()[i];
            alchBag[i] = new AlchemicalBagItem(ProjectE.NAMESPACE.id(color.getName() + "_alchemical_bag"), color);
        }

        kleinStars = new Item[KleinStarItem.Tier.values().length];
        for(int i = 0; i < KleinStarItem.Tier.values().length; i++) {
            KleinStarItem.Tier tier = KleinStarItem.Tier.values()[i];
            kleinStars[i] = new KleinStarItem(ProjectE.NAMESPACE.id("klein_star_" + tier.name), tier);
        }

        redMatter = new TemplateItem(ProjectE.NAMESPACE.id("red_matter"));
        darkMatter = new TemplateItem(ProjectE.NAMESPACE.id("dark_matter"));
        lowCovalenceDust = new TemplateItem(ProjectE.NAMESPACE.id("low_covalence_dust"));
        mediumCovalenceDust = new TemplateItem(ProjectE.NAMESPACE.id("medium_covalence_dust"));
        highCovalenceDust = new TemplateItem(ProjectE.NAMESPACE.id("high_covalence_dust"));
    }
}
