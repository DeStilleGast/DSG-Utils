package xyz.destillegast.dsgutils.api;

import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import xyz.destillegast.dsgutils.internalutils.ConfigurateWrapper;

import java.nio.file.Path;

public interface ConfigurateLoader<T> {

    ConfigurationLoader<?> createLoader(final Path source);

    ConfigurateWrapper<T> createWrapper(final Path source, final Class<T> baseClass) throws ConfigurateException;
}
