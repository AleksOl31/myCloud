package test;

public class Receiver {

    public static void main(String[] args) {
        Transmitter transmitter = new Transmitter();

        transmitter.messageReceive((msg) -> {
            System.out.println(msg);
        });
    }
}
