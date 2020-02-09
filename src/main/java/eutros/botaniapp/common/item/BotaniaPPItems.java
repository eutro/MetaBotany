/*
 * This was taken in part from its counterpart in the Botania mod,
 * found at https://github.com/Vazkii/Botania/blob/master/src/main/java/vazkii/botania/common/item/ModItems.java
 */

package eutros.botaniapp.common.item;

import eutros.botaniapp.common.core.BotaniappCreativeTab;
import eutros.botaniapp.common.crafting.recipe.RecipeLensBinding;
import eutros.botaniapp.common.crafting.recipe.RecipeLensUnbinding;
import eutros.botaniapp.common.item.lens.BindingLens;
import eutros.botaniapp.common.utils.Reference;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(Reference.MOD_ID)
public class BotaniaPPItems {
    
    @ObjectHolder(Reference.ItemNames.LENS_PREFIX + "binding") public static BindingLens bindingLens;

    public static Item.Properties defaultBuilder() {
        return new Item.Properties().group(BotaniappCreativeTab.INSTANCE);
    }

    private static Item.Properties unstackable() {
        return defaultBuilder().maxStackSize(1);
    }

    @SubscribeEvent
    public static void registerItems(final RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> r = event.getRegistry();

        register(r, new BindingLens(unstackable()), Reference.ItemNames.LENS_PREFIX + "binding");
    }

    @SubscribeEvent
    public static void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        IForgeRegistry<IRecipeSerializer<?>> r = event.getRegistry();

        register(r, RecipeLensBinding.SERIALIZER, "lens_binding");
        register(r, RecipeLensUnbinding.SERIALIZER, "lens_unbinding");
    }

    public static <V extends IForgeRegistryEntry<V>> void register(IForgeRegistry<V> reg, IForgeRegistryEntry<V> thing, ResourceLocation name) {
        reg.register(thing.setRegistryName(name));
    }

    public static <V extends IForgeRegistryEntry<V>> void register(IForgeRegistry<V> reg, IForgeRegistryEntry<V> thing, String name) {
        register(reg, thing, new ResourceLocation(Reference.MOD_ID, name));
    }
}
