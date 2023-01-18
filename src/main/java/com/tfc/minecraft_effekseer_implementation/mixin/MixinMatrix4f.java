package com.tfc.minecraft_effekseer_implementation.mixin;

import com.mojang.math.Matrix4f;
import com.tfc.minecraft_effekseer_implementation.Matrix4fExtended;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Matrix4f.class)
public class MixinMatrix4f implements Matrix4fExtended {

    @Shadow
    protected float m00;
    @Shadow
    protected float m01;
    @Shadow
    protected float m02;
    @Shadow
    protected float m03;
    @Shadow
    protected float m10;
    @Shadow
    protected float m11;
    @Shadow
    protected float m12;
    @Shadow
    protected float m13;
    @Shadow
    protected float m20;
    @Shadow
    protected float m21;
    @Shadow
    protected float m22;
    @Shadow
    protected float m23;
    @Shadow
    protected float m30;
    @Shadow
    protected float m31;
    @Shadow
    protected float m32;
    @Shadow
    protected float m33;


    @Override
    public float[][] toArray() {
        return new float[][]{
                {m00, m01, m02, m03},
                {m10, m11, m12, m13},
                {m20, m21, m22, m23},
                {m30, m31, m32, m33}
        };
    }
}
