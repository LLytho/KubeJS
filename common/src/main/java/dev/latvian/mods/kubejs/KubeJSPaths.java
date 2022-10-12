package dev.latvian.mods.kubejs;

import dev.architectury.platform.Platform;
import net.minecraft.server.packs.PackType;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author LatvianModder
 */
public interface KubeJSPaths {
	MutableBoolean FIRST_RUN = new MutableBoolean(false);

	static Path dir(Path dir, boolean markFirstRun) {
		if (Files.notExists(dir)) {
			try {
				Files.createDirectories(dir);

				if (markFirstRun) {
					FIRST_RUN.setTrue();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return dir;
	}

	static Path dir(Path dir) {
		return dir(dir, false);
	}

	Path DIRECTORY = dir(Platform.getGameFolder().resolve("kubejs"), true);
	Path DATA = dir(DIRECTORY.resolve("data"));
	Path ASSETS = dir(DIRECTORY.resolve("assets"));
	Path STARTUP_SCRIPTS = DIRECTORY.resolve("startup_scripts");
	Path SERVER_SCRIPTS = DIRECTORY.resolve("server_scripts");
	Path CLIENT_SCRIPTS = DIRECTORY.resolve("client_scripts");
	Path CONFIG = dir(DIRECTORY.resolve("config"));
	Path EXPORTED = dir(DIRECTORY.resolve("exported"));
	Path README = DIRECTORY.resolve("README.txt");
	Path LOCAL = dir(Platform.getGameFolder().resolve("local"));

	static Path get(PackType type) {
		return type == PackType.CLIENT_RESOURCES ? ASSETS : DATA;
	}
}
