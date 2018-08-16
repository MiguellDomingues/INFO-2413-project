import java.util.ArrayList;




public class Deck 
{
	ArrayList<Card> drawnCards;
	
	public Deck()
	{
		drawnCards = new ArrayList<Card>();
	}
	
	//thia method does not take into account the probability changes of getting cards as the player draws cards
	//solution is to just make 52 cards, store them into a list and grab random cards when this method is called
	//zzzzzzzzzzzzz
	public Card drawCard()
	{
		if(drawnCards.size() == 52)
			return null;
		
		Card newCard;
		
		do
		{
			int suite = (int)(Math.random() * 4);
			int rank = (int)(Math.random() * 13);
			
			newCard = new Card(Card.Suite.values()[suite], Card.Rank.values()[rank]);
	    }
		while(drawnCards.contains(newCard));
		
		drawnCards.add(newCard);
		
		return newCard;
	}
	
	public void addBackDrawnCards()
	{
		drawnCards.clear();
	}
	
	public ArrayList<Card> getDrawnCards()
	{
		return drawnCards;
	}
	
	
	
	

}
