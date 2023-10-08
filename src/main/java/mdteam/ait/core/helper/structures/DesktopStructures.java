package mdteam.ait.core.helper.structures;

import mdteam.ait.AITMod;
import net.minecraft.util.Identifier;

public abstract class DesktopStructures {
    private final String structureName;
    protected Identifier location;
    public DesktopStructures(String structureName) {
        this(new Identifier(AITMod.MOD_ID,structureName),structureName);
    }
    public DesktopStructures(Identifier location, String structureName) {
        this.location = location;
        this.structureName = structureName;
    }

    public String getStructureName() {
        return this.structureName;
    }
    public Identifier getLocation() {
        return this.location;
    }
}
