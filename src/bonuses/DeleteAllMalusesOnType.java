package bonuses;

import artificialintelligence.State;
import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class DeleteAllMalusesOnType extends Bonus  {
    public long serialVersionUID = 4;
    public int priority = 5;
    public final String text;
    private Type deleteMalusesOnThisType;

    public DeleteAllMalusesOnType(Type t){
        this.deleteMalusesOnThisType = t;
        this.text = "Remove all maluses from type " + BigSwitches.switchTypeForName(deleteMalusesOnThisType);
        //System.out.println("Card INIT: Text: " + getText("en"));
        //System.out.println("Card INIT: Text: " + getText("cs"));
    }

    @Override
    public String getText(){
        return this.text;
    }
    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle bonuses = ResourceBundle.getBundle("bonuses.CardBonuses",loc);
        ResourceBundle rb = ResourceBundle.getBundle("server.CardTypes",loc);
        sb.append(bonuses.getString("deleteAllMalusesOnType"));
        sb.append(rb.getString(BigSwitches.switchTypeForName(deleteMalusesOnThisType).toLowerCase() + "6"));
        sb.append(".");
        return sb.toString();
    }
    @Override
    public int getPriority(){ return this.priority; }
    @Override
    public int count(ArrayList<Card> hand) {
        for(Card c: hand){
            if(c.type.equals(deleteMalusesOnThisType)){
                if(c.maluses != null){
                    c.maluses.clear();
                    //System.out.println("=========Mazu POSTIH NA karte " + c.name + " !!!//////////////---------" + this.getText());
                }

            }

        }
        return 0;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double potential = 0.0;
        double swamp = 0.0;
        double wildfire = 0.0;
        double greatflood = 0.0;
        for(Card c : hand){
            if( c.getId() == 35 ){
                swamp = c.getMaluses().get(0).count(hand);
            }
            if(c.getId() == 33 ){
                ArrayList<Card> whatToRemove = new ArrayList<>();
                c.getMaluses().get(0).count(hand, whatToRemove);
                greatflood = giveValue(hand, whatToRemove);
            }
            if(c.getId() == 41){
                ArrayList<Card> whatToRemove = new ArrayList<>();
                c.getMaluses().get(0).count(hand, whatToRemove);
                wildfire = giveValue(hand, whatToRemove);
            }
        }
        return Math.max(Math.max(swamp,wildfire), greatflood);
    }


}
