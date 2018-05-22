
public class Timer implements Runnable {

	private long time=0; //czas
	private volatile boolean isStopped=false; //zatrzymany
	private Thread t;
	
	public Timer(){ //konstruktor klasy
		t=new Thread(this);
		t.start();
	}
	
	/** resetowanie czasu */
	public void reset(){ 
		this.time=0;
	}
	/** zatrzymanie czasu */
	public void isStopped(boolean isStopped){
		if(!isStopped) reset();
		this.isStopped=isStopped;
	}
	
	public boolean getIsStopped(){
	return this.isStopped;
	}
	
	/** zwrocenie obecnego czasu */
	public long getTime(){
		return this.time;
	}
	
	@Override
	public void run() {
		
		while(true){
			
		while(isStopped){Thread.yield();} //zatrzymaj watek gdy statek plynie
		

		System.out.println(++time);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {;} //czekamy sekunde po czym zwiekszamy licznik

	//	System.out.println(time);
		
		}
	}
	
}
