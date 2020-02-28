package eutros.botaniapp.common.block.flower;

import com.google.common.collect.ImmutableList;
import eutros.botaniapp.common.block.flower.functional.SubtileBouganvillea;
import eutros.botaniapp.common.item.BotaniaPPItems;
import eutros.botaniapp.common.utils.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.commons.lang3.tuple.Pair;
import vazkii.botania.api.subtile.TileEntitySpecialFlower;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;

import java.util.List;
import java.util.function.Supplier;

import static vazkii.botania.common.block.ModBlocks.register;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(Reference.MOD_ID)
public class BotaniaPPFlowers {
    @ObjectHolder(Reference.FlowerNames.BOUGANVILLEA) public static Block bouganvillea;

    @ObjectHolder("floating_" + Reference.FlowerNames.BOUGANVILLEA) public static Block bouganvilleaFloating;

    private static ResourceLocation floating(ResourceLocation orig) {
        return new ResourceLocation(orig.getNamespace(), "floating_" + orig.getPath());
    }

    private static ResourceLocation chibi(ResourceLocation orig) {
        return new ResourceLocation(orig.getNamespace(), orig.getPath() + "_chibi");
    }

    private static final List<Pair<Supplier<? extends TileEntitySpecialFlower>, ResourceLocation>> TYPES = ImmutableList.of(
            Pair.of(SubtileBouganvillea::new, new ResourceLocation(Reference.MOD_ID, Reference.FlowerNames.BOUGANVILLEA))
    );

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> evt) {
        IForgeRegistry<Block> r = evt.getRegistry();
        Block.Properties props = Block.Properties.from(Blocks.POPPY);
        Block.Properties floatProps = Block.Properties.create(Material.EARTH).hardnessAndResistance(0.5F).sound(SoundType.GROUND).lightValue(15);

        for (Pair<Supplier<? extends TileEntitySpecialFlower>, ResourceLocation> type : TYPES) {
            register(r, new BlockSpecialFlower(props, type.getLeft()), type.getValue());
            register(r, new BlockFloatingSpecialFlower(floatProps, type.getLeft()), floating(type.getValue()));
        }
    }

    @SubscribeEvent
    public static void registerItemBlocks(RegistryEvent.Register<Item> evt) {
        IForgeRegistry<Block> b = ForgeRegistries.BLOCKS;
        IForgeRegistry<Item> r = evt.getRegistry();
        Item.Properties props = BotaniaPPItems.defaultBuilder();

        for (Pair<Supplier<? extends TileEntitySpecialFlower>, ResourceLocation> type : TYPES) {
            Block block = b.getValue(type.getRight());
            Block floating = b.getValue(floating(type.getRight()));

            register(r, new ItemBlockSpecialFlower(block, props), type.getRight());
            register(r, new ItemBlockSpecialFlower(floating, props), floating(type.getRight()));
        }
    }

    @SubscribeEvent
    public static void registerTEs(RegistryEvent.Register<TileEntityType<?>> evt) {
        IForgeRegistry<Block> b = ForgeRegistries.BLOCKS;
        IForgeRegistry<TileEntityType<?>> r = evt.getRegistry();

        for (Pair<Supplier<? extends TileEntitySpecialFlower>, ResourceLocation> type : TYPES) {
            Block block = b.getValue(type.getRight());
            Block floating = b.getValue(floating(type.getRight()));
            register(r, TileEntityType.Builder.create(type.getLeft(), block, floating).build(null), type.getRight());
        }
    }
}
