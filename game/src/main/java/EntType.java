package main.java;

/** All types of entities in the game. **/
public enum EntType
{
    /** Indicates the entity is the player. **/
    PLAYER,
	/** Indicates the entity is a projectile from the player. **/
    PROJECTILE,
	/** Indicates the entity is the boss. **/
    BOSS,
	/** Indicates the entity is a projectile from the boss. **/
    BOSS_PROJECTILE,
	/** Indicates the entity is an experience orb. **/
	XP_ORB
}
