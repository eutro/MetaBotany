/*
 * This was taken in part from its counterpart in the Botania mod,
 * found at https://github.com/Vazkii/Botania/blob/master/src/main/java/vazkii/botania/client/core/handler/ColorHandler.java
 */

package eutros.metabotany.client.core.handler;

import eutros.metabotany.common.item.MetaBotanyItems;
import eutros.metabotany.common.item.lens.BindingLens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import vazkii.botania.api.mana.ILens;

public final class ColorHandler {

    private ColorHandler() {
    }

    public static void init() {

        ItemColors items = Minecraft.getInstance().getItemColors();

        IItemColor lensDyeHandler = (s, t) -> t == 0 ? ((ILens) s.getItem()).getLensColor(s) : -1;

        items.register((s, t) ->
                        t == 2 ? BindingLens.getColor2(s)
                               : t == 3 ? BindingLens.getColor1(s)
                                        : lensDyeHandler.getColor(s, t),
                MetaBotanyItems.bindingLens);

    }

}
