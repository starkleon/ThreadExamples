package SimpleExamples;

public class SimpleRaceCon {
    public static int x = 0;

    public SimpleRaceCon(){

    }
    public synchronized void inc(){
        x++;
    }
    public Incrementor getInc(){
        return new Incrementor();
    }
    public class Incrementor extends Thread{
        @Override
        public void run() {
            for(int i = 0; i < 5000; i++){
                inc();
            }
        }
    }
    public static void main(String[] args){
        SimpleRaceCon rc = new SimpleRaceCon();
        Incrementor i1 = rc.getInc();
        Incrementor i2 = rc.getInc();

        i1.start();
        i2.start();

        try{
            i1.join();
            i2.join();
        }catch (InterruptedException e){
            System.out.println("Got interrupted!");
        }

        System.out.println(SimpleRaceCon.x);
    }
}
