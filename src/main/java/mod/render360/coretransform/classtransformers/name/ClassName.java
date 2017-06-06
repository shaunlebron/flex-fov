package mod.render360.coretransform.classtransformers.name;

import mod.render360.coretransform.CoreLoader;

public class ClassName {

    private final String deobfuscatedName;
    private final String obfuscatedName;

    public ClassName(String deobfuscatedName, String obfuscatedName) {
        this.deobfuscatedName = deobfuscatedName;
        this.obfuscatedName = obfuscatedName;
    }

    public String getName() {
        return getName(CoreLoader.isObfuscated);
    }

    public String getName(boolean obfuscated) {
        if (obfuscated) {
            return obfuscatedName;
        } else {
            return deobfuscatedName;
        }
    }

    /**
     * See {@link org.objectweb.asm.Type#getInternalName Type.getInternalName()}
     */
    public String getInternalName() {
        return getInternalName(CoreLoader.isObfuscated);
    }

    /**
     * See {@link org.objectweb.asm.Type#getInternalName Type.getInternalName()}
     */
    public String getInternalName(boolean obfuscated) {
        if (obfuscated) {
            return obfuscatedName;
        } else {
            return deobfuscatedName.replace('.', '/');
        }
    }

    //TODO comment
    public String getNameAsDesc() {
        return "L" + getInternalName(CoreLoader.isObfuscated) + ";";
    }

    //TODO comment
    public String getNameAsDesc(boolean obfuscated) {
        return "L" + getInternalName(obfuscated) + ";";
    }

    public String all() {
        return deobfuscatedName + "   " + obfuscatedName;
    }
}
