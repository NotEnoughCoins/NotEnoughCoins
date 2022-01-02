package me.mindlessly.notenoughcoins.tweaker;

import net.minecraft.launchwrapper.Launch;

public class PreTransformationChecks {
    public static boolean deobfuscated;
    public static boolean usingNotchMappings;

    static void runChecks() {
        deobfuscated = false;
        deobfuscated = (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
        usingNotchMappings = !deobfuscated;
    }
}
