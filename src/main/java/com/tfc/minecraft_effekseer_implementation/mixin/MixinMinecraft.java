package com.tfc.minecraft_effekseer_implementation.mixin;

import Effekseer.swig.EffekseerBackendCore;
import Effekseer.swig.EffekseerManagerCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Paths;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Inject(method = "<init>", at = @At("RETURN"))
    public void test(GameConfig pGameConfig, CallbackInfo ci) {

    }

}
