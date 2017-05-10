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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import im.delight.android.location.SimpleLocation;


public class MainActivity extends AppCompatActivity {

    private Spinner spinner, spinner2;
    private int level;
    private int exp;
    private int health;
    private int maxHealth;
    private int playerCritChance = 10;
    private double latitude;
    private double longitude;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private SimpleLocation location;
    private enemy currentEnemy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        level = 1;
        exp = 0;
        maxHealth = 10;
        health = maxHealth;
        AddArmorToSpinner(level);
        AddWeaponsToSpinner(level);
        location = new SimpleLocation(this);
        // if we can't access the location yet
        if (!location.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(this);
        }
    }

    public void AddWeaponsToSpinner(int level) {
        spinner = (Spinner) findViewById(R.id.spinner);
        List<String> list = new ArrayList<String>();
        int i = level;
        while (i > 0) {
            if (i == 1) {
                list.add(getResources().getString(R.string.weapon1));
            }
            if (i == 2) {
                list.add(getResources().getString(R.string.weapon2));
            }
            if (i == 3) {
                list.add(getResources().getString(R.string.weapon3));
            }
            if (i == 4) {
                list.add(getResources().getString(R.string.weapon4));
            }
            if (i == 5) {
                list.add(getResources().getString(R.string.weapon5));
            }
            if (i == 6) {
                list.add(getResources().getString(R.string.weapon6));
            }
            if (i == 7) {
                list.add(getResources().getString(R.string.weapon7));
            }
            if (i == 8) {
                list.add(getResources().getString(R.string.weapon8));
            }
            if (i == 9) {
                list.add(getResources().getString(R.string.weapon9));
            }
            i--;
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    public void AddArmorToSpinner(int level) {
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        List<String> list = new ArrayList<String>();
        int i = level;
        while (i > 0) {
            if (i == 1) {
                list.add(getResources().getString(R.string.armor1));
            }
            if (i == 2) {
                list.add(getResources().getString(R.string.armor2));
            }
            if (i == 3) {
                list.add(getResources().getString(R.string.armor3));
            }
            if (i == 4) {
                list.add(getResources().getString(R.string.armor4));
            }
            if (i == 5) {
                list.add(getResources().getString(R.string.armor5));
            }
            if (i == 6) {
                list.add(getResources().getString(R.string.armor6));
            }
            if (i == 7) {
                list.add(getResources().getString(R.string.armor7));
            }
            if (i == 8) {
                list.add(getResources().getString(R.string.armor8));
            }
            if (i == 9) {
                list.add(getResources().getString(R.string.armor9));
            }
            i--;
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(dataAdapter);

    }

    public void Attack(View view) {
        LocatePlayer();
        TextView statusBar = (TextView) findViewById(R.id.textView9);

        if(GetArea() == 0){
            statusBar.setText("You aren't in a dungeon!");
            return;
        }

        TextView enemyName = (TextView) findViewById(R.id.textView7);
        TextView enemyHealth = (TextView) findViewById(R.id.textView8);
        TextView levelingInfo = (TextView) findViewById(R.id.textView11);

        String status = "";
        levelingInfo.setText("");

        updatePlayerInfo();

        int weaponValue = GetWeaponValue();
        int armorValue = GetArmorValue();

        if(currentEnemy == null || currentEnemy.getEnemyHealth() <= 0)
        {
            currentEnemy = new enemy(GetArea(), level, this);
        }

        if(currentEnemy.getEnemyHealth() > 0)
        {
            enemyName.setText("Level " + currentEnemy.getEnemyLevel() + " " + currentEnemy.getEnemyName());
            int fight = currentEnemy.fight((level + weaponValue), (level + armorValue), playerCritChance);
            if(fight < 0) {
                health -= fight;
                health = Math.min(health, maxHealth);
                status = ("You defeated level " + currentEnemy.getEnemyLevel() + " " + currentEnemy.getEnemyName());
                enemyHealth.setText("Enemy Health: 0 / " + currentEnemy.getEnemyMaxHealth());
                if (level > GetArea())
                {
                    status = ("You defeated level " + currentEnemy.getEnemyLevel() + " " + currentEnemy.getEnemyName() + ", but gained no experience.");
                    levelingInfo.setText("This dungeon is too easy for a warrior of your ability!");
                }
                else
                {
                    exp += currentEnemy.getEnemyLevel();

                }
                LevelUp(level, exp);
                updatePlayerInfo();
            }
            else
            {
                health -= fight;
                status = ("You did " + currentEnemy.getPlayerAttackDamage() + " Damage to level " + currentEnemy.getEnemyLevel() + " " + currentEnemy.getEnemyName());
                enemyHealth.setText("Enemy Health: " + currentEnemy.getEnemyHealth() + " / " + currentEnemy.getEnemyMaxHealth());
                status += (". " + currentEnemy.getEnemyName() + " did " + fight + " to You." );
                updatePlayerInfo();

            }
            statusBar.setText(status);
            if(health <= 0)
            {
                playerDeath();
            }
        }



        DisplayHealth(view);
    }

    public void DisplayHealth(View view) {
        TextView health = (TextView) findViewById(R.id.textView3);
        health.setText("Health: " + this.health);
    }

    public void LevelUp(int currentLevel, int currentExp) {
        TextView levelingInfo = (TextView) findViewById(R.id.textView11);
        if (currentExp >= currentLevel * 10) {
            level++;
            exp = 0;
            maxHealth = level * 10;
            health = maxHealth;
            levelingInfo.setText("Level up!");
            AddWeaponsToSpinner(level);
            AddArmorToSpinner(level);
        }
        updatePlayerInfo();
    }


    @Override
    protected void onResume() {
        super.onResume();

        // make the device update its location
        location.beginUpdates();

    }

    @Override
    protected void onPause() {
        // stop location updates (saves battery)
        location.endUpdates();


        super.onPause();
    }

    public void LocatePlayer()
    {
        onResume();
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    private int GetArmorValue()
    {
        Spinner armorSpinner = (Spinner) findViewById(R.id.spinner2);
        String armor = armorSpinner.getSelectedItem().toString();
        int armorValue = 1;

        if (armor.equals(getResources().getString(R.string.armor1))) {
            armorValue = 1;
        } else if (armor.equals(getResources().getString(R.string.armor2))) {
            armorValue = 2;
        } else if (armor.equals(getResources().getString(R.string.armor3))) {
            armorValue = 3;
        } else if (armor.equals(getResources().getString(R.string.armor4))) {
            armorValue = 4;
        } else if (armor.equals(getResources().getString(R.string.armor5))) {
            armorValue = 5;
        } else if (armor.equals(getResources().getString(R.string.armor6))) {
            armorValue = 6;
        } else if (armor.equals(getResources().getString(R.string.armor7))) {
            armorValue = 7;
        } else if (armor.equals(getResources().getString(R.string.armor8))) {
            armorValue = 8;
        } else if (armor.equals(getResources().getString(R.string.armor9))) {
            armorValue = 9;
        }

        return armorValue;
    }

    private int GetWeaponValue()
    {
        Spinner weaponSpinner = (Spinner) findViewById(R.id.spinner);
        String weapon = weaponSpinner.getSelectedItem().toString();
        int weaponValue = 1;

        if (weapon.equals(getResources().getString(R.string.weapon1))) {
            weaponValue = 1;
        } else if (weapon.equals(getResources().getString(R.string.weapon2))) {
            weaponValue = 2;
        } else if (weapon.equals(getResources().getString(R.string.weapon3))) {
            weaponValue = 3;
        } else if (weapon.equals(getResources().getString(R.string.weapon4))) {
            weaponValue = 4;
        } else if (weapon.equals(getResources().getString(R.string.weapon5))) {
            weaponValue = 5;
        } else if (weapon.equals(getResources().getString(R.string.weapon6))) {
            weaponValue = 6;
        } else if (weapon.equals(getResources().getString(R.string.weapon7))) {
            weaponValue = 7;
        } else if (weapon.equals(getResources().getString(R.string.weapon8))) {
            weaponValue = 8;
        } else if (weapon.equals(getResources().getString(R.string.weapon9))) {
            weaponValue = 9;
        }

        return weaponValue;
    }

    private int GetArea()
    {
        TextView dungeon = (TextView) findViewById(R.id.textView10);

        double seasons = location.calculateDistance(Double.parseDouble(getResources().getString(R.string.SeasonsLat)), Double.parseDouble(getResources().getString(R.string.SeasonsLon)), latitude, longitude);
        double union = location.calculateDistance(Double.parseDouble(getResources().getString(R.string.UnionLat)), Double.parseDouble(getResources().getString(R.string.UnionLon)), latitude, longitude);
        double parks = location.calculateDistance(Double.parseDouble(getResources().getString(R.string.ParksLat)), Double.parseDouble(getResources().getString(R.string.ParksLon)), latitude, longitude);
        double state = location.calculateDistance(Double.parseDouble(getResources().getString(R.string.StateGymLat)), Double.parseDouble(getResources().getString(R.string.StateGymLon)), latitude, longitude);
        double troxel = location.calculateDistance(Double.parseDouble(getResources().getString(R.string.TroxelLat)), Double.parseDouble(getResources().getString(R.string.TroxelLon)), latitude, longitude);
        double armory = location.calculateDistance(Double.parseDouble(getResources().getString(R.string.ArmoryLat)), Double.parseDouble(getResources().getString(R.string.ArmoryLon)), latitude, longitude);
        double hilton = location.calculateDistance(Double.parseDouble(getResources().getString(R.string.HiltonLat)), Double.parseDouble(getResources().getString(R.string.HiltonLon)), latitude, longitude);
        double howe = location.calculateDistance(Double.parseDouble(getResources().getString(R.string.HoweLat)), Double.parseDouble(getResources().getString(R.string.HoweLon)), latitude, longitude);
        double black = location.calculateDistance(Double.parseDouble(getResources().getString(R.string.BlackLat)), Double.parseDouble(getResources().getString(R.string.BlackLon)), latitude, longitude);
        double coover = location.calculateDistance(Double.parseDouble(getResources().getString(R.string.CooverLat)), Double.parseDouble(getResources().getString(R.string.CooverLon)), latitude, longitude);

        if(seasons < 30.0) { dungeon.setText("Current Dungeon: Seasons Marketplace"); return 1; } //level 1: Seasons Marketplace
        if(union < 50.0) { dungeon.setText("Current Dungeon: Memorial Union"); return 2; } //level 2: Memorial Union
        if(parks < 30.0) { dungeon.setText("Current Dungeon: Parks Library"); return 3; } //level 3: Parks library
        if(state < 36.0) { dungeon.setText("Current Dungeon: State Gym"); return 4; } //level 4: State Gym
        if(troxel < 20.0) { dungeon.setText("Current Dungeon: Troxel Hall"); return 5; } //level 5: Troxel Hall
        if(armory < 40.0) { dungeon.setText("Current Dungeon: The Armory"); return 6; } //level 6: The Armory
        if(hilton < 55.0) { dungeon.setText("Current Dungeon: Hilton Coliseum"); return 7; } //level 7: Hilton Coliseum
        if(howe < 50.0) { dungeon.setText("Current Dungeon: Howe Hall"); return 8; } //level 8: Howe Hall
        if(black < 40.0) { dungeon.setText("Current Dungeon: Black Engineering"); return 9; } //level 9: Black Engineering
        if(coover < 41.0) { dungeon.setText("Current Dungeon: Coover Hall"); return 10; } //level 10: Coover Hall

        dungeon.setText("Currently Wandering Between Dungeons");
        return 0; //If 0 is returned, the player isn't in a dungeon area
    }

    public void playerDeath()
    {
        TextView status = (TextView) findViewById(R.id.textView9);
        if(level > 1)
        {
            level--;
            maxHealth = level * 10;
            status.setText("You were killed and lost a level!");
        }
        else
        {
            status.setText("You were killed! Fortunately, you regenerated.");
        }
        health = maxHealth;
        currentEnemy = new enemy(GetArea(), level, this);
        exp = 0;
        updatePlayerInfo();
    }

    public void updatePlayerInfo()
    {
        TextView playerLevel = (TextView) findViewById(R.id.textView4);
        TextView playerExp = (TextView) findViewById(R.id.textView5);
        TextView nextLevel = (TextView) findViewById(R.id.textView6);

        playerLevel.setText("Level: " + Integer.toString(level));
        playerExp.setText("Experience: " + Integer.toString(exp));
        nextLevel.setText(Integer.toString((level * 10) - exp) + " to Next Level");
    }

}



