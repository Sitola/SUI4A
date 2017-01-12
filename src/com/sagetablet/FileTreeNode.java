package com.sagetablet;

import java.util.LinkedList;

public class FileTreeNode {
	FileTreeNode parent = null;
	
	String name = "";
	String path = "";
	LinkedList<FileTreeNode> successors = null; 
    int isFile = 0;

    
    
    public FileTreeNode(String name, String path, FileTreeNode parent, LinkedList<FileTreeNode> successors, int isFile){
    	this.name = name;
    	this.parent= parent;
    	this.path = path;
    	this.successors = (successors == null) ? new LinkedList<FileTreeNode>() : successors;
    	this.isFile = isFile;
    	
    }



	public FileTreeNode getParent() {
		return parent;
	}



	public void setParent(FileTreeNode parent) {
		this.parent = parent;
	}



	public String getPath() {
		return path;
	}



	public void setPath(String path) {
		this.path = path;
	}



	public LinkedList<FileTreeNode> getSuccessors() {
		return successors;
	}



	public void setSuccessors(LinkedList<FileTreeNode> successors) {
		this.successors = successors;
	}

	public void addSuccessor(FileTreeNode successor){
		this.successors.add(successor);
	}
	

	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public int isFile() {
		return isFile;
	}



	public void setFile(int isFile) {
		this.isFile = isFile;
	}
  
    
	
	
}
