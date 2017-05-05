  // access flags 0x1
  public setupCameraTransform(FI)V
   L0
    LINENUMBER 714 L0
    ALOAD 0
    ALOAD 0
    GETFIELD net/minecraft/client/renderer/EntityRenderer.mc : Lnet/minecraft/client/Minecraft;
    GETFIELD net/minecraft/client/Minecraft.gameSettings : Lnet/minecraft/client/settings/GameSettings;
    GETFIELD net/minecraft/client/settings/GameSettings.renderDistanceChunks : I
    BIPUSH 16
    IMUL
    I2F
    PUTFIELD net/minecraft/client/renderer/EntityRenderer.farPlaneDistance : F
   L1
    LINENUMBER 715 L1
    SIPUSH 5889
    INVOKESTATIC net/minecraft/client/renderer/GlStateManager.matrixMode (I)V
   L2
    LINENUMBER 716 L2
    INVOKESTATIC net/minecraft/client/renderer/GlStateManager.loadIdentity ()V
   L3
    LINENUMBER 717 L3
    LDC 0.07
    FSTORE 3
   L4
    LINENUMBER 719 L4
    ALOAD 0
    GETFIELD net/minecraft/client/renderer/EntityRenderer.mc : Lnet/minecraft/client/Minecraft;
    GETFIELD net/minecraft/client/Minecraft.gameSettings : Lnet/minecraft/client/settings/GameSettings;
    GETFIELD net/minecraft/client/settings/GameSettings.anaglyph : Z
    IFEQ L5
   L6
    LINENUMBER 721 L6
    ILOAD 2
    ICONST_2
    IMUL
    ICONST_1
    ISUB
    INEG
    I2F
    LDC 0.07
    FMUL
    FCONST_0
    FCONST_0
    INVOKESTATIC net/minecraft/client/renderer/GlStateManager.translate (FFF)V
   L5
    LINENUMBER 724 L5
   FRAME APPEND [F]
    ALOAD 0
    GETFIELD net/minecraft/client/renderer/EntityRenderer.cameraZoom : D
    DCONST_1
    DCMPL
    IFEQ L7
   L8
    LINENUMBER 726 L8
    ALOAD 0
    GETFIELD net/minecraft/client/renderer/EntityRenderer.cameraYaw : D
    D2F
    ALOAD 0
    GETFIELD net/minecraft/client/renderer/EntityRenderer.cameraPitch : D
    DNEG
    D2F
    FCONST_0
    INVOKESTATIC net/minecraft/client/renderer/GlStateManager.translate (FFF)V
   L9
    LINENUMBER 727 L9
    ALOAD 0
    GETFIELD net/minecraft/client/renderer/EntityRenderer.cameraZoom : D
    ALOAD 0
    GETFIELD net/minecraft/client/renderer/EntityRenderer.cameraZoom : D
    DCONST_1
    INVOKESTATIC net/minecraft/client/renderer/GlStateManager.scale (DDD)V
   L7
    LINENUMBER 730 L7
   FRAME SAME
    ALOAD 0
    FLOAD 1
    ICONST_1
    INVOKESPECIAL net/minecraft/client/renderer/EntityRenderer.getFOVModifier (FZ)F
    ALOAD 0
    GETFIELD net/minecraft/client/renderer/EntityRenderer.mc : Lnet/minecraft/client/Minecraft;
    GETFIELD net/minecraft/client/Minecraft.displayWidth : I
    I2F
    ALOAD 0
    GETFIELD net/minecraft/client/renderer/EntityRenderer.mc : Lnet/minecraft/client/Minecraft;
    GETFIELD net/minecraft/client/Minecraft.displayHeight : I
    I2F
    FDIV
    LDC 0.05
    ALOAD 0
    GETFIELD net/minecraft/client/renderer/EntityRenderer.farPlaneDistance : F
    GETSTATIC net/minecraft/util/math/MathHelper.SQRT_2 : F
    FMUL
    INVOKESTATIC org/lwjgl/util/glu/Project.gluPerspective (FFFF)V
   L10
    LINENUMBER 731 L10
    SIPUSH 5888
    INVOKESTATIC net/minecraft/client/renderer/GlStateManager.matrixMode (I)V
   L11
    LINENUMBER 732 L11
    INVOKESTATIC net/minecraft/client/renderer/GlStateManager.loadIdentity ()V
   L12
    LINENUMBER 734 L12
    ALOAD 0
    GETFIELD net/minecraft/client/renderer/EntityRenderer.mc : Lnet/minecraft/client/Minecraft;
    GETFIELD net/minecraft/client/Minecraft.gameSettings : Lnet/minecraft/client/settings/GameSettings;
    GETFIELD net/minecraft/client/settings/GameSettings.anaglyph : Z
    IFEQ L13
   L14
    LINENUMBER 736 L14
    ILOAD 2
    ICONST_2
    IMUL
    ICONST_1
    ISUB
    I2F
    LDC 0.1
    FMUL
    FCONST_0
    FCONST_0
    INVOKESTATIC net/minecraft/client/renderer/GlStateManager.translate (FFF)V
   L13
    LINENUMBER 739 L13
   FRAME SAME
    ALOAD 0
    FLOAD 1
    INVOKESPECIAL net/minecraft/client/renderer/EntityRenderer.hurtCameraEffect (F)V
   L15
    LINENUMBER 741 L15
    ALOAD 0
    GETFIELD net/minecraft/client/renderer/EntityRenderer.mc : Lnet/minecraft/client/Minecraft;
    GETFIELD net/minecraft/client/Minecraft.gameSettings : Lnet/minecraft/client/settings/GameSettings;
    GETFIELD net/minecraft/client/settings/GameSettings.viewBobbing : Z
    IFEQ L16
   L17
    LINENUMBER 743 L17
    ALOAD 0
    FLOAD 1
    INVOKESPECIAL net/minecraft/client/renderer/EntityRenderer.setupViewBobbing (F)V
   L16
    LINENUMBER 746 L16
   FRAME SAME
    ALOAD 0
    GETFIELD net/minecraft/client/renderer/EntityRenderer.mc : Lnet/minecraft/client/Minecraft;
    GETFIELD net/minecraft/client/Minecraft.thePlayer : Lnet/minecraft/client/entity/EntityPlayerSP;
    GETFIELD net/minecraft/client/entity/EntityPlayerSP.prevTimeInPortal : F
    ALOAD 0
    GETFIELD net/minecraft/client/renderer/EntityRenderer.mc : Lnet/minecraft/client/Minecraft;
    GETFIELD net/minecraft/client/Minecraft.thePlayer : Lnet/minecraft/client/entity/EntityPlayerSP;
    GETFIELD net/minecraft/client/entity/EntityPlayerSP.timeInPortal : F
    ALOAD 0
    GETFIELD net/minecraft/client/renderer/EntityRenderer.mc : Lnet/minecraft/client/Minecraft;
    GETFIELD net/minecraft/client/Minecraft.thePlayer : Lnet/minecraft/client/entity/EntityPlayerSP;
    GETFIELD net/minecraft/client/entity/EntityPlayerSP.prevTimeInPortal : F
    FSUB
    FLOAD 1
    FMUL
    FADD
    FSTORE 4
   L18
    LINENUMBER 748 L18
    FLOAD 4
    FCONST_0
    FCMPL
    IFLE L19
   L20
    LINENUMBER 750 L20
    BIPUSH 20
    ISTORE 5
   L21
    LINENUMBER 752 L21
    ALOAD 0
    GETFIELD net/minecraft/client/renderer/EntityRenderer.mc : Lnet/minecraft/client/Minecraft;
    GETFIELD net/minecraft/client/Minecraft.thePlayer : Lnet/minecraft/client/entity/EntityPlayerSP;
    GETSTATIC net/minecraft/init/MobEffects.NAUSEA : Lnet/minecraft/potion/Potion;
    INVOKEVIRTUAL net/minecraft/client/entity/EntityPlayerSP.isPotionActive (Lnet/minecraft/potion/Potion;)Z
    IFEQ L22
   L23
    LINENUMBER 754 L23
    BIPUSH 7
    ISTORE 5
   L22
    LINENUMBER 757 L22
   FRAME APPEND [F I]
    LDC 5.0
    FLOAD 4
    FLOAD 4
    FMUL
    LDC 5.0
    FADD
    FDIV
    FLOAD 4
    LDC 0.04
    FMUL
    FSUB
    FSTORE 6
   L24
    LINENUMBER 758 L24
    FLOAD 6
    FLOAD 6
    FMUL
    FSTORE 6
   L25
    LINENUMBER 759 L25
    ALOAD 0
    GETFIELD net/minecraft/client/renderer/EntityRenderer.rendererUpdateCount : I
    I2F
    FLOAD 1
    FADD
    ILOAD 5
    I2F
    FMUL
    FCONST_0
    FCONST_1
    FCONST_1
    INVOKESTATIC net/minecraft/client/renderer/GlStateManager.rotate (FFFF)V
   L26
    LINENUMBER 760 L26
    FCONST_1
    FLOAD 6
    FDIV
    FCONST_1
    FCONST_1
    INVOKESTATIC net/minecraft/client/renderer/GlStateManager.scale (FFF)V
   L27
    LINENUMBER 761 L27
    ALOAD 0
    GETFIELD net/minecraft/client/renderer/EntityRenderer.rendererUpdateCount : I
    I2F
    FLOAD 1
    FADD
    FNEG
    ILOAD 5
    I2F
    FMUL
    FCONST_0
    FCONST_1
    FCONST_1
    INVOKESTATIC net/minecraft/client/renderer/GlStateManager.rotate (FFFF)V
   L19
    LINENUMBER 764 L19
   FRAME CHOP 1
    ALOAD 0
    FLOAD 1
    INVOKESPECIAL net/minecraft/client/renderer/EntityRenderer.orientCamera (F)V
   L28
    LINENUMBER 766 L28
    ALOAD 0
    GETFIELD net/minecraft/client/renderer/EntityRenderer.debugView : Z
    IFEQ L29
   L30
    LINENUMBER 768 L30
    ALOAD 0
    GETFIELD net/minecraft/client/renderer/EntityRenderer.debugViewDirection : I
    TABLESWITCH
      0: L31
      1: L32
      2: L33
      3: L34
      4: L35
      default: L29
   L31
    LINENUMBER 771 L31
   FRAME SAME
    LDC 90.0
    FCONST_0
    FCONST_1
    FCONST_0
    INVOKESTATIC net/minecraft/client/renderer/GlStateManager.rotate (FFFF)V
   L36
    LINENUMBER 772 L36
    GOTO L29
   L32
    LINENUMBER 774 L32
   FRAME SAME
    LDC 180.0
    FCONST_0
    FCONST_1
    FCONST_0
    INVOKESTATIC net/minecraft/client/renderer/GlStateManager.rotate (FFFF)V
   L37
    LINENUMBER 775 L37
    GOTO L29
   L33
    LINENUMBER 777 L33
   FRAME SAME
    LDC -90.0
    FCONST_0
    FCONST_1
    FCONST_0
    INVOKESTATIC net/minecraft/client/renderer/GlStateManager.rotate (FFFF)V
   L38
    LINENUMBER 778 L38
    GOTO L29
   L34
    LINENUMBER 780 L34
   FRAME SAME
    LDC 90.0
    FCONST_1
    FCONST_0
    FCONST_0
    INVOKESTATIC net/minecraft/client/renderer/GlStateManager.rotate (FFFF)V
   L39
    LINENUMBER 781 L39
    GOTO L29
   L35
    LINENUMBER 783 L35
   FRAME SAME
    LDC -90.0
    FCONST_1
    FCONST_0
    FCONST_0
    INVOKESTATIC net/minecraft/client/renderer/GlStateManager.rotate (FFFF)V
   L29
    LINENUMBER 786 L29
   FRAME SAME
    RETURN
   L40
    LOCALVARIABLE i I L21 L19 5
    LOCALVARIABLE f2 F L24 L19 6
    LOCALVARIABLE this Lnet/minecraft/client/renderer/EntityRenderer; L0 L40 0
    LOCALVARIABLE partialTicks F L0 L40 1
    LOCALVARIABLE pass I L0 L40 2
    LOCALVARIABLE f F L4 L40 3
    LOCALVARIABLE f1 F L18 L40 4
    MAXSTACK = 6
    MAXLOCALS = 7
