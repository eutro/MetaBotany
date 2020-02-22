package eutros.botaniapp.common.block.tile;

import com.mojang.blaze3d.platform.GlStateManager;
import eutros.botaniapp.client.core.helper.HUDHelper;
import eutros.botaniapp.common.block.BotaniaPPBlocks;
import eutros.botaniapp.common.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;
import org.lwjgl.opengl.GL11;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.IManaReceiver;

import javax.annotation.Nonnull;
import java.util.Optional;

public class TileChargingPlate extends TileSimpleInventory implements IManaReceiver {
	@ObjectHolder(Reference.MOD_ID + ":" + Reference.BlockNames.CHARGING_PLATE)
	
	public static TileEntityType<TileChargingPlate> TYPE;
	
	public TileChargingPlate() {
        super(TYPE);
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    protected SimpleItemStackHandler createItemHandler() {
        return new SimpleItemStackHandler(this, true) {
            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if (!stack.isEmpty() && stack.getItem() instanceof IManaItem) {
                    return super.insertItem(slot, stack, simulate);
                }
                return stack;
            }

            @Override
            protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
                return 1;
            }
        };
    }


    @Override
    public boolean isFull() {
	    return getManaItem().map(item -> {
            ItemStack stack = itemHandler.getStackInSlot(0);
            return item.getMana(stack) >= item.getMaxMana(stack);
        }).orElse(true);
    }

    @Override
    public void recieveMana(int mana) {
	    getManaItem().ifPresent(item -> {
	        if(!isFull()) {
                ItemStack stack = itemHandler.getStackInSlot(0);
                item.addMana(stack, Math.min(mana, item.getMaxMana(stack)-item.getMana(stack)));
                assert this.world != null;
                this.world.updateComparatorOutputLevel(this.pos, this.getBlockState().getBlock());
            }
	    });
    }

    @Override
    public boolean canRecieveManaFromBursts() {
        return getManaItem().isPresent();
    }

    @Override
    public int getCurrentMana() {
        return getManaItem().map(item -> item.getMana(itemHandler.getStackInSlot(0))).orElse(0);
    }

    private Optional<IManaItem> getManaItem() {
        ItemStack stack = itemHandler.getStackInSlot(0);
        if(stack.isEmpty())
            return Optional.empty();
        return Optional.of((IManaItem) stack.getItem());
    }

    public void renderHUD(Minecraft mc) {
        int color = 0x4444FF;
        ItemStack stack = itemHandler.getStackInSlot(0);
        HUDHelper.drawSimpleManaHUD(color,
                getCurrentMana(),
                getManaItem().map(item -> item.getMaxMana(stack)).orElse(1),
                stack.isEmpty() ? I18n.format(BotaniaPPBlocks.chargingPlate.getTranslationKey()) : stack.getDisplayName().getFormattedText());

        int x = Minecraft.getInstance().mainWindow.getScaledWidth() / 2;
        int y = Minecraft.getInstance().mainWindow.getScaledHeight() / 2;

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        mc.textureManager.bindTexture(HUDHelper.MANA_HUD);

        net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
        mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, x+26, y-10);
        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();

        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
    }

    public int comparatorPower() {
	    ItemStack stack = itemHandler.getStackInSlot(0);
	    Optional<IManaItem> manaItem = getManaItem();
        return manaItem.map((item) -> {
            int raw = (int) ((double) item.getMana(stack) / (double) item.getMaxMana(stack) * 15.0);
            return Math.max(raw, 1);
        }).orElse(0);
    }
}