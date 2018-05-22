import java.awt.Image;
import java.util.Random;

import javax.swing.ImageIcon;

public class Samochod implements Runnable {
	
	private Thread t; //watek t
	private Prom prom; //prom
	private final boolean brzeg; //brzeg wyjsciowy jest niezmienny false lewy, true prawy
	private volatile boolean zezwolenie=false;
	private volatile boolean isVisible=false;
	private volatile boolean statekVisible=true;
	private boolean isTerminated=false; //czy zakonczony
	private volatile byte pozycjaNaUlicy;
	
	private Image img;//=new ImageIcon(this.getClass().getResource("Samochod.png")).getImage();
	private Image imgFromLeft;//=new ImageIcon(this.getClass().getResource("SamochodFromLeft.png")).getImage();
	private CarFactory cf;
	private volatile boolean stopSamochod=false;
	
	int x,y;
	//private boolean czyCzerwony;
	
	private int wartosc;
	
	private Random rand=new Random();
	
	
	public Samochod(Prom prom, boolean brzeg, CarFactory cf){
		this.prom = prom;
		this.brzeg=brzeg;
		this.cf=cf;
	//	czyCzerwony = rand.nextBoolean();
		wartosc = rand.nextInt(3);
		if(wartosc == 0){
			img=new ImageIcon(this.getClass().getResource("Samochod.png")).getImage();
			imgFromLeft=new ImageIcon(this.getClass().getResource("SamochodFromLeft.png")).getImage();	
		}
		else if(wartosc == 1){
			img=new ImageIcon(this.getClass().getResource("samochod2.png")).getImage();
			imgFromLeft=new ImageIcon(this.getClass().getResource("Samochod2FromLeft.png")).getImage();	
		}
		else{
			img=new ImageIcon(this.getClass().getResource("Samochod3.png")).getImage();
			imgFromLeft=new ImageIcon(this.getClass().getResource("Samochod3FromLeft.png")).getImage();	
		}
		
		pozycjaNaUlicy=-1;
		if(!brzeg){ //lewy
			x=-49; //-30 na -80
			y=190;
		} else { //prawy
			x=1199; //1200, 1199 na 1249
			y=110;
		}
		
		t = new Thread(this);
		t.start();
	}

	@Override
	public void run() {
		//cf.leftTab[0]=false; //zeby nie najezdzaly na siebie
		try {Thread.sleep(50);} catch (InterruptedException e) {} //konieczne by nie najezdzaly na siebie
		if(!brzeg){ //dla lewego brzego

			

			if(cf.leftTab[0]){//wyswietlanie ilosci czekajacych samochodow
				cf.leftWaitCounter++;
				cf.leftTabLock.lock();
				while(cf.leftTab[0]){Thread.yield();};
				cf.leftTabLock.unlock();
				cf.leftWaitCounter--;
			}
			if(cf.leftTab[0]){//wyswietlanie ilosci czekajacych samochodow
				cf.leftTabLock.lock();
				while(cf.leftTab[0]){Thread.yield();};
				cf.leftTabLock.unlock();
			}
			cf.leftTab[0]=true; //zajmuje pierwsza pozycje, zeby nie najezdzaly na siebie
			isVisible=true;	//widoczny samochodzik
			pozycjaNaUlicy=0;
			
			while(x<350){
				
			
				if(x%50 == 0 && pozycjaNaUlicy < 8  && pozycjaNaUlicy>=0){ 

					pozycjaNaUlicy++; // jezeli samochod dojedzie do nastepnej pozycji, sprawdzamy warunki
				//	try {Thread.sleep(30);} catch (InterruptedException e) {} //konieczne by nie najezdzaly na siebie
					if(cf.leftTab[pozycjaNaUlicy]){ // jezeli pozycja na ktora chcemy wjechac jest zajeta (true), zatrzymujemy samochod
						stopSamochod=true;
					} 
					else  { // w innym wpadku zwalniamy nasza stara pozycje, i zajmujemy nowa
						cf.leftTab[pozycjaNaUlicy-1] = false; 
						cf.leftTab[pozycjaNaUlicy] = true; 
						} 

				while(stopSamochod){ //jezeli zatrzymany
					Thread.yield();
					if(!cf.leftTab[pozycjaNaUlicy]){ //czeka na zwolnienie
					//	try {Thread.sleep(20);} catch (InterruptedException e) {} //konieczne by nie najezdzaly na siebie
						
						stopSamochod=false; 
						cf.leftTab[pozycjaNaUlicy-1] = false; 
						cf.leftTab[pozycjaNaUlicy] = true; 
					} 
				}
				
				}
		
				x+=1;
				try {Thread.sleep(9);} catch (InterruptedException e) {}
			}
		}
		 else{ //dla prawego brzegu analogicznie
			

			 if(cf.rightTab[0]){
					cf.rightWaitCounter++;
					cf.rightTabLock.lock();
					while(cf.rightTab[0]){Thread.yield();};
					cf.rightTabLock.unlock();
					cf.rightWaitCounter--;
				}
			if(cf.rightTab[0]){
					cf.rightTabLock.lock();
					while(cf.rightTab[0]){Thread.yield();};
					cf.rightTabLock.unlock();
			}
			 
			
			 cf.rightTab[0]=true;
			 isVisible=true;
			 pozycjaNaUlicy=0;
			 
			while(x>800){

				if(x%50 == 0 && pozycjaNaUlicy < 8 && pozycjaNaUlicy>=0){ 
			
					pozycjaNaUlicy++;
			//		try {Thread.sleep(30);} catch (InterruptedException e) {} //konieczne by nie najezdzaly na siebie
					if(cf.rightTab[pozycjaNaUlicy]){
						stopSamochod=true;
					} 
					else  {
						cf.rightTab[pozycjaNaUlicy-1] = false; 
						cf.rightTab[pozycjaNaUlicy] = true; 
						} 

				while(stopSamochod){
					Thread.yield();
					if(!cf.rightTab[pozycjaNaUlicy]){
				//		try {Thread.sleep(20);} catch (InterruptedException e) {} //konieczne by nie najezdzaly na siebie
						stopSamochod=false; 
						cf.rightTab[pozycjaNaUlicy-1] = false; 
						cf.rightTab[pozycjaNaUlicy] = true; 
					} 
				}
				
				}
				
				
				x-=1;
				try {Thread.sleep(9);} catch (InterruptedException e) {}
			
			}
		}
		
		//semafor blokujacy wjazd do zapelnionego statku
		//lock blokuje wjazd do statku bedacego po zlej stronie
		if (brzeg) {
			prom.RightStatekSem.acquireUninterruptibly(); //to samo co acquire ale bez bloku trycatch podobno lepsze
			prom.rightLock.lock(); //zamkniecie rygla
		} else {
			prom.LeftStatekSem.acquireUninterruptibly();
			prom.leftLock.lock(); //zamkniecie rygla
		}
		
		//kod sekcji krytycznej - moment wjazdu
		prom.WjedzNaStatek(this, brzeg, cf);
		if (!brzeg) //wspolrzedne po przeplynieciu
			x = 800;
		else
			x = 350;
		
		if (brzeg) {
			prom.rightLock.unlock(); //otworzenie rygla
		} else {
			prom.leftLock.unlock(); // otworzenie rygla
		}

		while (!zezwolenie) {
			Thread.yield();// watek czeka az dostanie pozwolenie od statku na zjazd					
		}
		
		if (brzeg) {
			prom.RightStatekSem.release(); //zjezdza auto wiec pojemnosc semafora++
		} else {
			prom.LeftStatekSem.release();
		}
		
		//wspolrzedne po przejechaniu na druga strone
		if (!brzeg) { // dla lewego brzego
			while (x < 1200) {
				x += 1;
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
				}

			}
		} else { // dla prawego brzegu
			while (x > -50) {
				x -= 1;
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
				}

			}
		}
		
		
		this.isTerminated=true; //info ze zakonczony, potrzebne w CarFactory
		
	}

	public void setStopSam(boolean stopSamochod) {
		this.stopSamochod = stopSamochod;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setZezwolenie(boolean zezwolenie) {
		this.zezwolenie = zezwolenie;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public boolean getVisible() {
		return isVisible;
	}

	public void setStatekVisible(boolean statekVisible) {
		this.statekVisible = statekVisible;
	}

	public boolean getIsTerminated() {
		return isTerminated;
	}

	public boolean getStatekVisible() {
		return statekVisible;
	}

	public boolean getBrzeg() {
		return this.brzeg;
	}

	public Image getImg() {
		return img;
	}

	public Image getImgFromLeft() {
		return imgFromLeft;
	}
	
}
