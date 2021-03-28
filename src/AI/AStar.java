package AI;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import ie.gmit.sw.ai.GameView;
import ie.gmit.sw.ai.Node;
import ie.gmit.sw.ai.NodeType;


public class AStar {
	private GameView g;
	private Node[][] maze;
	private Node goal;
	
	private HeuristicNodeComparator heuristicComparer;
	private List<Node> closed = new ArrayList<Node>();
	private PriorityQueue<Node> open;
	
	public AStar(Node[][] maze, Node start, Node goal, GameView g) {
		this.maze = maze;
		this.goal = goal;
		this.g= g;
		heuristicComparer = new HeuristicNodeComparator();
		open  = new PriorityQueue<Node>(20, heuristicComparer);
		new Thread(new Runnable() {
			public void run () {
				search(start);
				//System.out.println("Start: "+start);
			}
		}).start();
		setNodesAsUnvisited();
	}
	
	public void search(Node start) {
		open.add(start);
		start.setDistanceTravelled(0);
		while(!open.isEmpty()) {
			
			Node next = open.poll();
			
			if (next.isGoalNode()) {
				List<Node> path = new ArrayList<Node>();
				
				while (next.getParent() != null) {
					path.add(next);
					next = next.getParent();
				}
				// return with List of Nodes and repaint the scene 
				// to show path to goal
				g.showPath(path);
				
			}

			pushSuccessors(next);
			closed.add(next);
		}
		System.out.println("Goal " + goal.getRow() + "/" + goal.getCol());
	}
	
	public void pushSuccessors(Node current) {
		List<Node> children = current.getAdjacentNodes(maze);

		for (Node child : children) {

//			float score = HeuristicCalculator.getHeuristicValue(current.getDistanceTravelled()+current.getHeuristic(child), current.getHeuristic(goal));
			int score = current.getHeuristic(goal) + current.getDistanceTravelled();

			child.setDistanceTravelled(current.getDistanceTravelled()+1);
			child.setApproxDistanceToGoal(child.getHeuristic(goal));

			if(!child.isTraversable() && child.getNodeType()!= NodeType.trophy) continue;
			if(open.contains(child) || closed.contains(child)) continue; 
			
			child.setDistanceTravelled(current.getDistanceTravelled() + current.getHeuristic(child));
			child.setParent(current);

			if(child.getScore() < score) {
				open.add(child);
				closed.add(current);
			}
			else {
				open.add(child);
			}
		}
	}
	
	private void setNodesAsUnvisited() {
		for(int r=0; r<maze.length;r++) {
			for(int c=0; c<maze[0].length;c++)
				maze[r][c].setVisited(false);
		}
	}
}
