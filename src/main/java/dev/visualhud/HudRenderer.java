package dev.visualhud;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class HudRenderer implements HudElement {
	private static final int TEXT = 0xFFFFFFFF;
	private static final int ACCENT = 0xFF55FFFF;
	private static final int BG = 0x90000000;
	private static final int KEY_ON = 0xB0FFFFFF;
	private static final int KEY_OFF = 0x80000000;

	private static final EquipmentSlot[] ARMOR = {
		EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
	};

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		MinecraftClient mc = MinecraftClient.getInstance();
		ClientPlayerEntity player = mc.player;
		if (!VisualHudClient.hudEnabled || player == null || mc.options.hudHidden) return;

		drawInfoPanel(context, mc, player);
		if (VisualHudClient.showKeys) drawKeys(context, mc, mc.options);
		if (VisualHudClient.showArmor) drawArmor(context, player);
	}

	private void drawInfoPanel(DrawContext ctx, MinecraftClient mc, ClientPlayerEntity p) {
		int ping = 0;
		if (mc.getNetworkHandler() != null) {
			PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(p.getUuid());
			if (entry != null) ping = entry.getLatency();
		}

		String[] lines = {
			"FPS: " + mc.getCurrentFps(),
			"Ping: " + ping + " ms",
			String.format("XYZ: %.1f / %.1f / %.1f", p.getX(), p.getY(), p.getZ()),
			"Facing: " + p.getHorizontalFacing().asString()
		};

		int x = 4, y = 4, width = 0;
		for (String l : lines) width = Math.max(width, mc.textRenderer.getWidth(l));

		ctx.fill(x - 2, y - 2, x + width + 3, y + lines.length * 10 + 1, BG);
		ctx.fill(x - 2, y - 2, x, y + lines.length * 10 + 1, ACCENT);

		for (int i = 0; i < lines.length; i++) {
			ctx.drawTextWithShadow(mc.textRenderer, lines[i], x + 3, y + i * 10, TEXT);
		}
	}

	private void drawKeys(DrawContext ctx, MinecraftClient mc, GameOptions o) {
		int size = 20, gap = 2;
		int baseX = 8;
		int baseY = ctx.getScaledWindowHeight() - 3 * size - 2 * gap - 8;

		key(ctx, mc, "W", baseX + size + gap, baseY, size, size, o.forwardKey.isPressed());
		key(ctx, mc, "A", baseX, baseY + size + gap, size, size, o.leftKey.isPressed());
		key(ctx, mc, "S", baseX + size + gap, baseY + size + gap, size, size, o.backKey.isPressed());
		key(ctx, mc, "D", baseX + 2 * (size + gap), baseY + size + gap, size, size, o.rightKey.isPressed());
		key(ctx, mc, "SPACE", baseX, baseY + 2 * (size + gap), 3 * size + 2 * gap, size - 6, o.jumpKey.isPressed());
	}

	private void key(DrawContext ctx, MinecraftClient mc, String label, int x, int y, int w, int h, boolean down) {
		ctx.fill(x, y, x + w, y + h, down ? KEY_ON : KEY_OFF);
		int color = down ? 0xFF000000 : 0xFFFFFFFF;
		int tw = mc.textRenderer.getWidth(label);
		ctx.drawText(mc.textRenderer, label, x + (w - tw) / 2, y + (h - 8) / 2, color, false);
	}

	private void drawArmor(DrawContext ctx, ClientPlayerEntity p) {
		MinecraftClient mc = MinecraftClient.getInstance();
		int x = ctx.getScaledWindowWidth() - 60;
		int y = 6;
		for (EquipmentSlot slot : ARMOR) {
			ItemStack stack = p.getEquippedStack(slot);
			if (stack.isEmpty()) continue;
			ctx.drawItem(stack, x, y);
			if (stack.isDamageable()) {
				int left = stack.getMaxDamage() - stack.getDamage();
				float ratio = (float) left / stack.getMaxDamage();
				int color = ratio > 0.5f ? 0xFF55FF55 : ratio > 0.2f ? 0xFFFFFF55 : 0xFFFF5555;
				ctx.drawTextWithShadow(mc.textRenderer, String.valueOf(left), x + 20, y + 4, color);
			}
			y += 18;
		}
	}
}
