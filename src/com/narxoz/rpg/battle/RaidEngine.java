package com.narxoz.rpg.battle;

import com.narxoz.rpg.bridge.Skill;
import com.narxoz.rpg.composite.CombatNode;

import java.util.Random;

public class RaidEngine {
    private static final int DEFAULT_MAX_ROUNDS = 1000;
    private static final int CRIT_CHANCE_PERCENT = 10;
    private Random random = new Random(1L);

    public RaidEngine setRandomSeed(long seed) {
        this.random = new Random(seed);
        return this;
    }

    public RaidResult runRaid(CombatNode teamA, CombatNode teamB, Skill teamASkill, Skill teamBSkill) {
        RaidResult result = new RaidResult();
        if (teamA == null || teamB == null) {
            result.setRounds(0);
            result.setWinner("INVALID_INPUT");
            result.addLine("ERROR: teamA/teamB mustn't be null");
            return result;
        }
        if (teamASkill == null || teamBSkill == null) {
            result.setRounds(0);
            result.setWinner("INVALID_INPUT");
            result.addLine("ERROR: teamASkill/teamBSkill mustn't be null");
            return result;
        }
        result.addLine("Raid started: " + teamA.getName() + " vs " + teamB.getName());
        result.addLine("Team A skill: " + formatSkill(teamASkill));
        result.addLine("Team B skill: " + formatSkill(teamBSkill));

        if (!teamA.isAlive() && !teamB.isAlive()) {
            result.setRounds(0);
            result.setWinner("DRAW");
            result.addLine("Both teams are already defeated");
            return result;
        }
        if (!teamA.isAlive()) {
            result.setRounds(0);
            result.setWinner(teamB.getName());
            result.addLine(teamA.getName() + " is already defeated");
            return result;
        }
        if (!teamB.isAlive()) {
            result.setRounds(0);
            result.setWinner(teamA.getName());
            result.addLine(teamB.getName() + " is already defeated");
            return result;
        }
        int rounds = 0;
        while (teamA.isAlive() && teamB.isAlive() && rounds < DEFAULT_MAX_ROUNDS) {
            rounds++;
            result.addLine("\n-- Round " + rounds + " --");
            if (teamA.isAlive()) {
                castWithOptionalCrit(result, teamA, teamB, teamASkill);
            }
            if (teamB.isAlive()) {
                castWithOptionalCrit(result, teamB, teamA, teamBSkill);
            }
            result.addLine("Status: "
                    + teamA.getName() + " HP=" + teamA.getHealth() + " | "
                    + teamB.getName() + " HP=" + teamB.getHealth());
        }
        result.setRounds(rounds);

        if (teamA.isAlive() && !teamB.isAlive()) {
            result.setWinner(teamA.getName());
            result.addLine("\nWinner: " + teamA.getName());
        } else if (teamB.isAlive() && !teamA.isAlive()) {
            result.setWinner(teamB.getName());
            result.addLine("\nWinner: " + teamB.getName());
        } else {
            result.setWinner("DRAW");
            if (rounds >= DEFAULT_MAX_ROUNDS) {
                result.addLine("\nMax rounds reached (" + DEFAULT_MAX_ROUNDS + "). Declared DRAW.");
            } else {
                result.addLine("\nBoth teams defeated. Declared DRAW.");
            }
        }

        return result;
    }

    private void castWithOptionalCrit(RaidResult result, CombatNode attacker, CombatNode defender, Skill skill) {
        if (!attacker.isAlive()) {
            return;
        }
        if (!defender.isAlive()) {
            result.addLine(attacker.getName() + " wants to act, but " + defender.getName() + " is already defeated");
            return;
        }
        boolean crit = random.nextInt(100) < CRIT_CHANCE_PERCENT;
        result.addLine(attacker.getName() + " casts " + formatSkill(skill)
                + " on " + defender.getName() + (crit ? " [CRIT]" : ""));
        skill.cast(defender);
        if (crit && defender.isAlive()) {
            skill.cast(defender);
            result.addLine("CRIT effect: " + formatSkill(skill) + " cast second time.");
        }
    }

    private String formatSkill(Skill skill) {
        return skill.getSkillName() + " (" + skill.getEffectName() + ", base=" + skill.getBasePower() + ")";
    }
}


