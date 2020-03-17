package eutros.botaniapp.common.block.tinkerer.cart.handlers;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import eutros.botaniapp.api.carttinkerer.CartTinkerHandler;
import eutros.botaniapp.api.internal.config.Configurable;
import eutros.botaniapp.common.entity.cart.EntityGenericBlockCart;
import eutros.botaniapp.common.entity.cart.EntityGenericTileEntityCart;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.IProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ITickList;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static net.minecraft.state.properties.BlockStateProperties.*;

public class DefaultTinkerHandler extends CartTinkerHandler {

    public DefaultTinkerHandler() {
        super(new BlockState[]{});
    }
    
    @Configurable(path={"cart_tinkerer", "generic"},
            comment="If true, disable generic tinkering altogether.")
    public static boolean DISABLED = false;

    @Configurable(path={"cart_tinkerer", "generic"},
            comment="If false, unbreakable blocks like bedrock can be tinkered.")
    public static boolean DISABLE_UNBREAKABLE = true;
    
    @Configurable(path={"cart_tinkerer", "generic"},
            comment="Any blocks in this set will be blacklisted from generic tinkering.")
    public static Set<String> BLOCK_BLACKLIST = new HashSet<>(Arrays.asList(
            "minecraft:piston_head",
            "botania:cell_block",
            "botania:enchanter"
    ));

    @Configurable(path={"cart_tinkerer", "generic"},
            comment="Any blocks with a state property in this set will be blacklisted from generic tinkering.")
    public static Set<String> STATE_PROPERTY_BLACKLIST = new HashSet<>(Arrays.asList(
            BED_PART.toString(),
            DOUBLE_BLOCK_HALF.toString()
    ));

    @Configurable(path={"cart_tinkerer", "generic"},
            comment="Any blocks with a state in this list will be blacklisted from generic tinkering.")
    public static Multimap<String, Object> STATE_VALUE_BLACKLIST = HashMultimap.create();

    static {
        STATE_VALUE_BLACKLIST.put(EXTENDED.getName(), true);
    }

    @Configurable(path={"cart_tinkerer", "generic"},
            comment="Disable generic Tile Entity tinkering altogether.")
    public static boolean DISABLE_TILE_ENTITIES = false;
    
    @Configurable(path={"cart_tinkerer", "generic"},
            comment="Any blocks above this harvest level will be blacklisted from generic tinkering.")
    public static int HARVEST_LEVEL = 2;

    @Override
    public boolean doInsert(BlockPos sourcePos, BlockState sourceState, AbstractMinecartEntity destinationCart, World world, BlockPos tinkererPos) {
        if(DISABLED || sourceState.isAir(world, sourcePos)) {
            return false;
        }

        if(sourceState.getHarvestLevel() > HARVEST_LEVEL ||
                (sourceState.getBlockHardness(world, sourcePos) < 0 && DISABLE_UNBREAKABLE) ||
                BLOCK_BLACKLIST.contains(Optional.ofNullable(sourceState.getBlock().getRegistryName()).map(Object::toString).orElse("")) ||
                sourceState.getProperties().stream().map(Object::toString).map(STATE_PROPERTY_BLACKLIST::contains)
                .reduce(Boolean::logicalOr).orElse(false))
            return false;

        for(IProperty<?> property : sourceState.getProperties()) {
            if(STATE_VALUE_BLACKLIST.get(property.getName()).contains(sourceState.get(property)))
                return false;
        }

        if(sourceState.has(WATERLOGGED) && sourceState.get(WATERLOGGED))
            sourceState = sourceState.with(WATERLOGGED, false);

        TileEntity te = world.getTileEntity(sourcePos);
        EntityGenericBlockCart cart;
        if(te == null)
            cart = new EntityGenericBlockCart(world, destinationCart.getX(), destinationCart.getY(), destinationCart.getZ(), sourceState);
        else {
            if(DISABLE_TILE_ENTITIES)
                return false;

            cart = new EntityGenericTileEntityCart(world, destinationCart.getX(), destinationCart.getY(), destinationCart.getZ(), sourceState, te);
            te.read(new CompoundNBT()); // Completely and utterly remove the TE from existence.
        }
        if(sourceState.getBlock() instanceof BushBlock)
            cart.setGroundState(world.getBlockState(sourcePos.down()));

        ITickList<Block> pendingTicks = world.getPendingBlockTicks();
        if(pendingTicks.isTickScheduled(sourcePos, sourceState.getBlock())) {
            cart.proxyWorld.getPendingBlockTicks().scheduleTick(cart.getPosition(), sourceState.getBlock(), 1);
        }

        BlockState state = cart.getDisplayTile();
        state.neighborChanged(cart.proxyWorld, cart.getPosition(), state.getBlock(), tinkererPos, true);

        return doSwap(sourcePos, world.getFluidState(sourcePos).getBlockState(), destinationCart, cart, world, tinkererPos);
    }

    @Override
    public boolean doExtract(BlockPos destinationPos, BlockState destinationState, AbstractMinecartEntity sourceCart, World world, BlockPos tinkererPos) {
        BlockState state = sourceCart.getDisplayTile();

        IFluidState fluidState = world.getFluidState(destinationPos);
        if(state.has(WATERLOGGED) && fluidState.isSource() && fluidState.getFluid() == Fluids.WATER)
            state = state.with(WATERLOGGED, true);

        AbstractMinecartEntity cart = new MinecartEntity(world, sourceCart.getX(), sourceCart.getY(), sourceCart.getZ());
        boolean ret = doSwap(destinationPos, state, sourceCart, cart, world, tinkererPos);
        if(sourceCart instanceof EntityGenericTileEntityCart) {
            TileEntity tile = ((EntityGenericTileEntityCart) sourceCart).getTile();
            tile.validate();
            tile.setLocation(world, destinationPos);
            world.setTileEntity(destinationPos, tile);
        }
        return ret;
    }
}
