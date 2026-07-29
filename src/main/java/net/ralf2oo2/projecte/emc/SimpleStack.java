package net.ralf2oo2.projecte.emc;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.registry.ItemRegistry;
import net.modificationstation.stationapi.api.util.Identifier;
import net.ralf2oo2.projecte.ProjectE;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SimpleStack
{
    public final Identifier id;
    public final int damage;

    public SimpleStack(Identifier id, int damage)
    {
        this.id = id;
        this.damage = damage;
    }

    public SimpleStack(ItemStack stack)
    {
        // stack.isEmpty()
        if (stack == null || stack.count <= 0)
        {
            id = Identifier.of("minecraft:air");
            damage = 0;
        }
        else
        {
            id = ItemRegistry.INSTANCE.getId(stack.getItem());
            damage = stack.getDamage();
        }
    }

    public SimpleStack withMeta(int meta)
    {
        return new SimpleStack(id, meta);
    }

    public boolean isValid()
    {
        return !id.equals(Identifier.of("minecraft:air"));
    }


    @Nullable
    public ItemStack toItemStack()
    {
        if (isValid())
        {
            Item item = ItemRegistry.INSTANCE.get(id);

            if (item != null)
            {
                return new ItemStack(item, 1, damage);
            }
        }

        return null;
    }

    @Override
    public int hashCode()
    {
        int hash = 31 * id.hashCode();
        if (this.damage == ProjectE.WILDCARD_VALUE)
            hash = hash * 57 ^ this.damage;
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof SimpleStack other)
        {
            if (this.damage == ProjectE.WILDCARD_VALUE || other.damage == ProjectE.WILDCARD_VALUE)
            {
                return Objects.equals(this.id, other.id);
            }

            return Objects.equals(this.id, other.id) && this.damage == other.damage;
        }

        return false;
    }

    @Override
    public String toString()
    {
        Item obj = ItemRegistry.INSTANCE.get(id);

        if (obj != null)
        {
            return id + " " + damage;
        }

        return "id:" + id + " damage:" + damage;
    }
}
