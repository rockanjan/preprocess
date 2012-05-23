package featurenew;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Generates all features for the dependency
 * NOTE: other features like hmm+word+hmm for each word (for comma related), etc will be added from python script 
 */
public class FeatureNew extends FeatureGeneratorBase {
	Logger logger = Logger.getLogger(FeatureNew.class.getName());

	public FeatureNew(String inputFilename, String outputFilename) {
		super(inputFilename, outputFilename);
	}

	@Override
	public void run() {
		try {
			load();
		} catch (IOException ioe) {
			System.err.println("Error in processing input file");
			ioe.printStackTrace();
			System.exit(1);
		}
		for (Sentence s : data) {
			int verbWordIndex = s.getVerbWordIndex();
			for (DataRow dr : s) {
				// processing word level features like numeric, capitalization,
				// periods, hyphens, colons etc
				String wordFeatures[] = getWordFeatures(dr);
				String alphaNum = wordFeatures[0]; // alpha or numeric
				String fourDigitNum = wordFeatures[1]; // Y or N
				String hasPeriods = wordFeatures[2];
				String hasHyphens = wordFeatures[3];
				String hasCapitalized = wordFeatures[4];
				String hasSlashes = wordFeatures[5]; // like a\/k\/a
				String hasColons = wordFeatures[6];
				String hasDollars = wordFeatures[7];
				String suffix1 = wordFeatures[8];
				String suffix2 = wordFeatures[9];
				String suffix3 = wordFeatures[10];

				// more features from fei
				String beforeAfterPredicate = getBeforeOrAfterPredicate(dr,
						verbWordIndex);
				String hmmBeforePredicate = getHmmBeforePredicate(s, dr,
						verbWordIndex);
				String hmmAfterPredicate = getHmmAfterPredicate(s, dr,
						verbWordIndex);
				String hmmPathToPredicate = getHmmPathToPredicate(s, dr,
						verbWordIndex);
				String wordPathToPredicate = getWordPath(s, dr, verbWordIndex);
				//String stemmedWordPathToPredicate = getStemmedWordPath(s, dr,verbWordIndex);
				String distance = getDistance(dr, verbWordIndex);
				// for now discretize the distance, later have to use it as
				// continuous feature
				if (!distance.equals(FeatureGeneratorBase.NO_VERB)) {
					int intDistance = Integer.parseInt(distance);
					if (intDistance == 0 || intDistance == 1
							|| intDistance == 2) {
						// do nothing
					} else if (intDistance > 2 && intDistance <= 5) {
						distance = "3-5";
					} else if (intDistance >= 6 && intDistance <= 9) {
						distance = "6-9";
					} else if (intDistance >= 10 && intDistance <= 15) {
						distance = "10-15";
					} else if (intDistance >= 16) {
						distance = ">=16";
					}
				}
				// more features: words/hmms before verb, which might capture
				// info about passive verbs
				String[] combo = getCombinationsBeforePredicate(s, dr,
						verbWordIndex);
				String wordBeforePredicate = combo[0];
				String twoHmms = combo[1];
				String oneHmmOneWord = combo[2];
				String oneWordOneHmm = combo[3];
				String twoWords = combo[4];
				String threeHmms = combo[5];
				String twoHmmsWord = combo[6];
				String oneHmmsTwoWord = combo[7];
				String oneHmmOneWordOneHmm = combo[8];

				// verb specific features
				String[] verbSpecific = getVerbSpecific(s, verbWordIndex);
				String verbHmm = verbSpecific[0];
				String verbWord = verbSpecific[1];
				String verbStemmedWord = verbSpecific[2];
				String verbOneSuffix = verbSpecific[3];
				String verbTwoSuffix = verbSpecific[4];

				pw.println(dr.getRowWithAppendedFeatureNew(alphaNum,
						fourDigitNum, hasPeriods, hasHyphens, hasCapitalized,
						hasSlashes,
						hasColons,
						hasDollars,
						suffix1,
						suffix2,
						suffix3,
						beforeAfterPredicate,// start of fei's features
						hmmBeforePredicate, hmmAfterPredicate,
						hmmPathToPredicate, wordPathToPredicate,
						//stemmedWordPathToPredicate, 
						distance,

						wordBeforePredicate, twoHmms, oneHmmOneWord,
						oneWordOneHmm, twoWords, threeHmms, twoHmmsWord,
						oneHmmsTwoWord, oneHmmOneWordOneHmm,

						verbHmm, verbWord, verbStemmedWord, verbOneSuffix,
						verbTwoSuffix));

			}
			pw.println();
		}
		close();
	}

	private String[] getVerbSpecific(Sentence s, int verbIndex) {
		// verb hmm, verb word, stemmed verb word, 1-suffix, 2-suffix
		String hmm = FeatureGeneratorBase.NO_VERB;
		String verbWord = FeatureGeneratorBase.NO_VERB;
		String stemmedVerbWord = FeatureGeneratorBase.NO_VERB;
		String oneSuffix = FeatureGeneratorBase.NO_VERB;
		String twoSuffix = FeatureGeneratorBase.NO_VERB;
		if (verbIndex != -1) {
			DataRow dr = s.get(verbIndex);
			hmm = "" + dr.getHmmState();
			verbWord = dr.getWord();
			stemmedVerbWord = Stemmer.stemWord(verbWord);
			oneSuffix = verbWord.substring(verbWord.length() - 1,
					verbWord.length());
			if (verbWord.length() >= 2) {
				twoSuffix = verbWord.substring(verbWord.length() - 2,
						verbWord.length());
			} else {
				twoSuffix = FeatureGeneratorBase.NO_SUFFIX;
			}
		}
		String[] returnValue = { hmm, verbWord, stemmedVerbWord, oneSuffix,
				twoSuffix };
		return returnValue;
	}

	// word/hmm combinations before predicate
	private String[] getCombinationsBeforePredicate(Sentence s, DataRow dr,
			int verbIndex) {
		String hmmPredicate = FeatureGeneratorBase.NO_HMM;
		String hmmOneBefore = FeatureGeneratorBase.NO_HMM1;
		String hmmTwoBefore = FeatureGeneratorBase.NO_HMM2;

		String wordPredicate = FeatureGeneratorBase.NO_WORD;
		// include this only as feature as well
		String wordOneBefore = FeatureGeneratorBase.NO_WORD1;
		String wordTwoBefore = FeatureGeneratorBase.NO_WORD2;

		if (verbIndex >= 0) {
			hmmPredicate = "" + s.get(verbIndex).getHmmState();
			//wordPredicate = s.get(verbIndex).getWord();
			wordPredicate = VocabIndexReader.getIndex(s.get(verbIndex).getWord()) + "";
		}

		if (verbIndex >= 1) {
			hmmOneBefore = "" + s.get(verbIndex - 1).getHmmState();
			//wordOneBefore = s.get(verbIndex - 1).getWord();
			wordOneBefore = VocabIndexReader.getIndex(s.get(verbIndex - 1).getWord()) + "";
		}
		if (verbIndex >= 2) {
			hmmTwoBefore = "" + s.get(verbIndex - 2).getHmmState();
			//wordTwoBefore = "" + s.get(verbIndex - 2).getWord();
			wordTwoBefore = VocabIndexReader.getIndex(s.get(verbIndex - 2).getWord()) + "";
		}

		// two hmms
		String twoHmms = hmmOneBefore + FeatureGeneratorBase.JOIN
				+ hmmPredicate;
		// one hmm and word
		String oneHmmOneWord = hmmOneBefore + FeatureGeneratorBase.JOIN
				+ wordPredicate;
		// one word and hmm
		String oneWordOneHmm = wordOneBefore + FeatureGeneratorBase.JOIN
				+ hmmPredicate;
		// both words
		String twoWords = wordOneBefore + FeatureGeneratorBase.JOIN
				+ wordPredicate;

		// three hmms
		String threeHmms = hmmTwoBefore + FeatureGeneratorBase.JOIN
				+ hmmOneBefore + FeatureGeneratorBase.JOIN + hmmPredicate;
		// two hmms and word
		String twoHmmsWord = hmmTwoBefore + FeatureGeneratorBase.JOIN
				+ hmmOneBefore + FeatureGeneratorBase.JOIN + wordPredicate;
		// one hmm and two words
		String oneHmmsTwoWord = hmmTwoBefore + FeatureGeneratorBase.JOIN
				+ wordOneBefore + FeatureGeneratorBase.JOIN + wordPredicate;
		// one hmm one word one hmm
		String oneHmmOneWordOneHmm = hmmTwoBefore + FeatureGeneratorBase.JOIN
				+ wordOneBefore + FeatureGeneratorBase.JOIN + hmmPredicate;

		String[] returnString = { wordOneBefore, twoHmms, oneHmmOneWord,
				oneWordOneHmm, twoWords, threeHmms, twoHmmsWord,
				oneHmmsTwoWord, oneHmmOneWordOneHmm };
		return returnString;
	}

	private String[] getWordFeatures(DataRow dr) {
		String word = dr.getWord();
		int wordIndex = dr.getIndex(); // to check capitalization, if it's first
										// word or not
		String suffix1 = FeatureGeneratorBase.NO_SUFFIX, suffix2 = FeatureGeneratorBase.NO_SUFFIX, suffix3 = FeatureGeneratorBase.NO_SUFFIX;
		String alphaNum; // alpha or numeric
		String fourDigitNum; // Y or N
		String hasPeriods;
		String hasHyphens;
		String hasCapitalized;
		String hasSlashes; // like a\/k\/a
		String hasColons;
		String hasDollars;
		suffix1 = word.substring(word.length() - 1, word.length());
		if (word.length() > 1) {
			suffix2 = word.substring(word.length() - 2, word.length());
		}
		if (word.length() > 2) {
			suffix3 = word.substring(word.length() - 3, word.length());
		}
		Pattern p = Pattern.compile("^-{0,1}[0-9]+\\.*[0-9]*"); // eg -9, 100,
																// 100.001 etc
		Pattern p2 = Pattern.compile("^-{0,1}[0-9]*\\.*[0-9]+"); // eg. -.5, .5
		Pattern p3 = Pattern.compile("^-{0,1}[0-9]{1,3}[,[0-9]{3}]*\\.*[0-9]*"); // matches
																					// 100,000
		Pattern p4 = Pattern.compile("[0-9]+\\\\/[0-9]+"); // four \ needed,
															// java converts it
															// to \\
		Pattern p5 = Pattern.compile("[0-9]+:[0-9]+"); // ratios and time
		Pattern p6 = Pattern.compile("([0-9]+-)+[0-9]+"); // 1-2-3, 1-2-3-4 etc
		Matcher m = p.matcher(word);
		Matcher m2 = p2.matcher(word);
		Matcher m3 = p3.matcher(word);
		Matcher m4 = p4.matcher(word);
		Matcher m5 = p5.matcher(word);
		Matcher m6 = p6.matcher(word);
		// alpha or num
		if (m.matches() || m2.matches() || m3.matches() || m4.matches()
				|| m5.matches() || m6.matches()) {
			// System.out.println(word);
			alphaNum = FeatureGeneratorBase.NUM;
		} else {
			alphaNum = FeatureGeneratorBase.ALPHA;
		}
		// four digit
		Matcher fourDigitMatcher = Pattern.compile("[0-9]{4}").matcher(word);
		if (fourDigitMatcher.matches()) {
			if (wordIndex == 0) {

			}
			fourDigitNum = FeatureGeneratorBase.YES;
		} else {
			fourDigitNum = FeatureGeneratorBase.NO;
		}
		// period
		Matcher periodMatcher = Pattern.compile(".*\\..*").matcher(word);
		if (periodMatcher.matches())
			hasPeriods = FeatureGeneratorBase.YES;
		else
			hasPeriods = FeatureGeneratorBase.NO;

		// hyphen
		Matcher hyphenMatcher = Pattern.compile(".*-.*").matcher(word);
		if (hyphenMatcher.matches())
			hasHyphens = FeatureGeneratorBase.YES;
		else
			hasHyphens = FeatureGeneratorBase.NO;

		// capitalized
		Pattern capitalizedPattern = Pattern.compile(".*[A-Z]+.*");
		Matcher capitalized = capitalizedPattern.matcher(word);
		hasCapitalized = FeatureGeneratorBase.NO;
		if (capitalized.matches()) {
			if (wordIndex == 0) {// first word
				// remove the first char, and if it still matches, then add to
				// hasCapitalized
				String firstCharRemoved = word.substring(1);
				Matcher cap = Pattern.compile(".*[A-Z]+.*").matcher(
						firstCharRemoved);
				if (cap.matches()) {
					hasCapitalized = FeatureGeneratorBase.YES;
				}
			} else { // it's not first word and is capitalized
				hasCapitalized = FeatureGeneratorBase.YES;
			}
		}

		// has slashes (backslashes)
		Matcher slashMatcher = Pattern.compile(".*/.*").matcher(word);
		if (slashMatcher.matches())
			hasSlashes = FeatureGeneratorBase.YES;
		else
			hasSlashes = FeatureGeneratorBase.NO;

		// colons
		Matcher colonMatcher = Pattern.compile(".*:.*").matcher(word);
		if (colonMatcher.matches())
			hasColons = FeatureGeneratorBase.YES;
		else
			hasColons = FeatureGeneratorBase.NO;

		Matcher dollarMatcher = Pattern.compile(".*\\$.*").matcher(word);
		if (dollarMatcher.matches())
			hasDollars = FeatureGeneratorBase.YES;
		else
			hasDollars = FeatureGeneratorBase.NO;
		String wordFeatures[] = { alphaNum, fourDigitNum, hasPeriods,
				hasHyphens, hasCapitalized, hasSlashes, hasColons, hasDollars,
				suffix1, suffix2, suffix3 };
		return wordFeatures;
	}

	// Fei feature
	private String getBeforeOrAfterPredicate(DataRow dr, int verbIndex) {
		String returnValue = FeatureGeneratorBase.ERROR;
		if (verbIndex == -1) {
			return FeatureGeneratorBase.NO_VERB;
		}
		if (dr.getPredicateLabel().equals("V")) {
			return "V";
		}
		if (dr.getIndex() < verbIndex) {
			return FeatureGeneratorBase.BEFORE;
		}
		if (dr.getIndex() > verbIndex) {
			return FeatureGeneratorBase.AFTER;
		}
		return returnValue;
	}

	private String getDistance(DataRow dr, int verbIndex) {
		String returnValue = FeatureGeneratorBase.ERROR;
		if (verbIndex == -1) {
			return FeatureGeneratorBase.NO_VERB;
		}
		if (dr.getPredicateLabel().equals("V")) {
			return "0";
		}
		returnValue = Math.abs(dr.getIndex() - verbIndex) + "";
		return returnValue;
	}

	private String getHmmBeforePredicate(Sentence s, DataRow dr, int verbIndex) {
		String returnValue = FeatureGeneratorBase.ERROR;
		if (verbIndex == -1) {
			return FeatureGeneratorBase.NO_VERB;
		}
		if (verbIndex == 0) { // if verb is itself the first word
			return FeatureGeneratorBase.OOB;
		}
		returnValue = "" + s.get(verbIndex - 1).getHmmState();
		return returnValue;
	}

	private String getHmmAfterPredicate(Sentence s, DataRow dr, int verbIndex) {
		String returnValue = FeatureGeneratorBase.ERROR;
		if (verbIndex == -1) {
			return FeatureGeneratorBase.NO_VERB;
		}
		if (verbIndex == s.size() - 1) {
			return FeatureGeneratorBase.OOB;
		}
		returnValue = "" + s.get(verbIndex + 1).getHmmState();
		return returnValue;
	}

	private String getHmmPathToPredicate(Sentence s, DataRow dr, int verbIndex) {
		String returnValue = "";
		if (verbIndex == -1) {
			return FeatureGeneratorBase.NO_VERB;
		}
		int currentIndex = dr.getIndex();
		if (currentIndex < verbIndex) {
			for (int i = currentIndex + 1; i < verbIndex; i++) {
				returnValue += s.get(i).getHmmState();
				if (i != verbIndex - 1) {
					returnValue += FeatureGeneratorBase.JOIN;
				}
			}
		} else {
			for (int i = verbIndex + 1; i < currentIndex; i++) {
				returnValue += s.get(i).getHmmState();
				if (i != currentIndex - 1) {
					returnValue += FeatureGeneratorBase.JOIN;
				}
			}
		}
		if (returnValue.equals("")) {
			returnValue = FeatureGeneratorBase.NO_PATH;
		}
		if (dr.getIndex() == verbIndex) {
			returnValue = FeatureGeneratorBase.VERB;
		}
		return returnValue;
	}

	private String getWordPath(Sentence s, DataRow dr, int verbIndex) {
		String returnValue = "";
		if (verbIndex == -1) {
			return FeatureGeneratorBase.NO_VERB;
		}
		int currentIndex = dr.getIndex();
		if (currentIndex < verbIndex) {
			for (int i = currentIndex + 1; i < verbIndex; i++) {
				//returnValue += s.get(i).getWord();
				returnValue += VocabIndexReader.getIndex(s.get(i).getWord());
				if (i != verbIndex - 1) {
					returnValue += FeatureGeneratorBase.JOIN;
				}
			}
		} else {
			for (int i = verbIndex + 1; i < currentIndex; i++) {
				//returnValue += s.get(i).getWord();
				returnValue += VocabIndexReader.getIndex(s.get(i).getWord());
				if (i != currentIndex - 1) {
					returnValue += FeatureGeneratorBase.JOIN;
				}
			}
		}
		if (returnValue.equals("")) {
			returnValue = FeatureGeneratorBase.NO_PATH;
		}
		if (dr.getIndex() == verbIndex) {
			returnValue = FeatureGeneratorBase.VERB;
		}
		return returnValue;
	}

	private String getStemmedWordPath(Sentence s, DataRow dr, int verbIndex) {
		String returnValue = "";
		if (verbIndex == -1) {
			return FeatureGeneratorBase.NO_VERB;
		}
		int currentIndex = dr.getIndex();
		if (currentIndex < verbIndex) {
			for (int i = currentIndex + 1; i < verbIndex; i++) {
				returnValue += Stemmer.stemWord(s.get(i).getWord());
				if (i != verbIndex - 1) {
					returnValue += FeatureGeneratorBase.JOIN;
				}
			}
		} else {
			for (int i = verbIndex + 1; i < currentIndex; i++) {
				returnValue += Stemmer.stemWord(s.get(i).getWord());
				if (i != currentIndex - 1) {
					returnValue += FeatureGeneratorBase.JOIN;
				}
			}
		}
		if (returnValue.equals("")) {
			returnValue = FeatureGeneratorBase.NO_PATH;
		}
		if (dr.getIndex() == verbIndex) {
			returnValue = FeatureGeneratorBase.VERB;
		}
		return returnValue;
	}

}
