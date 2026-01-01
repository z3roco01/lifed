package z3roco01.lifed.util.player;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;

public class TeamUtil {
    /**
     * Creates and adds a team to the servers Scoreboard
     * @param name the name of the team
     * @param colour the colour of the team
     * @return the new or already existing Team object
     */
    public static Team createTeam(String name, Formatting colour) {
        Scoreboard scoreboard = ScoreboardUtil.getScoreboard();

        // check if it already exists
        Team exists = scoreboard.getTeam(name);
        // if it does, return it
        if(exists != null)
            return exists;

        // Create the Team Object
        Team team = scoreboard.addTeam(name);
        team.setColor(colour);

        return team;
    }

    /**
     * Adds a player to the passed team
     * @param player the player
     * @param team the team
     */
    public static void addPlayerToTeam(ServerPlayerEntity player, Team team) {
        Scoreboard scoreboard = ScoreboardUtil.getScoreboard();
        scoreboard.addScoreHolderToTeam(player.getNameForScoreboard(), team);
    }

    /**
     * Removes a player from the passed team
     * @param player the player
     * @param team the team
     */
    public static void removePlayerFromTeam(ServerPlayerEntity player, Team team) {
        Scoreboard scoreboard = ScoreboardUtil.getScoreboard();
        scoreboard.removeScoreHolderFromTeam(player.getNameForScoreboard(), team);
    }

    /**
     * Removes a team from the server when it is done
     * @param team the team to remove
     */
    public static void removeTeam(Team team) {
        Scoreboard scoreboard = ScoreboardUtil.getScoreboard();

        scoreboard.removeTeam(team);
    }
}
