import java.io.Serializable;


public class Card implements Serializable 
{
	//Rank rank1 = Rank.values()[2];
	/*
	 * may need to overwrite compare to method for use in the future handevaluator class
	 */
	

	//{"Spade","Heart","Diamond","Club"};
	//{"A","K","Q","J","10","9","8","7","6","5","4","3","2"};
	static public enum Suite
	{
		SPADE(0),HEART(1),DIAMOND(2),CLUB(3);
		
		private final int value;
		
	    private Suite(int value) 
	    {
	        this.value = value;
	    }

	    public int getValue() 
	    {
	        return value;
	    }
	}
	
	static public enum Rank
	{
		TWO(0),THREE(1),FOUR(2),FIVE(3),SIX(4),SEVEN(5),EIGHT(6),NINE(7),TEN(8),JACK(9),QUEEN(10),KING(11),ACE(12);
		
		private final int value;
		
	    private Rank(int value) 
	    {
	        this.value = value;
	    }

	    public int getValue() 
	    {
	        return value;
	    }
	}
	
	Suite suite;
	Rank rank;
	
	public Card(Suite suite, Rank rank)
	{
		this.suite = suite;
		this.rank = rank;
		
		//Rank rank1 = Rank.values()[2];
	}
	
	//copy constructor
	public Card(Card card)
	{
		this.suite = card.getSuite();
		this.rank = card.getRank();
	}
	
	public Suite getSuite() 
	{
		return suite;
	}

	public Rank getRank() 
	{
		return rank;
	}
	
	public String toString()
	{
		return rank.toString() + " of " + suite.toString();
	}
	
	public boolean equals(Card card)
	{
		return (this.suite == card.getSuite() && this.rank == card.getRank());
	}
}
