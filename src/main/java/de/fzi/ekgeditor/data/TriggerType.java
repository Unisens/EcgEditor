package de.fzi.ekgeditor.data;

public class TriggerType{
	private String notation;
	private String comment;
	
	public TriggerType(String notation, String comment){
		this.notation = notation;
		this.comment = comment;
	}
	public String getNotation() {
		return notation;
	}
	public void setNotation(String notation) {
		this.notation = notation;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
}
