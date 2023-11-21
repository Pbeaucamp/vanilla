package bpm.test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * another class unexplained go To hell mr coder!!!
 * 
 */
public class Nou {

	// 24572
	public static void main(String[] args) {
		System.out.println("te\"te");
		System.out.println("te\"te".replace("\"", ""));
	}

	public static BigInteger listPosition(String word) {

		List<Character> letters = new ArrayList<Character>();
		for (char c : word.toCharArray()) {
			// if (letters.indexOf(c) == -1) {
			letters.add(c);
			// }
		}
		Collections.sort(letters);

		int position = 0;
		for (int i = 0; i < word.length(); i++) {
			char letter = word.charAt(i);
			position = position + findPositionForLetter(letters, letter, i);

			int indexLetter = letters.indexOf(letter);
			letters.remove(indexLetter);
		}

		position++;

		position = position - findDoublonBeforeWord();
		return BigInteger.valueOf(position);
	}

	private static int findDoublonBeforeWord() {
		// TODO Auto-generated method stub
		return 0;
	}

	private static int findPositionForLetter(List<Character> letters, char letter, int positionInWord) {
		int wordLength = letters.size();
		int letterPosition = findLetterPosition(letters, letter);

		return letterPosition * factorial(wordLength - 1);
	}

	private static int findLetterPosition(List<Character> letters, char letter) {
		for (int i = 0; i < letters.size(); i++) {
			if (letters.get(i) == letter) {
				return i;
			}
		}
		return 0;
	}

	public static int factorial(int n) {
		int fact = 1; // this will be the result
		for (int i = 1; i <= n; i++) {
			fact *= i;
		}
		return fact;
	}

	public static void createList(List<Character> letters) {
		StringBuffer buf = new StringBuffer();
		for (Character letter : letters) {
			
			buf.append(letter);
		}
	}
}
