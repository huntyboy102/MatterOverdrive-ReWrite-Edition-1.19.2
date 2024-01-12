
package huntyboy102.moremod.gui.config;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class EnumConfigProperty extends Screen {
	private final List<String> validValues;
	private int selectedIndex = 0;
	private EditBox valueTextField;

	public EnumConfigProperty(List<String> validValues) {
		super(new Component("Title"));
		this.validValues = validValues;
	}

	@Override
	protected void init() {
		super.init();
		int buttonWidth = 100;
		int buttonHeight = 20;
		int x = (width- buttonWidth) / 2;
		int y = (height - buttonHeight) / 2;

		addWidget(new Button(x, y, buttonWidth, buttonHeight, new Component("Select Enum"), this::buttonPressed));
		valueTextField = new EditBox(font, x, y + 30, buttonWidth, buttonHeight, (Component) Component.EMPTY);
		valueTextField.setMaxLength(32767);
		valueTextField.setValue(validValues.get(selectedIndex));
		addWidget(valueTextField);
	}

	private void buttonPressed(Button button) {
		selectedIndex = (selectedIndex + 1) % validValues.size();
		valueTextField.setValue(validValues.get(selectedIndex));
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		renderBackground();
		super.render(mouseX, mouseY, partialTicks);

		// Draw additional components if needed
		RenderSystem.disableLighting();
		RenderSystem.disableBlend();
		valueTextField.render(mouseX, mouseY, partialTicks);
	}

	@Override
	public void tick() {
		super.tick();
		valueTextField.tick();
	}

	@Override
	public void renderBackground() {
		renderBackground(0);
	}

	@Override
	public void renderBackground(int tint) {
		fillGradient(0, 0, width, height, 0xFF000000, 0xFF000000);
	}
}
