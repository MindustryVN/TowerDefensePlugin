package towerdefense;

import mindustry.ai.Pathfinder;
import mindustry.content.Blocks;
import mindustry.gen.PathTile;
import mindustry.world.Tile;

public class TowerDefensePathFinder extends Pathfinder {

    public static final int impassable = -1, notPath = 999999;

    public TowerDefensePathFinder() {
        costTypes.set(costGround,
                (team, tile) -> (PathTile.allDeep(tile)
                        || ((PathTile.team(tile) == 0 || PathTile.team(tile) == team) && PathTile.solid(tile)))
                                ? impassable
                                : 1 + (PathTile.deep(tile) ? notPath : 0) + (PathTile.damages(tile) ? 50 : 0)
                                        + (PathTile.nearSolid(tile) ? 50 : 0) + (PathTile.nearLiquid(tile) ? 10 : 0));

        costTypes.set(costLegs,
                (team, tile) -> (PathTile.allDeep(tile) || PathTile.legSolid(tile)) ? impassable
                        : 1 + (PathTile.deep(tile) ? notPath : 0) + (PathTile.damages(tile) ? 50 : 0)
                                + (PathTile.nearLegSolid(tile) ? 50 : 0) + (PathTile.nearSolid(tile) ? 10 : 0));

        costTypes.set(costNaval,
                (team, tile) -> (PathTile.solid(tile) || !PathTile.liquid(tile) ? notPath : 1)
                        + (PathTile.damages(tile) ? 50 : 0) + (PathTile.nearSolid(tile) ? 10 : 0)
                        + (PathTile.nearGround(tile) ? 10 : 0));
    }

    @Override
    public int packTile(Tile tile) {
        boolean nearLiquid = false, nearSolid = false, nearLegSolid = false, nearGround = false,
                allDeep = tile.floor().isDeep();

        for (int i = 0; i < 4; i++) {
            var other = tile.nearby(i);
            if (other == null)
                continue;

            if (other.floor().isLiquid)
                nearLiquid = true;
            if (other.solid() || !isPath(other))
                nearSolid = true;
            if (other.legSolid() || !isPath(other))
                nearLegSolid = true;
            if (!other.floor().isLiquid)
                nearGround = true;
            if (!other.floor().isDeep())
                allDeep = false;
        }

        return get(0, tile.getTeamID(), tile.solid(), tile.floor().isLiquid, tile.legSolid(), nearLiquid,
                nearGround, nearSolid, nearLegSolid, tile.floor().isDeep() || !isPath(tile),
                tile.floor().damageTaken > 0f || !isPath(tile), allDeep, tile.block().teamPassable);
    }

    // Temp
    public static int get(int health, int team, boolean solid, boolean liquid, boolean legSolid, boolean nearLiquid, boolean nearGround, boolean nearSolid, boolean nearLegSolid, boolean deep, boolean damages, boolean allDeep, boolean nearDeep, boolean teamPassable) {
      return (int)((long)(health << 0) & 255L | (long)(team << 8) & 65280L | (solid ? 65536L : 0L) | (liquid ? 131072L : 0L) | (legSolid ? 262144L : 0L) | (nearLiquid ? 524288L : 0L) | (nearGround ? 1048576L : 0L) | (nearSolid ? 2097152L : 0L) | (nearLegSolid ? 4194304L : 0L) | (deep ? 8388608L : 0L) | (damages ? 16777216L : 0L) | (allDeep ? 33554432L : 0L) | (nearDeep ? 67108864L : 0L) | (teamPassable ? 134217728L : 0L));
    }

    public static boolean isPath(Tile tile) {
        return tile.floor() == Blocks.darkPanel4 || tile.floor() == Blocks.darkPanel5;
    }
}
