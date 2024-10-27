import java.util.*;
import java.text.SimpleDateFormat;

public class Repository {

    private String name;
    private Commit head;
    private int size;

    public Repository(String name) {
        if (name == "" || name == null) {
            throw new IllegalArgumentException("Name cannot be empty or null.");
        }

        this.name = name;
        this.size = 0;
        this.head = null;
    }

    public String getRepoHead() {
        if (head == null) {
            return null;
        } else {
            return head.id;
        }
    }

    public int getRepoSize() {
        return size;
    }

    public String toString() {
        String result = name + " - ";
        if (getRepoSize() == 0) {
            return result + "No commits";
        } else {
            return result + "Current head: " + head.toString();
        }   
    }

    public boolean contains(String targetId) {
        Commit currentCommit = head;
        while (currentCommit != null) {
            if (currentCommit.id.equals(targetId)) {
                return true;
            }
            currentCommit = currentCommit.past;
        }
        return false;

    }

    public String getHistory(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Number entered should be positive.");
        }
        if (size == 0) {
            return "";
        }
        String history = "";
        int numCommits = n;
        Commit currentCommit = head;
        while (numCommits != 0 && currentCommit != null) {
            history += currentCommit.toString() + "\n";
            currentCommit = currentCommit.past;
            numCommits--;
        }
        return history;
    }

    public String commit(String message) {
        Commit newHead = new Commit(message, head);
        head = newHead;
        size++;
        return head.id;
    }

    public boolean drop(String targetId) {
        if (head == null) {
            return false;
        }
        if (targetId.equals("0") && size == 1) {
            head = null;
            size--;
            return true;
        }
        Commit currentCommit = head;
        Commit tempCommit = currentCommit;
        while (currentCommit.past != null) {
            if (currentCommit.past.id.equals(targetId)) {
                currentCommit.past = currentCommit.past.past;
                head = tempCommit;
                size--;
                return true;
            }
            currentCommit = currentCommit.past;
        }
        return false;
    }

    public void synchronize(Repository other) {
        Commit thisCommit = this.head;
        Commit otherCommit = other.head;
        Commit finalCommit;
        if (thisCommit.timeStamp > otherCommit.timeStamp) {
            finalCommit = thisCommit;
        } else {
            finalCommit = otherCommit;
        }
        int finalSize = this.size + other.size;

        while (thisCommit.past != null && otherCommit.past != null) {
            if (thisCommit.timeStamp > otherCommit.timeStamp) {
                if (thisCommit.past.timeStamp > otherCommit.timeStamp) {
                    thisCommit.past = thisCommit;
                } else {
                    Commit tempThisCommit = thisCommit;
                    thisCommit.past = otherCommit;
                    thisCommit.past.past = tempThisCommit.past;

                    otherCommit = otherCommit.past;
                    thisCommit = thisCommit.past;
                }
            } else {
                if (otherCommit.past.timeStamp > thisCommit.timeStamp) {
                    otherCommit.past = otherCommit;
                } else {
                    Commit tempOtherCommit = otherCommit;
                    otherCommit.past = thisCommit;
                    otherCommit.past.past = tempOtherCommit.past;

                    thisCommit = thisCommit.past;
                    otherCommit = otherCommit.past;
                }
            }
        }
        if (thisCommit.past == null) {
            thisCommit.past = otherCommit;
        } else {
            otherCommit.past = thisCommit;
        }
        this.head = finalCommit;
        this.size = finalSize;
        other.head = null;
        other.size = 0;
    }


    /**
     * DO NOT MODIFY
     * A class that represents a single commit in the repository.
     * Commits are characterized by an identifier, a commit message,
     * and the time that the commit was made. A commit also stores
     * a reference to the immediately previous commit if it exists.
     *
     * Staff Note: You may notice that the comments in this 
     * class openly mention the fields of the class. This is fine 
     * because the fields of the Commit class are public. In general, 
     * be careful about revealing implementation details!
     */
    public static class Commit {

        private static int currentCommitID;

        /**
         * The time, in milliseconds, at which this commit was created.
         */
        public final long timeStamp;

        /**
         * A unique identifier for this commit.
         */
        public final String id;

        /**
         * A message describing the changes made in this commit.
         */
        public final String message;

        /**
         * A reference to the previous commit, if it exists. Otherwise, null.
         */
        public Commit past;

        /**
         * Constructs a commit object. The unique identifier and timestamp
         * are automatically generated.
         * @param message A message describing the changes made in this commit. Should be non-null.
         * @param past A reference to the commit made immediately before this
         *             commit.
         */
        public Commit(String message, Commit past) {
            this.id = "" + currentCommitID++;
            this.message = message;
            this.timeStamp = System.currentTimeMillis();
            this.past = past;
        }

        /**
         * Constructs a commit object with no previous commit. The unique
         * identifier and timestamp are automatically generated.
         * @param message A message describing the changes made in this commit. Should be non-null.
         */
        public Commit(String message) {
            this(message, null);
        }

        /**
         * Returns a string representation of this commit. The string
         * representation consists of this commit's unique identifier,
         * timestamp, and message, in the following form:
         *      "[identifier] at [timestamp]: [message]"
         * @return The string representation of this collection.
         */
        @Override
        public String toString() {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            Date date = new Date(timeStamp);

            return id + " at " + formatter.format(date) + ": " + message;
        }

        /**
        * Resets the IDs of the commit nodes such that they reset to 0.
        * Primarily for testing purposes.
        */
        public static void resetIds() {
            Commit.currentCommitID = 0;
        }
    }
}
