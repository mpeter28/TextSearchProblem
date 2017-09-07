package search;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

public class TextSearcher {

	private List<String> tokens;
	private Map<String, List<Integer>> wordOccurrences;

	/**
	 * Initializes the text searcher with the contents of a text file.
	 * The current implementation just reads the contents into a string 
	 * and passes them to #init().  You may modify this implementation if you need to.
	 * 
	 * @param f Input file.
	 * @throws IOException
	 */
	public TextSearcher(File f) throws IOException {
		FileReader r = new FileReader(f);
		StringWriter w = new StringWriter();
		char[] buf = new char[4096];
		int readCount;
		
		while ((readCount = r.read(buf)) > 0) {
			w.write(buf,0,readCount);
		}
		
		init(w.toString());
	}
	
	/**
	 *  Constructs a list of all tokens, which should alternate word, not-word, word... or not-word, word, not-word...;
	 *  further, constructs a look up table of every word amongst the tokens and all indices where seen
	 */
	protected void init(String fileContents) {
		tokens = new ArrayList<>();
		wordOccurrences = new HashMap<>();

		TextTokenizer tokenizer = new TextTokenizer(fileContents, "[0-9a-zA-Z']+");
		int tokenIndex = -1;
		while (tokenizer.hasNext()) {
			String nextToken = tokenizer.next();
			tokens.add(nextToken);
			tokenIndex++;

			if (tokenizer.isWord(nextToken)) {
				String tokenWord = nextToken.toLowerCase(Locale.US);
				if (wordOccurrences.containsKey(tokenWord)) {
					wordOccurrences.get(tokenWord).add(tokenIndex);
				} else {
					List<Integer> tokenWordOccurrences = new LinkedList<>();
					tokenWordOccurrences.add(tokenIndex);
					wordOccurrences.put(tokenWord, tokenWordOccurrences);
				}
			}
		}
	}
	
	/**
	 * 
	 * @param queryWord The word to search for in the file contents.
	 * @param contextWords The number of words of context to provide on
	 *                     each side of the query word.
	 * @return One context string for each time the query word appears in the file.
	 */
	public String[] search(String queryWord, int contextWords) {
		queryWord = queryWord.toLowerCase(Locale.US);

		if (wordOccurrences.containsKey(queryWord)) {
			List<Integer> queryWordOccurrences = wordOccurrences.get(queryWord);
			String[] results = new String[queryWordOccurrences.size()];
			int neededTokens = 2 * contextWords;
			int resultNumber = 0;

			for (int occurrence : queryWordOccurrences) {
				StringBuilder documentContext = new StringBuilder();
				for (int tokenIndex = occurrence - neededTokens; tokenIndex <= occurrence + neededTokens; tokenIndex++) {
					if (tokenIndex >= 0 && tokenIndex < tokens.size())
						documentContext.append(tokens.get(tokenIndex));
				}
				results[resultNumber] = documentContext.toString();
				resultNumber++;
			}

			return results;
		} else {
			return new String[0];
		}
	}
}