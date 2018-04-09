package mod.render360.coretransform.classtransformers.name;

public class Names {

    public static final ClassName EntityRenderer = new ClassName("net.minecraft.client.renderer.EntityRenderer", "bqc");
    public static final MethodName EntityRenderer_getFOVModifier = new MethodName("getFOVModifier", "func_78481_a", "a", "(FZ)F", "(FZ)F");
    public static final MethodName EntityRenderer_hurtCameraEffect = new MethodName("hurtCameraEffect", "func_78482_e", "d", "(F)V", "(F)V");
    public static final MethodName EntityRenderer_orientCamera = new MethodName("orientCamera", "func_78467_g", "f", "(F)V", "(F)V");
    public static final MethodName EntityRenderer_setupCameraTransform = new MethodName("setupCameraTransform", "func_78479_a", "a", "(FI)V", "(FI)V");
    public static final MethodName EntityRenderer_updateCameraAndRender = new MethodName("updateCameraAndRender", "func_181560_a", "a", "(FJ)V", "(FJ)V");
    public static final MethodName EntityRenderer_renderWorld = new MethodName("renderWorld", "func_78471_a", "b", "(FJ)V", "(FJ)V");
    public static final MethodName EntityRenderer_renderWorldPass = new MethodName("renderWorldPass", "func_175068_a", "a", "(IFJ)V", "(IFJ)V");
    public static final MethodName EntityRenderer_updateFogColor = new MethodName("updateFogColor", "func_78466_h", "h", "(F)V", "(F)V");
    public static final MethodName EntityRenderer_drawNameplate = new MethodName("drawNameplate", "func_189692_a", "a", "(Lnet/minecraft/client/gui/FontRenderer;Ljava/lang/String;FFFIFFZZ)V", "(Lbfe;Ljava/lang/String;FFFIFFZZ)V");
    public static final FieldName EntityRenderer_mc = new FieldName("mc", "field_78531_r", "h", "Lnet/minecraft/client/Minecraft;", "Lbeq;");

    public static final FieldName GameSettings_saveOptions = new FieldName("saveOptions", "func_74303_b", "b", "()V", "()V");

    public static final MethodName GlStateManager_loadIdentity = new MethodName("loadIdentity", "func_179096_D", "F", "()V", "()V");

    public static final ClassName GuiButton = new ClassName("net.minecraft.client.gui.GuiButton", "bfk");
    public static final FieldName GuiButton_id = new FieldName("id", "field_146127_k", "k", "I", "I");

    public static final ClassName GuiOptions = new ClassName("net.minecraft.client.gui.GuiOptions", "bhg");
    public static final MethodName GuiOptions_initGui = new MethodName("initGui", "func_73866_w_", "b", "()V", "()V");
    public static final MethodName GuiOptions_actionPerformed = new MethodName("actionPerformed", "func_146284_a", "a", "(Lnet/minecraft/client/gui/GuiButton;)V", "(Lbfk;)V");

    public static final ClassName GuiScreen = new ClassName("net.minecraft.client.gui.GuiScreen", "bhm");
    public static final MethodName GuiScreen_drawWorldBackground = new MethodName("drawWorldBackground", "func_146270_b", "d_", "(I)V", "(I)V");
    public static final MethodName GuiScreen_drawBackground = new MethodName("drawBackground", "func_146278_c", "c", "(I)V", "(I)V");
    public static final FieldName GuiScreen_buttonList = new FieldName("buttonList", "field_146292_n", "n", "Ljava/util/List;", "Ljava/util/List;");
    public static final FieldName GuiScreen_width = new FieldName("width", "field_146294_l", "l", "I", "I");
    public static final FieldName GuiScreen_height = new FieldName("height", "field_146295_m", "m", "I", "I");
    public static final FieldName GuiScreen_mc = new FieldName("mc", "field_146297_k", "j", "Lnet/minecraft/client/Minecraft;", "Lbeq;");

    public static final ClassName LoadingScreenRenderer = new ClassName("net.minecraft.client.LoadingScreenRenderer", "bet");
    public static final MethodName LoadingScreenRenderer_setLoadingProgress = new MethodName("setLoadingProgress", "func_73718_a", "a", "(I)V", "(I)V");
    public static final FieldName LoadingScreenRenderer_mc = new FieldName("mc", "field_73725_b", "b", "Lnet/minecraft/client/Minecraft;", "Lbeq;");
    public static final FieldName LoadingScreenRenderer_framebuffer = new FieldName("framebuffer", "field_146588_g", "g", "Lnet/minecraft/client/shader/Framebuffer;", "Lbqp;");

    public static final ClassName Minecraft = new ClassName("net.minecraft.client.Minecraft", "beq");
    public static final MethodName Minecraft_loadWorld = new MethodName("loadWorld", "func_71353_a", "a", "(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", "(Lbno;Ljava/lang/String;)V");
    public static final MethodName Minecraft_displayGuiScreen = new MethodName("displayGuiScreen", "func_147108_a", "a", "(Lnet/minecraft/client/gui/GuiScreen;)V", "(Lbhm;)V");
    public static final FieldName Minecraft_currentScreen = new FieldName("currentScreen", "field_71462_r", "m", "Lnet/minecraft/client/gui/GuiScreen;", "Lbhm;");
    public static final FieldName Minecraft_gameSettings = new FieldName("gameSettings", "field_71474_y", "u", "Lnet/minecraft/client/settings/GameSettings;", "Lbes;");

    public static final ClassName Barrier = new ClassName("net.minecraft.client.particle.Barrier", "bnx");
    public static final ClassName ParticleBreaking = new ClassName("net.minecraft.client.particle.ParticleBreaking", "bny");
    public static final ClassName ParticleDigging = new ClassName("net.minecraft.client.particle.ParticleDigging", "bph");
    public static final ClassName ParticleExplosionLarge = new ClassName("net.minecraft.client.particle.ParticleExplosionLarge", "bol");
    public static final ClassName ParticleSweepAttack = new ClassName("net.minecraft.client.particle.ParticleSweepAttack", "bnw");
    public static final ClassName Particle = new ClassName("net.minecraft.client.particle.Particle", "bos");
    public static final MethodName Particle_renderParticle = new MethodName("renderParticle", "func_180434_a", "a", "(Lnet/minecraft/client/renderer/BufferBuilder;Lnet/minecraft/entity/Entity;FFFFFF)V", "(Lbpw;Lsm;FFFFFF)V");

}
