package eutros.botaniapp.common.item.lens;

import eutros.botaniapp.common.BotaniaPP;
import eutros.botaniapp.common.core.helper.ItemNBTHelper;
import eutros.botaniapp.common.item.BotaniaPPItems;
import eutros.botaniapp.common.utils.BotaniaPPFakePlayer;
import eutros.botaniapp.common.utils.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import vazkii.botania.api.internal.IManaBurst;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.wand.ICoordBoundItem;
import vazkii.botania.api.wand.IWandBindable;
import vazkii.botania.api.wand.IWandable;
import vazkii.botania.common.block.BlockPistonRelay;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.item.ModItems;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Optional;

public class BindingLens extends ItemLens implements ICoordBoundItem {

    private static final String TAG_COLOR1 = "color1";
    private static final String TAG_COLOR2 = "color2";
    private static final String TAG_BOUND_TILE_X = "boundTileX";
    private static final String TAG_BOUND_TILE_Y = "boundTileY";
    private static final String TAG_BOUND_TILE_Z = "boundTileZ";
    public static final BlockPos UNBOUND_POS = new BlockPos(0, -1, 0);

    public BindingLens(Properties properties) {
        super(properties);
        addPropertyOverride(new ResourceLocation(Reference.MOD_ID, "bound"),
                (stack, worldIn, entityIn) -> getBindingAttempt(stack).isPresent() ? 1 : 0);
    }

    @Override
    public int getLensColor(ItemStack stack) {
        return getBindingAttempt(stack).isPresent() ? getColor2(stack) : getColor1(stack);
    }

    public int getLensColorId(ItemStack stack) {
        return getBindingAttempt(stack).isPresent() ? getColorId2(stack) : getColorId1(stack);
    }

    public static int getColorId1(ItemStack stack) {
        return ItemNBTHelper.getInt(stack, TAG_COLOR1, 0);
    }

    public static int getColorId2(ItemStack stack) {
        return ItemNBTHelper.getInt(stack, TAG_COLOR2, 0);
    }

    public static int getColor1(ItemStack stack) {
        return colorOf(getColorId1(stack));
    }

    public static int getColor2(ItemStack stack) {
        return colorOf(getColorId2(stack), 180);
    }

    public static int colorOf(int colorId, int offset) {
        if(0 <= colorId && colorId <= 15) {
            return DyeColor.byId(colorId).getColorValue();
        } else {
            return Color.HSBtoRGB((float)((BotaniaPP.proxy.getWorldElapsedTicks() * 2L % 360L) + offset) / 360.0F, 1.0F, 1.0F);
        }
    }

    public static int colorOf(int colorId) {
        return colorOf(colorId, 0);
    }

    @Override
    protected boolean isBlacklist(ItemStack sourceLens, ItemStack compositeLens) {
        return compositeLens.getItem() == ModItems.lensFlare;
    }

    @Override
    public boolean collideBurst(IManaBurst burst, RayTraceResult traceResult, boolean isManaBlock, boolean dead, ItemStack stack) {
        if(!burst.isFake() && traceResult.getType() == RayTraceResult.Type.BLOCK) {
            World world = ((ThrowableEntity) burst).world;

            if(world.isRemote()) {
                return super.collideBurst(burst, traceResult, isManaBlock, dead, stack);
            }

            BlockPos pos = ((BlockRayTraceResult) traceResult).getPos();
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();

            BlockPos sourcePos = burst.getBurstSourceBlockPos();

            ItemStack composite = ((ItemLens) stack.getItem()).getCompositeLens(stack);

            boolean warp = false, paint = false;
            if(!composite.isEmpty()) {
                Item item = composite.getItem();
                if(item == ModItems.lensWarp) {
                    warp = true;
                } else if(item == ModItems.lensPaint) {
                    paint = true;
                }
            }

            if(paint) {
                ItemNBTHelper.setInt(composite, "color", getLensColorId(stack));
            }

            if(pos.equals(sourcePos) || (warp && block instanceof BlockPistonRelay)) {
                return super.collideBurst(burst, traceResult, isManaBlock, dead, stack);
            }

            Direction side = ((BlockRayTraceResult) traceResult).getFace();

            LivingEntity entity = ((ThrowableEntity) burst).getThrower();
            PlayerEntity player = entity instanceof PlayerEntity ? (PlayerEntity) entity : new BotaniaPPFakePlayer((ServerWorld) world);

            if(simBind(world, stack, pos, block, side, player)) {
                if(!sourcePos.equals(UNBOUND_POS)) {
                    TileEntity tileEntity = world.getTileEntity(sourcePos);
                    if (tileEntity != null)
                        tileEntity.markDirty();
                }
            }
        }
        return super.collideBurst(burst, traceResult, isManaBlock, dead, stack);
    }

    private boolean simBind(World world, ItemStack stack, BlockPos pos, Block block, Direction side, PlayerEntity player) {
        Optional<BlockPos> boundPos = getBindingAttempt(stack);

        if (boundPos.isPresent() && tryCompleteBinding(boundPos.get(), pos, stack, world, side, player)) {
            return true;
        }

        if(block instanceof IWandable) {
            TileEntity tile = world.getTileEntity(pos);

            if((tile instanceof IWandBindable && ((IWandBindable) tile).canSelect(player, stack, pos, side))
                    || block instanceof BlockPistonRelay) {
                if (!boundPos.isPresent() || !boundPos.get().equals(pos)) {
                    setBindingAttempt(stack, pos);
                    return true;
                }
            }
        }

        if(!world.isRemote && boundPos.isPresent()) {
            if(tryCompletePistonRelayBinding(world, boundPos.get(), pos)) {
                setBindingAttempt(stack, UNBOUND_POS);
                return true;
            }
        }

        return false;
    }

    private boolean tryCompleteBinding(BlockPos src, BlockPos dst, ItemStack stack, World world, Direction face, PlayerEntity player) {
        if(!dst.equals(src)) {

            TileEntity srcTile = world.getTileEntity(src);
            if(srcTile instanceof IWandBindable) {
                if(((IWandBindable) srcTile).bindTo(player, stack, dst, face)) {
                    VanillaPacketDispatcher.dispatchTEToNearbyPlayers(world, src);
                    setBindingAttempt(stack, UNBOUND_POS);
                }
                return true;
            }
        }
        return false;
    }

    private boolean tryCompletePistonRelayBinding(World world, BlockPos src, BlockPos pos) {
        if(src.equals(pos))
            return false;
        if(world.getBlockState(src).getBlock() instanceof BlockPistonRelay) { // Could this minimal checking cause issues with block protections?
            GlobalPos bindPos = GlobalPos.of(world.getDimension().getType(), src.toImmutable());
            GlobalPos currentPos = GlobalPos.of(world.getDimension().getType(), pos.toImmutable());

            ((BlockPistonRelay) ModBlocks.pistonRelay).mappedPositions.put(bindPos, currentPos);
            BlockPistonRelay.WorldData.get(world).markDirty();

            // TODO play ding?
            return true;
        }
        return false;
    }


    public static void setBindingAttempt(ItemStack stack, BlockPos pos) {
        ItemNBTHelper.setInt(stack, TAG_BOUND_TILE_X, pos.getX());
        ItemNBTHelper.setInt(stack, TAG_BOUND_TILE_Y, pos.getY());
        ItemNBTHelper.setInt(stack, TAG_BOUND_TILE_Z, pos.getZ());
    }

    public static Optional<BlockPos> getBindingAttempt(ItemStack stack) {
        int x = ItemNBTHelper.getInt(stack, TAG_BOUND_TILE_X, 0);
        int y = ItemNBTHelper.getInt(stack, TAG_BOUND_TILE_Y, -1);
        int z = ItemNBTHelper.getInt(stack, TAG_BOUND_TILE_Z, 0);
        return y < 0 ? Optional.empty() : Optional.of(new BlockPos(x, y, z));
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if(player.isSneaking()) {
            if(!world.isRemote)
                setBindingAttempt(stack, UNBOUND_POS);
            // TODO add ding sound
            return ActionResult.newResult(ActionResultType.SUCCESS, stack);
        }

        return ActionResult.newResult(ActionResultType.PASS, stack);
    }

    @Nonnull
    @Override
    public ActionResultType onItemUse(ItemUseContext ctx) {
        ItemStack stack = ctx.getItem();
        BlockPos pos = ctx.getPos();
        Direction side = ctx.getFace();
        PlayerEntity player = ctx.getPlayer();
        World world = ctx.getWorld();
        TileEntity tile = world.getTileEntity(pos);
        Block block = world.getBlockState(pos).getBlock();

        if((player == null || player.isSneaking()) &&
                ((tile instanceof IWandBindable &&
                ((IWandBindable) tile).canSelect(player, stack, pos, side)) ||
                block instanceof BlockPistonRelay)) {
            setBindingAttempt(stack, pos);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.FAIL;
    }

    public static ItemStack forColors(int color1, int color2) {
        ItemStack stack = new ItemStack(BotaniaPPItems.bindingLens);
        ItemNBTHelper.setInt(stack, TAG_COLOR1, color1);
        ItemNBTHelper.setInt(stack, TAG_COLOR2, color2);

        return stack;
    }

    @Override
    public BlockPos getBinding(ItemStack stack) {
        return getBindingAttempt(stack).orElse(null);
    }
}
