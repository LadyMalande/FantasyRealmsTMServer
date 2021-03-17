package server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

import static server.Type.*;

public class BigSwitches {

    public static String switchIdForName(int id){
        Locale loc = new Locale("en");
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames", loc);
        switch(id){
            case 1: return  rb.getString("unicorn");
            case 2: return rb.getString("hydra");
            case 3: return rb.getString("basilisk");
            case 4: return rb.getString("warhorse");
            case 5: return rb.getString("dragon");
            case 6: return rb.getString("wand");
            case 7: return rb.getString("dirigible");
            case 8: return rb.getString("warship");
            case 9: return rb.getString("bow");
            case 10: return rb.getString("sword");
            case 11: return rb.getString("beastmaster");
            case 12: return rb.getString("collector");
            case 13: return rb.getString("necromance");
            case 14: return rb.getString("jester");
            case 15: return rb.getString("enchantress");
            case 16: return rb.getString("warlock");
            case 17: return rb.getString("princess");
            case 18: return rb.getString("warlord");
            case 19: return rb.getString("queen");
            case 20: return rb.getString("king");
            case 21: return rb.getString("empress");
            case 22: return rb.getString("knights");
            case 23: return rb.getString("rangers");
            case 24: return rb.getString("dwarfs");
            case 25: return rb.getString("archers");
            case 26: return rb.getString("cavalry");
            case 27: return rb.getString("shield");
            case 28: return rb.getString("rune");
            case 29: return rb.getString("gem");
            case 30: return rb.getString("tree");
            case 31: return rb.getString("book");
            case 32: return rb.getString("fountain");
            case 33: return rb.getString("greatflood");
            case 34: return rb.getString("welemental");
            case 35: return rb.getString("swamp");
            case 36: return rb.getString("island");
            case 37: return rb.getString("felemental");
            case 38: return rb.getString("lightning");
            case 39: return rb.getString("candle");
            case 40: return rb.getString("forge");
            case 41: return rb.getString("wildfire");
            case 42: return rb.getString("belfry");
            case 43: return rb.getString("eelemental");
            case 44: return rb.getString("cavern");
            case 45: return rb.getString("mountain");
            case 46: return rb.getString("forest");
            case 47: return rb.getString("blizzard");
            case 48: return rb.getString("aelemental");
            case 49: return rb.getString("tornado");
            case 50: return rb.getString("storm");
            case 51: return rb.getString("smoke");
            case 52: return rb.getString("shapeshifter");
            case 53: return rb.getString("mirage");
            case 54: return rb.getString("doppleganger");

            default: return "FAIL";
        }
    }

    public static String switchNameForOriginalName(String name, Locale locale){
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames", locale);
        ResourceBundle rben = ResourceBundle.getBundle("server.CardNames", new Locale("en"));
        for(String key : rb.keySet()){
            if(rb.getString(key).equals(name)){
                return rben.getString(key);
            }
        }
        return "FAIL";
    }

    public static String switchIdForName(int id, String locale){
        Locale loc = new Locale(locale);
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames", loc);
        switch(id){
            case 1: return  rb.getString("unicorn");
            case 2: return rb.getString("hydra");
            case 3: return rb.getString("basilisk");
            case 4: return rb.getString("warhorse");
            case 5: return rb.getString("dragon");
            case 6: return rb.getString("wand");
            case 7: return rb.getString("dirigible");
            case 8: return rb.getString("warship");
            case 9: return rb.getString("bow");
            case 10: return rb.getString("sword");
            case 11: return rb.getString("beastmaster");
            case 12: return rb.getString("collector");
            case 13: return rb.getString("necromancer");
            case 14: return rb.getString("jester");
            case 15: return rb.getString("enchantress");
            case 16: return rb.getString("warlock");
            case 17: return rb.getString("princess");
            case 18: return rb.getString("warlord");
            case 19: return rb.getString("queen");
            case 20: return rb.getString("king");
            case 21: return rb.getString("empress");
            case 22: return rb.getString("knights");
            case 23: return rb.getString("rangers");
            case 24: return rb.getString("dwarfs");
            case 25: return rb.getString("archers");
            case 26: return rb.getString("cavalry");
            case 27: return rb.getString("shield");
            case 28: return rb.getString("rune");
            case 29: return rb.getString("gem");
            case 30: return rb.getString("tree");
            case 31: return rb.getString("book");
            case 32: return rb.getString("fountain");
            case 33: return rb.getString("greatflood");
            case 34: return rb.getString("welemental");
            case 35: return rb.getString("swamp");
            case 36: return rb.getString("island");
            case 37: return rb.getString("felemental");
            case 38: return rb.getString("lightning");
            case 39: return rb.getString("candle");
            case 40: return rb.getString("forge");
            case 41: return rb.getString("wildfire");
            case 42: return rb.getString("belfry");
            case 43: return rb.getString("eelemental");
            case 44: return rb.getString("cavern");
            case 45: return rb.getString("mountain");
            case 46: return rb.getString("forest");
            case 47: return rb.getString("blizzard");
            case 48: return rb.getString("aelemental");
            case 49: return rb.getString("tornado");
            case 50: return rb.getString("storm");
            case 51: return rb.getString("smoke");
            case 52: return rb.getString("shapeshifter");
            case 53: return rb.getString("mirage");
            case 54: return rb.getString("doppleganger");

            default: return "FAIL";
        }
    }

    public static String switchIdForSimplifiedName(int id){
        switch(id){
            case 1: return "unicorn";
            case 2: return "hydra";
            case 3: return "basilisk";
            case 4: return "warhorse";
            case 5: return "dragon";
            case 6: return "wand";
            case 7: return "dirigible";
            case 8: return "warship";
            case 9: return "bow";
            case 10: return "sword";
            case 11: return "beastmaster";
            case 12: return "collector";
            case 13: return "necromant";
            case 14: return "jester";
            case 15: return "enchantress";
            case 16: return "warlock";
            case 17: return "princess";
            case 18: return "warlord";
            case 19: return "queen";
            case 20: return "king";
            case 21: return "empress";
            case 22: return "knights";
            case 23: return "rangers";
            case 24: return "dwarfs";
            case 25: return "archers";
            case 26: return "cavalry";
            case 27: return "shield";
            case 28: return "rune";
            case 29: return "gem";
            case 30: return "tree";
            case 31: return "book";
            case 32: return "fountain";
            case 33: return "greatflood";
            case 34: return "welemental";
            case 35: return "swamp";
            case 36: return "island";
            case 37: return "felemental";
            case 38: return "lightning";
            case 39: return "candle";
            case 40: return "forge";
            case 41: return "wildfire";
            case 42: return "belfry";
            case 43: return "eelemental";
            case 44: return "cavern";
            case 45: return "mountain";
            case 46: return "forest";
            case 47: return "blizzard";
            case 48: return "aelemental";
            case 49: return "tornado";
            case 50: return "storm";
            case 51: return "smoke";
            case 52: return "shapeshifter";
            case 53: return "mirage";
            case 54: return "doppleganger";

            default: return "FAIL";
        }
    }

    public static String switchTypeForName(Type type, String locale){
        if (type == null) {
            System.out.println("Null pointer v switchTypeForName");
            return null;

        }
        Locale loc = new Locale(locale);
        ResourceBundle rb = ResourceBundle.getBundle("server.CardTypes", loc);
        switch(type){
            case ARMY: return rb.getString("army");
            case BEAST: return rb.getString("beast");
            case LEADER: return rb.getString("leader");
            case WIZARD: return rb.getString("wizard");
            case FLOOD: return rb.getString("flood");
            case LAND: return rb.getString("land");
            case FLAME: return rb.getString("flame");
            case WEAPON: return rb.getString("weapon");
            case WEATHER: return rb.getString("weather");
            case ARTIFACT: return rb.getString("artifact");
            case WILD: return rb.getString("wild");
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
            case BEAST: return "Beast";
            case LEADER: return "Leader";
            case WIZARD: return "Wizard";
            case FLOOD: return "Flood";
            case LAND: return "Land";
            case FLAME: return "Flame";
            case WEAPON: return "Weapon";
            case WEATHER: return "Weather";
            case ARTIFACT: return "Artifact";
            case WILD: return "Wild";
            default: return "FAIL";
        }
    }

    public static String switchTypeForGender(Type type){
        switch(type){
            case WEATHER: return "S";
            case ARMY:
            case FLOOD:
            case LAND:
            case WEAPON:
            case WILD: return "Z";
            case FLAME:
            case ARTIFACT: return "MN";
            default: return "";
        }
    }

    public static Type switchNameForType(String name){
        if (name == null) {
            System.out.println("Null pointer v switchNameForType");
            return null;

        }
        switch(name){
            case "Army":
            case "Armáda": return ARMY ;
            case "Beast":
            case "Tvor": return BEAST;
            case "Leader":
            case "Vůdce": return LEADER;
            case "Wizard":
            case "Čaroděj": return WIZARD;
            case "Flood":
            case "Potopa": return FLOOD;
            case "Land":
            case "Země": return LAND;
            case "Flame":
            case "Oheň": return FLAME;
            case "Weapon":
            case "Zbraň": return WEAPON;
            case "Weather":
            case "Počasí": return WEATHER;
            case "Artifact":
            case "Artefakt": return ARTIFACT;
            case "Wild":
            case "Divoká": return WILD;
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
                return BEAST;

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
                return LAND;

            case "Fire Elemental":
            case "Wildfire":
            case "Forge":
            case "Candle":
            case "Lightning":
                return FLAME;

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

    public static ArrayList<String> switchTypeForNames(Type type, String locale){
        Locale loc = new Locale(locale);
        ResourceBundle rs = ResourceBundle.getBundle("CardNames",loc);
        switch(type){
            case FLAME: return new ArrayList<>(
                    Arrays.asList(rs.getString("forge"), rs.getString("wildfire"), rs.getString("felemental"), rs.getString("lightning"), rs.getString("candle")));
            case ARMY: return new ArrayList<>(
                    Arrays.asList(rs.getString("knights"), rs.getString("rangers"), rs.getString("dwarfs"), rs.getString("archers"), rs.getString("cavalry")));
            case WILD: return new ArrayList<>(
                    Arrays.asList(rs.getString("shapeshifter"), rs.getString("doppleganger"), rs.getString("mirage")));
            case LAND: return new ArrayList<>(
                    Arrays.asList(rs.getString("forest"), rs.getString("mountain"), rs.getString("cavern"), rs.getString("belfry"), rs.getString("eelemental")));
            case FLOOD: return new ArrayList<>(
                    Arrays.asList(rs.getString("island"), rs.getString("greatflood"), rs.getString("welemental"), rs.getString("fountain"), rs.getString("swamp")));
            case LEADER: return new ArrayList<>(
                    Arrays.asList(rs.getString("princess"), rs.getString("king"), rs.getString("queen"), rs.getString("empress"), rs.getString("warlord")));
            case WEAPON: return new ArrayList<>(
                    Arrays.asList(rs.getString("wand"), rs.getString("bow"), rs.getString("sword"), rs.getString("warship"), rs.getString("dirigible")));
            case WIZARD: return new ArrayList<>(
                    Arrays.asList(rs.getString("jester"), rs.getString("collector"), rs.getString("warlock"), rs.getString("necromancer"), rs.getString("enchantress"), rs.getString("beastmaster")));
            case WEATHER: return new ArrayList<>(
                    Arrays.asList(rs.getString("tornado"), rs.getString("blizzard"), rs.getString("aelemental"), rs.getString("smoke"), rs.getString("storm")));
            case ARTIFACT: return new ArrayList<>(
                    Arrays.asList(rs.getString("rune"), rs.getString("shield"), rs.getString("tree"), rs.getString("book"), rs.getString("gem")));
            case BEAST: return new ArrayList<>(
                    Arrays.asList(rs.getString("dragon"), rs.getString("hydra"), rs.getString("basilisk"), rs.getString("unicorn"), rs.getString("warhorse")));
            default: return null;
        }


    }

    public static ArrayList<String> switchTypeForNames(Type type){
        switch(type){
            case FLAME: return new ArrayList<>(
                    Arrays.asList("Forge", "Wildfire", "Fire Elemental", "Lightning", "Candle"));
            case ARMY: return new ArrayList<>(
                    Arrays.asList("Knights", "Striders", "Dwarf Infantry", "Archers", "Cavalry"));
            case WILD: return new ArrayList<>(
                    Arrays.asList("Skinchanger", "Doppleganger", "Mirage"));
            case LAND: return new ArrayList<>(
                    Arrays.asList("Forest", "Mountain", "Cave", "Earth Elemental", "Belfry"));
            case FLOOD: return new ArrayList<>(
                    Arrays.asList("Water Elemental", "Island", "Swamp", "Great Flood", "Fountain of Life"));
            case LEADER: return new ArrayList<>(
                    Arrays.asList("Princess", "Queen", "King", "Commander", "Empress"));
            case WEAPON: return new ArrayList<>(
                    Arrays.asList("Magic Wand", "War Dirigible", "Warship", "Elven Longbow", "Sword"));
            case WIZARD: return new ArrayList<>(
                    Arrays.asList("Necromant", "Enchantress", "Warlock Lord", "Collector", "Jester", "Beastmaster"));
            case WEATHER: return new ArrayList<>(
                    Arrays.asList("Tornado", "Storm", "Blizzard", "Smoke", "Air Elemental"));
            case ARTIFACT: return new ArrayList<>(
                    Arrays.asList("Shield", "Protection Rune", "World Tree", "Spellbook", "Gem of Order"));
            case BEAST: return new ArrayList<>(
                    Arrays.asList("Hydra", "Basilisk", "Warhorse", "Dragon", "Unicorn"));
            default: return null;
        }


    }

}
