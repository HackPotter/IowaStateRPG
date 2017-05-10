package com.example.jack.iowastate;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import java.util.ArrayList;
import java.lang.Math;
import java.util.Random;

public class enemy {
    private Context context;
    private String name = "";
    private int level = 0;
    private int maxHealth = 0;
    private int health = 0;
    private int attack = 0;
    private int defense = 0;
    private int critChance;
    private int playerAttackDamage;
    private int enemyAttackDamage;

    public enemy (int area, int playerLevel, Context current)
    {
        this.context = current;

        Random random = new Random();
        level = Math.max(area, playerLevel); //Set enemy level
        int levelModifer = random.nextInt(3);
        switch (levelModifer)
        {
            case 0:
                level = level + 1;
                break;
            case 1:
                level = level - 1;
                if(level == 0) { level = 1; }
                break;
            default:
                break;
        }
        maxHealth = level * 2 + random.nextInt(level); //Default max health value
        health = maxHealth;
        attack = level + random.nextInt(level); //Default attack value
        defense = random.nextInt(level); //Default defense value
        critChance = 10; //default crit percentage

        switch (area)
        {
            case 1: //Seasons Marketplace
                name = context.getResources().getString(R.string.SeasonsEnemy);
                break;
            case 2: //Memorial Union
                name = context.getResources().getString(R.string.UnionEnemy);
                break;
            case 3: //Parks Library
                name = context.getResources().getString(R.string.ParksEnemy);
                break;
            case 4: //State Gym
                name = context.getResources().getString(R.string.StateGymEnemy);
                break;
            case 5: //Troxel
                name = context.getResources().getString(R.string.TroxelEnemy);
                break;
            case 6: //Armory
                name = context.getResources().getString(R.string.ArmoryEnemy);
                break;
            case 7: //Hilton
                name = context.getResources().getString(R.string.HiltonEnemy);
                break;
            case 8: //Howe
                name = context.getResources().getString(R.string.HoweEnemy);
                break;
            case 9: //Black
                name = context.getResources().getString(R.string.BlackEnemy);
                break;
            case 10: //Howe
                name = context.getResources().getString(R.string.CooverEnemy);
                break;
            default:
                name = "UNDEFINED";
        }


    }

    public String getEnemyName() { return name; }

    public int getEnemyLevel() {return level; }

    public int getEnemyHealth() { return health; }

    public int getEnemyMaxHealth() { return maxHealth; }

    public int getEnemyAttack() { return attack; }

    public int getEnemyDefense() { return defense; }

    public int getPlayerAttackDamage() { return playerAttackDamage; }

    public void heal()
    {
        health = maxHealth;
    }

    public int fight(int playerAttack, int playerDefence, int playerCritChance)
    {
        Random critical = new Random();
        boolean playerCrit = false, enemyCrit = false;
        if(critical.nextInt(100) < playerCritChance) { playerCrit = true; }
        if(critical.nextInt(100) < critChance) { enemyCrit = true; }

        int enemyDamage = playerAttack - defense;
        if(playerCrit) { enemyDamage = (enemyDamage + 1) * 2; }
        enemyDamage = Math.max(enemyDamage, 0);
        playerAttackDamage = enemyDamage;

        health -= enemyDamage;
        if(health <= 0)
        {
            return (level * -1); //Enemy defeated, player heals based on enemy level
        }

        int playerDamage = attack - playerDefence;
        if(enemyCrit) { playerDamage = (playerDamage + 1) * 2; }

        return playerDamage;
    }
}
