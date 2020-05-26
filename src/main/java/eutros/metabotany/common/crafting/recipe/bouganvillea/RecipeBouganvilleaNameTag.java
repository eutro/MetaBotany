package eutros.metabotany.common.crafting.recipe.bouganvillea;

import eutros.metabotany.api.recipe.IBouganvilleaInventory;
import eutros.metabotany.api.recipe.RecipeBouganvillea;
import eutros.metabotany.common.block.flower.functional.SubtileBouganvillea;
import eutros.metabotany.common.utils.MetaBotanyFakePlayer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.api.subtile.TileEntityFunctionalFlower;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class RecipeBouganvilleaNameTag extends RecipeBouganvillea {

    private static NonNullList<Ingredient> ingredients = NonNullList.create();
    private static ItemStack egg = new ItemStack(Items.CHICKEN_SPAWN_EGG);
    private static ItemStack named_egg;
    public static IRecipeSerializer<RecipeBouganvilleaNameTag> SERIALIZER = new SpecialRecipeSerializer<>(RecipeBouganvilleaNameTag::new);

    static {
        ItemStack name_tag = new ItemStack(Items.NAME_TAG);
        ITextComponent displayName = name_tag.getDisplayName();
        named_egg = egg.copy();
        name_tag.setDisplayName(displayName);
        named_egg.setDisplayName(displayName);
        ingredients.add(Ingredient.fromStacks(name_tag));
    }

    public RecipeBouganvilleaNameTag(ResourceLocation location) {
        super(location, named_egg, SubtileBouganvillea.BUILTIN_GROUP);
    }

    @Override
    public boolean shouldTrigger(IBouganvilleaInventory inventory) {
        return true;
    }

    @Override
    public boolean matches(@NotNull IBouganvilleaInventory inventory, @NotNull World world) {
        return inventory.getThrown().getItem().getItem() == Items.NAME_TAG;
    }

    @NotNull
    @Override
    public ItemStack getCraftingResult(@NotNull IBouganvilleaInventory inventory) {
        TileEntityFunctionalFlower flower = inventory.getFlower();
        ItemStack stack = inventory.getThrown().getItem();
        World world = flower.getWorld();
        if(world != null) {
            BlockPos efPos = flower.getEffectivePos();
            List<LivingEntity> allLiving = world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(efPos.add(-1, -1, -1), efPos.add(2, 2, 2)));
            List<LivingEntity> subjects = allLiving.stream().filter(e -> !e.hasCustomName()).collect(Collectors.toList());
            Random random = world.getRandom();
            PlayerEntity player = Optional.ofNullable(inventory.getThrown().getThrowerId()).map(world::getPlayerByUuid)
                    .orElseGet(() -> new MetaBotanyFakePlayer((ServerWorld) world));
            LivingEntity target;
            while(stack.getCount() > 0) {
                if(!subjects.isEmpty()) {
                    target = subjects.remove(random.nextInt(subjects.size()));
                    allLiving.remove(target);
                    stack.getItem().itemInteractionForEntity(stack, player, target, null);
                } else if(!allLiving.isEmpty()) {
                    target = allLiving.remove(random.nextInt(allLiving.size()));
                    stack.getItem().itemInteractionForEntity(stack, player, target, null);
                } else
                    break;
            }
        }
        return stack;
    }

    @NotNull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @NotNull
    @Override
    public NonNullList<Ingredient> getIngredients() {
        if(ingredients.size() == 1) {
            egg.setDisplayName(new StringTextComponent(I18n.format("metabotany.name_tag.animal")));
            ingredients.add(Ingredient.fromStacks(egg));
        }
        return ingredients;
    }

}
