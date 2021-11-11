package io.github.darealturtywurty.minecraftdot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod(MinecraftDot.MODID)
public class MinecraftDot {

	public static final String MODID = "minecraftdot";
	public static final Logger LOGGER = LogManager.getLogger();
	private static final Map<UUID, Boolean> RENDER_MAP = new HashMap<>();

	@SuppressWarnings("deprecation")
	public static void draw(int posX, int posY, int texU, int texV, int width, int height, int red, int green, int blue,
			int alpha) {
		BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferbuilder.pos((double) posX, (double) (posY + height), 0.0D)
				.tex(((float) texU * 0.00390625F), ((float) (texV + height) * 0.00390625F)).color(red, green, blue, alpha)
				.endVertex();
		bufferbuilder.pos((double) (posX + width), (double) (posY + height), 0.0D)
				.tex(((float) (texU + width) * 0.00390625F), ((float) (texV + height) * 0.00390625F))
				.color(red, green, blue, alpha).endVertex();
		bufferbuilder.pos((double) (posX + width), (double) posY, 0.0D)
				.tex(((float) (texU + width) * 0.00390625F), ((float) texV * 0.00390625F)).color(red, green, blue, alpha)
				.endVertex();
		bufferbuilder.pos((double) posX, (double) posY, 0.0D).tex(((float) texU * 0.00390625F), ((float) texV * 0.00390625F))
				.color(red, green, blue, alpha).endVertex();
		Tessellator.getInstance().draw();
	}

	@EventBusSubscriber(modid = MODID, bus = Bus.FORGE, value = Dist.CLIENT)
	public static class ClientForgeEvents {
		@SuppressWarnings({ "resource", "deprecation" })
		@SubscribeEvent
		public static void renderDot(final RenderGameOverlayEvent.Post event) {
			if (event.getType() == ElementType.ALL) {
				if (RENDER_MAP.containsKey(Minecraft.getInstance().player.getUniqueID())) {
					Minecraft.getInstance().getTextureManager()
							.bindTexture(new ResourceLocation(MODID, "textures/gui/dot.png"));
					RenderSystem.enableBlend();
					RenderSystem.defaultBlendFunc();
					RenderSystem.disableAlphaTest();
					draw(0, 0, 0, 0, Minecraft.getInstance().getMainWindow().getWidth(),
							Minecraft.getInstance().getMainWindow().getHeight(), 255, 255, 255, 255);
					RenderSystem.disableBlend();
					RenderSystem.enableAlphaTest();
				}
			}
		}

		@SubscribeEvent
		public static void playerLoadWorld(final EntityJoinWorldEvent event) {
			final Entity entity = event.getEntity();
			if (entity instanceof PlayerEntity && !RENDER_MAP.containsKey(entity.getUniqueID())
					&& event.getWorld().isRemote) {
				RENDER_MAP.put(entity.getUniqueID(), true);
			}
		}

		@SubscribeEvent
		public static void playerLeaveWorld(final EntityLeaveWorldEvent event) {
			final Entity entity = event.getEntity();
			if (RENDER_MAP.containsKey(entity.getUniqueID()) && event.getWorld().isRemote) {
				RENDER_MAP.put(entity.getUniqueID(), false);
			}
		}
	}
}
