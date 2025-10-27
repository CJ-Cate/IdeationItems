package io.github.cj_cate.ideationitems.Utils;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class ScoreboardUtil
{
    private static Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

    private final ArrayList<Runnable> teamStuffs = new ArrayList<>(Arrays.asList(
        () -> scoreboard.registerNewTeam("noNameTeam").setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER)
    ));

    public ScoreboardUtil()
    {
        for(Runnable runnable : teamStuffs)
        {
            try {
                runnable.run();
            } catch (IllegalArgumentException ignored) { } // If a team is already registered server side then it needs to catch this
        }

        // old debug
//        scoreboard.getTeams().forEach(t -> {
//            System.out.println(t.getName() + t.getDisplayName());
//        });
    }

    public static void addEntityToTeam(String UUID, String teamName)
    {
        scoreboard.getTeam(teamName).addEntry(UUID);
    }
    public static void addEntityToTeam(UUID UUID, String teamName)
    {
        scoreboard.getTeam(teamName).addEntry(UUID.toString());
    }
}
