package xyz.destillegast.dsgutils.internalutils;

import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

public interface ConfigurateWrapper<T> {
    ConfigurationLoader<?> getLoader();

    T load() throws ConfigurateException;

    void save(T newConfig) throws ConfigurateException;
}
