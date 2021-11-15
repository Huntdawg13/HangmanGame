import java.util.*;

/**
 * Class used to manage how the Hangman game runs. This
 * class controls the storing of data, the words being used, the
 * guesses left, all the characters that have been guessed, the 
 * current pattern during the game's rounds, the recording of each round
 * and its new data, and the method to find the family pattern of a specifc word.
 * 
 * @author Hunter J. McClure
 * @version (April 29 2019)
 */
public class HangmanManager
{
   private Set<String> WordsUsed = new TreeSet<String>();
   private Set<String> tempSet = new TreeSet<String>();
   private SortedSet<Character> CharsGuessed = new TreeSet<>();
   private Map<String, Integer> FamilyPatterns = new TreeMap<>();
   private int wordLength;
   private int numGuesses;
   
   private String tempWord;
   private int StartingCount = 1;
   private int GuessOccurence;
   private String MaxPattern = "";
   private String CurrentPattern = "";
   
   /**
    * Creates a constructor for the Hangman game that
    * stores the starting dictionary, the length of the words being used, 
    * and stores the max starting amount of guesses. But also, I made the
    * constructor get rid of all the words in the set that are bigger or smaller
    * than the length of the words that was chosen. 
    * 
    * @param Dictionary It is a list that contains the starting amount of words being used
    * @param Length It is a int that states what length the words will be during the game
    * @param Max It is a int that states the total starting amount of guesses at the beginning of the game
    * @return nothing
    * @throws IllegalArgumentException When length of word is less than 1 or when starting guesses is less than 0
    */
   public HangmanManager(List<String> Dictionary, int Length, int Max)
   {
      WordsUsed.addAll(Dictionary);
      if(Length < 1 || Max < 0)
      {
         throw new IllegalArgumentException("Word length cannot be less than 1");
      }
      else 
      {
         wordLength = Length;
         numGuesses = Max;
      }
      
      //trims down the words being used by getting rid of 
      //all the words that don't meet the length parameter
      for(String word : WordsUsed)
      {
         if(word.length() == Length)
            tempSet.add(word);
      }
      
      WordsUsed.clear();
      WordsUsed.addAll(tempSet);
      tempSet.clear();
      
   }
   
   /**
    * Sends to the main program/client, the current 
    * words being used/considered during the game 
    * 
    * @param nothing
    * @return the set of the words being used/considered
    */
   public Set<String> words()
   {
      return WordsUsed;
   }
   
   /**
    * Sends to the main program/client, the total amount of 
    * guesses left that the user can get wrong before they lose  
    * 
    * @param nothing
    * @return A int that represents the amount of guesses left that the person can get wrong
    */
   public int guessesLeft()
   {
      return numGuesses;
   }
   
   /**
    * Sends to the main program/client, the currently 
    * updated set of characters that the user has guesses 
    * 
    * @param nothing
    * @return the set of the characters/guesses that have been made
    */
   public SortedSet<Character> guesses()
   {
      return CharsGuessed;
   }
   
   /**
    * Finds the current pattern that is being displayed during the Hangman game,
    * but also creates the starting pattern at the beginning of the game 
    * 
    * @param nothing
    * @return the current pattern/string that is being used/displayed in the Hangman game
    * @throws IllegalStateException When the set of words being considered is empty
    */
   public String pattern()
   {
      char[] CP_Chars = new char[wordLength];
      //creates starting pattern at beginning of game
      if(MaxPattern.equals("") && CurrentPattern.equals(""))
      {
         char[] MaxPatternChars = new char[wordLength];
         for(int x = 0; x < wordLength; x++)
         {    
             MaxPatternChars[x] = '-';
         }
         MaxPattern = String.valueOf(MaxPatternChars);
      
         for(int y = 0; y < wordLength; y++)
         {    
             CP_Chars[y] = '-';
         }
         CurrentPattern = String.valueOf(CP_Chars);
      }
      
      if(WordsUsed.isEmpty())
      {
         throw new IllegalStateException("No words being used");
      }
      else
      {
         String Current = "";
         
         char[] CP = new char[wordLength*2];
         for(int c = 1; c < wordLength*2; c=c+2)
         {    
             CP_Chars[c] = ' ';
         }
         Current = String.valueOf(CP);
         
         return Current;     
      }      
   }
   
   /**
    * This method records the user's next guess and updates the set 
    * of words that the computer will be using from here on out. Also 
    * it will update the number of guesses that are left and will 
    * return the number of times the user's guess appeared in the word. 
    * 
    * @param Guess The char/most recent guess that the user made
    * @return the int that represents the number of times the guess appeared in the word
    * @throws IllegalStateException When number of guesses drops below 1
    * @throws IllegalStateException When the set of words being considered is empty
    * @throws IllegalArgumentException When there are words being considered in the set but also when one the user's same guess is entered again
    */
   public int record(char Guess)
   {
      if(numGuesses < 1)
      {
         throw new IllegalStateException("No guesses left");
      }
      else if(WordsUsed.isEmpty())
      {
         throw new IllegalStateException("No words left");
      }
      else if(!WordsUsed.isEmpty() && CharsGuessed.contains(Guess))
      {
         throw new IllegalArgumentException("Already guessed that character");
      }
      
      CharsGuessed.add(Guess); //add guess to set that holds all guesses made
      GuessOccurence = 0;
      
      //creates or adds to map of family patterns.
      //Uses patterns as keys and Integers as the data stored
      //which tell us how many times the pattern occurs in the set of considered words
      for(String word : WordsUsed)
      {
         tempWord = getPattern(word, Guess);
         
         if(FamilyPatterns.containsKey(tempWord))
         {
            FamilyPatterns.put(tempWord,FamilyPatterns.get(tempWord)+1);
         }
         else 
         {
            FamilyPatterns.put(tempWord,StartingCount);
         } 
      }
      
      //Finds the pattern with the most occurences in the set
      Integer MaxOccurences = 0;
      for (String z : FamilyPatterns.keySet())
      {
         if(FamilyPatterns.get(z) > MaxOccurences)
         {
            MaxOccurences = FamilyPatterns.get(z);
            MaxPattern = z;
         }
      }
      
      //reduces the number of gueses if incorrect
      if(MaxPattern.indexOf(Guess) < 0)
         numGuesses = numGuesses-1;
      
      //creates current pattern being used
      char[] CP_Chars = CurrentPattern.toCharArray();
      for(int h = 0; h < wordLength; h++)
      {
         if(MaxPattern.charAt(h) != ('-'))
         {       
            CP_Chars[h] = MaxPattern.charAt(h);
         }
      }
      CurrentPattern = String.valueOf(CP_Chars);
      
      //updates set of words being used/considered by computer
      for(String word : WordsUsed)
      {
         tempWord = getPattern(word, Guess);
         if(tempWord.equals(MaxPattern))
         {
            tempSet.add(word);
         }
      }
      WordsUsed.clear();
      WordsUsed.addAll(tempSet);
      tempSet.clear();
      FamilyPatterns.clear();
      
      //finds how many times the guess occurs in the word
      for (int i = 0; i < MaxPattern.length(); i++)
      {
         if (MaxPattern.charAt(i) == Guess)
         {
            GuessOccurence++;
         }
      }
      
      return GuessOccurence;

   }
   
   /**
    * Figures out the specific family pattern between 
    * a specific character and of a word that is sent through 
    * 
    * @param incWord A string that holds the word that is being transferred into a pattern
    * @param incChar A char that holds the character that is being referred to in the word and pattern
    * @return the pattern as a string of the word and char that were sent through
    */
   public String getPattern(String incWord, char incChar)
   {
      String theWord = incWord;
      char[] theWordChars = theWord.toCharArray();
      
      for(int i = 0; i < theWord.length(); i++)
      {           
         if(theWord.charAt(i) == incChar)
         {
            theWordChars[i] = incChar;
         }
         else
         {
            theWordChars[i] = '-';
         }
      }
      return String.valueOf(theWordChars);
   }
}