package doggytalents.client.screen.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import doggytalents.common.lib.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;

import net.minecraft.client.gui.components.Button.OnPress;

public class SmallButton extends Button {

    public SmallButton(int x, int y, Component text, OnPress onPress) {
        super(x, y, 12, 12, text, onPress);
    }

    @Override
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
       Minecraft mc = Minecraft.getInstance();
       Font font = mc.font;
       mc.getTextureManager().bind(Resources.SMALL_WIDGETS);
       RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
       int i = this.getYImage(this.isHovered());
       RenderSystem.enableBlend();
       RenderSystem.defaultBlendFunc();
       RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
       this.blit(stack, this.x, this.y, 0, i * 12, this.width, this.height);
       this.renderBg(stack, mc, mouseX, mouseY);
       int j = getFGColor();
       this.drawCenteredString(stack, font, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
    }
}
