package com.bvengo.soundcontroller.region;

import net.minecraft.world.phys.Vec3;

public interface RegionGeometry {
    boolean contains(Vec3 pos);
    String getDescription();
}
