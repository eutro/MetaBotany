package eutros.botaniapp.common.block;

import eutros.botaniapp.common.block.corporea.BlockAdvancedFunnel;
import eutros.botaniapp.common.block.tile.corporea.TileAdvancedFunnel;
import eutros.botaniapp.common.utils.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import vazkii.botania.common.item.ModItems;

import static eutros.botaniapp.common.item.BotaniaPPItems.register;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(Reference.MOD_ID)
public class BotaniaPPBlocks {
    @ObjectHolder(Reference.BlockNames.ADVANCED_FUNNEL) public static BlockAdvancedFunnel advancedFunnel;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> evt) {
        IForgeRegistry<Block> r = evt.getRegistry();

        Block.Properties builder = Block.Properties.create(Material.IRON).hardnessAndResistance(5.5F).sound(SoundType.METAL);
        register(r, new BlockAdvancedFunnel(builder), Reference.BlockNames.ADVANCED_FUNNEL);
    }

    @SubscribeEvent
    public static void registerItemBlocks(RegistryEvent.Register<Item> evt) {
        IForgeRegistry<Item> r = evt.getRegistry();
        Item.Properties props = ModItems.defaultBuilder();

        register(r, new BlockItem(advancedFunnel, props), advancedFunnel.getRegistryName());
    }

    @SubscribeEvent
    public static void initTileEntities(RegistryEvent.Register<TileEntityType<?>> evt) {
        IForgeRegistry<TileEntityType<?>> r = evt.getRegistry();

        register(r, TileEntityType.Builder.create(TileAdvancedFunnel::new, advancedFunnel).build(null), Reference.BlockNames.ADVANCED_FUNNEL);
    }
}
