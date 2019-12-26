import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UnisexBathroom {
    static Semaphore Mutex;
    static Semaphore TheLine;
    static Semaphore Lock;
    static Semaphore RoomLimit;
    static int guyCount =0, girlCount = 0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int totMen;
        int totWomen;
        String Line; // to store Male and Female instead of using array

        System.out.print("number of men: ");
        totMen = scanner.nextInt();
        System.out.print("number of women: ");
        totWomen = scanner.nextInt();

        Line = Rall(totMen, totWomen); // Rall method: Create random String of M and F. M for male, F for female.
        Mutex = new Semaphore(1);
        TheLine = new Semaphore (1);
        Lock = new Semaphore (1);
        RoomLimit = new Semaphore (3);
        System.out.println("Working\t\tEntering\tIn Bathroom\tLeaving");
        System.out.println("----------------------------------------------------------");

        Thread[] men = new Thread[Line.length()];
        Thread[] women = new Thread[Line.length()];
        for (int i = 0; i < Line.length(); i++) {
            if(Line.charAt(i)=='M'){
                men[i] = new ManThread(i);
                men[i].start();
            }
            if(Line.charAt(i)=='F'){
                women[i] = new WomanThread(i);
                women[i].start();
            }
        }
        for (int i = 1; i < Line.length(); i++) {
            if(Line.charAt(i)!=Line.charAt(i-1)){
                if(Line.charAt(i)=='M'){
                    try {
                        men[i].join();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(UnisexBathroom.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }else{
                    try {women[i].join();
                    } catch (InterruptedException ex)
                    {
                        Logger.getLogger(UnisexBathroom.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        System.exit(0);
    }

    private static String Rall(int x, int y) { // create a string MFFM...
        String Output = "";
        Random rnd = new Random();
        int male = 0;
        int female = 0;
        while (male != x || female != y) {
            int R = rnd.nextInt(2); // get a random int number between 0 and 2
            switch (R) {
                case 0:
                    if (male < x) {
                        Output = Output.concat("M"); // add characters after "Output" Characters.
                        male++;
                    }
                    break;
                case 1:
                    if (female < y) {
                        Output = Output.concat("F");
                        female++;
                    }
                    break;
            }
        }
        return Output;
    }

    public static void randomSleep(int max){
        Random rnd = new Random();
        int x = 1+rnd.nextInt(2);
        try {
            Thread.sleep((int) (x* max));
        }
        catch (InterruptedException e) {
            System.out.println(e);
        }
    }
    private static class ManThread extends Thread{
        private int id;

        public ManThread(int id){
            this.id = id;
        }

        public void run(){
            //doWork();
            if(UnisexBathroom.Mutex.availablePermits() ==0)
            {
                useBathroom();
            }
            else{
                GuyEnters();
                useBathroom();
                GuyLeaves();
            }
        }

        private void doWork(){
            System.out.println("Man " + id);
            UnisexBathroom.randomSleep(10000);    // do work randomly amongst 10 secs
        }

        private void useBathroom() {
            System.out.println("\t\t\t\t\t\tMan " + id );
            UnisexBathroom.randomSleep(3000);
        }
        private void GuyEnters(){
            doWork();
            try {
                UnisexBathroom.RoomLimit.acquire();
                UnisexBathroom.TheLine.acquire();
                UnisexBathroom.Mutex.acquire();
            } catch (InterruptedException e) {
                System.out.println(e);
                System.exit(-1);
            }
            try {
                if (guyCount++ == 0 || girlCount > 0){
                    UnisexBathroom.Mutex.release();
                    UnisexBathroom.Lock.acquire();
                }
                else {
                    UnisexBathroom.Mutex.release();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println("\t\t\tMan " + id );
                UnisexBathroom.randomSleep(1000);
                UnisexBathroom.TheLine.release();
            }

        }
        private void GuyLeaves(){
            try{
                UnisexBathroom.RoomLimit.release();
                UnisexBathroom.Mutex.acquire();
            } catch (InterruptedException e){
                System.out.println(e);
                System.exit(-1);
            }
            try{
                if (--guyCount==0){
                    UnisexBathroom.Lock.release();
                }
            }finally {
                System.out.println("\t\t\t\t\t\t\t\t\tMan " + id );
                UnisexBathroom.randomSleep(1000);
                Mutex.release();
            }
        }
    }
    private static class WomanThread extends Thread{
        private int id;

        public WomanThread(int id){
            this.id = id;
        }

        public void run(){
            //doWork();
            if(UnisexBathroom.Mutex.availablePermits() ==0)
            {
                useBathroom();
            }
            else{
                GirlEnters();
                useBathroom();
                GirlLeaves();
            }
        }

        private void doWork(){
            System.out.println("Woman " + id);
            UnisexBathroom.randomSleep(10000);    // do work randomly amongst 10 secs
        }

        private void useBathroom() {
            System.out.println("\t\t\t\t\t\tWoman " + id );
            UnisexBathroom.randomSleep(3000);
        }
        private void GirlEnters(){
            doWork();

            try {
                UnisexBathroom.RoomLimit.acquire();
                UnisexBathroom.TheLine.acquire();
                UnisexBathroom.Mutex.acquire();
            } catch (InterruptedException e) {
                System.out.println(e);
                System.exit(-1);
            }
            try {
                if (girlCount++ == 0 || guyCount > 0){
                    UnisexBathroom.Mutex.release();
                    UnisexBathroom.Lock.acquire();
                }
                else {
                    UnisexBathroom.Mutex.release();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println("\t\t\tWoman " + id );
                UnisexBathroom.randomSleep(1000);
                UnisexBathroom.TheLine.release();
            }

        }
        private void GirlLeaves(){
            try{
                UnisexBathroom.RoomLimit.release();
                UnisexBathroom.Mutex.acquire();
            } catch (InterruptedException e){
                System.out.println(e);
                System.exit(-1);
            }
            try{
                if (--girlCount==0){
                    UnisexBathroom.Lock.release();
                }
            }finally {
                System.out.println("\t\t\t\t\t\t\t\t\tWoman " + id );
                UnisexBathroom.randomSleep(1000);
                Mutex.release();
            }
        }
    }

}

