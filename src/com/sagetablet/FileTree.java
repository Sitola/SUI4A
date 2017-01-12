package com.sagetablet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class FileTree {
	FileTreeNode root = null;
	FileTreeNode activeNode = null;
	String currentPath;
	final int PATH = 0;
	final int DIRECTORIES = 1;
	final int FILES = 2;
	final int DIRNAME = 3;
	
	//Note: The directory has got 4 params if accessed by the traversal
	//0 is path
	//1 is dirs in a hashmap
	//2 is files in a tuple (Object[])
	//3 is dirName
	
	//FileTreeNode attribs are as follows
	//Name
	//Path
	//Parent
	//Successors
	//isFile
	public LinkedList<NameTypeTuple> getCurrentDirectoriesAndFiles(){
		
		LinkedList<NameTypeTuple> contentList = new LinkedList<NameTypeTuple>();
		//System.out.println("in here");
		for(FileTreeNode successor : activeNode.getSuccessors()){
			
			NameTypeTuple successorEntry = new NameTypeTuple(successor.getName(),successor.isFile());
			contentList.add(successorEntry);
		}
		
		//System.out.println("out here");
		return contentList;
	}
	
	public boolean isInRoot(){
		return activeNode.getName().equals("root");
	}
	
	public int clickedIsFile(int number){
		return activeNode.getSuccessors().get(number).isFile();
	}
	
	public FileTreeNode getActiveNode() {
		return activeNode;
	}

	public void setActiveNode(FileTreeNode activeNode) {
		this.activeNode = activeNode;
	}

	public String getCurrentPath() {
		return currentPath;
	}

	public void setCurrentPath(String currentPath) {
		this.currentPath = currentPath;
	}

	public void getLevelLower(int number){
		activeNode = activeNode.getSuccessors().get(number);
		
		//or(FileTreeNode successor : activeNode.getSuccessors()){
		//	if(successor.getName().equals(name)){
		//		activeNode = successor;
		//	}
		//}
	}
	
	public void getLevelHigher(){
		activeNode = activeNode.getParent();
	}
	
	public FileTree(HashMap<String, Object[]> rootDir){
		

		FileTreeNode root = new FileTreeNode("root", "/mnt/home/sage/images/", null, null, 0);
		
		for(String key : rootDir.keySet())
	    {
	    	addDirectoryToNode(root, rootDir.get(key), key);

	    }
		this.root = root;
		this.activeNode = root;
		
		
	}
	
	
	
	public void addDirectoryToNode(FileTreeNode parent, Object[] dirInfo, String name){
		
		
		//System.out.println("name is: " + name);
		FileTreeNode newDirNode = new FileTreeNode(name, dirInfo[PATH].toString(), parent, null, 0);
		HashMap<String, Object[]> currentDirectories = (HashMap<String, Object[]>)dirInfo[DIRECTORIES];
		
	    for(String key : currentDirectories.keySet())
	    {
	    	Object[] dir = currentDirectories.get(key);
	    	//System.out.println("dir name is: " + key);
	    	//for(int i=0;i<dir.length;i++){
	    	//	System.out.println("DirInfo " + i + " is: " + dir[i]);
	    	//}
	    	
	    	addDirectoryToNode(newDirNode, currentDirectories.get(key),key);
	    	
	    }
	    
		parent.addSuccessor(newDirNode);
		
		Object[] files = (Object[])dirInfo[FILES]	;
	    addFilesToNode(newDirNode, files);
		
	}
	
	public void addFilesToNode(FileTreeNode node, Object[] files){
		for(int i = 0; i< files.length; i++){
			String name = files[i].toString();
	
			String path =node.path;
			
			FileTreeNode newFileNode = new FileTreeNode(name, path, node, null, 1);

			node.addSuccessor(newFileNode);

		}
	}
	
	
	/*	 
			 //Object resultOfExecution = appLauncher.call("startDefaultApp", appName, "147.251.54.71", 20002, false, "default", position, size, optionalArgs);
			 Object resultOfExecution = appLauncher.call("startDefaultApp", appName, "147.251.54.71", 20002, false, "default", false, false, optionalArgs);
					 
			 System.out.println("result of execution is: " + resultOfExecution);
	
	
	
	*/
}
