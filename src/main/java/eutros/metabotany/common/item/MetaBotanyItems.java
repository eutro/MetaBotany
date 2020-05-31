/*
 * This was taken in part from its counterpart in the Botania mod,
 * found at https://github.com/Vazkii/Botania/blob/master/src/main/java/vazkii/botania/common/item/ModItems.java
 */

package eutros.metabotany.common.item;

import eutros.metabotany.common.core.MetaBotanyCreativeTab;
import eutros.metabotany.common.item.dispenser.BehaviourCorporeaSpark;
import eutros.metabotany.common.item.dispenser.BehaviourHangingItem;
import eutros.metabotany.common.item.dispenser.BehaviourSpark;
import eutros.metabotany.common.item.lens.AdvancedRedirectLens;
import eutros.metabotany.common.item.lens.BindingLens;
import eutros.metabotany.common.item.lens.RedstoneLens;
import eutros.metabotany.common.item.lens.UnresponsiveLens;
import eutros.metabotany.common.utils.Reference;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(Reference.MOD_ID)
public class MetaBotanyItems {

    @ObjectHolder(Reference.ItemNames.LENS_PREFIX + "binding") public static BindingLens bindingLens;
    @ObjectHolder(Reference.ItemNames.LENS_PREFIX + "redstone") public static RedstoneLens redstoneLens;
    @ObjectHolder(Reference.ItemNames.LENS_PREFIX + "redirect_plus") public static AdvancedRedirectLens redirectPlusLens;
    @ObjectHolder(Reference.ItemNames.LENS_PREFIX + "unresponsive") public static UnresponsiveLens unresponsiveLens;

    public static Item BOTANIA_ENTROPIC_LENS;
    public static Item BOTANIA_FLARE_LENS;
    public static Item BOTANIA_WARP_LENS;
    public static Item BOTANIA_PAINT_LENS;
    public static Item BOTANIA_MINE_LENS;
    public static Item BOTANIA_WEIGHT_LENS;

    public static Item BOTANIA_BLACK_HOLE_TALISMAN;
    public static Item BOTANIA_RED_STRING;
    public static Item BOTANIA_MANA_STRING;
    public static Item BOTANIA_SPARK;
    public static Item BOTANIA_CORPOREA_SPARK;
    public static Item BOTANIA_CORPOREA_SPARK_MASTER;
    public static Item BOTANIA_PIXIE_DUST;
    public static Item BOTANIA_TWIG_WAND;

    public static Item.Properties defaultBuilder() {
        return new Item.Properties().group(MetaBotanyCreativeTab.INSTANCE);
    }

    private static Item.Properties unstackable() {
        return defaultBuilder().maxStackSize(1);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> r = event.getRegistry();

        register(r, new BindingLens(unstackable()), Reference.ItemNames.LENS_PREFIX + "binding");
        register(r, new RedstoneLens(unstackable()), Reference.ItemNames.LENS_PREFIX + "redstone");
        register(r, new AdvancedRedirectLens(unstackable()), Reference.ItemNames.LENS_PREFIX + "redirect_plus");
        register(r, new UnresponsiveLens(unstackable()), Reference.ItemNames.LENS_PREFIX + "unresponsive");
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void getBotaniaItems(RegistryEvent.Register<Item> evt) {
        IForgeRegistry<Item> r = evt.getRegistry();

        final String b = "botania";

        BOTANIA_ENTROPIC_LENS = r.getValue(new ResourceLocation(b, "lens_explosive"));
        BOTANIA_FLARE_LENS = r.getValue(new ResourceLocation(b, "lens_flare"));
        BOTANIA_WARP_LENS = r.getValue(new ResourceLocation(b, "lens_warp"));
        BOTANIA_PAINT_LENS = r.getValue(new ResourceLocation(b, "lens_paint"));
        BOTANIA_MINE_LENS = r.getValue(new ResourceLocation(b, "lens_mine"));
        BOTANIA_WEIGHT_LENS = r.getValue(new ResourceLocation(b, "lens_weight"));

        BOTANIA_RED_STRING = r.getValue(new ResourceLocation(b, "red_string"));
        BOTANIA_MANA_STRING = r.getValue(new ResourceLocation(b, "mana_string"));
        BOTANIA_SPARK = r.getValue(new ResourceLocation(b, "spark"));
        BOTANIA_CORPOREA_SPARK = r.getValue(new ResourceLocation(b, "corporea_spark"));
        BOTANIA_CORPOREA_SPARK_MASTER = r.getValue(new ResourceLocation(b, "corporea_spark_master"));
        BOTANIA_BLACK_HOLE_TALISMAN = r.getValue(new ResourceLocation(b, "black_hole_talisman"));
        BOTANIA_PIXIE_DUST = r.getValue(new ResourceLocation(b, "pixie_dust"));
        BOTANIA_TWIG_WAND = r.getValue(new ResourceLocation(b, "twig_wand"));
    }

    public static <V extends IForgeRegistryEntry<V>> void register(IForgeRegistry<V> reg, IForgeRegistryEntry<V> thing, ResourceLocation name) {
        reg.register(thing.setRegistryName(name));
    }

    public static <V extends IForgeRegistryEntry<V>> void register(IForgeRegistry<V> reg, IForgeRegistryEntry<V> thing, String name) {
        register(reg, thing, new ResourceLocation(Reference.MOD_ID, name));
    }

    public static void addDispenserBehaviours() {
        DispenserBlock.registerDispenseBehavior(BOTANIA_SPARK, new BehaviourSpark());
        DispenserBlock.registerDispenseBehavior(BOTANIA_CORPOREA_SPARK, new BehaviourCorporeaSpark());
        DispenserBlock.registerDispenseBehavior(BOTANIA_CORPOREA_SPARK_MASTER, new BehaviourCorporeaSpark());

        IDispenseItemBehavior behaviour = new BehaviourHangingItem();
        DispenserBlock.registerDispenseBehavior(Items.ITEM_FRAME, behaviour);
        DispenserBlock.registerDispenseBehavior(Items.PAINTING, behaviour);
    }

}
