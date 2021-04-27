package devlaunchers.structuresystem.populator;

import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.World;

public abstract class StructurePlacementDeterminator {

  private StructureGeneratorConfig structureConfig;

  public void setStructureConfig(StructureGeneratorConfig structureConfig) {
    this.structureConfig = structureConfig;
  }

  public StructureGeneratorConfig getConfig() {
    return structureConfig;
  }

  public abstract void initPlacementDeterminator(StructureGeneratorConfig structureConfig);

  public abstract boolean determinePlacement(World world, Random rand, Chunk chunk);
}
