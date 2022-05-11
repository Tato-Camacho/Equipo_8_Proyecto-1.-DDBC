public class Proceso extends Thread {
    public MemoryManager m1;
    public Proceso (MemoryManager m1){
        this.m1 = m1;
    }
    public MemoryManager getMem1(){
        return m1;
    }
    public void run(){
        m1.sjf();
    }
}