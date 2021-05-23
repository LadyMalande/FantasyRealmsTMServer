package interactive;

import artificialintelligence.State;
import server.Card;
import server.ClientHandler;
import server.Server;
import server.Type;
import util.BigSwitches;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * The base class for all of the Interactive bonuses.
 * @author Tereza Miklóšová
 */
public class Interactive implements InteractiveBonusInterface , Serializable, Cloneable  {
    /**
     * Default interactive bonus priority value.
     */
    public int priority = 3;

    /**
     * Get localized text of the interactive bonus.
     * @param locale Target language.
     * @return Localized text of the interactive bonus for requested language.
     */
    public String getText(String locale){ return null; }

    /**
     * Get {@link Interactive#priority}.
     * @return {@link Interactive#priority}
     */
    public int getPriority(){
        return this.priority;
    }

    @Override
    public void askPlayer(ClientHandler client) {
    }

    /**
     * Creates a text of localized types with given separator and in given grammar case.
     * @param types Types to put into text.
     * @param locale Target language of the text.
     * @param grammar Grammar case of the types.
     * @return Localized text of types in given grammar case and joined by given separator.
     */
    String giveListOfTypesWithSeparator(ArrayList<Type> types, String locale, int grammar){
        Locale loc = new Locale(locale);
        ResourceBundle rs = ResourceBundle.getBundle("server.CardTypes",loc);
        StringBuilder listtypes = new StringBuilder();
        boolean first = true;
        for(Type type: types){
            if(!first && types.get(types.size() - 1).equals(type)){
                listtypes.append(" ");
                listtypes.append(rs.getString("or"));
                listtypes.append(" ");

            } else if (!first){
                listtypes.append(", ");
            }
            if(grammar == 1){
                listtypes.append(rs.getString(Objects.requireNonNull(BigSwitches.switchTypeForName(type)).toLowerCase()));

            } else{
                listtypes.append(rs.getString(Objects.requireNonNull(BigSwitches.switchTypeForName(type)).toLowerCase() + grammar));

            }
            first = false;
        }
        return listtypes.toString();
    }

    /**
     * Creates text of strings joined with given separator.
     * @param strings Strings to join together to a text.
     * @param separator Separator used for joining strings.
     * @return Text of given strings joined with given separator.
     */
    public String giveStringOfStringsWithSeparator(ArrayList<String> strings, String separator){
        StringBuilder listtypes = new StringBuilder();
        boolean first = true;
        for(String str: strings){
            if(!first){
                listtypes.append(separator);
            }
            listtypes.append(str);
            first = false;
        }
        return listtypes.toString();
    }

    /**
     * Clone the Interactive.
     * @return Cloned Interactive.
     * @throws CloneNotSupportedException Throws if the cloning had problems.
     */
    public Interactive clone() throws CloneNotSupportedException{
        return (Interactive)super.clone();
    }


    /**
     * Change the hand to optimal state in regard to final score.
     * @param originalHand The cards in hand.
     * @param cardsOnTable Cards on table.
     * @return Maximal score gained after changing the hand with the interactive bonus.
     * @throws CloneNotSupportedException Throws if the cloning had problems.
     */
    public int changeHandWithInteractive(ArrayList<Card> originalHand, ArrayList<Card> cardsOnTable, Server server,
        Integer idOfCardToChange, Card toChangeInto) throws CloneNotSupportedException {
        return 0;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        // TODO
        return 0.0;
    }

}
