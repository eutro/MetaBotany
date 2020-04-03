package eutros.botaniapp.common.item;

import eutros.botaniapp.common.core.helper.ItemNBTHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemManaCompactedStacks extends Item {

    private static final String TAG_STACKS = "contained_stacks";

    public ItemManaCompactedStacks(Properties properties) {
        super(properties);
    }

    public static Stream<ItemStack> getStacks(ItemStack stack) {
        ListNBT list = ItemNBTHelper.getList(stack, TAG_STACKS, 10, true);
        if(list == null)
            return Stream.of();

        return list.stream()
                .filter(CompoundNBT.class::isInstance).map(CompoundNBT.class::cast)
                .map(ItemStack::read);
    }

    public static void setStacks(ItemStack stack, Stream<ItemStack> stacks) {
        ListNBT nbt = new ListNBT();
        nbt.addAll(stacks.map(s -> s.write(new CompoundNBT())).collect(Collectors.toList()));

        ItemNBTHelper.setList(stack, TAG_STACKS, nbt);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if(player.isSneaking()) {
            Stream<ItemStack> stacks = getStacks(stack);
            Stream.Builder<ItemStack> ret = Stream.builder();
            IItemHandler inv = new PlayerMainInvWrapper(player.inventory);
            boolean contains = false;
            for(Iterator<ItemStack> iterator = stacks.iterator(); iterator.hasNext(); ) {
                ItemStack s = iterator.next();
                s = ItemHandlerHelper.insertItemStacked(inv, s, false);

                if(!s.isEmpty()) {
                    contains = true;
                    ret.accept(s);
                }
            }
            setStacks(stack, ret.build());
            return contains ? ActionResult.success(stack) : ActionResult.consume(ItemStack.EMPTY);
        }
        return ActionResult.pass(stack);
    }

    @Nonnull
    @Override
    public ActionResultType onItemUse(ItemUseContext ctx) {
        BlockPos pos = ctx.getPos();
        World world = ctx.getWorld();
        Direction side = ctx.getFace();
        TileEntity tile = world.getTileEntity(pos);
        ItemStack stack = ctx.getItem();
        PlayerEntity player = ctx.getPlayer();

        if(player != null && !player.isSneaking())
            return ActionResultType.PASS;

        if(tile != null) {
            if(!world.isRemote) {
                IItemHandler inv;
                LazyOptional<IItemHandler> cap = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
                if(cap.isPresent()) {
                    inv = cap.orElseThrow(NullPointerException::new);
                } else if(tile instanceof IInventory) {
                    inv = new InvWrapper((IInventory) tile);
                } else return ActionResultType.FAIL;

                Stream<ItemStack> stacks = getStacks(stack);
                Stream.Builder<ItemStack> ret = Stream.builder();
                boolean contains = false;

                for(Iterator<ItemStack> it = stacks.iterator(); it.hasNext(); ) {
                    ItemStack s = it.next();

                    s = ItemHandlerHelper.insertItemStacked(inv, s, false);

                    if(!s.isEmpty()) {
                        contains = true;
                        ret.accept(s);
                    }
                }

                setStacks(stack, ret.build());
                if(!contains && player != null) {
                    player.setHeldItem(ctx.getHand(), ItemStack.EMPTY);
                    return ActionResultType.CONSUME;
                }
                return ActionResultType.SUCCESS;
            }
            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flags) {
        int count = Objects.requireNonNull(ItemNBTHelper.getList(stack, TAG_STACKS, 10, false)).size();
        ITextComponent c = new TranslationTextComponent("botaniapp.tooltip.stack_count").appendText(String.valueOf(count));
        Style style = c.getStyle();
        style.setItalic(true);
        style.setColor(TextFormatting.GRAY);
        tooltip.add(c);
        super.addInformation(stack, world, tooltip, flags);
    }

}
