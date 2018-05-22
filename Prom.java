import java.awt.Image;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.ImageIcon;


public class Prom extends Thread {

	public int POJEMNOSC; //max aut na promie
	public int CZAS_OCZEKIWANIA; 
	
	private Image img;//=new ImageIcon(this.getClass().getResource("Images\\SHIPfromRight.png")).getImage();
	private Image imgFromLeft;//=new ImageIcon(this.getClass().getResource("Images\\SHIP.png")).getImage();
	private int x, y; //wspolrzedne
	
	private List <Samochod> samochodListNaPromie; //lista samochodow na promie
	private boolean czyPrawyBrzeg; //false - lewy brzeg, true - prawy brzeg
	public Timer timer;
	
	public Semaphore LeftStatekSem; // semafor dla samochodow wjezdzajacyc z lewego brzegu
	public Semaphore RightStatekSem; // --||-- z prawego brzegu
	public ReentrantLock leftLock; //rygle do zamykania sekcji krytycznej
	public ReentrantLock rightLock;
	
	private boolean isTransporting = false;
	
	
	public Prom(int POJEMNOSC,int CZAS_OCZEKIWANIA){	
		this.POJEMNOSC = POJEMNOSC;
		this.CZAS_OCZEKIWANIA=CZAS_OCZEKIWANIA;
		img=new ImageIcon(this.getClass().getResource("SHIPfromRight.png")).getImage();
		imgFromLeft=new ImageIcon(this.getClass().getResource("SHIP.png")).getImage();
		x=412;
		y=120;
		
		LeftStatekSem = new Semaphore (POJEMNOSC,true); //pojemnosc semafora
		RightStatekSem = new Semaphore (POJEMNOSC,true);
		leftLock=new ReentrantLock(true); 
		rightLock=new ReentrantLock(true);
		timer=new Timer(); 
		czyPrawyBrzeg=false;
		samochodListNaPromie = Collections.synchronizedList(new LinkedList<>());
	}
	
	@Override
	public void run() {
		

		rightLock.lock(); // blokujemy wjazd z prawej strony

		while(true){
		
			if(czyPrawyBrzeg) { //jezeli prawy brzeg i brak miejsca
				if(RightStatekSem.availablePermits() == 0 ) 
					plyn();
				} 
			else if (LeftStatekSem.availablePermits() == 0 ) plyn(); //czy lewy brzeg i brak miejsca
			
			if(timer.getTime() >= CZAS_OCZEKIWANIA) plyn(); 	//czas oczekiwania minal
		}
		
		}
	
	
	private void plyn()
	{
		isTransporting = true;
		timer.isStopped(true); //stopujemy tajmer
		
		if(czyPrawyBrzeg) 	rightLock.lock(); 
		else 				leftLock.lock(); //blokujemy mozliwosc wjazdu
		
		// animacja prom plynie, odleglosc = 114 pixeli
		for(int i=0;i<57;i++){
			if(czyPrawyBrzeg) x-=2; else x+=2; //pokonujemy droge
			try {Thread.sleep(40);} catch (InterruptedException e) {;} //(5) motoruwa, (100) najs
		}
		
	//synchronized(samochodListNaPromie){      
		for(Samochod s: samochodListNaPromie){
			s.setZezwolenie(true); //zwalniamy petle blokujaca dzialanie samochodu, zezwalamy na zjazd pojazdu, 
			s.setVisible(true); //znow widoczny
			s.setStatekVisible(false);
			try {
				Thread.sleep(900);
			} catch (InterruptedException e){;} // czekamy 1 sekunde pomiedzy "wypuszczeniem" kolejnych aut
		//}
		}
	
		samochodListNaPromie.clear(); // wszystkie samochody zjechaly
		
		czyPrawyBrzeg=!czyPrawyBrzeg; // statek przeplynal na przeciwny brzeg
		
		if(czyPrawyBrzeg) 	rightLock.unlock();  //dajemy mozliwosc wjazdu z drugiej strony
		else 				leftLock.unlock();  
		
		try {
			Thread.sleep(1000);
			} catch (InterruptedException e) {;}
		timer.isStopped(false); //i znow ruszamy stoper
		isTransporting = false;
		
	}
	
	

	
    public void WjedzNaStatek(Samochod s, boolean czyPrawyBrzeg, CarFactory cf){
    	if(!czyPrawyBrzeg) 	cf.leftTab[7]=false; 
    	else 				cf.rightTab[7]=false; //usuniecie z drogi
    	s.setVisible(false); //chowamy wontka
    	samochodListNaPromie.add(s);
    	timer.reset();
    	try {
    		Thread.sleep(1000);
    	} catch (InterruptedException e) {;} 
    	
	}
    public boolean getIsTransporting(){
    	return this.isTransporting;
    }
    
    
    public boolean getCzyPrawyBrzeg(){
    	return this.czyPrawyBrzeg;	
    }
    
    public int getPojemnosc(){
    	return this.POJEMNOSC;
    }
    
    
    public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public Image getImg(){
		return img;
	}
	
	public Image getImgFromLeft(){
		return imgFromLeft;
	}
	
	public List <Samochod> getSList(){
		return samochodListNaPromie;
	}

}
