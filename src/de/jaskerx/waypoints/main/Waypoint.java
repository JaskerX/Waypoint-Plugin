package de.jaskerx.waypoints.main;

import java.util.UUID;

import org.bukkit.Material;

public class Waypoint {

	double[] coords;
	UUID creator;
	String dimension;
	Material material;
	String name;
	float yaw;
	float pitch;
	boolean isPrivate;
	
	public Waypoint(String name, double[] coords, UUID creator, String dimension) {
		this.name = name;
		this.coords = coords;
		this.creator = creator;
		this.dimension = dimension;
		material = Material.TORCH;
	}
	
	public Waypoint(String name, double[] coords, UUID creator, String dimension, Material material, float yaw, float pitch, boolean isPrivate) {
		this.name = name;
		this.coords = coords;
		this.creator = creator;
		this.dimension = dimension;
		this.material = material;
		this.yaw = yaw;
		this.pitch = pitch;
		this.isPrivate = isPrivate;
	}
	
	public double[] getCoords() {
		return coords;
	}
	public UUID getCreator() {
		return creator;
	}
	public String getDimension() {
		return dimension;
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
	public boolean isPrivate() {
		return isPrivate;
	}
	
	public void setMaterial(Material material) {
		this.material = material;
	}
	public void setYaw(float yaw) {
		this.yaw = yaw;
	}
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}
	
}
