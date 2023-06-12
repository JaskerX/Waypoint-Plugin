package de.jaskerx.waypoints;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.World;

public class Waypoint {

	private int id;
	private final String name;
	private final Visibility visibility;
	private final UUID creator;
	private final double x;
	private final double y;
	private final double z;
	private final float yaw;
	private final float pitch;
	private final World world;
	private final Material material;

	private Waypoint(WaypointBuilder builder) {
		this.id = builder.id;
		this.name = builder.name;
		this.visibility = builder.visibility;
		this.creator = builder.creator;
		this.x = builder.x;
		this.y = builder.y;
		this.z = builder.z;
		this.yaw = builder.yaw;
		this.pitch = builder.pitch;
		this.world = builder.world;
		this.material = builder.material;
	}

	public static class WaypointBuilder {

		private int id;
		private String name;
		private Visibility visibility;
		private UUID creator;
		private double x;
		private double y;
		private double z;
		private float yaw;
		private float pitch;
		private World world;
		private Material material;

		public WaypointBuilder setId(int id) {
			this.id = id;
			return this;
		}

		public WaypointBuilder setName(String name) {
			this.name = name;
			return this;
		}

		public WaypointBuilder setVisibility(Visibility visibility) {
			this.visibility = visibility;
			return this;
		}

		public WaypointBuilder setCreator(UUID creator) {
			this.creator = creator;
			return this;
		}

		public WaypointBuilder setX(double x) {
			this.x = x;
			return this;
		}

		public WaypointBuilder setY(double y) {
			this.y = y;
			return this;
		}

		public WaypointBuilder setZ(double z) {
			this.z = z;
			return this;
		}

		public WaypointBuilder setYaw(float yaw) {
			this.yaw = yaw;
			return this;
		}

		public WaypointBuilder setPitch(float pitch) {
			this.pitch = pitch;
			return this;
		}

		public WaypointBuilder setWorld(World world) {
			this.world = world;
			return this;
		}

		public WaypointBuilder setMaterial(Material material) {
			this.material = material;
			return this;
		}

		public Waypoint build() {
			return new Waypoint(this);
		}

	}
	
	public UUID getCreator() {
		return creator;
	}
	public Material getMaterial()  {
		return material;
	}
	public String getName()  {
		return name;
	}
	public float getYaw()  {
		return yaw;
	}
	public float getPitch()  {
		return pitch;
	}
	public Visibility getVisibility() {
		return visibility;
	}
	public int getId() {
		return id;
	}
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public double getZ() {
		return z;
	}
	public World getWorld() {
		return world;
	}

	public void setId(int id) {
		this.id = id;
	}

}
