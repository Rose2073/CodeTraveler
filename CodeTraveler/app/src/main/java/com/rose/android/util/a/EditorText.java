package com.rose.android.util.a;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.Editable;
import java.util.ArrayList;
import android.text.Spannable;

//Created By Rose on 2019/7/26

//Helper class of CodeEditor
//It is only a container of WatcherTransformer,
//LineManager and UndoManager.
//And it is a information provider of nested batch edit
public class EditorText extends SpannableStringBuilder{
	
	private WatcherTransformer transformer;
	private UndoManger stack;
	private LineManager lines;
	private int nestedBatchEdit;
	
	public EditorText(){
		this("");
	}
	
	public EditorText(CharSequence src){
		super(src);
		transformer = new WatcherTransformer(this);
		//register LineManager and UndoManager as listeners
		addWatcher(stack = new UndoManger());
		addWatcher(lines = new LineManager(this));
		//Send insert action to LineManager
		//so that it can calculate the index of last line
		//Or it might spend much time on finding index
		if(this.length() != 0)
			lines.onInsert(this,0,this);
		nestedBatchEdit = 0;
		transformer.addE(lines);
	}
	
	//Batch Edit part
	
	public void addWatcher(TextWatcherR r){
		transformer.add(r);
	}
	
	public void removeWatcher(TextWatcherR r){
		transformer.remove(r);
	}
	
	public void beginBatchEdit(){
		nestedBatchEdit++;
		stack.setBatchEdit(nestedBatchEdit > 0);
	}
	
	public void endBatchEdit(){
		nestedBatchEdit--;
		if(nestedBatchEdit < 0){
			nestedBatchEdit = 0;
		}
		stack.setBatchEdit(nestedBatchEdit > 0);
	}
	
	public void resetBatchEdit(){
		nestedBatchEdit = 0;
		stack.setBatchEdit(false);
	}
	
	//UndoManager part
	
	public boolean canUndo(){
		return stack.canUndo();
	}
	
	public boolean canRedo(){
		return stack.canRedo();
	}
	
	public void redo(){
		stack.redo(this);
	}
	
	public void undo(){
		stack.undo(this);
	}
	
	public UndoManger getUndoManager(){
		return stack;
	}
	
	//LineManager part
	
	public int getLineStart(int line){
		return lines.getLineStart(line);
	}
	
	public int getLineEnd(int line){
		return lines.getLineEnd(line);
	}
	
	public int getLineCount(){
		return lines.getLineCount();
	}
	
	public int getLineByIndex(int charIndex){
		return lines.getLineByIndex(charIndex);
	}
	
}
