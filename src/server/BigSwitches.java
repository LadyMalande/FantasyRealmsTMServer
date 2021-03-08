package server;

import static server.Type.*;

public class BigSwitches {

    public static String switchIdForName(int id){
        switch(id){
            case 1: return "Unicorn";
            case 2: return "Hydra";
            case 3: return "Basilisk";
            case 4: return "Warhorse";
            case 5: return "Dragon";
            case 6: return "Magic Wand";
            case 7: return "War Dirigible";
            case 8: return "Warship";
            case 9: return "Elven Longbow";
            case 10: return "Sword";
            case 11: return "Beastmaster";
            case 12: return "Collector";
            case 13: return "Necromant";
            case 14: return "Jester";
            case 15: return "Enchantress";
            case 16: return "Warlock Lord";
            case 17: return "Princess";
            case 18: return "Commander";
            case 19: return "Queen";
            case 20: return "King";
            case 21: return "Empress";
            case 22: return "Knights";
            case 23: return "Rangers";
            case 24: return "Dwarf Infantry";
            case 25: return "Archers";
            case 26: return "Cavalry";
            case 27: return "Shield";
            case 28: return "Protection Rune";
            case 29: return "Gem of Order";
            case 30: return "World Tree";
            case 31: return "Spellbook";
            case 32: return "Fountain of Life";
            case 33: return "Great Flood";
            case 34: return "Water Elemental";
            case 35: return "Swamp";
            case 36: return "Island";
            case 37: return "Fire Elemental";
            case 38: return "Lightning";
            case 39: return "Candle";
            case 40: return "Forge";
            case 41: return "Wildfire";
            case 42: return "Belfry";
            case 43: return "Earth Elemental";
            case 44: return "Cave";
            case 45: return "Mountain";
            case 46: return "Forest";
            case 47: return "Blizzard";
            case 48: return "Air Elemental";
            case 49: return "Tornado";
            case 50: return "Storm";
            case 51: return "Smoke";
            case 52: return "Skinchanger";
            case 53: return "Mirage";
            case 54: return "Doppleganger";

            default: return "FAIL";
        }
    }

    public static String switchTypeForName(Type type){
        if (type == null) {
            System.out.println("Null pointer v switchTypeForName");
            return null;

        }
        switch(type){
            case ARMY: return "Army";
            case CREATURE: return "Creature";
            case LEADER: return "Leader";
            case WIZARD: return "Wizard";
            case FLOOD: return "Flood";
            case EARTH: return "Earth";
            case FIRE: return "Fire";
            case WEAPON: return "Weapon";
            case WEATHER: return "Weather";
            case ARTIFACT: return "Artifact";
            case WILD: return "Wild";
            default: return "FAIL";
        }
    }
    public static Type switchNameForType(String name){
        if (name == null) {
            System.out.println("Null pointer v switchNameForType");
            return null;

        }
        switch(name){
            case "Army": return ARMY ;
            case "Creature": return CREATURE;
            case "Leader":  return LEADER;
            case "Wizard": return Type.WIZARD;
            case "Flood": return Type.FLOOD;
            case "Earth": return Type.EARTH;
            case "Fire": return Type.FIRE;
            case "Weapon": return Type.WEAPON;
            case "Weather": return Type.WEATHER;
            case "Artifact": return Type.ARTIFACT;
            case "Wild": return Type.WILD;
            default: return null;
        }
    }


    public static Type switchCardNameForType(String name){
        if (name == null) {
            System.out.println("Null pointer v switchCardNameForType");
            return null;

        }
        switch(name){
            case "Knights":
            case "Rangers":
            case "Dwarf Infantry":
            case "Archers":
            case "Cavalry":
                return ARMY ;

            case "Unicorn":
            case "Basilisk":
            case "Dragon":
            case "Warhorse":
            case "Hydra":
                return CREATURE;

            case "Princess":
            case "Commander":
            case "Queen":
            case "Empress":
            case "King":
                return LEADER;

            case "Beastmaster":
            case "Collector":
            case "Jester":
            case "Warlock Lord":
            case "Enchantress":
            case "Necromant":
                return WIZARD;

            case "Fountain of Life":
            case "Great Flood":
            case "Swamp":
            case "Island":
            case "Water Elemental":
                return FLOOD;

            case "Belfry":
            case "Earth Elemental":
            case "Mountain":
            case "Cave":
            case "Forest":
                return EARTH;

            case "Fire Elemental":
            case "Wildfire":
            case "Forge":
            case "Candle":
            case "Lightning":
                return FIRE;

            case "Magic Wand":
            case "War Dirigible":
            case "Warship":
            case "Elven Longbow":
            case "Sword":
                return WEAPON;

            case "Shield":
            case "Protection Rune":
            case "Gem of Order":
            case "World Tree":
            case "Spellbook":
                return ARTIFACT;

            case "Air Elemental":
            case "Smoke":
            case "Blizzard":
            case "Storm":
            case "Tornado":
                return WEATHER;

            case "Skinchanger":
            case "Doppleganger":
            case "Mirage":
                return WILD;
            default: return null;
        }
    }

}
