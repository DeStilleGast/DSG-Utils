package xyz.destillegast.dsgutils.internalconfig;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class UtilConfig {

    @Setting
    public SignManagerSettings signManagerSettings = new SignManagerSettings();
}


