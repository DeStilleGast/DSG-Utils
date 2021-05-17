package xyz.destillegast.dsgutils.configuration;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Created by DeStilleGast 22-2-2021
 */
public class SimpleConfiguration {

    private final File configFile;
    private final Object obj;
    private final Plugin plugin;

    public SimpleConfiguration(Plugin owner, File file, Object obj) {
        this.obj = obj;
        this.configFile = file;
        this.plugin = owner;
    }

    public SimpleConfiguration(Plugin owner, String filename, Object obj){
        this.plugin = owner;
        if(!owner.getDataFolder().exists()) owner.getDataFolder().mkdir();
        this.configFile = new File(owner.getDataFolder(), filename);
        this.obj = obj;
    }

    public static <T> T load(T obj, Plugin owner, String filename) throws IllegalAccessException {
        new SimpleConfiguration(owner, filename, obj).load();
        return obj;
    }

    public void load() throws IllegalAccessException {
        if(!configFile.exists()) save();

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        for (Field field : obj.getClass().getDeclaredFields()){
            if(field.isAnnotationPresent(ConfigOption.class)){
                field.setAccessible(true);

                String configPath = field.getAnnotation(ConfigOption.class).value();

                if(config.contains(configPath))
                    field.set(obj, config.get(configPath));
            }
        }
    }

    public void save() throws IllegalAccessException {
        YamlConfiguration config = new YamlConfiguration();

        for (Field field : obj.getClass().getDeclaredFields()){
            if(field.isAnnotationPresent(ConfigOption.class)){
                field.setAccessible(true);

                config.set(field.getAnnotation(ConfigOption.class).value(), field.get(obj));
            }
        }

        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save configuration to " + configFile.getAbsolutePath());
            e.printStackTrace();
        }
    }
}
