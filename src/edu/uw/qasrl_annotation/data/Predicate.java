package edu.uw.qasrl_annotation.data;

public class Predicate {
	public Sentence sentence;
	public int propID;
	// The span of the entire group of words. For example: "has considered",
	// instead of "considered". Not sure we will need this eventually, but it
	// might help with annotation.
	public int[] span;
	
	public Predicate() {
		this.propID = -1;
		this.span = new int[2];
		this.span[0] = this.span[1];
	}
	
	public Predicate(int[] span) {
		this.span = new int[] {span[0], span[1]};
	}
	
	public void setPropositionSpan(int spanStart, int spanEnd) {
		this.span[0] = spanStart;
		this.span[1] = spanEnd;
	}
	
	public void setProposition(int propID) {
		this.propID = propID;
		this.span[0] = this.propID;
		this.span[1] = this.propID + 1;
	}
		
	@Override
	public Predicate clone() {
		Predicate newProp = new Predicate();
		newProp.propID = this.propID;
		newProp.span[0] = this.span[0];
		newProp.span[1] = this.span[1];
		return newProp;
	}
	
	@Override
	public String toString() {
		// TODO
		return "";
	}
	
}
