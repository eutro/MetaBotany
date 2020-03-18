package eutros.botaniapp.common.block.tinkerer.cart;

import com.google.common.collect.Multimap;
import eutros.botaniapp.api.carttinkerer.CartTinkerHandler;
import eutros.botaniapp.api.internal.block.BlockRedstoneControlled;
import eutros.botaniapp.common.utils.MathUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

        List<AbstractMinecartEntity> carts = getCarts(world, pos);
        if(carts.isEmpty()) {
            return;
        }

        AbstractMinecartEntity cart = carts.get(world.rand.nextInt(carts.size()));

        BlockPos diff = pos.subtract(cart.getPosition());
        BlockPos opposite = pos.offset(Direction.getFacingFromVector(diff.getX(), diff.getY(), diff.getZ()));

        if(opposite.equals(from))
            return;

        BlockState oppositeState = world.getBlockState(opposite);

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
    private static List<AbstractMinecartEntity> getCarts(World world, BlockPos pos) {
        List<AbstractMinecartEntity> carts = new ArrayList<>();
        for(Direction dir : MathUtils.HORIZONTALS) {
            AxisAlignedBB aabb = new AxisAlignedBB(pos.offset(dir));
            carts.addAll(world.getEntitiesWithinAABB(AbstractMinecartEntity.class, aabb));
        }
        return carts;
    }
}
