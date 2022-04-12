package me.mindlessly.tokenmanager.gui;

import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class TokenManager extends GuiScreen {

	private GuiButton newButton = null;
	private int xSize = this.width - 10;
	private int ySize = this.height - 10;
	public int guiLeft = (this.width - xSize) / 2;
	public int guiTop = (this.height - ySize) / 2;

	public GuiTextField name;
	public GuiTextField uuid;
	public GuiTextField token;

	private ArrayList<GuiTextField> textFields = new ArrayList<>();

	@Override
	public void initGui() {
		textFields.clear();

		super.initGui();
		name = new GuiTextField(0, this.fontRendererObj, this.width / 2 - 100, this.guiTop + 64, 79,
				this.fontRendererObj.FONT_HEIGHT);
		name.setText("");
		name.setVisible(true);
		name.setFocused(true);
		name.setMaxStringLength(16);
		textFields.add(name);

		uuid = new GuiTextField(0, this.fontRendererObj, this.width / 2 - 100, this.guiTop + 96, 79,
				this.fontRendererObj.FONT_HEIGHT);
		uuid.setText("");
		uuid.setVisible(true);
		uuid.setMaxStringLength(32);
		textFields.add(uuid);

		token = new GuiTextField(0, this.fontRendererObj, this.width / 2 - 100, this.guiTop + 128, 79,
				this.fontRendererObj.FONT_HEIGHT);
		token.setText("");
		token.setVisible(true);
		token.setMaxStringLength(400);
		textFields.add(token);

		newButton = new GuiButton(0, this.width / 2 - 100, guiTop + 160, "Save & Exit");
		buttonList.add(newButton);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		boolean typed = false;
		for (GuiTextField text : textFields) {
			typed = typed || text.textboxKeyTyped(typedChar, keyCode);
		}
		if (!typed) {
			super.keyTyped(typedChar, keyCode);
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		for (GuiTextField text : textFields) {
			text.updateCursorCounter();
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		boolean prevFocused = false;
		boolean postFocused = false;
		for (GuiTextField text : textFields) {
			prevFocused = text.isFocused() || prevFocused;
			text.mouseClicked(mouseX, mouseY, mouseButton);
			postFocused = text.isFocused() || postFocused;
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);

		name.drawTextBox();
		uuid.drawTextBox();
		token.drawTextBox();

		name.drawString(fontRendererObj, "Name", name.xPosition, name.yPosition - fontRendererObj.FONT_HEIGHT - 2,
				16777215);
		uuid.drawString(fontRendererObj, "UUID", uuid.xPosition, uuid.yPosition - fontRendererObj.FONT_HEIGHT - 2,
				16777215);
		token.drawString(fontRendererObj, "Token", token.xPosition, token.yPosition - fontRendererObj.FONT_HEIGHT - 2,
				16777215);
	}

}
