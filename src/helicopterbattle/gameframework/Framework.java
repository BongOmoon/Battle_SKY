package helicopterbattle.gameframework;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import helicopterbattle.game.Bgm;
import helicopterbattle.game.PlayerHelicopter;
import helicopterbattle.net.GameClient;
import helicopterbattle.net.GameServer;
import helicopterbattle.net.packets.Packet00Login;

/**
 * Framework that controls the game (Game.java) that created it, update it and draw it on the screen.
 * 
 * @author www.gametutorial.net
 */

public class Framework extends Canvas {
	Bgm introBgm = new Bgm("999.mp3",true);
    /**
     * Width of the frame.
     */
    public static int frameWidth;
    /**
     * Height of the frame.
     */
    public static int frameHeight;

    /**
     * Time of one second in nanoseconds.
     * 1 second = 1 000 000 000 nanoseconds
     */
    public static final long secInNanosec = 1000000000L;
    
    /**
     * Time of one millisecond in nanoseconds.
     * 1 millisecond = 1 000 000 nanoseconds
     */
    public static final long milisecInNanosec = 1000000L;
    
    /**
     * FPS - Frames per second
     * How many times per second the game should update?
     */
    private final int GAME_FPS = 60;
    /**
     * Pause between updates. It is in nanoseconds.
     */
    private final long GAME_UPDATE_PERIOD = secInNanosec / GAME_FPS;
    
    /**
     * Possible states of the game
     */
    public static enum GameState{STARTING, VISUALIZING, GAME_CONTENT_LOADING, MAIN_MENU, SELECT_MENU, MULTI_PLAY, MULTI_GAME, OPTIONS, PLAYING, GAMEOVER, DESTROYED}
    /**
     * Current state of the game
     */
    public static GameState gameState;
    
    /**
     * Elapsed game time in nanoseconds.
     */
    private long gameTime;
    // It is used for calculating elapsed time.
    private long lastTime;
    
    // The actual game
    private Game game;
    
    private Font font;
    
    private PlayerHelicopter multiPlayer;
    private String username = null;
    private GameClient socketClient;
    private GameServer socketServer;
    
    // Images for menu.
    private BufferedImage menuBackGround;
    private BufferedImage gameTitleImg;
    private BufferedImage menuBorderImg;
    private BufferedImage selectMenuImg;
    private BufferedImage skyColorImg;
	private BufferedImage cloudLayer1Img;
	private BufferedImage cloudLayer2Img;
	private BufferedImage gameStartPressImg;
	private BufferedImage multiStartPressImg;
	private BufferedImage exitPressImg;
	private BufferedImage jetImg;
    private BufferedImage helicopter1Img;
    private BufferedImage airballoonImg;
    private BufferedImage helicopter2Img;
    
    ImageIcon gameStartNotPressImg = new ImageIcon(this.getClass().getResource("/helicopterbattle/resources/images/game_start_not_press.png"));
    private final JButton gameStartBtn = new JButton(gameStartNotPressImg);
    ImageIcon multiStartNotPressImg = new ImageIcon(this.getClass().getResource("/helicopterbattle/resources/images/multi_start_not_press.png"));
    private final JButton multiStartBtn = new JButton(multiStartNotPressImg);
    ImageIcon exitNotPressImg = new ImageIcon(this.getClass().getResource("/helicopterbattle/resources/images/exit_not_press.png"));
    private final JButton exitBtn = new JButton(exitNotPressImg);
    
    ImageIcon jetSelectImg = new ImageIcon(this.getClass().getResource("/helicopterbattle/resources/images/jet_select.png"));
    private final JButton jetSelectBtn = new JButton(jetSelectImg);
    
    ImageIcon heliSelectImg = new ImageIcon(this.getClass().getResource("/helicopterbattle/resources/images/helicopter_select.png"));
    private final JButton heliSelectBtn = new JButton(heliSelectImg);
    
    ImageIcon heli2SelectImg = new ImageIcon(this.getClass().getResource("/helicopterbattle/resources/images/helicopter2_select.png"));
    private final JButton heli2SelectBtn = new JButton(heli2SelectImg);
    
    ImageIcon airBallonSelectImg = new ImageIcon(this.getClass().getResource("/helicopterbattle/resources/images/air_ballon_select.png"));
    private final JButton airBallonSelectBtn = new JButton(airBallonSelectImg);
    
    public Framework ()
    {  
        super();

        introBgm.start();
        gameState = GameState.VISUALIZING;
        
        //We start game in new thread.
        Thread gameThread = new Thread() {
            @Override
            public void run(){
                GameLoop();   
            }
        };
        gameThread.start();
    	
    }
    
    
   /**
     * Set variables and objects.
     * This method is intended to set the variables and objects for this class, variables and objects for the actual game can be set in Game.java.
     */
    private void Initialize()
    {
        font = new Font("monospaced", Font.BOLD, 28);
       
    }
    
    /**
     * Load files (images).
     * This method is intended to load files for this class, files for the actual game can be loaded in Game.java.
     */
    private void LoadContent() {
        try {
            URL menuBackGroundUrl = this.getClass().getResource("/helicopterbattle/resources/images/game_menu_backgroud.jpg");
            menuBackGround = ImageIO.read(menuBackGroundUrl);
            
            URL menuBorderImgUrl = this.getClass().getResource("/helicopterbattle/resources/images/menu_border.png");
            menuBorderImg = ImageIO.read(menuBorderImgUrl);
            
            URL skyColorImgUrl = this.getClass().getResource("/helicopterbattle/resources/images/sky_color.jpg");
            skyColorImg = ImageIO.read(skyColorImgUrl);
            
            URL gameTitleImgUrl = this.getClass().getResource("/helicopterbattle/resources/images/helicopter_battle_title.png");
            gameTitleImg = ImageIO.read(gameTitleImgUrl);
            
            URL selectMenuImgUrl = this.getClass().getResource("/helicopterbattle/resources/images/select_menu.png");
            selectMenuImg = ImageIO.read(selectMenuImgUrl);
            
            URL cloudLayer1ImgUrl = this.getClass().getResource("/helicopterbattle/resources/images/cloud_layer_1.png");
            cloudLayer1Img = ImageIO.read(cloudLayer1ImgUrl);
            
            URL cloudLayer2ImgUrl = this.getClass().getResource("/helicopterbattle/resources/images/cloud_layer_2.png");
            cloudLayer2Img = ImageIO.read(cloudLayer2ImgUrl);

            URL gameStartPressImgUrl = this.getClass().getResource("/helicopterbattle/resources/images/game_start_press.png");
            gameStartPressImg = ImageIO.read(gameStartPressImgUrl);   
            
			URL multiStartPressImgUrl = this.getClass().getResource("/helicopterbattle/resources/images/multi_start_press.png");
			multiStartPressImg = ImageIO.read(multiStartPressImgUrl);
			
			URL exitPressImgUrl = this.getClass().getResource("/helicopterbattle/resources/images/exit_press.png");
			exitPressImg = ImageIO.read(exitPressImgUrl);

            URL jetImgUrl = this.getClass().getResource("/helicopterbattle/resources/images/jet_body_left.png");
            jetImg = ImageIO.read(jetImgUrl);
            
            URL helicopter1ImgUrl = this.getClass().getResource("/helicopterbattle/resources/images/1_helicopter_body_right.png");
            helicopter1Img = ImageIO.read(helicopter1ImgUrl);
            
            URL helicopter2ImgUrl = this.getClass().getResource("/helicopterbattle/resources/images/2_helicopter_body_left.png");
            helicopter2Img = ImageIO.read(helicopter2ImgUrl);
            
            URL airballoonImgUrl = this.getClass().getResource("/helicopterbattle/resources/images/air_ballon.png");
            airballoonImg = ImageIO.read(airballoonImgUrl);
        } catch (IOException ex) {
            Logger.getLogger(Framework.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /**
     * In specific intervals of time (GAME_UPDATE_PERIOD) the game/logic is updated and then the game is drawn on the screen.
     */
    private void GameLoop()
    {
        // This two variables are used in VISUALIZING state of the game. We used them to wait some time so that we get correct frame/window resolution.
        long visualizingTime = 0, lastVisualizingTime = System.nanoTime();
        
        // This variables are used for calculating the time that defines for how long we should put threat to sleep to meet the GAME_FPS.
        long beginTime, timeTaken, timeLeft;
        
        while(true)
        {
            beginTime = System.nanoTime();
            
            switch (gameState)
            {
                case PLAYING:
                    BufferedImage blankCursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
                    java.awt.Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(blankCursorImg, new Point(0, 0), null);
                    this.setCursor(blankCursor);
                    
                    gameTime += System.nanoTime() - lastTime;
                    
                    game.UpdateGame(gameTime, mousePosition());
                    
                    lastTime = System.nanoTime();
                break;
                case GAMEOVER:

                break;
                case MULTI_PLAY:
                	//multiPlayer = new PlayerHelicopter(Framework.frameWidth / 4, Framework.frameHeight / 4, JOptionPane.showInputDialog(this, "Please enter a username"));
                    //PlayerHelicopter.username = JOptionPane.showInputDialog(this, "Please enter a username"));
                    if(JOptionPane.showConfirmDialog(this, "Do you want to run the server") == 0) {
                    	socketServer = new GameServer(this);
                    	socketServer.start();                   
                    }
                    socketClient = new GameClient(this, "localhost");
                    socketClient.start();
//                    socketClient.sendData("ping".getBytes());
                    Packet00Login loginPacket = new Packet00Login(JOptionPane.showInputDialog(this, "Please enter a username"));
                    loginPacket.writeData(socketClient);
                    gameState = GameState.SELECT_MENU;
//                    Packet00Login loginPacket = new Packet00Login(JOptionPane.showInputDialog(this, "Please enter a username"));
//                    loginPacket.writeData(socketClient);
                break;
                case MULTI_GAME:
                    BufferedImage blankCursorImg2 = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
                    java.awt.Cursor blankCursor2 = Toolkit.getDefaultToolkit().createCustomCursor(blankCursorImg2, new Point(0, 0), null);
                    this.setCursor(blankCursor2);
                    
                    gameTime += System.nanoTime() - lastTime;
                    
                    game.UpdateGame(gameTime, mousePosition());
                    
                    lastTime = System.nanoTime();
                break;
                case MAIN_MENU:    
                    gameStartBtn.setBounds(frameWidth / 2 - 250, frameHeight / 2 + 100, 500, 100);
                    gameStartBtn.setBorderPainted(false);
                    gameStartBtn.setContentAreaFilled(false);
                    gameStartBtn.setFocusPainted(false);
                    gameStartBtn.addMouseListener(new MouseAdapter() {
                    	@Override
                    	public void mouseExited(MouseEvent e) {
                    		gameStartBtn.setIcon(new ImageIcon(this.getClass().getResource("/helicopterbattle/resources/images/game_start_not_press.png")));
                    	}
                    	@Override
                    	public void mouseEntered(MouseEvent e) {
                    		gameStartBtn.setIcon(new ImageIcon(gameStartPressImg));                    		
                    	}
                    	@Override
                    	public void mouseClicked(MouseEvent e) {
                    		gameStartBtn.setVisible(false);
                    		multiStartBtn.setVisible(false);
                    		exitBtn.setVisible(false);
                    		gameState = GameState.SELECT_MENU;
                    	}
                    });
                    add(gameStartBtn);
                    
                    multiStartBtn.setBounds(frameWidth / 2 - 250, frameHeight / 2 + 225, 500, 100);
                    multiStartBtn.setBorderPainted(false);
                    multiStartBtn.setContentAreaFilled(false);
                    multiStartBtn.addMouseListener(new MouseAdapter() {
                    	@Override
                    	public void mouseExited(MouseEvent e) {
                    		 multiStartBtn.setIcon(new ImageIcon(this.getClass().getResource("/helicopterbattle/resources/images/multi_start_not_press.png")));
                    	}
                    	@Override
                    	public void mouseEntered(MouseEvent e) {
                    		multiStartBtn.setIcon(new ImageIcon(multiStartPressImg));                    		
                    	}
                    	@Override
                    	public void mouseClicked(MouseEvent e) {
                    		gameStartBtn.setVisible(false);
                    		multiStartBtn.setVisible(false);
                    		exitBtn.setVisible(false);
                    		gameState = GameState.MULTI_PLAY;
                    	}
                    });
                    add(multiStartBtn);
                  
                    exitBtn.setBounds(frameWidth / 2 - 250, frameHeight / 2 + 350, 500, 100);
                    exitBtn.setBorderPainted(false);
                    exitBtn.setContentAreaFilled(false);
                    exitBtn.addMouseListener(new MouseAdapter() {
                    	@Override
                    	public void mouseExited(MouseEvent e) {
                    		 exitBtn.setIcon(new ImageIcon(this.getClass().getResource("/helicopterbattle/resources/images/exit_not_press.png")));
                    	}
                    	@Override
                    	public void mouseEntered(MouseEvent e) {
                    		exitBtn.setIcon(new ImageIcon(exitPressImg));                    		
                    	}
                    	@Override
                    	public void mouseClicked(MouseEvent e) {
                    		
                    		System.exit(0);
                    	}
                    });
                    add(exitBtn);

                break;
                case SELECT_MENU:
                    //Packet00Login loginPacket = new Packet00Login("bong");
                	//Packet00Login loginPacket = new Packet00Login(JOptionPane.showInputDialog(this, "Please enter a username"));
                    //loginPacket.writeData(socketClient);
                    jetSelectBtn.setBounds(frameWidth / 4 - 550, frameHeight / 2 - 300, 600, 800);
                    jetSelectBtn.setBorderPainted(false);
                    jetSelectBtn.setContentAreaFilled(false);
                    jetSelectBtn.setFocusPainted(false);
                    jetSelectBtn.addMouseListener(new MouseAdapter() {
                    	@Override
                    	public void mouseExited(MouseEvent e) {
                    		jetSelectBtn.setIcon(new ImageIcon(this.getClass().getResource("/helicopterbattle/resources/images/jet_select.png")));
                    	}
                    	@Override
                    	public void mouseEntered(MouseEvent e) {
                    		//jetSelectBtn.setIcon(new ImageIcon(this.getClass().getResource("/helicopterbattle/resources/images/jet_select.png")));                    		
                    	}
                    	@Override
                    	public void mouseClicked(MouseEvent e) {
                    		Game.flightState = Game.FlightState.JETPLANE;
                    		jetSelectBtn.setVisible(false);
                    		heliSelectBtn.setVisible(false);
                    		airBallonSelectBtn.setVisible(false);
                    		heli2SelectBtn.setVisible(false);
                    		newGame();
                    	}
                    });
                    add(jetSelectBtn);
                	
                    heliSelectBtn.setBounds(frameWidth / 4 - 50, frameHeight / 2 - 300, 600, 800);
                    heliSelectBtn.setBorderPainted(false);
                    heliSelectBtn.setContentAreaFilled(false);
                    heliSelectBtn.setFocusPainted(false);
                    heliSelectBtn.addMouseListener(new MouseAdapter() {
                    	@Override
                    	public void mouseExited(MouseEvent e) {
                    		heliSelectBtn.setIcon(new ImageIcon(this.getClass().getResource("/helicopterbattle/resources/images/helicopter_select.png")));
                    	}
                    	@Override
                    	public void mouseEntered(MouseEvent e) {
                    		//heliSelectBtn.setIcon(new ImageIcon(this.getClass().getResource("/helicopterbattle/resources/images/jet_select.png")));                    		
                    	}
                    	@Override
                    	public void mouseClicked(MouseEvent e) {
                    		Game.flightState = Game.FlightState.HELICOPTER1;
                    		jetSelectBtn.setVisible(false);
                    		heliSelectBtn.setVisible(false);
                    		airBallonSelectBtn.setVisible(false);
                    		heli2SelectBtn.setVisible(false);
                    		newGame();
                    	}
                    });
                    add(heliSelectBtn);

                    airBallonSelectBtn.setBounds(frameWidth / 2 - 50, frameHeight / 2 - 300, 600, 800);
                    airBallonSelectBtn.setBorderPainted(false);
                    airBallonSelectBtn.setContentAreaFilled(false);
                    airBallonSelectBtn.setFocusPainted(false);
                    airBallonSelectBtn.addMouseListener(new MouseAdapter() {
                    	@Override
                    	public void mouseExited(MouseEvent e) {
                    		airBallonSelectBtn.setIcon(new ImageIcon(this.getClass().getResource("/helicopterbattle/resources/images/air_ballon_select.png")));
                    	}
                    	@Override
                    	public void mouseEntered(MouseEvent e) {
                    		//heliSelectBtn.setIcon(new ImageIcon(this.getClass().getResource("/helicopterbattle/resources/images/jet_select.png")));                    		
                    	}
                    	@Override
                    	public void mouseClicked(MouseEvent e) {
                    		Game.flightState = Game.FlightState.AIRBALLOON;
                    		jetSelectBtn.setVisible(false);
                    		heliSelectBtn.setVisible(false);
                    		airBallonSelectBtn.setVisible(false);
                    		heli2SelectBtn.setVisible(false);
                    		newGame();
                    	}
                    });
                    add(airBallonSelectBtn);
                    
                    heli2SelectBtn.setBounds(frameWidth*3 / 4 - 50, frameHeight / 2 - 300, 600, 800);
                    heli2SelectBtn.setBorderPainted(false);
                    heli2SelectBtn.setContentAreaFilled(false);
                    heli2SelectBtn.setFocusPainted(false);
                    heli2SelectBtn.addMouseListener(new MouseAdapter() {
                    	@Override
                    	public void mouseExited(MouseEvent e) {
                    		 heli2SelectBtn.setIcon(new ImageIcon(this.getClass().getResource("/helicopterbattle/resources/images/helicopter2_select.png")));
                    	}
                    	@Override
                    	public void mouseEntered(MouseEvent e) {
                    		//heliSelectBtn.setIcon(new ImageIcon(this.getClass().getResource("/helicopterbattle/resources/images/jet_select.png")));                    		
                    	}
                    	@Override
                    	public void mouseClicked(MouseEvent e) {
                    		Game.flightState = Game.FlightState.HELICOPTER2;
                    		jetSelectBtn.setVisible(false);
                    		heliSelectBtn.setVisible(false);
                    		airBallonSelectBtn.setVisible(false);
                    		heli2SelectBtn.setVisible(false);
                    		newGame();
                    	}
                    });
                    add(heli2SelectBtn);
                    
                break;
                case OPTIONS:
                    //...
                break;
                case GAME_CONTENT_LOADING:
                	introBgm.close();
                break;
                case STARTING:
                    // Sets variables and objects.
                    Initialize();
                    // Load files - images, sounds, ...
                    LoadContent();

                    // When all things that are called above finished, we change game status to main menu.
                    gameState = GameState.MAIN_MENU;
                break;
                case VISUALIZING:
                    // On Ubuntu OS (when I tested on my old computer) this.getWidth() method doesn't return the correct value immediately (eg. for frame that should be 800px width, returns 0 than 790 and at last 798px). 
                    // So we wait one second for the window/frame to be set to its correct size. Just in case we
                    // also insert 'this.getWidth() > 1' condition in case when the window/frame size wasn't set in time,
                    // so that we although get approximately size.
                    if(this.getWidth() > 1 && visualizingTime > secInNanosec)
                    {
                        frameWidth = this.getWidth();
                        frameHeight = this.getHeight();

                        // When we get size of frame we change status.
                        gameState = GameState.STARTING;
                    }
                    else
                    {
                        visualizingTime += System.nanoTime() - lastVisualizingTime;
                        lastVisualizingTime = System.nanoTime();
                    }
                break;
            }
            
            // Repaint the screen.
            repaint();
            
            // Here we calculate the time that defines for how long we should put threat to sleep to meet the GAME_FPS.
            timeTaken = System.nanoTime() - beginTime;
            timeLeft = (GAME_UPDATE_PERIOD - timeTaken) / milisecInNanosec; // In milliseconds
            // If the time is less than 10 milliseconds, then we will put thread to sleep for 10 millisecond so that some other thread can do some work.
            if (timeLeft < 10) 
                timeLeft = 10; //set a minimum
            try {
                 //Provides the necessary delay and also yields control so that other thread can do work.
                 Thread.sleep(timeLeft);
            } catch (InterruptedException ex) { }
        }
    }
    
    /**
     * Draw the game to the screen. It is called through repaint() method in GameLoop() method.
     */
    @Override
    public void Draw(Graphics2D g2d)
    {
        switch (gameState)
        {
            case PLAYING:
                game.Draw(g2d, mousePosition(), gameTime);
            break;
            case GAMEOVER:
                drawMenuBackground(g2d);
                g2d.setColor(Color.black);
                g2d.drawString("Press ENTER to restart or ESC to exit.", frameWidth/2 - 113, frameHeight/4 + 30);
                game.DrawStatistic(g2d, gameTime);
                g2d.setFont(font);
                g2d.drawString("GAME OVER", frameWidth/2 - 90, frameHeight/4);
            break;
            case MULTI_PLAY:
                drawMenuBackground(g2d);
                g2d.drawImage(gameTitleImg, frameWidth/2 - gameTitleImg.getWidth()/2, frameHeight/4, null);
            break;
            case MAIN_MENU:
                drawMenuBackground(g2d);
                g2d.drawImage(gameTitleImg, frameWidth/2 - gameTitleImg.getWidth()/2, frameHeight/4, null);
            break;
            case SELECT_MENU:
                g2d.drawImage(selectMenuImg, 0, 0, Framework.frameWidth, Framework.frameHeight, null);
            break;
            case OPTIONS:
                //...
            break;
            case GAME_CONTENT_LOADING:

                g2d.setColor(Color.white);
                g2d.drawString("GAME is LOADING", frameWidth/2 - 50, frameHeight/2);
            break;
        }
    }
    
    private void drawMenuBackground(Graphics2D g2d){
        g2d.drawImage(menuBackGround,  0, 0, Framework.frameWidth, Framework.frameHeight, null);
        g2d.drawImage(menuBorderImg,  0, 0, Framework.frameWidth, Framework.frameHeight, null);
    }
    
//    private JButton drawButton(BufferedImage img) {
////		ImageIcon normalIcon = new ImageIcon("images/normalIcon.gif");
////		ImageIcon rolloverIcon = new ImageIcon("images/rolloverIcon.gif");
////		ImageIcon pressedIcon = new ImageIcon("images/pressedIcon.gif");
//
//		JButton btn = new JButton();
////		btn.setPressedIcon(pressedIcon); // pressedIcon용 이미지 등록
////		btn.setRolloverIcon(rolloverIcon); // rolloverIcon용 이미지 등록
////		add(btn);
//		try {
//			btn.setIcon(new ImageIcon(img));
//		} catch (Exception ex) {
//			System.out.println(ex);
//		}
//		
//		return btn;
//	}
    /**
     * Starts new game.
     */
    private void newGame()
    {
        // We set gameTime to zero and lastTime to current time for later calculations.
        gameTime = 0;
        lastTime = System.nanoTime();
        
        game = new Game();
    }
    
    /**
     *  Restart game - reset game time and call RestartGame() method of game object so that reset some variables.
     */
    private void restartGame()
    {
        // We set gameTime to zero and lastTime to current time for later calculations.
        gameTime = 0;
        lastTime = System.nanoTime();
        
        game.RestartGame();
        
        // We change game status so that the game can start.
        gameState = GameState.PLAYING;
    }
    
    
    /**
     * Returns the position of the mouse pointer in game frame/window.
     * If mouse position is null than this method return 0,0 coordinate.
     * 
     * @return Point of mouse coordinates.
     */
    private Point mousePosition()
    {
        try
        {
            Point mp = this.getMousePosition();
            
            if(mp != null)
                return this.getMousePosition();
            else
                return new Point(0, 0);
        }
        catch (Exception e)
        {
            return new Point(0, 0);
        }
    }
    
    
    /**
     * This method is called when keyboard key is released.
     * 
     * @param e KeyEvent
     */
    @Override
    public void keyReleasedFramework(KeyEvent e)
    {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
            System.exit(0);
        
        switch(gameState)
        {
            case GAMEOVER:
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                    restartGame();
            break;
            case MULTI_PLAY:
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                    gameState = GameState.SELECT_MENU;
            break;
        }
    }
    
    /**
     * This method is called when mouse button is clicked.
     * 
     * @param e MouseEvent
     */
    @Override
    public void mouseClicked(MouseEvent e)
    {
        switch(gameState) {
        	case SELECT_MENU:
        		
        	break;
        }
    }
}