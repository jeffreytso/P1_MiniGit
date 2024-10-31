import java.util.*;
import java.text.SimpleDateFormat;

// Jeffrey Tso
// 10/30/2024
// CSE 123
// Programming Assignment 1: MiniGit
// Sean Eglip

// Simulates a real repository by supporting a subset of the operations supported by real 
// Git repositories. This includes tracking metadata and history.
public class Repository {

    private String name;
    private Commit head;
    private int size;

    // Behavior:
    //      - Creates a new, empty repository with the specified name.
    // Exceptions:
    //      - Throws an IllegalArgumentException if the name is null or empty.
    // Parameters:
    //      - Takes a name (String).
    public Repository(String name) {
        if (name == "" || name == null) {
            throw new IllegalArgumentException("Name cannot be empty or null.");
        }

        this.name = name;
        this.size = 0;
        this.head = null;
    }

    // Behavior:
    //      - Return the ID of the current head of this repository.
    // Returns:
    //      - Returns the head ID (String) of this repository. 
    //      - Returns null if the current head is null.
    public String getRepoHead() {
        if (head == null) {
            return null;
        } else {
            return head.id;
        }
    }

    // Returns the number of commits (int) in the repository.
    public int getRepoSize() {
        return size;
    }

    // Returns a string representation of the repository. The string includes the name of the
    // repository and the current head (if it exists).
    public String toString() {
        String result = name + " - ";
        if (getRepoSize() == 0) {
            return result + "No commits";
        } else {
            return result + "Current head: " + head.toString();
        }   
    }

    // Behavior:
    //      - Determines whether the commit with the passed Id is in the repository.
    // Returns:
    //      - Returns true if the specified commit is in the repository, false if not.
    // Parameter:
    //      - Takes an Id (String). The Id should be non-null.
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

    // Behavior:
    //      - Returns a string consisting of the String representations of a specified amount of
    //      - most recent commits in the repository, with the most recent first. If there are 
    //      - fewer than the specified amount of commits in the repository, returns them all.
    // Exceptions:
    //      - Throws an IllegalArgumentException if n is non-positive.
    // Returns:
    //      - Returns the completed string representation. If there are no commits in the
    //      - repository, returns an empty string.
    // Parameter:
    //      - Takes in a number (int) that specifies how many 
    //      - past commits should be included in the return string.
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

    // Behavior:
    //      - Creates a new commit with the given message and adds it to the repository.
    // Return:
    //      - Returns the ID of the new commit.
    // Parameter:
    //      - Takes in a message (String) to be included in the new commit.
    //      - The message should be non-null.
    public String commit(String message) {
        Commit newHead = new Commit(message, head);
        head = newHead;
        size++;
        return head.id;
    }

    // Behavior:
    //      - Removes the commit with the specified ID from the repository.
    // Return:
    //      - Returns true if the commit was successfully dropped, and false if there is no
    //      - commit that matches the given ID in the repository.
    // Parameter:
    //      - Takes in an ID (String). The Id should be non-null.
    public boolean drop(String targetId) {
        if (head == null) {
            return false;
        }
        if (head.id.equals(targetId)) {
            head = head.past;
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

    // Behavior:
    //      - Takes all the commits from a separate repository and moves them into this repository,
    //      - combining the two repository histories such that chronological order is preserved. 
    //      - The commits are ordered in timestamp order from most recent to least recent. If this
    //      - repository is empty, all commits in the other repository gets moved into this one. 
    //      - The other repository will become null following the execution of this method. If the
    //      - other repository is passed initially as null, this repository remains unchanged.
    // Parameters:
    //      - Takes in another repository (Repository) that should be non-null.
    public void synchronize(Repository other) {
        if (this.head == null && other.head != null){
            this.head = other.head;
            other.head = null;
            this.size = other.size;
            other.size = 0;
        } else if (this.head != null && other.head != null) {
            Commit finalCommit;
            if (this.head.timeStamp > other.head.timeStamp) {
                finalCommit = this.head;
            } else {
                finalCommit = other.head;
            }
            int finalSize = this.size + other.size;

            
            while (this.head != null && other.head != null) {
                if (this.head.timeStamp > other.head.timeStamp) {
                    if (this.head.past == null) {
                        this.head.past = other.head;
                        other.head = null;
                    } else if (this.head.past.timeStamp > other.head.timeStamp) {
                        this.head = this.head.past;
                    } else {
                        Commit temp1 = this.head.past;
                        Commit temp2 = other.head.past;
                        this.head.past = other.head;
                        this.head.past.past = temp1;

                        this.head = this.head.past;
                        other.head = temp2;
                    }
                } else {
                    if (other.head.past == null) {
                        other.head.past = this.head;
                        this.head = null;
                    } else if (other.head.past.timeStamp > this.head.timeStamp) {
                        other.head = other.head.past;
                    } else {
                        Commit temp1 = other.head.past;
                        Commit temp2 = this.head.past;
                        other.head.past = this.head;
                        other.head.past.past = temp1;

                        other.head = other.head.past;
                        this.head = temp2;
                    }
                }
            }
            this.head = finalCommit;
            this.size = finalSize;
            other.head = null;
            other.size = 0;
        }
            
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
