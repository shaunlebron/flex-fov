package mod.render360.coretransform.classtransformers.name;

import mod.render360.coretransform.CoreLoader;

public class FieldName {

    private final String deobfuscatedName;
    private final String obfuscatedNameFull;
    private final String obfuscatedNameShort;
    private final String deobfuscatedDesc;
    private final String obfuscatedDesc;

    public FieldName(String deobfuscatedName, String obfuscatedNameFull, String obfuscatedNameShort,
                     String deobfuscatedDesc, String obfuscatedDesc) {
        this.deobfuscatedName = deobfuscatedName;
        this.obfuscatedNameFull = obfuscatedNameFull;
        this.obfuscatedNameShort = obfuscatedNameShort;
        this.deobfuscatedDesc = deobfuscatedDesc;
        this.obfuscatedDesc = obfuscatedDesc;
    }

    public String getShortName() {
        return getShortName(CoreLoader.isObfuscated);
    }

    public String getShortName(boolean obfuscated) {
        if (obfuscated) {
            return obfuscatedNameShort;
        } else {
            return deobfuscatedName;
        }
    }

    public String getFullName() {
        return getFullName(CoreLoader.isObfuscated);
    }

    public String getFullName(boolean obfuscated) {
        if (obfuscated) {
            return obfuscatedNameFull;
        } else {
            return deobfuscatedName;
        }
    }

    public String getDesc() {
        return getDesc(CoreLoader.isObfuscated);
    }

    public String getDesc(boolean obfuscated) {
        if (obfuscated) {
            return obfuscatedDesc;
        } else {
            return deobfuscatedDesc;
        }
    }

    public String all() {
        return deobfuscatedName + " " + deobfuscatedDesc + "   " + obfuscatedNameShort + " " + obfuscatedDesc + "   " + obfuscatedNameFull;
    }
}
