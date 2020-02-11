package eutros.botaniapp.common.block.tile.corporea;

import com.mojang.blaze3d.platform.GlStateManager;
import eutros.botaniapp.common.block.tile.corporea.matchers.AdvancedMatcher;
import eutros.botaniapp.common.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ObjectHolder;
import org.lwjgl.opengl.GL11;
import vazkii.botania.api.corporea.CorporeaHelper;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;
import vazkii.botania.api.corporea.ICorporeaRequestor;
import vazkii.botania.api.corporea.ICorporeaSpark;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.wand.ITileBound;
import vazkii.botania.common.block.tile.corporea.TileCorporeaFunnel;
import vazkii.botania.common.core.helper.InventoryHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TileAdvancedFunnel extends TileCorporeaBase implements ICorporeaRequestor, ITileBound {
    @ObjectHolder(Reference.MOD_ID + ":" + Reference.BlockNames.ADVANCED_FUNNEL)

    public static TileEntityType<TileAdvancedFunnel> TYPE;
    private static final String TAG_RANGE = "range";

    private int range = 3;

    public TileAdvancedFunnel() {
        super(TYPE);
    }

    public void doRequest() {
        ICorporeaSpark spark = getSpark();
        if(spark != null && spark.getMaster() != null) {
            List<ItemStack> filter = getFilter();
            if(!filter.isEmpty()) {
                assert world != null;
                ItemStack stack = filter.get(world.rand.nextInt(filter.size()));

                if(!stack.isEmpty())
                    doCorporeaRequest(AdvancedMatcher.fromItemStack(stack, true), stack.getCount(), spark);
            }
        }
    }

    public List<ItemStack> getFilter() {
        List<ItemStack> filter = new ArrayList<>();

        final int[] rotationToStackSize = new int[] {
                1, 2, 4, 8, 16, 32, 48, 64
        };

        for(Direction dir : Direction.values()) {
            assert world != null;
            List<ItemFrameEntity> frames = world.getEntitiesWithinAABB(ItemFrameEntity.class, new AxisAlignedBB(pos.offset(dir), pos.offset(dir).add(1, 1, 1)));
            for(ItemFrameEntity frame : frames) {
                Direction orientation = frame.getHorizontalFacing();
                if(orientation == dir) {
                    ItemStack stack = frame.getDisplayedItem();
                    if(!stack.isEmpty()) {
                        ItemStack copy = stack.copy();
                        copy.setCount(rotationToStackSize[frame.getRotation()]);
                        filter.add(copy);
                    }
                }
            }
        }

        return filter;
    }

    @Override
    public void doCorporeaRequest(ICorporeaRequestMatcher request, int count, ICorporeaSpark spark) {
        IItemHandler inv = getInv();

        List<ItemStack> stacks = CorporeaHelper.requestItem(request, count, spark, true);
        spark.onItemsRequested(stacks);
        for(ItemStack reqStack : stacks) {
            if(inv != null && ItemHandlerHelper.insertItemStacked(inv, reqStack, true).isEmpty())
                ItemHandlerHelper.insertItemStacked(inv, reqStack, false);
            else {
                assert world != null;
                ItemEntity item = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, reqStack);
                world.addEntity(item);
            }
        }
    }

    @Override
    public void writePacketNBT(CompoundNBT cmp) {
        super.writePacketNBT(cmp);
        cmp.putInt(TAG_RANGE, range);
    }

    @Override
    public void readPacketNBT(CompoundNBT cmp) {
        super.readPacketNBT(cmp);
        range = cmp.getInt(TAG_RANGE);
    }

    private IItemHandler getInv() {
        TileEntity te;
        IItemHandler ret;

        assert world != null;
        for(int down = 1; down < this.range; down++) {
            te = world.getTileEntity(pos.down(down));
            ret = InventoryHelper.getInventory(world, pos.down(down), Direction.UP);
            if (ret == null)
                ret = InventoryHelper.getInventory(world, pos.down(down), null);
            if (ret != null && !(te instanceof TileCorporeaFunnel))
                return ret;
        }

        return null;
    }

    private BlockPos getInvPos() {
        TileEntity te;
        IItemHandler itemHandler;

        assert world != null;
        for(int down = 1; down < this.range; down++) {
            te = world.getTileEntity(pos.down(down));
            itemHandler = InventoryHelper.getInventory(world, pos.down(down), Direction.UP);
            if (itemHandler == null)
                itemHandler = InventoryHelper.getInventory(world, pos.down(down), null);
            if (itemHandler != null && !(te instanceof TileCorporeaFunnel))
                return pos.down(down);
        }

        return null;
    }

    public boolean onWanded() {

        range = range == 9 ? 1 : range + 1;
        assert world != null;
        VanillaPacketDispatcher.dispatchTEToNearbyPlayers(world, pos);

        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public void renderHUD(Minecraft mc) {
        int color = 0xFF00CCCC;

        String rangeStr = TextFormatting.BOLD +
                I18n.format(String.join(".", "hud", Reference.MOD_ID, Reference.BlockNames.ADVANCED_FUNNEL, "pre")) +
                (range - 1) +
                I18n.format(String.join(".", "hud", Reference.MOD_ID, Reference.BlockNames.ADVANCED_FUNNEL, "post"));

        int x = mc.mainWindow.getScaledWidth() / 2 - mc.fontRenderer.getStringWidth(rangeStr) / 2;
        int y = mc.mainWindow.getScaledHeight() / 2 - 15;
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        mc.fontRenderer.drawStringWithShadow(rangeStr, x, y, color);
        GlStateManager.disableBlend();
    }

    @Nullable
    @Override
    public BlockPos getBinding() {
        return getInvPos();
    }
}
