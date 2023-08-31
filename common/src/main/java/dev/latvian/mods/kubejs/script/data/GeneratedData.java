package dev.latvian.mods.kubejs.script.data;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.util.Lazy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public record GeneratedData(ResourceLocation id, Lazy<byte[]> data) implements IoSupplier<InputStream> {
	public static final GeneratedData PACK_META = new GeneratedData(KubeJS.id("pack.mcmeta"), Lazy.of(() -> {
		var json = new JsonObject();
		var pack = new JsonObject();
		pack.addProperty("description", "KubeJS Pack");
		pack.addProperty("pack_format", 15);
		json.add("pack", pack);
		return json.toString().getBytes(StandardCharsets.UTF_8);
	}));

	public static final GeneratedData PACK_ICON = new GeneratedData(KubeJS.id("kubejs_logo.png"), Lazy.of(() -> {
		try {
			return Files.readAllBytes(KubeJS.thisMod.findResource("kubejs_logo.png").get());
		} catch (Exception ex) {
			ex.printStackTrace();
			return new byte[0];
		}
	}));

	@Override
	@NotNull
	public InputStream get() {
		return new ByteArrayInputStream(data.get());
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof GeneratedData g && id.equals(g.id);
	}

	@Override
	public String toString() {
		return id.toString();
	}
}
