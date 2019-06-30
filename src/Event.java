public class Event {

    final int id;
    final int user;

    Event(int id, int user) {
        this.id = id;
        this.user = user;
    }

    @Override
    public String toString() {
        return String.format("T=" + System.currentTimeMillis()/1000 + ": %s %s", user, id);
    }
}
