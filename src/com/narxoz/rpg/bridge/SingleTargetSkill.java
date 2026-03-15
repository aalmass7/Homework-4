package com.narxoz.rpg.bridge;

import com.narxoz.rpg.composite.CombatNode;

import java.util.List;

public class SingleTargetSkill extends Skill {
    public SingleTargetSkill(String skillName, int basePower, EffectImplementor effect) {
        super(skillName, basePower, effect);
    }

    @Override
    public void cast(CombatNode target) {
        if (target == null || !target.isAlive()) {
            return;
        }

        int damage = resolvedDamage();
        if (damage <= 0) {
            return;
        }

        CombatNode chosenLeaf = chooseAliveLeafWithLowestHealth(target);
        if (chosenLeaf != null) {
            chosenLeaf.takeDamage(damage);
        }
    }

    private CombatNode chooseAliveLeafWithLowestHealth(CombatNode node){
            List<CombatNode> children = node.getChildren();
            if (children.isEmpty()) {
                return node.isAlive() ? node : null;
            }

        CombatNode best = null;
        int bestHp = Integer.MAX_VALUE;

        for (CombatNode child : children) {
            CombatNode candidate = chooseAliveLeafWithLowestHealth(child);
            if (candidate != null) {
                int hp = candidate.getHealth();
                if (best == null || hp < bestHp) {
                    best = candidate;
                    bestHp = hp;
                }
            }
        }

        return best;
    }
}
