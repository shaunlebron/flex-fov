package mod.render360.coretransform.gui;

import java.io.IOException;

import mod.render360.coretransform.RenderUtil;
import mod.render360.coretransform.render.Flex;
import mod.render360.coretransform.render.RenderMethod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class Render360Settings extends GuiScreen {

	private final GuiScreen parentGuiScreen;
	public static final String screenTitle = "Render 360 Settings";

	private GuiButton buttonDefault;
	private GuiButton buttonSimple;
	private GuiButton buttonAdvanced;

	private Slider defaultFov;
	private GuiButton[] defaultButtons;

	private Slider simpleFov;
	private GuiButton simpleRubix;
	private GuiButton[] simpleButtons;

	private GuiButton[] advancedButtons;
	
	//Load options from file. Run only once.
	/*static {
		File optionsFile = new File("optionsRender360.txt");
		try {
			List<String> options = Files.readAllLines(Paths.get("optionsRender360.txt"));
			//TODO actually read
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/

	public Render360Settings(GuiScreen parentScreenIn)
	{
		this.parentGuiScreen = parentScreenIn;
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
	 * window resizes, the buttonList is cleared beforehand.
	 */
	@Override
	public void initGui() {
		buttonDefault = new GuiButton(18100, super.width / 2 - 194, super.height / 6 - 12, 130, 20, "Default");
		buttonSimple = new GuiButton(18200, super.width / 2 - 65, super.height / 6 - 12, 130, 20, "Flex");
		buttonAdvanced = new GuiButton(18300, super.width / 2 + 64, super.height / 6 - 12, 130, 20, "Advanced");
		buttonDefault.enabled = !RenderUtil.renderMethod.getName().equals("Standard"); //TODO find a better way
		buttonSimple.enabled = !RenderUtil.renderMethod.getName().equals("Flex");
		buttonAdvanced.enabled = !RenderUtil.renderMethod.getName().equals("Advanced");
		super.buttonList.add(buttonDefault);
		super.buttonList.add(buttonSimple);
		super.buttonList.add(buttonAdvanced);

		defaultFov = new Slider(new Responder(), 18101, width / 2 - 75, height / 6 + 21 - 6, 150, 20, "FOV", 30f, 110f, this.mc.gameSettings.fovSetting, 1f, null);
		defaultFov.visible = !buttonDefault.enabled;
		super.buttonList.add(defaultFov);

		simpleFov = new Slider(new Responder(), 18201, width / 2 - 180, height / 6 + 21 - 6, 360, 20, "FOV", 0f, 360f, Flex.fov, 1f, null);
		simpleRubix = new GuiButton(18202, super.width / 2 - 155, super.height / 6 + 48 - 6, 150, 20, "Rubix: " + (Flex.rubix ? "ON" : "OFF"));
		simpleFov.visible = !buttonSimple.enabled;
		simpleRubix.visible = !buttonSimple.enabled;
		super.buttonList.add(simpleFov);
		super.buttonList.add(simpleRubix);

		defaultButtons = new GuiButton[] {defaultFov};
		simpleButtons = new GuiButton[] {simpleFov, simpleRubix};

		super.buttonList.add(new GuiButton(200, super.width / 2 - 100, super.height / 6 + 168, I18n.format("gui.done", new Object[0])));
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
	 */
	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		if (button.enabled)
		{
			switch (button.id) {
				case 200:
					this.mc.displayGuiScreen(this.parentGuiScreen);
					break;
				case 18100:
					button.enabled = false;
					buttonSimple.enabled = true;
					buttonAdvanced.enabled = true;

					for (GuiButton b : defaultButtons) {
						b.visible = true;
					}

					for (GuiButton b : simpleButtons) {
						b.visible = false;
					}

					RenderUtil.renderMethod = RenderMethod.getRenderMethod(0); //FIXME use name, not index
					//RenderUtil.forceReload();
					break;
				case 18200:
					buttonDefault.enabled = true;
					button.enabled = false;
					buttonAdvanced.enabled = true;

					for (GuiButton b : defaultButtons) {
						b.visible = false;
					}

					for (GuiButton b : simpleButtons) {
						b.visible = true;
					}

					RenderUtil.renderMethod = RenderMethod.getRenderMethod(1);
					RenderUtil.forceReload();
					break;
				case 18300:
					buttonDefault.enabled = true;
					buttonSimple.enabled = true;
					button.enabled = false;
					break;

				case 18202:
					Flex.rubix = !Flex.rubix;
					button.displayString = "Rubix: " + (Flex.rubix ? "ON" : "OFF");
					break;
			}
			//Render Mode
			/*if (button.id == 18100)
			{
				RenderUtil.index = RenderMethod.getNextIndex(RenderUtil.index);
				RenderUtil.renderMethod = RenderMethod.getRenderMethod(RenderUtil.index);
				RenderUtil.forceReload();
				button.displayString = "Render Mode: " + RenderUtil.renderMethod.getName();
				//update buttons
				this.mc.displayGuiScreen(new Render360Settings(this.parentGuiScreen));
			}
			//Done
			else if (button.id == 200)
			{
				this.mc.displayGuiScreen(this.parentGuiScreen);
			}
			else
			{
				//RenderUtil.renderMethod.onButtonPress(button);
			}*/
		}
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawDefaultBackground();
		this.drawCenteredString(super.fontRendererObj, this.screenTitle, this.width / 2, 15, 0xFFFFFF);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	private class Responder implements GuiPageButtonList.GuiResponder {
		@Override
		public void setEntryValue(int id, boolean value) {

		}

		@Override
		public void setEntryValue(int id, float value) {
			switch (id) {
				case 18101:
					Minecraft.getMinecraft().gameSettings.fovSetting = value;
					break;
				case 18201:
					Flex.fov = value;
					break;
			}
		}

		@Override
		public void setEntryValue(int id, String value) {

		}
	}
}
