package dev.visualhud;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class VisualHudClient implements ClientModInitializer {
	public static final String MOD_ID = "visualhud";

	public static boolean hudEnabled = true;
	public static boolean showKeys = true;
	public static boolean showArmor = true;

	private static KeyBinding toggleHud;
	private static KeyBinding toggleKeys;
	private static KeyBinding toggleArmor;

	@Override
	public void onInitializeClient() {
		KeyBinding.Category category = KeyBinding.Category.create(Identifier.of(MOD_ID, "main"));

		toggleHud = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.visualhud.toggle_hud", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, category));
		toggleKeys = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.visualhud.toggle_keys", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, category));
		toggleArmor = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.visualhud.toggle_armor", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, category));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (toggleHud.wasPressed()) hudEnabled = !hudEnabled;
			while (toggleKeys.wasPressed()) showKeys = !showKeys;
			while (toggleArmor.wasPressed()) showArmor = !showArmor;
		});

		HudElementRegistry.attachElementAfter(
			VanillaHudElements.MISC_OVERLAYS,
			Identifier.of(MOD_ID, "hud"),
			new HudRenderer());
	}
}
