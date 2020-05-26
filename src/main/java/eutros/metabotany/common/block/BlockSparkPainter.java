package eutros.metabotany.common.block;

import eutros.metabotany.api.internal.block.BlockWaterloggable;
import eutros.metabotany.common.block.tile.TileSparkPainter;
import eutros.metabotany.common.core.network.MetaBotanyEffectPacket;
import eutros.metabotany.common.core.network.PacketHandler;
import eutros.metabotany.common.utils.MathUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.corporea.CorporeaHelper;
import vazkii.botania.api.internal.IManaBurst;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.ISparkEntity;
import vazkii.botania.common.entity.EntityCorporeaSpark;
import vazkii.botania.common.entity.EntitySparkBase;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.*;

import static eutros.metabotany.common.item.MetaBotanyItems.register;

// TODO resolve internal references

public class BlockSparkPainter extends BlockWaterloggable {

    public static Map<DyeColor, BlockSparkPainter> dyeMap = new EnumMap<>(DyeColor.class);
    private final VoxelShape BASE = makeCuboidShape(4, 0, 4, 12, 2, 12);
    private final VoxelShape ORB = makeCuboidShape(5, 5, 5, 11, 11, 11);
    private final VoxelShape SHAPE = VoxelShapes.combine(BASE, ORB, IBooleanFunction.OR);
    private final DyeColor color;

    private BlockSparkPainter(Properties builder, DyeColor color) {
        super(builder);
        this.color = color;
    }

    public static void registerAll(IForgeRegistry<Block> r) {
        Properties builder = Properties.create(Material.GLASS)
                .hardnessAndResistance(1F, 1F)
                .harvestLevel(0)
                .harvestTool(ToolType.PICKAXE);
        for(DyeColor color : DyeColor.values()) {
            BlockSparkPainter block = new BlockSparkPainter(builder, color);
            dyeMap.put(color, block);
            register(r, block, "spark_painter_" + color);
            BotaniaAPI.instance().registerPaintableBlock(block, dyeMap::get);
        }
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileSparkPainter(color);
    }

    @NotNull
    @ParametersAreNonnullByDefault
    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
        if(ctx instanceof EntitySelectionContext) { // Mana bursts look at the outline for some reason.
            if(ctx.getEntity() instanceof IManaBurst)
                return ORB;
        }
        return SHAPE;
    }

    @NotNull
    @ParametersAreNonnullByDefault
    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
        return BASE;
    }

    @ParametersAreNonnullByDefault
    @SuppressWarnings("deprecation")
    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
        List<EntitySparkBase> sparks = new LinkedList<>();

        for(Direction dir : MathUtils.HORIZONTALS) {
            BlockPos target = pos.offset(dir);
            TileEntity tile = world.getTileEntity(target);
            if(tile instanceof ISparkAttachable) {
                ISparkEntity spark = ((ISparkAttachable) tile).getAttachedSpark();
                if(spark != null) {
                    sparks.add((EntitySparkBase) spark);
                }
            }
            if(CorporeaHelper.instance().doesBlockHaveSpark(world, target)) {
                sparks.add((EntitySparkBase) CorporeaHelper.instance().getSparkForBlock(world, target));
            }
        }

        ByteBuffer buffer;
        for(EntitySparkBase spark : sparks) {
            buffer = ByteBuffer.allocate(8);
            buffer.putInt(spark.getEntityId());
            buffer.putInt(1);
            PacketHandler.sendToNearby(world,
                    pos,
                    new MetaBotanyEffectPacket(MetaBotanyEffectPacket.EffectType.SMOKE,
                            buffer.array()));
            spark.setNetwork(color);

            if(spark instanceof EntityCorporeaSpark) {
                try {
                    ObfuscationReflectionHelper.setPrivateValue(EntityCorporeaSpark.class, (EntityCorporeaSpark) spark, true, "firstTick");
                    ObfuscationReflectionHelper.findMethod(EntityCorporeaSpark.class, "restartNetwork").invoke(spark);
                } catch(IllegalAccessException | InvocationTargetException e) {
                    LOGGER.warn("Some hacky network restarting failed.");
                }
            }
        }
    }

    @Override
    public int getLightValue(BlockState state) {
        return 15;
    }

}
