/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * <p>
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * <p>
 * File Created @ [Jan 21, 2014, 9:18:28 PM (GMT)]
 */
package eutros.metabotany.common.block.tile;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class TileMod extends TileEntity {

    public TileMod(TileEntityType<?> type) {
        super(type);
    }

    @Nonnull
    @Override
    public CompoundNBT write(@NotNull CompoundNBT cmp) {
        CompoundNBT ret = super.write(cmp);
        writePacketNBT(ret);
        return ret;
    }

    @Nonnull
    @Override
    public final CompoundNBT getUpdateTag() {
        return write(new CompoundNBT());
    }

    @Override
    public void read(@NotNull CompoundNBT cmp) {
        super.read(cmp);
        readPacketNBT(cmp);
    }

    public void writePacketNBT(CompoundNBT cmp) {
    }

    public void readPacketNBT(CompoundNBT cmp) {
    }

    @Override
    public final SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT tag = new CompoundNBT();
        writePacketNBT(tag);
        return new SUpdateTileEntityPacket(pos, -999, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        super.onDataPacket(net, packet);
        readPacketNBT(packet.getNbtCompound());
    }

}
