import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class CommunityCards implements Serializable
{
	public final int MAX_COMMUNITY_CARDS = 5;
	
	protected static final long serialVersionUID = 1112122200L;
	
	ArrayList<Card> communityCards;
	
	public CommunityCards()
	{
		communityCards = new ArrayList<Card>();
	}
	
	//copy constructor
	public CommunityCards(CommunityCards communitycards)
	{
		this.communityCards = deepCopyCommunityCards(communitycards);
				
	}
	
	public void getTheFlop(Deck deck)
	{
		communityCards.add(deck.drawCard());
		communityCards.add(deck.drawCard());
		communityCards.add(deck.drawCard());
		
	}
	
	public void getTheRiver(Deck deck)
	{
		communityCards.add(deck.drawCard());
	}
	
	public void getTheTurn(Deck deck)
	{
		communityCards.add(deck.drawCard());
	}
	
	public  void clearCommunityCards()
	{
		communityCards.clear();
	}
	
	public ArrayList<Card> getCommunityCards()
	{
		return communityCards;
	}
	
	public void revealAllCommunityCards(Deck deck)
	{
		int currentCommunityCards = communityCards.size();
		
		for(int i = 0; i < MAX_COMMUNITY_CARDS-currentCommunityCards; i++)
			communityCards.add(deck.drawCard());
		
		//ASSERT
		if(communityCards.size() != 5)
			System.out.println("ERROR! COMMUNITY CARDS ARE NOT 5 AT SUDDEN DEATH ROUND, THEY ARE " + communityCards.size());
	}
	
	//temp method for testing 
	/*
	public int getCommunityCardSum()
	{
		Iterator<Integer> communityCardIterator = communityCards.listIterator();
		
		int sum = 0;
		
		 while(communityCardIterator.hasNext())
			 sum =+ communityCardIterator.next();
			 
		return sum;
		 
	}
	*/
	
	public String toString()
	{
		String cards = "";
		Iterator<Card> communityCardIterator = communityCards.listIterator();
		
		while(communityCardIterator.hasNext())
			 cards = String.valueOf(communityCardIterator.next()) + cards + " ";
		
		cards = cards + "/n";
		
		return cards;
		
	}
	
	public ArrayList<Card> deepCopyCommunityCards(CommunityCards sourceToCopy)
	{
		communityCards = new  ArrayList<Card>();
		
		Iterator<Card> communityCardIterator = sourceToCopy.getCommunityCards().listIterator();
		
		
		
		 while(communityCardIterator.hasNext())
		 {
			 communityCards.add(new Card(communityCardIterator.next()));
			 //pocketCardIterator.next().g;
		 }
		 
		 return communityCards;
	}
	
	
}
