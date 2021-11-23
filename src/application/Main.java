package application;

import java.util.ArrayList;
import java.util.Arrays;


import com.sun.prism.paint.Color;

import javafx.animation.AnimationTimer;
//connect 4
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class Main extends Application {
	Image boardImage;
	ImageView board;
	static String empty = "\u25A2";
	static String redPlayed ="\u25C9";
	static String yellowPlayed= "\u25EF";
	static Label lose = new Label("Yellow Wins!");
	static Label win = new Label("Red Wins!");
	static Label tie = new Label("Tie!");
	static boolean leftAI = false;
	static boolean rightAI = false;
	static int counter = 0;
	static boolean running = true;
	static boolean ready = true;
	static int difficultyRed = 5;
	static double timeRed = Math.round(1000*0.00002428531359*Math.pow(7, difficultyRed))/1000.0;
	static int difficultyYellow = 5;
	static double timeYellow = Math.round(1000*0.00002428531359*Math.pow(7, difficultyYellow))/1000.0;
	static int[][] game = new int[7][6];
	static Pane root;
	static ArrayList<Piece> pieces = new ArrayList<>();
	static ArrayList<Integer> pmoves = new ArrayList<>();
	
	static boolean isRedTurn = true;
	static boolean runAi = !isRedTurn;

	@Override
	public void start(Stage primaryStage) {
		try {
			root = new Pane();
			Scene scene = new Scene(root, 512, 484);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			boardImage = new Image(getClass().getResource("Board.png").toExternalForm(), 512, 384, false, false);
			board = new ImageView(boardImage);
			root.getChildren().add(board);
			board.setTranslateY(100);
			Button collumns[] = new Button[7];
			for (int i = 0; i < collumns.length; i++) {
				collumns[i] = new Button();
				collumns[i].setPrefSize((int) (scene.getWidth() / 7.0), 384);
				collumns[i].setTranslateX(scene.getWidth() / 7.0 * i);
				collumns[i].setTranslateY(100);
				// collumns[i].setVisible(false);
				collumns[i].setStyle(" -fx-background-color: transparent; -fx-border-width: 0px;");
				root.getChildren().add(collumns[i]);
			}

			Slider sliderRed = new Slider(1, 8, 5);
			Label diffLabelRed = new Label("Difficulty: 5");
			Label timeToPlayRed = new Label("Max Time/Move: "+timeRed);
			root.getChildren().addAll(sliderRed, diffLabelRed,timeToPlayRed);
			diffLabelRed.setTranslateY(25);
			timeToPlayRed.setTranslateY(40);
			sliderRed.setTranslateX(80);
			sliderRed.setTranslateY(5);
			
			 sliderRed.valueProperty().addListener(new ChangeListener<Number>() {
			      @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
			        if (newValue == null) {
			          diffLabelRed.setText("Difficulty: ");
			          return;
			        }
			        difficultyRed =Math.round(newValue.intValue());
			        timeRed = 0.00002428531359*Math.pow(7, difficultyRed);
			        timeToPlayRed.setText("Max Time/Move: "+Math.round(1000*timeRed)/1000.0);
			        diffLabelRed.setText("Difficulty: "+ (newValue.intValue()));
			      }
			    });

			 ToggleButton toggleButton1 = new ToggleButton("Red AI");
			 root.getChildren().add(toggleButton1);
			 
			 ToggleButton toggleButton2 = new ToggleButton("Yellow AI");
			 toggleButton2.setTranslateX(250);
			 root.getChildren().add(toggleButton2);
			 Slider sliderYellow = new Slider(1, 8, 5);
				Label diffLabelYellow = new Label("Difficulty: 5");
				Label timeToPlayYellow = new Label("Max Time/Move: "+timeYellow);
				diffLabelYellow.setTranslateX(250);
				timeToPlayYellow.setTranslateX(250);
				root.getChildren().addAll(sliderYellow, diffLabelYellow,timeToPlayYellow);
				diffLabelYellow.setTranslateY(25);
				timeToPlayYellow.setTranslateY(40);
				sliderYellow.setTranslateX(80+250);
				sliderYellow.setTranslateY(10);
				
				 sliderYellow.valueProperty().addListener(new ChangeListener<Number>() {
				      @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
				        if (newValue == null) {
				          diffLabelYellow.setText("Difficulty: ");
				          return;
				        }
				        difficultyYellow =Math.round(newValue.intValue());
				        timeYellow = 0.00002428531359*Math.pow(7, difficultyYellow);
				        timeToPlayYellow.setText("Max Time/Move: "+Math.round(1000*timeYellow)/1000.0);
				        diffLabelYellow.setText("Difficulty: "+ (newValue.intValue()));
				      }
				    });
			 
			
			 
			for (int i = 0; i < collumns.length; i++) {
				int x = i;
				collumns[i].setOnAction(event -> {
					if (running) {
						System.out.println("You clicked on: " + x);
						if (Piece.filled[x] < 6 && isRedTurn) {
							Piece a = new Piece(root, x, isRedTurn);
							pieces.add(a);
							isRedTurn = !isRedTurn;
							checkIsOver();

						}else if(Piece.filled[x] < 6){
							Piece a = new Piece(root, x, isRedTurn);
							pieces.add(a);
							isRedTurn = !isRedTurn;
							checkIsOver();
							
						}
					}

				});
			}

			scene.setOnKeyPressed(e -> {
				switch (e.getCode()) {
				case R:
					isRedTurn = true;
					for(Piece a: pieces){
						a.remove();
					}
					pieces.clear();
					toggleButton1.setSelected(false);
					toggleButton2.setSelected(false);
					remove(win);
					remove(lose);
					remove(tie);
					for(int i = 0; i < Piece.filled.length; i++){
						Piece.filled[i]=0;
					}
					for(int i = 0; i < game.length; i++){
						for(int p = 0; p < game[i].length; p++){
							game[i][p] = 0;
						}
					}
					runAi = false;
					running = true;
					break;
				case P:
					printBoard(game);
					
					
				}
				
			});
			
			AnimationTimer timer = new AnimationTimer(){

				@Override
				public void handle(long now) {
					// TODO Auto-generated method stub
					if(runAi){
						counter++;
//						System.out.println("Counter "+ counter);
					}
					if(runAi && counter > 5){
						counter = 0;
						runAi = false;
						int diff = isRedTurn?difficultyRed:difficultyYellow;
						ai(diff);
					}
//					System.out.println(ready + " " + toggleButton1.isPressed() + " " + toggleButton2.isPressed()+ isRedTurn);
					
					if(ready && toggleButton1.isSelected() && isRedTurn && running){
						runAi = true;
					}
					if(ready && toggleButton2.isSelected() && !isRedTurn && running){
						runAi = true;
					}
				}
				
			};
			timer.start();
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void remove(Label l){
		if(root.getChildren().contains(l)){
			root.getChildren().remove(l);
		}
	}

	public static void ai(int difficulty) {
		System.out.println("Start -------------------------------------------------");
		ready = false;
		if (isRedTurn && running) {
			int ai = move(true, game, 1, difficulty);
			System.out.println("AI moved to: " + ai);
			Piece b = new Piece(root, ai, isRedTurn);
			pieces.add(b);
			isRedTurn = !isRedTurn;
		} else if (running) {
			int ai = move(true, game, -1, difficulty);
			System.out.println("AI moved to: " + ai);
			Piece b = new Piece(root, ai, isRedTurn);
			pieces.add(b);
			isRedTurn = !isRedTurn;

		}
		System.out.println(Arrays.toString(pmoves.toArray()));
		checkIsOver();
		ready = true;
	}

	public static void main(String[] args) {
//		System.out.println("\u25A2"  +("u\25EF") + ("u\25C9"));
				launch(args);
	}
	
	public static void checkIsOver(){
		if (evalBoard(game) < -1000) {
			System.out.println("Yellow Wins!");
			running = false;
			
			lose.setFont(new Font("Arial", 100));
			root.getChildren().add(lose);
		}else if(evalBoard(game) > 1000){
			//red wins
			System.out.println("Red wins");
			running = false;
			
			win.setFont(new Font("Arial", 100));
			root.getChildren().add(win);
		}
		boolean draw = true;
		for (int p : Piece.filled) {
			if (p < 6) {
				draw = false;
			}
		}
		if (draw && running) {
			System.out.println("Tie");
			running = false;
			
			tie.setFont(new Font("Arial", 100));
			root.getChildren().add(tie);
		}
	}

	public static int[][] flipBoard(int[][] board) {
		for (int i = 0; i < board.length; i++) {
			for (int p = 0; p < board[i].length; p++) {
				board[i][p] *= -1;
			}
		}
		return board;
	}
	public static void printBoard(int[][] board){
		for(int i = board[0].length-1; i>=0; i--){
			for(int p = 0; p < board.length; p++){
				switch(board[p][i]){
				case -1:
					System.out.print(yellowPlayed);
					break;
				case 0:
					System.out.print(empty);
					break;
				case 1:
					System.out.print(redPlayed);
				}
			}
			System.out.println();
		}
	}

	public static int evalBoard(int[][] board) {
		int height = board[0].length;
		int width = board.length;
		int score = 0;
		// horizontal

		// score+=evalFour(board[x][y],board[x][y],board[x][y],board[x][y]);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width - 3; x++) {
				score += evalFour(board[x][y], board[x + 1][y], board[x + 2][y], board[x + 3][y]);
			}
		}
		// vertical
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height - 3; y++) {
				score += evalFour(board[x][y], board[x][y + 1], board[x][y + 2], board[x][y + 3]);
			}
		}
		// diagonal up
		for (int x = 0; x < width - 3; x++) {
			for (int y = 0; y < height - 3; y++) {
				score += evalFour(board[x][y], board[x + 1][y + 1], board[x + 2][y + 2], board[x + 3][y + 3]);
			}
		}
		// diagonal down
		for (int x = 0; x < width - 3; x++) {
			for (int y = 3; y < height; y++) {
				score += evalFour(board[x][y], board[x + 1][y - 1], board[x + 2][y - 2], board[x + 3][y - 3]);
			}
		}
		return score;
	}

	public static int evalFour(int a, int b, int c, int d) {
		if (a + b == 2 && c == 0 && d == 0) {// x x 0 0
			return 2;
		}
		if (b + c == 2 && a == 0 && d == 0) {// 0 x x 0
			return 3;
		}
		if (c + d == 2 && a == 0 && b == 0) {// 0 0 x x
			return 2;
		}
		if (a + b + c + d == 3) {// any 3
			return 10;
		}
		if (a + b + c + d == 4) {// all 4
			return 10000;
		}
		// enemy player
		if (a + b == -2 && c == 0 && d == 0) {// x x 0 0
			return -2;
		}
		if (b + c == -2 && a == 0 && d == 0) {// 0 x x 0
			return -3;
		}
		if (c + d == -2 && a == 0 && b == 0) {// 0 0 x x
			return -2;
		}
		if (a + b + c + d == -3) {// any 3
			return -5;
		}
		if (a + b + c + d == -4) {// all 4
			return -10000;
		}
		return 0;
	}

	public static int evalMove(int[][] board, int player, int coll) {
		int ypos = ypos(board, coll);
		board[coll][ypos] = player;
		int output = evalBoard(board);
		output-=player;
		board[coll][ypos] = 0;
		return output;
	}

	public static int ypos(int[][] board, int coll) {
		int ypos = 0;
		for (int y = 0; y < board[coll].length; y++) {
			if (board[coll][y] == 0) {
				ypos = y;
				break;
			}
		}
		return ypos;
	}

	public static int move(boolean first, int[][] board, int turn, int depth) {
//		 System.out.println("Checking Board= turn:" + turn + " depth: " + depth);
		 
		ArrayList<Integer> moves = possibleMoves(board);

		if (moves.size() == 0) {
			return 0;
		}
		// look through each possible move
		int coll = moves.get(0);
		int bestColl = coll;
		int bestMove = evalMove(board, turn, coll);
		for (int i = 1; i < moves.size(); i++) {
			coll = moves.get(i);
			int temp = evalMove(board, turn, coll);
			if (turn == 1 && bestMove < temp) {
				bestColl = coll;
				bestMove = temp;

			} else if (turn == -1 && temp < bestMove) {
				bestColl = coll;
				bestMove = temp;
			}
		}
//		printBoard(board);
//		System.out.println("possible moves: " + Arrays.toString(moves.toArray()));
//		System.out.println("best Collum: " + bestColl +" score:" + bestMove);
		if (depth == 0) {
			return bestMove;
		}
		if (turn == 1 && bestMove > 1000) {
		} else if (turn != 1 && bestMove < -1000) {
		} else if (turn == 1) {// recursion
			coll = moves.get(0);
			bestColl = coll;
			int ypos = ypos(board, coll);

			board[coll][ypos] = turn;
//			System.out.println("Checking down with " + coll);
			bestMove = move(false, board, -turn, depth - 1);
//			System.out.println(bestMove + " thing whatever pleas help");
			board[coll][ypos] = 0;

			for (int i = 1; i < moves.size(); i++) {
				coll = moves.get(i);
				ypos = ypos(board, coll);
				board[coll][ypos] = turn;
//				System.out.println("Checking down with " + coll);
				int check =  move(false, board, -turn, depth - 1);
				
				if (bestMove <  check) {
//					System.out.println("updated best move to " + coll + "-=-=-=-=-=-=-=-=-=-=- at depth " + depth);
					bestMove = check;
					bestColl = coll;
				}
				if(depth==2){
//					System.out.println(turn+" "+coll +" is "+ check +" a bababa abab ab a  ab aab a b ab ab a ba bab ab  "+ bestColl +"best move: "+ bestMove);
				}

				board[coll][ypos] = 0;
			}
		} else {
			coll = moves.get(0);
			bestColl = coll;
			int ypos = ypos(board, coll);

			board[coll][ypos] = turn;
//			System.out.println("Checking down with " + coll);
			bestMove = move(false, board, -turn, depth - 1);
//			System.out.println("Checking down with " + bestMove);
			board[coll][ypos] = 0;

			for (int i = 1; i < moves.size(); i++) {
				coll = moves.get(i);
				ypos = ypos(board, coll);
				board[coll][ypos] = turn;
//				System.out.println("Checking down with " + coll);
				int check =  move(false, board, -turn, depth - 1);
				
				if (bestMove >check) {
//					System.out.println("updated best move to " + coll + "-=-=-=-=-=-=-=-=-=-=-at depth " + depth);
					bestMove = check;
					bestColl = coll;
				}
				if(depth==2){
//					System.out.println(turn+" "+coll +" is "+ check +" a bababa abab ab a  ab aab a b ab ab a ba bab ab  "+ bestColl +"best move: "+ bestMove);
				}
				board[coll][ypos] = 0;
			}
		}
//		System.out.println("DONE: Best Coll: " + bestColl + " score: " +bestMove);
//		System.out.println("================ ");
//		System.out.println();
		if (first) {
			return bestColl;
		}
		return bestMove;

	}

	public static ArrayList<Integer> possibleMoves(int[][] board) {
		ArrayList<Integer> output = new ArrayList<>();
		for (int x = 0; x < board.length; x++) {
			for (int y = 0; y < board[x].length; y++) {
				if (board[x][y] == 0) {
					output.add(x);
					break;
				}
			}
		}
		return output;
	}
}
