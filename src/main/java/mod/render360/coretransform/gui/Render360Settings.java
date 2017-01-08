package mod.render360.coretransform.gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import mod.render360.coretransform.RenderUtil;
import mod.render360.coretransform.render.RenderMethod;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class Render360Settings extends GuiScreen {

	private final GuiScreen parentGuiScreen;
	public static final String screenTitle = "Render 360 Settings";
	
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
		super.buttonList.add(new GuiButton(18100, super.width / 2 - 100, super.height / 6 - 12, 200, 20, "Render Mode: " + RenderUtil.renderMethod.getName()));
		RenderUtil.renderMethod.addButtonsToGui(buttonList, width, height);
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
			//Render Mode
			if (button.id == 18100)
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
				RenderUtil.renderMethod.onButtonPress(button);
			}
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
}
