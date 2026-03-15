package com.narxoz.rpg.bridge;

import com.narxoz.rpg.composite.CombatNode;

import java.util.List;

public class AreaSkill extends Skill {
    public AreaSkill(String skillName, int basePower, EffectImplementor effect) {
        super(skillName, basePower, effect);
    }

    @Override
    public void cast(CombatNode target) {
        if(target == null || !target.isAlive()){
            return;
        }
        int damage = resolvedDamage();
        if(damage <= 0){
            return;
        }
        applyToAllAliveLeaves(target, damage);
    }

    private void applyToAllAliveLeaves(CombatNode node, int damage){
        List<CombatNode> children = node.getChildren();
        if(children.isEmpty()){
            if(node.isAlive()){
                node.takeDamage(damage);
            }
            return;
        }
        for(CombatNode child : children){
            applyToAllAliveLeaves(child, damage);
        }
    }
}
