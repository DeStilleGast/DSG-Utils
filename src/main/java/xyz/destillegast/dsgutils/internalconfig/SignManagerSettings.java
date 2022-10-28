package xyz.destillegast.dsgutils.internalconfig;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.Collections;
import java.util.List;

@ConfigSerializable
public class SignManagerSettings {

    @Setting
    @Comment("Timer that refreshes all nearby signs for all players, default 30 seconds")
    public long refreshTimer = 30;

    @Setting
    @Comment("Define 'nearby' for updating nearby signs")
    public long nearbyDistance = 64;

    @Setting
    @Comment("List of handles that should be disabled")
    public List<String> blacklistedHandlers = Collections.emptyList();

}
