package com.tfc.minecraft_effekseer_implementation.network;

import com.tfc.minecraft_effekseer_implementation.common.Effek;
import com.tfc.minecraft_effekseer_implementation.common.Effeks;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class EndEmitterPacket implements IMessage<EndEmitterPacket> {
	public ResourceLocation effekName;
	public ResourceLocation emitterName;
	public boolean deleteEmitter;

	public EndEmitterPacket() {

	}
	
	public EndEmitterPacket(ResourceLocation effekName, ResourceLocation emitterName, boolean deleteEmitter) {
		this.effekName = effekName;
		this.emitterName = emitterName;
		this.deleteEmitter = deleteEmitter;
	}

	@Override
	public void encode(EndEmitterPacket message, FriendlyByteBuf buffer) {
		buffer.writeResourceLocation(effekName);
		buffer.writeResourceLocation(emitterName);
		buffer.writeBoolean(deleteEmitter);
	}

	@Override
	public EndEmitterPacket decode(FriendlyByteBuf buffer) {
		effekName = buffer.readResourceLocation();
		emitterName = buffer.readResourceLocation();
		deleteEmitter = buffer.readBoolean();
		return null;
	}

	@Override
	public void handle(EndEmitterPacket message, Supplier<NetworkEvent.Context> supplier) {
		Effek effek = Effeks.get(message.effekName.toString());
		if (effek != null) effek.delete(effek.getOrCreate(message.emitterName.toString()));
		supplier.get().setPacketHandled(true);
	}
}
