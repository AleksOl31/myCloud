package test.calbacktest;

import java.util.function.Consumer;

public class Transmitter {
    private Consumer<String> consumer;
    private int i = 0;

    public Transmitter() {
        Thread thread = new Thread(() -> {
            while (true) {
                try {

                        Thread.sleep(2000);
                        onTimerOver(String.valueOf(i));
                        i += 1;

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void onTimerOver(String str) {
        consumer.accept(str);
    }

    public void messageReceive(Consumer<String> consumer) {
        this.consumer = consumer;
    }
}
