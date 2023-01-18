package com.tfc.minecraft_effekseer_implementation;

import com.mojang.math.Matrix4f;
import com.tfc.effekseer4j.EffekseerEffect;
import com.tfc.effekseer4j.enums.TextureType;
import com.tfc.minecraft_effekseer_implementation.common.Effek;
import com.tfc.minecraft_effekseer_implementation.common.Effeks;
import com.tfc.minecraft_effekseer_implementation.common.LoaderIndependentIdentifier;
import com.tfc.minecraft_effekseer_implementation.common.api.EffekEmitter;
import com.tfc.minecraft_effekseer_implementation.network.Command;
import com.tfc.minecraft_effekseer_implementation.network.Networking;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("mc_effekseer_impl")
public class MEI {
	private static final Logger LOGGER = LogManager.getLogger();

	private static final Effeks mapHandler = Effeks.getMapHandler();

	public MEI() {
		// resource locations are very nice to have for registry type stuff, and I want the api to be 100% loader independent
		if (LoaderIndependentIdentifier.rlConstructor1.get() == null) {
			LoaderIndependentIdentifier.rlConstructor1.set(ResourceLocation::new);
			LoaderIndependentIdentifier.rlConstructor2.set(ResourceLocation::new);
		}
		if (Effek.widthGetter.get() == null) {
			Effek.widthGetter.set(() -> Minecraft.getInstance().getMainRenderTarget().viewWidth);
			Effek.heightGetter.set(() -> Minecraft.getInstance().getMainRenderTarget().viewHeight);
		}

		Networking.init();
		MinecraftForge.EVENT_BUS.addListener(this::onServerStartup);
		if (!FMLEnvironment.dist.isClient()) return;
		MinecraftForge.EVENT_BUS.addListener(this::renderWorldLast);
		MinecraftForge.EVENT_BUS.addListener(this::fovEvent);
		ReloadableResourceManager manager = (ReloadableResourceManager) Minecraft.getInstance().getResourceManager();
		manager.registerReloadListener(EffekseerMCAssetLoader.INSTANCE);


	}

	private void onServerStartup(RegisterCommandsEvent event) {
		event.getDispatcher().register(Command.construct());
	}
	
	private static long lastFrame = -1;

	private double fov = 70;

	private void fovEvent(EntityViewRenderEvent.FieldOfView event) {
		fov = event.getFOV();
	}
	
	private void renderWorldLast(RenderLevelLastEvent event) {
		mapHandler.setTimeSinceReload(Effeks.getTimeSinceReload() + 1);
		Effek effek = Effeks.get("mc_effekseer_impl:example");
		if (effek != null) {
			EffekEmitter emitter = effek.getOrCreate("test:test");
			emitter.setVisible(false);
			for (Entity allEntity : Minecraft.getInstance().level.entitiesForRendering()) {
				if (allEntity instanceof ArmorStand) {
					ResourceLocation location = new ResourceLocation("modid:"+ allEntity.getUUID());
					EffekEmitter emitter1 = effek.getOrCreate(location.toString());
					emitter1.setPosition(allEntity.getX(), allEntity.getY() + allEntity.getEyeHeight(), allEntity.getZ());
					if (!allEntity.isAlive()) effek.delete(emitter1);
				}
			}
		}
//		effek = Effeks.get("example:aura");
//		if (effek != null)
//			for (int x = 0; x < 16; x++) {
//				for (int y = 0; y < 16; y++) {
//					EffekEmitter emitter = effek.getOrCreate("test:x" + x + "y" + y + "z0");
//					if (emitter != null) emitter.setPosition(x, y + 16, 0);
//					effek.delete(emitter);
//				}
//			}
		float diff = 1;
		if (lastFrame != -1) {
			long currentTime = System.currentTimeMillis();
			diff = (Math.abs(currentTime - lastFrame) / 1000f) * 60;
		}
		lastFrame = System.currentTimeMillis();
		Matrix4f matrix;
		event.getPoseStack().pushPose();
		assert Minecraft.getInstance().player != null;
		Vec3 pos = Minecraft.getInstance().player.position();
		event.getPoseStack().translate(
				-pos.x,
				-pos.y,
				-pos.z
		);
		event.getPoseStack().translate(0.5f, 0.5f, 0.5f);
		matrix = event.getPoseStack().last().pose();
		float[][] cameraMatrix = matrixToArray(matrix);
		event.getPoseStack().popPose();
		matrix = Minecraft.getInstance().gameRenderer.getProjectionMatrix(fov);
		float[][] projectionMatrix = matrixToArray(matrix);
		final float finalDiff = diff;
		if (Minecraft.getInstance().levelRenderer.getParticlesTarget() != null)
			Minecraft.getInstance().levelRenderer.getParticlesTarget().copyDepthFrom(Minecraft.getInstance().getMainRenderTarget());
//		ParticleRenderType.TERRAIN_SHEET.begin();
		// TODO 似乎应该有个插值
		Effeks.forEach((name, effect) -> effect.draw(cameraMatrix, projectionMatrix, finalDiff));
//		ParticleRenderType.TERRAIN_SHEET.end();
	}
	
	public static void printEffectInfo(EffekseerEffect effect) {
		System.out.println("Effect info:");
		System.out.println(" curveCount: " + effect.curveCount());
		for (int index = 0; index < effect.curveCount(); index++) System.out.println("  curve"+index+": " + effect.getCurvePath(index));
		System.out.println(" materialCount: " + effect.materialCount());
		for (int index = 0; index < effect.materialCount(); index++) System.out.println("  material"+index+": " + effect.getMaterialPath(index));
		System.out.println(" modelCount: " + effect.modelCount());
		for (int index = 0; index < effect.modelCount(); index++) System.out.println("  model"+index+": " + effect.getModelPath(index));
		System.out.println(" textureCount: " + effect.textureCount());
		for (TextureType value : TextureType.values()) {
			System.out.println("  textureCount"+value.toString()+":"+effect.textureCount(value));
			for (int index = 0; index < effect.textureCount(value); index++) System.out.println("   model"+index+": " + effect.getTexturePath(index, value));
		}
		System.out.println(" isLoaded: " + effect.isLoaded());
		System.out.println(" minTerm: " + effect.minTerm());
		System.out.println(" maxTerm: " + effect.maxTerm());
	}
	
	public static float[][] matrixToArray(Matrix4f matrix) {
		return ((Matrix4fExtended)(Object)matrix).toArray();
	}
}
