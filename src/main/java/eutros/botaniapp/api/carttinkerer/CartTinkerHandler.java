package eutros.botaniapp.api.carttinkerer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.Arrays;
import java.util.List;

/**
 * Handles the insertion and removal of blocks from mine carts, for the Cart Tinkerer.
 */
public abstract class CartTinkerHandler extends ForgeRegistryEntry<CartTinkerHandler> {

    public List<Class<? extends AbstractMinecartEntity>> cartTypes;
    public List<BlockState> workingStates;

    @SafeVarargs
    public CartTinkerHandler(BlockState[] workingStates, Class<? extends AbstractMinecartEntity>... cartTypes) {
        this.cartTypes = Arrays.asList(cartTypes);
        this.workingStates = Arrays.asList(workingStates);
    }

    @SafeVarargs
    public CartTinkerHandler(Block[] workingBlocks, Class<? extends AbstractMinecartEntity>... cartTypes) {
        this(Arrays.stream(workingBlocks).flatMap(b -> b.getStateContainer().getValidStates().stream()).toArray(BlockState[]::new), cartTypes);
    }

    public abstract boolean doInsert(BlockPos sourcePos, BlockState sourceState, AbstractMinecartEntity destinationCart, World world);

    public abstract boolean doExtract(BlockPos destinationPos, AbstractMinecartEntity sourceCart, World world);

    protected boolean doSwap(BlockPos pos,
                             BlockState newState,
                             AbstractMinecartEntity cart,
                             AbstractMinecartEntity newCart,
                             World world) {
        if(newState != null) {
            world.removeBlock(pos, false);
            world.setBlockState(pos, newState);
            world.notifyBlockUpdate(pos, newState, newState, 0);
        }
        cart.onKillCommand();
        world.addEntity(newCart);
        return true;
    }

}
