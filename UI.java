import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


public class UI extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private final Image tlo; //t³o
	private int X_car_on_ship,Y_car_on_ship; //wspolrzedne samochodu na statku
	private boolean jb5_flag=true;//flaga do stop/start
	
	private Prom prom;
	private CarFactory cf;
	private JPanel actionPanel; //panel z przyciskami akcji
	
	
	private JButton jb1,jb4,jb5,jb6,jb7,jb8,jb9;
	private JFrame window;
	
	public UI(){	
		init();
		prom=new Prom(4,5); // obsluga przystosowana do max 10 pojazdow
		prom.start();
		cf=new CarFactory(prom,2); // praca normalna na poczatku
		tlo=new ImageIcon(this.getClass().getResource("tlo2.png")).getImage();
		
		window = new JFrame();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setTitle("Programowanie wspó³bie¿ne - prom rzeczny.");
		window.setSize(1200, 365); //rozmiar
		window.setBackground(new Color(181, 230, 29));
		window.setResizable(false); //brak mozliwosci zmiany wielkosci okna
		window.getContentPane().add(this); //dodanie do frame
		window.setVisible(true);
		window.setLocationRelativeTo ( null ); //wysrodkowanie okienka
	}

	
	private void init() {
	    actionPanel = new JPanel(); 
	    
		this.setLayout(new BorderLayout());
		
		jb1 = new JButton("GODZINA 15:00"); //pierwszy przycisk
        jb1.setBackground(new java.awt.Color(18,18,18));
        jb1.setFont(new Font("Verdana", 1, 15));
        jb1.setForeground(Color.WHITE);
		jb1.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        jb1.setFocusable(false);	
        
		jb1.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				cf.switchMode(3); //3 je dobre
			}
			
		});
		

		
		jb4 = new JButton("Normalna praca");
	    jb4.setBackground(new java.awt.Color(18,18,18));
	    jb4.setFont(new Font("Verdana", 1, 15));
	    jb4.setForeground(Color.WHITE);
		jb4.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
		jb4.setFocusable(false);
		
		jb4.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				cf.switchMode(2);		
			}
			
		});


		jb5 = new JButton("Start");
	    jb5.setBackground(new java.awt.Color(18,18,18));
	    jb5.setFont(new Font("Verdana", 1, 15));
	    jb5.setForeground(Color.WHITE);	
		jb5.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
		jb5.setFocusable(false);
		jb5.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				jb5_flag=!jb5_flag;    
				if(jb5_flag)	cf.pause(true); //jesli flaga postawiona to nie tworzymy nowych watkow
				else  			cf.pause(false);
				setNewText(jb5);	
			}
			
		});
		
		jb6 = new JButton("Czas--");
	    jb6.setBackground(new java.awt.Color(18,18,18));
	    jb6.setFont(new Font("Verdana", 1, 15));
	    jb6.setForeground(Color.WHITE);	
		jb6.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
		jb6.setFocusable(false);
		
		jb6.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!prom.timer.getIsStopped())
				if(prom.CZAS_OCZEKIWANIA>1)
				prom.CZAS_OCZEKIWANIA--;
				
			}
			
		});
		
		jb7 = new JButton("Czas++");
	    jb7.setBackground(new java.awt.Color(18,18,18));
	    jb7.setFont(new Font("Verdana", 1, 15));
	    jb7.setForeground(Color.WHITE);	
		jb7.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
		jb7.setFocusable(false);
		
		jb7.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				prom.CZAS_OCZEKIWANIA++;
				
			}
			
		});
		
		
		jb8 = new JButton("Pojemnosc++");
	    jb8.setBackground(new java.awt.Color(18,18,18));
	    jb8.setFont(new Font("Verdana", 1, 15));
	    jb8.setForeground(Color.WHITE);	
		jb8.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
		jb8.setFocusable(false);
		
		jb8.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(prom.POJEMNOSC<10){
				prom.POJEMNOSC++;
				prom.LeftStatekSem.release();
				prom.RightStatekSem.release();
				System.out.println("Pojemnosc:" + prom.POJEMNOSC);
				}

				
			}
			
		});
		
		jb9 = new JButton("Pojemnosc--");
	    jb9.setBackground(new java.awt.Color(18,18,18));
	    jb9.setFont(new Font("Verdana", 1, 15));
	    jb9.setForeground(Color.WHITE);	
		jb9.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
		jb9.setFocusable(false);
		
		jb9.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
			//	if(prom.CZAS_OCZEKIWANIA<11)
				if(!prom.getIsTransporting()){
				if(prom.POJEMNOSC>1){
				prom.POJEMNOSC--;
				try {
					prom.LeftStatekSem.acquire();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					prom.RightStatekSem.acquire();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Pojemnosc:" + prom.POJEMNOSC);
				}
			}
			}
		});
		
        actionPanel.setLayout(new GridLayout());
        actionPanel.add(jb1);
		actionPanel.add(jb4);
		actionPanel.add(jb5);
		actionPanel.add(jb7);
		actionPanel.add(jb6);
		actionPanel.add(jb8);
		actionPanel.add(jb9);
		
		add(actionPanel, BorderLayout.PAGE_END);
		
		
	}
	
	@Override
    public void paintComponent(Graphics g){
		Graphics2D g2d = (Graphics2D) g;
		//wyswietlanie tla
		g2d.drawImage(tlo, 0, 0, this);
	
		//wyswietlanie statku
		if(prom.getCzyPrawyBrzeg())
		g2d.drawImage(prom.getImgFromLeft(),prom.getX(),prom.getY(),this);
		else
		g2d.drawImage(prom.getImg(),prom.getX(),prom.getY(),this);
		
		//wyswietlanie samochodow
		//g2d.setColor(Color.RED);
	synchronized(cf.samochodList){
		for(Samochod samochod:cf.samochodList){
			if(samochod.getVisible())
		//	g2d.fillRect(samochod.getX(),samochod.getY(), 50, 50); //zmienic wymiary samochodu
			if(samochod.getBrzeg())
			g2d.drawImage(samochod.getImg(),samochod.getX() , samochod.getY(), this);
			else
			g2d.drawImage(samochod.getImgFromLeft(),samochod.getX() , samochod.getY(), this);
		}
	}
		//wyswietlanie samochodow na statku
	
		X_car_on_ship=prom.getX()+5;
		Y_car_on_ship=prom.getY()+5;
		
		
	synchronized(prom.getSList()){ // synchronizacja dostepu do listy
		for(Samochod samochod:prom.getSList()){
			{  
				if(samochod.getStatekVisible()){
					if(samochod.getBrzeg())
					g.drawImage(samochod.getImg(),X_car_on_ship , Y_car_on_ship, null);
					else
					g.drawImage(samochod.getImgFromLeft(),X_car_on_ship , Y_car_on_ship, null);	
			//		g.fillOval(X_car_on_ship, Y_car_on_ship, 50,50); //tutaj zmien wyswietlanie samochodu
					X_car_on_ship+=50; //kolejny umieszczany odpowiednio daleko 
					if(X_car_on_ship > prom.getX()+250){ //dla +200 zmieszcza sie 4 samochody
						X_car_on_ship=prom.getX()+3; 
						Y_car_on_ship+=50;
					} 
				}
			} 
		}
	}
	
	
		//etykieta dla napisow
		g2d.setColor(new java.awt.Color(18,18,18)); 
		g2d.fillRect(490, 0, 205, 45);
				
		//rysowanie liczby samochodow oczekujacych na wjazd na plansze
		g2d.setColor(Color.WHITE);
		g2d.setFont(new Font("Tahoma", 1, 17)); //czcionka
		if(cf.leftWaitCounter!= 0) g2d.drawString("+"+(cf.leftWaitCounter), 10, 220);
		if(cf.rightWaitCounter!= 0) g2d.drawString("+"+(cf.rightWaitCounter), 1155, 140);

		g2d.drawString("Czas oczekiwania: "+ (prom.CZAS_OCZEKIWANIA-prom.timer.getTime()), 500, 20);
		g2d.drawString("Pojemnoœæ: " + prom.POJEMNOSC, 530, 40);

		
		this.repaint(); 
	/*	
		try {
			Thread.sleep(10); //spowolnienie animacji rysowania
		} catch (InterruptedException e) {;}
		*/
	}
	
	
	
	  public void setNewText(JButton jb5) {
		  	if(jb5_flag)	jb5.setText("Start");
		  	else 			jb5.setText("Stop");
	        this.repaint();
	    }
	
	
}
