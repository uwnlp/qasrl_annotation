package edu.uw.qasrl_annotation.data;

public class TargetPredicate {
	public Sentence sentence;
	public int propId;
	// The span of the entire group of words. For example: "has considered",
	// instead of "considered". Not sure we will need this eventually, but it
	// might help with annotation.
	public int[] span;

	public TargetPredicate(Sentence sentence, int propId, int[] span) {
		this.sentence = sentence;
		this.propId = propId;
		this.span = new int[]{span[0], span[1]};
	}

	@Override
	public String toString() {
		// TODO
		return "";
	}

}
