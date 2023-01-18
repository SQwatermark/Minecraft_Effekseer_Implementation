package com.tfc.minecraft_effekseer_implementation.network;

import com.mojang.math.Vector3d;
import com.tfc.minecraft_effekseer_implementation.network.EffekPacket;
import com.tfc.minecraft_effekseer_implementation.network.EndEmitterPacket;
import com.tfc.minecraft_effekseer_implementation.network.IMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Predicate;

public class Networking {
	private static final String version = "1";

	private static int nextId = 0;
	private static final SimpleChannel channel = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation("mc_effekseer_impl:main"))
			.serverAcceptedVersions((v) -> version.equals(v) || NetworkRegistry.ABSENT.equals(v) || NetworkRegistry.ACCEPTVANILLA.equals(v))
			.clientAcceptedVersions((v) -> version.equals(v) || NetworkRegistry.ABSENT.equals(v) || NetworkRegistry.ACCEPTVANILLA.equals(v))
			.networkProtocolVersion(() -> "1")
			.simpleChannel();
	
	public static void init() {
	}
	
	static {
		register(EffekPacket.class, new EffekPacket());
		register(EndEmitterPacket.class, new EndEmitterPacket());
	}

	private static <T> void register(Class<T> clazz, IMessage<T> message) {
		channel.registerMessage(nextId++, clazz, message::encode, message::decode, message::handle);
	}
	
	public static Vector3d blockPosToVector(BlockPos pos) {
		return new Vector3d(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f);
	}
	
	public static void sendStartEffekPacket(
			Predicate<Player> selector, Level world,
			ResourceLocation effekName, ResourceLocation emitterName, float progress, Vec3 position
	) {
		EffekPacket packet = new EffekPacket(effekName, progress, position, emitterName);
		for (Player player : world.players()) {
			if (selector.test(player) && player instanceof ServerPlayer) {
				channel.send(
						PacketDistributor.PLAYER.with(() -> ((ServerPlayer) player)),
						packet
				);
			}
		}
	}
	
	public static void sendStartEffekPacket(
			PacketDistributor.PacketTarget target,
			ResourceLocation effekName, ResourceLocation emitterName, float progress, Vec3 position
	) {
		EffekPacket packet = new EffekPacket(effekName, progress, position, emitterName);
		channel.send(target, packet);
	}
	
	public static void sendEndEffekPacket(
			PacketDistributor.PacketTarget target,
			ResourceLocation effekName, ResourceLocation emitterName, boolean deleteEmitter
	) {
		EndEmitterPacket packet = new EndEmitterPacket(effekName, emitterName, deleteEmitter);
		channel.send(target, packet);
	}
}
