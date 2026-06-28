package com.bvengo.soundcontroller.region;

import net.minecraft.world.phys.Vec3;

public class SphereGeometry implements RegionGeometry {
    public double x, y, z;
    public double radius;

    public SphereGeometry(double x, double y, double z, double radius) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
    }

    @Override
    public boolean contains(Vec3 pos) {
        double dx = pos.x - x;
        double dy = pos.y - y;
        double dz = pos.z - z;
        return (dx * dx + dy * dy + dz * dz) <= (radius * radius);
    }

    @Override
    public String getDescription() {
        return String.format("Sphere (%.0f, %.0f, %.0f) r=%.1f", x, y, z, radius);
    }
}
