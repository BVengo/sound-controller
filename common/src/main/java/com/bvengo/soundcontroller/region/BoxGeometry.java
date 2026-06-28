package com.bvengo.soundcontroller.region;

import net.minecraft.world.phys.Vec3;

public class BoxGeometry implements RegionGeometry {
    public double x1, y1, z1;
    public double x2, y2, z2;

    public BoxGeometry(double x1, double y1, double z1, double x2, double y2, double z2) {
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
    }

    @Override
    public boolean contains(Vec3 pos) {
        double minX = Math.min(x1, x2), maxX = Math.max(x1, x2);
        double minY = Math.min(y1, y2), maxY = Math.max(y1, y2);
        double minZ = Math.min(z1, z2), maxZ = Math.max(z1, z2);
        return pos.x >= minX && pos.x <= maxX
            && pos.y >= minY && pos.y <= maxY
            && pos.z >= minZ && pos.z <= maxZ;
    }

    @Override
    public String getDescription() {
        return String.format("Box (%.0f,%.0f,%.0f)→(%.0f,%.0f,%.0f)", x1, y1, z1, x2, y2, z2);
    }
}
