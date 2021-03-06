package com.blakebr0.mysticalagriculture.api.lib;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IngredientNBT;
import net.minecraftforge.registries.ForgeRegistries;

public class LazyIngredient {
    public static final LazyIngredient EMPTY = new LazyIngredient(null, null, null) {
        @Override
        public Ingredient getIngredient() {
            return Ingredient.EMPTY;
        }
    };

    private String name;
    private CompoundNBT nbt;
    private Type type;
    private Ingredient ingredient;

    private LazyIngredient(String name, Type type, CompoundNBT nbt) {
        this.name = name;
        this.type = type;
        this.nbt = nbt;
    }

    public static LazyIngredient item(String name) {
        return item(name, null);
    }

    public static LazyIngredient item(String name, CompoundNBT nbt) {
        return new LazyIngredient(name, Type.ITEM, nbt);
    }

    public static LazyIngredient tag(String name) {
        return new LazyIngredient(name, Type.TAG, null);
    }

    public boolean isItem() {
        return this.type == Type.ITEM;
    }

    public boolean isTag() {
        return this.type == Type.TAG;
    }

    public Ingredient getIngredient() {
        if (this.ingredient == null) {
            this.ingredient = Ingredient.EMPTY;
            if (this.isTag()) {
                Tag<Item> tag = ItemTags.getCollection().get(new ResourceLocation(this.name));
                if (tag != null && !tag.getAllElements().isEmpty())
                    this.ingredient = Ingredient.fromTag(tag);
            } else if (this.isItem()) {
                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(this.name));
                if (item != null) {
                    if (this.nbt == null || this.nbt.isEmpty()) {
                        this.ingredient = Ingredient.fromItems(item);
                    } else {
                        ItemStack stack = new ItemStack(item);
                        stack.setTag(this.nbt);
                        this.ingredient = new NBTIngredient(stack);
                    }
                }
            }
        }

        return this.ingredient;
    }

    private enum Type {
        ITEM, TAG
    }

    private static class NBTIngredient extends IngredientNBT {
        private NBTIngredient(ItemStack stack) {
            super(stack);
        }
    }
}
