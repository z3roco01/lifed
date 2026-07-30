package z3roco01.lifed.util.player;

import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public class TeamUtil {
    /**
     * Creates and adds a team to the servers Scoreboard
     * @param name the name of the team
     * @param colour the colour of the team
     * @return the new or already existing Team object
     */
    public static PlayerTeam createTeam(String name, ChatFormatting colour) {
        Scoreboard scoreboard = ScoreboardUtil.getScoreboard();

        // check if it already exists
        PlayerTeam exists = scoreboard.getPlayersTeam(name);
        // if it does, return it
        if(exists != null)
            return exists;

        // Create the Team Object
        PlayerTeam team = scoreboard.addPlayerTeam(name);
        team.setColor(colour);

        return team;
    }

    /**
     * Adds a player to the passed team
     * @param player the player
     * @param team the team
     */
    public static void addPlayerToTeam(ServerPlayer player, PlayerTeam team) {
        Scoreboard scoreboard = ScoreboardUtil.getScoreboard();
        scoreboard.addPlayerToTeam(player.getScoreboardName(), team);
    }

    /**
     * Removes a player from the passed team
     * @param player the player
     * @param team the team
     */
    public static void removePlayerFromTeam(ServerPlayer player, PlayerTeam team) {
        Scoreboard scoreboard = ScoreboardUtil.getScoreboard();
        scoreboard.removePlayerFromTeam(player.getScoreboardName(), team);
    }

    /**
     * Removes a team from the server when it is done
     * @param team the team to remove
     */
    public static void removeTeam(PlayerTeam team) {
        Scoreboard scoreboard = ScoreboardUtil.getScoreboard();

        scoreboard.removePlayerTeam(team);
    }
}
