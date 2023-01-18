package com.tfc.minecraft_effekseer_implementation.network;

import com.tfc.minecraft_effekseer_implementation.common.Effek;
import com.tfc.minecraft_effekseer_implementation.common.Effeks;
import com.tfc.minecraft_effekseer_implementation.common.api.EffekEmitter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

//I will implement IPacket, and you cannot stop me.
public class EffekPacket implements IMessage<EffekPacket> {
	public ResourceLocation effekName;
	public ResourceLocation emmiterName;
	public float progress;
	public Vec3 position;

	public EffekPacket() {

	}
	
	public EffekPacket(ResourceLocation effekName, float progress, Vec3 position, ResourceLocation emmiterName) {
		this.effekName = effekName;
		this.progress = progress;
		this.position = position;
		this.emmiterName = emmiterName;
	}

	@Override
	public void encode(EffekPacket message, FriendlyByteBuf buffer) {
		buffer.writeResourceLocation(effekName);
		buffer.writeFloat(progress);
		buffer.writeDouble(position.x);
		buffer.writeDouble(position.y);
		buffer.writeDouble(position.z);
		buffer.writeResourceLocation(emmiterName);
	}

	@Override
	public EffekPacket decode(FriendlyByteBuf buffer) {
		return new EffekPacket(
				buffer.readResourceLocation(),
				buffer.readFloat(),
				new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()),
				buffer.readResourceLocation()
		);
	}

	@Override
	public void handle(EffekPacket message, Supplier<NetworkEvent.Context> supplier) {
		Effek effek = Effeks.get(message.effekName.toString());
		if (effek != null) {
			EffekEmitter emitter = effek.getOrCreate(message.emmiterName.toString());
			emitter.setVisible(true);
			emitter.setPaused(false);
			emitter.setPlayProgress(message.progress);
			emitter.setPosition(message.position.x(), message.position.y(), message.position.z());
		}
		supplier.get().setPacketHandled(true);
	}
}
