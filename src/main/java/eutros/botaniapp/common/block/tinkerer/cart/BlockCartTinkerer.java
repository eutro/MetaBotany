package eutros.botaniapp.common.block.tinkerer.cart;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import eutros.botaniapp.api.carttinkerer.CartTinkerHandler;
import eutros.botaniapp.api.internal.block.BlockRedstoneControlled;
import eutros.botaniapp.common.utils.MathUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

import static eutros.botaniapp.common.registries.BotaniaPPRegistries.CART_TINKER;
import static eutros.botaniapp.common.registries.BotaniaPPRegistries.CART_TINKER_DEFAULT;

public class BlockCartTinkerer extends BlockRedstoneControlled {

    private static VoxelShape SHAPE = makeCuboidShape(0, 0, 0, 16, 3, 16);
    private long lastSwitch = 0;

    public BlockCartTinkerer(Properties properties) {
        super(properties);
    }

    @NotNull
    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void doPulse(BlockState state, BlockPos pos, World world, BlockPos from) {
        if(world.isRemote() || world.getGameTime() <= lastSwitch)
            return;

        // Prevent the cart tinkerer from being activated more than once per tick, causing unpredictable outcomes.
        lastSwitch = world.getGameTime();

        Multimap<Direction, AbstractMinecartEntity> carts = getCarts(world, pos);
        if(carts.isEmpty()) {
            return;
        }

        Pair<Direction, AbstractMinecartEntity> cartPair = Collections.max(carts.keySet().stream()
                        .flatMap((key) -> carts.get(key).stream().map(val -> Pair.of(key, val))).collect(Collectors.toList()),
                Comparator.comparingInt(a -> a.getRight().ticksExisted)); // get the oldest minecart, and the direction it came from.

        AbstractMinecartEntity cart = cartPair.getRight();

        BlockPos opposite = pos.offset(cartPair.getLeft().getOpposite());

        BlockState oppositeState = world.getBlockState(opposite);
        Collection<AbstractMinecartEntity> oppositeCarts = carts.get(cartPair.getLeft().getOpposite());

        if(!oppositeCarts.isEmpty()) {
            AbstractMinecartEntity oppositeCart = Collections.max(oppositeCarts, Comparator.comparingInt(a -> a.ticksExisted));
            CartHelper.swapCartPositions(cart, oppositeCart);
            world.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 1F, 1F);
            return;
        }

        if(opposite.equals(from))
            return;

        if(cart.getClass() == MinecartEntity.class) {
            Multimap<BlockState, ResourceLocation> stateMap =
                    CART_TINKER.getSlaveMap(TinkerHandlerMap.STATE_MAP_LOC, Multimap.class);
            Collection<ResourceLocation> locations = stateMap.get(oppositeState);
            locations.add(CART_TINKER_DEFAULT);

            for(ResourceLocation location : locations) {
                CartTinkerHandler handler = CART_TINKER.getValue(location);
                if(handler == null) {
                    continue;
                }

                if(handler.doInsert(opposite, oppositeState, cart, world, pos)) {
                    break;
                }
            }
        } else if(world.getBlockState(opposite) == world.getFluidState(opposite).getBlockState()) {
            Multimap<Class<? extends AbstractMinecartEntity>, ResourceLocation> cartMap =
                    CART_TINKER.getSlaveMap(TinkerHandlerMap.CART_MAP_LOC, Multimap.class);
            Class<? extends AbstractMinecartEntity> cartType = cart.getClass();
            Collection<ResourceLocation> locations;
            locations = new ArrayList<>(cartMap.get(cartType));
            locations.add(CART_TINKER_DEFAULT);

            for(ResourceLocation location : locations) {
                CartTinkerHandler handler = CART_TINKER.getValue(location);
                if(handler == null)
                    continue;

                if(handler.doExtract(opposite, oppositeState, cart, world, pos))
                    break;
            }
        }
    }

    @NotNull
    private static Multimap<Direction, AbstractMinecartEntity> getCarts(World world, BlockPos pos) {
        Multimap<Direction, AbstractMinecartEntity> carts = HashMultimap.create();
        for(Direction dir : MathUtils.HORIZONTALS) {
            AxisAlignedBB area = new AxisAlignedBB(pos.offset(dir));
            carts.putAll(dir,
                    world.getEntitiesWithinAABB(AbstractMinecartEntity.class,
                    area,
                    null));
        }
        return carts;
    }
}
