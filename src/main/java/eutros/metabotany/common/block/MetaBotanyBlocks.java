package eutros.metabotany.common.block;

import eutros.metabotany.common.block.corporea.BlockAdvancedFunnel;
import eutros.metabotany.common.block.tile.TileChargingPlate;
import eutros.metabotany.common.block.tile.TileLeakyPool;
import eutros.metabotany.common.block.tile.TilePoweredAir;
import eutros.metabotany.common.block.tile.TileSparkPainter;
import eutros.metabotany.common.block.tile.corporea.TileAdvancedFunnel;
import eutros.metabotany.common.block.tinkerer.BlockFrameTinkerer;
import eutros.metabotany.common.block.tinkerer.tile.TileFrameTinkerer;
import eutros.metabotany.common.item.MetaBotanyItems;
import eutros.metabotany.common.utils.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static eutros.metabotany.common.item.MetaBotanyItems.register;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(Reference.MOD_ID)
public class MetaBotanyBlocks {

    @ObjectHolder(Reference.BlockNames.ADVANCED_FUNNEL) public static BlockAdvancedFunnel advancedFunnel;
    @ObjectHolder(Reference.BlockNames.CHARGING_PLATE) public static BlockChargingPlate chargingPlate;
    @ObjectHolder(Reference.BlockNames.LEAKY_POOL) public static BlockLeakyPool leakyPool;
    @ObjectHolder(Reference.BlockNames.FRAME_TINKERER) public static BlockFrameTinkerer frameTinkerer;
    @ObjectHolder(Reference.BlockNames.POWERED_AIR) public static BlockPoweredAir poweredAir;

    public static Block BOTANIA_PISTON_RELAY;
    public static Block BOTANIA_MANA_VOID;
    public static Block BOTANIA_RED_STRING_RELAY;
    public static Block BOTANIA_MANA_POOL;
    public static Block BOTANIA_LIVINGROCK_BRICKS;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> evt) {
        IForgeRegistry<Block> r = evt.getRegistry();

        Block.Properties builder = Block.Properties.create(Material.IRON).hardnessAndResistance(5.5F).sound(SoundType.METAL);
        register(r, new BlockAdvancedFunnel(builder), Reference.BlockNames.ADVANCED_FUNNEL);

        builder = Block.Properties.create(Material.ROCK).hardnessAndResistance(2F, 6F);
        register(r, new BlockChargingPlate(builder), Reference.BlockNames.CHARGING_PLATE);
        register(r, new BlockLeakyPool(builder), Reference.BlockNames.LEAKY_POOL);

        builder = Block.Properties.create(Material.WOOD).hardnessAndResistance(1F, 3F);
        register(r, new BlockFrameTinkerer(builder), Reference.BlockNames.FRAME_TINKERER);

        register(r, new BlockPoweredAir(Block.Properties.create(Material.AIR)), Reference.BlockNames.POWERED_AIR);

        BlockSparkPainter.registerAll(r);
    }

    @SubscribeEvent
    public static void registerItemBlocks(RegistryEvent.Register<Item> evt) {
        IForgeRegistry<Item> r = evt.getRegistry();
        Item.Properties props = MetaBotanyItems.defaultBuilder();

        register(r, new BlockItem(advancedFunnel, props), advancedFunnel.getRegistryName());
        register(r, new BlockItem(chargingPlate, props), chargingPlate.getRegistryName());
        register(r, new BlockItem(leakyPool, props), leakyPool.getRegistryName());
        register(r, new BlockItem(frameTinkerer, props), frameTinkerer.getRegistryName());

        for(Block block : BlockSparkPainter.dyeMap.values())
            register(r, new BlockItem(block, props), block.getRegistryName());
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void getBotaniaBlocks(RegistryEvent.Register<Block> evt) {
        IForgeRegistry<Block> r = evt.getRegistry();

        final String b = "botania";

        BOTANIA_MANA_VOID = r.getValue(new ResourceLocation(b, "mana_void"));
        BOTANIA_PISTON_RELAY = r.getValue(new ResourceLocation(b, "piston_relay"));
        BOTANIA_RED_STRING_RELAY = r.getValue(new ResourceLocation(b, "red_string_relay"));
        BOTANIA_MANA_POOL = r.getValue(new ResourceLocation(b, "mana_pool"));
        BOTANIA_LIVINGROCK_BRICKS = r.getValue(new ResourceLocation(b, "livingrock_bricks"));
    }

    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public static void initTileEntities(RegistryEvent.Register<TileEntityType<?>> evt) {
        IForgeRegistry<TileEntityType<?>> r = evt.getRegistry();

        register(r, TileEntityType.Builder.create(TileAdvancedFunnel::new, advancedFunnel).build(null), Reference.BlockNames.ADVANCED_FUNNEL);
        register(r, TileEntityType.Builder.create(TileChargingPlate::new, chargingPlate).build(null), Reference.BlockNames.CHARGING_PLATE);
        register(r, TileEntityType.Builder.create(TileLeakyPool::new, leakyPool).build(null), Reference.BlockNames.LEAKY_POOL);
        register(r, TileEntityType.Builder.create(TileFrameTinkerer::new, frameTinkerer).build(null), Reference.BlockNames.FRAME_TINKERER);
        register(r, TileEntityType.Builder.create(TilePoweredAir::new, poweredAir).build(null), Reference.BlockNames.POWERED_AIR);
        register(r, TileEntityType.Builder.create(TileSparkPainter::new, BlockSparkPainter.dyeMap.values().toArray(new Block[0])).build(null), "spark_painter");
    }

}
