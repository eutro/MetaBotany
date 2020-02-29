package eutros.botaniapp.common.crafting.recipe.bouganvillea;

import eutros.botaniapp.api.recipe.IBouganvilleaInventory;
import eutros.botaniapp.api.recipe.RecipeBouganvillea;
import eutros.botaniapp.common.block.flower.functional.SubtileBouganvillea;
import eutros.botaniapp.common.item.BotaniaPPItems;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import org.jetbrains.annotations.NotNull;

public class RecipeBouganvilleaRename extends RecipeBouganvillea {

    private IRecipeSerializer<RecipeBouganvilleaRename> SERIALIZER = new SpecialRecipeSerializer<>(RecipeBouganvilleaRename::new);

    public RecipeBouganvilleaRename(ResourceLocation location) {
        super(location, null, null, SubtileBouganvillea.FALLBACK_GROUP);
    }

    @Override
    public boolean shouldTrigger(IBouganvilleaInventory inv) {
        ItemStack stack = inv.getTrigger().getItem();
        return stack.getCount() != 1 || stack.getItem() != BotaniaPPItems.BOTANIA_MANA_STRING;
    }

    @Override
    public boolean checkHead(ItemEntity entity) {
        return entity.getItem().getItem() == BotaniaPPItems.BOTANIA_MANA_STRING;
    }

    @NotNull
    @Override
    public ItemStack getCraftingResult(IBouganvilleaInventory inventory) {
        TextComponent tc = new StringTextComponent("");
        for(ItemEntity entity : inventory.allEntities()) {
            tc.appendSibling(entity.getItem().getDisplayName());
        }

        if(!tc.equals(new StringTextComponent("")))
            inventory.getTrigger().getItem().setDisplayName(tc);

        return inventory.getTrigger().getItem();
    }

    @NotNull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }
}
