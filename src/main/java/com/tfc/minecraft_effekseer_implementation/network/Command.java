package com.tfc.minecraft_effekseer_implementation.network;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.PacketDistributor;

public class Command {
	public static LiteralArgumentBuilder<CommandSourceStack> construct() {
		return Commands.literal("effek").requires(commandSource -> commandSource.hasPermission(2))
				.then(Commands.argument("effek", StringArgumentType.string())
						.then(Commands.argument("emitter", StringArgumentType.string())
								.then(Commands.literal("true").executes((source) -> handle(source, source.getSource(), true)))
								.then(Commands.literal("false").executes((source) -> handle(source, source.getSource(), false)))));
	}
	
	private static int handle(CommandContext<?> context, CommandSourceStack source, boolean delete) {
		if (!delete) {
			Networking.sendEndEffekPacket(
					PacketDistributor.DIMENSION.with(() -> source.getLevel().dimension()),
					new ResourceLocation(StringArgumentType.getString(context, "effek")),
					new ResourceLocation(StringArgumentType.getString(context, "emitter")),
					true
			);
		} else {
			Networking.sendStartEffekPacket(
					PacketDistributor.DIMENSION.with(() -> source.getLevel().dimension()),
					new ResourceLocation(StringArgumentType.getString(context, "effek")),
					new ResourceLocation(StringArgumentType.getString(context, "emitter")),
					0, source.getPosition()
			);
		}
		return 0;
	}
}
