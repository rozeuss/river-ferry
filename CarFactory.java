import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;


public class CarFactory implements Runnable{

	List <Samochod> samochodList;
	private Prom prom;
	private volatile boolean pauza=true;
	volatile short leftWaitCounter=0,rightWaitCounter=0; //ZMIENIC NA PRIVATE
	//tablice odpowiedzialne za zatrzymywanie samochodow, aby na siebie nie najezdzaly
	public volatile boolean leftTab[];
	public volatile boolean rightTab[];
	private Random rand;
	private int mode;
	private Thread t;

	
	public ReentrantLock leftTabLock; //rygle do zamykania sekcji krytycznej
	public ReentrantLock rightTabLock;

	
	public CarFactory(Prom prom,int mode){
		this.mode=mode;
		this.prom=prom;
		samochodList=Collections.synchronizedList(new LinkedList<>());
		leftTab= new boolean [8];
		rightTab= new boolean [8];
		leftTabLock=new ReentrantLock(true); 
		rightTabLock=new ReentrantLock(true);
		
		t=new Thread(this);
		rand= new Random();
		t.start();
		
		Thread t2=new Thread(new Runnable(){
			//sprzatanie
			@Override
			public void run() {
				while(true){
					synchronized(samochodList){
					for(Iterator <Samochod> iter=samochodList.iterator();iter.hasNext();){
						Samochod s=iter.next();
						if(s.getIsTerminated())iter.remove();
					}
					}
					try {Thread.sleep(500);} catch (InterruptedException e) {} // co pol sekundy wyrzucanie zakonczonych watkow
				}
			}
			
		});
		t2.start();
	}
	
	
	@Override
	public void run() {

	
		while(true){
			while(pauza){Thread.yield();}
				switch(mode){
				case 0: //intensywna produkcja 
						for(int i=0;i<8;i++){
						samochodList.add(new Samochod (prom,false, this)); //lewy
						samochodList.add(new Samochod(prom,true, this)); //prawy
						try {Thread.sleep((rand.nextInt(2)+1)*1000);} catch (InterruptedException e) {}
						}
						try {Thread.sleep(10*1000);} catch (InterruptedException e) {}
						break;
		
				case 1: //produkujemy z obu stron tylko szybko		
						samochodList.add(new Samochod(prom,false, this));
						samochodList.add(new Samochod(prom,true, this));
						try {Thread.sleep((rand.nextInt(1)+1)*1000);} catch (InterruptedException e) {}
						break;
				case 2:	//normalna praca
						samochodList.add(new Samochod(prom,rand.nextBoolean(), this));
						try {Thread.sleep((rand.nextInt(6)+1)*1000);} catch (InterruptedException e) {}
						break;
				case 3: //normalna szybko
						samochodList.add(new Samochod(prom,rand.nextBoolean(), this));
						try {Thread.sleep((rand.nextInt(3)+1)*1000);} catch (InterruptedException e) {}
						break;
				}
		}
	}
		
	
	public void switchMode(int mode){
		this.mode=mode;
	}
	public void pause(boolean pauza){
		this.pauza=pauza;
	}
	

}
