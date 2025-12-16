## 📅 2025-12-16 - Maven Setup + Mobs

### 🚀 Goal 1: Start using Maven for building
* Refactored the project structure
* Created `pom.xml`. Set up Spiggot dependency. Current version: `1.21-R0.1-SNAPSHOT`.
*  **Status: Success**

### 🚀 Goal 2: Crazy Mobs
* Slime and Magma duplicating on hit
  * Potential Issues due to too much duplication / player abuse
* Fast Creepers
    * Made fast, but AI still stops when close
* Invisible skeletons
  * 🛑 STUCK - Can't figure out how to manipulate the packets to make client not see skeleton's bow
    * ✅ SOLVED
### 🛑 Current Main Problem Breakdown:

1. When are we supposed to hide the bow?
    * When player's client loads the Skeleton Entity
        * SPAWN_ENTITY seems correct
2. How do we hide the bow?
    * By replacing the ItemSlot.MAIN_HAND with AIR
        * ENTITY_EQUIPMENT? 
        * How to identify MAIN_HAND?
* **✅ Status: Solved**

---
---