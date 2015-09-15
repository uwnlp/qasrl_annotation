package edu.uw.annotation;

import java.util.Arrays;

/* Encode the question into information we want.
 *  
 */
public class  QuestionEncoder {
	
	public static void normalize(String[] question) {
		String ph2 = question[QASlots.PH2SlotId],
			   pp  = question[QASlots.PPSlotId],
			   ph3 = question[QASlots.PH3SlotId];
		
		if (QASlotPlaceHolders.valueSet.contains(ph3) && !ph3.isEmpty() &&
			ph2.isEmpty() && pp.isEmpty()) {
			question[QASlots.PH2SlotId] = question[QASlots.PH3SlotId];
			question[QASlots.PH3SlotId] = "";
		}
	}
	
	private static String getLabel(String qlabel, String pp, String qval) {
		if (qval == null || qval.isEmpty()) {
			return "";
		}
		String label = (pp.isEmpty() ? qlabel : qlabel + "_" + pp);
		return label + "=" + qval;
	}
	
	public static String[] getLabels(String[] question) {	
		assert (question.length == 7);
		String wh  = question[0],
			   aux = question[1],
			   ph1 = question[2],
			   trg = question[3],
			   ph2 = question[4],
			   pp  = question[5],
			   ph3 = question[6];
		boolean nullPh1 = ph1.isEmpty(),
				nullPh2 = ph2.isEmpty(),
				nullPh3 = ph3.isEmpty(),
				nullPP = pp.isEmpty();
		boolean verbalPh3 = (ph3.equals("do") || ph3.equals("doing") ||
							 ph3.equals("be") || ph3.equals("being"));
		boolean haveAux = (aux.startsWith("have") || aux.startsWith("had") ||
	   					   aux.startsWith("has"));
		boolean standAlonePP = (!nullPP && nullPh3);
		boolean passiveVoice = isPassiveVoice(aux, trg),
				activeVoice = !passiveVoice;
		String whSlot = wh.equals("who") ? "someone" : "something",
			   whSlot2 = verbalPh3 ? ph3 + " something" : whSlot;
		
		// Template format [wh, ph1, ph2, ph3, voice]
		String[] template = new String[5];
		Arrays.fill(template, "");

		if (isWhoWhat(wh)) {
			/*** Special case Who had something done ***/
			if (activeVoice && wh.equals("who") && haveAux &&
				ph1.equals("something")) {
				template[0] = getLabel("W0", "", whSlot);
				template[1] = getLabel("W1", "", ph1);
			} else if (activeVoice && nullPh1) {
				template[0] = getLabel("W0", "", whSlot);
				template[2] = getLabel("W1", "", ph2);
			} else if (activeVoice && !nullPh1 && nullPh2 && !standAlonePP) {
				template[0] = getLabel("W1", "", whSlot);
				template[1] = getLabel("W0", "", ph1);
			} else if (activeVoice && !nullPh1) {
				template[0] = getLabel("W2", pp, whSlot2);
				template[1] = getLabel("W0", "", ph1);
				template[2] = getLabel("W1", "", ph2);
			} else if (passiveVoice && nullPh1 && !standAlonePP) {
				template[0] = getLabel("W1", "", whSlot);
				template[2] = getLabel("W2", "", ph2);
			} else if (passiveVoice) {
				template[0] = getLabel("W2", pp, whSlot2);
				template[1] = getLabel("W1", "", ph1);
			}
			if (ph3.equals("somewhere")) {
				template[3] = getLabel("WHERE", pp, ".");
			} else if (passiveVoice && pp.equals("by") &&
					(ph3.equals("someone") || ph3.equals("something"))) {
				template[3] = getLabel("W0", "", ph3);
			} else if (!verbalPh3){
				template[3] = getLabel("W2", pp, ph3);
			}
		} else {
			template[0] = getLabel(wh.toUpperCase(), "", ".");
			if (!nullPP && nullPh3) {
				template[0] = getLabel(wh.toUpperCase(), pp, ".");
			}
			if (activeVoice) {
				template[1] = getLabel("W0", "", ph1);
				template[2] = getLabel("W1", "", ph2);
				template[3] = getLabel("W2", pp, ph3);
			} else {
				template[1] = getLabel("W1", "", ph1);
				template[2] = getLabel("W2", "", ph2);
				template[3] = getLabel("W2", pp, ph3);
			}
		}
		template[4] = (activeVoice ? "active" : "passive");
		return template;
	}
	
	public static boolean isPassiveVoice(String[] question) {
		return isPassiveVoice(question[1].toLowerCase(),
				question[3].toLowerCase());
	}
	
	private static boolean isPassiveVoice(String aux, String trg) {
		if (!trg.endsWith("ing") && (
				trg.startsWith("have been") ||
				trg.startsWith("be ") ||
				trg.startsWith("been ") ||
				trg.startsWith("being "))) {
			// e.g. have been broken, been broken, be broken, being broken
			return true;
		}
		if (!trg.endsWith("ing") && (
				aux.startsWith("is") ||
				aux.startsWith("are") ||
				aux.startsWith("was") ||
				aux.startsWith("were"))) {
			return true;
		}
		return false;
	}
	
	private static boolean isWhoWhat(String wh) {
		// Assume people are careless about using whom.
		return wh.equalsIgnoreCase("who") || wh.equalsIgnoreCase("what") ||
			   wh.equalsIgnoreCase("whom");		
	}
}
