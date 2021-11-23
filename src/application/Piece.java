package application;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

public class Piece {
	static int[] filled = new int[7];
	Circle c = new Circle(28);
	int targetY;
	Pane root;
	public Piece(Pane root, int coll, boolean isRed){
		if(isRed){
			c.setFill(javafx.scene.paint.Color.RED);
		}else{
			c.setFill(javafx.scene.paint.Color.YELLOW);
		}
		
		c.toBack();
		c.setTranslateX(coll*72+39.5);
		targetY= 452- 64*filled[coll];
		c.setTranslateY(targetY);
		Main.game[coll][filled[coll]] = isRed?1:-1;
		filled[coll]++;
		root.getChildren().add(c);
		this.root=root;
		Main.pmoves.add(coll);
		
	}
	public void remove(){
		root.getChildren().remove(c);
	}
	
	
	
}
