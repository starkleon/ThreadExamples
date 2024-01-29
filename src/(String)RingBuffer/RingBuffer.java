package RingBuffer;

import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RingBuffer {

    String[] data;
    int first;
    int last;
    Semaphore free;
    Semaphore occupied;
    ReentrantReadWriteLock lock;

    public RingBuffer(int length){
        data = new String[length];
        first = last = 0;
        free = new Semaphore(length);
        occupied = new Semaphore(0);
        lock = new ReentrantReadWriteLock();
    }

    public void add(String add){
        try {
            free.acquire();
            lock.writeLock().lock();
            data[last] = add;
            last = (last + 1) % data.length;
        }catch (InterruptedException e){
            System.out.println("Got interrupted waiting to add!");
        }
        finally {
            lock.writeLock().unlock();
            occupied.release();
        }
    }

    public String remove(){
        try{
            occupied.acquire();
            lock.writeLock().lock();
            return data[first];
        }
        catch (InterruptedException e){
            System.out.println("Got interrupted waiting to add!");
            return null;
        }finally {
            data[first] = null;
            first = (first + 1) % data.length;
            lock.writeLock().unlock();
            free.release();
        }
    }

    public String peek(){
        try {
            lock.readLock().lock();
            return data[first];
        }finally {
            lock.readLock().unlock();
        }
    }

    public Producer getProducer(){
        return new Producer();
    }
    public Consumer getConsumer(){
        return new Consumer();
    }
    public boolean contains(String comp){
        try {
            lock.readLock().lock();
            for(String s : data){
                if(s.equals(comp)){
                    return true;
                }
            }
            return false;
        }finally {
            lock.readLock().unlock();
        }
    }
    @Override
    public String toString(){
        return Arrays.toString(data);
    }

    public class Producer extends Thread{
        @Override
        public void run() {
            for(int i = 0; i < 20; i++){
                add("" + i);
                System.out.println(Arrays.toString(data));
            }
            System.out.println("Producer finished");
        }
    }
    public class Consumer extends Thread{
        @Override
        public void run() {
            for(int i = 0; i < 16; i++){
                remove();
                try {
                    sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("Consumer finished");
        }
    }




    public static void main(String[] args){
        RingBuffer rb = new RingBuffer(5);
        Consumer c = rb.getConsumer();
        Producer p = rb.getProducer();
        c.start();
        p.start();
        try{
            c.join();
            p.join();
        }catch (InterruptedException e){
            System.out.println("Nononono");
        }

        System.out.println(rb);



        //System.out.println(rb);



    }



}
