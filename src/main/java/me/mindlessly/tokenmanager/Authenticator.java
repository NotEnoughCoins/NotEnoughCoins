package me.mindlessly.tokenmanager;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class Authenticator {

	public static void reAuth(String name, String uuid, String token) {
		Session session = new Session(name, uuid, token, "mojang");
		Field sessionField = ReflectionHelper.findField(Minecraft.class, "session", "field_71449_j");
		ReflectionHelper.setPrivateValue(Field.class, sessionField, sessionField.getModifiers() & ~Modifier.FINAL, "modifiers");
		ReflectionHelper.setPrivateValue(Minecraft.class, Minecraft.getMinecraft(), session, "session", "field_71449_j");
	}
}
