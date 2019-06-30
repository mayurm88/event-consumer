import java.util.Random;

class Util {
    private static final Random r = new Random();

    static void process(Event event) {
        try {
            Thread.sleep(r.nextInt(20));
            System.err.println(event);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
