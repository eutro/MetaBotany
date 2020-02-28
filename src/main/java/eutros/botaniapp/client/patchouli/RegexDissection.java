package eutros.botaniapp.client.patchouli;

import com.mojang.blaze3d.systems.RenderSystem;
import eutros.botaniapp.common.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.gui.BookTextRenderer;
import vazkii.patchouli.client.book.gui.GuiBookEntry;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;
import java.util.function.Supplier;

public class RegexDissection implements ICustomComponent {

    public String text;
    private transient final float sf = 1.2f;

    public static void init() {
        ResourceLocation regexDissection = new ResourceLocation(Reference.MOD_ID, "patchouli/templates/regex_dissection.json");

        Supplier<InputStream> regexDissectionSupplier = () -> {
            try {
                return Minecraft.getInstance().getResourceManager().getResource(regexDissection).getInputStream();
            } catch (IOException e) {
                System.out.println("Uh oh, stinky.");
            }
            return null;
        };

        PatchouliAPI.instance.registerTemplateAsBuiltin(new ResourceLocation("botania", "regex_dissection"), regexDissectionSupplier);
    }

    private transient int x, y;
    private transient BookTextRenderer renderer;

    @Override
    public void build(int componentX, int componentY, int pageNum) {
        this.x = (int) (componentX/sf);
        this.y = (int) (componentY/sf);
    }

    @Override
    public void onDisplayed(IComponentRenderContext context) {
        GuiBookEntry parent = (GuiBookEntry) context;

        renderer = new BookTextRenderer(parent, text, x, y);
    }

    @Override
    public void render(IComponentRenderContext context, float pTicks, int mouseX, int mouseY) {
        RenderSystem.scalef(sf, sf, 1);
        renderer.render(x + (int) ((mouseX-x)/sf), y + (int) ((mouseY-y)/sf));
        RenderSystem.scalef(1/sf, 1/sf, 1);
    }

    @Override
    public void onVariablesAvailable(Function<String, String> function) {}
}
