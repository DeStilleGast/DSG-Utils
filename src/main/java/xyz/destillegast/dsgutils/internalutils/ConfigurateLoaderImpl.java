package xyz.destillegast.dsgutils.internalutils;

import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.loader.HeaderMode;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.objectmapping.meta.NodeResolver;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import xyz.destillegast.dsgutils.api.ConfigurateLoader;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

public class ConfigurateLoaderImpl<T> implements ConfigurateLoader<T> {

    @Override
    public ConfigurationLoader<?> createLoader(final Path source) {
        final ObjectMapper.Factory customFactory = ObjectMapper.factoryBuilder()
                .addNodeResolver(NodeResolver.onlyWithSetting())
                .build();

        return YamlConfigurationLoader.builder()
                .path(source)
                .nodeStyle(NodeStyle.BLOCK)
                .headerMode(HeaderMode.PRESERVE)
//                .defaultOptions(opts -> opts.serializers(build -> build.registerAnnotatedObjects(customFactory)))
                .build();
    }


    public ConfigurateWrapper<T> createWrapper(final Path source, final Class<T> baseClass) throws ConfigurateException {
        final ConfigurationLoader<?> loader = createLoader(source);

        ConfigurateWrapper<T> configurateWrapper = new ConfigurateWrapper<>() {
            final ConfigurationNode node = loader.load();

            @Override
            public ConfigurationLoader<?> getLoader() {
                return loader;
            }

            @Override
            public T load() throws ConfigurateException {
                return node.get(baseClass);
            }

            @Override
            public void save(T newConfig) throws ConfigurateException {
                node.set(baseClass, newConfig);
                loader.save(node);
            }
        };

        try {
            if (!source.toFile().exists()) {
                Optional<Constructor<?>> constructor = Arrays.stream(baseClass.getConstructors()).findFirst();
                if (constructor.isPresent()) {
                    T newInstance = (T) constructor.get().newInstance(null);
                    configurateWrapper.save(newInstance);
                }
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        return configurateWrapper;
    }
}
