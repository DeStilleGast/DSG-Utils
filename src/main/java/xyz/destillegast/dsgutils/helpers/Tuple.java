package xyz.destillegast.dsgutils.helpers;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by DeStilleGast 23-5-2021
 */
public final class Tuple<A, B> implements ConfigurationSerializable {
    private final A partA;
    private final B partB;

    public Tuple(A partA, B partB) {
        this.partA = partA;
        this.partB = partB;
    }

    public A getPartA() {
        return partA;
    }

    public B getPartB() {
        return partB;
    }



    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> keyValueMap = new HashMap<>();

        if(partA instanceof ConfigurationSerializable){
            keyValueMap.put("k", ((ConfigurationSerializable) partA).serialize());
        }else {
            keyValueMap.put("k", partA);
        }
        if(partB instanceof ConfigurationSerializable){
            keyValueMap.put("v", ((ConfigurationSerializable) partB).serialize());
        }else {
            keyValueMap.put("v", partB);
        }

        return keyValueMap;
    }


    public static Tuple<?, ?> deserialize(Map<String, Object> args) {

        Object key = args.get("k");
        if(key instanceof Map){
            key = ConfigurationSerialization.deserializeObject((Map<String, ?>)key);
        }

        Object value = args.get("v");
        if(value instanceof Map){
//            value = value.getClass().getDeclaredMethod("deserialize", value.getClass());
            value = ConfigurationSerialization.deserializeObject((Map<String, ?>)value);
        }

        return new Tuple(key, value);
    }
}
