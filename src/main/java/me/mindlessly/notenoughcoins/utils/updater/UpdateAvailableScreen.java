package me.mindlessly.notenoughcoins.utils.updater;

import gg.essential.universal.UDesktop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;

import java.net.URI;

public class UpdateAvailableScreen extends GuiScreen {
    private final String text;

    public UpdateAvailableScreen() {
        text = "A new update is available " + EnumChatFormatting.YELLOW + GitHub.getLatestVersion();
    }

    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 84, 200, 20, "View changelog"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 108, 98, 20, "Update now"));
        this.buttonList.add(new GuiButton(2, this.width / 2 + 2, this.height / 4 + 108, 98, 20, "Update at exit"));
        this.buttonList.add(new GuiButton(3, this.width / 2 - 100, this.height / 4 + 132, 200, 20, "Cancel"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        int yOffset = Math.min(this.height / 2, this.height / 4 + 80 - Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT * 2);
        drawCenteredString(Minecraft.getMinecraft().fontRendererObj, text, this.width / 2, yOffset - Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT - 2, 0xFFFFFFFF);
        drawCenteredString(Minecraft.getMinecraft().fontRendererObj, "Update now or when leaving Minecraft?", this.width / 2, yOffset, 0xFFFFFFFF);
        drawCenteredString(Minecraft.getMinecraft().fontRendererObj, "(Updating now will exit Minecraft after downloading update)", this.width / 2, yOffset + Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + 2, 0xFFFFFFFF);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button.id == 1 || button.id == 2) {
            // Update
            GitHub.showChangelog = true;
            Minecraft.getMinecraft().displayGuiScreen(new UpdatingScreen(button.id == 1));
        } else if (button.id == 3) {
            // Cancel
            Minecraft.getMinecraft().displayGuiScreen(null);
        } else if (button.id == 0) {
            // View changelog
            UDesktop.browse(URI.create("https://github.com/NotEnoughCoins/NotEnoughCoins/releases"));
        }
    }
}
