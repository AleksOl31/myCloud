package test.calbacktest;

public class Receiver {

    public static void main(String[] args) throws InterruptedException {
        Transmitter transmitter = new Transmitter();

        transmitter.messageReceive((msg) -> {
            System.out.println(msg + " received");
        });

        while (true) {
            Thread.sleep(1000);
            System.out.println("tick");
        }
    }
}
