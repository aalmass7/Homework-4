package com.narxoz.rpg.composite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PartyComposite implements CombatNode {
    private final String name;
    private final List<CombatNode> children = new ArrayList<>();

    public PartyComposite(String name) {
        this.name = name;
    }

    public void add(CombatNode node) {
        if (node == null) {
            throw new IllegalArgumentException("node must not be null");
        }
        children.add(node);
    }

    public void remove(CombatNode node) {
        children.remove(node);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getHealth() {
        int sum = 0;
        for (CombatNode child : children) {
            sum += Math.max(0, child.getHealth());
        }
        return sum;
    }

    @Override
    public int getAttackPower() {
        int sum = 0;
        for (CombatNode child : children) {
            if (child.isAlive()) {
                sum += Math.max(0, child.getAttackPower());
            }
        }
        return sum;
    }

    @Override
    public void takeDamage(int amount) {
        int dmg = Math.max(0, amount);
        if (dmg == 0) {
            return;
        }
        List<CombatNode> aliveChildren = getAliveChildren();
        if (aliveChildren.isEmpty()) {
            return;
        }
        int n = aliveChildren.size();
        int baseShare = dmg / n;
        int remainder = dmg % n;

        for (int i = 0; i < n; i++) {
            int share = baseShare + (i < remainder ? 1 : 0);
            if (share > 0) {
                aliveChildren.get(i).takeDamage(share);
            }
        }
    }

    @Override
    public boolean isAlive() {
        for (CombatNode child : children) {
            if (child.isAlive()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<CombatNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public void printTree(String indent) {
        System.out.println(indent + "+ " + name + " [HP=" + getHealth() + ", ATK=" + getAttackPower() + "]");
        String childIndent = indent + "  ";
        for (CombatNode child : children) {
            child.printTree(childIndent);
        }
    }

    private List<CombatNode> getAliveChildren() {
        List<CombatNode> alive = new ArrayList<>();
        for (CombatNode child : children) {
            if (child.isAlive()) {
                alive.add(child);
            }
        }
        return alive;
    }
}
